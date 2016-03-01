package com.tecomgroup.qos.rest;

import java.io.*;
import java.net.URL;
import java.util.*;

import com.tecomgroup.qos.exception.DuplicateException;
import com.tecomgroup.qos.modelspace.jdbc.dao.JdbcChannelServiceDao;
import com.tecomgroup.qos.service.StreamsInfoService;
import com.tecomgroup.qos.service.UserService;
import com.tecomgroup.qos.service.bean.ChannelViewSessionContext;
import com.tecomgroup.qos.service.bean.SessionContextBean;
import com.tecomgroup.qos.service.rbac.AuthorizeService;
import com.tecomgroup.qos.util.SimpleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.rest.data.*;
import com.tecomgroup.qos.service.alert.AlertReportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author stroganov.d
 */
public class ChannelServiceImpl implements ChannelService {

	private final static Logger LOGGER = Logger
			.getLogger(ChannelServiceImpl.class);

	private static final String TASKS_RECORD_AND_STREAM_KEY = "RecordAndStream";
	@Autowired
	private AlertReportService alertReportService;

	@Autowired
	private SessionContextBean sessionContextBean;

	@Qualifier("sessionFactory")
	@Autowired
	private SessionFactory sessionFactory;

	@Qualifier("agentService")
	@Autowired
	private StreamsInfoService agentService;

	@Autowired
	private AuthorizeService authorizeService;

	@Autowired
	private UserService userService;

	@Autowired
	private JdbcChannelServiceDao channelServiceDataProvider;

	@Value("${client.hide.channelview.page}")
	boolean noNavigateToChannelViewPage;

	@Value("${client.hide.livevideo.page}")
	boolean noNavigateToLiveVideoPage;

	@Value("${client.hide.recordedvideo.page}")
	boolean noNavigateToRecordedVideoPage;

	private Map<String, ParameterGroup> groupsConfig;

	public ChannelServiceImpl() {
	}

	public ChannelsStateResponse getCommonChannelsSates(boolean includesConfiguration) {
		SetConfiguration[] channels = getAllUserChannels();
		TimeInterval interval = getMaximumInterval(channels);
		Map<String, SetConfiguration> sources = getChannelsAlertsSourceKeys(channels);
		// Criterion criterion=new UnaryCriterion("endDateTime",
		// Criterion.UnaryOperation.isNull);
		List<ChannelStateCommon> stateList = new ArrayList();
		if(sources.size() > 0) {
			List<MAlertReport> reports = alertReportService.getAlertReports(
					sources.keySet(), interval, null, Order.asc("startDateTime"),
					null, null);
			for (int i = 0; i < channels.length; i++) {
				SetConfiguration channel = channels[i];
				ChannelStateCommon state = createChannelState(
						includesConfiguration, channel);
				TimeInterval timeInterval = getTimeInterval(channel.interval);
				state.setEndDate(timeInterval.getEndDateTime().getTime());
				state.setStartDate(timeInterval.getStartDateTime().getTime());
				stateList.add(state);
				Set<String> sourceKeys = channel.retriveSourceKeys();
				for (MAlertReport report : reports) {
					if (report.getEndDateTime() == null || report.getEndDateTime().after(new Date(state.getStartDate()))) {
						String alertKey = report.getAlert().getSource().getKey();
						if (sourceKeys.contains(alertKey)) {
							ParameterGroup parameterGroup = getParameterGroup(report);
							state.updateState(parameterGroup, report);
						}
					}
				}
			}
		}
		ViewConfiguration config = getUserViewConfig();
		ChannelsStateResponse response = new ChannelsStateResponse(config,
				stateList);
		return response;
	}

