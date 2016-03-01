/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace.hibernate;

import com.tecomgroup.qos.exception.ModelSpaceException;
import com.tecomgroup.qos.projection.AbstractProjectionWithParameter;
import com.tecomgroup.qos.projection.CountProjection;
import com.tecomgroup.qos.projection.MaxProjection;
import com.tecomgroup.qos.projection.MinProjection;
import com.tecomgroup.qos.projection.Projection;
import com.tecomgroup.qos.projection.Projection.Operator;
import com.tecomgroup.qos.projection.ProjectionConverter;
import com.tecomgroup.qos.projection.RowCountProjection;

/**
 * @author kunilov.p
 * 
 */
public class HibernateProjectionConverter implements ProjectionConverter {

	private static volatile ProjectionConverter converter;

	public static ProjectionConverter getConverter() {
		if (converter == null) {
			synchronized (HibernateCriterionConverter.class) {
				if (converter == null) {
					converter = new HibernateProjectionConverter();
				}
			}
		}
		return converter;
	}

	private String fromProjection(final String rootEntityAlias,
			final Projection projection) {
		final String result;
		final String projectionOperator = fromProjectionOperator(projection
				.getOperator());
		if (projection instanceof RowCountProjection) {
			result = projectionOperator + "(*)";
		} else if (projection instanceof CountProjection
				|| projection instanceof MaxProjection
                || projection instanceof MinProjection) {
			result = projectionOperator
					+ "("
					+ rootEntityAlias
					+ "."
					+ ((AbstractProjectionWithParameter) projection)
							.getParameter() + ")";
		} else {
			throw new ModelSpaceException("Projection "
					+ projection.getClass().getSimpleName()
					+ " is not supported.");
		}
		return result;
	}

	private String fromProjectionOperator(final Operator type) {
		final String result;
		switch (type) {
			case rowCount :
			case count :
				result = "count";
				break;
			case max :
				result = "max";
				break;
            case min :
                result = "min";
                break;
			default :
				throw new ModelSpaceException("Projection type " + type
						+ " is not supported.");
		}
		return result;
	}

	@Override
	public Object toNativeProjection(final String rootEntityAlias,
			final Projection projection) {
		return fromProjection(rootEntityAlias, projection);
	}

}
