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

import java.util.List;

import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.server.model.FileIdentifier;

/**
 * {@link UploadQueue} implementation backed by a {@link ProjectSpaceBase}.
 */
public class ProjectSpaceUploadQueue implements UploadQueue {

	private final ProjectSpaceBase projectSpace;

	/**
	 * @param projectSpace the backing {@link ProjectSpaceBase}
	 */
	public ProjectSpaceUploadQueue(ProjectSpaceBase projectSpace) {
		this.projectSpace = projectSpace;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.model.filetransfer.util.UploadQueue#getWaitingUploads()
	 */
	public List<FileIdentifier> getWaitingUploads() {
		return projectSpace.getWaitingUploads();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.model.filetransfer.util.UploadQueue#add(org.eclipse.emf.emfstore.internal.server.model.FileIdentifier)
	 */
	public void add(final FileIdentifier identifier) {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				projectSpace.getWaitingUploads().add(identifier);
				projectSpace.saveProjectSpaceOnly();
			}
		}.run(true);

	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.model.filetransfer.util.UploadQueue#remove(org.eclipse.emf.emfstore.internal.server.model.FileIdentifier)
	 */
	public void remove(final FileIdentifier identifier) {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				projectSpace.getWaitingUploads().remove(identifier);
				projectSpace.saveProjectSpaceOnly();
			}
		}.run(true);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.model.filetransfer.util.UploadQueue#remove(int)
	 */
	public void remove(int index) {
		projectSpace.getWaitingUploads().remove(index);
		projectSpace.saveProjectSpaceOnly();
	}

}
