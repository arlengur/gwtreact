

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MStream;
import java.util.List;



public interface StreamsInfoService {

	public <T extends MStream> List<T> getStreams(final MAgentTask task,final Class<T> streamType);

}
