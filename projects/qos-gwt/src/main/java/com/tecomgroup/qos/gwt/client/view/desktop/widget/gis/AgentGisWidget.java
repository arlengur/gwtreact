/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.gis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.OverviewMap;
import org.gwtopenmaps.openlayers.client.control.ScaleLine;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureSelectedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.OSMOptions;
import org.gwtopenmaps.openlayers.client.layer.Vector;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceRequest.Builder;
import com.tecomgroup.qos.domain.GISPosition;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.gwt.client.GeneralIcons;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ColorConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AgentSelectionListener;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * Компонент для выбора БК на карте и отображение статуса агентов
 *
 * @author abondin
 *
 */
public class AgentGisWidget
		implements
			IsWidget,
			AgentSelectionListener,
			MapInfo {

	/**
	 * Navigates to probe status page.
	 *
	 * @param agentKey
	 *            the key of the agent to show status page.
	 */
	public static void navigateToProbeStatusPage(final String agentKey) {
		// getEventBus().fireEvent(new AgentSelectedEvent(agent));
		final Builder request = new Builder()
				.nameToken(QoSNameTokens.agentStatus);
		request.with(RequestParams.agentName, agentKey);
		AppUtils.getPlaceManager().revealPlace(request.build());
	}

	private static final Logger LOGGER = Logger.getLogger(AgentGisWidget.class
			.getName());

	private static final String CURSOR_TYPE = "pointer";

	private static final String TILE_SERVERS_PROPERTY_NAME = "client.gis.tile.servers";

	private static final String TILE_SERVERS_PROPERTY_DEFAULT_VALUE = "http://b.tile.openstreetmap.org/${z}/${x}/${y}.png";

	private static final String BASE_LAYER_PROPERTY = "baseLbl";

	private static final String OVERLAYS_PROPERTY = "dataLbl";

	private static final String AGENT_GROUP_PREFIX = "Group_";

	/**
	 * Default value of {@link #parentContainerKey}.
	 */
	private static final String DEFAULT_PARENT_CONTAINER_KEY = "Default";

	private static final Projection DEFAULT_PROJECTION = new Projection(
			"EPSG:4326");

	/**
	 * The key of the parent container, which holds the current
	 * {@link AgentGisWidget}, to create unique keys of the map features.
	 */
	private String parentContainerKey;

	private MapWidget mapWidget;

	private Vector agentLayer;

	/**
	 * The key of the map is created by {@link #createFeatureId(String)}
	 */
	private final Map<String, MAgent> agentByFeatureId = new HashMap<String, MAgent>();

	/**
	 * The key of the map is created by {@link #createFeatureId(String)}
	 */
	private final Map<String, List<MAgent>> agentGroups = new HashMap<String, List<MAgent>>();

	/**
	 * The key of the map is created by {@link #createFeatureId(String)}
	 */
	private Map<String, PerceivedSeverity> agentStatusesByFeatureId;

	private Map<PerceivedSeverity, String> severityColors;

	private String[] tileServers;

	private final Set<AgentSelectionListener> listeners = new HashSet<AgentSelectionListener>();

	private MAgent selectedAgent;

	private LayerSwitcher layerSwitcher;

	private OverviewMap overviewMap;

	private ScaleLine scaleLine;

	private final AgentGroupSelectionDialog groupDialog;

	@Inject
	public AgentGisWidget(final DialogFactory dialogFactory,
			final QoSMessages messages) {
		groupDialog = dialogFactory.createAgentGroupSelectionDialog(this);
		initializeUI();
		initalizeMap(messages);
	}

	public void actionAgentSelected(final MAgent agent) {
		notifyListeners(agent);
	}

	public void addListener(final AgentSelectionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void agentSelected(final MAgent agent) {
		if (agent == null) {
			mapWidget.getMap().zoomToExtent(agentLayer.getDataExtent());
		} else if (selectedAgent == null
				|| !agent.getKey().equals(selectedAgent.getKey())) {

			String featureID = "";
			final String agentFeatureId = createFeatureId(agent.getKey());
			if (agentByFeatureId.containsKey(agentFeatureId)) {
				featureID = agentFeatureId;
			} else {
				for (final String groupKey : agentGroups.keySet()) {
					if (agentGroups.get(groupKey).contains(agent)) {
						featureID = groupKey;
						break;
					}
				}
			}
			if (featureID.isEmpty()) {
				mapWidget.getMap().zoomToExtent(agentLayer.getDataExtent());
				LOGGER.warning("Unable to show agent "
						+ agent
						+ " as it is not found in single agent or group agent cache");
			} else {
				final VectorFeature feature = agentLayer
						.getFeatureById(featureID);
				final Bounds bounds = feature.getGeometry().getBounds();
				mapWidget.getMap().zoomToExtent(bounds);
			}
		}
		selectedAgent = agent;
	}

	@Override
	public Widget asWidget() {
		return mapWidget;
	}

	private Map<String, PerceivedSeverity> convertAgentStatuses(
			final Map<Source, PerceivedSeverity> agentStatuses) {
		final Map<String, PerceivedSeverity> convertedAgentStatuses = new HashMap<String, PerceivedSeverity>();
		for (final Source source : agentStatuses.keySet()) {
			convertedAgentStatuses.put(createFeatureId(source.getKey()),
					agentStatuses.get(source));
		}

		return convertedAgentStatuses;
	}

	private String createFeatureId(final String agentKey) {
		final String validatedParentContainerKey = SimpleUtils
				.isNotNullAndNotEmpty(parentContainerKey)
				? parentContainerKey
				: DEFAULT_PARENT_CONTAINER_KEY;
		return validatedParentContainerKey + "_" + agentKey;
	}

	public void disableLayerSwitcher() {
		mapWidget.getMap().removeControl(layerSwitcher);
	}

	public void disableOverviewMap() {
		mapWidget.getMap().removeControl(overviewMap);
	}

	public void disableScaleLine() {
		mapWidget.getMap().removeControl(scaleLine);
	}

	private void drawGroupAgentsPoints() {
		for (final String groupKey : agentGroups.keySet()) {
			final GISPosition gisPosition = agentGroups.get(groupKey).get(0)
					.getGisPosition();
			if (gisPosition != null) {
				final Point point = new Point(gisPosition.getLongitude(),
						gisPosition.getLatitude());
				point.transform(DEFAULT_PROJECTION, new Projection(mapWidget
						.getMap().getProjection()));

				final Style style = getAgentPointStyle(true);
				style.setLabel("...");
				final VectorFeature agentGroupFeature = new VectorFeature(
						point, style);
				agentGroupFeature.setFeatureId(groupKey);
				agentLayer.addFeature(agentGroupFeature);
			}
		}
	}

	private void drawSingleAgentPoints() {
		for (final String key : agentByFeatureId.keySet()) {
			final MAgent agent = agentByFeatureId.get(key);
			final GISPosition gisPosition = agent.getGisPosition();
			if (gisPosition != null) {
				final Point point = new Point(gisPosition.getLongitude(),
						gisPosition.getLatitude());
				point.transform(DEFAULT_PROJECTION, new Projection(mapWidget
						.getMap().getProjection()));

				final Style style = getAgentPointStyle(false);
				style.setLabel(agent.getDisplayName());
				final VectorFeature agentFeature = new VectorFeature(point,
						style);
				agentFeature.setFeatureId(createFeatureId(agent.getKey()));
				agentLayer.addFeature(agentFeature);
			}
		}
	}

	private Style getAgentPointStyle(final boolean isGroupPoint) {
		final Style agentPointStyle = new Style();
		agentPointStyle.setFillOpacity(1);
		agentPointStyle.setFill(true);
		agentPointStyle.setFillColor("grey");
		agentPointStyle.setPointRadius(6);
		agentPointStyle.setStroke(true);
		agentPointStyle.setStrokeColor("grey");
		agentPointStyle.setLabelAlign("ct");
		agentPointStyle.setLabelYOffset(-30);
		agentPointStyle.setFontWeight("bold");
		if (isGroupPoint) {
			agentPointStyle
					.setBackgroundGraphic(GeneralIcons.GIS_AGENT_MULTIPLE
							.getIcon60().getSafeUri().asString());
		} else {
			agentPointStyle.setBackgroundGraphic(GeneralIcons.GIS_AGENT
					.getIcon60().getSafeUri().asString());
		}
		agentPointStyle.setBackgroundHeight(40);
		agentPointStyle.setBackgroundWidth(40);
		agentPointStyle.setBackgroundOffset(-20, -12);
		agentPointStyle.setCursor(CURSOR_TYPE);
		return agentPointStyle;
	}

	@Override
	public GISPosition getCenter() {
		final LonLat lonLat = mapWidget.getMap().getCenter();
		return new GISPosition(lonLat.lon(), lonLat.lat());
	}

	private PerceivedSeverity getGroupStatus(final String groupID) {
		PerceivedSeverity maxSeverity = null;
		final List<MAgent> agents = agentGroups.get(groupID);
		for (final MAgent agent : agents) {
			final PerceivedSeverity severity = agentStatusesByFeatureId
					.get(createFeatureId(agent.getKey()));
			if (severity != null && severity.greater(maxSeverity)) {
				maxSeverity = severity;
			}
		}
		return maxSeverity;
	}

	@Override
	public int getZoom() {
		return mapWidget.getMap().getZoom();
	}

	private void groupAgents(final List<MAgent> agents) {
		final List<MAgent> groupList = new ArrayList<MAgent>();
		int groupIndex = 0;
		for (final MAgent agent : agents) {
			if(agent!=null) {
				boolean isGrouped = false;
				for (final String groupKey : agentGroups.keySet()) {
					if (agentGroups.get(groupKey).contains(agent)) {
						isGrouped = true;
						break;
					}
				}
				if (agent.getGisPosition() == null || isGrouped) {
					continue;
				}
				for (int i = agents.indexOf(agent) + 1; i < agents.size(); ++i) {
					if (agents.get(i).getGisPosition() != null
							&& agents.get(i).getGisPosition()
							.coordinateEquals(agent.getGisPosition())) {
						groupList.add(agents.get(i));
					}
				}
				if (!groupList.isEmpty()) {
					groupList.add(agent);
					agentGroups.put(
							AGENT_GROUP_PREFIX
									+ createFeatureId(String.valueOf(groupIndex)),
							new ArrayList<MAgent>(groupList));
					++groupIndex;
				} else {
					agentByFeatureId.put(createFeatureId(agent.getKey()), agent);
				}
				groupList.clear();
			}
		}
	}

	/**
	 *
	 */
	private void initalizeMap(final QoSMessages messages) {
		final MapOptions defaultMapOptions = new MapOptions();
		defaultMapOptions.setNumZoomLevels(16);

		mapWidget = new MapWidget("100%", "100%", defaultMapOptions);
		final org.gwtopenmaps.openlayers.client.Map map = mapWidget.getMap();

		final OSM mapnik = new OSM(messages.gisMap(), tileServers,
				new OSMOptions());
		mapnik.setIsBaseLayer(true);
		map.addLayer(mapnik);

		agentLayer = new Vector(messages.gisAgents());
		final SelectFeature selectFeature = new SelectFeature(agentLayer);
		selectFeature.setAutoActivate(true);
		map.addControl(selectFeature);
		agentLayer
				.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() {
					@Override
					public void onFeatureSelected(
							final FeatureSelectedEvent eventObject) {
						final VectorFeature feature = eventObject
								.getVectorFeature();
						final String featureName = feature.getFeatureId();
						// final Bounds bounds =
						// feature.getGeometry().getBounds();
						// mapWidget.getMap().zoomToExtent(bounds);
						if (featureName.startsWith(AGENT_GROUP_PREFIX)) {
							showGroupDialog(agentGroups.get(featureName));
						} else {
							notifyListeners(agentByFeatureId.get(featureName));
						}
						selectFeature.unSelect(feature);
					}
				});
		final Style style = new Style();
		style.setCursor(CURSOR_TYPE);
		agentLayer.setStyle(style);
		map.addLayer(agentLayer);

		layerSwitcher = new LayerSwitcher();
		overviewMap = new OverviewMap();
		scaleLine = new ScaleLine();

		map.addControl(layerSwitcher);
		map.addControl(overviewMap);
		map.addControl(scaleLine);

		layerSwitcher.getJSObject()
				.getPropertyAsDomElement(BASE_LAYER_PROPERTY)
				.setInnerHTML(messages.gisBaseLayer());
		layerSwitcher.getJSObject().getPropertyAsDomElement(OVERLAYS_PROPERTY)
				.setInnerHTML(messages.gisOverlays());
	}

	private void initializeUI() {
		Object tileServersPropertyValue = AppUtils.getClientProperties().get(
				TILE_SERVERS_PROPERTY_NAME);
		if (tileServersPropertyValue == null
				|| ((String) tileServersPropertyValue).isEmpty()) {
			tileServersPropertyValue = TILE_SERVERS_PROPERTY_DEFAULT_VALUE;
			LOGGER.severe(TILE_SERVERS_PROPERTY_NAME
					+ " property is not set, use default server");
		}
		this.tileServers = ((String) tileServersPropertyValue).split(",");
		this.agentStatusesByFeatureId = new HashMap<String, PerceivedSeverity>();

		// TODO get rid of it
		severityColors = new HashMap<PerceivedSeverity, String>();
		severityColors.put(PerceivedSeverity.CRITICAL,
				ColorConstants.getSeverityCritical());
		severityColors.put(PerceivedSeverity.MAJOR,
				ColorConstants.getSeverityMajor());
		severityColors.put(PerceivedSeverity.MINOR,
				ColorConstants.getSeverityMinor());
		severityColors.put(PerceivedSeverity.WARNING,
				ColorConstants.getSeverityWarning());
		severityColors.put(PerceivedSeverity.NOTICE,
				ColorConstants.getSeverityNotice());
		severityColors.put(PerceivedSeverity.INDETERMINATE,
				ColorConstants.getSeverityIndeterminate());
		severityColors.put(null, "#00FF00");
	}

	protected void notifyListeners(final MAgent agent) {
		if (selectedAgent != agent) {
			selectedAgent = agent;
			for (final AgentSelectionListener listener : listeners) {
				listener.agentSelected(agent);
			}
		}
	}

	public void refresh() {
		mapWidget.onResize();
	}

	public void removeListener(final AgentSelectionListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Sets the center of the map.
	 */
	public void setCenter(final GISPosition center) {
		mapWidget.getMap().setCenter(
				new LonLat(center.getLongitude(), center.getLatitude()));
	}

	/**
	 * @param parentContainerKey
	 *            the key of the parent container, which holds the current
	 *            {@link AgentGisWidget}, to create unique keys of the map
	 *            features.
	 */
	public void setParentContainerKey(final String parentContainerKey) {
		this.parentContainerKey = parentContainerKey;
	}

	private void showGroupDialog(final List<MAgent> agentGroupList) {
		final Map<MAgent, PerceivedSeverity> agentGroupMap = new HashMap<MAgent, PerceivedSeverity>();
		for (final MAgent agent : agentGroupList) {
			agentGroupMap.put(agent, agentStatusesByFeatureId
					.get(createFeatureId(agent.getKey())));
		}
		groupDialog.setAgentGroup(agentGroupMap);
		groupDialog.show();
	}

	public void updateAgent(final String agentKey,
			final PerceivedSeverity severity) {
		final String featureId = createFeatureId(agentKey);
		final PerceivedSeverity oldSeverity = agentStatusesByFeatureId
				.get(featureId);
		if (oldSeverity == null || severity != oldSeverity) {
			agentStatusesByFeatureId.put(featureId, severity);
			updateAgentStatusesOnMap();
		}
	}

	/**
	 * Обновление местоположений блоков контроля на карте. Ранее заданные
	 * статусы блоков контроля будут применены.
	 *
	 * @param agents
	 */
	public void updateAgents(final List<MAgent> agents) {
		this.agentByFeatureId.clear();
		this.agentGroups.clear();
		agentLayer.destroyFeatures();
		groupAgents(agents);
		drawSingleAgentPoints();
		drawGroupAgentsPoints();
		updateAgentStatusesOnMap();
		final Bounds dataExtent = agentLayer.getDataExtent();
		if (dataExtent != null) {
			mapWidget.getMap().zoomToExtent(dataExtent);
			// zoom to show all features with labels
			mapWidget.getMap().zoomTo(mapWidget.getMap().getZoom() - 1);
		} else {
			mapWidget.getMap().zoomToMaxExtent();
		}

		mapWidget.onResize();
	}

	/**
	 * Обновление статусов блоков контроля
	 *
	 * @param agentStatuses
	 *            - имя->статус
	 */
	public void updateAgentStatuses(
			final Map<Source, PerceivedSeverity> agentStatuses) {
		this.agentStatusesByFeatureId = convertAgentStatuses(agentStatuses);
		updateAgentStatusesOnMap();
	}

	private void updateAgentStatusesOnMap() {
		if (!agentStatusesByFeatureId.isEmpty()) {
			final VectorFeature[] features = agentLayer.getFeatures();
			if (features != null) {
				for (final VectorFeature pointFeature : agentLayer
						.getFeatures()) {
					final String featureID = pointFeature.getFeatureId();
					String color = "";

					if (agentStatusesByFeatureId.containsKey(featureID)) {
						final PerceivedSeverity severity = agentStatusesByFeatureId
								.get(featureID);
						color = severityColors.get(severity);
					} else if (agentGroups.containsKey(featureID)) {
						color = severityColors.get(getGroupStatus(featureID));
					}

					final Style pointStyle = pointFeature.getStyle();
					if (!color.isEmpty()) {
						pointStyle.setFill(true);
						pointStyle.setFillColor(color);
					} else {
						pointStyle.setFill(false);
					}
					pointFeature.setStyle(pointStyle);
				}
				agentLayer.redraw();
			}
		}
	}

	/**
	 * Sets zoom of the map.
	 *
	 * @param zoomLevel
	 *            the level of the zoom.
	 */
	public void zoomTo(final int zoomLevel) {
		mapWidget.getMap().zoomTo(zoomLevel);
	}
}
