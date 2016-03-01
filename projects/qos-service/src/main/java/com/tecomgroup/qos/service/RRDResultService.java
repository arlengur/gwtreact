/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.Sample;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.tecomgroup.qos.ExportResultsWrapper;
import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.communication.result.Result;
import com.tecomgroup.qos.communication.result.Result.ResultIdentifier;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MProperty;
import com.tecomgroup.qos.domain.MProperty.PropertyType;
import com.tecomgroup.qos.domain.MResultConfiguration;
import com.tecomgroup.qos.domain.MResultConfigurationTemplate;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.event.ResultEvent;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.exception.SourceNotFoundException;
import com.tecomgroup.qos.util.RrdUtil;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author abondin
 * 
 */
public class RRDResultService extends AbstractService
		implements
			ResultService,
			InitializingBean {

	public static final DateFormat LONG_DATE_FORMAT = DateFormat
			.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);

	private static Logger LOGGER = Logger.getLogger(RRDResultService.class);

	private static final String DEFAULT_DATASOURCE = "datasource";

	private ObjectReader exportResultsWrapperReader;

	private ObjectWriter exportResultsWrapperWriter;

	/**
	 * The property is used in
	 * {@link ResultServletHandler#setMaxResultsCount(Integer)}. Both classes
	 * must use the same property.
	 */
	private int maxResultsCount;

	/**
	 * The base of power-mode function to calculate aggregation step.
	 * 
	 * @See {@link RRDResultService#getAdaptiveAggregationStep(Long, Date, Date)}
	 *      {@link RrdUtil#calcAggregationFactor(double, int)}
	 *      {@link RrdUtil#calcAggregationStep(double, int)}
	 */
	private int aggregationBase;

	/**
	 * A number of days for store data in rrd
	 */
	private int storedDaysCount;

	@Autowired
	private StorageService storageService;

	@Autowired
	private PropertyUpdater propertyUpdater;

	@Autowired
	private InternalTaskService taskService;

	@Override
	public void addResults(final MAgentTask task, final SortedMap<ResultIdentifier, Result> results) {
		if (!results.isEmpty()) {
			if (task.isDeleted()) {
				throw new SourceNotFoundException("Task with key " + task.getKey() + " not found. Possibly it was already deleted.");
			}
			final MResultConfigurationTemplate resultConfigurationTemplate = task.getModule().getTemplateResultConfiguration();
			final Set<String> taskParameterNames = resultConfigurationTemplate.getParameterNames();

			final Date startDateTime = findTheEarliestDateTime(results.keySet());
			for (final Map.Entry<ResultIdentifier, Result> resultEntry : results.entrySet()) {
				final ResultIdentifier resultIdentifier = resultEntry.getKey();
				final Result result = resultEntry.getValue();
				final List<MProperty> modelProperties = result.getModelProperties(resultConfigurationTemplate);
				for (final Map.Entry<String, Double> resultParameterEntry : result.getParameters().entrySet()) {
					final String parameterName = resultParameterEntry.getKey();
					if (taskParameterNames.contains(parameterName)) {
						final ParameterIdentifier parameterIdentifier = new ParameterIdentifier(parameterName, modelProperties);
						if (task.hasParameter(parameterIdentifier)	|| task.hasTemplateParameter(parameterIdentifier)) {
							updateParameterProperties(task, parameterIdentifier);
							// dateTime should be taken from resultIdentifier.
							// It is important for interval results.
							writeResult(task, parameterIdentifier, resultIdentifier.getDateTime(), resultParameterEntry.getValue(),	startDateTime);
						} else {
							LOGGER.error("Result (timestamp=" + result.getResultDateTime()
									+ ") was not processed because its parameter configuration was not found: "
									+ parameterIdentifier);
						}
					}
				}
			}
			eventBroadcastDispatcher.broadcastWithoutTransaction(
					Arrays.asList(new ResultEvent(task.getModule().getAgent().getKey(),	task.getKey(), results.size())));
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.PROPERTY);
		exportResultsWrapperReader = objectMapper
				.reader(ExportResultsWrapper.class);
		exportResultsWrapperWriter = objectMapper
				.writerWithDefaultPrettyPrinter();
	}

	/**
	 * Convert time interval to sorting type. If sortType = ASC, then the data
	 * should be retrieved for the start of provided time interval.
	 * 
	 * @param timeInterval
	 * @param lastUpdateTime
	 * @param samplingRate
	 * @param aggregationStep
	 * @param startPosition
	 * @param size
	 * @return converted time interval
	 */
	private TimeInterval calculateAscTimeInterval(
			final TimeInterval timeInterval, final long lastUpdateTime,
			final long samplingRate, final long aggregationStep,
			final long startPosition, final long size) {
		long calculatedStartTimestamp = timeInterval.getStartDateTime()
				.getTime() / TimeConstants.MILLISECONDS_PER_SECOND;
		long calculatedEndTimestamp = timeInterval.getEndDateTime().getTime()
				/ TimeConstants.MILLISECONDS_PER_SECOND;

		// if requested start time is less than minimum stored time
		final long minStoredTime = getMinStoredTime(lastUpdateTime);
		if (calculatedStartTimestamp < minStoredTime) {
			calculatedStartTimestamp = minStoredTime;
		}

		// move start time to start position
		calculatedStartTimestamp += startPosition * aggregationStep
				* samplingRate;
		// calculate end time as start time moved to the size
		final long endTimestampCandidat = calculatedStartTimestamp + size
				* aggregationStep * samplingRate;
		// if new end time is less than provided end time then use it,
		// because the data is retrieved for the start of the time interval
		if (endTimestampCandidat < calculatedEndTimestamp) {
			calculatedEndTimestamp = endTimestampCandidat;
		}

		// if last update time is less than requested end time, then use last
		// update time.
		if (lastUpdateTime < calculatedEndTimestamp) {
			calculatedEndTimestamp = lastUpdateTime;
		}

		return TimeInterval
				.get(new Date(calculatedStartTimestamp
						* TimeConstants.MILLISECONDS_PER_SECOND), new Date(
						calculatedEndTimestamp
								* TimeConstants.MILLISECONDS_PER_SECOND));
	}

	/**
	 * Convert time interval to sorting type. If sortType = DESC, then the data
	 * should be retrieved for the end of provided time interval.
	 * 
	 * @param timeInterval
	 * @param lastUpdateTime
	 * @param samplingRate
	 * @param aggregationStep
	 * @param startPosition
	 * @param size
	 * @return converted time interval
	 */
	private TimeInterval calculateDescTimeInterval(
			final TimeInterval timeInterval, final long lastUpdateTime,
			final long samplingRate, final long aggregationStep,
			final long startPosition, final long size) {
		long calculatedStartTimestamp = timeInterval.getStartDateTime()
				.getTime() / TimeConstants.MILLISECONDS_PER_SECOND;
		long calculatedEndTimestamp = timeInterval.getEndDateTime().getTime()
				/ TimeConstants.MILLISECONDS_PER_SECOND;

		// if last update time is less than requested end time, then use last
		// update time.
		if (lastUpdateTime < calculatedEndTimestamp) {
			calculatedEndTimestamp = lastUpdateTime;
		}

		// move end time to start position, because the data is retrieved for
		// the end of the time interval
		calculatedEndTimestamp -= startPosition * aggregationStep
				* samplingRate;
		// calculate start time as end time moved to the size
		final long startTimestampCandidat = calculatedEndTimestamp - size
				* aggregationStep * samplingRate;
		// if new start time is greater than provided start time, then use it,
		// because the data is retrieved for the end of the time interval
		if (startTimestampCandidat > calculatedStartTimestamp) {
			calculatedStartTimestamp = startTimestampCandidat;
		}
		// if new start time is less than minimum stored time
		final long minStoredTime = getMinStoredTime(lastUpdateTime);
		if (calculatedStartTimestamp < minStoredTime) {
			calculatedStartTimestamp = minStoredTime;
		}

		return TimeInterval.get(timeInterval.getType(), new Date(
				calculatedStartTimestamp
						* TimeConstants.MILLISECONDS_PER_SECOND),
				new Date(calculatedEndTimestamp
						* TimeConstants.MILLISECONDS_PER_SECOND), timeInterval
						.getTimeZoneType(), timeInterval.getTimeZone(),
				timeInterval.getClientTimeZone());
	}

	/**
	 * Cast time interval to the aggregation step and samplingRate.
	 * 
	 * @param timeInterval
	 * @param samplingRate
	 * @param aggregationStep
	 * @return casted time interval
	 */
	private TimeInterval castTimeIntervalToAggregation(
			final TimeInterval timeInterval, final long samplingRate,
			final long aggregationStep) {
		// it is necessary to round left side of the interval to greater
		// number, because start time must be always greater than minimal
		// storage time
		final long castingDevisor = aggregationStep * samplingRate;
		final long castedStartTimestamp = (long) Math
				.ceil((timeInterval.getStartDateTime().getTime() / (double) (TimeConstants.MILLISECONDS_PER_SECOND * castingDevisor)))
				* castingDevisor;
		final long castedEndTimestamp = ((timeInterval.getEndDateTime()
				.getTime() / (TimeConstants.MILLISECONDS_PER_SECOND * castingDevisor)))
				* castingDevisor;

		return TimeInterval.get(timeInterval.getType(), new Date(
				castedStartTimestamp * TimeConstants.MILLISECONDS_PER_SECOND),
				new Date(castedEndTimestamp
						* TimeConstants.MILLISECONDS_PER_SECOND), timeInterval
						.getTimeZoneType(), timeInterval.getTimeZone(),
				timeInterval.getClientTimeZone());
	}

	@Override
	public ExportResultsWrapper deserializeBean(final String beanPayload) {
		ExportResultsWrapper bean = null;
		try {
			bean = exportResultsWrapperReader
					.<ExportResultsWrapper> readValue(beanPayload);
		} catch (final Exception ex) {
			throw new ServiceException(
					"Unable to deserialize export results wrapper: "
							+ beanPayload, ex);
		}
		return bean;
	}

	private Date findTheEarliestDateTime(
			final Set<ResultIdentifier> resultIdentifiers) {
		Date foundDateTime = new Date();
		if (resultIdentifiers != null && !resultIdentifiers.isEmpty()) {
			final ResultIdentifier minResultIdentifier = Collections.min(
					resultIdentifiers, new Comparator<ResultIdentifier>() {

						@Override
						public int compare(final ResultIdentifier source,
								final ResultIdentifier target) {
							int result = 0;
							if (source == null) {
								if (target != null) {
									result = -1;
								}
							} else {
								if (target == null) {
									result = 1;
								} else {
									result = source.getDateTime().compareTo(
											target.getDateTime());
								}
							}
							return result;
						}
					});
			foundDateTime = minResultIdentifier.getDateTime();
		}
		return foundDateTime;
	}

	/**
	 * Gets aggregation step dependent on
	 * {@link MResultConfiguration#getSamplingRate()}, {@link #maxResultsCount}
	 * and {@link #aggregationBase}.
	 * 
	 * @param samplingRate
	 * @param startDateTime
	 *            in seconds
	 * @param endDateTime
	 *            in seconds
	 * @return aggregation step
	 */
	private Long getAdaptiveAggregationStep(final long samplingRate,
			final long startDateTime, final long endDateTime) {
		final double timeDividend = Math.ceil((endDateTime - startDateTime)
				/ (double) (maxResultsCount * samplingRate));
		return RrdUtil.calcAggregationStep(timeDividend, aggregationBase);
	}

	private MAgentTask getAgentTaskByKey(final String taskKey) {
		MAgentTask task = null;

		task = taskService.getTaskFromCache(taskKey);

		if (task == null) {
			throw new SourceNotFoundException("Task with key " + taskKey
					+ " not found. Possibly it was already deleted.");
		}
		if (task.getResultConfiguration() == null) {
			taskService.validateTaskConfiguration(task);
		}

		return task;
	}

	@Override
	public List<Map<String, Object>> getLastResults(
			final Map<String, Collection<?>> taskParameters,
			final Long startPosition, final Long size, final OrderType orderType) {
		final List<Map<String, Object>> results = new ArrayList<>();

		for (final Map.Entry<String, Collection<?>> taskParameterEntry : taskParameters
				.entrySet()) {
			final MAgentTask task = getAgentTaskByKey(taskParameterEntry
					.getKey());
			final Collection<ParameterIdentifier> parameterIdentifiers = validateParameterIdentifiers(
					task, taskParameterEntry.getValue());
			for (final ParameterIdentifier parameterIdentifier : parameterIdentifiers) {
				final Long lastUpdateTime = getRRDLastUpdateTime(task,
						parameterIdentifier);
				final Date endDateTime = new Date(lastUpdateTime
						* TimeConstants.MILLISECONDS_PER_SECOND);
				final Long samplingRate = task.getResultConfiguration()
						.getSamplingRate();
				final Date startDateTime = new Date(endDateTime.getTime()
						- samplingRate * TimeConstants.MILLISECONDS_PER_SECOND);
				final TimeInterval timeInterval = TimeInterval.get(
						startDateTime, endDateTime);
				results.addAll(getResults(task.getKey(),
						Collections.singletonList(parameterIdentifier),
						1l, timeInterval, startPosition, size, orderType));
			}
		}

		return results;
	}

	/**
	 * Calculates mininum stored time.
	 * 
	 * @param lastUpdateTime
	 *            in seconds
	 * @return time in secods
	 */
	private long getMinStoredTime(final long lastUpdateTime) {
		return lastUpdateTime - storedDaysCount * TimeConstants.SECONDS_PER_DAY;
	}

	private TimeInterval getRealDataExistenceInterval(final MAgentTask task,
			final ParameterIdentifier parameterIdentifier) {
		final long lastUpdateTime = getRRDLastUpdateTime(task,
				parameterIdentifier);
		final long minStoredTime = getMinStoredTime(lastUpdateTime);
		return TimeInterval.get(new Date(minStoredTime
				* TimeConstants.MILLISECONDS_PER_SECOND), new Date(
				lastUpdateTime * TimeConstants.MILLISECONDS_PER_SECOND));
	}

	@Override
	public List<Map<String, Object>> getResults(
			final Map<String, Collection<?>> taskParameters,
			final Long aggregationStep, final TimeInterval timeInterval,
			final Long startPosition, final Long size, final OrderType orderType) {
		return getResults(taskParameters, aggregationStep, timeInterval,
				startPosition, size, orderType, true, true);
	}

	@Override
	public List<Map<String, Object>> getResults(
			final Map<String, Collection<?>> taskParameters,
			final Long aggregationStep, final TimeInterval timeInterval,
			final Long startPosition, final Long size,
			final OrderType orderType, final boolean addStartOfData,
			final boolean addEndOfData) {
		final List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
		final Map<String, Map<Date, Double>> currentResults = new HashMap<String, Map<Date, Double>>();
		Set<Date> resultTimes = null;
		if (orderType.equals(OrderType.DESC)) {
			resultTimes = new TreeSet<Date>(Collections.reverseOrder());
		} else {
			resultTimes = new TreeSet<Date>();
		}

		final Map<String, TimeInterval> realDataExistenceIntervals = new HashMap<>();
		for (final Map.Entry<String, Collection<?>> taskParameterEntry : taskParameters
				.entrySet()) {
			final String taskKey = taskParameterEntry.getKey();
			final MAgentTask task = getAgentTaskByKey(taskKey);

			final Collection<ParameterIdentifier> parameterIdentifiers = validateParameterIdentifiers(
					task, taskParameterEntry.getValue());
			for (final ParameterIdentifier parameterIdentifier : parameterIdentifiers) {
				Map<Date, Double> parameterResults = null;
				if (aggregationStep != null) {
					parameterResults = getResultsByAggregationStep(taskKey,
							parameterIdentifier, aggregationStep, timeInterval,
							startPosition, size, orderType, addStartOfData,
							addEndOfData);
				} else {
					parameterResults = getResultsUsingAdaptiveAggregationStep(
							taskKey, parameterIdentifier, timeInterval,
							orderType, addStartOfData, addEndOfData);
				}
				resultTimes.addAll(parameterResults.keySet());

				final String taskStorageKey = parameterIdentifier
						.createTaskStorageKey(taskKey);

				realDataExistenceIntervals
						.put(taskStorageKey,
								getRealDataExistenceInterval(task,
										parameterIdentifier));
				currentResults.put(taskStorageKey, parameterResults);
			}
			if (Thread.currentThread().isInterrupted()) {
				LOGGER.warn("RRD results collecting was interrupted.");
				return result;
			}
		}
		for (final Date resultTime : resultTimes) {
			final Map<String, Object> parametersMap = new HashMap<String, Object>();
			for (final Map.Entry<String, Map<Date, Double>> currentResultsEntry : currentResults
					.entrySet()) {
				final String taskStorageKey = currentResultsEntry.getKey();
				final Map<Date, Double> values = currentResultsEntry.getValue();
				// add NaNs only in the interval of real data existence
				Double value = values.get(resultTime);

				final TimeInterval dataExistenceInterval = realDataExistenceIntervals
						.get(taskStorageKey);
				if (value == null
						&& (dataExistenceInterval == null || dataExistenceInterval
								.isDateIncluded(resultTime))) {
					value = Double.NaN;
				}
				parametersMap.put(taskStorageKey, value);
			}
			parametersMap.put(SimpleUtils.DATE_PARAMETER_NAME, resultTime);
			result.add(parametersMap);
			if (Thread.currentThread().isInterrupted()) {
				LOGGER.warn("RRD results collecting was interrupted.");
				return result;
			}
		}

		return result;
	}

	@Override
	public List<Map<String, Object>> getResults(final String taskKey,
			final Collection<?> parameters, final Long aggregationStep,
			final TimeInterval timeInterval, final Long startPosition,
			final Long size, final OrderType orderType) {
		final Map<String, Collection<?>> taskParameters = new HashMap<String, Collection<?>>();
		taskParameters.put(taskKey, parameters);
		return getResults(taskParameters, aggregationStep, timeInterval,
				startPosition, size, orderType);
	}

	@Override
	public List<Map<String, Object>> getResults(final String taskKey,
			final Long aggregationStep, final TimeInterval timeInterval,
			final Long startPosition, final Long size, final OrderType orderType) {
		final Map<String, Collection<?>> taskParameters = new HashMap<String, Collection<?>>();
		taskParameters.put(taskKey, null);
		return getResults(taskParameters, aggregationStep, timeInterval,
				startPosition, size, orderType);
	}

	@Override
	public Map<Date, Double> getResults(final String taskKey,
			final ParameterIdentifier parameterIdentifier,
			final TimeInterval timeInterval, final OrderType orderType) {
		return getResultsUsingAdaptiveAggregationStep(taskKey,
				parameterIdentifier, timeInterval, orderType);
	}

	@Override
	public Map<Date, Double> getResultsByAggregationStep(final String taskKey,
			final ParameterIdentifier parameterIdentifier,
			final Long aggregationStep, final TimeInterval timeInterval,
			final Long startPosition, final Long size, final OrderType orderType) {
		return getResultsByAggregationStep(taskKey, parameterIdentifier,
				aggregationStep, timeInterval, startPosition, size, orderType,
				false, false);
	}

	private Map<Date, Double> getResultsByAggregationStep(final String taskKey,
			final ParameterIdentifier parameterIdentifier,
			final Long aggregationStep, final TimeInterval timeInterval,
			final Long startPosition, final Long size,
			final OrderType orderType, final boolean addStartOfDatea,
			final boolean addEndOfData) {
		final Map<Date, Double> results = new TreeMap<Date, Double>();

		final MAgentTask task = getAgentTaskByKey(taskKey);
		storageService.executeInRRD(task, parameterIdentifier, false, null,
				new StorageService.RrdRequest() {
					@Override
					public Object execute(final RrdDb rrdDb) {
						try {
							final Long samplingRate = task
									.getResultConfiguration().getSamplingRate();

							final long lastUpdateTime = rrdDb
									.getLastUpdateTime();

							TimeInterval calculatedTimeInterval = null;
							switch (orderType) {
								case DESC : {
									calculatedTimeInterval = calculateDescTimeInterval(
											timeInterval,
											lastUpdateTime, samplingRate,
											aggregationStep, startPosition,
											size - 1);
									break;
								}
								case ASC : {
									calculatedTimeInterval = calculateAscTimeInterval(
											timeInterval,
											lastUpdateTime, samplingRate,
											aggregationStep, startPosition,
											size - 1);
									break;
								}

								default : {
									throw new ServiceException(
											"Unsupproted order type: "
													+ orderType);
								}
							}

							// casting time interval should be done in the end
							// of converting and casting sequence, otherwise
							// wrong rrd archive will be matched and rrd will
							// return wrong values count from wrong archive
							final TimeInterval castedTimeInterval = castTimeIntervalToAggregation(
									calculatedTimeInterval, samplingRate,
									aggregationStep);

							final long calculatedStartTimestamp = castedTimeInterval
									.getStartDateTime().getTime()
									/ TimeConstants.MILLISECONDS_PER_SECOND;
							final long calculatedEndTimestamp = castedTimeInterval
									.getEndDateTime().getTime()
									/ TimeConstants.MILLISECONDS_PER_SECOND;

							if (addStartOfDatea) {
								final long minStoredTime = getMinStoredTime(lastUpdateTime);
								if (isMinStoredTimeReached(
										timeInterval.getStartDateTime(),
										minStoredTime)) {
									results.put(
											new Date(
													minStoredTime
															* TimeConstants.MILLISECONDS_PER_SECOND
															- TimeConstants.MILLISECONDS_PER_SECOND
															/ 2),
											RrdUtil.START_OF_DATA);
								}
							}
							if (calculatedStartTimestamp <= calculatedEndTimestamp) {
								// resolution is a real aggregation step in rrd
								// achive. it depends on samplingRate.
								final long resolution = aggregationStep
										* samplingRate;
								final FetchRequest request = rrdDb.createFetchRequest(
										ConsolFun.valueOf(task
												.getResultConfiguration()
												.findParameterConfiguration(
														parameterIdentifier)
												.getAggregationType()
												.toString()),
										calculatedStartTimestamp,
										calculatedEndTimestamp, resolution);

								int index = 0;

								if (request.fetchData().getRowCount() > 0) {
									final double[] values = request.fetchData()
											.getValues(rrdDb.getDatasource(0).getName());
									final long[] timestamps = request
											.fetchData().getTimestamps();
									for (final long timestamp : timestamps) {
										results.put(
												new Date(timestamp * TimeConstants.MILLISECONDS_PER_SECOND),
												values[index++]);
									}
								}
							}
							if (addEndOfData) {
								if (isLastUpdateTimeReached(
										timeInterval.getEndDateTime(),
										lastUpdateTime)) {
									results.put(
											new Date(
													lastUpdateTime
															* TimeConstants.MILLISECONDS_PER_SECOND
															+ TimeConstants.MILLISECONDS_PER_SECOND
															/ 2),
											RrdUtil.END_OF_DATA);
								}
							}
						} catch (final Exception ex) {
							final String errorMessage = "Unable to get results for taskKey = "
									+ taskKey
									+ " parameterIdentifier = "
									+ parameterIdentifier.toString()
									+ " located at "
									+ rrdDb.getPath()
									+ ": "
									+ ex.getMessage();
							LOGGER.error(errorMessage, ex);
							throw new ServiceException(errorMessage, ex);
						}
						return null;
					}
				});

		return results;
	}

	@Override
	public Map<Date, Double> getResultsUsingAdaptiveAggregationStep(
			final String taskKey,
			final ParameterIdentifier parameterIdentifier,
			final TimeInterval timeInterval, final OrderType orderType) {
		return getResultsUsingAdaptiveAggregationStep(taskKey,
				parameterIdentifier, timeInterval, orderType, false, false);
	}

	private Map<Date, Double> getResultsUsingAdaptiveAggregationStep(
			final String taskKey,
			final ParameterIdentifier parameterIdentifier,
			final TimeInterval timeInterval, final OrderType orderType,
			final boolean addStartOfData, final boolean addEndOfData) {
		final MAgentTask task = getAgentTaskByKey(taskKey);

		long calculatedStartTime = timeInterval.getStartDateTime()
				.getTime() / TimeConstants.MILLISECONDS_PER_SECOND;
		long calculatedEndTime = timeInterval.getEndDateTime()
				.getTime() / TimeConstants.MILLISECONDS_PER_SECOND;

		// if last update time is less than requested end time
		final long lastUpdateTime = getRRDLastUpdateTime(task,
				parameterIdentifier);
		if (lastUpdateTime < calculatedEndTime) {
			calculatedEndTime = lastUpdateTime;
		}

		// if requested start time is less than minimum stored time
		final long minStoredTime = getMinStoredTime(lastUpdateTime);
		if (calculatedStartTime < minStoredTime) {
			calculatedStartTime = minStoredTime;
		}

		final long samplingRate = task.getResultConfiguration()
				.getSamplingRate();

		final long aggregationStep = getAdaptiveAggregationStep(samplingRate,
				calculatedStartTime, calculatedEndTime);

		// the count of intervals with aggregationStep length in the provided
		// time interval.
		final long calculatedCountOfIntervals = (long) Math
				.ceil((calculatedEndTime - calculatedStartTime)
						/ ((double) aggregationStep * samplingRate));

		// the count of points is always greater than the count of intervals by
		// one.
		final long calculatedCountOfPoints = calculatedCountOfIntervals + 1;

		return getResultsByAggregationStep(taskKey, parameterIdentifier,
				aggregationStep, timeInterval, 0l, calculatedCountOfPoints,
				orderType, addStartOfData, addEndOfData);
	}

	private Long getRRDLastUpdateTime(final MAgentTask task,
									  final ParameterIdentifier parameterIdentifier) {
		Long lastUpdateTime = (Long) storageService.executeInRRD(task, parameterIdentifier, false, null, new StorageService.RrdRequest() {
					@Override
					public Object execute(final RrdDb rrdDb) {
						Long lastUpdateTime = null;
						try {
							lastUpdateTime = rrdDb.getLastUpdateTime();
						} catch (final Exception ex) {
							LOGGER.error("Unable to get rrd last update time for taskKey = " + task.getKey()
									  + " parameterIdentifier = " + parameterIdentifier.toString()
									  + " located at " + rrdDb.getPath() + ": " + ex.getMessage(), ex);
						}
						return lastUpdateTime;
					}
				});
		if (lastUpdateTime == null) {
			lastUpdateTime = System.currentTimeMillis()	/ TimeConstants.MILLISECONDS_PER_SECOND;
		}
		return lastUpdateTime;
	}

	private Integer getTotalResultCount(final MAgentTask agentTask,
			final ParameterIdentifier parameterIdentifier,
			final Long aggregationStep, final Long samplingRate,
			final Long startTimestamp, final Long endTimestamp) {
		final int resultPageCount = (Integer) storageService.executeInRRD(
				agentTask, parameterIdentifier, false, null,
				new StorageService.RrdRequest() {

					@Override
					public Object execute(final RrdDb rrdDb) {
						final Long calculatedStartTimestamp = startTimestamp;
						Long calculatedEndTimestamp = endTimestamp;
						Long lastUpdateTime;
						try {
							lastUpdateTime = rrdDb.getLastUpdateTime();
						} catch (final Exception ex) {
							throw new ServiceException(
									"Unable to get last updated time of rrd database for parameter = "
											+ parameterIdentifier.getName()
											+ ": " + ex.getMessage(), ex);
						}
						final long castingDevisor = aggregationStep
								* samplingRate;
						if (lastUpdateTime < calculatedEndTimestamp) {
							calculatedEndTimestamp = (lastUpdateTime / castingDevisor)
									* castingDevisor;
						}
						if (calculatedStartTimestamp < calculatedEndTimestamp) {
							return (int) Math
									.ceil(((calculatedEndTimestamp - calculatedStartTimestamp) / ((double) castingDevisor)));
						}
						return 0;
					}
				});

		return resultPageCount;
	}
	@Override
	public Integer getTotalResultCount(final String taskKey,
			final Long aggregationStep, final TimeInterval timeInterval) {
		return getTotalResultsCount(taskKey, null, aggregationStep,
				timeInterval);
	}

	@Override
	public Integer getTotalResultsCount(
			final Map<String, Collection<?>> taskParameters,
			final Long aggregationStep, final TimeInterval timeInterval) {

		int totalResultsCount = 0;
		for (final Map.Entry<String, Collection<?>> taskParametersEntry : taskParameters
				.entrySet()) {
			totalResultsCount = Math.max(
					totalResultsCount,
					getTotalResultsCount(taskParametersEntry.getKey(),
							taskParametersEntry.getValue(), aggregationStep,
							timeInterval));
		}

		return totalResultsCount;
	}

	private Integer getTotalResultsCount(final String taskKey,
			final Collection<?> parameterIdendifiers,
			final Long aggregationStep, final TimeInterval timeInterval) {
		int resultPageCount = Integer.MAX_VALUE;
		final MAgentTask agentTask = getAgentTaskByKey(taskKey);

		final Long samplingRate = agentTask.getResultConfiguration()
				.getSamplingRate();
		Long validatedAggregationStep = aggregationStep;
		if (aggregationStep == null) {
			validatedAggregationStep = getAdaptiveAggregationStep(samplingRate,
					timeInterval.getStartDateTime().getTime(), timeInterval.getEndDateTime().getTime());
		}

		final long castingDevisor = validatedAggregationStep * samplingRate;
		final Long calculatedStartTimestamp = ((timeInterval.getStartDateTime().getTime() / (TimeConstants.MILLISECONDS_PER_SECOND * castingDevisor)))
				* castingDevisor;
		final Long calculatedEndTimestamp = ((timeInterval.getEndDateTime().getTime() / (TimeConstants.MILLISECONDS_PER_SECOND * castingDevisor)) + 1)
				* castingDevisor;

		final Collection<ParameterIdentifier> taskParameterIdentifiers = validateParameterIdentifiers(
				agentTask, parameterIdendifiers);
		for (final ParameterIdentifier taskParameterIdentifier : taskParameterIdentifiers) {
			int currentPageCount = 0;
			try {
				currentPageCount = getTotalResultCount(agentTask,
						taskParameterIdentifier, validatedAggregationStep,
						samplingRate, calculatedStartTimestamp,
						calculatedEndTimestamp);
			} catch (final Exception ex) {
				LOGGER.error("Unable to get pageCount for taskKey = " + taskKey
						+ ": " + ex.getMessage());
			}
			resultPageCount = Math.min(resultPageCount, currentPageCount);
		}

		return resultPageCount;
	}

	@Override
	public void handleIntervalResult(final String taskKey, final List<Interval> intervals) {

		final MAgentTask agentTask = getAgentTaskByKey(taskKey);
		final Long samplingRateMilis = agentTask.getResultConfiguration().getSamplingRate() * TimeConstants.MILLISECONDS_PER_SECOND;
		final SortedMap<ResultIdentifier, Result> resultMap = new TreeMap<ResultIdentifier, Result>();

		for (final Interval interval : intervals) {
			final Result result = interval.getLeft();

			final List<MProperty> modelProperties = result.getModelProperties(agentTask.getModule().getTemplateResultConfiguration());
			final String propertyStorageKey = ParameterIdentifier.createPropertyStorageKey(modelProperties);

			long rrdMinStoredTime = getMinStoredTime(new Date().getTime() / TimeConstants.MILLISECONDS_PER_SECOND);

			// get oldest rrd stored time for all parameters
			for (final String parameterName : result.getParameters().keySet()) {
				final Long lastUpdateTime = getRRDLastUpdateTime(agentTask,	new ParameterIdentifier(parameterName, modelProperties));
				final long parameterMinStoredTime = getMinStoredTime(lastUpdateTime);
				rrdMinStoredTime = Math.min(rrdMinStoredTime, parameterMinStoredTime);
			}

			final Date leftDate = interval.getLeft().getConvertedResultDateTime();
			final Date rightDate = interval.getRight().getConvertedResultDateTime();

			final long intervalStartTime = leftDate.getTime() / TimeConstants.MILLISECONDS_PER_SECOND;
			final long startTimestamp = Math.max(intervalStartTime,	rrdMinStoredTime) * TimeConstants.MILLISECONDS_PER_SECOND;

			for (Long timestamp = startTimestamp; timestamp < rightDate.getTime(); timestamp += samplingRateMilis) {
				resultMap.put(new ResultIdentifier(new Date(timestamp),	propertyStorageKey), result);
			}
		}
		addResults(agentTask, resultMap);
	}

	@Override
	public void handleSingleValueResult(final String taskKey, final List<Result> results) {
		final MAgentTask agentTask = getAgentTaskByKey(taskKey);
		final SortedMap<ResultIdentifier, Result> resultMap = new TreeMap<ResultIdentifier, Result>();
		for (final Result result : results) {
			final List<MProperty> modelProperties = result.getModelProperties(agentTask.getModule().getTemplateResultConfiguration());
			final String propertyStorageKey = ParameterIdentifier.createPropertyStorageKey(modelProperties);
			final Date convertedResultDateTime = result.getConvertedResultDateTime();
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Task = " + taskKey
						 + ", propertyStorageKey = " + propertyStorageKey
						 + ", realAgentTime = "	+ result.getResultDateTime()
						 + ", convertedResultDateTime = " + convertedResultDateTime.getTime()
						 + " ("	+ LONG_DATE_FORMAT.format(convertedResultDateTime) + ")");
			}
			resultMap.put(new ResultIdentifier(convertedResultDateTime,	propertyStorageKey), result);
		}
		addResults(agentTask, resultMap);
	}

	private boolean isLastUpdateTimeReached(final Date dateTime,
			final long lastUpdateTime) {
		return lastUpdateTime <= (dateTime.getTime() / TimeConstants.MILLISECONDS_PER_SECOND);
	}

	private boolean isMinStoredTimeReached(final Date dateTime,
			final long minStoredTime) {
		return (dateTime.getTime() / TimeConstants.MILLISECONDS_PER_SECOND) <= minStoredTime;
	}

	@Override
	public String serializeBean(final ExportResultsWrapper bean) {
		String result = null;
		try {
			result = exportResultsWrapperWriter.writeValueAsString(bean);
		} catch (final Exception ex) {
			throw new ServiceException(
					"Unable to serialize export results wrapper: " + bean, ex);
		}

		return result;
	}

	/**
	 * @param aggregationBase
	 *            the aggregationBase to set
	 */
	public void setAggregationBase(final int aggregationBase) {
		this.aggregationBase = aggregationBase;
	}

	/**
	 * @param maxResultsCount
	 *            the maxResultsCount to set
	 */
	public void setMaxResultsCount(final Integer maxResultsCount) {
		this.maxResultsCount = maxResultsCount;
	}

	public void setStoredDaysCount(final int storedDaysCount) {
		this.storedDaysCount = storedDaysCount;
	}

	private void updateParameterProperties(final MAgentTask task,
										   final ParameterIdentifier parameterIdentifier) {
		if (SimpleUtils.isNotNullAndNotEmpty(parameterIdentifier.getProperties())) {
			final MResultParameterConfiguration parameterConfigurationToUpdate = task.getResultConfiguration()
																					 .findParameterConfiguration(parameterIdentifier);
			if (parameterConfigurationToUpdate != null) {
				final boolean propertiesAreUpdated = propertyUpdater.updateProperties(parameterConfigurationToUpdate,
						                                                              parameterIdentifier,
						                                                              PropertyType.UNREQUIRED);
				if (propertiesAreUpdated) {
					// it is necessary to update task cache
					taskService.createOrUpdateTask(task);
					LOGGER.info("Update task: " + task);
				}
			}
		}
	}

	private Collection<ParameterIdentifier> validateParameterIdentifiers(
			final MAgentTask task, final Collection<?> parameterIdentifiers) {
		final Collection<ParameterIdentifier> result = new ArrayList<>();

		final MResultConfiguration taskConfiguration = task
				.getResultConfiguration();
		if (taskConfiguration != null) {
			for (final MResultParameterConfiguration parameterConfiguration : taskConfiguration
					.findParameterConfigurations(parameterIdentifiers)) {
				result.add(parameterConfiguration.getParameterIdentifier());
			}
			if (result.isEmpty()) {
				result.addAll(task.getResultConfiguration()
						.getParameterIdentifiers());
			}
		}
		return result;
	}

	private void writeResult(final MAgentTask task,
							 final ParameterIdentifier parameterIdentifier,
							 final Date resultDateTime,
							 final Double resultValue,
							 final Date startDateTime) {
		final Long samplingRate = task.getResultConfiguration().getSamplingRate();
		storageService.executeInRRD(task, parameterIdentifier, true, false,	startDateTime, new StorageService.RrdRequest() {
					@Override
					public Object execute(final RrdDb rrdDb) {
						if (resultValue != null) {
							try {
								writeResult(rrdDb, parameterIdentifier.getName(), samplingRate, resultValue, resultDateTime);
							} catch (final Exception ex) {
								final String errorMessage = "Unable to write value: taskKey = "
										+ task.getKey()
										+ ",  parameterIdentifier = "
										+ parameterIdentifier
										+ ", resultDateTime = "
										+ resultDateTime.getTime()
										+ ", value = " + resultValue;
								LOGGER.error(errorMessage, ex);
								throw new ServiceException(errorMessage, ex);
							}
						}
						return null;
					}
				});
	}

	private void writeResult(final RrdDb rrdDb,
							 final String parameterName,
							 final Long samplingRate,
							 final Double parameterValue,
							 final Date resultTime) {
		try {
			if (rrdDb != null) {
				Sample sample = null;
				try {
					sample = rrdDb.createSample();
				} catch (final IOException ex) {
					final String errorMessage = "Unable to create sample to store parameter value for database = " + rrdDb.getPath()
							                  + ": " + ex.getMessage();
					LOGGER.error(errorMessage, ex);
					throw new ServiceException(errorMessage, ex);
				}
				final Long timeForRRD = RrdUtil.castTimeToSamplingRate(RrdUtil.castTimeForRRD(resultTime, null), samplingRate);
				sample.setTime(timeForRRD);
				sample.setValue(rrdDb.getDatasource(0).getName(), parameterValue);
				try {
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("Writing sample to " + rrdDb.getPath()
								+ ": setting time = " + timeForRRD
								+ ", getting time = " + sample.getTime() + " ("
								+ LONG_DATE_FORMAT.format(resultTime)
								+ "), value = " + parameterValue);
					}
					sample.update();
				} catch (final Exception ex) {
					final String errorMessage = "Unable to update RRD database: " + rrdDb.getPath() + ": " + ex.getMessage();
					LOGGER.error(errorMessage);
				}
			}
		} catch (final Exception ex) {
			final String errorMessage = "Unable to write result (value=" + parameterValue
					                  + ",time=" + resultTime
					                  + ") for parameterName=" + parameterName + ": " + ex.getMessage();
			LOGGER.error(errorMessage, ex);
			throw new ServiceException(errorMessage);
		}
	}
}
