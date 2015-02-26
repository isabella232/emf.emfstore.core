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

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.client.model.CompositeOperationHandle;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.InvalidHandleException;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESOperationImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.server.model.ESOperation;

/**
 * A {@link Runnable} implementation that applies a given list of operations
 * onto a {@link ProjectSpaceBase}.
 *
 * @author emueller
 *
 */
public class ApplyOperationsAndRecordRunnable implements Runnable {

	private final ProjectSpaceBase projectSpace;
	private final Iterable<ESOperation> operations;

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
		Iterable<ESOperation> operations) {

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
				for (final ESOperation operation : operations) {
					try {
						applyOperation(operation);
						// BEGIN SUPRESS CATCH EXCEPTION
					} catch (final RuntimeException e) {
						WorkspaceUtil.handleException(e);
						// END SUPRESS CATCH EXCEPTION
					}
				}
			}

		}.run(false);
	}

	private void applyOperation(final ESOperation operation) {
		projectSpace.getOperationManager().commandStarted(null);
		final AbstractOperation internalOp = ESOperationImpl.class.cast(operation).toInternalAPI();

		if (CompositeOperation.class.isInstance(operation)) {
			final CompositeOperation compositeOperation = CompositeOperation.class.cast(operation);
			final String compositeName = compositeOperation.getCompositeName();
			final CompositeOperationHandle handle = projectSpace.getOperationManager()
				.beginCompositeOperation();
			internalOp.apply(projectSpace.getProject());
			try {
				handle.end(compositeName, StringUtils.EMPTY, compositeOperation.getModelElementId());
				projectSpace.getOperationManager().commandCompleted(null, true);
			} catch (final InvalidHandleException ex) {
				WorkspaceUtil.logException(ex.getMessage(), ex);
			}
		} else {
			internalOp.apply(projectSpace.getProject());
			projectSpace.getOperationManager().commandCompleted(null, true);
		}
	}
}
