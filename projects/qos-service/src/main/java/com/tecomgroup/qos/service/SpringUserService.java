/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import com.tecomgroup.qos.dashboard.DashboardChartWidget;
import com.tecomgroup.qos.exception.DuplicateException;
import com.tecomgroup.qos.util.AuditLogger;
import com.tecomgroup.qos.util.SimpleUtils;
import com.tecomgroup.qos.util.Utils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.hibernate.collection.spi.PersistentCollection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.util.Assert;

import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.criterion.SerializedCriterionContainer;
import com.tecomgroup.qos.dashboard.DashboardAgentsWidget;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.dashboard.DashboardWidget.HasUpdatableData;
import com.tecomgroup.qos.dashboard.DashboardWidget.WidgetData;
import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.domain.MContactInformation;
import com.tecomgroup.qos.domain.MDashboard;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.domain.MUserReportsTemplate;
import com.tecomgroup.qos.domain.MUserResultTemplate;
import com.tecomgroup.qos.domain.UserSettings;
import com.tecomgroup.qos.domain.UserSettings.NotificationLanguage;
import com.tecomgroup.qos.exception.LimitExceededException;
import com.tecomgroup.qos.exception.ModelSpaceException;
import com.tecomgroup.qos.exception.SecurityException;
import com.tecomgroup.qos.exception.SecurityException.Reason;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.modelspace.hibernate.HibernateEntityConverter;
/**
 * 
 * @author abondin
 * 
 */
