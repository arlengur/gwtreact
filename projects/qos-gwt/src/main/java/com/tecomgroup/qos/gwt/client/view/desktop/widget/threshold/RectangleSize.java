/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.threshold;


public class RectangleSize {

	public Double x0;

	public Double y0;

	public Double width;

	public Double height;

	public RectangleSize() {
	}

	public RectangleSize[] divideBy2() {
		final RectangleSize[] sizeArray = new RectangleSize[2];
		final RectangleSize size1 = new RectangleSize();
		size1.x0 = x0;
		size1.y0 = y0;
		size1.width = width / 2;
		size1.height = height;
		sizeArray[0] = size1;

		final RectangleSize size2 = new RectangleSize();
		size2.x0 = size1.x0 + size1.width;
		size2.y0 = y0;
		size2.width = size1.width;
		size2.height = height;
		sizeArray[1] = size2;

		return sizeArray;
	}

	public RectangleSize[] divideBy3() {
		final RectangleSize[] sizeArray = new RectangleSize[3];
		final RectangleSize size1 = new RectangleSize();
		size1.x0 = x0;
		size1.y0 = y0;
		size1.width = width / 3;
		size1.height = height;
		sizeArray[0] = size1;

		final RectangleSize size2 = new RectangleSize();
		size2.x0 = size1.x0 + size1.width;
		size2.y0 = y0;
		size2.width = size1.width;
		size2.height = height;
		sizeArray[1] = size2;

		final RectangleSize size3 = new RectangleSize();
		size3.x0 = size1.x0 + 2 * size1.width;
		size3.y0 = y0;
		size3.width = size1.width;
		size3.height = height;
		sizeArray[2] = size3;

		return sizeArray;
	}
}
