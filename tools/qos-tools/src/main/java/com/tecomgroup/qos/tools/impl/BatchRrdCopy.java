/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.tecomgroup.qos.tools.QoSTool;

/**
 * 
 * Copy all rrd files from RRD home to new directory with updated RRD
 * parameters: rrd.copy.sampling.rate or rrd.copy.stored.days.count
 * 
 * NOTE: targetFolder must not be subfolder of sourceFolder
 * 
 * 
 * @author abondin
 * 
 */
@Component
public class BatchRrdCopy implements QoSTool {

	private final Logger LOGGER = Logger.getLogger(BatchRrdCopy.class);

	@Value("${rrd.copy.source.folder}")
	private File sourceFolder;
	@Value("${rrd.copy.target.folder}")
	private File targetFolder;

	@Value("${rrd.copy.file.mask}")
	private String rrdFilePattern;

	@Autowired
	private CopyRrd copyRrd;

	public void copyFile(final File file) {
		if (file.isDirectory()) {
			for (final File child : file.listFiles()) {
				copyFile(child);
			}
		} else if (file.getName().matches(rrdFilePattern)) {
			final String releativeFilePath = sourceFolder.toURI()
					.relativize(file.toURI()).getPath();
			final File targetFile = new File(targetFolder, releativeFilePath);
			targetFile.getParentFile().mkdirs();
			LOGGER.info("Copying " + file);
			copyRrd.execute(file.getAbsolutePath(),
					targetFile.getAbsolutePath());
		}
	}

	@Override
	public void execute() {
		Assert.isTrue(sourceFolder.exists() && sourceFolder.isDirectory(),
				"Source folder not found " + sourceFolder.getAbsolutePath());
		Assert.isTrue(!(targetFolder.getAbsolutePath() + File.separator)
				.startsWith((sourceFolder.getAbsolutePath() + File.separator)),
				"Target folder cannot be subfolder of source folder");
		copyFile(sourceFolder);

	}

	@Override
	public String getDescription() {
		return "Copy all rrd files from RRD home to new directory with updated RRD parameters:\n rrd.copy.sampling.rate or rrd.copy.stored.days.count"
				+ "\nSupported VM arguments:"
				+ "\nrrd.source.folder - RRD home"
				+ "\nrrd.target.folder - new RRD home";
	}
}
