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
package org.eclipse.emf.emfstore.internal.common;

import org.eclipse.emf.emfstore.common.ESUIRunnableContext;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;

/**
 * Singleton for obtaining an {@link ESUIRunnableContext} instance.
 *
 * @author emueller
 *
 */
public final class ESUIRunnableContextProvider {

	private static ESUIRunnableContextProvider instance;
	private ESUIRunnableContext runnableContext;
	private boolean initialized;

	private ESUIRunnableContextProvider() {

	}

	/**
	 * Returns the {@link ESUIRunnableContextProvider} singleton instance.
	 *
	 * @return the {@link ESUIRunnableContextProvider} singleton
	 */
	public static ESUIRunnableContextProvider getInstance() {
		if (instance == null) {
			instance = new ESUIRunnableContextProvider();
		}
		return instance;
	}

	/**
	 * Embed the given {@link Runnable} into the context of the currently
	 * registered {@link ESUIRunnableContext} if any.
	 *
	 * @param runnable
	 *            the {@link Runnable} to be embedded
	 *
	 * @return the wrapped {@link Runnable}
	 */
	public Runnable embedInContext(Runnable runnable) {
		final ESUIRunnableContext runnableContext = getRunnableContext();
		if (runnableContext == null) {
			return runnable;
		}
		return runnableContext.createRunnable(runnable);
	}

	private ESUIRunnableContext getRunnableContext() {
		if (!initialized) {
			initialized = true;
			runnableContext = new ESExtensionPoint("org.eclipse.emf.emfstore.common.uiRunnableContext").getClass( //$NON-NLS-1$
				"class", ESUIRunnableContext.class); //$NON-NLS-1$
		}
		return runnableContext;
	}
}
