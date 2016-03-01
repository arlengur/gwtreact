/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.data.shared.TreeStore;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;
import com.tecomgroup.qos.gwt.client.model.template.TemplateGroupRow;
import com.tecomgroup.qos.gwt.client.model.template.TemplateRow;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractLocalDataTreeGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author meleshin.o
 * 
 */
public class TemplatesGridWidgetPresenter
		extends
			AbstractLocalDataTreeGridWidgetPresenter<TreeGridRow, TemplatesGridWidgetPresenter.MyView> {

	public interface MyView
			extends
				AbstractLocalDataTreeGridWidgetPresenter.MyView<TreeGridRow, TemplatesGridWidgetPresenter> {

		TreeStore<TreeGridRow> getStore();

		void refreshGridView();

		void updateTemplates(List<? extends MUserAbstractTemplate> templates);
	}

	private Map<String, String> templateHrefMap;

	private final UserServiceAsync userService;

	private Map<TemplateType, String> templateLabels;

	private List<TemplateType> templateTypes;

	@Inject
	public TemplatesGridWidgetPresenter(final EventBus eventBus,
			final MyView view, final UserServiceAsync userService) {
		super(eventBus, view);
		this.userService = userService;
		getView().setUiHandlers(this);
	}

	public String getTemplateHref(final String templateClass) {
		return templateHrefMap.get(templateClass);
	}

	private void loadTemplates() {
		final Long currentUserId = AppUtils.getCurrentUser().getUser().getId();
		final int[] typesLoadedNumber = new int[]{0};
		for (final TemplateType type : templateTypes) {
			userService
					.getTemplates(
							type,
							currentUserId,
							new AutoNotifyingAsyncCallback<List<MUserAbstractTemplate>>(
									messages.templateLoadingFail(), true) {

								@Override
								protected void success(
										final List<MUserAbstractTemplate> templates) {
									final TreeStore<TreeGridRow> store = getView()
											.getStore();

									store.add(new TemplateGroupRow(
											templateLabels.get(type), type));
									getView().updateTemplates(templates);
									typesLoadedNumber[0] = typesLoadedNumber[0]++;
									if (typesLoadedNumber[0] == templateTypes
											.size()) {
										getView().refreshGridView();
									}
								}

							});
		}
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		getView().getStore().clear();
		loadTemplates();
	}

	@Override
	public void reload(final boolean force) {
		// Do nothing
	}

	public void removeTemplateAction(final TreeGridRow row) {
		if (row instanceof TemplateRow) {
			final TemplateRow templateRow = (TemplateRow) row;
			final MUserAbstractTemplate template = templateRow.getTemplate();
			final TemplateGroupRow group = (TemplateGroupRow) getView()
					.getStore().findModelWithKey(template.getClass().getName());
			final Long currentUserId = AppUtils.getCurrentUser().getUser()
					.getId();
			userService.removeTemplate(
					group.getType(),
					currentUserId,
					template.getName(),
					new AutoNotifyingAsyncCallback<Void>(messages
							.templateRemovingFail(), true) {

						@Override
						protected void success(final Void result) {
							getView().getStore().remove(row);
							AppUtils.showInfoMessage(messages
									.templateRemovingSuccess());
						}
					});

		}
	}

	public void setTemplateHrefs(final Map<String, String> templateHrefMap) {
		this.templateHrefMap = templateHrefMap;
	}

	public void setTemplateLabels(final Map<TemplateType, String> templateLabels) {
		this.templateLabels = templateLabels;
	}

	public void setTemplateTypes(final List<TemplateType> templateTypes) {
		this.templateTypes = templateTypes;
	}
}
