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

import org.eclipse.emf.emfstore.common.ESRunnableWrapper;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;

/**
 * Singleton for obtaining an {@link ESRunnableWrapper} instance.
 *
 * @author emueller
 *
 */
public final class ESRunnableWrapperProvider {

	private static ESRunnableWrapperProvider instance;
	private ESRunnableWrapper runnableContext;
	private boolean initialized;

	private ESRunnableWrapperProvider() {

	}

	/**
	 * Returns the {@link ESRunnableWrapperProvider} singleton instance.
	 *
	 * @return the {@link ESRunnableWrapperProvider} singleton
	 */
	public static ESRunnableWrapperProvider getInstance() {
		if (instance == null) {
			instance = new ESRunnableWrapperProvider();
		}
		return instance;
	}

	/**
	 * Embed the given {@link Runnable} into the context of the currently
	 * registered {@link ESRunnableWrapper} if any.
	 *
	 * @param runnable
	 *            the {@link Runnable} to be embedded
	 *
	 * @return the wrapped {@link Runnable}
	 */
	public Runnable embedInContext(Runnable runnable) {
		final ESRunnableWrapper runnableWrapper = getRunnableWrapper();
		if (runnableWrapper == null) {
			return runnable;
		}
		return runnableWrapper.createRunnable(runnable);
	}

	private ESRunnableWrapper getRunnableWrapper() {
		if (!initialized) {
			initialized = true;
			runnableContext = new ESExtensionPoint("org.eclipse.emf.emfstore.common.runnableWrapper").getClass( //$NON-NLS-1$
				"class", ESRunnableWrapper.class); //$NON-NLS-1$
		}
		return runnableContext;
	}
}
