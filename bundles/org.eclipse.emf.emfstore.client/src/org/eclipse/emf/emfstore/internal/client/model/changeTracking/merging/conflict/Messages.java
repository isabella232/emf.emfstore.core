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
package org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict;

import org.eclipse.osgi.util.NLS;

/**
 * Conflict options related messages.
 * 
 * @author emueller
 * @generated
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.messages"; //$NON-NLS-1$
	public static String ConflictContext_Attribute_Label;
	public static String ConflictContext_ModelElement_Label;
	public static String ConflictContext_Opponent_Label;
	public static String ConflictOption_Unset_Label;
	public static String VisualConflict_Conflict_Not_Resolved;
	public static String VisualConflict_No_TheirOps;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
