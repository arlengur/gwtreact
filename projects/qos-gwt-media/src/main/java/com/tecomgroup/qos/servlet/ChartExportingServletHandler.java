/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.log4j.Logger;
import org.springframework.web.HttpRequestHandler;

/**
 * @author ivlev.e
 * 
 */
public class ChartExportingServletHandler implements HttpRequestHandler {

	private enum Extensions {
		JPG("jpg"), PNG("png"), PDF("pdf"), SVG("svg");

		private final String name;
		Extensions(final String name) {
			this.name = name;
		}

		String getName() {
			return name;
		}
	}

	private final static Logger LOGGER = Logger
			.getLogger(ChartExportingServletHandler.class);

	private final static String DEFAULT_FILE_NAME = "chart";

	private final static String DEFAULT_ENCODING = "UTF-8";

	private final static String MIME_PNG = "image/png";

	private final static String MIME_JPEG = "image/jpeg";

	private final static String MIME_PDF = "application/pdf";

	private final static String MIME_SVG = "image/svg+xml";

	private final static String PARAM_TYPE = "type";

	private final static String PARAM_CONTENT = "svg";

	private final static String PARAM_FILE_NAME = "filename";

	private final static String TMP_FILE_PREFIX = "tmpFile_";

	private File getTmpDir() {
		return new File(System.getProperty("java.io.tmpdir"));
	}

	private SVGAbstractTranscoder getTranscoder(final String outputMimeType) {
		SVGAbstractTranscoder transcoder = null;
		if (outputMimeType.equals(MIME_JPEG)) {
			transcoder = new JPEGTranscoder();
			transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
					new Float(.8));
		} else if (outputMimeType.equals(MIME_PNG)) {
			transcoder = new PNGTranscoder();
		} else if (outputMimeType.equals(MIME_PDF)) {
			transcoder = new PDFTranscoder();
		}

		return transcoder;
	}

	@Override
	public void handleRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final Map<String, String> postParameters = readRequest(request);

		final String type = postParameters.get(PARAM_TYPE);
		final String svg = postParameters.get(PARAM_CONTENT);
		String filename = postParameters.get(PARAM_FILE_NAME);
		if (filename == null || filename.isEmpty()) {
			filename = DEFAULT_FILE_NAME;
		}
		final String tempName = TMP_FILE_PREFIX
				+ DigestUtils.md5Hex(String.valueOf(new Date().getTime()));
		Extensions ext = null;
		String typeString = null;
		if (type.equals(MIME_PNG)) {
			typeString = "-m " + MIME_PNG;
			ext = Extensions.PNG;

		} else if (type.equals(MIME_JPEG)) {
			typeString = "-m " + MIME_JPEG;
			ext = Extensions.JPG;

		} else if (type.equals(MIME_PDF)) {
			typeString = "-m " + MIME_PDF;
			ext = Extensions.PDF;

		} else if (type.equals(MIME_SVG)) {
			ext = Extensions.SVG;
		}

		if (typeString != null) {
			try {
				final File tmpDir = getTmpDir();
				final File tempFile = writeSvgToTempFile(svg, tempName, tmpDir);

				final File outputTmpFile = transcodeSvgToImage(tempFile, type,
						tempName, ext, tmpDir);

				writeImageToResponse(outputTmpFile, response, filename, ext,
						typeString);
			} catch (final Exception e) {
				LOGGER.error("Can't convert svg to image", e);
			}
		} else if (ext.equals(Extensions.SVG)) {
			writeSvgToResponse(response, filename, ext, type, svg);
		} else {
			response.getWriter().println("Invalid type");
			response.getWriter().close();
			response.getWriter().flush();
		}
	}

	private Map<String, String> readRequest(final HttpServletRequest request) {
		final Map<String, String> postParameters = new HashMap<String, String>();

		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				final Iterator<FileItem> items = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request)
						.iterator();
				while (items.hasNext()) {
					final FileItem thisItem = items.next();
					if (thisItem.isFormField()) {
						postParameters.put(thisItem.getFieldName(),
								thisItem.getString(DEFAULT_ENCODING));
					}
				}
			} catch (final Exception e) {
				LOGGER.warn("Reading parameters from http request error", e);
			}
		}
		return postParameters;
	}

	private File transcodeSvgToImage(final File inputSvgFile,
			final String type, final String tmpFileName, final Extensions ext,
			final File tmpDir) throws IOException, TranscoderException {
		final SVGAbstractTranscoder transcoder = getTranscoder(type);

		final TranscoderInput input = new TranscoderInput(
				new InputStreamReader(new FileInputStream(inputSvgFile),
						DEFAULT_ENCODING));
		final File outputTmpFile = File.createTempFile(tmpFileName,
				"." + ext.getName(), tmpDir);
		final OutputStream outputStream = new FileOutputStream(outputTmpFile);
		final TranscoderOutput output = new TranscoderOutput(outputStream);

		transcoder.transcode(input, output);

		outputStream.flush();
		outputStream.close();

		return outputTmpFile;
	}

	private void writeImageToResponse(final File imageFile,
			final HttpServletResponse response, final String filename,
			final Extensions ext, final String type) throws IOException {
		response.addHeader("Content-Disposition", "attachment; filename="
				+ filename + "." + ext.getName());
		response.setContentType(type);

		final BufferedInputStream convertedInputStream = new BufferedInputStream(
				new FileInputStream(imageFile.getAbsoluteFile()));

		try {
			// read from file
			final byte[] data = new byte[convertedInputStream.available()];
			convertedInputStream.read(data);
			// write to http response
			final ServletOutputStream out = response.getOutputStream();
			out.write(data);
			out.close();
		} finally {
			convertedInputStream.close();
		}
	}

	private void writeSvgToResponse(final HttpServletResponse response,
			final String filename, final Extensions ext, final String type,
			final String content) throws IOException {
		response.addHeader("Content-Disposition", "attachment; filename="
				+ filename + "." + ext.getName());
		response.setContentType(type);
		final PrintWriter out = response.getWriter();
		out.print(content);
		out.close();
		out.flush();
	}

	private File writeSvgToTempFile(final String content,
			final String tmpFileName, final File tmpDir) throws IOException {
		final File tempFile = File.createTempFile(tmpFileName, ".svg", tmpDir);
		final PrintWriter printWriter = new PrintWriter(tempFile,
				DEFAULT_ENCODING);
		printWriter
				.print("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		printWriter.print(content);
		printWriter.flush();
		printWriter.close();
		return tempFile;
	}
}
