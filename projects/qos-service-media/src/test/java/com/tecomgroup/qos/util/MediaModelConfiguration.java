/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.communication.result.Result;
import com.tecomgroup.qos.communication.result.VideoResult;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MLiveStream;
import com.tecomgroup.qos.domain.MLiveStreamTemplate;
import com.tecomgroup.qos.domain.MLiveStreamWrapper;
import com.tecomgroup.qos.domain.MMediaAgentModule;
import com.tecomgroup.qos.domain.MProperty;
import com.tecomgroup.qos.domain.MRecordedStream;
import com.tecomgroup.qos.domain.MRecordedStreamTemplate;
import com.tecomgroup.qos.domain.MRecordedStreamWrapper;
import com.tecomgroup.qos.domain.MStream;
import com.tecomgroup.qos.domain.MVideoResult;
import com.tecomgroup.qos.domain.Source;

/**
 * @author kunilov.p
 * 
 */
public class MediaModelConfiguration extends SharedModelConfiguration {

	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			Result.RESULT_DATE_FORMAT);

	private final static String STREAM_URL_PREFIX = "rtmpt://192.168.118.4:80/qligentPlayer/";

	public final static String LIVE_STREAM_1 = "looping_video_flv1";

	public final static String LIVE_STREAM_2 = "looping_video_flv2";

	public final static String LIVE_STREAM_3 = "looping_video_h264";

	public final static String LIVE_STREAM_4 = "looping_video_flv1_1";

	public final static String LIVE_STREAM_5 = "looping_video_flv2_1";

	public final static String LIVE_STREAM_6 = "looping_video_h264_1";

	public final static String LIVE_STREAM_7 = "looping_video_flv1_2";

	public final static String LIVE_STREAM_8 = "looping_video_flv2_2";

	public final static String LIVE_STREAM_9 = "looping_video_h264_2";

	public static MLiveStreamTemplate createLiveStreamTemplate(
			final String templateName, final MAgent relatedAgent,
			final MAgentTask relatedTask, final int wrappersCount) {
		final List<MLiveStreamWrapper> wrappers = new ArrayList<MLiveStreamWrapper>();
		for (int i = 0; i < wrappersCount; i++) {
			wrappers.add(createLiveStreamWrapper(relatedAgent, relatedTask));
		}
		final MLiveStreamTemplate template = new MLiveStreamTemplate(
				templateName);
		template.setWrappers(wrappers);

		return template;
	}

	public static MLiveStreamWrapper createLiveStreamWrapper(
			final MAgent agent, final MAgentTask task) {
		final MStream stream = MediaModelConfiguration
				.createTemplateLiveStream(0, LIVE_STREAM_1);
		stream.setSource(Source.getStreamSource(task.getKey(), stream.getKey()));
		final MLiveStreamWrapper wrapper = new MLiveStreamWrapper();
		wrapper.setStream(stream);
		wrapper.setAgent(agent);
		wrapper.setTaskKey(task.getKey());
		wrapper.setStreamKey(stream.getKey());

		return wrapper;
	}

	public static MMediaAgentModule createMediaAgentModule(final MAgent agent) {
		final MMediaAgentModule mediaModule = new MMediaAgentModule();
		mediaModule.setDisplayName("Analog Signal Quality - IT-09A");
		mediaModule.setKey(createModuleKey(agent.getKey()));
		mediaModule.setParent(agent);
		mediaModule
				.setTemplateResultConfiguration(createTemplateResultConfiguration(false));
		final List<MStream> templateStreams = new LinkedList<MStream>();
		templateStreams.addAll(createTemplateLiveStreams());
		templateStreams.addAll(createTemplateRecordedStreams());
		mediaModule.setTemplateStreams(templateStreams);

		return mediaModule;
	}

	public static MMediaAgentModule createMediaAgentModule(final MAgent agent,
			final boolean generateStreams) {
		final MMediaAgentModule mediaModule = new MMediaAgentModule();
		mediaModule.setDisplayName("Analog Signal Quality - IT-09A");
		mediaModule.setKey(createModuleKey(agent.getKey()));
		mediaModule.setParent(agent);
		mediaModule
				.setTemplateResultConfiguration(createTemplateResultConfiguration(false));

		if (generateStreams) {
			final List<MStream> templateStreams = new LinkedList<MStream>();
			templateStreams.addAll(createTemplateLiveStreams());
			templateStreams.addAll(createTemplateRecordedStreams());
			mediaModule.setTemplateStreams(templateStreams);
		}

		return mediaModule;
	}

	public static MMediaAgentModule createMediaAgentModule(final MAgent agent,
			final String name) {
		final MMediaAgentModule mediaModule = new MMediaAgentModule();
		mediaModule.setDisplayName("Module - " + name);
		mediaModule.setKey(name);
		mediaModule.setParent(agent);
		mediaModule
				.setTemplateResultConfiguration(createTemplateResultConfiguration(false));
		final List<MStream> templateStreams = new LinkedList<MStream>();
		templateStreams.addAll(createTemplateLiveStreams());
		templateStreams.addAll(createTemplateRecordedStreams());
		mediaModule.setTemplateStreams(templateStreams);

		return mediaModule;
	}

	public static MRecordedStreamTemplate createRecordedStreamTemplate(
			final String templateName, final MAgent relatedAgent,
			final MAgentTask relatedTask, final int wrappersCount) {
		final List<MRecordedStreamWrapper> wrappers = new ArrayList<MRecordedStreamWrapper>();
		for (int i = 0; i < wrappersCount; i++) {
			wrappers.add(createRecordedStreamWrapper(relatedAgent, relatedTask));
		}
		final MRecordedStreamTemplate template = new MRecordedStreamTemplate(
				templateName);
		template.setWrappers(wrappers);

		return template;
	}

	public static MRecordedStreamWrapper createRecordedStreamWrapper(
			final MAgent agent, final MAgentTask task) {
		final MStream stream = MediaModelConfiguration
				.createTemplateRecordedStream((int) (Math.random() * 100));
		stream.setSource(Source.getStreamSource(task.getKey(), stream.getKey()));
		final MRecordedStreamWrapper wrapper = new MRecordedStreamWrapper();
		wrapper.setStream(stream);
		wrapper.setAgent(agent);
		wrapper.setTaskKey(task.getKey());
		wrapper.setStreamKey(stream.getKey());
		wrapper.setIntervalType(TimeInterval.Type.DAY);
		wrapper.setStartDateTime(new Date(System.currentTimeMillis()
				- TimeUnit.HOURS.toMillis(1)));
		wrapper.setEndDateTime(new Date());
		wrapper.setVideoTimeZone("0");

		return wrapper;
	}
	public static List<MProperty> createStreamProperties() {
		final List<MProperty> streamProperties = new ArrayList<MProperty>();
		streamProperties.add(new MProperty("videoBitrate", true,
				"Video Bitrate", "256"));
		streamProperties.add(new MProperty("codec", true, "Codec", "h264"));
		streamProperties.add(new MProperty("size", true, "Size", "768 X 576"));
		streamProperties.add(new MProperty("fps", true, "FPS", "5"));
		streamProperties.add(new MProperty(MStream.RECORDED_FILE_PREFIX, false,
				MStream.RECORDED_FILE_PREFIX, "384"));

		return streamProperties;
	}
	public static MLiveStream createTemplateLiveStream(final int index,
			final String postfixUrl) {
		final MLiveStream liveStream = new MLiveStream();
		liveStream.setTemplateUrl(STREAM_URL_PREFIX + postfixUrl);
		liveStream.setProperties(createStreamProperties());
		liveStream.setKey("liveStream" + index);

		return liveStream;
	}

	public static List<MStream> createTemplateLiveStreams() {
		final List<MStream> liveStreams = new ArrayList<MStream>();

		liveStreams.add(createTemplateLiveStream(0, LIVE_STREAM_1));
		liveStreams.add(createTemplateLiveStream(1, LIVE_STREAM_2));
		liveStreams.add(createTemplateLiveStream(2, LIVE_STREAM_3));
		liveStreams.add(createTemplateLiveStream(3, LIVE_STREAM_4));
		liveStreams.add(createTemplateLiveStream(4, LIVE_STREAM_5));
		liveStreams.add(createTemplateLiveStream(5, LIVE_STREAM_6));
		liveStreams.add(createTemplateLiveStream(6, LIVE_STREAM_7));
		liveStreams.add(createTemplateLiveStream(7, LIVE_STREAM_8));
		liveStreams.add(createTemplateLiveStream(8, LIVE_STREAM_9));

		return liveStreams;
	}

	public static MRecordedStream createTemplateRecordedStream(final int index) {
		final MRecordedStream recordedStream = new MRecordedStream();
		recordedStream.setTemplateStreamUrl(STREAM_URL_PREFIX
				+ "stream/${properties.plp}_${properties.pid}");
		recordedStream.setTemplateDownloadUrl(STREAM_URL_PREFIX
				+ "download/${properties.plp}_${properties.pid}");
		recordedStream.setProperties(createStreamProperties());
		recordedStream.setKey("recordedStream" + index);

		return recordedStream;
	}

	public static List<MStream> createTemplateRecordedStreams() {
		final List<MStream> streams = new ArrayList<MStream>();
		for (int i = 0; i < STREAM_COUNT; i++) {
			streams.add(createTemplateRecordedStream(i));
		}
		return streams;
	}

	public static MVideoResult createVideoResult(final Source streamSource) {
		final MVideoResult result = new MVideoResult();
		final Long time = System.currentTimeMillis();

		result.setStartDateTime(new Date(time
				- TimeConstants.MILLISECONDS_PER_MINUTE));
		result.setEndDateTime(new Date(time));
		result.setFileName("fileName");
		result.setSource(streamSource);
		return result;
	}

	public static VideoResult createVideoResult(final String streamKey) {
		final VideoResult result = new VideoResult();
		final Long time = System.currentTimeMillis();

		result.setStartDateTime(formatter.format(new Date(time
				- TimeConstants.MILLISECONDS_PER_MINUTE)));
		result.setEndDateTime(formatter.format(new Date(time)));
		result.setFileName("fileName");
		result.setStreamKey(streamKey);
		return result;
	}

	public static List<MVideoResult> createVideoResults(
			final Source streamSource) {
		return createVideoResults(streamSource, VIDEO_RESULT_COUNT);
	}

	public static List<MVideoResult> createVideoResults(
			final Source streamSource, final int resultCount) {
		final List<MVideoResult> results = new LinkedList<MVideoResult>();
		for (int index = 0; index < resultCount; index++) {
			results.add(createVideoResult(streamSource));
		}
		return results;
	}

	public static List<VideoResult> createVideoResults(final String streamKey) {
		return createVideoResults(streamKey, VIDEO_RESULT_COUNT);
	}

	public static List<VideoResult> createVideoResults(final String streamKey,
			final int resultCount) {
		final List<VideoResult> results = new LinkedList<VideoResult>();
		for (int index = 0; index < resultCount; index++) {
			results.add(createVideoResult(streamKey));
		}
		return results;
	}

}
