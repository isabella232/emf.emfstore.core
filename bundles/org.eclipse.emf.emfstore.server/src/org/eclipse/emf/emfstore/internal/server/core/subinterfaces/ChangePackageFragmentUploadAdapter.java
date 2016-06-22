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
package org.eclipse.emf.emfstore.internal.server.core.subinterfaces;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

import com.google.common.base.Optional;

/**
 * Adapter that is meant to be attached to a session and stores all change package fragments.
 *
 * @author emueller
 *
 */
public class ChangePackageFragmentUploadAdapter extends AdapterImpl {

	// maps proxy ID to file-based change package
	private final Map<String, File> proxyIdToChangePackageFragments = new LinkedHashMap<String, File>();
	private final Map<String, File> proxyIdToCompletedChangePackages = new LinkedHashMap<String, File>();

	/**
	 * Adds a single fragment.
	 *
	 * @param proxyId
	 *            the ID identifying the list of fragments this fragment belongs to
	 * @param fragment
	 *            the actual fragment to be added
	 * @throws ESException in case the fragment could not be added
	 */
	public void addFragment(String proxyId, List<String> fragment) throws ESException {
		File file = proxyIdToChangePackageFragments.get(proxyId);
		if (file == null) {
			file = new File(FileUtil.createLocationForTemporaryChangePackage() + ".temp"); //$NON-NLS-1$
			proxyIdToChangePackageFragments.put(proxyId, file);
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
			for (final String str : fragment) {
				writer.write(str + System.getProperty("line.separator")); //$NON-NLS-1$
			}
		} catch (final IOException ex) {
			throw new ESException(Messages.ChangePackageFragmentUploadAdapter_SplittingFailed, ex);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (final IOException ex) {
					throw new ESException(Messages.ChangePackageFragmentAdapter_SaveChangePackageFailed, ex);
				}
			}
		}
	}

	/**
	 * Mark a list of change package as completed.
	 *
	 * @param proxyId
	 *            the ID of the set of change package fragments that is supposed to be completed
	 * @throws ESException in case
	 */
	public void markAsComplete(String proxyId) throws ESException {
		final File possiblyCompletedChangePackage = proxyIdToCompletedChangePackages.get(proxyId);
		final File fileBasedChangePackage = proxyIdToChangePackageFragments.get(proxyId);

		if (possiblyCompletedChangePackage != null) {
			throw new ESException(Messages.ChangePackageFragmentUploadAdapter_ChangePackageAlreadyComplete);
		}

		if (fileBasedChangePackage == null) {
			throw new ESException(
				MessageFormat.format(
					Messages.ChangePackageFragmentUploadAdapter_NoChangePackageFragmentsFound, proxyId));
		}

		proxyIdToCompletedChangePackages.put(proxyId, fileBasedChangePackage);
		proxyIdToChangePackageFragments.remove(proxyId);
	}

	/**
	 * Returns the aggregated change package. Fragments considered as complete need to be marked as such by
	 * calling {{@link #markAsComplete(String)}.
	 *
	 * @param proxyId
	 *            the ID that identifies a list of change package fragments from which the change package
	 *            will be created
	 * @return the aggregated {@link ChangePackage} as an {@link Optional}
	 */
	public Optional<ChangePackage> convertFileBasedToInMemoryChangePackage(String proxyId) {
		final File file = proxyIdToCompletedChangePackages.get(proxyId);
		if (file == null) {
			return Optional.absent();
		}
		final FileBasedChangePackage cp = VersioningFactory.eINSTANCE.createFileBasedChangePackage();
		final String path = file.getAbsolutePath().substring(0,
			file.getAbsolutePath().length() - ".temp".length()); //$NON-NLS-1$
		cp.setFilePath(path);

		final ESCloseableIterable<AbstractOperation> operationsHandle = cp.operations();
		final ChangePackage changePackage = VersioningFactory.eINSTANCE.createChangePackage();
		try {
			for (final AbstractOperation operation : operationsHandle.iterable()) {
				changePackage.add(ModelUtil.clone(operation));
			}
		} finally {
			operationsHandle.close();
		}

		return Optional.of(changePackage);
	}

	/**
	 * Returns the temporary file based changepackage for the given proxy id, if present. The change package is backed
	 * by temporary files on server side, so {@link FileBasedChangePackage#move(String)} might be needed.
	 *
	 * @param proxyId the ID that identifies the {@link FileBasedChangePackage}
	 * @return the file based change package as an {@link Optional}
	 */
	public Optional<FileBasedChangePackage> getFileBasedChangePackage(String proxyId) {
		final File file = proxyIdToCompletedChangePackages.get(proxyId);
		if (file == null) {
			return Optional.absent();
		}
		final FileBasedChangePackage cp = VersioningFactory.eINSTANCE.createFileBasedChangePackage();
		final String path = file.getAbsolutePath().substring(0,
			file.getAbsolutePath().length() - ".temp".length()); //$NON-NLS-1$
		cp.setFilePath(path);
		try {
			cp.save();
		} catch (final IOException ex) {
			// TODO: use checked exception?
			throw new IllegalStateException(ex);
		}
		return Optional.of(cp);
	}

	/**
	 * Removes the completed change package matching the given ID.
	 *
	 * @param proxyId
	 *            the ID of the change package proxy
	 */
	public void clearCompleted(final String proxyId) {
		proxyIdToCompletedChangePackages.remove(proxyId);
	}
}