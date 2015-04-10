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

import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.util.EObjectTypeConverterFactory;
import org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.util.EObjectTypeFactory;
import org.eclipse.emf.emfstore.server.ESXmlRpcWebServerProvider;
import org.eclipse.emf.emfstore.server.exceptions.ESServerInitException;

/**
 * XML-RPC built-in web server for XML RPC connections.
 *
 * @author wesendon
 */
public final class XmlRpcBuiltinWebServer implements ESXmlRpcWebServerProvider {
	private WebServer webServer;
	private int port;

	/**
	 * Constructor.
	 */
	public XmlRpcBuiltinWebServer() {
		webServer = null;
		port = -1;
	}

	/**
	 * Starts the server.
	 *
	 * @throws ESServerInitException in case of failure
	 */
	@Override
	public void initServer() throws ESServerInitException {
		if (port == -1) {
			throw new ESServerInitException(Messages.XmlRpcBuiltinWebServer_PortUnset);
		}
		if (webServer != null) {
			return;
		}
		try {
			webServer = new EMFStoreWebServer(port);

			ModelUtil.logInfo(Messages.XmlRpcBuiltinWebServer_ServerStarted + port);

			final XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
			xmlRpcServer.setTypeFactory(new EObjectTypeFactory(xmlRpcServer));
			final EObjectTypeConverterFactory pFactory = new EObjectTypeConverterFactory();
			xmlRpcServer.setTypeConverterFactory(pFactory);

			final PropertyHandlerMapping phm = new PropertyHandlerMapping();

			phm.setVoidMethodEnabled(true);
			phm.setTypeConverterFactory(pFactory);

			xmlRpcServer.setHandlerMapping(phm);

			final XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
			serverConfig.setEnabledForExtensions(true);
			serverConfig.setEnabledForExceptions(true);
			serverConfig.setContentLengthOptional(true);

			webServer.start();
		} catch (final IOException e) {
			throw new ESServerInitException(Messages.XmlRpcBuiltinWebServer_ServerStartFailed, e);
		}
	}

	/**
	 * Adds a handler to the webserver.
	 *
	 * @param handlerName handler name
	 * @param clazz class of server interface
	 * @throws ESServerInitException in case of failure
	 */
	@Override
	public void addHandler(String handlerName, Class<?> clazz) throws ESServerInitException {
		try {
			final PropertyHandlerMapping mapper = (PropertyHandlerMapping) webServer.getXmlRpcServer()
				.getHandlerMapping();
			mapper.addHandler(handlerName, clazz);
		} catch (final XmlRpcException e) {
			throw new ESServerInitException(Messages.XmlRpcBuiltinWebServer_AddHandlerFailed, e);
		}
	}

	/**
	 * Removes a handler from the Webserver.
	 *
	 * @param handlerName the handler's name
	 * @return true, if other handler still available
	 */
	@Override
	public boolean removeHandler(String handlerName) {
		final PropertyHandlerMapping mapper = (PropertyHandlerMapping) webServer.getXmlRpcServer().getHandlerMapping();
		mapper.removeHandler(handlerName);
		try {
			return mapper.getListMethods().length > 0;
		} catch (final XmlRpcException e) {
			return false;
		}
	}

	/**
	 * Stops the server.
	 */
	@Override
	public void stopServer() {
		webServer.shutdown();
		webServer = null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.ESXmlRpcWebServerProvider#setPort(int)
	 */
	@Override
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.ESXmlRpcWebServerProvider#getPort()
	 */
	@Override
	public int getPort() {
		return port;
	}
}
