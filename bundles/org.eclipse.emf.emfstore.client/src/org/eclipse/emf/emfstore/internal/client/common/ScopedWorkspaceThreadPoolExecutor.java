/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller, Neil Mackenzie - initial API and implementation
 * 
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.common;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;

/**
 * This class extend ThreadPoolExecutor, and ensures than when each thread is asked
 * to run its task there is an initialization done on the thread first.
 * This initialization ensures that the static threadlocal ESWorkspaceProviderImpl
 * associated with the ESWorkspaceProviderImpl class is obtained from the thread
 * that is REQUESTING the thread pool to execute behavior, and set upon the thread
 * in the pool which actually EXECUTES the behavior.
 * 
 */
public class ScopedWorkspaceThreadPoolExecutor extends ThreadPoolExecutor {

	/**
	 * Default constructor.
	 */
	public ScopedWorkspaceThreadPoolExecutor() {
		super(
			10, // corePoolSize
			10, // maximumPoolSize
			10000, // keepAliveTime
			TimeUnit.SECONDS, // TimeUnit
			new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * <p>
	 * This overridden method ensures that the runnable is updated to have access threadlocal ESWorkspaceProviderImpl
	 * from the requesting thread set as the threadlocal ESWorkspaceProviderImpl of the executing thread.
	 * </p>
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor#execute(java.lang.Runnable)
	 */
	@Override
	public void execute(Runnable command) {
		final Runnable updatedRunnable = ESWorkspaceProviderImpl.initRunnable(command);
		super.execute(updatedRunnable);
	}

}
