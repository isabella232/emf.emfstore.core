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
package org.eclipse.emf.emfstore.internal.server.model.impl.api;

import java.util.List;

import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.ESLogMessage;
import org.eclipse.emf.emfstore.server.model.ESOperation;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Mapping between {@link ESChangePackage} and {@link FileBasedChangePackage}.
 *
 * @author emueller
 *
 * @since 1.5
 *
 */
public class ESFileBasedChangePackageImpl extends ESAbstractChangePackageImpl<FileBasedChangePackage>
	implements ESChangePackage {

	/**
	 * Constructor.
	 *
	 * @param changePackage
	 *            the delegate
	 */
	public ESFileBasedChangePackageImpl(FileBasedChangePackage changePackage) {
		super(changePackage);
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
		final List<AbstractOperation> removedOperations = toInternalAPI().removeAtEnd(n);
		return APIUtil.toExternal(removedOperations);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#operations()
	 */
	public ESCloseableIterable<ESOperation> operations() {
		final ESCloseableIterable<AbstractOperation> operations = toInternalAPI().operations();
		return new ESCloseableIterable<ESOperation>() {

			public void close() {
				operations.close();
			}

			public Iterable<ESOperation> iterable() {
				final Function<AbstractOperation, ESOperation> toESOperation = new Function<AbstractOperation, ESOperation>() {
					public ESOperation apply(AbstractOperation arg0) {
						return new ESOperationImpl(arg0);
					}
				};
				final Iterable<AbstractOperation> iterable = operations.iterable();
				return Iterables.transform(iterable, toESOperation);
			}
		};
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
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#reverse()
	 */
	public ESChangePackage reverse() {
		final FileBasedChangePackage reversedChangePackage = toInternalAPI();
		return reversedChangePackage.toAPI();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#getLogMessage()
	 */
	public ESLogMessage getLogMessage() {
		final LogMessage logMessage = toInternalAPI().getLogMessage();
		if (logMessage == null) {
			return null;
		}
		return logMessage.toAPI();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.impl.api.ESAbstractChangePackageImpl#toInternalAPI()
	 */
	@Override
	public FileBasedChangePackage toInternalAPI() {
		return getChangePackage();
	}
}
