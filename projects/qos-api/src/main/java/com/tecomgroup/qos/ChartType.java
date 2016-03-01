package com.tecomgroup.qos;

/**
* @author smyshlyaev.s
*/
public enum ChartType {
    LEVEL_SINGLE, LEVEL_MULTIPLE, COUNTER, BOOL, PERCENTAGE_MULTIPLE, PERCENTAGE_SINGLE, UNSUPPORTED;

    public boolean isSingle() {
        return this.equals(LEVEL_SINGLE) || this.equals(PERCENTAGE_SINGLE);
    }
}
