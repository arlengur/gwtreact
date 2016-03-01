var qosChartModule = (function() {

	var charts = {};

	var criticalPlotLinePrefix = "criticalPlotLine";

	var warningPlotLinePrefix = "warningPlotLine";

	var flagSeriesPrefix = "flagSeries";

	var serviceURI = "DesktopQoSMedia/ResultServlet";

	var minTimeInterval = 0;

    function getSeriesRequest(series, useInterpolation, isAutoscalingEnabled) {
        return {
            serviceURI: serviceURI,
            taskKey: series['taskKey'],
            paramName: series['paramName'],
            paramProperties: encodeURIComponent(series['paramProperties']),
            useInterpolation: useInterpolation,
            autoscalingEnabled: isAutoscalingEnabled
        };
    };

    return {
		setTimezoneOffset: function(offset) {
		Highcharts.setOptions({
			global: {
				timezoneOffset: offset
			}
		});
		},

        addParameterSeries: function(series, chart, lineType, isMouseTrackingEnabled,
                isCaptionsEnabled, isThresholdsExist, isThresholdsEnabled) {
            var seriesKey = series['seriesKey'];
            var result = series['results'];
            var seriesName = qosChartModule.getSeriesName(series);
            var createdSeries = chart.addSeries({
                data: result.data,
                type: lineType,
                id: seriesKey,
                name: seriesName,
                dataGrouping: {
                    enabled: false
                },
                enableMouseTracking: isMouseTrackingEnabled
            });

            if (isCaptionsEnabled) {
                qosChartModule.addOnSeriesFlag(chart, seriesKey,
                    createdSeries.data, seriesName);
            }

            createdSeries['qosDataRequest'] = series['qosDataRequest'];
            createdSeries['yAxisMin'] = result.min;
            createdSeries['yAxisMax'] = result.max;
            if (isThresholdsExist) {
                qosChartModule.addThresholdLines(
                    createdSeries,
                    series['warningThreshold'],
                    series['criticalThreshold'],
                    series['thresholdType'],
                    isThresholdsEnabled,
                    isCaptionsEnabled
                );
                series.thresholdLines = createdSeries.thresholdLines;
            }
        },

		addBoolSeries : function(chart, lineType, series, useInterpolation,
								 startDate, endDate, shift, isCaptionsEnabled,
								 isMouseTrackingEnabled, qosDataRequest, result) {
			var seriesName = this.getSeriesName(series);

			var isRange = (lineType == 'arearange') ? true : false;

			var threshold = qosThresholdModule.toBoolThreshold(series, isRange);

            var shiftData = qosChartModule.addBoolDataShift(shift,
                    result.data, isRange);

            var config = {
                data : shiftData,
                dashStyle : 'Solid',
                id : series['seriesKey'],
                name : seriesName,
                dataGrouping : {
                    enabled : false
                },
                threshold : threshold.threshold + shift,
                color : threshold.color,
                negativeColor : threshold.negativeColor,
                enableMouseTracking: isMouseTrackingEnabled
            };
            if (lineType == 'step') {
                config.step = 'center';
            } else if (lineType == 'arearange') {
                config.step = 'center';
                config.type = lineType;
            } else {
                config.type = lineType;
            }

            var createdSeries = chart.addSeries(config);
            if (isCaptionsEnabled) {
                qosChartModule.addOnSeriesFlag(chart,
                        series['seriesKey'], createdSeries.data,
                        seriesName);
            }

            createdSeries['dataShift'] = shift;
            createdSeries['qosDataRequest'] = qosDataRequest;
		},

		removeFlagFromSeries : function(chart, seriesKey) {
			var flagSeries = chart.get(flagSeriesPrefix + seriesKey);
			if (flagSeries != null) {
				flagSeries.remove();
			}
		},

		// convertedData implies Series.data (array of Object)
		addOnSeriesFlag : function(chart, seriesKey, convertedData, title) {

			function findForward(convertedData) {
				var length = convertedData.length;
				for ( var i = 0; i < length; i++) {
					if ((convertedData[i] !== undefined)
							&& (convertedData[i].y != null)) {
						return convertedData[i].x;
					}
				}
				return null;
			}

			function findBackward(convertedData) {
				var length = convertedData.length;
				for ( var i = length - 1; i >= 0; i--) {
					if ((convertedData[i] !== undefined)
							&& (convertedData[i].y != null)) {
						return convertedData[i].x;
					}
				}
				return null;
			}

			function findNotNullDate(convertedData) {
				var xDate = null;
				var midLength = Math.round(convertedData.length / 2);
				if (midLength != 0) {
					var index = midLength - 1;
					if ((convertedData[index] !== undefined)
							&& (convertedData[index].y != null)) {
						xDate = convertedData[index].x;
					} else {
						var xDate = findBackward(convertedData.slice(0, index));
						if (xDate == null) {
							xDate = findForward(convertedData.slice(index + 1,
									convertedData.length));
						}
					}
				}
				return xDate;
			}
			var xDate = findNotNullDate(convertedData);
			if (xDate != null) {
				chart.addSeries({
					type : 'flags',
					name : title,
					id : flagSeriesPrefix + seriesKey,
					onSeries : seriesKey,
					color : '#423189',
					shape : 'squarepin',
					showInLegend : false,
					data : [ {
						x : xDate,
						title : title
					} ]
				});
			}
		},

		addBoolDataShift : function(shift, data, isRange) {
			var length = data.length;
			for ( var i = 0; i < length; i++) {
				if (data[i][1] != null) {
					if (isRange) {
						data[i] = [ data[i][0], data[i][1] + shift,
								data[i][1] + shift + 0.99 ];
					} else {
						data[i][1] = data[i][1] + shift;
					}
				} else {
					if (isRange) {
						data[i] = [ data[i][0], null, null ];
					}
				}
			}

			return data;
		},

		addThresholdLines : function(series, warningThreshold,
				criticalThreshold, thresholdType, isThresholdsVisible,
                isCaptionsEnabled) {

			var seriesId = series.options.id;
			// create empty array for store plotLines
			series['thresholdLines'] = [];
			var line;

			if (warningThreshold !== undefined) {
				line = {
					value : warningThreshold,
                    type: 'Warning',
					color : qosThresholdModule.getWarningColor(),
					dashStyle : 'dash',
					width : 2,
					id : warningPlotLinePrefix + seriesId
				};
                if(isCaptionsEnabled) {
                    line.label = qosThresholdModule.getThresholdLabel('Warning', series.options.name);
                }
				series['thresholdLines'].push(line);
				if (isThresholdsVisible)
					series.yAxis.addPlotLine(line);

			}

			if (criticalThreshold !== undefined) {
				line = {
					value : criticalThreshold,
                    type: 'Critical',
					color : qosThresholdModule.getCriticalColor(),
					dashStyle : 'dash',
					width : 2,
					id : criticalPlotLinePrefix + seriesId
				};
                if(isCaptionsEnabled) {
                    line.label = qosThresholdModule.getThresholdLabel('Critical', series.options.name);
                }
				series['thresholdLines'].push(line);
				if (isThresholdsVisible)
					series.yAxis.addPlotLine(line);
			}
		},

		addAllSecondaryParameterSeries : function (chart, lineType, dataSeries,
                useInterpolation, startDate, endDate,
                isThresholdsExist, isThresholdsEnabled, isCaptionsEnabled,
                isAutoscalingEnabled, isMouseTrackingEnabled) {

			function onComplete() {
				$.each(dataSeries, function(index, series) {
                    qosChartModule.addParameterSeries(series, chart, lineType, isMouseTrackingEnabled,
                        isCaptionsEnabled, isThresholdsExist, isThresholdsEnabled);
                });

				qosThresholdModule.adjustYaxisMinMax(chart, chart.qosToolbarAutoscaling, chart.qosToolbarThresholds);
				window.chartBuilt(chart.qosName);
			}

			// variables definition
			var seriesCount = dataSeries.length;
			var completeCount = 0;
			$.each(dataSeries, function(index, series) {
				chart.showLoading();
				var qosDataRequest = getSeriesRequest(series, useInterpolation, isAutoscalingEnabled);

				dataLoaderModule.getJSONData(qosDataRequest, startDate,
						endDate, function(result) {
							series['results'] = result;
							series['qosDataRequest'] = qosDataRequest;
							completeCount++;
							if (completeCount == seriesCount) {
								chart.hideLoading();
								onComplete();
							}
						});
			});
		},

		changeTypeBoolChart : function(chartName, oldType, newType) {
			this.changeTypeMultipleSeriesChart(chartName, oldType, newType);
		},

		changeTypeSingleSeriesChart : function(chartName, oldType, newType) {
			if (oldType == newType)
				return;

			var foundChart = this.findChartByName(chartName);
			if (foundChart != null) {
				// save info before update
				this.updateSeriesOptions(foundChart.series[0], {
					type : newType,
					data : foundChart.series[0].options.data,
					dataGrouping : {
						enabled : false
					}
				});
				window.chartTypeChanged(chartName, newType, oldType);
			}
		},

		changeTypeMultipleSeriesChart : function(chartName, oldType, newType) {
			if (oldType == newType)
				return;

			var foundChart = this.findChartByName(chartName);
			if (foundChart != null) {
				var series;
				var updatedSeries;
				var flagSeries;
				var effectiveNewType;
				
				var dataSeriesIds = this.getDataSeriesIds(foundChart);
				for ( var i = 0; i < dataSeriesIds.length; i++) {
					series = foundChart.get(dataSeriesIds[i]);
					updatedSeries = null;
					if ((newType != 'step') && (oldType != 'step')) {
						this.updateSeriesOptions(series, {
							type : newType,
							step : false,
							data : series.options.data,
							dataGrouping : {
								enabled : false
							}
						});
						updatedSeries = series;
					} else {
						var isNewTypeEqualsStep
						if (newType == 'step') {
							isNewTypeEqualsStep = true;
							effectiveNewType = 'line';
						} else if (newType == 'arearange') {
							isNewTypeEqualsStep = 'center';
							effectiveNewType = newType;
						} else {
							isNewTypeEqualsStep = false;
							effectiveNewType = newType;
						}

						updatedSeries = this.changeTypeByRemoving(oldType,
								effectiveNewType, series, isNewTypeEqualsStep);
					}
					// foundChart.redraw();
					if (updatedSeries != null) {
						flagSeries = foundChart.get(flagSeriesPrefix
								+ updatedSeries.options.id);
						if (flagSeries != null) {
							flagSeries.remove();
							this.addOnSeriesFlag(foundChart,
									updatedSeries.options.id,
									updatedSeries.data,
									updatedSeries.options.name);
						}
					}
				}
				window.chartTypeChanged(chartName, newType, oldType);
			}
		},

		changeTypeByRemoving : function(oldType, newType, series, isStepLine) {

			function toRangeSeries(data) {
				for ( var i = 0; i < data.length; i++) {
					if (data[i][1] != null) {
						data[i] = [ data[i][0], data[i][1], data[i][1] + 0.99 ];
					} else {
						data[i] = [ data[i][0], null, null ];
					}
				}
				return data;
			}

			function fromRangeSeries(data) {
				for ( var i = 0; i < data.length; i++) {
					if (data[i][1] != null) {
						data[i] = [ data[i][0], data[i][1] ];
					} else {
						data[i] = [ data[i][0], null ];
					}
				}
				return data;
			}

			var convertedData = series.options.data;
			var newThreshold = series.options.threshold;
			if (oldType == 'arearange') {
				convertedData = fromRangeSeries(convertedData);
				newThreshold = newThreshold - 0.5;
			} else if (newType == 'arearange') {
				convertedData = toRangeSeries(convertedData);
				newThreshold = newThreshold + 0.5;
			}

			var newConfig = {
				type : newType,
				step : isStepLine,
				name : series.name,
				color : series.options.color,
				negativeColor : series.options.negativeColor,
				threshold : newThreshold,
				data : convertedData,
				id : series.options.id,
				dataGrouping : {
					enabled : false
				}
			};
			var chart = series.chart;
			series.remove();
			return chart.addSeries(newConfig);
		},

        addAllSecondaryBoolSeries: function (seriesData, chartName, startDate,
											 endDate, shift, lineType,
											 isCaptionsEnabled, isMouseTrackingEnabled) {
            var seriesGraphData = {};
            var seriesDataRequests = {};

            function onComplete() {
                var shiftInterval = shift;
                $.each(seriesData, function (index, series) {
                    shift += shiftInterval;
                    qosChartModule.addBoolSeries(
                        charts[chartName], lineType,
                        series, 0,
                        startDate, endDate, shift,
                        isCaptionsEnabled, isMouseTrackingEnabled,
                        seriesDataRequests[index], seriesGraphData[index]
                    );
                });
                window.chartBuilt(chartName);
            }

            var size = seriesData.length;
            var seriesGraphDataCounter = 0;
            $.each(seriesData, function (index, series) {
                const chart = charts[chartName];
                chart.showLoading();
                seriesDataRequests[index] =
                    getSeriesRequest(series, 0, false);
                dataLoaderModule.getJSONData(seriesDataRequests[index], startDate, endDate,
                    function (data) {
                        seriesGraphData[index] = data;
                        seriesGraphDataCounter++;
                        // wait for all graph data to arrive, then put
                        // series on the graph in the correct order
                        if (seriesGraphDataCounter == size) {
                            chart.hideLoading();
                            onComplete();
                        }
                    });
            });
        },

        createBoolChart : function (chartName, lineType, seriesData, startDate,
                                       endDate, xTitle, yTitle, height, divElementId,
                                       isCaptionsEnabled, isZoomEnabled, isMouseTrackingEnabled,
                isLegendEnabled) {
			this.clearIfExists(chartName);

			var masterSeries = seriesData.shift();

            var qosDataRequest = getSeriesRequest(masterSeries, 0, false);

			var masterSeriesName = this.getSeriesName(masterSeries);

			var isRange = (lineType == 'arearange') ? true : false;

			var threshold = qosThresholdModule.toBoolThreshold(masterSeries,
					isRange);

			var configCopy = jQuery.extend(true, {}, qosConfigModule
					.getStandardChartConfig());

            dataLoaderModule.getJSONData(qosDataRequest, startDate, endDate,
                function(result) {

                    // should be even number
                    // also a negative one in order for series to go
                    // from top to bottom
                    var shift = -10;

                    var shiftData = qosChartModule.addBoolDataShift(
                        shift, result.data,	isRange);
                    configCopy.legend.enabled = isLegendEnabled;
                    configCopy.tooltip = {
                        formatter : function() {
                            return qosChartModule.formatTooltip(
                                    this.points, this.x, true);
                        }
                    };
                    configCopy.chart.renderTo = divElementId;
                    configCopy.chart.height = height;
                    configCopy.navigator.series.data = shiftData;
                    configCopy.xAxis.title.text = xTitle;
                    // configCopy.xAxis.minRange = minTimeInterval;
                    configCopy.legend.symbolWidth = 50;
                    configCopy.xAxis.events = {
                        afterSetExtremes : function(e) {
                            qosChartModule
                                    .setExtremesMultiSeries(
                                            qosChart.qosName, e,
                                            true, this);
                        }
                    };

                    configCopy.xAxis.dateTimeLabelFormats = qosChartModule
                            .getDateTimeFormats();
                    configCopy.yAxis.title.text = yTitle;
                    configCopy.yAxis.offset = 45;
                    configCopy.yAxis.labels = {
                        enabled : false
                    };
                    configCopy.series[0].id = masterSeries['seriesKey'];
                    configCopy.series[0].name = masterSeriesName;
                    configCopy.series[0].data = shiftData;
                    if (lineType == 'step') {
                        configCopy.series[0].step = 'center';
                    } else if (lineType == 'arearange') {
                        configCopy.series[0].step = 'center';
                        configCopy.series[0].type = lineType;
                    } else {
                        configCopy.series[0].type = lineType;
                    }
                    configCopy.series[0].dashStyle = 'Solid';
                    configCopy.series[0].threshold = threshold.threshold
                            + shift;
                    configCopy.series[0].color = threshold.color;
                    configCopy.series[0].negativeColor = threshold.negativeColor;
                    if (!isZoomEnabled) {
                        configCopy.chart.zoomType = false;
                        configCopy.navigator.enabled = false;
                        configCopy.scrollbar.enabled = false;
                    }
                    configCopy.series[0].enableMouseTracking = isMouseTrackingEnabled;

                    var qosChart = new Highcharts.StockChart(
                            configCopy);

                    var convertedData = qosChart
                            .get(masterSeries['seriesKey']).data;
                    if (isCaptionsEnabled) {
                        qosChartModule.addOnSeriesFlag(qosChart,
                                masterSeries['seriesKey'],
                                convertedData, masterSeriesName);
                    }

                    // store chart in repository
                    charts[chartName] = qosChart;
                    // assign custom properties for instance
                    charts[chartName].qosName = chartName;
                    charts[chartName].qosExtremes = {
                        xAxis : {
                            min : startDate,
                            max : endDate
                        }
                    };
                    charts[chartName].triggeredManually = true;
                    charts[chartName].xAxis[0].setExtremes(startDate, endDate);
                    charts[chartName].triggeredManually = false;
                    charts[chartName].qosExportStatus = false;
                    charts[chartName].qosCaptionEnabled = isCaptionsEnabled;
                    // assign custom properties for series
                    var seriesObj = charts[chartName]
                            .get(masterSeries['seriesKey']);
                    seriesObj['dataShift'] = shift;
                    seriesObj['qosDataRequest'] = qosDataRequest;

                    // load other series
                    qosChartModule.addAllSecondaryBoolSeries(seriesData, chartName, startDate,
                        endDate, shift, lineType, isCaptionsEnabled, isMouseTrackingEnabled);
                });
		},

		createChart : function (chartType, chartName, lineType, seriesData,
                startDate, endDate, xTitle, yTitle, height,
                divElementId, isThresholdsEnabled, isCaptionsEnabled,
                autoscaling, isZoomEnabled, isMouseTrackingEnabled,
                isLegendEnabled) {
			this.clearIfExists(chartName);

			var masterSeries = seriesData.shift();

			var seriesKey = masterSeries['seriesKey'];

            var qosDataRequest = getSeriesRequest(masterSeries, 0, true);

			var masterSeriesName = this.getSeriesName(masterSeries);

			var configCopy = jQuery.extend(true, {}, qosConfigModule
					.getStandardChartConfig());

			dataLoaderModule
					.getJSONData(
							qosDataRequest,
							startDate,
							endDate,
							function(result) {
                                configCopy.legend.enabled = isLegendEnabled;

								configCopy.chart.renderTo = divElementId;
								configCopy.chart.height = height;
								configCopy.navigator.series.data = result.data;
								configCopy.tooltip = {
									formatter : function() {
										return qosChartModule.formatTooltip(this.points,
											this.x);
									}
								};
								configCopy.xAxis.title.text = xTitle;

								configCopy.xAxis.events = {
									afterSetExtremes : function(e) {
										qosChartModule.setExtremesMultiSeries(
												qosChart.qosName, e, false,
												this);
									}
								};
								configCopy.xAxis.dateTimeLabelFormats = qosChartModule
										.getDateTimeFormats();
								if (chartType == 'PERCENTAGE') {
									configCopy.yAxis.min = -0.5;
									configCopy.yAxis.max = 100;
									configCopy.yAxis.ordinal = false;
								}
								configCopy.yAxis.title.text = yTitle;
								configCopy.yAxis.offset = 45;
								configCopy.series[0].id = seriesKey;
								configCopy.series[0].name = masterSeriesName;
								configCopy.series[0].data = result.data;
								configCopy.series[0].type = lineType;
								if (!isZoomEnabled) {
									configCopy.chart.zoomType = false;
									configCopy.navigator.enabled = false;
                                    configCopy.scrollbar.enabled = false;
								}
                                configCopy.series[0].enableMouseTracking = isMouseTrackingEnabled;

								var qosChart = new Highcharts.StockChart(
										configCopy);

								var convertedData = qosChart.get(seriesKey).data;
								if (isCaptionsEnabled) {
									qosChartModule.addOnSeriesFlag(qosChart,
											seriesKey, convertedData,
											masterSeriesName);
								}

								// store chart in repository
								charts[chartName] = qosChart;
								// assign custom properties for instance
								charts[chartName].qosName = chartName;
								charts[chartName].qosExtremes = {
									xAxis : {
										min : startDate,
										max : endDate
									}
								};
								charts[chartName].triggeredManually = true;
								charts[chartName].xAxis[0].setExtremes(startDate, endDate);
								charts[chartName].triggeredManually = false;
								charts[chartName].qosExportStatus = false;
								charts[chartName].qosToolbarAutoscaling = autoscaling;
								charts[chartName].qosToolbarThresholds = isThresholdsEnabled;
								charts[chartName].qosCaptionEnabled = isCaptionsEnabled;
								// assign custom properties for series
								var seriesObj = charts[chartName]
										.get(seriesKey);
								seriesObj['qosDataRequest'] = qosDataRequest;
								seriesObj['yAxisMin'] = result.min;
								seriesObj['yAxisMax'] = result.max;

								qosChartModule.addThresholdLines(
										charts[chartName].series[0],
										masterSeries['warningThreshold'],
										masterSeries['criticalThreshold'],
										masterSeries['thresholdType'],
										isThresholdsEnabled,
                                        isCaptionsEnabled
                                );

								// load other series
								qosChartModule.addAllSecondaryParameterSeries(
                                        charts[chartName], lineType,
                                        seriesData, 0,
                                        startDate, endDate, true,
                                        isThresholdsEnabled, isCaptionsEnabled,
                                        autoscaling, isMouseTrackingEnabled);
								
								if (seriesData.length == 0) {
									// adjust yAxis for single series
									qosThresholdModule.adjustYaxisMinMax(
											qosChart,
											qosChart.qosToolbarAutoscaling,
											qosChart.qosToolbarThresholds);
									// fire event
									window.chartBuilt(chartName);
								}								
							});
		},

		createSingleSeriesChart : function(chartName, lineType, taskKey, paramName,
                 paramProperties, parameterDisplayFormat, seriesKey, startDate,
                 endDate, criticalThreshold, warningThreshold, thresholdType, xTitle,
                 yTitle, height, divElementId, isCaptionsEnabled, autoscalingEnabled,
                 isZoomEnabled, isMouseTrackingEnabled, isLegendEnabled) {
			this.clearIfExists(chartName);

			var qosDataRequest = {
				serviceURI : serviceURI,
				taskKey : taskKey,
				paramName : paramName,
				paramProperties : encodeURIComponent(paramProperties),
				useInterpolation : 1,
				autoscalingEnabled : true
			// must be always true
			};

			var configCopy = jQuery.extend(true, {}, qosConfigModule
					.getStandardChartConfig());

			dataLoaderModule
					.getJSONData(
							qosDataRequest,
							startDate,
							endDate,
							function(result) {

                                configCopy.legend.enabled = isLegendEnabled;

								configCopy.chart.renderTo = divElementId;
								configCopy.chart.height = height;
								configCopy.navigator.series.data = result.data;
								configCopy.tooltip = {
									formatter : function() {
										return qosChartModule.formatTooltip(this.points,
												this.x);
									}
								};
								configCopy.xAxis.title.text = xTitle;
								configCopy.xAxis.events = {
									afterSetExtremes : function(e) {
										qosChartModule.setExtremes(
												qosChart.qosName, e);
									}
								};
								configCopy.xAxis.dateTimeLabelFormats = qosChartModule
										.getDateTimeFormats();
								// configCopy.xAxis.minRange = minTimeInterval;
								configCopy.yAxis.title.text = yTitle;
								configCopy.yAxis.offset = 45;
								configCopy.plotOptions = {
									column : {
										pointPadding : 0,
										groupPadding : 0,
										pointWidth : 3,
										color : qosThresholdModule
												.getNormalColor()
									},
									line : {
										lineWidth : 3
									},
									series : {
										minPointLength : 3,
										//feature is disabled by setting 0
										turboThreshold : 0
									}
								};
								configCopy.series[0].id = seriesKey;
								configCopy.series[0].name = parameterDisplayFormat;
								
								var data = qosChartModule.preProcessData(result.data, warningThreshold,
										criticalThreshold, thresholdType);
								
								configCopy.series[0].data = data;
								configCopy.series[0].type = lineType;
								if (!isZoomEnabled) {
									configCopy.chart.zoomType = false;
									configCopy.navigator.enabled = false;
                                    configCopy.scrollbar.enabled = false;
								}
                                configCopy.series[0].enableMouseTracking = isMouseTrackingEnabled;

								var qosChart = new Highcharts.StockChart(
										configCopy);
								
								if (isCaptionsEnabled) {
									qosChartModule.addOnSeriesFlag(qosChart,
											seriesKey,
											data, parameterDisplayFormat);
								}

								// store chart in repository
								charts[chartName] = qosChart;
								// assign custom properties for instance
								charts[chartName].qosName = chartName;
								charts[chartName].qosExtremes = {
									xAxis : {
										min : startDate,
										max : endDate
									}
								};
								charts[chartName].triggeredManually = true;
								charts[chartName].xAxis[0].setExtremes(startDate, endDate);
								charts[chartName].triggeredManually = false;
								charts[chartName].qosExportStatus = false;
								charts[chartName].qosToolbarAutoscaling = autoscalingEnabled;
								charts[chartName].qosCaptionEnabled = isCaptionsEnabled;
								charts[chartName].qosDataRequest = qosDataRequest;
								charts[chartName].defaultStartDate = startDate;
								charts[chartName].defaultEndDate = endDate;
								// assign custom properties for series
								var seriesObj = charts[chartName]
										.get(seriesKey);
								seriesObj['criticalThreshold'] = criticalThreshold;
								seriesObj['warningThreshold'] = warningThreshold;
								seriesObj['thresholdType'] = thresholdType;
								seriesObj['yAxisMin'] = result.min;
								seriesObj['yAxisMax'] = result.max;

								qosThresholdModule.manageAutoscaling(chartName,
										true, autoscalingEnabled);
								window.chartBuilt(chartName);
							});
		},

		clearAll : function() {
			for ( var chartName in charts) {
				this.removeChartInstance(charts[chartName]);
				// remove from list
				delete charts[chartName];
			}
		},

		clearIfExists : function(chartName) {
			var existChart = this.findChartByName(chartName);
			if (existChart != null) {
				existChart.destroy();
				delete charts[chartName];
			}
		},

		manageCaptions : function(chartName, captionsEnabled, thresholdsEnabled) {
			var chart = qosChartModule.findChartByName(chartName);
			if (chart != null) {
				chart.qosCaptionEnabled = captionsEnabled;
				var series;
				for ( var i = 0; i < chart.series.length; i++) {
					series = chart.series[i];
					if (qosChartModule.isDataSeries(series)) {
						if (captionsEnabled) {
							qosChartModule
									.addOnSeriesFlag(chart, series.options.id,
											series.data, series.name);
						} else {
							qosChartModule.removeFlagFromSeries(chart,
									series.options.id);
						}
                        qosThresholdModule.manageSeriesThresholdLabels(series, captionsEnabled, thresholdsEnabled);
					}
				}
                window.chartCaptionsOptionStateChanged(chartName, captionsEnabled);
			}
		},

		exportChart : function(chartName, chartDisplayName) {
			var chart = qosChartModule.findChartByName(chartName);
			chart.qosExportStatus = true;
			if (chart != null) {
				chart.setTitle({
					text : chartDisplayName
				});
				chart.exportChart();
				chart.setTitle({
					text : null
				});
			}
			chart.qosExportStatus = false;
		},

		findChartByName : function(chartName) {
			var foundChart = null;
			if (charts[chartName] !== undefined) {
				foundChart = charts[chartName];
			}
			return foundChart;
		},

		formatTooltip : function(points, x, isBoolean) {
			var s;
			var decimalPrecision = qosConfigModule.getStandardChartConfig().tooltip.valueDecimals;
			s = this.getFormattedDateTime(x);

			if (points === undefined)
				return false;

			$.each(points, function(i, point) {
				s += '<br/><span style="color: ' + this.series.color + '">'
						+ this.series.name + ':</span>';

				if (isBoolean) {
					if (point.y % 2 == 0) {
						s += Highcharts.qosMessages.noMessage;
					} else {
						s += Highcharts.qosMessages.yesMessage;
					}
				}
				else {
					s += Highcharts.numberFormat(point.y, decimalPrecision);
				}
			});
			return s;
		},

		getBoolDashStyles : function() {
			return [ 'Solid', 'LongDashDotDot', 'LongDash', 'Dot', 'ShortDot',
					'DashDot', 'ShortDash', 'ShortDashDotDot', 'ShortDashDot',
					'LongDashDot', 'Dash' ];
		},

		getDataSeriesCount : function(chart) {
			var count = 0;
			var length = chart.series.length;

			for ( var i = 0; i < length; i++) {
				if (this.isDataSeries(chart.series[i])) {
					count++;
				}
			}
			return count;
		},

		getDataSeriesXaxis : function(chart) {
			var xAxis = null;
			var dataSeriesIds = qosChartModule.getDataSeriesIds(chart);
			if (dataSeriesIds.length > 0) {
				xAxis = chart.get(dataSeriesIds[0]).xAxis;
			}
			return xAxis;
		},

		getDataSeriesIds : function(chart) {
			var ids = [];
			var length = chart.series.length;

			for ( var i = 0; i < length; i++) {
				if (this.isDataSeries(chart.series[i])) {
					ids.push(chart.series[i].options.id);
				}
			}
			return ids;
		},

		getExtremes : function(chartName, axisName) {
			var chart = qosChartModule.findChartByName(chartName);
			if (chart == null)
				return null;

			var extremes;
			if (axisName == "X") {
				extremes = chart.xAxis[0].getExtremes();
			} else if (axisName == "Y") {
				extremes = chart.yAxis[0].getExtremes();
			} else {
				return null;
			}
			var result = [];
			result.push(extremes.min);
			result.push(extremes.max);

			return result;
		},

		getSeriesName : function(series) {
			return series['agentName'] + " " + series['taskDisplayName'] + " "
					+ series['parameterDisplayFormat'];
		},

		getDateTimeFormats : function(locale) {
			var dateTimeLabelFormat = {};

			dateTimeLabelFormat.hour = Highcharts.dateFormats.shortTimeFormat;
			dateTimeLabelFormat.minute = Highcharts.dateFormats.shortTimeFormat;
			dateTimeLabelFormat.second = Highcharts.dateFormats.fullTimeFormat;

			return dateTimeLabelFormat;
		},

		getFormattedDateTime : function(value) {
			var format = Highcharts.dateFormats.dateTimeFormat;

			return Highcharts.dateFormat(format, value);
		},

		getVisibleSeriesKeys : function(chartName) {
			var chart = qosChartModule.findChartByName(chartName);
			if (chart == null)
				return null;

			var result = [];
			var dataSeriesIds = this.getDataSeriesIds(chart);
			for ( var i = 0; i < dataSeriesIds.length; i++) {
				if (chart.get(dataSeriesIds[i]).visible) {
					result.push(dataSeriesIds[i]);
				}
			}
			return result;
		},

		isTimeIntervalAllowed : function(startDate, endDate) {
			if ((endDate - startDate) < minTimeInterval) {
				return false;
			}
			return true;
		},

		isDataSeries : function(series) {
			if ((series.name == 'Navigator') || (series.type == 'flags')) {
				return false;
			}
			return true;
		},

		isChartHaveDataSeries : function(chart) {
			var result = false;
			var length = chart.series.length;
			for ( var i = 0; i < length; i++) {
				if (this.isDataSeries(chart.series[i])) {
					result = true;
					break;
				}
			}
			return result;
		},

		isNavigatorNeedsToBeUpdated : function(chart, newMin, newMax) {
			var result = false;

			// In current version it is possible to move chart on 1 second outside of data.
			// That is why "+-1" is needed here
			var oldMin = chart.qosExtremes.xAxis.min - 1;
			var oldMax = chart.qosExtremes.xAxis.max + 1;

			if ((newMin < oldMin) || (newMax > oldMax)) {
				chart.qosExtremes.xAxis.min = newMin;
				chart.qosExtremes.xAxis.max = newMax;
				result = true;
			}
			return result;
		},

		removeChartInstance : function(chartInstance) {
			var divElementId = $(chartInstance.container).parent().attr("id");
			// destroy chart to free memory
			chartInstance.destroy();
			// remove div container
			$('#' + divElementId).remove();
		},

		preProcessData : function(data, warningThreshold,
				criticalThreshold, thresholdType) {
			var seriesData = [];
			for (var i = 0; i < data.length; ++i) {
				var colorLabel = qosThresholdModule.applyThresholdToValue(
						data[i][1], warningThreshold, criticalThreshold,
						thresholdType);
				var resultColor = qosThresholdModule
						.getColorByLabel(colorLabel);
				seriesData[i] = {
						x : data[i][0],
						y : data[i][1],
						color : resultColor
				};
			}
			return seriesData;
		},
		
		removeSeries : function(chartName, seriesKey) {
			var removeChartInstance = false;
			var foundChart = this.findChartByName(chartName);
			if (foundChart != null) {
				var series = foundChart.get(seriesKey);
				if (series != null) {
					// remove plot lines
					series.yAxis.removePlotLine(warningPlotLinePrefix
							+ seriesKey);
					series.yAxis.removePlotLine(criticalPlotLinePrefix
							+ seriesKey);
					// remove series
					series.remove();
				}
				var flagSeries = foundChart.get(flagSeriesPrefix + seriesKey);
				if (flagSeries != null) {
					flagSeries.remove();
				}

				if (!this.isChartHaveDataSeries(foundChart)) {
					removeChartInstance = true;
					this.removeChartInstance(foundChart);
					// remove from list
					delete charts[chartName];
				}
			}
			return removeChartInstance;
		},

		renameChart : function(oldName, newName) {
			if (this.findChartByName(newName) == null) {

				var foundChart = this.findChartByName(oldName);
				if (foundChart != null) {
					foundChart.qosName = newName;
					delete charts[oldName];
					charts[newName] = foundChart;
				}
			}
		},

		setMinTimeInterval : function(milliseconds) {
			minTimeInterval = milliseconds;
		},

		setExtremes : function(chartName, e) {
			var chart = qosChartModule.findChartByName(chartName);

			// workaround for export to image
			if (chart.qosExportStatus === true) {
				return;
			}

			var currentMin = Math.round(chart.xAxis[0].getExtremes().min);
			var currentMax = Math.round(chart.xAxis[0].getExtremes().max);

			if (isNaN(currentMin) || isNaN(currentMax)) {
				currentMin = chart['defaultStartDate'];
				currentMax = chart['defaultEndDate'];

				alert("Default time interval is loaded");
			}
			var startDate = Math.round(e.min);
			var endDate = Math.round(e.max);
			if (!this.isTimeIntervalAllowed(startDate, endDate)) {
				endDate = startDate + minTimeInterval;
				currentMax = currentMin + minTimeInterval;
				window.printIncorrectTimeIntervalMessage(minTimeInterval);
			}

			if (!chart.triggeredManually) {

				chart.series[0].setData([]);

				dataLoaderModule.getJSONData(chart.qosDataRequest, startDate,
						endDate, function(result) {

							chart.series[0]['yAxisMin'] = result.min;
							chart.series[0]['yAxisMax'] = result.max;

							chart.triggeredManually = true;
							chart.xAxis[0].setExtremes(currentMin, currentMax);
							chart.triggeredManually = false;
							
							var data = qosChartModule.preProcessData(result.data, chart.series[0]['warningThreshold'],
									chart.series[0]['criticalThreshold'],
									chart.series[0]['thresholdType']);
							chart.series[0].setData(data);

							if (chart.qosToolbarAutoscaling) {
								qosThresholdModule.adjustYaxisMinMax(chart, chart.qosToolbarAutoscaling, false);
							}
							if (qosChartModule.isNavigatorNeedsToBeUpdated(
									chart, currentMin, currentMax)) {
								qosChartModule.updateNavigatorData(chart,
										result.data);
							}
						});
				window.timeIntervalChanged(chartName, startDate, endDate);
			}
		},

		setExtremesMultiSeries : function(chartName, e, isBoolChart, axis) {
			var chart = qosChartModule.findChartByName(chartName);

			// workaround for export to image
			if (chart.qosExportStatus === true) {
				return;
			}

			var currentMin = Math.round(chart.xAxis[0].getExtremes().min);
			var currentMax = Math.round(chart.xAxis[0].getExtremes().max);

			var startDate = Math.round(e.min);
			var endDate = Math.round(e.max);

			if (!this.isTimeIntervalAllowed(startDate, endDate)) {
				endDate = startDate + minTimeInterval;
				currentMax = currentMin + minTimeInterval;
				window.printIncorrectTimeIntervalMessage(minTimeInterval);
			}

			var createResultCallback = function(series) {

				function onAllSeriesLoaded() {
					chart.triggeredManually = true;
					axis.setExtremes(currentMin, currentMax);
					chart.triggeredManually = false;

					var firstSeriesData;
					for ( var j in allSeriesData) {
						firstSeriesData = allSeriesData[j];
						//set data with redraw
						chart.get(j).setData(allSeriesData[j]);
					}

					if (qosChartModule.isNavigatorNeedsToBeUpdated(chart,
							currentMin, currentMax)) {
						qosChartModule.updateNavigatorData(chart,
								firstSeriesData);
					}
					//don't call redraw due to high performance cost
					//chart.redraw();
					
					if (chart.qosCaptionEnabled) {
						qosChartModule.repaintCaptions(chart);
					}
					qosThresholdModule.adjustYaxisMinMax(chart,
							chart.qosToolbarAutoscaling,
							chart.qosToolbarThresholds);
				}

				return function resultCallback(result) {
					counter++;

					var seriesId = series.options.id;
					var isRange = series.type == 'arearange';

					if (isBoolChart) {
						result.data = qosChartModule.addBoolDataShift(
								series['dataShift'], result.data, isRange);
					}
					allSeriesData[seriesId] = result.data;
					series['yAxisMin'] = result.min;
					series['yAxisMax'] = result.max;

					if (counter == n) {
						onAllSeriesLoaded();
					}
				}
			};

			if (!chart.triggeredManually) {
				var n = qosChartModule.getDataSeriesCount(chart);
				var counter = 0;
				var allSeriesData = {};

				var dataSeriesIds = qosChartModule.getDataSeriesIds(chart);
				$.each(dataSeriesIds, function(i, seriesId) {
					var series = chart.get(seriesId);
					dataLoaderModule.getJSONData(series.qosDataRequest,
							startDate, endDate, createResultCallback(series));
				});
				window.timeIntervalChanged(chartName, startDate, endDate);
			}
		},
		
		setMouseTrackingEnabled : function(chartName, mouseTrackingEnabled) {
			var foundChart = this.findChartByName(chartName);
			var dataSeriesIds = this.getDataSeriesIds(foundChart);
			var series;
			for ( var i = 0; i < dataSeriesIds.length; i++) {
				series = foundChart.get(dataSeriesIds[i]);
				this.updateSeriesOptions(series, {
					enableMouseTracking : mouseTrackingEnabled
				});
			}
		},

		repaintCaptions : function(chart) {
			var seriesIds = qosChartModule.getDataSeriesIds(chart);

			for ( var i = 0; i < seriesIds.length; i++) {
				var flagSeries = chart.get(flagSeriesPrefix + seriesIds[i]);
				var dataSeries = chart.get(seriesIds[i]);
				if (flagSeries != null) {
					flagSeries.remove();
				}

				qosChartModule.addOnSeriesFlag(chart, seriesIds[i],
						dataSeries.data, dataSeries.name);
			}
		},

		setSize : function(chartName, width, height) {
			var foundChart = this.findChartByName(chartName);
			foundChart.setSize(width, height);
		},

		undoZoom : function(chartName) {
			var foundChart = this.findChartByName(chartName);
			var xAxis = qosChartModule.getDataSeriesXaxis(foundChart);
			if (xAxis != null) {
				var extremes = xAxis.getExtremes();
				var interval = extremes.max - extremes.min;
				var oldMin = foundChart.qosExtremes.xAxis.min;
				var oldMax = foundChart.qosExtremes.xAxis.max;

				if ((interval !== undefined) && (interval > 0)) {
					var newMin = extremes.min - interval;
					var newMax = extremes.max + interval;
					if ((newMin > oldMin) && (newMax < oldMax)) {
						xAxis.setExtremes(newMin, newMax);
					} else if ((extremes.min != oldMin)
							&& (extremes.max != oldMax)) {
						xAxis.setExtremes(oldMin, oldMax);
					}
				}
			}
		},

		updateNavigatorData : function(chart, data) {
			var navigatorSeries = null;
			for ( var i = 0; i < chart.series.length; i++) {
				if (chart.series[i].name == 'Navigator') {
					navigatorSeries = chart.series[i];
				}
			}
			if (navigatorSeries != null) {
				navigatorSeries.setData(data);
			}
		},
		
		updateSeriesOptions : function(series, options) {
			// save info before update
			var warningThreshold = series['warningThreshold'];
			var criticalThreshold = series['criticalThreshold'];
			var thresholdType = series['thresholdType'];
			var yAxisMin = series['yAxisMin'];
			var yAxisMax = series['yAxisMax'];
			var qosDataRequest = series['qosDataRequest'];
			var	thresholdLines = series['thresholdLines'];
			var dataShift = series['dataShift'];
			// do update
			series.update(options);
			// set info to new series
			series['warningThreshold'] = warningThreshold;
			series['criticalThreshold'] = criticalThreshold;
			series['thresholdType'] = thresholdType;
			series['yAxisMin'] = yAxisMin;
			series['yAxisMax'] = yAxisMax;
			series['qosDataRequest'] = qosDataRequest;
			series['thresholdLines'] = thresholdLines;
			series['dataShift'] = dataShift;
		},

		updateTimeZoneParameters : function(chart, timeZone, timeZoneType) {
			var chartDataRequest = chart.qosDataRequest;
			if (chartDataRequest !== undefined) {				
				chartDataRequest['timezone'] = encodeURIComponent(timeZone);
				chartDataRequest['timeZoneType'] = timeZoneType;
			} else {
				var length = chart.series.length;
				for ( var i = 0; i < length; i++) {
					var series = chart.series[i];
					if (this.isDataSeries(series)) {
						var qosDataRequest = series.qosDataRequest;
						qosDataRequest['timezone'] = encodeURIComponent(timeZone);
						qosDataRequest['timeZoneType'] = timeZoneType;
					}
				}
			}
		},

		zoomChart : function(chartName, startDate, endDate, timeZone,
				timeZoneType) {
			var foundChart = this.findChartByName(chartName);
			var xAxis = qosChartModule.getDataSeriesXaxis(foundChart);
			if (xAxis != null) {
				qosChartModule.updateTimeZoneParameters(foundChart, timeZone,
						timeZoneType);
				xAxis.setExtremes(startDate, endDate);
			}
		}
	}

}());