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

import org.eclipse.emf.emfstore.internal.server.model.FileIdentifier;

/**
 * A queue storing the files which will be uploaded to the server.
 */
public interface UploadQueue {

	/**
	 * @return the {@link FileIdentifier files} waiting to be uploaded
	 */
	List<FileIdentifier> getWaitingUploads();

	/**
	 * Enqueues a {@link FileIdentifier file} for upload.
	 *
	 * @param identifier the id of the file
	 */
	void add(FileIdentifier identifier);

	/**
	 * Removes a file from the upload queue.
	 *
	 * @param identifier the id of the file
	 */
	void remove(FileIdentifier identifier);

	/**
	 * Removes the file at the given index from the upload queue.
	 * 
	 * @param index the index of the {@link FileIdentifier file}
	 */
	void remove(int index);

}
