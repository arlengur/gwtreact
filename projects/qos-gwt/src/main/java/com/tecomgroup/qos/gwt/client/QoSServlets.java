/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

/**
 * @author kunilov.p
 * 
 */
public interface QoSServlets {

	/**
	 * Servlet to get results in json format.
	 */
	public final static String resultServlet = "ResultServlet";

	/**
	 * Servlet to download results in csv format.
	 */
	public final static String downloadResultServlet = "DownloadResultServlet";

    public final static String downloadResultServletStatusUrl = "status";

    public final static String downloadResultServletResultUrl = "result";

	public final static String exportAlertReport = "ExportAlertReport";

	/**
	 * Servlet to find Video On Demand.
	 */
	public final static String searchVideoFilesServlet = "VodSearcherServlet";

}
