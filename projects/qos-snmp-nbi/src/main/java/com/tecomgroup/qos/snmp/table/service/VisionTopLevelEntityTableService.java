package com.tecomgroup.qos.snmp.table.service;

import java.util.Date;
import java.util.Iterator;

import org.snmp4j.agent.mo.DefaultMOTableRow;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tecomgroup.qos.service.SnmpService;

/**
 * @author smyshlyaev.s
 */
@Service
public class VisionTopLevelEntityTableService implements SnmpTableService<MOTableRow<Variable>> {

    @Autowired
    private SnmpService snmpService;

    private class VisionTopLevelEntityTableIterator implements Iterator<MOTableRow<Variable>> {

        private Integer current;
        private Integer next;

        private VisionTopLevelEntityTableIterator() {
            this.current = Integer.MIN_VALUE;
            this.next = null;
        }

        private VisionTopLevelEntityTableIterator(final Integer current) {
            this.current = current;
            this.next = null;
        }

        @Override
        public boolean hasNext() {
            final Integer maybeNext = snmpService.getNextTopLevelEntityIndex(current);
            if(maybeNext != null) {
                next = maybeNext;
            }
            return maybeNext != null;
        }

        @Override
        public MOTableRow<Variable> next() {
            if(next == null) {
                next = snmpService.getNextTopLevelEntityIndex(current);
            }
            current = next;
            final MOTableRow<Variable> result = getRow(new OID(new int[]{next}));
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Date getLastEntityModificationTimestamp() {
        return new Date();
    }

    @SuppressWarnings("unchecked")
    @Override
    public MOTableRow<Variable> getRow(final OID index) {
        return new DefaultMOTableRow(index, new Variable[]{new Integer32(index.get(0))});
    }

    @Override
    public int getRowCount() {
        return snmpService.getTopLevelEntityCount();
    }

    @Override
    public Iterator<MOTableRow<Variable>> iterator() {
        return new VisionTopLevelEntityTableIterator();
    }

    @Override
    public Iterator<MOTableRow<Variable>> iterator(final OID lowerBound) {
        final Iterator<MOTableRow<Variable>> result;
        if(lowerBound == null) {
            result = new VisionTopLevelEntityTableIterator();
        } else {
            result = new VisionTopLevelEntityTableIterator(lowerBound.get(0));
        }
        return result;
    }
}
