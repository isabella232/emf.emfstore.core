/*******************************************************************************
 * Copyright (c) 2011-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.scm;

import org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

/**
 * Implementors of this interface may be passed to a {@link SCMContentProvider}. The content provider will use this
 * initializer to init newly created {@link OperationProxy operation proxies}.
 *
 * @author Johannes Faltermeier
 *
 */
public interface ProxyInitializer {

	/**
	 * Initialises the given proxy.
	 * 
	 * @param newProxy the newly created {@link OperationProxy proxy}
	 * @param operation the {@link AbstractOperation operation} which was used to create the proxy
	 */
	void prepareProxy(OperationProxy newProxy, AbstractOperation operation);

}
