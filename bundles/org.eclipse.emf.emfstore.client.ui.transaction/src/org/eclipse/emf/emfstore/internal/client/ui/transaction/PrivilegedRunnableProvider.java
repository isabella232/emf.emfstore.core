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
package org.eclipse.emf.emfstore.internal.client.ui.transaction;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.emfstore.common.ESUIRunnableContext;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * Implementation of {@link ESUIRunnableContext} for creating
 * {@link TransactionalEditingDomain#createPrivilegedRunnable(Runnable) privileged runnables} to be executed on the UI
 * thread.
 *
 * @author jfaltermeier
 *
 */
public class PrivilegedRunnableProvider implements ESUIRunnableContext {

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.common.ESUIRunnableContext#createRunnable(java.lang.Runnable)
	 */
	public Runnable createRunnable(Runnable runnable) {
		final EditingDomain editingDomain = ESWorkspaceProviderImpl.getInstance().getEditingDomain();
		if (!TransactionalEditingDomain.class.isInstance(editingDomain)) {
			throw new IllegalStateException(
				"The PrivilegedRunnableProvider may only be used in conjunction with a Transactional Editing Domain"); //$NON-NLS-1$
		}
		final TransactionalEditingDomain transactionalEditingDomain = TransactionalEditingDomain.class
			.cast(editingDomain);
		try {
			final RunnableWithResult<?> privilegedRunnable = transactionalEditingDomain
				.createPrivilegedRunnable(runnable);
			return privilegedRunnable;
		} catch (final IllegalStateException ex) {
			/*
			 * createPrivilegedRunnable will fail if
			 * - there is no active transaction
			 * - the current thread is different than the thread of the transaction owner
			 * in this case we have no other option than to return the original runnable
			 */
			return runnable;
		}
	}

}
