/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.users;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.sencha.gxt.cell.core.client.PropertyDisplayCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.filters.BooleanFilter;
import com.sencha.gxt.widget.core.client.grid.filters.BooleanFilter.BooleanFilterMessages;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.UserManagerGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.DisabledGridViewConfig;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.RoleListValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ValueProviderWithPath;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.UserProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractRemoteDataGridView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.search.SearchField;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author meleshin.o
 * 
 */
public class UserManagerGridWidgetView
		extends
			AbstractRemoteDataGridView<MUser, UserManagerGridWidgetPresenter>
		implements
			UserManagerGridWidgetPresenter.MyView {

	private Image createButton;

	private Image disableButton;

	private Image enableButton;

	private SearchField searchField;

	private final UserProperties userProperties;

	@Inject
	public UserManagerGridWidgetView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
		userProperties = GWT.create(UserProperties.class);
	}

	@Override
	protected boolean addButtonsToToolbar() {
		searchField = new SearchField(
				new TriggerClickEvent.TriggerClickHandler() {
					@Override
					public void onTriggerClick(final TriggerClickEvent event) {
						doSearch(searchField.getText());
					}
				}, messages.search());
		searchField.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH);

		final CssFloatData layoutData = new CssFloatData();
		toolbar.add(searchField, layoutData);
		searchField.getElement().<XElement> cast()
				.setMargins(new Margins(5, 5, 5, 0));
		searchField.getElement().<XElement> cast().getStyle()
				.setFloat(Float.RIGHT);

		addCreateButton(layoutData);
		toolbar.add(createSeparator(), layoutData);
		addDisableButton(layoutData);
		addEnableButton(layoutData);

		return true;
	}

	private void addCreateButton(final CssFloatData layoutData) {
		final ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().openUserInformationDialog(null);
			}
		};
		createButton = createToolBarButton(appearanceFactory.resources()
				.newUserButton(), messages.createNewAccount(),
				messages.createNewAccount(), CommentMode.DISABLED, null);
		createButton.addClickHandler(handler);
		createButton.getElement().<XElement> cast()
				.setMargins(new Margins(8, 0, 0, 10));
		toolbar.add(createButton, layoutData);
	}

	private void addDisableButton(final CssFloatData layoutData) {
		final SelectedItemsActionHandler<MUser> handler = new SelectedItemsActionHandler<MUser>() {
			@Override
			public void onAction(final List<MUser> items, final String comment) {
				getUiHandlers().disableUsers(items);
			}
		};
		disableButton = createToolBarButton(appearanceFactory.resources()
				.disableUserButton(), messages.disableUserAccounts(),
				messages.disableUserAccountsMessage(), CommentMode.DISABLED,
				handler);
		disableButton.getElement().<XElement> cast()
				.setMargins(new Margins(8, 0, 0, 10));
		toolbar.add(disableButton, layoutData);
	}

	private void addEnableButton(final CssFloatData layoutData) {
		final SelectedItemsActionHandler<MUser> handler = new SelectedItemsActionHandler<MUser>() {
			@Override
			public void onAction(final List<MUser> items, final String comment) {
				getUiHandlers().enableUsers(items);
			}
		};
		enableButton = createToolBarButton(appearanceFactory.resources()
				.enableUserButton(), messages.enableUserAccounts(),
				messages.enableUserAccountsMessage(), CommentMode.DISABLED,
				handler);
		enableButton.getElement().<XElement> cast()
				.setMargins(new Margins(8, 0, 0, 10));
		toolbar.add(enableButton, layoutData);
	}

	@Override
	protected void applyDefaultConfiguration() {
		applyOrder(Order.asc(userProperties.disabled().getPath()), false);
		applyCriterionToFilters(loadConfig, getUiHandlers()
				.getFilteringCriterion());
	}

	private BooleanFilter<MUser> createDisabledFilter() {
		final BooleanFilter<MUser> filter = filterFactory
				.createBooleanFilter(userProperties.disabled());
		filter.setMessages(new BooleanFilterMessages() {

			@Override
			public String noText() {
				return messages.actionYes();
			}

			@Override
			public String yesText() {
				return messages.actionNo();
			}
		});
		return filter;
	}

	@Override
	protected List<Filter<MUser, ?>> createFilters() {
		final List<Filter<MUser, ?>> filters = new ArrayList<Filter<MUser, ?>>();

		filters.add(createDisabledFilter());
		return filters;
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridStandardAppearance();
	}

	@Override
	protected ListStore<MUser> createStore() {
		return new ListStore<MUser>(userProperties.key());
	}

	private void doSearch(final String searchText) {
		getUiHandlers().setLoadingCriterion(null);
		if (SimpleUtils.isNotNullAndNotEmpty(searchText)) {
			final CriterionQuery query = CriterionQueryFactory.getQuery();

			getUiHandlers().setLoadingCriterion(
					query.or(query.istringContains(userProperties.login()
							.getPath(), searchText), query.or(query
							.istringContains(userProperties.lastName()
									.getPath(), searchText), query.or(query
							.istringContains(userProperties.firstName()
									.getPath(), searchText), query
							.istringContains(userProperties.secondName()
									.getPath(), searchText)))));
		}
		loadFirstPage();
	}

	@Override
	protected List<ColumnConfig<MUser, ?>> getGridColumns() {
		final List<ColumnConfig<MUser, ?>> columns = new ArrayList<ColumnConfig<MUser, ?>>();

		final ColumnConfig<MUser, String> ldapColumn = new ColumnConfig<MUser, String>(
				new ValueProviderWithPath<MUser, String>(userProperties
						.ldapAuthenticated().getPath()) {
					@Override
					public String getValue(final MUser user) {
						return user.isLdapAuthenticated() ? "LDAP" : "LOCAL";
					}

				}, 50, messages.type());
		ldapColumn.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		ldapColumn.setFixed(true);
		ldapColumn.setRowHeader(false);
		ldapColumn.setMenuDisabled(true);

		final ColumnConfig<MUser, String> login = new ColumnConfig<MUser, String>(
				userProperties.login(), 20, messages.login());
		final ColumnConfig<MUser, String> lastName = new ColumnConfig<MUser, String>(
				userProperties.lastName(), 50, messages.lastName());
		final ColumnConfig<MUser, String> firstName = new ColumnConfig<MUser, String>(
				userProperties.firstName(), 50, messages.firstName());
		final ColumnConfig<MUser, String> secondName = new ColumnConfig<MUser, String>(
				userProperties.secondName(), 50, messages.middleName());
		final ColumnConfig<MUser, String> roles = new ColumnConfig<MUser, String>(
				new RoleListValueProvider(messages), 30, messages.roles());
		roles.setSortable(false);

		final ColumnConfig<MUser, Boolean> isActive = new ColumnConfig<MUser, Boolean>(
				userProperties.disabled(), 10, messages.userIsActive());
		isActive.setCell(new PropertyDisplayCell<Boolean>(
				new PropertyEditor<Boolean>() {

					@Override
					public Boolean parse(final CharSequence text)
							throws ParseException {
						return text.equals(messages.actionYes()) ? false : true;
					}

					@Override
					public String render(final Boolean status) {
						return status ? messages.actionNo() : messages
								.actionYes();
					}
				}));

		columns.add(ldapColumn);
		columns.add(login);
		columns.add(lastName);
		columns.add(firstName);
		columns.add(secondName);
		columns.add(roles);
		columns.add(isActive);

		return columns;
	}

	@Override
	protected String[] getGridStyles() {
		return new String[]{ClientConstants.QOS_GRID_STANDARD_STYLE};
	}

	@Override
	public UserProperties getUserProperties() {
		return userProperties;
	}

	@Override
	protected void initializeGrid() {
		super.initializeGrid();
		final GridView<MUser> gridView = grid.getView();

		grid.addCellClickHandler(new CellClickHandler() {

			@Override
			public void onCellClick(final CellClickEvent event) {
				final int colIndex = event.getCellIndex();
				final ColumnConfig<MUser, ?> column = grid.getColumnModel()
						.getColumn(colIndex);

				if (column != null
						&& userProperties.login().getPath()
								.equals(column.getPath())) {
					final MUser user = grid.getSelectionModel()
							.getSelectedItem();
					getUiHandlers().openUserInformationDialog(user);
				}
			}
		});
		gridView.setStripeRows(true);
		gridView.setViewConfig(new DisabledGridViewConfig<MUser>(
				appearanceFactory) {

			@Override
			public String getColStyle(final MUser model,
					final ValueProvider<? super MUser, ?> valueProvider,
					final int rowIndex, final int colIndex) {
				String style = null;
				if (valueProvider != null
						&& userProperties.login().getPath()
								.equals(valueProvider.getPath())) {
					style = appearanceFactory.resources().css()
							.gridNavigation();
				}
				return style;
			}
		});
	}

	@Override
	protected RpcProxy<FilterPagingLoadConfig, PagingLoadResult<MUser>> initializeLoaderProxy() {
		return new RpcProxy<FilterPagingLoadConfig, PagingLoadResult<MUser>>() {

			@Override
			public void load(final FilterPagingLoadConfig loadConfig,
					final AsyncCallback<PagingLoadResult<MUser>> callback) {
				getUiHandlers().setFilteringCriterion(
						convertFiltersToCriterion(loadConfig, null));
				final Criterion criterion = getUiHandlers()
						.getConfigurableCriterion();
				getUiHandlers().getUsersCount(
						criterion,
						new AutoNotifyingAsyncCallback<Long>(messages
								.usersCountLoadingFail(), true) {
							@Override
							protected void success(final Long result) {
								final int totalCount = result.intValue();
								getUiHandlers()
										.loadUsers(
												criterion,
												getCurrentOrder(),
												loadConfig.getOffset(),
												loadConfig.getLimit(),
												new AutoNotifyingAsyncCallback<List<MUser>>(
														messages.usersLoadingFail(),
														true) {

													@Override
													protected void success(
															final List<MUser> result) {
														final PagingLoadResultBean<MUser> pagingLoadResult = new PagingLoadResultBean<MUser>(
																result,
																totalCount,
																loadConfig
																		.getOffset());

														callback.onSuccess(pagingLoadResult);
													}
												});
							}
						});
			}
		};
	}
}
