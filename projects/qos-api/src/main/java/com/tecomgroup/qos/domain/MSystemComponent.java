/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

/**
 * The root class of system components represents Server, Agent {@link MAgent}
 * and PolicyManager. <br />
 * 
 * <b>IMPORTANT<b> <br/>
 * This class should be only one and should be the root the hierarchy of sources
 * for this particular system component.
 * 
 * @author kunilov.p
 * 
 */
public abstract class MSystemComponent extends MSource {
	private static final long serialVersionUID = -6272267069473396617L;

}
