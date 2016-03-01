package com.tecomgroup.qos.projection;

/**
 * @author smyshlyaev.s
 */
public class MinProjection extends AbstractProjectionWithParameter{

    public MinProjection(final String parameter) {
        super(parameter, Operator.min);
    }
}
