/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.impl.api;

import java.util.concurrent.Callable;

import org.eclipse.emf.emfstore.client.ESCompositeOperationHandle;
import org.eclipse.emf.emfstore.client.exceptions.ESInvalidCompositeOperationException;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.common.model.ESModelElementId;
import org.eclipse.emf.emfstore.internal.client.model.CompositeOperationHandle;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.InvalidHandleException;
import org.eclipse.emf.emfstore.internal.common.api.AbstractAPIImpl;
import org.eclipse.emf.emfstore.internal.common.model.impl.ESModelElementIdImpl;

/**
 * Implementation of {@link ESCompositeOperationHandle}.
 *
 * @author Johannes Faltermeier
 *
 */
public class ESCompositeOperationHandleImpl extends
	AbstractAPIImpl<ESCompositeOperationHandleImpl, CompositeOperationHandle> implements ESCompositeOperationHandle {

	/**
	 * @param internal the non-API instance
	 */
	public ESCompositeOperationHandleImpl(CompositeOperationHandle internal) {
		super(internal);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.ESCompositeOperationHandle#isValid()
	 */
	public boolean isValid() {
		return RunESCommand.runWithResult(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return toInternalAPI().isValid();
			}
		});
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.ESCompositeOperationHandle#abort()
	 */
	public void abort() throws ESInvalidCompositeOperationException {
		RunESCommand.WithException.run(ESInvalidCompositeOperationException.class, new Callable<Void>() {
			public Void call() throws ESInvalidCompositeOperationException {
				try {
					toInternalAPI().abort();
					return null;
				} catch (final InvalidHandleException ex) {
					throw new ESInvalidCompositeOperationException(ex.getMessage());
				}
			}
		});
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.ESCompositeOperationHandle#end(java.lang.String, java.lang.String,
	 *      org.eclipse.emf.emfstore.common.model.ESModelElementId)
	 */
	public void end(final String name, final String description, final ESModelElementId modelElementId)
		throws ESInvalidCompositeOperationException {
		RunESCommand.WithException.run(ESInvalidCompositeOperationException.class, new Callable<Void>() {
			public Void call() throws ESInvalidCompositeOperationException {
				try {
					toInternalAPI().end(name, description,
						ESModelElementIdImpl.class.cast(modelElementId).toInternalAPI());
					return null;
				} catch (final InvalidHandleException ex) {
					throw new ESInvalidCompositeOperationException(ex.getMessage());
				}
			}
		});
	}

}
