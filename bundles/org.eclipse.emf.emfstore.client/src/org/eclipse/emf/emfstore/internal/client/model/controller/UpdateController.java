/*******************************************************************************
 * Copyright (c) 2008-2014 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk, Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.client.callbacks.ESUpdateCallback;
import org.eclipse.emf.emfstore.client.exceptions.ESProjectNotSharedException;
import org.eclipse.emf.emfstore.client.observer.ESUpdateObserver;
import org.eclipse.emf.emfstore.internal.client.common.UnknownEMFStoreWorkloadCommand;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ServerCall;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.ChangeConflictException;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ChangeConflictSet;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictDetector;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ModelElementIdToEObjectMappingImpl;
import org.eclipse.emf.emfstore.internal.server.impl.api.ESConflictSetImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Versions;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util.ChangePackageUtil;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;

import com.google.common.collect.Lists;

/**
 * Controller class for updating a project space.
 *
 * @author ovonwesen
 * @author emueller
 */
public class UpdateController extends ServerCall<PrimaryVersionSpec> {

	private final VersionSpec version;
	private final ESUpdateCallback callback;

	/**
	 * Constructor.
	 *
	 * @param projectSpace
	 *            the project space to be updated
	 * @param version
	 *            the target version
	 * @param callback
	 *            an optional update callback instance
	 * @param progress
	 *            a progress monitor that is used to indicate the progress of the update
	 */
	public UpdateController(ProjectSpaceBase projectSpace, VersionSpec version, ESUpdateCallback callback,
		IProgressMonitor progress) {
		super(projectSpace);

		if (!projectSpace.isShared()) {
			throw new ESProjectNotSharedException();
		}

		// SANITY CHECKS
		if (version == null) {
			version = Versions.createHEAD(projectSpace.getBaseVersion());
		}
		if (callback == null) {
			callback = ESUpdateCallback.NOCALLBACK;
		}

		this.version = version;
		this.callback = callback;
		setProgressMonitor(progress);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ServerCall#run()
	 */
	@Override
	protected PrimaryVersionSpec run() throws ESException {
		return doUpdate(version);
	}

	private PrimaryVersionSpec doUpdate(VersionSpec version) throws ChangeConflictException, ESException {
		getProgressMonitor().beginTask(Messages.UpdateController_UpdatingProject, 100);
		getProgressMonitor().worked(1);
		getProgressMonitor().subTask(Messages.UpdateController_ResolvingNewVersion);
		final PrimaryVersionSpec resolvedVersion = getProjectSpace().resolveVersionSpec(version, getProgressMonitor());
		if (equalsBaseVersion(resolvedVersion)) {
			return resolvedVersion;
		}
		getProgressMonitor().worked(5);

		if (getProgressMonitor().isCanceled()) {
			return getProjectSpace().getBaseVersion();
		}

		getProgressMonitor().subTask(Messages.UpdateController_FetchingChanges);

		final List<AbstractChangePackage> incomingChanges = getIncomingChanges(resolvedVersion);

		checkAndRemoveDuplicateOperations(incomingChanges);

		AbstractChangePackage copiedLocalChangedPackage = ChangePackageUtil.createChangePackage(
			Configuration.getClientBehavior().useInMemoryChangePackage()
			);
		final ESCloseableIterable<AbstractOperation> operations = getProjectSpace().getLocalChangePackage()
			.operations();
		try {
			for (final AbstractOperation operation : operations.iterable()) {
				copiedLocalChangedPackage.add(ModelUtil.clone(operation));
			}
		} finally {
			operations.close();
		}

		// build a mapping including deleted and create model elements in local and incoming change packages
		final ModelElementIdToEObjectMappingImpl idToEObjectMapping = new ModelElementIdToEObjectMappingImpl(
			getProjectSpace().getProject(), incomingChanges);
		idToEObjectMapping.put(copiedLocalChangedPackage);

		getProgressMonitor().worked(65);

		if (getProgressMonitor().isCanceled()) {
			return getProjectSpace().getBaseVersion();
		}

		getProgressMonitor().subTask(Messages.UpdateController_CheckingForConflicts);

		final List<ESChangePackage> incomingAPIChangePackages = new ArrayList<ESChangePackage>();
		for (final AbstractChangePackage incomingChange : incomingChanges) {
			incomingAPIChangePackages.add(incomingChange.toAPI());
		}

		// TODO ASYNC review this cancel
		if (getProgressMonitor().isCanceled()
			|| !callback.inspectChanges(
				getProjectSpace().toAPI(),
				incomingAPIChangePackages,
				idToEObjectMapping.toAPI())) {

			return getProjectSpace().getBaseVersion();
		}

		ESWorkspaceProviderImpl
		.getObserverBus()
		.notify(ESUpdateObserver.class, true)
		.inspectChanges(
			getProjectSpace().toAPI(),
			incomingAPIChangePackages,
			getProgressMonitor());

		if (!getProjectSpace().getLocalChangePackage().isEmpty()) {
			final ChangeConflictSet changeConflictSet = calcConflicts(copiedLocalChangedPackage, incomingChanges,
				idToEObjectMapping);
			if (changeConflictSet.getConflictBuckets().size() > 0) {
				getProgressMonitor().subTask(Messages.UpdateController_ConflictsDetected);
				if (callback.conflictOccurred(new ESConflictSetImpl(changeConflictSet), getProgressMonitor())) {

					final List<AbstractChangePackage> myChanges = Lists.newArrayList();
					myChanges.add(copiedLocalChangedPackage);

					copiedLocalChangedPackage = getProjectSpace().mergeResolvedConflicts(
						changeConflictSet,
						myChanges,
						incomingChanges);
					// continue with update by applying changes
				} else {
					throw new ChangeConflictException(changeConflictSet);
				}
			}
		}

		getProgressMonitor().worked(15);

		getProgressMonitor().subTask(Messages.UpdateController_ApplyingChanges);

		getProjectSpace().applyChanges(
			resolvedVersion,
			incomingChanges,
			copiedLocalChangedPackage,
			getProgressMonitor(),
			true);

		final Date now = new Date();
		getProjectSpace().setLastUpdated(now);

		ESWorkspaceProviderImpl.getObserverBus()
			.notify(ESUpdateObserver.class, true)
			.updateCompleted(getProjectSpace().toAPI(), getProgressMonitor());

		return getProjectSpace().getBaseVersion();
	}

	private boolean equalsBaseVersion(final PrimaryVersionSpec resolvedVersion) {
		return resolvedVersion.compareTo(getProjectSpace().getBaseVersion()) == 0;
	}

	private List<AbstractChangePackage> getIncomingChanges(final PrimaryVersionSpec resolvedVersion) throws ESException {
		final List<AbstractChangePackage> changePackages = new UnknownEMFStoreWorkloadCommand<List<AbstractChangePackage>>(
			getProgressMonitor()) {
			@Override
			public List<AbstractChangePackage> run(IProgressMonitor monitor) throws ESException {
				return getConnectionManager().getChanges(getSessionId(), getProjectSpace().getProjectId(),
					getProjectSpace().getBaseVersion(), resolvedVersion);
			}
		}.execute();
		return changePackages;
	}

	private ChangeConflictSet calcConflicts(AbstractChangePackage localChanges,
		List<AbstractChangePackage> changes, ModelElementIdToEObjectMappingImpl idToEObjectMapping) {

		final ConflictDetector conflictDetector = new ConflictDetector();
		return conflictDetector.calculateConflicts(
			Collections.singletonList(localChanges), changes, idToEObjectMapping);
	}

	private void checkAndRemoveDuplicateOperations(List<AbstractChangePackage> incomingChanges) {

		final int baseVersionDelta = removeFromChangePackages(incomingChanges);

		if (baseVersionDelta == 0) {
			return;
		}

		// some change have been matched, fix base version and save
		final PrimaryVersionSpec baseVersion = getProjectSpace().getBaseVersion();
		baseVersion.setIdentifier(baseVersion.getIdentifier() + baseVersionDelta);
		ModelUtil.logError(MessageFormat
			.format(
				Messages.UpdateController_ChangePackagesRemoved
				+ Messages.UpdateController_PullingUpBaseVersion,
				baseVersionDelta, baseVersion.getIdentifier(), baseVersion.getIdentifier() + baseVersionDelta));
		getProjectSpace().save();
	}

	/**
	 * Remove duplicate change packages from the change package.
	 *
	 * @param incomingChanges incoming change packages
	 * @return baseVersionDelta
	 */
	public int removeFromChangePackages(List<AbstractChangePackage> incomingChanges) {
		final Iterator<AbstractChangePackage> incomingChangesIterator = incomingChanges.iterator();
		int baseVersionDelta = 0;

		while (incomingChangesIterator.hasNext()) {
			final AbstractChangePackage incomingChangePackage = incomingChangesIterator.next();
			final boolean hasBeenConsumed = removeDuplicateOperations(
				incomingChangePackage,
				getProjectSpace().getLocalChangePackage());

			if (hasBeenConsumed) {
				baseVersionDelta += 1;
				incomingChangesIterator.remove();
			} else {
				break;
			}
		}

		return baseVersionDelta;
	}

	/**
	 * Remove duplicate operations.
	 *
	 * @param incomingChanges incoming change package
	 * @param localChanges local change package
	 * @return <code>true</code> when all change packages have been consumed
	 */
	public boolean removeDuplicateOperations(AbstractChangePackage incomingChanges, AbstractChangePackage localChanges) {

		// TODO: cleanup this mess, ensure compatibility with in-memory change package
		if (localChanges.size() == 0) {
			return false;
		}

		final AbstractChangePackage tempChangePackage = ChangePackageUtil.createChangePackage(
			Configuration.getClientBehavior().useInMemoryChangePackage()
			);
		final ESCloseableIterable<AbstractOperation> localOperations = localChanges.operations();
		final ESCloseableIterable<AbstractOperation> incomingOps = incomingChanges.operations();
		final int incomingOpsSize = incomingChanges.size();
		int incomingIdx = 0;
		boolean operationMatchingStarted = false;

		try {
			final Iterator<AbstractOperation> localOperationsIterator = localOperations.iterable().iterator();
			final Iterator<AbstractOperation> incomingOpsIterator = incomingOps.iterable().iterator();

			while (localOperationsIterator.hasNext()) {
				final AbstractOperation localOp = localOperationsIterator.next();
				if (incomingIdx == incomingOpsSize) {
					new EMFStoreCommand() {
						@Override
						protected void doRun() {
							tempChangePackage.add(ModelUtil.clone(localOp));
						}
					}.run(false);
					while (localOperationsIterator.hasNext()) {
						// add all remaining local ops
						final AbstractOperation next = localOperationsIterator.next();
						tempChangePackage.add(ModelUtil.clone(next));
					}

					// incoming change package is fully consumed, continue with next change package
					return true;
				}

				final AbstractOperation incomingOp = incomingOpsIterator.next();
				incomingIdx += 1;
				if (incomingOp.getIdentifier().equals(localOp.getIdentifier())) {
					operationMatchingStarted = true;
				} else {
					tempChangePackage.add(ModelUtil.clone(localOp));
					while (localOperationsIterator.hasNext()) {
						tempChangePackage.add(ModelUtil.clone(localOperationsIterator.next()));
					}
					if (operationMatchingStarted) {
						ModelUtil.logError(Messages.UpdateController_IncomingOperationsOnlyPartlyMatch);
						throw new IllegalStateException(Messages.UpdateController_IncomingOpsPartlyMatched);
					}
					// first operation of incoming change package does not match
					return false;
				}
			}

			// all incoming and local changes have been consumed
			if (incomingIdx == incomingOpsSize) {
				return true;
			}
			ModelUtil.logError(Messages.UpdateController_IncomingOperationsNotFullyConsumed);
			throw new IllegalStateException(Messages.UpdateController_IncomingOpsNotConsumed);
		} finally {
			localOperations.close();
			incomingOps.close();
			if (incomingIdx == incomingOpsSize) {
				tempChangePackage.attachToProjectSpace(getProjectSpace());
			}
		}
	}
}
