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
package org.eclipse.emf.emfstore.fuzzy;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Fuzzy API related messages.
 * 
 * @author emueller
 * @since 2.0
 * 
 */
public final class Messages {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.fuzzy.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	/**
	 * Returns the messages with the given ID.
	 * 
	 * @param id
	 *            message ID
	 * @return the message
	 */
	public static String getString(String id) {
		try {
			return RESOURCE_BUNDLE.getString(id);
		} catch (final MissingResourceException e) {
			return '!' + id + '!';
		}
	}
}
