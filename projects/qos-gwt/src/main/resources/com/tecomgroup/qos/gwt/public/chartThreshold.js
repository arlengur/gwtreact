var qosThresholdModule = (function() {

	var colors = {
		critical : 'red',
		warning : '#e89600',
		normal : 'green'
	};

	return {

		acceptThreshold : function(value, threshold, thresholdType) {

			if (thresholdType == '<') {
				return value < threshold;
			} else if (thresholdType == '>') {
				return value > threshold;
			} else if (thresholdType == '=') {
				return value == threshold;
			} else if (thresholdType == '<=') {
				return value <= threshold;
			} else if (thresholdType == '>=') {
				return value >= threshold;
			} else if (thresholdType == '!=') {
				return value != threshold;
			}

			return false;
		},

		adjustYaxisMinMax : function(chart, isAutoscalingEnabled, isThresholdsEnabled) {
			if (isAutoscalingEnabled === undefined && isThresholdsEnabled === undefined) {
				return;
			}

			var extremes = this.findYaxisExtremesIncludingThresholdLines(chart.qosName);
			if (extremes == null) {
				return;
			}

			var dataSeriesIds = qosChartModule.getDataSeriesIds(chart);
			var yAxis = chart.get(dataSeriesIds[0]).yAxis;

			var min, minPad, max, maxPad, padding;

			if (extremes.dataMin != undefined && extremes.dataMax != undefined) {
				// if the thresholds are enabled, the min and max choose based thresholds
				if (isThresholdsEnabled) {
					if (extremes.min != undefined && extremes.max != undefined) {
						max = extremes.dataMax > extremes.max ? extremes.dataMax : extremes.max;
						min = extremes.dataMin > extremes.min ? extremes.min : extremes.dataMin;
					}
				// if the thresholds are turned off, the min and max choose from results
				} else {
					max = extremes.dataMax;
					min = extremes.dataMin;
				}
			}
			if (min != undefined && max != undefined) {
				if (isAutoscalingEnabled) {
					padding = (max - min) * 0.10;
					minPad = min - padding;
					maxPad = max + padding;
					// if the graph above the axis X, and the minimum value of the Y-axis can not be negative
					if (min > 0 && max > 0 && minPad < 0) {
						minPad = 0;
					}
					// if the graph below the axis X, the maximum value of the Y-axis can not be positive
					if (max < 0 && min < 0 && maxPad > 0) {
						maxPad = 0;
					}
				} else {
					// graphics should be built so that the screen is always present axis X
					if (min > 0 && max > 0) {
						padding = max * 0.10;
						minPad = 0;
						maxPad = max + padding;
					} else if (min < 0 && max < 0) {
						padding = -min * 0.10;
						minPad = min - padding;
						maxPad = 0;
					} else {
						padding = (max - min) * 0.10;
						minPad = min - padding;
						maxPad = max + padding;
					}
				}
				// check trivial cases
				if (minPad == maxPad && maxPad == 0) {
					yAxis.setExtremes(minPad, 0.5, true);
				} else if (minPad == maxPad) {
					yAxis.setExtremes(minPad * 0.9, maxPad * 1.1, true);
				} else {
					yAxis.setExtremes(minPad, maxPad, true);
				}
			}
		},

		applyThresholdToValue : function(doubleValue, warningThreshold,
				criticalThreshold, thresholdType) {
			var resultLabel = "NORMAL";

			if ((criticalThreshold != null) && (warningThreshold != null)) {
				// If two thresholds exist
				if (qosThresholdModule.acceptThreshold(doubleValue,
						criticalThreshold, thresholdType)) {
					resultLabel = "CRITICAL";
				} else if (qosThresholdModule.acceptThreshold(doubleValue,
						warningThreshold, thresholdType)) {
					resultLabel = "WARNING";
				} else {
					resultLabel = "NORMAL";
				}
			} else if (criticalThreshold != null) {
				// If only critical threshold exists
				if (qosThresholdModule.acceptThreshold(doubleValue,
						criticalThreshold, thresholdType)) {
					resultLabel = "CRITICAL";
				} else {
					resultLabel = "NORMAL";
				}
			} else if (warningThreshold != null) {
				// If only warning threshold exists
				if (qosThresholdModule.acceptThreshold(doubleValue,
						warningThreshold, thresholdType)) {
					resultLabel = "WARNING";
				} else {
					resultLabel = "NORMAL";
				}
			} else {
				// paint default color
			}
			return resultLabel;
		},

		manageAutoscaling : function(chartName, singleSeriesChart, autoscalingEnabled) {
			var chart = qosChartModule.findChartByName(chartName);
			if (chart != null) {
				chart.qosToolbarAutoscaling = autoscalingEnabled;
				qosThresholdModule.adjustYaxisMinMax(chart,	chart.qosToolbarAutoscaling, chart.qosToolbarThresholds);
				window.chartAutoscalingOptionStateChanged(chartName, autoscalingEnabled);
			}
		},

		manageChartThresholdLines : function(chartName, thresholdsEnabled) {
			var chart = qosChartModule.findChartByName(chartName);
			if (chart != null) {
				chart.qosToolbarThresholds = thresholdsEnabled;
				for ( var i = 0; i < chart.series.length; i++) {
					qosThresholdModule.manageSeriesThresholdLines(
							chart.series[i], thresholdsEnabled);
				}
				qosThresholdModule
						.adjustYaxisMinMax(chart, chart.qosToolbarAutoscaling,
								chart.qosToolbarThresholds);

				window.chartThresholdsOptionStateChanged(chartName,
						thresholdsEnabled);
			}
		},

		manageSeriesThresholdLines : function(series, thresholdsEnabled,
				considerGlobalThresholdsState) {
			if (considerGlobalThresholdsState
					&& !series.chart.qosToolbarThresholds) {
				return;
			}
			if (series.thresholdLines !== undefined) {
				var thresholdLine;
				for ( var i = 0; i < series.thresholdLines.length; i++) {
					thresholdLine = series.thresholdLines[i];
					if (thresholdsEnabled) {
						series.yAxis.addPlotLine(thresholdLine);
					} else {
						series.yAxis.removePlotLine(thresholdLine.id);
					}
				}
			}
		},

        manageSeriesThresholdLabels : function(series, captionsEnabled, thresholdsEnabled) {
            if(series.thresholdLines != null) {
                for (var i = 0; i < series.thresholdLines.length; i++) {
                    var thresholdLine = series.thresholdLines[i];
                    if (captionsEnabled) {
                        thresholdLine.label =
                            qosThresholdModule.getThresholdLabel(
                                thresholdLine.type, series.options.name)
                    } else {
                        thresholdLine.label = {}
                    }
                    if(thresholdsEnabled) {
                        series.yAxis.removePlotLine(thresholdLine.id);
                        series.yAxis.addPlotLine(thresholdLine);
                    }
                }
            }
        },

		// return hash (with min, max, dataMin, dataMax) or null
		findYaxisExtremesIncludingThresholdLines : function(chartName) {
			var chart = qosChartModule.findChartByName(chartName);
			var dataSeriesIds = qosChartModule.getVisibleSeriesKeys(chartName);

			if (dataSeriesIds.length == 0) {
				return null;
			}

			var extremes = {
				min : undefined,
				max : undefined,
				dataMin : undefined,
				dataMax : undefined
			};
			var curExtremes;
			var plotLines;
			var serverMin;
			var serverMax;

			for ( var i = 0; i < dataSeriesIds.length; i++) {
				// At first, find extremes from Highstock
				curExtremes = chart.get(dataSeriesIds[i]).yAxis.getExtremes();
				// Next, change this extremes to our extremes if last exists.
				// This is workaround for area type of chart. This chart found
				// min=0 if series containts Null values
				serverMin = chart.get(dataSeriesIds[i])['yAxisMin'];
				serverMax = chart.get(dataSeriesIds[i])['yAxisMax'];
				if ((serverMin !== undefined) && (serverMax !== undefined)) {
					curExtremes.dataMin = serverMin;
					curExtremes.dataMax = serverMax;
				}
				if (curExtremes.dataMin !== undefined) {
					if (extremes.dataMin === undefined || curExtremes.dataMin < extremes.dataMin) {
						extremes.dataMin = curExtremes.dataMin;
					}
				}
				if (curExtremes.dataMax !== undefined) {
					if (extremes.dataMax === undefined || curExtremes.dataMax > extremes.dataMax) {
						extremes.dataMax = curExtremes.dataMax;
					}
				}
				plotLines = chart.get(dataSeriesIds[i]).thresholdLines;
				if (plotLines !== undefined) {
					for ( var j = 0; j < plotLines.length; j++) {
						if (extremes.min === undefined || parseFloat(plotLines[j].value) < extremes.min) {
							extremes.min = parseFloat(plotLines[j].value);
						}
						if (extremes.max === undefined || parseFloat(plotLines[j].value) > extremes.max) {
							extremes.max = parseFloat(plotLines[j].value);
						}
					}
				}
			}
			return extremes;
		},

		getColorByLabel : function(colorLabel) {
			var resultColor;

			switch (colorLabel) {
			case "CRITICAL":
				resultColor = qosThresholdModule.getCriticalColor();
				break;
			case "WARNING":
				resultColor = qosThresholdModule.getWarningColor();
				break;
			case "NORMAL":
				resultColor = qosThresholdModule.getNormalColor();
				break;
			default:
				resultColor = qosThresholdModule.getNormalColor();
			}
			return resultColor;
		},

		getCriticalColor : function() {
			return colors.critical;
		},

		getWarningColor : function() {
			return colors.warning;
		},

		getNormalColor : function() {
			return colors.normal;
		},

		toBoolThreshold : function(series, isRange) {
			var singleThreshold = {
					color : "blue",
					negativeColor : "gray",
					threshold : (isRange) ? 1 : 0.5
				};			
			
			if (series.criticalThreshold){
				var threshold = parseInt(series.criticalThreshold);
				if (1 === threshold) {
					singleThreshold.color = this.getCriticalColor();
					singleThreshold.negativeColor = this.getNormalColor();
				} else if (0 === threshold) {
					singleThreshold.color = this.getNormalColor();
					singleThreshold.negativeColor = this.getCriticalColor();
				}
			} else if (series.warningThreshold) {
				var threshold = parseInt(series.warningThreshold); 				
				if (1 === threshold) {
					singleThreshold.color = this.getWarningColor();
					singleThreshold.negativeColor = this.getNormalColor();
				} else if (0 === threshold) {
					singleThreshold.color = this.getNormalColor();
					singleThreshold.negativeColor = this.getWarningColor();
				}
			}else{
				window.printDefaultThresholdColorsMessage();
			}	

			return singleThreshold;
		},

        getThresholdLabel : function(levelName, seriesName) {
            return {
                text: levelName + ' level of ' + seriesName
            }
        }
	}
}());