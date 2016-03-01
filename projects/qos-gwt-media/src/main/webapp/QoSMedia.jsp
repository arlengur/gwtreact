<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!doctype html>
<html>
<%
	String userAgent = request.getHeader("user-agent");
	String mode = request.getParameter("mode");
	//boolean isMobile = userAgent.matches("(?i).*(ipod|ipad|android|iphone|mobile).*") || ((mode != null ) && ("mobile".equalsIgnoreCase(mode)));
	// Do not support mobile version now
	// isMobile = ((mode != null ) && ("desktop".equalsIgnoreCase(mode))) ? false : isMobile;
	boolean isMobile = false;
%>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="gwt:property" content="locale=<%=request.getLocale()%>">
<title>QoS Media</title>
<link rel="shortcut icon" href="DesktopQoSMedia/qos/resources/images/favicon.png">
	<%
	if (isMobile) {
%>
<meta name="viewport"
	content="width = device-width, initial-scale=1.0, user-scalable = no, maximum-scale=1.0">
<meta name="apple-mobile-web-app-capable" content="yes" />
<%
	} 
%>
</head>
<body class="preloading" onload="setTimeout(function() { window.scrollTo(0, 1) }, 100);">
	<div id="preloader"></div>
	<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1'
		style="position: absolute; width: 0; height: 0; border: 0"></iframe>
	<!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
	<noscript>
		<div
			style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
			Your web browser must have JavaScript enabled in order for this
			application to display correctly.</div>
	</noscript>
<%
	if (isMobile) {
%>
<script type="text/javascript"
	src="MobileQoSMedia/MobileQoSMedia.nocache.js"></script>
<link type="text/css" rel="stylesheet"
	href="./MobileQoSMedia/touch-charts/resources/css/touch-charts-demo.css" />
<link type="text/css" rel="stylesheet"
	href="MobileQoSMedia/grid/resources/css/Ext.ux.grid.View.compressed.css" />
<link id="0" type="text/css" rel="stylesheet"
	href="MobileQoSMedia/touch/resources/css/bb6.css" />
<link id="1" type="text/css" rel="stylesheet"
	href="MobileQoSMedia/qos/resources/css/qos-ipad.css" />
<script type="text/javascript" charset="utf-8"
	src="MobileQoSMedia/touch/sencha-touch-all-compat-2.0.1.1.gpl.js"></script>
<script type="text/javascript"
	src="./MobileQoSMedia/touch-charts/touch-charts.2.0.0.js"></script>
<script type="text/javascript"
	src="MobileQoSMedia/grid/Ext.ux.touch.grid/feature/Abstract-compressed.js"></script>
<script type="text/javascript"
	src="MobileQoSMedia/grid/Ext.ux.touch.grid/feature/Feature-compressed.js"></script>
<script type="text/javascript"
	src="MobileQoSMedia/grid/Ext.ux.touch.grid/feature/Editable-compressed.js"></script>
<script type="text/javascript"
	src="MobileQoSMedia/grid/Ext.ux.touch.grid/feature/Sorter-compressed.js"></script>
<script type="text/javascript"
	src="MobileQoSMedia/grid/Ext.ux.touch.grid/feature/Paging-compressed.js"></script>
<script type="text/javascript"
	src="MobileQoSMedia/grid/Ext.ux.touch.grid/View-compressed.js"></script>
<!-- Google Maps support -->
<script type="text/javascript"
	src="http://maps.google.com/maps/api/js?sensor=true"></script>
<%
	} else {
%>
<script type="text/javascript"
	src="DesktopQoSMedia/DesktopQoSMedia.nocache.js"></script>
<link type="text/css" rel="stylesheet"
	href="./DesktopQoSMedia/reset.css" />
<link type="text/css" rel="stylesheet"
	href="./DesktopQoSMedia/qos/resources/css/appearance.css" />
<script src="DesktopQoSMedia/thirdparty/jquery-1.7.2.min.js"></script>
<script src="DesktopQoSMedia/thirdparty/timezone-js/date.js"></script>
<script type="text/javascript" src="DesktopQoSMedia/thirdparty/zeroclipboard-1.2.1/ZeroClipboard.min.js"></script>
<script type="text/javascript">
	timezoneJS.timezone.zoneFileBasePath = 'tz';
	timezoneJS.timezone.init();
	
	ZeroClipboard.setDefaults( { moviePath: 'DesktopQoSMedia/thirdparty/zeroclipboard-1.2.1/ZeroClipboard.swf' } );
</script>
<script src="DesktopQoSMedia/player/dependencies/swfobject.js"></script>
<script src="DesktopQoSMedia/player/qligentPlayerSettings.js"></script>
<script src="DesktopQoSMedia/player/qligentPlayerModule.js"></script>
<script src="DesktopQoSMedia/thirdparty/Highstock-1.3.10/js/highstock.js"></script>
<script
	src="DesktopQoSMedia/thirdparty/Highstock-1.3.10/js/highcharts-more.js"></script>
<script
	src="DesktopQoSMedia/thirdparty/Highstock-1.3.10/js/modules/exporting.js"></script>
<script src="DesktopQoSMedia/grayChartTheme.js"></script>
<script src="DesktopQoSMedia/chartConfig.js"></script>
<script src="DesktopQoSMedia/chartDataLoader.js"></script>
<script src="DesktopQoSMedia/chartThreshold.js"></script>
<script src="DesktopQoSMedia/chartResult.js"></script>
<!-- <script src="DesktopQoSMedia/js/gwt-openlayers/util.js"></script> -->
<script src="DesktopQoSMedia/thirdparty/OpenLayers-2.12/OpenLayers.js"></script>
<script src="DesktopQoSMedia/thirdparty/openstreetmap.js"></script>
<script src="DesktopQoSMedia/thirdparty/jquery.datetimepicker.js"></script>
<link type="text/css" rel="stylesheet"
      href="./DesktopQoSMedia/thirdparty/jquery.datetimepicker.css" />
<script src="DesktopQoSMedia/datePicker.js"></script>
<script src="DesktopQoSMedia/thirdparty/d3/d3.js"></script>
<script src="DesktopQoSMedia/thirdparty/lodash/index.js"></script>
<script src="DesktopQoSMedia/thirdparty/color-js/color.js"></script>
<script src="DesktopQoSMedia/thirdparty/babel/browser.js"></script>
<link rel="stylesheet" href="DesktopQoSMedia/piechart.css">
<script src="DesktopQoSMedia/piechart.es6" type="text/babel"></script>
<%
	}
%>
</body>
</html>