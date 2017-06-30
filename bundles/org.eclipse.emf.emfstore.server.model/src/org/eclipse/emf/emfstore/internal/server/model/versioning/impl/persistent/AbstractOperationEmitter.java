/*******************************************************************************
 * Copyright (c) 2011-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInput;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;

/**
 * Abstract super class for implementing types which emit operations when given an {@link ReadLineCapable} type.
 *
 */
public abstract class AbstractOperationEmitter implements Closeable {

	private final Direction direction;

	private final File operationsFile;

	private ReadLineCapable reader;
	private final List<Long> forwardOffsets = new ArrayList<Long>();
	private final List<Long> backwardsOffsets = new ArrayList<Long>();
	private int currentOpIndex;
	private long startOffset;
	private boolean isClosed;

	/**
	 * Constructor.
	 *
	 * @param direction
	 *            the {@link Direction} that is used for reading
	 * @param file
	 *            the operation file
	 */
	public AbstractOperationEmitter(Direction direction, File file) {
		this.direction = direction;
		operationsFile = file;
		determineOperationOffsets();
		currentOpIndex = direction == Direction.Forward ? 0 : backwardsOffsets.size() - 1;
		initReader();
	}

	/**
	 * @return the direction the {@link Direction}
	 */
	protected final Direction getDirection() {
		return direction;
	}

	private void determineOperationOffsets() {
		try {
			final RandomAccessFile randomAccessFile = new RandomAccessFile(operationsFile, "r"); //$NON-NLS-1$
			final InputStream inputStream = Channels.newInputStream(randomAccessFile.getChannel());
			final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			long filePointer = 0;
			try {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					filePointer += line.getBytes().length;
					final long filePointerAfterReadline = randomAccessFile.getFilePointer();
					randomAccessFile.seek(filePointer);
					int byteAfterLine = randomAccessFile.read();
					/*
					 * Line is terminated by either:
					 * \r\n
					 * \r
					 * \n
					 */
					if (byteAfterLine == '\r') {
						filePointer += 1;
						byteAfterLine = randomAccessFile.read();
					}
					if (byteAfterLine == '\n') {
						filePointer += 1;
					}
					randomAccessFile.seek(filePointerAfterReadline);

					if (line.contains(XmlTags.CHANGE_PACKAGE_START)) {
						startOffset = filePointer;
					} else if (line.contains(XmlTags.OPERATIONS_START_TAG)) {
						forwardOffsets.add(filePointer);
					} else if (line.contains(XmlTags.OPERATIONS_END_TAG)) {
						backwardsOffsets.add(filePointer);
					}
				}
			} finally {
				bufferedReader.close();
				randomAccessFile.close();
			}
		} catch (final IOException ex) {
			ModelUtil.logException(ex);
		}
	}

	private void initReader() {
		try {
			if (getDirection() == Direction.Forward) {
				reader = ReadLineCapable.INSTANCE.create(new BufferedReader(new FileReader(operationsFile)));
			} else {
				reader = ReadLineCapable.INSTANCE.create(new ReversedLinesFileReader(operationsFile));
			}
		} catch (final IOException ex) {
			ModelUtil.logException(ex);
		}
	}

	/**
	 * Returns the current offset.
	 *
	 * @return the current offset
	 */
	public long getOffset() {
		if (currentOpIndex < 0) {
			return startOffset;
		}
		return backwardsOffsets.get(currentOpIndex);
	}

	/**
	 * Since an XML Resource needs exactly one root object, we have to write a dummy object to the stream.
	 *
	 * @param pos the {@link PipedOutputStream}
	 * @throws IOException in case there is a problem during write
	 */
	private static void writeDummyResourceToStream(PipedOutputStream pos) throws IOException {
		pos.write(XmlTags.XML_RESOURCE_WITH_EOBJECT.getBytes());
	}

	/**
	 * Reads the file in forward direction and writes read lines to the given stream.
	 *
	 * @param pos the output stream
	 */
	protected final void readForward(PipedOutputStream pos) {
		try {
			boolean operationsFound = false;
			boolean withinOperationsElement = false;
			final boolean isForwardDir = getDirection() == Direction.Forward;
			final String closingTag = getClosingTag(isForwardDir);
			String line = reader.readLine();
			while (line != null && !line.contains(closingTag)) {
				if (line.contains(getOpeningTag(isForwardDir))) {
					withinOperationsElement = true;
				} else if (withinOperationsElement) {
					operationsFound = true;
					pos.write(line.getBytes());
				}
				line = reader.readLine();
			}
			if (line != null) {
				withinOperationsElement = false;
			}
			if (!operationsFound) {
				writeDummyResourceToStream(pos);
			}
		} catch (final IOException ex) {
			ModelUtil.logException(ex);
		} finally {
			try {
				pos.close();
			} catch (final IOException ex) {
				ModelUtil.logException(ex);
			}
		}
	}

	private void readForward(DataInput reader, PipedOutputStream pos) {
		try {
			boolean operationsFound = false;
			boolean withinOperationsElement = true;
			final String closingTag = getClosingTag(true);
			String line = reader.readLine();
			while (line != null && !line.contains(closingTag)) {
				if (line.contains(getOpeningTag(true))) {
					withinOperationsElement = true;
				} else if (withinOperationsElement && line.length() > 0) {
					operationsFound = true;
					pos.write(line.getBytes());
				}
				line = reader.readLine();
			}

			if (!operationsFound) {
				writeDummyResourceToStream(pos);
			}
		} catch (final IOException ex) {
			ModelUtil.logException(ex);
		} finally {
			try {
				pos.close();
			} catch (final IOException ex) {
				ModelUtil.logException(ex);
			}
		}
	}

	/**
	 * Reads the file in backward direction and writes read lines to the given stream.
	 *
	 * @param pos the output strea,
	 */
	protected final void readBackward(PipedOutputStream pos) {

		if (currentOpIndex < 0) {
			try {
				writeDummyResourceToStream(pos);
				pos.close();
			} catch (final IOException ex) {
				ModelUtil.logException(ex);
			}
			return;
		}

		final long offset = forwardOffsets.get(currentOpIndex);
		currentOpIndex -= 1;

		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(operationsFile, "r"); //$NON-NLS-1$
			raf.skipBytes((int) offset);
			readForward(raf, pos);
		} catch (final IOException ex) {
			ModelUtil.logException(ex);
		} finally {
			try {
				raf.close();
			} catch (final IOException ex) {
				ModelUtil.logException(ex);
			}
		}
	}

	private String getClosingTag(boolean isForward) {
		return isForward ? XmlTags.OPERATIONS_END_TAG : XmlTags.OPERATIONS_START_TAG;
	}

	private String getOpeningTag(boolean isForward) {
		return isForward ? XmlTags.OPERATIONS_START_TAG : XmlTags.OPERATIONS_END_TAG;
	}

	/**
	 * Closes the emitter.
	 */
	public void close() {
		setClosed(true);
		try {
			reader.close();
		} catch (final IOException ex) {
			ModelUtil.logException(ex);
		}
	}

	/**
	 * @return <code>true</code> if emitter was closed, <code>false</code> otherwise
	 */
	protected boolean isClosed() {
		return isClosed;
	}

	private void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

}