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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.client.callbacks.ESUpdateCallback;
import org.eclipse.emf.emfstore.client.exceptions.ESProjectNotSharedException;
import org.eclipse.emf.emfstore.client.observer.ESUpdateObserver;
import org.eclipse.emf.emfstore.client.util.ESClientURIUtil;
import org.eclipse.emf.emfstore.internal.client.common.UnknownEMFStoreWorkloadCommand;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ServerCall;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.ChangeConflictException;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ChangeConflictSet;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictDetector;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ModelElementIdToEObjectMappingImpl;
import org.eclipse.emf.emfstore.internal.server.impl.api.ESConflictSetImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.CloseableIterable;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Versions;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.persistent.PersistentChangePackage;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;

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

		final List<ESChangePackage> incomingChanges = getIncomingChanges(resolvedVersion);

		checkAndRemoveDuplicateOperations(incomingChanges);

		// TODO: LCP - getLocalChangePackage actually causes copy
		final ESChangePackage localChanges2 = getProjectSpace().getLocalChangePackage(false);

		final ESChangePackage changePackage = getProjectSpace().changePackage();
		ESChangePackage copiedLocalChangedPackage = VersioningFactory.eINSTANCE.createChangePackage();
		final CloseableIterable<AbstractOperation> operations = changePackage.operations();
		try {
			for (final AbstractOperation operation : operations.iterable()) {
				copiedLocalChangedPackage.add(operation);
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

		// TODO ASYNC review this cancel
		if (getProgressMonitor().isCanceled()
			|| !callback.inspectChanges(getProjectSpace().toAPI(), incomingChanges, idToEObjectMapping.toAPI())) {
			return getProjectSpace().getBaseVersion();
		}

		ESWorkspaceProviderImpl
			.getObserverBus()
			.notify(ESUpdateObserver.class, true)
			.inspectChanges(getProjectSpace().toAPI(), incomingChanges, getProgressMonitor());

		if (getProjectSpace().changePackage().size() > 0) {
			final ChangeConflictSet changeConflictSet = calcConflicts(copiedLocalChangedPackage, incomingChanges,
				idToEObjectMapping);
			if (changeConflictSet.getConflictBuckets().size() > 0) {
				getProgressMonitor().subTask(Messages.UpdateController_ConflictsDetected);
				if (callback.conflictOccurred(new ESConflictSetImpl(changeConflictSet), getProgressMonitor())) {
					copiedLocalChangedPackage = getProjectSpace().mergeResolvedConflicts(changeConflictSet,
						Collections.singletonList(copiedLocalChangedPackage),
						incomingChanges);
					// continue with update by applying changes
				} else {
					throw new ChangeConflictException(changeConflictSet);
				}
			}
		}

		getProgressMonitor().worked(15);

		getProgressMonitor().subTask(Messages.UpdateController_ApplyingChanges);

		getProjectSpace().applyChanges(resolvedVersion, incomingChanges, copiedLocalChangedPackage,
			getProgressMonitor(), true);

		ESWorkspaceProviderImpl.getObserverBus().notify(ESUpdateObserver.class, true)
			.updateCompleted(getProjectSpace().toAPI(), getProgressMonitor());

		return getProjectSpace().getBaseVersion();
	}

	private void save(EObject eObject, String failureMsg) {
		try {
			if (eObject.eResource() != null) {
				eObject.eResource().save(ModelUtil.getResourceSaveOptions());
			}
		} catch (final IOException ex) {
			ModelUtil.logException(failureMsg, ex);
		}
	}

	private boolean equalsBaseVersion(final PrimaryVersionSpec resolvedVersion) {
		return resolvedVersion.compareTo(getProjectSpace().getBaseVersion()) == 0;
	}

	private List<ESChangePackage> getIncomingChanges(final PrimaryVersionSpec resolvedVersion) throws ESException {
		final List<ChangePackage> changePackages = new UnknownEMFStoreWorkloadCommand<List<ChangePackage>>(
			getProgressMonitor()) {
			@Override
			public List<ChangePackage> run(IProgressMonitor monitor) throws ESException {
				return getConnectionManager().getChanges(getSessionId(), getProjectSpace().getProjectId(),
					getProjectSpace().getBaseVersion(), resolvedVersion);
			}
		}.execute();
		return APIUtil.mapToAPI(ESChangePackage.class, changePackages);
	}

	private ChangeConflictSet calcConflicts(ESChangePackage localChanges,
		List<ESChangePackage> changes, ModelElementIdToEObjectMappingImpl idToEObjectMapping) {

		final ConflictDetector conflictDetector = new ConflictDetector();
		return conflictDetector.calculateConflicts(
			Collections.singletonList(localChanges), changes, idToEObjectMapping);
	}

	private void checkAndRemoveDuplicateOperations(List<ESChangePackage> incomingChanges) {

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
		save(getProjectSpace(), Messages.UpdateController_ProjectSpace_SaveFailed);
		// TODO: LCP - if localChanges is a PersistentChangePackage, saving should always occur automatically
		// save(localChanges, Messages.UpdateController_LocalChanges_SaveFailed);
	}

	/**
	 * Remove duplicate change packages from the change package.
	 *
	 * @param incomingChanges incoming change packages
	 * @param localChanges local change package
	 * @return baseVersionDelta
	 */
	public int removeFromChangePackages(List<ESChangePackage> incomingChanges) {
		final Iterator<ESChangePackage> incomingChangesIterator = incomingChanges.iterator();
		int baseVersionDelta = 0;

		while (incomingChangesIterator.hasNext()) {
			final ESChangePackage incomingChangePackage = incomingChangesIterator.next();
			final boolean hasBeenConsumed = removeDuplicateOperations(incomingChangePackage, getProjectSpace()
				.changePackage());
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
	public boolean removeDuplicateOperations(ESChangePackage incomingChanges, ESChangePackage localChanges) {

		if (localChanges.size() == 0) {
			return false;
		}

		File tempFile = null;
		try {
			tempFile = File.createTempFile("ops", "eoc");
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		final PersistentChangePackage tempChangePackage = new PersistentChangePackage(tempFile.getAbsolutePath());
		final CloseableIterable<AbstractOperation> localOperations = localChanges.operations();
		final Iterator<AbstractOperation> localOperationsIterator = localOperations.iterable().iterator();
		// final Iterable<AbstractOperation> incomingOps = incomingChanges.operations().iterable();
		final CloseableIterable<AbstractOperation> incomingOps = incomingChanges.operations();
		final Iterator<AbstractOperation> incomingOpsIterator = incomingOps.iterable().iterator();
		final int incomingOpsSize = incomingChanges.size();
		int incomingIdx = 0;
		boolean operationMatchingStarted = false;

		try {
			while (localOperationsIterator.hasNext()) {
				final AbstractOperation localOp = localOperationsIterator.next();
				if (incomingIdx == incomingOpsSize) {
					tempChangePackage.add(localOp);
					while (localOperationsIterator.hasNext()) {
						// add all remaining local ops
						final AbstractOperation next = localOperationsIterator.next();
						tempChangePackage.add(next);
					}

					// incoming change package is fully consumed, continue with next change package
					return true;
				}

				final AbstractOperation incomingOp = incomingOpsIterator.next();
				incomingIdx += 1;
				if (incomingOp.getIdentifier().equals(localOp.getIdentifier())) {
					operationMatchingStarted = true;
				} else {
					tempChangePackage.add(localOp);
					if (operationMatchingStarted) {
						ModelUtil.logError(Messages.UpdateController_IncomingOperationsOnlyPartlyMatch);
						throw new IllegalStateException("Incoming operations only partly match with local."); //$NON-NLS-1$
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
			throw new IllegalStateException("Incoming operations are not fully consumed."); //$NON-NLS-1$
		} finally {
			localOperations.close();
			incomingOps.close();
			if (incomingIdx == incomingOpsSize) {
				final URI operationsURI = ESClientURIUtil.createOperationsURI(getProjectSpace());
				final URI normalizedOperationUri = ESWorkspaceProviderImpl.getInstance().getInternalWorkspace()
					.getResourceSet().getURIConverter().normalize(operationsURI);
				final String operationFileString = normalizedOperationUri.toFileString();
				final File operationFile = new File(operationFileString);
				final boolean delete = operationFile.delete();
				try {
					if (!delete) {
						System.out.println();
					}
					moveAndOverwrite(tempFile, operationFile);
					((ProjectSpaceBase) getProjectSpace()).setPersistentChangePackage(new PersistentChangePackage(
						operationFile.getAbsolutePath()));
				} catch (final IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static void moveAndOverwrite(File source, File dest) throws IOException {
		// Backup the src
		FileUtils.copyFile(source, dest);
		if (!source.delete()) {
			throw new IOException("Failed to delete " + source.getName());
		}
	}
}
