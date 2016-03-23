package com.tecomgroup.qos.domain.probestatus;

/**
 * Created by uvarov.m on 02.03.2016.
 */
public class MProbeEventFactory {

    private MProbeEventFactory() {
    }

    public static MProbeEvent getInstance(MProbeEvent event) {
        if(MExportVideoEvent.class.getName().equals(event.getEventType())) {
            return new MExportVideoEvent(event);
        }

        return null;
    }
}
