/*******************************************************************************
 * Copyright (c) 2012-2015 EclipseSource Muenchen GmbH and others.
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

import java.util.Collections;

import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESOperationImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

/**
 * A {@link Runnable} implementation that applies a given list of operations
 * onto a {@link ProjectSpaceBase}.
 *
 * @author emueller
 *
 */
public class ApplyOperationsRunnable implements Runnable {

	private final ProjectSpaceBase projectSpace;
	private final Iterable<AbstractOperation> operations;
	private final boolean addOperations;

	/**
	 * Constructor.
	 *
	 * @param projectSpaceBase
	 *            the {@link ProjectSpaceBase} onto which to apply the operations
	 * @param operations
	 *            the operations to be applied upon the project space
	 * @param addOperations
	 *            whether the operations should be added to the project space
	 */
	public ApplyOperationsRunnable(ProjectSpaceBase projectSpaceBase, Iterable<AbstractOperation> operations,
		boolean addOperations) {
		projectSpace = projectSpaceBase;
		this.operations = operations;
		this.addOperations = addOperations;
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
				projectSpace.stopChangeRecording();
				try {
					for (final AbstractOperation operation : operations) {
						final AbstractOperation internalOp = ESOperationImpl.class.cast(operation).toInternalAPI();
						try {
							internalOp.apply(projectSpace.getProject());
							// BEGIN SUPRESS CATCH EXCEPTION
						} catch (final RuntimeException e) {
							WorkspaceUtil.handleException(e);
							// END SUPRESS CATCH EXCEPTION
						}
						if (addOperations) {
							projectSpace.addOperations(Collections.singletonList(internalOp));
						}
					}
				} finally {
					if (projectSpace.getOperationManager() != null) {
						projectSpace.startChangeRecording();
					}
				}
			}
		}.run(false);
	}
}