	public ChannelStateCommon getDetailedChannelSate(long channelId) {

		SetConfiguration channel = getChannelById(channelId);
		TimeInterval interval = getMaximumInterval(new SetConfiguration[]{channel});
		ChannelStateHighDetailed state = new ChannelStateHighDetailed(channel);
		state.setStartDate(interval.getStartDateTime().getTime());
		state.setEndDate(interval.getEndDateTime().getTime());
		Set<String> sourceKeys = channel.retriveSourceKeys();
		//init all ParameterGroups for detailed view
		for (String key : sourceKeys )
		{
			ParameterGroup pg=getParameterGroup(key);
			state.addParameterState(pg);
		}
		List<MAlertReport> reports = alertReportService.getAlertReports(
				sourceKeys, interval, null, Order.asc("startDateTime"), null,
				null);
		for (MAlertReport report : reports) {
			String alertKey = report.getAlert().getSource().getKey();
			if (sourceKeys.contains(alertKey)) {
				ParameterGroup parameterGroup = getParameterGroup(report);
				state.updateState(parameterGroup, report);
			}
		}
		return state;
	}

	public SetConfiguration getChannelById(Long channelId) {

		Session session = sessionFactory.openSession();

		try {
			MSetConfiguration config = (MSetConfiguration) session.get(
					MSetConfiguration.class, channelId);

			SetConfiguration channelConfig = parseChannelConfig(config);
			Set<ParameterGroup> groupSet = new HashSet<>();
			Set<String> keys = channelConfig.retriveSourceKeys();
			for (String key : keys) {
				ParameterGroup group = getParameterGroup(key);
				groupSet.add(group);
			}
			channelConfig.groups = groupSet;
			return channelConfig;

		} finally {
			session.close();
		}
	}

	public SetConfiguration[] getAllUserChannels() {
		Session session = sessionFactory.openSession();
		try {
			MUser muser=getCurrentMUser();
			Criteria criteria = session.createCriteria(MSetConfiguration.class).add(Restrictions.eq("userId",muser.getId()));
			criteria.add(Restrictions.eq("channel", Boolean.TRUE));
			return getSetsConfiguration(criteria).toArray(
					new SetConfiguration[0]);
		} finally {
			session.close();
		}
	}

	public SetConfiguration[] getAllUserSets() {
		Session session = sessionFactory.openSession();
		try {
			MUser muser=getCurrentMUser();
			Criteria criteria = session.createCriteria(MSetConfiguration.class).add(Restrictions.eq("userId", muser.getId()));
			return getSetsConfiguration(criteria).toArray(
					new SetConfiguration[0]);
		} finally {
			session.close();
		}
	}

	private List<SetConfiguration> getSetsConfiguration(Criteria criteria) {
		Session session = sessionFactory.openSession();
		List<SetConfiguration> channelsList = new ArrayList<>();
		try {
			List<MSetConfiguration> setConfigList = criteria.list();
			for (MSetConfiguration config : setConfigList) {
				SetConfiguration channelConfig = parseChannelConfig(config);
				Set<ParameterGroup> groupSet = new HashSet<>();
				Set<String> keys = channelConfig.retriveSourceKeys();
				for (String key : keys) {
					ParameterGroup group = getParameterGroup(key);
					groupSet.add(group);
				}
				channelConfig.groups = groupSet;
				channelsList.add(channelConfig);
			}
		} finally {
			session.close();
		}
		return channelsList;
	}

