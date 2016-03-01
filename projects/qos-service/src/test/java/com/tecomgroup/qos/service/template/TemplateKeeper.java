/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.template;

import java.util.ArrayList;
import java.util.List;

import com.tecomgroup.qos.domain.MUserAbstractTemplate;

/**
 * Класс-утилита для тестирования шаблонов. Хранит историю изменений шаблона в
 * снэпшотах. Cнэпшот может быть в 3 состояниях - old, new, actual.
 * <ul>
 * <li>old - cтарый снэпшот, уже не актуальный для базы</li>
 * <li>new - новый, но еще не подтвержденный тестированием</li>
 * <li>actual - прошедший тестирование</li>
 * </ul>
 * Цепочка снэпшотов может быть видов :
 * <ul>
 * <li>new</li>
 * <li>old, old, new</li>
 * <li>old, old, actual, new</li>
 * </ul>
 * В цепочке не может быть более одного снэпшота new или actual.
 * 
 * @see TemplateSnapshot
 * @author meleshin.o
 */
public class TemplateKeeper {
	private final List<TemplateSnapshot> snapshots = new ArrayList<TemplateSnapshot>();

	public TemplateKeeper(final MUserAbstractTemplate template) {
		stage(template);
	}

	/**
	 * Actualize given snapshot
	 * */
	public void actualize(final TemplateSnapshot snapshot,
			final MUserAbstractTemplate template) {
		final int index;

		if ((index = snapshots.indexOf(snapshot)) != -1) {
			unactualize();
			snapshots.get(index).setActual(template);
		}
	}

	public TemplateSnapshot getActual() {
		TemplateSnapshot result = null;
		final int snapshotsCount = snapshots.size();
		for (int i = snapshotsCount - 1; i >= 0; i--) {
			final TemplateSnapshot snapshot = snapshots.get(i);
			if (snapshot.isActual()) {
				result = snapshot;
				break;
			}
		}

		return result;
	}

	public TemplateSnapshot getActualOrNew() {
		TemplateSnapshot snapshot, result;
		snapshot = getNew();
		if (snapshot != null) {
			result = snapshot;
		} else {
			result = getActual();
		}

		return result;
	}

	public TemplateSnapshot getNew() {
		TemplateSnapshot result = null;
		final int snapshotsCount = snapshots.size();
		for (int i = snapshotsCount - 1; i >= 0; i--) {
			final TemplateSnapshot snapshot = snapshots.get(i);
			if (snapshot.isNew()) {
				result = snapshot;
				break;
			}
		}

		return result;
	}

	public List<TemplateSnapshot> getOld() {
		final List<TemplateSnapshot> result = new ArrayList<TemplateSnapshot>();
		final int snapshotsCount = snapshots.size();
		for (int i = snapshotsCount - 1; i >= 0; i--) {
			final TemplateSnapshot snapshot = snapshots.get(i);
			if (snapshot.isOld()) {
				result.add(snapshot);
			}
		}

		return result;
	}

	public List<TemplateSnapshot> getSnapshots() {
		return snapshots;
	}

	public boolean hasActualOrNew() {
		return (getNew() != null) || (getActual() != null);
	}

	public boolean isEmpty() {
		return (snapshots.size() == 0);
	}

	/**
	 * Add new, but not actualized snapshot
	 * */
	public void stage(final MUserAbstractTemplate template) {
		final TemplateSnapshot snapshot = new TemplateSnapshot(template.copy());

		final TemplateSnapshot newSnapshot = getNew();
		if (newSnapshot != null) {
			snapshots.remove(newSnapshot);
		}

		snapshots.add(snapshot);
	}

	public void unactualize() {
		for (final TemplateSnapshot snapshot : snapshots) {
			snapshot.setOld();
		}
	}
}
