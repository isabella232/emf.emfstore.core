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
import org.eclipse.emf.emfstore.internal.server.model.versioning.persistent.PersistentChangePackage;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.ESLogMessage;

/**
 * Mapping between {@link ESChangePackage} and {@link ChangePackage}.
 * 
 * @author emueller
 * 
 */
public class ESPersistentChangePackageImpl extends AbstractAPIImpl<ESChangePackage, PersistentChangePackage> implements
	ESChangePackage {

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
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#getCommitMessage()
	 */
	public ESLogMessage getCommitMessage() {
		return toInternalAPI().getCommitMessage();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#setCommitMessage(org.eclipse.emf.emfstore.server.model.ESLogMessage)
	 */
	public void setCommitMessage(ESLogMessage logMessage) {
		toInternalAPI().setCommitMessage(logMessage);
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
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#clear()
	 */
	public void clear() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#isEmpty()
	 */
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#removeFromEnd(int)
	 */
	public List<AbstractOperation> removeFromEnd(int n) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#operations()
	 */
	public Iterable<AbstractOperation> operations() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#save()
	 */
	public void save() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#size()
	 */
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#reversedOperations()
	 */
	public Iterable<AbstractOperation> reversedOperations() {
		// TODO Auto-generated method stub
		return null;
	}

}
