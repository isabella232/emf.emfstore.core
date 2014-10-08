/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * wesendon
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.connection.xmlrpc;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPointException;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.Messages;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.server.ESLocationProvider;
import org.eclipse.emf.emfstore.server.ESXmlRpcWebServerProvider;

/**
 * Manages the webserver for XML RPC connections.
 * 
 * @author wesendon
 */
public final class XmlRpcWebserverManager {
	private static final String WEBSERVER_PROVIDER_KEY = "org.eclipse.emf.emfstore.server.webServerProvider"; //$NON-NLS-1$

	/**
	 * The web server provider.
	 */
	public static ESXmlRpcWebServerProvider webServerProvider = null;

	/**
	 * Returns an instance of the webserver manager.
	 * 
	 * @return instance of websever manager.
	 */
	public static ESXmlRpcWebServerProvider getInstance() {
		final ESXmlRpcWebServerProvider provider = getWebServerProvider();
		return provider;
	}

	private XmlRpcWebserverManager() {
	}

	/**
	 * Returns the registered {@link ESLocationProvider} or if not existent, the
	 * {@link org.eclipse.emf.emfstore.internal.server.DefaultServerWorkspaceLocationProvider}.
	 * 
	 * @return workspace location provider
	 */
	public static synchronized ESXmlRpcWebServerProvider getWebServerProvider() {
		if (webServerProvider == null) {
			int port = 8080;
			try {

				port = Integer.valueOf(ServerConfiguration.getProperties()
					.getProperty(ServerConfiguration.XML_RPC_PORT));
			} catch (final NumberFormatException e) {
				port = Integer.valueOf(ServerConfiguration.XML_RPC_PORT_DEFAULT);
			}

			try {

				final IExtensionRegistry reg = Platform.getExtensionRegistry();
				final IConfigurationElement[] elements = reg.getConfigurationElementsFor(WEBSERVER_PROVIDER_KEY);
				if (new ESExtensionPoint(WEBSERVER_PROVIDER_KEY, true).size() > 0) {
					webServerProvider = new ESExtensionPoint(WEBSERVER_PROVIDER_KEY, true).getClass(
						"providerClass", ESXmlRpcWebServerProvider.class); //$NON-NLS-1$
				} else {
					webServerProvider = new XmlRpcBuiltinWebServer();
				}
			} catch (final ESExtensionPointException e) {
				final String message = Messages.ServerConfiguration_No_WebServer_Provider;
				ModelUtil.logException(message, e);
				return null;
			}

			webServerProvider.setPort(port);
			ModelUtil.logInfo("Using WebServerProvider " + webServerProvider.getClass() + " at port "
				+ webServerProvider.getPort());
		}

		return webServerProvider;
	}

}
