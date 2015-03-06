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
package org.eclipse.emf.emfstore.internal.server.model.versioning.persistent;

import java.util.List;

import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.common.api.AbstractAPIImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESOperationImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.ESLogMessage;
import org.eclipse.emf.emfstore.server.model.ESOperation;

/**
 * Mapping between {@link ESChangePackage} and {@link PersistentChangePackage}.
 *
 * @author emueller
 *
 * @since 1.5
 *
 */
public class ESPersistentChangePackageImpl extends AbstractAPIImpl<ESChangePackage, PersistentChangePackage>
	implements ESChangePackage {

	/**
	 * Constructor.
	 *
	 * @param changePackage
	 *            the delegate
	 */
	public ESPersistentChangePackageImpl(PersistentChangePackage changePackage) {
		super(changePackage);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#setLogMessage(org.eclipse.emf.emfstore.server.model.ESLogMessage)
	 */
	public void setLogMessage(ESLogMessage logMessage) {
		toInternalAPI().setCommitMessage(logMessage);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#addAll(java.util.List)
	 */
	public void addAll(List<ESOperation> ops) {
		final List<AbstractOperation> internalOps = APIUtil.toInternal(ops);
		toInternalAPI().addAll(internalOps);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#add(org.eclipse.emf.emfstore.server.model.ESOperation)
	 */
	public void add(ESOperation op) {
		toInternalAPI().add(ESOperationImpl.class.cast(op).toInternalAPI());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#clear()
	 */
	public void clear() {
		toInternalAPI().clear();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#isEmpty()
	 */
	public boolean isEmpty() {
		return toInternalAPI().isEmpty();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#removeFromEnd(int)
	 */
	public List<ESOperation> removeFromEnd(int n) {
		return toInternalAPI().removeFromEnd(n);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#operations()
	 */
	public ESCloseableIterable<ESOperation> operations() {
		return toInternalAPI().operations();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#size()
	 */
	public int size() {
		return toInternalAPI().size();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#reversedOperations()
	 */
	public ESCloseableIterable<ESOperation> reversedOperations() {
		return toInternalAPI().reversedOperations();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#getLogMessage()
	 */
	public ESLogMessage getLogMessage() {
		return toInternalAPI().getLogMessage();
	}
}
