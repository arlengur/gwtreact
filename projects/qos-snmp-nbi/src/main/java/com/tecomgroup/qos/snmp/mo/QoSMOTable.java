/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.mo;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.snmp4j.agent.DefaultMOScope;
import org.snmp4j.agent.MOScope;
import org.snmp4j.agent.mo.DefaultMOTable;
import org.snmp4j.agent.mo.MOColumn;
import org.snmp4j.agent.mo.MOTableCellInfo;
import org.snmp4j.agent.mo.MOTableIndex;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.request.Request;
import org.snmp4j.agent.request.SubRequest;
import org.snmp4j.agent.util.OIDScope;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

/**
 * Implements its own cell handling with cache specific to QoS Server model.
 * 
 * <br/>
 * <br/>
 * 
 * Ignores GET-per-cell algorithm, retrieves complete rows and caches them. Each
 * row will be retrieved from the database only once when first cell from this
 * row requested. When other cells are requested, cached row used.
 * 
 * <br/>
 * <br/>
 * 
 * Read more about cache at {@link RowCache}.
 * 
 * @author novohatskiy.r
 */
public class QoSMOTable<R extends MOTableRow<Variable>, C extends MOColumn<Variable>, M extends QoSMOTableModel<R>>
		extends
			DefaultMOTable<R, C, M> {

	protected class CellInfo implements MOTableCellInfo {

		private OID index;
		private int id = 0;
		private int col = -1;
		private R row;
		private DefaultMOTable<R, C, M> table;

		public CellInfo(final DefaultMOTable<R, C, M> table, final OID oid) {
			this.table = table;
			this.index = table.getIndexPart(oid);
			if ((oid.size() > table.getOID().size())
					&& (oid.startsWith(table.getOID()))) {
				id = oid.get(table.getOID().size());
			}
		}

		public CellInfo(final DefaultMOTable<R, C, M> table, final OID index,
				final int column, final int columnID) {
			this.table = table;
			this.index = index;
			this.col = column;
			this.id = columnID;
		}

		public CellInfo(final DefaultMOTable<R, C, M> table, final OID index,
				final int column, final int columnID, final R row) {
			this(table, index, column, columnID);
			this.row = row;
		}

		@Override
		public OID getCellOID() {
			return table.getCellOID(index, col);
		}

		@Override
		public int getColumn() {
			if (col < 0) {
				col = table.getColumnIndex(id);
			}
			return col;
		}

		@Override
		public int getColumnID() {
			return id;
		}

		@Override
		public OID getIndex() {
			return index;
		}

		public R getRow() {
			return row;
		}
	}

	/**
	 * The QoS Server specific 2-level cache.
	 * 
	 * <br/>
	 * 
	 * First level is based on entity last modification date, second level is
	 * based on request.
	 * 
	 * <br/>
	 * <br/>
	 * 
	 * There are two types of {@link QoSMOTable#findCell(MOScope, SubRequest)}
	 * calls when RowCache is needed:
	 * 
	 * <br/>
	 * 
	 * 1. When SubRequest parameter is null. <br/>
	 * 2. When SubRequest parameter is not null.
	 * 
	 * <br/>
	 * <br/>
	 * 
	 * In first case there is no request to rely on, so only first level is
	 * used. Every time cache receives "get" operation, the last modification
	 * date is checked.
	 * 
	 * <br/>
	 * 
	 * In second case assumed that entire request have one actuality date
	 * (timestamp of last modification) and it checked only once, after that
	 * request object becomes a cache key. If first-level cache with same
	 * actuality date as request have exists, it used as a part of 2nd level
	 * cache.
	 * 
	 * <br/>
	 * <br/>
	 * 
	 * Cache expiration: <br/>
	 * 1. First level cache expires when last modification timestamp changes and
	 * where are no second level caches pointing it.<br/>
	 * 2. Second level cache expires when request completed.
	 * 
	 * <br/>
	 * 
	 * Cache removal is performed by GC when all links to request or to an old
	 * actuality date object are lost.
	 */
	@SuppressWarnings("rawtypes")
	private class RowCache {
		private Date actualDate;
		private final Map<Date, Map<OID, RowCacheEntry>> cache;
		private final Map<Request, Date> requestToActualityDate;

		RowCache() {
			actualDate = new Date();
			cache = new WeakHashMap<Date, Map<OID, RowCacheEntry>>();
			requestToActualityDate = new WeakHashMap<Request, Date>();
		}

		void add(final Request request, final OID lowerBound,
				final boolean lowerIncluded, final R row) {
			Date entryActualityDate = null;
			if (request != null) {
				entryActualityDate = getOrCreateRequestActualityDate(request);
			} else {
				updateActualDate();
				entryActualityDate = actualDate;
			}

			Map<OID, RowCacheEntry> cacheForDate = cache
					.get(entryActualityDate);
			if (cacheForDate == null) {
				cacheForDate = new HashMap<OID, RowCacheEntry>();
				cache.put(entryActualityDate, cacheForDate);
			}

			cacheForDate.put(lowerBound, new RowCacheEntry(row, lowerBound,
					lowerIncluded));
		}

		RowCacheEntry get(final Request request,
				final MOTableCellInfo cellInfo, final boolean lowerIncluded) {
			RowCacheEntry result = null;
			Date requestActualityDate = null;

			if (request != null) {
				requestActualityDate = getOrCreateRequestActualityDate(request);
			} else {
				updateActualDate();
				requestActualityDate = actualDate;
			}

			if (requestActualityDate != null) {
				final Map<OID, RowCacheEntry> cacheForDate = cache
						.get(requestActualityDate);
				if (cacheForDate != null) {
					final RowCacheEntry entry = cacheForDate.get(cellInfo
							.getIndex());
					if (entry != null) {
						if (((entry.searchLowerBound == null) && (cellInfo
								.getIndex() == null))
								|| ((entry.searchLowerBound != null)
										&& (entry.searchLowerBound
												.equals(cellInfo.getIndex())) && (lowerIncluded == entry.searchLowerBoundIncluded))) {
							result = entry;
						}
					}
				}
			}

			return result;
		}

		Date getOrCreateRequestActualityDate(final Request request) {
			Date requestActualityDate = requestToActualityDate.get(request);
			if (requestActualityDate == null) {
				updateActualDate();
				requestActualityDate = actualDate;
				requestToActualityDate.put(request, requestActualityDate);
			}
			return requestActualityDate;
		}

		private void updateActualDate() {
			final Date freshActualDate = model
					.getLastEntityModificationTimestamp();
			if (freshActualDate != null && !freshActualDate.equals(actualDate)) {
				actualDate = freshActualDate;
			}
		}
	}

	private class RowCacheEntry {
		private final R row;
		private final OID searchLowerBound;
		private final boolean searchLowerBoundIncluded;

		RowCacheEntry(final R row, final OID searchLowerBound,
				final boolean searchLowerBoundIncluded) {
			this.row = row;
			this.searchLowerBound = searchLowerBound;
			this.searchLowerBoundIncluded = searchLowerBoundIncluded;
		}
	}

	private transient RowCache rowCache;

	public QoSMOTable(final OID oid, final MOTableIndex indexDef,
			final C[] columns) {
		super(oid, indexDef, columns);
		rowCache = new RowCache();
	}

	public QoSMOTable(final OID oid, final MOTableIndex indexDef,
			final C[] columns, final M model) {
		super(oid, indexDef, columns, model);
		rowCache = new RowCache();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	protected MOTableCellInfo findCell(final MOScope range,
			final SubRequest request) {
		MOTableCellInfo result = null;
		synchronized (model) {
			update(range, request);
			final MOTableCellInfo cellInfo = getCellInfo(range.getLowerBound());
			int col = cellInfo.getColumn();
			boolean exactMatch = true;
			if (col < 0) {
				col = (-col) - 1;
				exactMatch = false;
			}
			if (col < getColumns().length) {
				final boolean lowerIncluded = (!exactMatch)
						|| range.isLowerIncluded();
				final Request globalRequest = request != null ? request
						.getRequest() : null;
				final RowCacheEntry rowEntry = rowCache.get(globalRequest,
						cellInfo, lowerIncluded);
				MOTableCellInfo next;
				if (rowEntry != null) {
					next = new CellInfo(this, rowEntry.row.getIndex(), col,
							cellInfo.getColumnID(), rowEntry.row);
				} else {
					next = getNextCell(col, cellInfo.getIndex(), lowerIncluded);
					if ((next != null) && (next.getColumn() == col)) {
						rowCache.add(globalRequest, cellInfo.getIndex(),
								lowerIncluded, ((CellInfo) next).row);
					}
				}
				if (next != null) {
					final OID cellOID = next.getCellOID();
					if (range.isCovered(new OIDScope(cellOID))) {
						result = next;
					}
				}
			}
		}
		return result;
	}

	private MOTableCellInfo getNextCell(final int col, OID indexLowerBound,
			boolean isLowerBoundIncluded) {
		MOTableCellInfo result = null;
		for (int i = col; i < getColumnCount(); i++) {
			final Iterator<R> it = model.tailIterator(indexLowerBound);
			if (it.hasNext()) {
				R row = it.next();
				if ((indexLowerBound != null) && (!isLowerBoundIncluded)) {
					if (row.getIndex().compareTo(indexLowerBound) <= 0) {
						if (it.hasNext()) {
							row = it.next();
						} else {
							row = null;
						}
					}
				}
				if (row != null) {
					result = new CellInfo(this, row.getIndex(), i,
							getColumns()[i].getColumnID(), row);
					break;
				}
			} else if (indexLowerBound == null) {
				break;
			}
			indexLowerBound = null;
			isLowerBoundIncluded = true;
		}
		return result;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public boolean next(final SubRequest request) {
		boolean result = false;
		final DefaultMOScope scope = new DefaultMOScope(request.getScope());
		MOTableCellInfo nextCell;
		while ((nextCell = findCell(scope, request)) != null) {
			if (getColumns()[nextCell.getColumn()].getAccess()
					.isAccessibleForRead()) {
				Variable value;
				// Use row instance from cell info as shortcut if available
				if ((nextCell instanceof QoSMOTable.CellInfo)
						&& (((CellInfo) nextCell).getRow() != null)) {
					value = getValue(((CellInfo) nextCell).getRow(),
							nextCell.getColumn());
				} else {
					value = getValue(nextCell.getIndex(), nextCell.getColumn());
				}
				if (value == null) {
					scope.setLowerBound(nextCell.getCellOID());
					scope.setLowerIncluded(false);
				} else {
					request.getVariableBinding().setOid(nextCell.getCellOID());
					request.getVariableBinding().setVariable(value);
					request.completed();
					result = true;
					break;
				}
			} else {
				if (nextCell.getColumn() + 1 < getColumnCount()) {
					final OID nextColOID = new OID(getOID());
					nextColOID.append(getColumns()[nextCell.getColumn() + 1]
							.getColumnID());
					scope.setLowerBound(nextColOID);
					scope.setLowerIncluded(false);
				} else {
					break;
				}
			}
		}
		return result;
	}

}
