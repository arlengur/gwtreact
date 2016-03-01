package com.tecomgroup.qos.exception;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO to pass stack trace of exceptions via soap
 * 
 * @author kunilov.p
 * 
 */
@XmlRootElement
public class FaultInfo {
	private String[] stackTrace;

	public FaultInfo() {
	};

	public FaultInfo(final Exception exception) {
		final List<String> stackTraceList = new ArrayList<String>();

		Throwable exceptionCause = exception;
		while (exceptionCause != null) {
			stackTraceList.add("Caused by: " + exceptionCause.toString());
			for (final StackTraceElement element : exception.getStackTrace()) {
				stackTraceList.add("at " + element.toString());
			}
			exceptionCause = exceptionCause.getCause();
		}
		stackTrace = stackTraceList.toArray(new String[0]);
	}

	public String[] getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(final String[] stackTrace) {
		this.stackTrace = stackTrace;
	}
}
