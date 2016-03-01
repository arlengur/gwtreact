/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.grid.GroupingView.GroupingData;
import com.sencha.gxt.widget.core.client.grid.GroupingView.GroupingViewAppearance;
import com.sencha.gxt.widget.core.client.grid.GroupingView.GroupingViewStyle;
import com.tecomgroup.qos.gwt.client.style.HasIcon;
import com.tecomgroup.qos.gwt.client.style.theme.dark.DarkResources;
import com.tecomgroup.qos.gwt.client.style.theme.dark.DarkResources.DarkStyle;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ButtonedGroupingViewSupport;

/**
 * @author ivlev.e
 * 
 */
public class DarkGroupingViewAppearance
		implements
			GroupingViewAppearance,
			ButtonedGroupingViewSupport {

	public interface DarkGroupHeaderTemplate<M> {
		SafeHtml renderGroupHeader(GroupingData<M> groupInfo);

		SafeHtml renderGroupHeader(GroupingData<M> groupInfo,
				DarkGroupingViewStyle style, SafeHtml expandIconHtml,
				SafeHtml removeIconHtml);

		SafeHtml rowIcon(DarkGroupingViewStyle style, SafeHtml rowIconHtml);
	}

	public interface DarkGroupingViewStyle extends GroupingViewStyle {

		String buttons();

		String expandIcon();

		String removeIcon();

		String rowIcon();

		String text();
	}

	public interface DefaultHeaderTemplate
			extends
				XTemplates,
				DarkGroupHeaderTemplate<Object> {

		@Override
		@XTemplate("{groupInfo.value}")
		public SafeHtml renderGroupHeader(GroupingData<Object> groupInfo);

		@Override
		@XTemplate("<div class=\"{style.text}\">{groupInfo.value}</div><div class=\"{style.buttons}\"><span class=\"{style.expandIcon}\">{expandIconHtml}</span><span class=\"{style.removeIcon}\">{removeIconHtml}</span></div>")
		public SafeHtml renderGroupHeader(GroupingData<Object> groupInfo,
				DarkGroupingViewStyle style, SafeHtml expandIconHtml,
				SafeHtml removeIconHtml);

		@Override
		@XTemplate("<div class=\"{style.rowIcon}\">{rowIconHtml}</div>")
		public SafeHtml rowIcon(DarkGroupingViewStyle style,
				SafeHtml rowIconHtml);
	}

	public interface GroupingViewResources extends ClientBundle {

		@Source("com/sencha/gxt/theme/base/client/grid/groupBy.gif")
		ImageResource groupBy();

		@Source({"GroupingView.css"})
		@Import(DarkStyle.class)
		DarkGroupingViewStyle style();
	}

	public interface GroupTemplate<M> {
		SafeHtml renderGroup(GroupingView.GroupingViewStyle style,
				SafeHtml groupHeader, SafeHtml rows, SafeHtml groupSummary);
	}

	private final GroupingViewResources resources;

	private final DarkGroupingViewStyle style;

	protected boolean expanded = true;

	private DarkGroupHeaderTemplate<?> headerTemplate = GWT
			.create(DefaultHeaderTemplate.class);

	private final SafeHtml collapsedStateButtonHtml;

	private final SafeHtml expandedStateButtonHtml;

	private final SafeHtml removeGroupButtonHtml;

	public DarkGroupingViewAppearance() {
		this(GWT.<GroupingViewResources> create(GroupingViewResources.class));
	}

	public DarkGroupingViewAppearance(final GroupingViewResources resources) {
		this.resources = resources;
		this.style = this.resources.style();

		expandedStateButtonHtml = AbstractImagePrototype.create(
				DarkResources.INSTANCE.gridCellCollapseButton()).getSafeHtml();
		collapsedStateButtonHtml = AbstractImagePrototype.create(
				DarkResources.INSTANCE.gridCellExpandButton()).getSafeHtml();
		removeGroupButtonHtml = AbstractImagePrototype.create(
				DarkResources.INSTANCE.gridCellRemoveButton()).getSafeHtml();

		StyleInjectorHelper.ensureInjected(style, true);
	}

	@Override
	public void deselectGroup(final XElement element) {
		final XElement head = findHead(element);
		if (head != null) {
			head.removeClassName(DarkResources.INSTANCE.css().selected());
		}
	}

	@Override
	public String findGroupName(final XElement element) {
		return element.child("." + style.text()).getInnerText();
	}

	@Override
	public XElement findHead(final XElement element) {
		return element.findParent("." + style.gridGroupHead(), 10);
	}

	@Override
	public XElement getGroup(final XElement head) {
		return head.getParentElement().cast();
	}

	@Override
	public ImageResource groupByIcon() {
		return resources.groupBy();
	}

	@Override
	public boolean isCollapseButtonPressed(final XElement element) {
		return element.findParent("." + style.expandIcon(), 2) != null;
	}

	@Override
	public boolean isCollapsed(final XElement group) {
		return group.hasClassName(style.gridGroupCollapsed());
	}

	@Override
	public boolean isRemoveButtonPressed(final XElement element) {
		return element.findParent("." + style.removeIcon(), 2) != null;
	}

	@Override
	public void onGroupExpand(final XElement group, final boolean expanded) {
		this.expanded = expanded;
		group.setClassName(style.gridGroupCollapsed(), !expanded);
		if (GXT.isIE7()) {
			group.getNextSiblingElement().getStyle()
					.setDisplay(expanded ? Display.BLOCK : Display.NONE);
		} else {
			group.getNextSiblingElement().getStyle()
					.setProperty("display", expanded ? "table-row" : "none");
		}
		final XElement expandImgParent = group.child("." + style.expandIcon());
		if (expandImgParent != null) {
			expandImgParent.removeChildren();
			if (expanded) {
				expandImgParent.createChild(AbstractImagePrototype.create(
						DarkResources.INSTANCE.gridCellCollapseButton())
						.getHTML());
			} else {
				expandImgParent.createChild(AbstractImagePrototype.create(
						DarkResources.INSTANCE.gridCellExpandButton())
						.getHTML());
			}
		}
	}
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public SafeHtml renderGroupHeader(final GroupingData<?> groupInfo) {
		final SafeHtml stateButton = expanded
				? expandedStateButtonHtml
				: collapsedStateButtonHtml;

		final SafeHtmlBuilder sb = new SafeHtmlBuilder();

		final int size = groupInfo.getItems().size();
		if (size > 0) {
			final Object model = groupInfo.getItems().get(0);
			if (model instanceof HasIcon) {
				final HasIcon icon = (HasIcon) model;
				if (icon.getIcon() != null) {
					final Element imageElement = icon.getIcon().getElement();
					if (icon.getDefaultImageUri() != null) {
						// FIXME Did not find a correct way to add Error Handler
						// to
						// the Image (default GWT behavior doesn't work because
						// of
						// our HTML generation in appearance code)
						imageElement.setAttribute("onerror", "this.src='"
								+ icon.getDefaultImageUri().asString() + "'");
					}
					sb.append(headerTemplate.rowIcon(style, SafeHtmlUtils
							.fromTrustedString(imageElement.getString())));
				}
			}
		}

		sb.append(headerTemplate.renderGroupHeader((GroupingData) groupInfo,
				style, stateButton, removeGroupButtonHtml));

		return sb.toSafeHtml();
	}

	@Override
	public void selectGroup(final XElement element) {
		final XElement head = findHead(element);
		if (head != null) {
			head.addClassName(DarkResources.INSTANCE.css().selected());
		}
	}

	public void setHeaderTemplate(
			final DarkGroupHeaderTemplate<?> headerTemplate) {
		this.headerTemplate = headerTemplate;
	}

	@Override
	public GroupingView.GroupingViewStyle style() {
		return style;
	}

}
