package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SortChangeEvent;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyTreeGridRow;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyWrapper;
import com.tecomgroup.qos.gwt.client.model.policy.SourceRow;
import com.tecomgroup.qos.gwt.client.presenter.PolicyItemWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.AgentPoliciesGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyToolbarWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyToolbarWidgetPresenter.RemoveItemsHandler;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyToolbarWidgetPresenter.SearchHandler;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.CustomTreeGridView;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.PolicyActionListValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ValueProviderWithPath;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.PolicyProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomCheckBoxSelectionModel;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ToolTipPagingToolBar;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.agent.TreeGridFields;
import com.tecomgroup.qos.util.SimpleUtils.SimpleHandler;

/**
 * @author smyshlyaev.s
 */
public class AgentPoliciesGridWidgetView
		extends
			ViewWithUiHandlers<AgentPoliciesGridWidgetPresenter>
		implements
			AgentPoliciesGridWidgetPresenter.MyView {

	private static class PolicyTreeGridCheckboxCell
			extends
				AbstractCell<PolicyTreeGridRow> {

		private final Cell<PolicyTreeGridRow> defaultRenderer;

		public PolicyTreeGridCheckboxCell(
				final Cell<PolicyTreeGridRow> defaultRenderer) {
			this.defaultRenderer = defaultRenderer;
		}

		@Override
		public void render(final Context context,
				final PolicyTreeGridRow value, final SafeHtmlBuilder sb) {
			if (value instanceof PolicyWrapper) {
				defaultRenderer.render(context, value, sb);
			}
		}
	}

	private final QoSMessages messages;
	private final AppearanceFactory appearanceFactory;

	private VerticalLayoutContainer widget;
	private TreeGrid<PolicyTreeGridRow> grid;
	private TreeStore<PolicyTreeGridRow> store;
	private GridAppearance appearance;
	private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<PolicyTreeGridRow>> loader;
	private ToolTipPagingToolBar pagingToolbar;
	private final int gridPageSize = 50;
	protected final FilterPagingLoadConfig loadConfig;

	private final PolicyProperties policyProperties = GWT
			.create(PolicyProperties.class);

	private static final String CONDITION_LEVEL_SEPARATOR = " - ";
	private CheckBoxSelectionModel<PolicyTreeGridRow> selectionModel;
	private final PolicyToolbarWidgetPresenter<PolicyTreeGridRow> policyToolbarWidgetPresenter;
	private final PolicyItemWidgetPresenter policyItemWidgetPresenter;

	@Inject
	public AgentPoliciesGridWidgetView(
			final PolicyToolbarWidgetPresenter<PolicyTreeGridRow> policyToolbarWidgetPresenter,
			final PolicyItemWidgetPresenter policyItemWidgetPresenter) {
		super();
		appearanceFactory = AppearanceFactoryProvider.instance();
		messages = AppUtils.getMessages();
		this.policyToolbarWidgetPresenter = policyToolbarWidgetPresenter;
		this.policyItemWidgetPresenter = policyItemWidgetPresenter;
		loadConfig = new FilterPagingLoadConfigBean();
		loadConfig.setLimit(gridPageSize);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	private ColumnConfig<PolicyTreeGridRow, ?> createActionsColumn() {
		return new ColumnConfig<PolicyTreeGridRow, String>(
				new ValueProviderWithPath<PolicyTreeGridRow, String>("actions") {
					PolicyActionListValueProvider provider = new PolicyActionListValueProvider(messages);
					@Override
					public String getValue(final PolicyTreeGridRow row) {
						if (row instanceof PolicyWrapper) {
							final PolicyWrapper policyWrapper = (PolicyWrapper) row;
							return provider.getValue(policyWrapper);
						} else {
							return "";
						}
					}
				}, 70, messages.notifications());
	}

	private ColumnConfig<PolicyTreeGridRow, ?> createActionsTemplateColumn() {
		return new ColumnConfig<PolicyTreeGridRow, String>(
				new ValueProviderWithPath<PolicyTreeGridRow, String>(
						policyProperties.actionsTemplateName().getPath()) {
					@Override
					public String getValue(final PolicyTreeGridRow row) {
						return row.getActionsTemplateName();
					}
				}, 30, messages.notificationsTemplate());

	}

	private ColumnConfig<PolicyTreeGridRow, ?> createAgentColumn() {
		return new ColumnConfig<PolicyTreeGridRow, String>(
				new ValueProviderWithPath<PolicyTreeGridRow, String>("agent") {
					@Override
					public String getValue(final PolicyTreeGridRow row) {
						return row.getAgent();
					}
				}, 30, messages.agent());
	}

	private ColumnConfig<PolicyTreeGridRow, ?> createConditionsTemplateColumn() {
		return new ColumnConfig<PolicyTreeGridRow, String>(
				new ValueProviderWithPath<PolicyTreeGridRow, String>(
						policyProperties.conditionsTemplateName().getPath()) {
					@Override
					public String getValue(final PolicyTreeGridRow row) {
						return row.getConditionsTemplateName();
					}
				}, 30, messages.conditionsTemplate());

	}

	private ColumnConfig<PolicyTreeGridRow, ?> createCriticalCeaseColumn() {
		return new ColumnConfig<PolicyTreeGridRow, String>(
				new ValueProviderWithPath<PolicyTreeGridRow, String>(
						"criticalCease") {
					@Override
					public String getValue(final PolicyTreeGridRow row) {
						return row.getCriticalCease();
					}
				}, 30, messages.criticalLevel() + CONDITION_LEVEL_SEPARATOR
						+ messages.cease());
	}

	private ColumnConfig<PolicyTreeGridRow, ?> createCriticalRaiseColumn() {
		return new ColumnConfig<PolicyTreeGridRow, String>(
				new ValueProviderWithPath<PolicyTreeGridRow, String>(
						"criticalRaise") {
					@Override
					public String getValue(final PolicyTreeGridRow row) {
						return row.getCriticalRaise();
					}
				}, 30, messages.criticalLevel() + CONDITION_LEVEL_SEPARATOR
						+ messages.raise());
	}

	private ColumnConfig<PolicyTreeGridRow, ?> createNameColumn() {
		return new ColumnConfig<PolicyTreeGridRow, String>(
				new ValueProviderWithPath<PolicyTreeGridRow, String>("name") {
					@Override
					public String getValue(final PolicyTreeGridRow row) {
						return row.getName();
					}
				}, 50, messages.name());
	}

	private ColumnConfig<PolicyTreeGridRow, ?> createPaddingColumn() {
		final ColumnConfig<PolicyTreeGridRow, String> paddingColumn = new ColumnConfig<PolicyTreeGridRow, String>(
				new ValueProviderWithPath<PolicyTreeGridRow, String>(
						TreeGridFields.PADDING.toString()) {

					@Override
					public String getValue(final PolicyTreeGridRow row) {
						return null;
					}
				});
		paddingColumn.setWidth(15);
		paddingColumn.setFixed(true);
		paddingColumn.setSortable(false);
		paddingColumn.setMenuDisabled(true);
		paddingColumn.setResizable(false);

		return paddingColumn;
	}

	private ColumnConfig<PolicyTreeGridRow, ?> createParameterColumn() {
		return new ColumnConfig<PolicyTreeGridRow, String>(
				new ValueProviderWithPath<PolicyTreeGridRow, String>(
						"parameter") {
					@Override
					public String getValue(final PolicyTreeGridRow row) {
						return row.getParameterDisplayName();
					}
				}, 50, messages.parameter());
	}

	protected TreeStore<PolicyTreeGridRow> createStore() {
		return new TreeStore<PolicyTreeGridRow>(
				new ModelKeyProvider<PolicyTreeGridRow>() {
					@Override
					public String getKey(final PolicyTreeGridRow item) {
						return item.getKey();
					}
				});
	}

	private ColumnConfig<PolicyTreeGridRow, ?> createWarningCeaseColumn() {
		return new ColumnConfig<PolicyTreeGridRow, String>(
				new ValueProviderWithPath<PolicyTreeGridRow, String>(
						"warningCease") {
					@Override
					public String getValue(final PolicyTreeGridRow row) {
						return row.getWarningCease();
					}
				}, 30, messages.warningLevel() + CONDITION_LEVEL_SEPARATOR
						+ messages.cease());
	}

	private ColumnConfig<PolicyTreeGridRow, ?> createWarningRaiseColumn() {
		return new ColumnConfig<PolicyTreeGridRow, String>(
				new ValueProviderWithPath<PolicyTreeGridRow, String>(
						"warningRaise") {
					@Override
					public String getValue(final PolicyTreeGridRow row) {
						return row.getWarningRaise();
					}
				}, 30, messages.warningLevel() + CONDITION_LEVEL_SEPARATOR
						+ messages.raise());
	}

	private void doSearch(final String searchText) {
		getUiHandlers().setSearchText(searchText);
		loadFirstPage();
	}

	private List<ColumnConfig<PolicyTreeGridRow, ?>> getGridColumns() {
		final List<ColumnConfig<PolicyTreeGridRow, ?>> columns = new ArrayList<ColumnConfig<PolicyTreeGridRow, ?>>();

		final Cell<PolicyTreeGridRow> renderer = selectionModel.getColumn()
				.getCell();
		selectionModel.getColumn().setCell(
				new PolicyTreeGridCheckboxCell(renderer));
		ColumnConfig<PolicyTreeGridRow, ?> selectionColumn = selectionModel
				.getColumn();

		columns.add(createPaddingColumn());
		columns.add(selectionColumn);
		columns.add(createNameColumn());
		columns.add(createParameterColumn());
		columns.add(createActionsColumn());
		columns.add(createCriticalRaiseColumn());
		columns.add(createCriticalCeaseColumn());
		columns.add(createWarningRaiseColumn());
		columns.add(createWarningCeaseColumn());
		columns.add(createAgentColumn());
		columns.add(createActionsTemplateColumn());
		columns.add(createConditionsTemplateColumn());

		return columns;
	}

	@Override
	public void initialize() {
		widget = new VerticalLayoutContainer();

		widget.addStyleName(appearanceFactory.resources().css()
				.themeLighterBackgroundColor());
		widget.setBorders(false);
		widget.add(policyToolbarWidgetPresenter.asWidget());
		initializeGrid();
		widget.add(grid, new VerticalLayoutData(1, 1));
		initializeLoader();
		initializePagingToolbar();
		initializePolicyToolbar();
	}

	private void initializeGrid() {
		store = createStore();
		appearance = appearanceFactory.gridStandardAppearance();
		selectionModel = new CustomCheckBoxSelectionModel<PolicyTreeGridRow>(
				new IdentityValueProvider<PolicyTreeGridRow>(),
				appearanceFactory
						.<PolicyTreeGridRow> checkBoxColumnAppearance());
		final List<ColumnConfig<PolicyTreeGridRow, ?>> gridColumns = getGridColumns();
		grid = new TreeGrid<PolicyTreeGridRow>(store,
				new ColumnModel<PolicyTreeGridRow>(gridColumns),
				gridColumns.get(0), appearance);
		grid.setSelectionModel(selectionModel);
		grid.getStyle().setNodeOpenIcon(
				appearanceFactory.resources().transparent1x1());
		grid.getStyle().setNodeCloseIcon(
				appearanceFactory.resources().transparent1x1());
		grid.getStyle().setJointOpenIcon(
				appearanceFactory.resources().treeOpen());
		grid.getStyle().setJointCloseIcon(
				appearanceFactory.resources().treeClose());
		grid.addStyleName(ClientConstants.QOS_GRID_STANDARD_STYLE);
		grid.addStyleName(appearanceFactory.resources().css().textMainColor());
		grid.setView(new CustomTreeGridView<PolicyTreeGridRow>(
				appearanceFactory.columnHeaderAppearance()));
		grid.getView().setStripeRows(true);
		grid.getView().setAutoFill(true);
		// Sorting by columns is done locally, without this handler
		// grid would not expand after sorting
		grid.addSortChangeHandler(new SortChangeEvent.SortChangeHandler() {
			@Override
			public void onSortChange(final SortChangeEvent event) {
				Scheduler.get().scheduleDeferred(
						new Scheduler.ScheduledCommand() {
							@Override
							public void execute() {
								grid.expandAll();
							}
						});
			}
		});
	}

	private void initializeLoader() {
		loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<PolicyTreeGridRow>>(
				initializeLoaderProxy()) {
			@Override
			protected FilterPagingLoadConfig newLoadConfig() {
				return loadConfig;
			}
		};
	}

	private RpcProxy<FilterPagingLoadConfig, PagingLoadResult<PolicyTreeGridRow>> initializeLoaderProxy() {
		return new RpcProxy<FilterPagingLoadConfig, PagingLoadResult<PolicyTreeGridRow>>() {

			@Override
			public void load(
					final FilterPagingLoadConfig loadConfig,
					final AsyncCallback<PagingLoadResult<PolicyTreeGridRow>> callback) {
				getUiHandlers().actionLoadPolicies(loadConfig, callback);
			}
		};
	}

	private void initializePagingToolbar() {
		pagingToolbar = new ToolTipPagingToolBar(
				appearanceFactory.toolBarAppearance(),
				appearanceFactory.pagingToolBarAppearance(), gridPageSize);
		pagingToolbar.addStyleName(ClientConstants.QOS_PAGING_TOOLBAR);
		pagingToolbar.bind(loader);
		widget.add(pagingToolbar, new VerticalLayoutData(-1, 40));
	}

	private boolean initializePolicyToolbar() {
		policyToolbarWidgetPresenter.setGrid(grid);
		policyToolbarWidgetPresenter
				.setOpenActionsTemplatesEditorHandler(new SimpleHandler() {

					@Override
					public void handle() {
						getUiHandlers().actionOpenActionsTemplatesEditor();
					}
				});
		policyToolbarWidgetPresenter
				.setOpenConditionsTemplatesEditorHandler(new SimpleHandler() {

					@Override
					public void handle() {
						getUiHandlers().actionOpenConditionsTemplatesEditor();
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

		policyToolbarWidgetPresenter.setSearchHandler(new SearchHandler() {

			@Override
			public void onSearch(final String searchText) {
				doSearch(searchText);
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

		return true;
	}

	@Override
	public void load() {
		loader.load();
	}

	private void loadFirstPage() {
		loadConfig.setOffset(0);
		loader.load();
	}

	@Override
	public void removeItems(final Set<String> keySet) {
		final TreeStore<PolicyTreeGridRow> treeStore = grid.getTreeStore();
		for (final PolicyTreeGridRow root : treeStore.getRootItems()) {
			for (final PolicyTreeGridRow gridRow : treeStore
					.getAllChildren(root)) {
				if (gridRow instanceof PolicyWrapper) {
					final PolicyWrapper wrapper = (PolicyWrapper) gridRow;
					if (keySet.contains(wrapper.getPolicy().getKey())) {
						treeStore.remove(gridRow);
					}
				}
				if (treeStore.getAllChildren(root).size() == 0) {
					treeStore.remove(root);
				}
			}
		}
	}

	@Override
	public void resetPolicyToolbar() {
		policyToolbarWidgetPresenter.reset();
	}

	@Override
	public void updateGrid(final Map<String, List<PolicyWrapper>> policyWrappers) {
		grid.getTreeStore().clear();
		for (final Map.Entry<String, List<PolicyWrapper>> entry : policyWrappers
				.entrySet()) {
			if (entry.getValue() != null && entry.getValue().size() > 0) {
				final SourceRow root = new SourceRow(entry.getKey(), entry
						.getValue().get(0).getSourceDisplayName());
				grid.getTreeStore().add(root);
				for (final PolicyWrapper policyWrapper : entry.getValue()) {
					grid.getTreeStore().add(root, policyWrapper);
				}
			}
		}
		grid.getView().refresh(false);
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				grid.collapseAll();
			}
		});
	}
}
