var dataLoaderModule = (function() {

	return {
		getJSONData : function(qosDataRequest, startDate, endDate, callback) {
			$.getJSON(
					qosDataRequest.serviceURI + '?taskKey='
							+ qosDataRequest.taskKey + '&parameterName='
							+ qosDataRequest.paramName
							+ '&parameterProperties='
							+ qosDataRequest.paramProperties + '&startDate='
							+ startDate + '&endDate=' + endDate
							+ '&useInterpolation='
							+ qosDataRequest.useInterpolation
							+ '&useAutoscaling='
							+ qosDataRequest.autoscalingEnabled,
					function(data) {
						callback(data);
					}).fail(
					function(jqXHR) {
						window.printMessage(jqXHR.status + " error: "
								+ jqXHR.statusText);
					});
		}
	}
}());