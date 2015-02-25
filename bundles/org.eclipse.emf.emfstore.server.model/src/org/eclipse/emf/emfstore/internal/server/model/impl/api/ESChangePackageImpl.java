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
package org.eclipse.emf.emfstore.internal.server.model.impl.api;

import java.util.List;

import org.eclipse.emf.emfstore.internal.common.api.AbstractAPIImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.persistent.CloseableIterable;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.ESLogMessage;

/**
 * Mapping between {@link ESChangePackage} and {@link ChangePackage}.
 *
 * @author emueller
 *
 */
public class ESChangePackageImpl extends AbstractAPIImpl<ESChangePackage, ChangePackage> implements ESChangePackage {

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
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#getCommitMessage()
	 */
	public ESLogMessage getCommitMessage() {
		return toInternalAPI().getLogMessage().toAPI();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#setCommitMessage(org.eclipse.emf.emfstore.server.model.ESLogMessage)
	 */
	public void setCommitMessage(ESLogMessage logMessage) {
		final ESLogMessageImpl logMessageImpl = (ESLogMessageImpl) logMessage;
		toInternalAPI().setLogMessage(logMessageImpl.toInternalAPI());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#addAll(java.util.List)
	 */
	// TODO LCP - api type
	public void addAll(List<? extends AbstractOperation> ops) {
		toInternalAPI().addAll(ops);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#add(org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation)
	 */
	public void add(AbstractOperation op) {
		toInternalAPI().add(op);
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
	public List<AbstractOperation> removeFromEnd(int n) {
		return toInternalAPI().removeFromEnd(n);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#operations()
	 */
	public CloseableIterable<AbstractOperation> operations() {
		return toInternalAPI().operations();
	}

	public void save() {

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
	public CloseableIterable<AbstractOperation> reversedOperations() {
		return toInternalAPI().reversedOperations();
	}

}
