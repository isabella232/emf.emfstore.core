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
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge;

import org.eclipse.osgi.util.NLS;

/**
 * Merge UI related messages.
 *
 * @author emueller
 *
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.messages"; //$NON-NLS-1$
	public static String MergeWizard_ResolveConflicts_Message_1;
	public static String MergeWizard_ResolveConflicts_Message_2;
	public static String MergeWizard_ResolveConflicts_Title;
	public static String MergeWizard_Title;
	public static String MergeWizardPage_Description_1;
	public static String MergeWizardPage_Description_2;
	public static String MergeWizardPage_Description_3;
	public static String MergeWizardPage_Description_4;
	public static String MergeWizardPage_KeepMyChanges;
	public static String MergeWizardPage_KeepTheirChanges;
	public static String MergeWizardPage_Override_Message_1;
	public static String MergeWizardPage_Override_Message_2;
	public static String MergeWizardPage_Override_Message_3;
	public static String MergeWizardPage_Override_Title;
	public static String MergeWizardPage_PageName;
	public static String MergeWizardPage_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
