/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * mkoegel
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.testers;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;

/**
 * Tests if auto save is enabled.
 *
 * @author mkoegel
 * @author emueller
 */
public class IsAutoSaveEnabledTester extends PropertyTester {

	private static final String ORG_ECLIPSE_EMF_EMFSTORE_CLIENT_UI_DISABLE_SAVE_CONTROLS = "org.eclipse.emf.emfstore.client.ui.disableSaveControls"; //$NON-NLS-1$
	private static boolean isAutoSaveEnabledTesterDisabled = initExtensionPoint();

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[],
	 *      java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		return expectedValue != null && expectedValue.equals(Configuration.getClientBehavior().isAutoSaveEnabled())
			&& !isAutoSaveEnabledTesterDisabled;
	}

	private static boolean initExtensionPoint() {
		final ESExtensionPoint extensionPoint = new ESExtensionPoint(
			ORG_ECLIPSE_EMF_EMFSTORE_CLIENT_UI_DISABLE_SAVE_CONTROLS);
		final ESExtensionElement element = extensionPoint.getFirst();

		if (element == null) {
			// default
			return false;
		}

		return element.getBoolean("enabled", false); //$NON-NLS-1$
	}
}
