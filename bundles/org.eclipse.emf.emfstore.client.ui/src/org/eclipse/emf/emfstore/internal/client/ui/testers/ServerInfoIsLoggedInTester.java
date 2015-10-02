/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Aleksandar Shterev - initial API and implementation
 * Edgar Mueller - Bug 473284
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.testers;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * Property tester to test if the server info has been logged in.
 *
 * @author shterev
 */
public class ServerInfoIsLoggedInTester extends PropertyTester {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[],
	 *      java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, final Object expectedValue) {
		if (ServerInfo.class.isInstance(receiver) && expectedValue instanceof Boolean) {
			final ServerInfo serverInfo = ServerInfo.class.cast(receiver);
			final Usersession usersession = serverInfo.getLastUsersession();
			if (usersession == null) {
				return Boolean.FALSE.equals(expectedValue);
			}
			try {
				usersession.toAPI().refresh();
				return new Boolean(usersession.isLoggedIn()).equals(expectedValue);
			} catch (final ESException ex) {
				return Boolean.FALSE.equals(expectedValue);
			}
		}
		return false;
	}

}
