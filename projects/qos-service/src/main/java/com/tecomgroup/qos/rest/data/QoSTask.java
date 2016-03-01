package com.tecomgroup.qos.rest.data;

import com.tecomgroup.qos.domain.MLiveStream;
import com.tecomgroup.qos.domain.MProperty;
import com.tecomgroup.qos.domain.MRecordedStream;
import com.tecomgroup.qos.domain.MStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.*;

/**
 *
 * @author stroganov.d
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class QoSTask extends QoSTaskBase{
    protected static final String RTMP_PROTOCOL_URL_KEY="RTMP";
    protected static final String HLS_PROTOCOL_URL_KEY="HLS";
    protected static final String RECORDED_STREAM="RECORDED";
    protected static final String RECORDED_DOWNLOAD="DOWNLOAD";
    protected static final String STREAM_ID="STREAM_ID";
    public Map<String,String> streams=new HashMap<>();
    public boolean defaultStream;
    public Long relatedRecordingTaskId;
    public ParameterGroup parameterGroup;

    public void addStream(MStream mstream)
    {
        if(mstream instanceof MLiveStream) {
            MLiveStream live= (MLiveStream) mstream;
            String url = live.getUrl();
            if (url != null && url.startsWith("rtmpt:")) {
                streams.put(RTMP_PROTOCOL_URL_KEY, url);
            } else if (url != null && url.endsWith(".m3u8")) {
                streams.put(HLS_PROTOCOL_URL_KEY, url);
            }
        }else if(mstream instanceof MRecordedStream)
        {
            MRecordedStream recorded= (MRecordedStream) mstream;
            String downloadURL=recorded.getDownloadUrl();
            String streamURL=recorded.getStreamUrl();
            MProperty mrfp= recorded.getProperty(MStream.RECORDED_FILE_PREFIX);
            if(mrfp!=null) {
                streams.put(STREAM_ID, mrfp.getValue());
            }
            streams.put(RECORDED_DOWNLOAD,downloadURL);
            streams.put(RECORDED_STREAM,streamURL);

        }
    }


	public void addStreams(Collection<MStream> mstreams) {
		if (mstreams != null && !mstreams.isEmpty()) {
			for (MStream stream : mstreams) {
				addStream(stream);
			}
		}
	}
}
