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
 * Edgar Mueller - Bug 460275 - Support lazy loading of local change package
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.impl.api;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.ESLogMessage;
import org.eclipse.emf.emfstore.server.model.ESOperation;

/**
 * Mapping between {@link ESChangePackage} and {@link ChangePackage}.
 *
 * @author emueller
 *
 */
public class ESChangePackageImpl extends ESAbstractChangePackageImpl<ChangePackage>implements ESChangePackage {

	/**
	 * Constructor.
	 *
	 * @param changePackage
	 *            the delegate
	 */
	public ESChangePackageImpl(ChangePackage changePackage) {
		super(changePackage);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#getLogMessage()
	 */
	public ESLogMessage getLogMessage() {
		if (toInternalAPI().getLogMessage() == null) {
			return null;
		}
		return toInternalAPI().getLogMessage().toAPI();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#setLogMessage(org.eclipse.emf.emfstore.server.model.ESLogMessage)
	 */
	public void setLogMessage(ESLogMessage logMessage) {
		final LogMessage logMsg = ESLogMessageImpl.class.cast(logMessage).toInternalAPI();
		toInternalAPI().setLogMessage(logMsg);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#addAll(java.util.List)
	 */
	public void addAll(List<ESOperation> ops) {
		final List<AbstractOperation> operations = APIUtil.toInternal(ops);
		toInternalAPI().getOperations().addAll(operations);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#add(org.eclipse.emf.emfstore.server.model.ESOperation)
	 */
	public void add(ESOperation op) {
		final AbstractOperation operation = ESOperationImpl.class.cast(op).toInternalAPI();
		toInternalAPI().getOperations().add(operation);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#clear()
	 */
	public void clear() {
		toInternalAPI().getOperations().clear();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#isEmpty()
	 */
	public boolean isEmpty() {
		return toInternalAPI().getOperations().isEmpty();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#removeFromEnd(int)
	 */
	public List<ESOperation> removeFromEnd(int n) {
		final List<ESOperation> collectedOperations = new ArrayList<ESOperation>();
		for (int i = 0; i < n; i++) {
			final int size = toInternalAPI().getOperations().size();
			final AbstractOperation operation = toInternalAPI().getOperations().get(size - 1);
			collectedOperations.add(operation.toAPI());
			toInternalAPI().getOperations().remove(size - 1);
		}
		return collectedOperations;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#operations()
	 */
	public ESCloseableIterable<ESOperation> operations() {
		return new ESCloseableIterable<ESOperation>() {

			public void close() {
				// nothing to do
			}

			public Iterable<ESOperation> iterable() {
				final EList<AbstractOperation> operations = toInternalAPI().getOperations();
				return APIUtil.toExternal(operations);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#size()
	 */
	public int size() {
		return toInternalAPI().getSize();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#reverse()
	 */
	public ESChangePackage reverse() {
		return toInternalAPI().reverse().toAPI();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.impl.api.ESAbstractChangePackageImpl#toInternalAPI()
	 */
	@Override
	public ChangePackage toInternalAPI() {
		return getChangePackage();
	}
}
