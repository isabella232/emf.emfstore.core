/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Marco.vanMeegen - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server;

import org.eclipse.emf.emfstore.server.exceptions.ESServerInitException;

/**
 * Extension point interface to use client specific web server/server configuration/logging etc to implement EMFStore
 * Server, e.g. embedded Jetty.
 * Only one extension is allowed in a OSGi system, otherwise EMFStore startup will terminate with an error. If none is
 * specified, the default EMFStore built-in server is used.
 *
 * @author Marco van Meegen
 * @since 1.3
 *
 */
public interface ESXmlRpcWebServerProvider {

	/**
	 * Stops the server.
	 */
	void stopServer();

	/**
	 * Removes a handler from the web server.
	 *
	 * @param handlerName the handler's name
	 * @return true, if other handler still available
	 */
	boolean removeHandler(String handlerName);

	/**
	 * Adds a handler to the web server.
	 *
	 * @param handlerName handler name
	 * @param clazz class of server interface
	 * @throws ESServerInitException in case of failure
	 */
	void addHandler(String handlerName, Class<?> clazz) throws ESServerInitException;

	/**
	 * Starts the server.
	 *
	 * @throws ESServerInitException in case of failure
	 */
	void initServer() throws ESServerInitException;

	/**
	 * Set the port for the server, must be called before any other method can be called.
	 *
	 * @param port http(s) port where server will answer
	 */
	void setPort(int port);

	/**
	 * Returns the port the server is listening to.
	 *
	 * @return port server listens to
	 */
	int getPort();

}