	private SetConfiguration parseChannelConfig(MSetConfiguration config) {
		SetConfiguration setConfiguration = new SetConfiguration();
		setConfiguration.id = config.getId();
		setConfiguration.name = config.getName();
		setConfiguration.logo = config.getLogo();
		setConfiguration.interval = config.getTimeinterval();
		setConfiguration.isChannel = config.getChannel();
		setConfiguration.isFavourite = config.isFavourite();

		Map<MAgent, Probe> probesMap = new HashMap<>();
		for (MSetConfigurationMAgentTask conf_task : config
				.getMsetConfigurationMagentTaskCollection()) {
			MAgent agent = conf_task.getmAgentTask().getModule().getAgent();
			MAgentTask agent_task = conf_task.getmAgentTask();
			boolean isDefStream = conf_task.isDefaultstreamsource();
			Probe probe = probesMap.get(agent);
			if (probe == null) {
				probe = new Probe();
				probe.entityKey = agent.getKey();
				probe.name = agent.getDisplayName();
				probe.id = agent.getId();
				probesMap.put(agent, probe);
			}
			QoSTask qosTask=getSessionQoSTaskConfig(agent_task.getKey());
			if(qosTask!=null){
				if(qosTask.streams.isEmpty())
				{
					MProperty property=agent_task.getProperty(MAgentTask.RELEATED_RECORDING_TASK_PROPERTY_NAME);
					if(property!=null)
					{
						QoSTask relatedTask=getSessionQoSTaskConfig(property.getValue());
						if(relatedTask!=null) {
							qosTask.relatedRecordingTaskId =relatedTask.id;
							qosTask.streams = relatedTask.streams;
						}
					}
				}
				if (qosTask.streams!=null && !qosTask.streams.isEmpty() && isDefStream) {
						setConfiguration.streams=qosTask.streams;
				}
				probe.tasks.add(qosTask);
			}

		}
		setConfiguration.probes.addAll(probesMap.values());
		return setConfiguration;
	}

	private ChannelStateCommon createChannelState(
			boolean includesConfiguration, SetConfiguration channel) {

		if (includesConfiguration) {
			return new ChannelStateMinorDetailed(channel);
		} else {
			ChannelStateCommon state = new ChannelStateMinorDetailed(channel);
			state.setConfiguration(null);
			return state;
		}
	}

