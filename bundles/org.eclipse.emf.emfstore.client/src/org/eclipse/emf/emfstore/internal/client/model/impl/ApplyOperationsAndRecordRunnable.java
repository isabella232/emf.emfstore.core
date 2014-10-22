/*******************************************************************************
 * Copyright (c) 2012-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.client.model.CompositeOperationHandle;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.InvalidHandleException;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;

/**
 * A {@link Runnable} implementation that applies a given list of operations
 * onto a {@link ProjectSpaceBase}.
 * 
 * @author emueller
 * 
 */
public class ApplyOperationsAndRecordRunnable implements Runnable {

	private final ProjectSpaceBase projectSpace;
	private final List<AbstractOperation> operations;

	/**
	 * Constructor.
	 * 
	 * @param projectSpaceBase
	 *            the {@link ProjectSpaceBase} onto which to apply the operations
	 * @param operations
	 *            the operations to be applied upon the project space
	 */
	public ApplyOperationsAndRecordRunnable(
		ProjectSpaceBase projectSpaceBase,
		List<AbstractOperation> operations) {
		projectSpace = projectSpaceBase;
		this.operations = operations;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				for (final AbstractOperation operation : operations) {
					try {
						projectSpace.getOperationManager().commandStarted(null);

						if (CompositeOperation.class.isInstance(operation)) {
							final CompositeOperation compositeOperation = CompositeOperation.class.cast(operation);
							final String compositeName = compositeOperation.getCompositeName();
							final CompositeOperationHandle handle = projectSpace.getOperationManager()
								.beginCompositeOperation();
							operation.apply(projectSpace.getProject());
							try {
								handle.end(compositeName, StringUtils.EMPTY, compositeOperation.getModelElementId());
								projectSpace.getOperationManager().commandCompleted(null, false);
							} catch (final InvalidHandleException ex) {
								WorkspaceUtil.logException(ex.getMessage(), ex);
							}
						} else {
							operation.apply(projectSpace.getProject());
							projectSpace.getOperationManager().commandCompleted(null, false);
						}
						// BEGIN SUPRESS CATCH EXCEPTION
					} catch (final RuntimeException e) {
						WorkspaceUtil.handleException(e);
					}
				}
			}
		}.run(false);
	}
}
