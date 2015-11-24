/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.ui;

import org.eclipse.emf.emfstore.common.ESUIRunnableContext;

/**
 * Interface for providing a {@link Runnable} which will get executed on the UI Thread.
 *
 * @author jfaltermeier
 * @since 1.5
 * @noextend This interface is not intended to be extended by clients.
 * @deprecated
 */
@Deprecated
public interface ESUIRunnableProvider extends ESUIRunnableContext {

}
