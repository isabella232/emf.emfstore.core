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
package org.eclipse.emf.emfstore.common.extensionpoint;

import org.eclipse.osgi.util.NLS;

/**
 * Extension point related messages.
 *
 * @author emueller
 * @generated
 * @since 1.3
 *
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.common.extensionpoint.messages"; //$NON-NLS-1$
	public static String ESExtensionPoint_ExceptionOccurred;
	public static String ESExtensionPoint_ValueNotFound;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
