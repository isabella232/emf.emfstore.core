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
package org.eclipse.emf.emfstore.common;

/**
 * Interface for providing a {@link Runnable} which will get executed on the UI Thread.
 *
 * @author emueller
 * @since 1.7
 *
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ESUIRunnableContext {

	/**
	 * The method is used to create a {@link Runnable} which will be executed on the UI thread. The returned
	 * {@link Runnable} is expected to wrap the given runnable.
	 *
	 * @param runnable the {@link Runnable} which will be executed by the runnable
	 * @return a {@link Runnable} which can be executed on the UI Thread in a safe fashion.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	Runnable createRunnable(Runnable runnable);
}
