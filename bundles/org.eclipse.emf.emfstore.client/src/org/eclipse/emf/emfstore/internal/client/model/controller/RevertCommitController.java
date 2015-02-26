/*******************************************************************************
 * Copyright (c) 2008-2015 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * // * Contributors:
 * Otto von Wesendonk, Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.controller;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ServerCall;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.CloseableIterable;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Versions;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;

/**
 * Controller that forces a revert of version specifier.
 *
 * @author ovonwesen
 * @author emueller
 */
public class RevertCommitController extends ServerCall<Void> {

	private final PrimaryVersionSpec versionSpec;
	private final boolean headRevert;
	private final String checkedoutCopyName;

	/**
	 * Constructor.
	 *
	 * @param projectSpace
	 *            the {@link ProjectSpace} containing the project upon which to revert a commit
	 * @param versionSpec
	 *            the target version to revert to
	 * @param headRevert
	 *            reverts HEAD if set to {@code true}, otherwise just revert individual version
	 * @param checkedoutCopyName
	 *            the name of the checkout that is going to be created
	 */
	public RevertCommitController(ProjectSpace projectSpace,
		PrimaryVersionSpec versionSpec, boolean headRevert, String checkedoutCopyName) {
		super(projectSpace);
		this.versionSpec = versionSpec;
		this.headRevert = headRevert;
		this.checkedoutCopyName = checkedoutCopyName;
	}

	private void checkoutHeadAndReverseCommit(final ProjectSpace projectSpace, final PrimaryVersionSpec baseVersion,
		boolean headRevert) throws ESException {

		final PrimaryVersionSpec localHead = getConnectionManager()
			.resolveVersionSpec(
				projectSpace.getUsersession().getSessionId(),
				projectSpace.getProjectId(),
				Versions.createHEAD(baseVersion));

		final ESLocalProjectImpl revertSpace = projectSpace.toAPI().getRemoteProject().checkout(
			checkedoutCopyName,
			projectSpace.getUsersession().toAPI(),
			getProgressMonitor());

		// TODO: LCP
		final List<ESChangePackage> changes = revertSpace.toInternalAPI().getChanges(
			baseVersion,
			headRevert ? localHead : ModelUtil.clone(baseVersion));

		Collections.reverse(changes);

		final Project project = revertSpace.toInternalAPI().getProject();

		for (final ESChangePackage changePackage : changes) {
			final CloseableIterable<AbstractOperation> reversedOperations = changePackage.reversedOperations();
			try {
				for (final AbstractOperation reversedOperation : reversedOperations.iterable()) {
					try {
						reversedOperation.apply(project);
					} catch (final IllegalStateException e) {
						// ignore all non-applied operations
					}
				}
			} finally {
				reversedOperations.close();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ServerCall#run()
	 */
	@Override
	protected Void run() throws ESException {
		return RunESCommand.WithException.runWithResult(ESException.class, new Callable<Void>() {
			public Void call() throws Exception {
				checkoutHeadAndReverseCommit(getProjectSpace(), versionSpec, headRevert);
				return null;
			}
		});
	}
}
