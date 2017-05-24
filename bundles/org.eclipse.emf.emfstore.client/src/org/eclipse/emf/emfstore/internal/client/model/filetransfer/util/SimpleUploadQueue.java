/*******************************************************************************
 * Copyright (c) 2011-2017 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.filetransfer.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.emfstore.internal.server.model.FileIdentifier;

/**
 * Simple in memory representation of an {@link UploadQueue}.
 */
public class SimpleUploadQueue implements UploadQueue {

	private final List<FileIdentifier> fileIdentifiers = new ArrayList<FileIdentifier>();

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.model.filetransfer.util.UploadQueue#getPendingUploads()
	 */
	public List<FileIdentifier> getPendingUploads() {
		return fileIdentifiers;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.model.filetransfer.util.UploadQueue#add(org.eclipse.emf.emfstore.internal.server.model.FileIdentifier)
	 */
	public void add(FileIdentifier identifier) {
		fileIdentifiers.add(identifier);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.model.filetransfer.util.UploadQueue#remove(org.eclipse.emf.emfstore.internal.server.model.FileIdentifier)
	 */
	public void remove(FileIdentifier identifier) {
		fileIdentifiers.remove(identifier);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.model.filetransfer.util.UploadQueue#remove(int)
	 */
	public void remove(int index) {
		fileIdentifiers.remove(index);
	}

}
