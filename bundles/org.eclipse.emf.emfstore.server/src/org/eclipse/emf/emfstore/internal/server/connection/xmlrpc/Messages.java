/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.connection.xmlrpc;

import org.eclipse.osgi.util.NLS;

/**
 * @author emueller
 * @generated
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.messages"; //$NON-NLS-1$
	public static String XmlRpcBuiltinWebServer_AddHandlerFailed;
	public static String XmlRpcBuiltinWebServer_PortUnset;
	public static String XmlRpcBuiltinWebServer_ServerSocketInitFailed;
	public static String XmlRpcBuiltinWebServer_ServerStarted;
	public static String XmlRpcBuiltinWebServer_ServerStartFailed;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
