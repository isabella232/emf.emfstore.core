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
package org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts;

import org.eclipse.osgi.util.NLS;

/**
 * Reference operations related messages.
 *
 * @author emueller
 * @generated
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.messages"; //$NON-NLS-1$
	public static String DeletionConflict_AndOtherElements;
	public static String DeletionConflict_Delete;
	public static String DeletionConflict_Recover;
	public static String MultiAttributeConflict_Add;
	public static String MultiAttributeConflict_Remove;
	public static String MultiAttributeMoveConflict_AddElement;
	public static String MultiAttributeMoveConflict_MoveElement;
	public static String MultiAttributeMoveSetConflict_ChangeElement;
	public static String MultiAttributeMoveSetConflict_MoveElement;
	public static String MultiAttributeSetConflict_ChangeElement;
	public static String MultiAttributeSetConflict_RemoveElement;
	public static String MultiAttributeSetSetConflict_KeepMy;
	public static String MultiAttributeSetSetConflict_KeepTheir;
	public static String MultiReference_Move_To;
	public static String MultiReferenceConflict_Add;
	public static String MultiReferenceConflict_MoveTo;
	public static String MultiReferenceConflict_Remove;
	public static String MultiReferenceSetConflict_Remove;
	public static String MultiReferenceSetConflict_Set;
	public static String SingleReferenceConflict_Unset;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
