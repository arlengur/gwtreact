/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.PermissionScope;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyWrapper;
import com.tecomgroup.qos.gwt.client.presenter.PolicyItemWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.AbstractPoliciesGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyToolbarWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyToolbarWidgetPresenter.RemoveItemsHandler;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.PolicyActionListValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractRemoteDataGridView;
import com.tecomgroup.qos.util.SimpleUtils.SimpleHandler;

/**
 * 
 * @author abondin
 * 
 */
public abstract class AbstractPoliciesGridWidgetView
		extends
			AbstractRemoteDataGridView<PolicyWrapper, AbstractPoliciesGridWidgetPresenter>
		implements
			AbstractPoliciesGridWidgetPresenter.MyView {

	private static class PolicyActionsTemplateNameValueProvider
			implements
				ValueProvider<PolicyWrapper, String> {

		@Override
		public String getPath() {
			// real path to the same property of MPolicy to sort and filter by
			// it
			return "actionsTemplate.name";
		}

		@Override
		public String getValue(final PolicyWrapper policyWrapper) {
			return policyWrapper.getActionsTemplateName();
		}

		@Override
		public void setValue(final PolicyWrapper policyWrapper,
				final String notificationTemplateName) {
			// do nothing
		}
	}

	private class PolicyConditionsTemplateNameValueProvider
			implements
				ValueProvider<PolicyWrapper, String> {

		@Override
		public String getPath() {
			// real path to the same property of MPolicy to sort and filer by it
			return "conditionsTemplate.name";
		}

		@Override
		public String getValue(final PolicyWrapper policyWrapper) {
			return policyWrapper.getConditionsTemplateName();
		}

		@Override
		public void setValue(final PolicyWrapper policyWrapper,
				final String conditionTemplateName) {
			// do nothing
		}
	}

	public static interface PolicyWrapperProperties
			extends
				PropertyAccess<PolicyWrapper> {
		@Path("agent")
		ValueProvider<PolicyWrapper, String> agent();
		@Path("criticalCease")
		ValueProvider<PolicyWrapper, String> criticalCease();
		@Path("criticalRaise")
		ValueProvider<PolicyWrapper, String> criticalRaise();
		@Path("displayName")
		ValueProvider<PolicyWrapper, String> displayName();
		@Path("key")
		ModelKeyProvider<PolicyWrapper> key();
		@Path("parameterDisplayName")
		ValueProvider<PolicyWrapper, String> parameterDisplayName();
		@Path("sourceDisplayName")
		ValueProvider<PolicyWrapper, String> source();
		@Path("warningCease")
		ValueProvider<PolicyWrapper, String> warningCease();
		@Path("warningRaise")
		ValueProvider<PolicyWrapper, String> warningRaise();
	}

	public static Logger LOGGER = Logger
			.getLogger(AbstractPoliciesGridWidgetView.class.getName());

	private static final String CONDITION_LEVEL_SEPARATOR = " - ";

	private final PolicyWrapperProperties policyWrapperProperties = GWT
			.create(PolicyWrapperProperties.class);

	private final PolicyToolbarWidgetPresenter<PolicyWrapper> policyToolbarWidgetPresenter;

	private final PolicyActionsTemplateNameValueProvider actionsTemplateNameValueProvider = new PolicyActionsTemplateNameValueProvider();

	private final PolicyConditionsTemplateNameValueProvider conditionsTemplateNameValueProvider = new PolicyConditionsTemplateNameValueProvider();

	private final PolicyItemWidgetPresenter policyItemWidgetPresenter;

	@Inject
	public AbstractPoliciesGridWidgetView(
			final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory,
			final PolicyToolbarWidgetPresenter<PolicyWrapper> policyToolbarWidgetPresenter,
			final PolicyItemWidgetPresenter policyItemWidgetPresenter) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
		this.policyToolbarWidgetPresenter = policyToolbarWidgetPresenter;
		this.policyItemWidgetPresenter = policyItemWidgetPresenter;
	}

	@Override
	protected boolean addButtonsToToolbar() {

		toolbar = (CssFloatLayoutContainer) policyToolbarWidgetPresenter
				.asWidget();

		policyToolbarWidgetPresenter.setGrid(grid);
		policyToolbarWidgetPresenter
				.setOpenActionsTemplatesEditorHandler(new SimpleHandler() {

					@Override
					public void handle() {
						getUiHandlers()
								.actionOpenPolicyActionsTemplatesEditor();
					}
				});

		policyToolbarWidgetPresenter
				.setOpenConditionsTemplatesEditorHandler(new SimpleHandler() {

					@Override
					public void handle() {
						getUiHandlers()
								.actionOpenPolicyConditionsTemplatesEditor();
					}
				});

		policyToolbarWidgetPresenter.setReloadHandler(new SimpleHandler() {

			@Override
			public void handle() {
				getUiHandlers().reload(false);
			}
		});
		policyToolbarWidgetPresenter
				.setRemoveItemsHandler(new RemoveItemsHandler() {

					@Override
					public void onRemove(final Set<String> keys) {
						removeItems(keys);
					}
				});

		policyToolbarWidgetPresenter
				.setOpenPolicyEditorHandler(new SimpleHandler() {
					@Override
					public void handle() {
						policyItemWidgetPresenter.setCreateMode();
						getUiHandlers().addToPopupSlot(
								policyItemWidgetPresenter, true);
					}
				});
		policyToolbarWidgetPresenter
				.setSearchHandler(new PolicyToolbarWidgetPresenter.SearchHandler() {

                    @Override
                    public void onSearch(final String searchText) {
                        doSearch(searchText);
                    }
                });

		return true;
	}

	@Override
	protected void applyDefaultConfiguration() {
		applyOrder(Order.desc(policyWrapperProperties.displayName().getPath()),
				false);
	}

	@Override
	protected List<Filter<PolicyWrapper, ?>> createFilters() {
		final List<Filter<PolicyWrapper, ?>> filters = new ArrayList<Filter<PolicyWrapper, ?>>();

		filters.add(filterFactory.createStringFilter(policyWrapperProperties
				.displayName()));
		filters.add(filterFactory
				.createStringFilter(actionsTemplateNameValueProvider));
		filters.add(filterFactory
				.createStringFilter(conditionsTemplateNameValueProvider));

		return filters;
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridStandardAppearance();
	}

	@Override
	protected ListStore<PolicyWrapper> createStore() {
		return new ListStore<PolicyWrapper>(policyWrapperProperties.key());
	}

	private void doSearch(final String searchText) {
		getUiHandlers().setSearchText(searchText);
		loadFirstPage();
	}

	@Override
	protected List<ColumnConfig<PolicyWrapper, ?>> getGridColumns() {
		final List<ColumnConfig<PolicyWrapper, ?>> columns = new ArrayList<ColumnConfig<PolicyWrapper, ?>>();
		final ColumnConfig<PolicyWrapper, String> source = new ColumnConfig<PolicyWrapper, String>(
				policyWrapperProperties.source(), 50, messages.source());

		final ColumnConfig<PolicyWrapper, String> actions = new ColumnConfig<PolicyWrapper, String>(
				new PolicyActionListValueProvider(messages), 70, messages.notifications());
		actions.setSortable(false);

		final ColumnConfig<PolicyWrapper, String> displayName = new ColumnConfig<PolicyWrapper, String>(
				policyWrapperProperties.displayName(), 50, messages.name());

		final ColumnConfig<PolicyWrapper, String> agent = new ColumnConfig<PolicyWrapper, String>(
				policyWrapperProperties.agent(), 50, messages.agent());

		final ColumnConfig<PolicyWrapper, String> parameter = new ColumnConfig<PolicyWrapper, String>(
				policyWrapperProperties.parameterDisplayName(), 50,
				messages.parameter());
		final ColumnConfig<PolicyWrapper, String> criticalRaise = new ColumnConfig<PolicyWrapper, String>(
				policyWrapperProperties.criticalRaise(), 30,
				messages.criticalLevel() + CONDITION_LEVEL_SEPARATOR
						+ messages.raise());
		final ColumnConfig<PolicyWrapper, String> criticalCease = new ColumnConfig<PolicyWrapper, String>(
				policyWrapperProperties.criticalCease(), 30,
				messages.criticalLevel() + CONDITION_LEVEL_SEPARATOR
						+ messages.cease());
		final ColumnConfig<PolicyWrapper, String> warningRaise = new ColumnConfig<PolicyWrapper, String>(
				policyWrapperProperties.warningRaise(), 30,
				messages.warningLevel() + CONDITION_LEVEL_SEPARATOR
						+ messages.raise());
		final ColumnConfig<PolicyWrapper, String> warningCease = new ColumnConfig<PolicyWrapper, String>(
				policyWrapperProperties.warningCease(), 30,
				messages.warningLevel() + CONDITION_LEVEL_SEPARATOR
						+ messages.cease());
		final ColumnConfig<PolicyWrapper, String> actionsTemplate = new ColumnConfig<PolicyWrapper, String>(
				actionsTemplateNameValueProvider, 30,
				messages.notificationsTemplate());

		final ColumnConfig<PolicyWrapper, String> conditionsTemplate = new ColumnConfig<PolicyWrapper, String>(
				conditionsTemplateNameValueProvider, 30,
				messages.conditionsTemplate());

		// FIXME
		source.setSortable(false);
		parameter.setSortable(false);
		criticalCease.setSortable(false);
		criticalRaise.setSortable(false);
		warningCease.setSortable(false);
		warningRaise.setSortable(false);
		agent.setSortable(false);
		actionsTemplate.setSortable(false);
		conditionsTemplate.setSortable(false);

		columns.add(source);
		columns.add(displayName);
		columns.add(parameter);
		columns.add(actions);
		columns.add(criticalRaise);
		columns.add(criticalCease);
		columns.add(warningRaise);
		columns.add(warningCease);
		columns.add(agent);
		columns.add(actionsTemplate);
		columns.add(conditionsTemplate);
		return columns;
	}

	@Override
	protected String[] getGridStyles() {
		return new String[]{ClientConstants.QOS_GRID_STANDARD_STYLE};
	}

	@Override
	protected void initializeGrid() {
		super.initializeGrid();
		grid.getView().setStripeRows(true);
		if (AppUtils.isPermitted(PermissionScope.POLICIES_ADVANCED)) {
			grid.addCellClickHandler(new CellClickHandler() {
				@Override
				public void onCellClick(final CellClickEvent event) {
					final int colIndex = event.getCellIndex();
					final ColumnConfig<PolicyWrapper, ?> column = grid
							.getColumnModel().getColumn(colIndex);
					if (column != null
							&& column.getPath().equals(
									policyWrapperProperties.displayName()
											.getPath())) {
						final PolicyWrapper wrapper = grid.getSelectionModel()
								.getSelectedItem();
						getUiHandlers()
								.openPolicyEditorDialog(wrapper.getKey());
					}
				}
			});
			grid.getView().setViewConfig(new GridViewConfig<PolicyWrapper>() {
				@Override
				public String getColStyle(
						final PolicyWrapper model,
						final ValueProvider<? super PolicyWrapper, ?> valueProvider,
						final int rowIndex, final int colIndex) {
					if (valueProvider.getPath().equals(
							policyWrapperProperties.displayName().getPath())) {
						return appearanceFactory.resources().css()
								.gridNavigation();
					}
					return null;
				}
				@Override
				public String getRowStyle(final PolicyWrapper model,
						final int rowIndex) {
					return null;
				}
			});
		}
	}

	@Override
	protected abstract RpcProxy<FilterPagingLoadConfig, PagingLoadResult<PolicyWrapper>> initializeLoaderProxy();

	@Override
	public void resetPolicyToolbar() {
		policyToolbarWidgetPresenter.reset();
    }
}
