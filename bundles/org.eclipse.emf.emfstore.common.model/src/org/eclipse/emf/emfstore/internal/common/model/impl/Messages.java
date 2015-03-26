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
package org.eclipse.emf.emfstore.internal.common.model.impl;

import org.eclipse.osgi.util.NLS;

/**
 * @author emueller
 * @generated
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.common.model.impl.messages"; //$NON-NLS-1$
	public static String IdEObjectCollectionImpl_CouldNotLoadElementResource;
	public static String IdEObjectCollectionImpl_ElementNotContainedInProject;
	public static String IdEObjectCollectionImpl_ResoruceCouldNotBeLoaded;
	public static String IdEObjectCollectionImpl_ResourceCouldNotBeSaved;
	public static String IdEObjectCollectionImpl_XMIResourceNotLoaded;
	public static String NotifiableIdEObjectCollectionImpl_Exception;
	public static String NotifiableIdEObjectCollectionImpl_Exception_Detach;
	public static String NotifiableIdEObjectCollectionImpl_Exception_NoDetach;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
