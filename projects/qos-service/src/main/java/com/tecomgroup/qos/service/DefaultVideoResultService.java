/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.communication.result.VideoResult;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MVideoResult;
import com.tecomgroup.qos.domain.Source;

/**
 * @author kunilov.p
 * 
 */
@Transactional(readOnly = true)
public class DefaultVideoResultService extends AbstractService
		implements
			VideoResultService {

	@Override
	@Transactional(readOnly = false)
	public void addResults(final String taskKey, final List<VideoResult> results) {
		if (results != null && !results.isEmpty()) {
			for (final VideoResult result : results) {
				final MVideoResult domainResult = new MVideoResult();
				domainResult.setSource(Source.getStreamSource(taskKey,
						result.getStreamKey()));
				domainResult.setFileName(result.getFileName());
				domainResult.setStartDateTime(result
						.getConvertedStartDateTime());
				domainResult.setEndDateTime(result.getConvertedEndDateTime());
				modelSpace.saveOrUpdate(domainResult);
			}
		}
	}

	@Override
	public List<MVideoResult> getResults(final Source streamSource,
			final TimeInterval timeInterval, final Integer startPosition,
			final Integer size) {
		List<MVideoResult> results = null;
		if (Source.Type.STREAM.equals(streamSource.getType())) {
			final CriterionQuery query = modelSpace.createCriterionQuery();
			Criterion criterion = query.eq("source.key", streamSource.getKey());
			criterion = query.and(criterion,
					query.eq("source.type", streamSource.getType()));

			if (timeInterval != null) {
				final Criterion timeCriterion = query.or(query.between(
						"startDateTime",
						timeInterval.getStartDateTime(),
						timeInterval.getEndDateTime()), query.between(
						"endDateTime",
						timeInterval.getStartDateTime(),
						timeInterval.getEndDateTime()));

				criterion = query.and(criterion, timeCriterion);
			}

			results = modelSpace.find(MVideoResult.class, criterion,
					Order.asc("startDateTime"), startPosition, size);
		} else {
			throw new IllegalArgumentException("Incorrect source type: "
					+ streamSource + ". Must be STREAM");
		}
		return results;
	}
}
