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

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

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
public class OperationEmitter extends AbstractOperationEmitter {

	/**
	 * Constructor.
	 *
	 * @param direction
	 *            the {@link Direction} that is used for reading
	 * @param file
	 *            the operation file
	 */
	public OperationEmitter(Direction direction, File file) {
		super(direction, file);
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
				if (getDirection() == Direction.Forward) {
					readForward(pos);
				} else {
					readBackward(pos);
				}
			}
		}).start();

		try {
			final EObject deserializedObject = deserialize(pis);
			if (AbstractOperation.class.isInstance(deserializedObject)) {
				return Optional.of(AbstractOperation.class.cast(deserializedObject));
			}
			return Optional.absent();
		} catch (final IOException e) {
			throw e;
		} finally {
			pis.close();
		}
	}

	private EObject deserialize(final PipedInputStream pis) throws IOException {
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Resource resource = resourceSet.createResource(URI.createURI("virtualResource.xmi")); //$NON-NLS-1$
		((XMLResourceImpl) resource).setIntrinsicIDToEObjectMap(Maps.<String, EObject> newLinkedHashMap());
		final XMLLoadImpl xmlLoadImpl = new XMLLoadImpl(new XMLHelperImpl());
		xmlLoadImpl.load((XMLResource) resource, pis, ModelUtil.getResourceLoadOptions());
		return resource.getContents().get(0);
	}
}
