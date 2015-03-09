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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CreateDeleteOperation;

/**
 * Type for emitting {@link AbstractOperation}s when given an {@link ReadLineCapable} type.
 *
 * @author emueller
 *
 */
public class OperationEmitter {

	private static final long NEWLINE_LENGTH = System.getProperty("line.separator").getBytes().length; //$NON-NLS-1$

	private boolean withinOperationsElement;
	private final Direction direction;
	private long offset;

	/**
	 * Constructor.
	 *
	 * @param direction
	 *            the {@link Direction} that is used for reading
	 */
	public OperationEmitter(Direction direction) {
		this.direction = direction;
		offset = 0;
	}

	/**
	 * Returns the current offset.
	 *
	 * @return the current offset
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * Given a reader, tries to parse an operation and emit it is,
	 * if parsing has been successful.
	 *
	 * @param reader
	 *            the reader that is used to de-serialize operations
	 * @return the successfully parsed operation
	 * @throws IOException
	 *             in case reading from the {@link ReadLineCapable} fails
	 */
	public AbstractOperation tryEmit(ReadLineCapable reader) throws IOException {
		final List<String> readLines = new ArrayList<String>();
		withinOperationsElement = false;
		String line;
		final boolean isForwardDir = direction == Direction.Forward;
		while ((line = reader.readLine()) != null && !line.contains(getClosingTag(isForwardDir))) {
			if (line.contains(getOpeningTag(isForwardDir))) {
				withinOperationsElement = true;
			} else if (withinOperationsElement) {
				readLines.add(line);
			}
			offset += line.getBytes().length;
			offset += NEWLINE_LENGTH;
		}
		if (line != null) {
			withinOperationsElement = false;
			offset += line.getBytes().length;
		}
		if (!withinOperationsElement && !readLines.isEmpty()) {
			if (direction == Direction.Backward) {
				Collections.reverse(readLines);
			}
			return deserialize(StringUtils.join(readLines, StringUtils.EMPTY));
		}

		return null;
	}

	private String getClosingTag(boolean isForward) {
		return isForward ? XmlTags.OPERATIONS_END_TAG : XmlTags.OPERATIONS_START_TAG;
	}

	private String getOpeningTag(boolean isForward) {
		return isForward ? XmlTags.OPERATIONS_START_TAG : XmlTags.OPERATIONS_END_TAG;
	}

	private AbstractOperation deserialize(final String string) throws IOException {
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Resource resource = resourceSet.createResource(URI.createURI("virtualResource.xmi")); //$NON-NLS-1$
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(string.getBytes());
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		resource.load(inputStream, ModelUtil.getResourceLoadOptions());
		final AbstractOperation operation = (AbstractOperation) resource.getContents().get(0);
		if (operation instanceof CreateDeleteOperation) {
			((CreateDeleteOperation) operation).getSubOperations();
		}
		return operation;

	}
}