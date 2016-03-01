
var qosConfigModule = (function() {
	
	var standartChartConfig = {

			chart : {
				renderTo : null,
				zoomType : 'x',
				height : null
			},

			exporting : {
				buttons : {
					contextButton : {
						enabled : false
					}
				},
				url : 'svgExporting',
				sourceWidth : 1000,
				sourceHeight : 500
			},

			tooltip : {
				valueDecimals : 5
			},

			navigator : {
				adaptToUpdatedData : false,
				series : {
					data : null
				},
				enabled : true
			},

			scrollbar : {
				liveRedraw : false
			},

			legend : {
				enabled : true,
				align : 'center',
				layout : 'vertical',
				verticalAlign : 'bottom',
				floating : false
			},

			rangeSelector : {
				inputEnabled : false,
				enabled : false
			},

			xAxis : {
				title : {
					text : null
				},
				events : null,
				showFirstLabel : true,
				showLastLabel : true,
				ordinal : false
			},

			yAxis : {
				title : {
					text : null
				}
			},

			credits : {
				enabled : false
			},

			plotOptions : {
				series : {
					events : {
						hide : function(event) {
							qosThresholdModule.manageSeriesThresholdLines(this,
									false, true);
							qosThresholdModule.adjustYaxisMinMax(this.chart,
									this.chart.qosToolbarAutoscaling,
									this.chart.qosToolbarThresholds);
						},
						show : function(event) {
							qosThresholdModule.manageSeriesThresholdLines(this,
									true, true);
							qosThresholdModule.adjustYaxisMinMax(this.chart,
									this.chart.qosToolbarAutoscaling,
									this.chart.qosToolbarThresholds);
						}
					}
				}
			},

			series : [ {
				id : null,
				name : null,
				data : null,
				type : null,
				dataGrouping : {
					enabled : false
				}
			} ]
		};
	
	return {
		getStandardChartConfig : function() {
			return standartChartConfig;
		}
	}
	
}());