@Transactional(readOnly = true)
@Component
@Profile(AbstractService.TEST_CONTEXT_PROFILE)
public class SpringUserService extends AbstractService
		implements
			TemplateDeleter,
			InternalUserService,
			InitializingBean,
			WidgetDeleter {

	private ObjectReader criterionReader;

	private ObjectWriter jsonWriter;

	private ObjectReader dashboardWidgetsReader;

	@Autowired
	private LdapTemplate ldapTemplate;

	@Value("${security.ldap.enabled}")
	private boolean ldapEnabled;

	@Value("${security.ldap.page.size}")
	private int ldapPageSize;

	@Value("${client.max.dashboard.widgets.count}")
	private Integer maxDashboardWidgetsCount;

	private final static Logger LOGGER = Logger
			.getLogger(SpringUserService.class);

	private final AttributesMapper<MUser> ldapUserAttributeMapper = new AttributesMapper<MUser>() {
		@Override
		public MUser mapFromAttributes(final Attributes attrs)
				throws NamingException {
			final MUser user = new MUser();

			user.setLdapAuthenticated(true);
			user.setLogin(attrs.get("sAMAccountName").get().toString());

			final Attribute email = attrs.get("mail");
			if (isLdapAttributeNotNullAndNotEmpty(email)) {
				user.setEmail(email.get().toString());
			}

			final Attribute phone = attrs.get("telephoneNumber");
			if (isLdapAttributeNotNullAndNotEmpty(phone)) {
				user.setPhone(phone.get().toString());
			}

			final Attribute firstName = attrs.get("givenName");
			if (isLdapAttributeNotNullAndNotEmpty(firstName)) {
				user.setFirstName(firstName.get().toString());
			}

			final Attribute lastName = attrs.get("sn");
			if (isLdapAttributeNotNullAndNotEmpty(lastName)) {
				user.setLastName(lastName.get().toString());
			}
			return user;
		}
	};

	private final EqualsFilter ldapFilter = new EqualsFilter("objectclass",
			"user");

	private final SearchControls ldapSearchControls = new SearchControls();

	@Autowired
	protected DashboardWidgetInitializer widgetInitializer;

	@Autowired
	private InternalPolicyConfigurationService policyConfigurationService;

	@Override
	@Transactional(readOnly = false)
	public void addWidgetToDashboard(final DashboardWidget widget)
			throws LimitExceededException, DuplicateException,
			UsernameNotFoundException, SecurityException {
		final MUser currentUser = getCurrentUser();
		if (currentUser == null) {
			throw new SecurityException(Reason.USER_NOT_FOUND);
		}
		final String username = currentUser.getLogin();
		MDashboard dashboard = getDashboard(username);
		if (dashboard == null) {
			dashboard = new MDashboard();
			dashboard.setUsername(username);
		}
		final int actualSize = dashboard.getWidgets().size();
		if (actualSize >= maxDashboardWidgetsCount) {
			throw new LimitExceededException(maxDashboardWidgetsCount,
					actualSize);
		}
		if (dashboard.hasWidget(widget)) {
			throw new DuplicateException();
		}
		if (widget instanceof DashboardChartWidget) {
			final DashboardChartWidget chartWidget = (DashboardChartWidget) widget;
			saveChartSeries(chartWidget);
		}
		dashboard.addWidget(widget);
		updateDashboard(dashboard);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.PROPERTY);
		criterionReader = objectMapper.reader(Criterion.class);
		dashboardWidgetsReader = objectMapper.reader(Map.class);
		jsonWriter = objectMapper.writerWithDefaultPrettyPrinter();
		ldapSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	}

	private void clearAgentRelatedWidgets(final MAgent agent,
			final List<MDashboard> dashboards) {
		final Map<String, DashboardWidget> widgetsToUpdate = new LinkedHashMap<String, DashboardWidget>();
		for (final MDashboard dashboard : dashboards) {
			for (final Map.Entry<String, DashboardWidget> widgetEntry : dashboard
					.getWidgets().entrySet()) {
				final DashboardWidget widget = widgetEntry.getValue();
				if (widget instanceof DashboardAgentsWidget) {
					final DashboardAgentsWidget agentsWidget = (DashboardAgentsWidget) widget;
					final Set<String> agentKeys = agentsWidget.getAgentKeys();
					if (agentKeys.contains(agent.getKey())) {
						final String oldWidgetKey = agentsWidget.getKey();
						agentKeys.remove(agent.getKey());
						widgetsToUpdate.put(oldWidgetKey, agentsWidget);
					}
				}
			}
			updateWidgetsInDashboard(dashboard, widgetsToUpdate);
            widgetsToUpdate.clear();
		}
	}

	private void clearSourceRelatedReportTemplates(final MSource source) {
		final String sourceKey = source.getKey();
		final List<MUserReportsTemplate> templates = modelSpace.find(
				MUserReportsTemplate.class, modelSpace.createCriterionQuery()
						.collectionContains("sourceKeys", sourceKey));

		for (final MUserReportsTemplate template : templates) {
			final Set<String> templateSourceKeys = template.getSourceKeys();
			final boolean isChanged = templateSourceKeys.remove(sourceKey);

			if (templateSourceKeys.isEmpty()) {
				modelSpace.delete(template);
			} else if (isChanged) {
				modelSpace.saveOrUpdate(template);
			}
		}
	}

	private void clearSourceRelatedResultTemplates(final MSource source) {
		final List<MChartSeries> allChartSeriesToDelete = modelSpace.find(
				MChartSeries.class,
				modelSpace.createCriterionQuery().eq("task.key",
						source.getKey()));

		if (allChartSeriesToDelete.size() > 0) {
			final List<MUserResultTemplate> templates = modelSpace
					.find(MUserResultTemplate.class,
							createTemplateContainingSeriesCriterion(allChartSeriesToDelete));

			for (final MUserResultTemplate template : templates) {
				final Set<MChartSeries> templateSeries = template.getSeries();

				if (allChartSeriesToDelete.containsAll(templateSeries)) {
					modelSpace.delete(template);
				} else {
					final Set<MChartSeries> chartSeriesToDeleteFromTemplate = new HashSet<MChartSeries>(
							allChartSeriesToDelete);
					chartSeriesToDeleteFromTemplate.retainAll(templateSeries);
					if (templateSeries
							.removeAll(chartSeriesToDeleteFromTemplate)) {
						modelSpace.saveOrUpdate(template);
						for (final MChartSeries chartSeries : chartSeriesToDeleteFromTemplate) {
							modelSpace.delete(chartSeries);
						}
					}
				}
			}
		}
	}

	@Override
	public void clearSourceRelatedTemplates(final MSource source) {
		clearSourceRelatedReportTemplates(source);
		clearSourceRelatedResultTemplates(source);
	}

	@Override
	@Transactional
	public void clearSourceRelatedWidgets(final MSource source) {
		final List<MDashboard> dashboards = getAllDashboards();

		if (source instanceof MAgent) {
			clearAgentRelatedWidgets((MAgent) source, dashboards);
		} else if (source instanceof MAgentTask) {
			clearTaskRelatedWidgets((MAgentTask) source, dashboards);
		}
	}

	protected void clearTaskRelatedWidgets(final MAgentTask task,
			final List<MDashboard> dashboards) {
		final Map<String, DashboardWidget> widgetsToUpdate = new LinkedHashMap<String, DashboardWidget>();
		for (final MDashboard dashboard : dashboards) {
			for (final Map.Entry<String, DashboardWidget> widgetEntry : dashboard
					.getWidgets().entrySet()) {
				final DashboardWidget widget = widgetEntry.getValue();
				if (widget instanceof DashboardChartWidget) {
					final List<DashboardChartWidget.ChartSeriesData> widgetChartSeriesData = ((DashboardChartWidget) widget)
							.getSeriesData();
					final String oldWidgetKey = widget.getKey();
					final int oldChartSeriesSize = widgetChartSeriesData.size();

					removeChartSeriesByTask((DashboardChartWidget) widget, task);

					if (oldChartSeriesSize > widgetChartSeriesData.size()) {
						widgetsToUpdate.put(oldWidgetKey, widget);
					}
				}
			}
			updateWidgetsInDashboard(dashboard, widgetsToUpdate);
            widgetsToUpdate.clear();
		}
	}

	private MUser convertUser(final Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getName())) {
			return findUser(authentication.getName());
		}
		return null;
	}

	private Criterion createFindTemplateCriterion(final Long userId,
			final String templateName) {
		final CriterionQuery criterionQuery = CriterionQueryFactory.getQuery();
		final Criterion userIdCriterion = criterionQuery.eq("user.id", userId);
		final Criterion templateNameCriterion = criterionQuery.eq("name",
				templateName);
		return criterionQuery.and(userIdCriterion, templateNameCriterion);
	}

	private Criterion createTemplateContainingSeriesCriterion(
			final List<MChartSeries> chartSeries) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion result = null;

		for (final MChartSeries chartSeria : chartSeries) {
			final Criterion criterion = query.collectionContains("series",
					chartSeria);

			if (result == null) {
				result = criterion;
			} else {
				result = query.or(result, criterion);
			}
		}

		return result;
	}

	private void deserializeDashboardWidgets(final MDashboard dashboard) {
		final String serializedWidgets = dashboard.getSerializedWidgets();
		final Map<String, DashboardWidget> widgets;
		try {
			widgets = dashboardWidgetsReader.readValue(serializedWidgets);
		} catch (final IOException ex) {
			throw new ModelSpaceException(
					"Cannot desirialize dashboard from datastore", ex);
		}
		final Map<String, DashboardWidget> configuredWidgets = new LinkedHashMap<String, DashboardWidget>();
		for (final Map.Entry<String, DashboardWidget> entry : widgets
				.entrySet()) {
			try {
				widgetInitializer.setupWidget(entry.getValue());
				configuredWidgets.put(entry.getKey(), entry.getValue());
			} catch (final Exception ex) {
				LOGGER.error("Cannot configure widget for dashboard", ex);
			}
		}
		dashboard.setWidgets(configuredWidgets);
	}

	protected void deserialzeCriterionInTemplates(
			final List<SerializedCriterionContainer> containers) {
		final List<SerializedCriterionContainer> blackList = new ArrayList<SerializedCriterionContainer>();
		for (final SerializedCriterionContainer container : containers) {
			final String serializedCriterion = container
					.getSerializedCriterion();
			if (serializedCriterion != null) {
				try {
					final Criterion criterion = criterionReader
							.<Criterion> readValue(serializedCriterion);
					container.setCriterion(criterion);
				} catch (final Exception ex) {
					LOGGER.error("Cannot desirialize criterion for "
							+ container, ex);
					blackList.add(container);
				}
			}
		}
		containers.removeAll(blackList);
	}

	@Override
	public MUser findUser(final String login) {
		return modelSpace.findUniqueEntity(MUser.class, CriterionQueryFactory
				.getQuery().eq("login", login));
	}

	@Override
	public List<MContactInformation> getAllContactInformations() {
		return modelSpace.getAll(MContactInformation.class);
	}

	@Override
	public List<MDashboard> getAllDashboards() {
		final List<MDashboard> dashboards = modelSpace.getAll(MDashboard.class);
		for (final MDashboard dashboard : dashboards) {
			deserializeDashboardWidgets(dashboard);
		}
		return dashboards;
	}

	@Override
	public MUser getCurrentUser() {
		final Authentication authentication = SecurityContextHolder
				.getContext().getAuthentication();
		return convertUser(authentication);
	}

	@Override
	public MDashboard getDashboard(final String username) {
		final MDashboard dashboard = modelSpace.findUniqueEntity(
				MDashboard.class,
				modelSpace.createCriterionQuery().eq("username", username));
		if (dashboard != null) {
			deserializeDashboardWidgets(dashboard);
			// Clean up hibernate internal properties
			dashboard.setSerializedWidgets(null);
		}
		return dashboard;
	}

	public LdapTemplate getLdapTemplate() {
		return ldapTemplate;
	}

	@Override
	public List<MUser> getLdapUsers() {

		final List<MUser> users = new ArrayList<MUser>();

		if (ldapEnabled) {
			// processor should be re-initialized for each request to get all
			// pages from the start
			final PagedResultsDirContextProcessor ldapProcessor = new PagedResultsDirContextProcessor(
					ldapPageSize);
			do {
				final List<MUser> partialUsers = ldapTemplate.search("",
						ldapFilter.encode(), ldapSearchControls,
						ldapUserAttributeMapper, ldapProcessor);

                for (MUser user : partialUsers) {
                    if (Utils.isUserLoginValid(user.getLogin())) {
                        users.add(user);
                    }
                }
			} while (ldapProcessor.hasMore());
		}

		return users;
	}

	@Override
	public MUserAbstractTemplate getTemplate(final TemplateType templateType,
			final Long userId, final String templateName) {
		final Class<? extends MUserAbstractTemplate> templateClass = getTemplateClass(templateType);

		final MUserAbstractTemplate result = modelSpace.findUniqueEntity(
				templateClass,
				createFindTemplateCriterion(userId, templateName));

		if (result != null
				&& SerializedCriterionContainer.class
						.isAssignableFrom(templateClass)) {
			deserialzeCriterionInTemplates(Arrays
					.<SerializedCriterionContainer> asList((SerializedCriterionContainer) result));
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends MUserAbstractTemplate> getTemplateClass(
			final TemplateType templateType) {
		Class<? extends MUserAbstractTemplate> clazz = null;
		try {
			clazz = (Class<? extends MUserAbstractTemplate>) Class
					.forName(templateType.getTemplateClassName());
		} catch (final ClassNotFoundException e) {
			final String message = "Cannot find template class for "
					+ templateType.getTemplateClassName();
			LOGGER.error(message, e);
			throw new ServiceException(message);
		}

		return clazz;
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public List<? extends MUserAbstractTemplate> getTemplates(
			final TemplateType templateType, final Long userId) {
		final Class<? extends MUserAbstractTemplate> templateClass = getTemplateClass(templateType);

		final List<? extends MUserAbstractTemplate> result = modelSpace.find(
				templateClass,
				CriterionQueryFactory.getQuery().eq("user.id", userId));

		if (result != null
				&& SerializedCriterionContainer.class
						.isAssignableFrom(templateClass)) {
			deserialzeCriterionInTemplates((List) result);
		}

		return result;
	}

	private boolean isLdapAttributeNotNullAndNotEmpty(final Attribute attribute) {
		boolean result = false;
		try {
			result = attribute != null && attribute.get() != null
					&& !attribute.get().toString().trim().isEmpty();
		} catch (final NamingException e) {
			LOGGER.error(e);
		}
		return result;
	}

	@Override
	public <M extends WidgetData> List<M> loadWigetData(
			final HasUpdatableData<M> widget) {
		return widgetInitializer.loadData(widget);
	}

	private void removeChartSeries(final DashboardChartWidget widget) {
		final Iterator<DashboardChartWidget.ChartSeriesData> iterator = widget.getSeriesData().iterator();
		while (iterator.hasNext()) {
			final DashboardChartWidget.ChartSeriesData chartSeriesData = iterator
					.next();
			removeSingleChartSeries(chartSeriesData);
			iterator.remove();
		}
	}

	private void removeChartSeriesByTask(final DashboardChartWidget widget,
			final MAgentTask task) {
		final Iterator<DashboardChartWidget.ChartSeriesData> iterator = widget.getSeriesData().iterator();
		while (iterator.hasNext()) {
			final DashboardChartWidget.ChartSeriesData chartSeriesData = iterator
					.next();

			if (task.getId().equals(chartSeriesData.getTaskId())) {
				removeSingleChartSeries(chartSeriesData);
				iterator.remove();
			}
		}
	}

	private void removeSingleChartSeries(
			final DashboardChartWidget.ChartSeriesData chartSeriesData) {
		final MChartSeries chartSeries = modelSpace.get(MChartSeries.class,
                chartSeriesData.getId());
		modelSpace.delete(chartSeries);
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void removeTemplate(final TemplateType templateType,
			final Long userId, final String templateName) {
		executeInTransaction(false, new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				final MAbstractEntity template = modelSpace.findUniqueEntity(
						getTemplateClass(templateType),
						createFindTemplateCriterion(userId, templateName));
				if (template != null) {
					modelSpace.delete(template);
				}
			}
		});
	}

	@Override
	@Transactional(readOnly = false)
	public void removeWidgetFromDashboard(final String widgetKey)
			throws ServiceException, SecurityException,
			IllegalArgumentException {
		final MUser currentUser = getCurrentUser();
		if (currentUser == null) {
			throw new SecurityException(Reason.USER_NOT_FOUND);
		}
		final String username = currentUser.getLogin();
		final MDashboard dashboard = getDashboard(username);
		if (dashboard == null) {
			throw new ServiceException("Dashboard is empty");
		}
		if (!dashboard.hasWidget(widgetKey)) {
			throw new IllegalArgumentException(
					"Dashboard doesn't have widget with key : " + widgetKey);
		}
		final DashboardWidget removedWidget = dashboard.removeWidget(widgetKey);
		updateDashboard(dashboard);
		if (removedWidget instanceof DashboardChartWidget) {
			final DashboardChartWidget chartWidget = (DashboardChartWidget) removedWidget;
			removeChartSeries(chartWidget);
		}
	}

	private void saveChartSeries(final DashboardChartWidget chartWidget) {
		final List<DashboardChartWidget.ChartSeriesData> updatedSeriesData = new ArrayList<>();
		final List<MChartSeries> series = chartWidget.getSeries();
		for (final MChartSeries chartSeries : series) {
			final MChartSeries seriesCopy = new MChartSeries(chartSeries);
			modelSpace.save(seriesCopy);
			updatedSeriesData.add(DashboardChartWidget.ChartSeriesData
					.fromMChartSeries(seriesCopy));
		}
		chartWidget.setSeriesData(updatedSeriesData);
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MUserAbstractTemplate saveTemplate(
			final MUserAbstractTemplate template) {
		if (template instanceof SerializedCriterionContainer) {
			serializeCriterionInTemplate((SerializedCriterionContainer) template);
		}
		// search template in db
		final MUserAbstractTemplate foundExistingTemplate = executeInTransaction(
				true, new TransactionCallback<MUserAbstractTemplate>() {

					@Override
					public MUserAbstractTemplate doInTransaction(
							final TransactionStatus status) {
						return modelSpace.findUniqueEntity(
								template.getClass(),
								createFindTemplateCriterion(template.getUser()
										.getId(), template.getName()));
					}
				});
		// remove previous template
		if (foundExistingTemplate != null) {
			executeInTransaction(false, new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(
						final TransactionStatus status) {
					modelSpace.delete(foundExistingTemplate);
				}
			});
		}
		// save template as new one
		final MUserAbstractTemplate result = executeInTransaction(false,
				new TransactionCallback<MUserAbstractTemplate>() {

					@Override
					public MUserAbstractTemplate doInTransaction(
							final TransactionStatus status) {
						modelSpace.save(template.copy());
						return template;
					}
				});
		HibernateEntityConverter.convertHibernateCollections(result,
                PersistentCollection.class);
		return result;
	}

	private void serializeCriterionInTemplate(
			final SerializedCriterionContainer container) {
		final Criterion criterion = container.getCriterion();

		try {
			final String serializedCriterion = jsonWriter
					.writeValueAsString(criterion);
			container.setSerializedCriterion(serializedCriterion);
		} catch (final Exception ex) {
			throw new ModelSpaceException(
					"Cannot serialize criterion in template", ex);
		}
	}

	public void setLdapTemplate(final LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	@Override
	@Transactional
	public void updateCurrentUser(final MUser user) {
		final MUser currentUser = getCurrentUser();
		boolean sendUpdatePolicyConfig = false;

		if (currentUser != null && user != null) {

			final UserSettings currentSettings = currentUser.getSettings();
			final UserSettings newSettings = user.getSettings();

			final NotificationLanguage currentNotifLang = currentSettings == null
					? null
					: currentSettings.getNotificationLanguage();
			final NotificationLanguage newNotifLang = newSettings == null
					? null
					: newSettings.getNotificationLanguage();

			sendUpdatePolicyConfig = currentNotifLang != newNotifLang;

			if (currentUser.updateSimpleFields(user)) {
				modelSpace.update(currentUser);
			}
		}
		AuditLogger.critical(AuditLogger.SyslogCategory.USER, AuditLogger.SyslogActionStatus.OK, "User {} updated", currentUser.toString());
		if (sendUpdatePolicyConfig) {
			policyConfigurationService.updatePolicyConfigurationsByUser(user);
		}
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void updateDashboard(final MDashboard dashboard)
			throws LimitExceededException {
		final int actualSize = dashboard.getWidgets().size();
		if (actualSize > maxDashboardWidgetsCount) {
			throw new LimitExceededException(maxDashboardWidgetsCount,
					actualSize);
		}

		Assert.isTrue(dashboard.getUsername() != null
				&& !dashboard.getUsername().isEmpty(),
				"Username is not specified");

		final String serializedWidgets;
		try {
			serializedWidgets = jsonWriter.writeValueAsString(dashboard
					.getWidgets());
		} catch (final IOException ex) {
			throw new ModelSpaceException(
					"Cannot serialize dasboard widgets for datastore", ex);
		}
		dashboard.setSerializedWidgets(serializedWidgets);

		executeInTransaction(false, new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				final MUser user = modelSpace.findUniqueEntity(
						MUser.class,
						modelSpace.createCriterionQuery().eq("login",
								dashboard.getUsername()));
				if (user == null) {
					throw new ModelSpaceException("User "
							+ dashboard.getUsername() + " not found");
				}
				modelSpace.saveOrUpdate(dashboard);
			}
		});
	}

	private void updateWidgetsInDashboard(
            final MDashboard dashboard,
            final Map<String, DashboardWidget> widgetsToUpdate) {
		if (!widgetsToUpdate.isEmpty()) {
			for (final Map.Entry<String, DashboardWidget> widgetEntry : widgetsToUpdate
					.entrySet()) {
				final String widgetKey = widgetEntry.getKey();
				final DashboardWidget widget = widgetEntry.getValue();

				if (widget.isEmpty()) {
					dashboard.removeWidget(widgetKey);
				} else {
					dashboard.updateWidgetByKey(widgetKey, widget);
				}
			}
			updateDashboard(dashboard);
		}
	}

	@Transactional
	@Override
	public void updatePassword(final String oldPassword,
			final String newPassword) throws SecurityException {
		final PasswordEncoder encoder = new Md5PasswordEncoder();
		final MUser currentUser = getCurrentUser();
		final String oldPasswordHash = encoder
				.encodePassword(oldPassword, null);

		if (!oldPasswordHash.equals(currentUser.getPassword())) {
			AuditLogger.critical(AuditLogger.SyslogCategory.USER, AuditLogger.SyslogActionStatus.NOK, "Unable to change password for user {}. Old password does not match with user password!", currentUser.getLogin());
			throw new SecurityException(Reason.INCORRECT_OLD_PASSWORD,
					"Old password does not match with user password!");
		}

		currentUser.setPassword(encoder.encodePassword(newPassword, null));
		modelSpace.update(currentUser);
		AuditLogger.critical(AuditLogger.SyslogCategory.USER, AuditLogger.SyslogActionStatus.OK, "Password for user {} changed", currentUser.getLogin());
	}
}
