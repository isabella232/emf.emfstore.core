/*******************************************************************************
 * Copyright (c) 2012-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESLogMessageImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.ESOperation;

/**
 * Provides the author for an operation based on the usersession of the containing change package.
 *
 * @author mkoegel
 *
 */
public class DefaultOperationAuthorProvider implements OperationAuthorProvider {

	private static final String UNKOWN = "UNKOWN"; //$NON-NLS-1$
	private final Map<String, String> operationAuthorMap;

	/**
	 * Default Constructor.
	 *
	 * @param leftChanges a
	 *            list of change packages
	 * @param rightChanges
	 *            another list of change packages
	 */
	public DefaultOperationAuthorProvider(
		List<ESChangePackage> leftChanges,
		List<ESChangePackage> rightChanges) {

		operationAuthorMap = new LinkedHashMap<String, String>();

		for (final ESChangePackage changePackage : leftChanges) {
			scanIntoAuthorMap(changePackage);
		}
		for (final ESChangePackage changePackage : rightChanges) {
			scanIntoAuthorMap(changePackage);
		}

	}

	private void scanIntoAuthorMap(ESChangePackage changePackage) {

		if (changePackage.getLogMessage() == null) {
			return;
		}

		final LogMessage logMessage = ESLogMessageImpl.class.cast(changePackage.getLogMessage()).toInternalAPI();
		if (logMessage.getAuthor() != null) {
			final String author = logMessage.getAuthor();
			final ESCloseableIterable<ESOperation> operations = changePackage.operations();
			try {
				for (final ESOperation operation : operations.iterable()) {
					operationAuthorMap.put(operation.getId(), author);
				}
			} finally {
				operations.close();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.util.OperationAuthorProvider#getAuthor(org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation)
	 */
	public String getAuthor(AbstractOperation operation) {
		String author = operationAuthorMap.get(operation);
		if (author == null) {
			author = UNKOWN;
		}
		return author;
	}
}
