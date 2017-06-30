/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * neilmack - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.test.workspace;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.junit.Test;

/**
 * @author neilmack
 *
 */
public class ThreadLocalWorkSpaceTest {

	@Test
	public void test() {
		final Client client1 = new Client("1");
		final Client client2 = new Client("2");
		final ClientWithSubTask client3 = new ClientWithSubTask("3");
		client1.start();
		client2.start();
		client3.start();
		assertEquals("default", ESWorkspaceProviderImpl.getInstance().getName());
	}

	class Client extends Thread {

		public Client(String name) {
			setName(name);
		}

		/**
		 * {@inheritDoc}
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			final ESWorkspaceProviderImpl instance = ESWorkspaceProviderImpl.getInstance(getName());
			assertEquals("Workspace " + getName(), instance.getName());
			assertEquals("Workspace " + getName(), ESWorkspaceProviderImpl.getInstance().getName());
		}
	}

	class ClientWithSubTask extends Client {

		/**
		 * @param name
		 */
		public ClientWithSubTask(String name) {
			super(name);
		}

		/**
		 * {@inheritDoc}
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			final ESWorkspaceProviderImpl instance = ESWorkspaceProviderImpl.getInstance(getName());
			final String wsName = instance.getName();
			final Thread t = new Thread() {
				@Override
				public void run() {
					assertEquals(wsName, ESWorkspaceProviderImpl.getInstance().getName());
				}
			};
			t.start();

		}
	}
}