	public Long createUserChannel(SetConfiguration channelConfig) {
		Session session = sessionFactory.openSession();
		MSetConfiguration entity = new MSetConfiguration();
		MUser muser = getCurrentMUser();
		entity.setUserId(muser.getId());
		boolean hasName = channelServiceDataProvider.hasName(muser.getId(), channelConfig.name);
		if(hasName) {
			throw new DuplicateException();
		}
		try {
			session.getTransaction().begin();
			copyConfigurationFields(channelConfig, entity);
			mergeTaskRelations(channelConfig, entity);
			session.save(entity);
			session.getTransaction().commit();
			sessionContextBean.dropTaskConfigurationContext();
			return entity.getId();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	public Long updateChannel(SetConfiguration channelConfig) {
		Session session = sessionFactory.openSession();
		try {
			session.getTransaction().begin();
			MSetConfiguration entity = (MSetConfiguration) session.get(
					MSetConfiguration.class, channelConfig.id);

			Collection<MSetConfigurationMAgentTask> oldkeySet = new HashSet<>();
			oldkeySet.addAll(entity.getMsetConfigurationMagentTaskCollection());
			copyConfigurationFields(channelConfig, entity);
			Collection<MSetConfigurationMAgentTask> relationToDelete = mergeTaskRelations(
					channelConfig, entity);
			for (MSetConfigurationMAgentTask taskToDelete : relationToDelete) {
				session.delete(taskToDelete);
			}
			session.update(entity);
			session.getTransaction().commit();
			sessionContextBean.dropTaskConfigurationContext();
			return entity.getId();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private Collection<MSetConfigurationMAgentTask> mergeTaskRelations(
			SetConfiguration channelConfig, MSetConfiguration entity) {
		HashSet<MSetConfigurationMAgentTask> relationToDelete = new HashSet<>();
		Collection<MSetConfigurationMAgentTask> entityRelationSet = entity
				.getMsetConfigurationMagentTaskCollection();
		HashMap<MSetConfigurationMAgentTask, MSetConfigurationMAgentTask> configurationRelationSet = new HashMap<>();
		for (Probe probe : channelConfig.probes) {
			for (QoSTask task : probe.tasks) {
				MSetConfigurationMAgentTask mSetConfigTask = new MSetConfigurationMAgentTask();
				mSetConfigTask.setmSetConfiguration(entity);
				mSetConfigTask.setDefaultstreamsource(task.defaultStream);
				MAgentTask agentTask = new MAgentTask();
				agentTask.setId(task.id);
				mSetConfigTask.setmAgentTask(agentTask);
				configurationRelationSet.put(mSetConfigTask, mSetConfigTask);
			}
		}
		for (MSetConfigurationMAgentTask checkRelation : entityRelationSet) {
			if (!configurationRelationSet.keySet().contains(checkRelation)) {

				/*
				 * checkRelation.setmAgentTask(null);
				 * checkRelation.setmSetConfiguration(null);
				 */
				relationToDelete.add(checkRelation);
			} else {
				MSetConfigurationMAgentTask configRelation = configurationRelationSet
						.get(checkRelation);
				checkRelation.setDefaultstreamsource(configRelation
						.isDefaultstreamsource());
				configurationRelationSet.remove(checkRelation);
			}
		}
		entityRelationSet.removeAll(relationToDelete);
		entityRelationSet.addAll(configurationRelationSet.keySet());
		return relationToDelete;
	}

	private void copyConfigurationFields(SetConfiguration channelConfig,
			MSetConfiguration entity) {
		entity.setLogo(channelConfig.logo);
		entity.setChannel(channelConfig.isChannel);
		entity.setName(channelConfig.name);
		entity.setTimeinterval(channelConfig.interval);
		entity.setFavourite(channelConfig.isFavourite);
	}

	public boolean deleteChannelById(Long id) {
		Session session = sessionFactory.openSession();
		try {
			session.getTransaction().begin();
			MSetConfiguration entity = (MSetConfiguration) session.get(
					MSetConfiguration.class, id);
			for (MSetConfigurationMAgentTask confTask : entity
					.getMsetConfigurationMagentTaskCollection()) {
				session.delete(confTask);
			}
			session.delete(entity);
			session.getTransaction().commit();
			sessionContextBean.dropTaskConfigurationContext();
			return true;
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private Map<String, SetConfiguration> getChannelsAlertsSourceKeys(
			SetConfiguration[] channels) {
		Map<String, SetConfiguration> sourceKeys = new HashMap();
		for (int i = 0; i < channels.length; i++) {
			SetConfiguration channel = channels[i];
			for (String key : channel.retriveSourceKeys()) {
				sourceKeys.put(key, channel);
			}
		}
		return sourceKeys;
	}

	public ViewConfiguration getUserViewConfig() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,
				false);
		try {
			URL url = getClass().getResource("/view-config.json");
			ViewConfiguration config = mapper.readValue(url,
					ViewConfiguration.class);
/*			config.selectedTimeInterval.setEndDateTime(TimeInterval.getEndDate(
					null, config.selectedTimeInterval.getType()));
			config.selectedTimeInterval.setStartDateTime(TimeInterval
					.getStartDate(null,
							config.selectedTimeInterval.getEndDateTime(),
							config.selectedTimeInterval.getType()));*/
			return config;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<MAgentTask> getPermittedTasks(Session session) {
		List<String> agentKeys = authorizeService.getProbeKeysUserCanManage();

		Criteria taskCriteria = session.createCriteria(MAgentTask.class);
		taskCriteria.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("disabled", Boolean.FALSE));

		Criteria moduleCriteria = taskCriteria.createCriteria("module");

		Criteria agentCriteria = moduleCriteria.createCriteria("agent");
		agentCriteria.add(Restrictions.in("name", agentKeys));

		return taskCriteria.list();

	}

	public Probe[] getProbesConfig() {
		return getProbesConfig(false);
	}

	public Probe[] getProbesConfig(boolean onlyDisplayable) {
		Session session = sessionFactory.openSession();
		Map<MAgent, Probe> probesMap = new HashMap<>();

		try {
			List<MAgentTask> tasks = getPermittedTasks(session);
			Map<String, MAgentTask> tasksMap = new HashMap<>();

			for (MAgentTask agent_task : tasks) {
				tasksMap.put(agent_task.getKey(), agent_task);
			}

			for (MAgentTask agent_task : tasks) {
				if (!onlyDisplayable || (onlyDisplayable && isDisplayableTask(agent_task))) {
					QoSTask qosTask = new QoSTask();
					MAgent agent = agent_task.getModule().getAgent();
					Probe probe = probesMap.get(agent);
					if (probe == null) {
						probe = new Probe();
						probe.entityKey = agent.getKey();
						probe.name = agent.getDisplayName();
						probe.id = agent.getId();
						probesMap.put(agent, probe);
					}
					qosTask.entityKey = agent_task.getKey();
					qosTask.id = agent_task.getId();
					qosTask.name = agent_task.getDisplayName();
					qosTask.parameterGroup = getParameterGroup(agent_task);
					MProperty property = agent_task.getProperty(MAgentTask.RELEATED_RECORDING_TASK_PROPERTY_NAME);
					if (property != null) {
						MAgentTask relatedTask = tasksMap.get(property.getValue());
						if (relatedTask != null) {
							qosTask.relatedRecordingTaskId = relatedTask.getId();
						}
					}
					List streamsList = agentService.getStreams(agent_task, MLiveStream.class);
					qosTask.addStreams(streamsList);
					streamsList = agentService.getStreams(agent_task, MRecordedStream.class);
					qosTask.addStreams(streamsList);

					probe.tasks.add(qosTask);
				}
			}
		} finally {
			session.close();
		}
		return probesMap.values().toArray(new Probe[0]);
	}


	private boolean isDisplayableTask(MAgentTask task)
	{
		if(task.getKey().contains(TASKS_RECORD_AND_STREAM_KEY))
		{
			return false;
		}
		return true;
	}


	private TimeInterval getMaximumInterval(SetConfiguration[] channels) {
		Long maxIntervalLong = 0L;
		for (int i = 0; i < channels.length; i++) {
			SetConfiguration conf = channels[i];
			if (conf.interval != null
					&& maxIntervalLong.compareTo(conf.interval) < 0) {
				maxIntervalLong = conf.interval;
			}
		}

		Calendar calendar = new GregorianCalendar();
		int intInterval = maxIntervalLong.intValue();
		calendar.add(Calendar.MILLISECOND, -intInterval);
		TimeInterval interval = TimeInterval
				.get(calendar.getTime(), new Date());
		return interval;
	}

	private TimeInterval getTimeInterval(Long interval) {
		Calendar calendar = new GregorianCalendar();
		int intInterval = interval.intValue();
		calendar.add(Calendar.MILLISECOND, -intInterval);
		TimeInterval timeInterval = TimeInterval
				.get(calendar.getTime(), new Date());
		return timeInterval;
	}

	public Map<String, ParameterGroup> getParametersGroupConfig() {
		//@TODO cache this object
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,
				false);
		try {
			URL url = getClass().getResource("/parameters-group-mapping.json");
			Map<String, ParameterGroup> config = mapper.readValue(url,
					new TypeReference<Map<String, ParameterGroup>>() {
					});
			return config;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public FilePath fileUpload(InputStream fis, String fileName, String basePath) {
		if(fileName.endsWith("png")) {
			FilePath fp = FilePath.uniqImgFilePath();
			fp.saveFile(fis, basePath);
			LOGGER.info("fileUpload [" + fileName + "] -> ok " + fp.path);
			return fp;
		} else {
			throw new RuntimeException("Incorrect file extension: " + fileName);
		}
	}

	private ParameterGroup getParameterGroup(MAgentTask task) {
		String moduleKey = task.getModule().getKey();
		return getParameterGroup(moduleKey);
	}

	private ParameterGroup getParameterGroup(MAlertReport report) {
		String moduleKey = report.getAlert().getSource().getKey();
		return getParameterGroup(moduleKey);
	}

	private ParameterGroup getParameterGroup(String moduleKey) {
		//@TODO move ro common service to use in Remote probe config
		if(groupsConfig==null)
		{
			groupsConfig = getParametersGroupConfig();
		}
		for (String key : groupsConfig.keySet()) {
			if (moduleKey != null && moduleKey.contains(key)) {
				return groupsConfig.get(key);
			}
		}
		/**
		 * @TODO Log error , no param group defined for current module
		 */
		return ParameterGroup.DATA;
	}

	private Map<String,QoSTask> getSessionTaskConfiguration()
	{
		Map<String,QoSTask> config= (Map<String,QoSTask>) sessionContextBean.getContextAttribute(ChannelViewSessionContext.TASKS_CONFIGURATION_SESSION_KEY);
		if(config==null)
		{
			config=new HashMap<>();
			Probe[] probeConfig=getProbesConfig();
			for (int i = 0; i < probeConfig.length; i++) {
				Probe probe = probeConfig[i];
				for (QoSTask task : probe.tasks) {
					config.put(task.entityKey,task);
				}
			}
			sessionContextBean.setContextAttribute(ChannelViewSessionContext.TASKS_CONFIGURATION_SESSION_KEY,config);
		}
		return config;
	}


	private QoSTask getSessionQoSTaskConfig(String taskKey)
	{
		if(taskKey!=null){
			Map<String,QoSTask>  config= getSessionTaskConfiguration();
			return config.get(taskKey);
		}
		return null;
	}

	private MUser getCurrentMUser()
	{
		Object muserObj=sessionContextBean.getContextAttribute(ChannelViewSessionContext.CURRENT_MUSER_SESSION_KEY);
		MUser muser=null;
		if(muserObj==null || !(muserObj instanceof MUser))
		{
			muser=userService.getCurrentUser();
			sessionContextBean.setContextAttribute(ChannelViewSessionContext.CURRENT_MUSER_SESSION_KEY,muser);
			return muser;
		}else{
			muser= (MUser) muserObj;
			UserDetails user=getCurrentUser();
			if(!StringUtils.equals(user.getUsername(),muser.getLogin()))
			{
				LOGGER.warn("User changed in session, update session data!");
				muser=userService.getCurrentUser();
				sessionContextBean.setContextAttribute(ChannelViewSessionContext.CURRENT_MUSER_SESSION_KEY,muser);
			}
			return muser;
		}
	}

	public UserDetails getCurrentUser() {
		final Authentication authentication = SecurityContextHolder
				.getContext().getAuthentication();
		if(authentication==null || authentication instanceof AnonymousAuthenticationToken)
		{
			return null;
		}
		UserDetails user = (UserDetails) authentication.getPrincipal();
		return user;
	}

	public Set<String> getPagesToNavigate(){
		Set<String> pages = new HashSet<>();

		MUser user = userService.getCurrentUser();

		for (MUser.Role role: user.getRoles()) {
			pages.addAll(role.getPermittedPageNames());
		}

		if(noNavigateToChannelViewPage) {
			pages.remove("CHANNEL_VIEW");
		}

		if(noNavigateToLiveVideoPage) {
			pages.remove("LIVE_VIDEO");
		}

		if(noNavigateToRecordedVideoPage) {
			pages.remove("RECORDED_VIDEO");
		}

		return pages;
	}


	@Override
	public void setChannelFavourite(Long id, boolean isFavourite) {
			Session session = sessionFactory.openSession();
			try {
				session.getTransaction().begin();
				MSetConfiguration entity = (MSetConfiguration) session.get(
						MSetConfiguration.class, id);
				entity.setFavourite(isFavourite);
				session.update(entity);
				session.getTransaction().commit();
				sessionContextBean.dropTaskConfigurationContext();
				LOGGER.info("Set favourite for channel [" +  id + "," + isFavourite + "] -> ok");
			} catch (Exception e) {
				LOGGER.error("Set favourite for channel [" + id + "," + isFavourite + "] -> error");
				session.getTransaction().rollback();
				throw e;
			} finally {
				session.close();
			}
	}

}
