package org.eclipse.emf.emfstore.internal.server.model.versioning.persistent;

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
import org.eclipse.emf.emfstore.internal.common.ResourceFactoryRegistry;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.persistent.FileBasedOperationIterable.Direction;

class OperationEmitter {

	private static final String NEWLINE = "\n"; //$NON-NLS-1$
	public static final String OPERATIONS_START_TAG = "<operations>"; //$NON-NLS-1$
	public static final String OPERATIONS_END_TAG = "</operations>"; //$NON-NLS-1$
	private static final String VIRTUAL_RESOURCE_URI = "virtualResource.xmi"; //$NON-NLS-1$
	private static final long NEWLINE_LENGTH = System.getProperty("line.separator").getBytes().length;

	boolean withinOperationsElement;
	private final Direction direction;
	private long offset;

	public OperationEmitter(Direction direction) {
		this.direction = direction;
		offset = 0;
	}

	public long getOffset() {
		return offset;
	}

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
		if (withinOperationsElement == false && !readLines.isEmpty()) {
			if (direction == Direction.Backward) {
				Collections.reverse(readLines);
			}
			return deserialize(StringUtils.join(readLines, ""));
		}

		return null;
	}

	// TODO: replace with enums?
	private String getClosingTag(boolean isForward) {
		return isForward ? OPERATIONS_END_TAG : OPERATIONS_START_TAG;
	}

	private String getOpeningTag(boolean isForward) {
		return isForward ? OPERATIONS_START_TAG : OPERATIONS_END_TAG;
	}

	private AbstractOperation deserialize(final String string) throws IOException {
		final ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.setResourceFactoryRegistry(new ResourceFactoryRegistry());
		final Resource resource = resourceSet.createResource(URI.createURI(VIRTUAL_RESOURCE_URI));
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(string.getBytes());
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		resource.load(inputStream, ModelUtil.getResourceLoadOptions());
		return (AbstractOperation) resource.getContents().get(0);

	}
}