/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLLoadImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * Type for emitting {@link AbstractOperation}s when given an {@link ReadLineCapable} type.
 *
 * @author emueller
 *
 */
public class OperationEmitter implements Closeable {

	private final Direction direction;

	private final File operationsFile;

	private ReadLineCapable reader;
	private final List<Long> forwardOffsets = new ArrayList<Long>();
	private final List<Long> backwardsOffsets = new ArrayList<Long>();
	private int currentOpIndex;
	private long startOffset;

	/**
	 * Constructor.
	 *
	 * @param direction
	 *            the {@link Direction} that is used for reading
	 * @param file
	 *            the operation file
	 */
	public OperationEmitter(Direction direction, File file) {
		this.direction = direction;
		operationsFile = file;
		determineOperationOffsets();
		currentOpIndex = direction == Direction.Forward ? 0 : backwardsOffsets.size() - 1;
		initReader();
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
			if (direction == Direction.Forward) {
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

	private void readForward(PipedOutputStream pos) {
		try {
			boolean withinOperationsElement = false;
			final boolean isForwardDir = direction == Direction.Forward;
			final String closingTag = getClosingTag(isForwardDir);
			String line = reader.readLine();
			while (line != null && !line.contains(closingTag)) {
				if (line.contains(getOpeningTag(isForwardDir))) {
					withinOperationsElement = true;
				} else if (withinOperationsElement) {
					pos.write(line.getBytes());
				}
				line = reader.readLine();
			}
			if (line != null) {
				withinOperationsElement = false;
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
			boolean withinOperationsElement = true;
			final String closingTag = getClosingTag(true);
			String line = reader.readLine();
			while (line != null && !line.contains(closingTag)) {
				if (line.contains(getOpeningTag(true))) {
					withinOperationsElement = true;
				} else if (withinOperationsElement && line.length() > 0) {
					pos.write(line.getBytes());
				}
				line = reader.readLine();
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

	private void readBackward(PipedOutputStream pos) {

		if (currentOpIndex < 0) {
			try {
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

	/**
	 * Tries to parse an operation in the reading directions and emits it,
	 * if parsing has been successful.
	 *
	 * @return the successfully parsed operation
	 * @throws IOException
	 *             in case reading from the {@link ReadLineCapable} fails
	 */
	public Optional<AbstractOperation> tryEmit() throws IOException {
		final PipedOutputStream pos = new PipedOutputStream();
		final PipedInputStream pis = new PipedInputStream(pos);

		new Thread(new Runnable() {
			public void run() {
				if (direction == Direction.Forward) {
					readForward(pos);
				} else {
					readBackward(pos);
				}
			}
		}).start();

		try {
			return Optional.of(deserialize(pis));
		} catch (final IOException e) {
			// e.printStackTrace();
			return Optional.absent();
		} finally {
			pis.close();
		}

	}

	private String getClosingTag(boolean isForward) {
		return isForward ? XmlTags.OPERATIONS_END_TAG : XmlTags.OPERATIONS_START_TAG;
	}

	private String getOpeningTag(boolean isForward) {
		return isForward ? XmlTags.OPERATIONS_START_TAG : XmlTags.OPERATIONS_END_TAG;
	}

	private AbstractOperation deserialize(final PipedInputStream pis) throws IOException {
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Resource resource = resourceSet.createResource(URI.createURI("virtualResource.xmi")); //$NON-NLS-1$
		((XMLResourceImpl) resource).setIntrinsicIDToEObjectMap(Maps.<String, EObject> newLinkedHashMap());
		final XMLLoadImpl xmlLoadImpl = new XMLLoadImpl(new XMLHelperImpl());
		xmlLoadImpl.load((XMLResource) resource, pis, ModelUtil.getResourceLoadOptions());
		final AbstractOperation operation = (AbstractOperation) resource.getContents().get(0);
		// ((XMLResourceImpl) resource).getIntrinsicIDToEObjectMap().clear();
		return operation;
	}

	/**
	 * Closes the emitter.
	 */
	public void close() {
		try {
			reader.close();
		} catch (final IOException ex) {
			ModelUtil.logException(ex);
		}
	}
}
