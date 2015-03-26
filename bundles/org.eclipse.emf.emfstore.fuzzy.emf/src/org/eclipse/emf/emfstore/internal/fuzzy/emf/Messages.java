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
package org.eclipse.emf.emfstore.internal.fuzzy.emf;

import org.eclipse.osgi.util.NLS;

/**
 * EMF Fuzzy related messages.
 *
 * @author emueller
 * @generated
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.fuzzy.emf.messages"; //$NON-NLS-1$

	static String FuzzyUtil_LoadPropertiesFailed;
	static String FuzzyUtil_NoTestConfigFound;
	static String FuzzyUtil_WrontTestDiffConfiguration;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
