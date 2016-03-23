/*
 * Copyright (C) 2016 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.i18n.client.*;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.probestatus.MExportVideoEvent;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;
import com.tecomgroup.qos.gwt.client.model.events.EventGroupRow;
import com.tecomgroup.qos.gwt.client.model.events.EventRow;
import com.tecomgroup.qos.gwt.client.presenter.ProbeEventsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.LabelUtils;
import com.tecomgroup.qos.gwt.client.utils.StyleUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.TreeGridRowModelKeyProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ValueProviderWithPath;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractLocalDataTreeGridView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.Anchor;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.IconedActionCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.agent.TreeGridFields;
import org.goda.time.*;
import org.goda.time.format.DateTimeFormatter;

import java.util.*;

/**
 * @author meleshin.o
 * 
 * 
 */
public class ProbeEventsGridWidgetView
		extends
			AbstractLocalDataTreeGridView<TreeGridRow, ProbeEventsGridWidgetPresenter>
		implements
			ProbeEventsGridWidgetPresenter.MyView {

	private FramedPanel framedPanel;

	private BorderLayoutContainer outerContainer;

	private final DialogFactory dialogFactory;

	private Map<MProbeEvent.STATUS, ImageResource> statusToResourseMap = new HashMap<MProbeEvent.STATUS, ImageResource>();

	private Map<MExportVideoEvent.DESCRIPTION, String> descriptionToMessageMap = new HashMap<MExportVideoEvent.DESCRIPTION, String>();

	private Map<String, String> fieldToMessageMap = new HashMap<String, String>();

	private Map<MProbeEvent.STATUS, String> statusToMessageMap = new HashMap<MProbeEvent.STATUS, String>();

	private static String CREATED_TIMESTAMP = "CREATED_TIMESTAMP";
	@Inject
	public ProbeEventsGridWidgetView(final DialogFactory dialogFactory) {
		super();
		this.dialogFactory = dialogFactory;
	}

	@Override
	protected void initializeWidget() {
		super.initializeWidget();
		outerContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());

		framedPanel = new FramedPanel(
				appearanceFactory.lightFramedPanelAppearance());
		StyleUtils.configureNoHeaders(framedPanel);
		outerContainer.add(framedPanel);
		framedPanel.add(grid);
		initStatusToResourceMap();
		initFieldToMessageMap();
		initStatusToMessageMap();
		initDescriptionMap();
		grid.expandAll();
	}

	private void initStatusToResourceMap() {
		statusToResourseMap.put(MProbeEvent.STATUS.QUEUED, appearanceFactory.resources().videoExportQueued());
		statusToResourseMap.put(MProbeEvent.STATUS.IN_PROGRESS, appearanceFactory.resources().videoExportInProgress());
		statusToResourseMap.put(MProbeEvent.STATUS.OK, appearanceFactory.resources().videoExportOk());
		statusToResourseMap.put(MProbeEvent.STATUS.TIMEOUT, appearanceFactory.resources().videoExportNok());
		statusToResourseMap.put(MProbeEvent.STATUS.FAILED, appearanceFactory.resources().videoExportNok());
	}

	private void initFieldToMessageMap() {
		fieldToMessageMap.put(MExportVideoEvent.FIELD.START_DATE.name(), messages.startDateTime());
		fieldToMessageMap.put(MExportVideoEvent.FIELD.DURATION.name(), messages.duration());
		fieldToMessageMap.put(MExportVideoEvent.FIELD.QUALITY.name(), messages.quality());
		fieldToMessageMap.put(MExportVideoEvent.FIELD.COMMENT.name(), messages.comment());
		fieldToMessageMap.put(MExportVideoEvent.FIELD.DESTINATION.name(), messages.destination());

		fieldToMessageMap.put(MProbeEvent.FIELD.AGENT_DISPLAY_NAME.name(), messages.agent());
		fieldToMessageMap.put(CREATED_TIMESTAMP, messages.creationDateTime());
	}

	private void initStatusToMessageMap() {
		statusToMessageMap.put(MProbeEvent.STATUS.QUEUED, messages.requestQueued());
		statusToMessageMap.put(MProbeEvent.STATUS.IN_PROGRESS, messages.inProgressState());
		statusToMessageMap.put(MProbeEvent.STATUS.OK, messages.actionSuccess());
		statusToMessageMap.put(MProbeEvent.STATUS.FAILED, messages.actionFailed());
		statusToMessageMap.put(MProbeEvent.STATUS.TIMEOUT, messages.failedState());
	}

	private void initDescriptionMap() {
		descriptionToMessageMap.put(MExportVideoEvent.DESCRIPTION.TRANSCODE_WAITING, messages.descriptionTranscodeWaiting());
		descriptionToMessageMap.put(MExportVideoEvent.DESCRIPTION.TRANSCODING, messages.descriptionTranscoding());
		descriptionToMessageMap.put(MExportVideoEvent.DESCRIPTION.UPLOAD_WAITING, messages.descriptionUploadWaiting());
		descriptionToMessageMap.put(MExportVideoEvent.DESCRIPTION.UPLOADING, messages.descriptionUploading());
		descriptionToMessageMap.put(MExportVideoEvent.DESCRIPTION.MISSED_RECORDS, messages.descriptionMissedRecords());
		descriptionToMessageMap.put(MExportVideoEvent.DESCRIPTION.ERROR_NO_FREE_DISK_SPACE, messages.descriptionNoFreeDiscSpace());
		descriptionToMessageMap.put(MExportVideoEvent.DESCRIPTION.ERROR_TRANSCODING_FAULT, messages.descriptionTranscodingFault());
		descriptionToMessageMap.put(MExportVideoEvent.DESCRIPTION.ERROR_FTP_FAULT, messages.descriptionFtpFault());
		descriptionToMessageMap.put(MExportVideoEvent.DESCRIPTION.TIMEOUT, messages.descriptionExpired());
		descriptionToMessageMap.put(MExportVideoEvent.DESCRIPTION.ERROR_MISSING_FILES, messages.descriptionMissingFiles());
		descriptionToMessageMap.put(MExportVideoEvent.DESCRIPTION.ERROR_UPLOAD_EXPIRED, messages.descriptionUploadTimeoutExpired());
		descriptionToMessageMap.put(MExportVideoEvent.DESCRIPTION.ERROR_INTERNAL, messages.descriptionInternalError());
	}

	private static final Map<String, DateTimeFormatter> LOCALE_DATE_TIME_FORMATS = new HashMap<String, DateTimeFormatter>() {
		{
			put("def",org.goda.time.format.DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss"));
			put("ru", org.goda.time.format.DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss"));
			put("en", org.goda.time.format.DateTimeFormat.forPattern("MM/dd/yyyy h:mm:ss a"));
		}
	};

	public static String formatDateTime(String date) {
		DateTime dt = LOCALE_DATE_TIME_FORMATS.get("def").parseDateTime(date);
		return LOCALE_DATE_TIME_FORMATS.get(LocaleInfo.getCurrentLocale().getLocaleName()).print(dt);
	}

	@Override
	public Widget asWidget() {
		return outerContainer;
	}

	private ValueProvider<TreeGridRow, TreeGridRow> createActionValueProvider(
			final String path) {
		return new ValueProviderWithPath<TreeGridRow, TreeGridRow>(path) {

			@Override
			public TreeGridRow getValue(final TreeGridRow treeGridRow) {
				if (treeGridRow instanceof EventGroupRow) {
					return null;
				}
				return treeGridRow;
			}
		};
	}

	private ColumnConfig<TreeGridRow, TreeGridRow> createStatusColumn(
			final String columnDisplayName, int width) {
		final ColumnConfig<TreeGridRow, TreeGridRow> column = new ColumnConfig<TreeGridRow, TreeGridRow>(
				createActionValueProvider(columnDisplayName), width);
		column.setCell(new AbstractCell<TreeGridRow>() {

			@Override
			public void render(final Context context, final TreeGridRow value,
							   final SafeHtmlBuilder sb) {
				if (value instanceof EventRow) {
					MProbeEvent event = ((EventRow) value).getEvent();

					if(event instanceof MExportVideoEvent) {
						ImageResource icon = statusToResourseMap.get(event.getStatus());
						Image image = new Image(icon);
						image.setAltText(event.getStatus().name());
						StringBuilder tooltip = new StringBuilder(statusToMessageMap.get(event.getStatus()));
						MExportVideoEvent.DESCRIPTION description = ((MExportVideoEvent) event).getDescription();
						if(description != null) {
							tooltip.append(", ");
							tooltip.append(descriptionToMessageMap.get(description));
						}
						image.getElement().setTitle(tooltip.toString());
						sb.append(SafeHtmlUtils.fromTrustedString(image.getElement().getString()));
					}
				} else {
					createBoldLabel(sb, columnDisplayName);
				}
			}
		});
		return column;
	}

	private ColumnConfig<TreeGridRow, TreeGridRow> createDeletionActionColumn(
			final String columnDisplayName, final ImageResource icon) {

		final IconedActionCell<TreeGridRow> deletionActionCell = new IconedActionCell<TreeGridRow>(
				appearanceFactory.<TreeGridRow> iconedActionCellAppearance(
						icon, columnDisplayName), columnDisplayName,
				new ActionCell.Delegate<TreeGridRow>() {

					@Override
					public void execute(final TreeGridRow row) {
						dialogFactory.createConfirmationDialog(
								new ConfirmationDialog.ConfirmationHandler() {

									@Override
									public void onCancel() {

									}

									@Override
									public void onConfirm(
											final String comment) {
										getUiHandlers().removeEvents(row);
									}
								}, messages.deleteEvent(),
								messages.deleteEventConfirmation(),
								ConfirmationDialog.CommentMode.DISABLED).show();
					}
				});
		final ColumnConfig<TreeGridRow, TreeGridRow> deletionActionColumn = new ColumnConfig<TreeGridRow, TreeGridRow>(
				createActionValueProvider(columnDisplayName), 5);
		deletionActionColumn.setCell(deletionActionCell);

		return deletionActionColumn;
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridTemplatesAppearance();
	}

	private void createBoldLabel(final SafeHtmlBuilder sb, String text) {
		final Label label = new Label(text);
		label.getElement().getStyle().setProperty("fontWeight", "bold");

		label.getElement().setTitle(text);
		sb.append(SafeHtmlUtils.fromTrustedString(label.getElement().getString()));
	}

	private static interface RenderDelegate {
		public void render(final TreeGridRow value, final SafeHtmlBuilder sb);
	}

	private class DefaultRenderDelegate implements RenderDelegate {
		private String fieldName;

		public DefaultRenderDelegate(String fieldName) {
			this.fieldName = fieldName;
		}

		@Override
		public void render(TreeGridRow value, SafeHtmlBuilder sb) {
			if (value instanceof EventRow) {
				MProbeEvent event = ((EventRow) value).getEvent();

				if(event instanceof MExportVideoEvent) {
					final String field = ((EventRow) value).getEvent().getPropertyValue(fieldName);
					final String labelHtml = new Label(field).getElement().getString();
					sb.append(SafeHtmlUtils.fromTrustedString(labelHtml));
				}
			} else {
				createBoldLabel(sb, fieldToMessageMap.get(fieldName));
			}
		}
	}

	private ColumnConfig<TreeGridRow, TreeGridRow> createDefaultColumn(int width) {
		return 	new ColumnConfig<TreeGridRow, TreeGridRow>(
				new ValueProvider<TreeGridRow, TreeGridRow>() {

					@Override
					public String getPath() {
						return TreeGridFields.NAME.toString();
					}

					@Override
					public TreeGridRow getValue(final TreeGridRow treeGridRow) {
						return treeGridRow;
					}

					@Override
					public void setValue(final TreeGridRow treeGridRow,
										 final TreeGridRow value) {

					}

				}, width);
	}

	private ColumnConfig<TreeGridRow, TreeGridRow> createColumnDelegated(int width, final RenderDelegate delegate) {
		final ColumnConfig<TreeGridRow, TreeGridRow> column = createDefaultColumn(width);
		column.setCell(new AbstractCell<TreeGridRow>() {

			@Override
			public void render(final Context context, final TreeGridRow value,
							   final SafeHtmlBuilder sb) {
				delegate.render(value, sb);
			}
		});
		return column;
	}

	@Override
	protected TreeStore<TreeGridRow> createStore() {
		return new TreeStore<TreeGridRow>(new TreeGridRowModelKeyProvider());
	}

	public String parseDestinationEnum(MExportVideoEvent.DESTINATION value) {
		if(MExportVideoEvent.DESTINATION.LOCAL == value) {
			return messages.linkLocal();
		} else if (MExportVideoEvent.DESTINATION.FTP == value) {
			return messages.linkFTP();
		} else {
			return messages.linkFTPonSchedule();
		}
	}

	@Override
	protected List<ColumnConfig<TreeGridRow, ?>> getGridColumns() {
		final List<ColumnConfig<TreeGridRow, ?>> columns = new ArrayList<ColumnConfig<TreeGridRow, ?>>();
		final ColumnConfig<TreeGridRow, TreeGridRow> deleteColumn = createDeletionActionColumn(
				messages.delete(), appearanceFactory.resources().deleteButton());
		final ColumnConfig<TreeGridRow, TreeGridRow> statusColumn = createStatusColumn(
				messages.status(), 15);

		columns.add(createPaddingColumn());

		columns.add(createColumnDelegated(100, new RenderDelegate() {
			@Override
			public void render(TreeGridRow value, SafeHtmlBuilder sb) {
				if (value instanceof EventRow) {
					sb.append(SafeHtmlUtils.fromTrustedString(getEventRowLinkHtml((EventRow) value)));
				} else {
					createBoldLabel(sb, value.getName());
				}
			}
		}));

		columns.add(createColumnDelegated(50, new DefaultRenderDelegate(MProbeEvent.FIELD.AGENT_DISPLAY_NAME.name())));

		columns.add(createColumnDelegated(35, new RenderDelegate() {
			@Override
			public void render(TreeGridRow value, SafeHtmlBuilder sb) {
				if (value instanceof EventRow) {
					MProbeEvent event = ((EventRow) value).getEvent();

					if(event instanceof MExportVideoEvent) {
						final String field = ((EventRow) value).getEvent().getPropertyValue(MExportVideoEvent.FIELD.START_DATE.name());

						final String labelHtml = new Label(formatDateTime(field)).getElement().getString();
						sb.append(SafeHtmlUtils.fromTrustedString(labelHtml));
					}
				} else {
					createBoldLabel(sb, fieldToMessageMap.get(MExportVideoEvent.FIELD.START_DATE.name()));
				}
			}
		}));

		columns.add(createColumnDelegated(30, new DefaultRenderDelegate(MExportVideoEvent.FIELD.DURATION.name())));

		columns.add(createColumnDelegated(30, new DefaultRenderDelegate(MExportVideoEvent.FIELD.QUALITY.name())));

		columns.add(createColumnDelegated(50, new RenderDelegate() {
			@Override
			public void render(TreeGridRow value, SafeHtmlBuilder sb) {
				if (value instanceof EventRow) {
					MProbeEvent event = ((EventRow) value).getEvent();

					if(event instanceof MExportVideoEvent) {
						final String field = ((EventRow) value).getEvent().getPropertyValue(MExportVideoEvent.FIELD.DESTINATION.name());
						final String fieldLocalized = parseDestinationEnum(MExportVideoEvent.DESTINATION.valueOf(field));
						final String labelHtml = new Label(fieldLocalized).getElement().getString();
						sb.append(SafeHtmlUtils.fromTrustedString(labelHtml));
					}
				} else {
					createBoldLabel(sb, fieldToMessageMap.get(MExportVideoEvent.FIELD.DESTINATION.name()));
				}
			}
		}));

		columns.add(createColumnDelegated(35, new RenderDelegate() {
			@Override
			public void render(TreeGridRow value, SafeHtmlBuilder sb) {
				if (value instanceof EventRow) {
					MProbeEvent event = ((EventRow) value).getEvent();

					if(event instanceof MExportVideoEvent) {
						MProbeEvent probeEvent = ((EventRow) value).getEvent();
						DateTime createDate = new DateTime(probeEvent.getCreatedTimestamp().getTime());
						String field = LOCALE_DATE_TIME_FORMATS
								.get(LocaleInfo
										.getCurrentLocale()
										.getLocaleName())
								.print(createDate);
						final String labelHtml = new Label(field).getElement().getString();
						sb.append(SafeHtmlUtils.fromTrustedString(labelHtml));
					}
				} else {
					createBoldLabel(sb, fieldToMessageMap.get(CREATED_TIMESTAMP));
				}
			}
		}));

		columns.add(createColumnDelegated(90, new DefaultRenderDelegate(MExportVideoEvent.FIELD.COMMENT.name())));
		columns.add(statusColumn);
		columns.add(deleteColumn);
		return columns;
	}

	@Override
	protected String[] getGridStyles() {
		return new String[]{ClientConstants.QOS_GRID_STANDARD_STYLE,
				ClientConstants.QOS_GRID_TEMPLATES_STYLE,
				appearanceFactory.resources().css().textMainColor()};
	}

	@Override
	public TreeStore<TreeGridRow> getStore() {
		return grid.getTreeStore();
	}

	private String getEventRowLinkHtml(final EventRow eventRow) {
		final MProbeEvent event = eventRow.getEvent();
		if(event instanceof MExportVideoEvent) {
			MExportVideoEvent exportEvent = (MExportVideoEvent) event;
			String url = exportEvent.getPropertyValue(MExportVideoEvent.FIELD.URL.name());
			String linkText = exportEvent.getPropertyValue(MExportVideoEvent.FIELD.TASK_DISPLAY_NAME.name());
			if (url != null && MProbeEvent.STATUS.OK == event.getStatus()) {
				return new Anchor(url, linkText).getElement().getString();
			} else {
				return new Label(linkText).getElement().getString();
			}
		} else {
			return new Label("unavailable").getElement().getString();
		}
	}

	@Override
	protected int getTreeColumnIndex() {
		return 1;
	}

	@Override
	public void refreshGridView() {
		grid.expandAll();
		grid.getView().refresh(false);
	}

	@Override
	public void updateEvents(
			final List<? extends MProbeEvent> events) {
		final TreeStore<TreeGridRow> store = grid.getTreeStore();
		EventGroupRow group;

		for (final MProbeEvent event : events) {
			group = (EventGroupRow) store.findModelWithKey(event
					.getClass().getName());

			if (group != null) {
				store.add(group, new EventRow(event));
			}
		}

		store.addSortInfo(
				new Store.StoreSortInfo<TreeGridRow>(new Comparator<TreeGridRow>() {
					@Override
					public int compare(final TreeGridRow o1, final TreeGridRow o2) {
						if(o1 instanceof EventRow && o2 instanceof EventRow) {
							MProbeEvent event1 = ((EventRow) o1).getEvent();
							MProbeEvent event2 = ((EventRow) o2).getEvent();
							return event1 == null ? -1 : event1.getTimestamp().compareTo(event2.getTimestamp());
						}
						return -1;
					}
				}, SortDir.ASC));
	}
}
