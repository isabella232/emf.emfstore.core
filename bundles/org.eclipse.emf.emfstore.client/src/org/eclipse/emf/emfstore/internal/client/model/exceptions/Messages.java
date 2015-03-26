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
package org.eclipse.emf.emfstore.internal.client.model.exceptions;

import org.eclipse.osgi.util.NLS;

/**
 * Exception related messages.
 *
 * @author emueller
 * @generated
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.client.model.exceptions.messages"; //$NON-NLS-1$
	public static String ChangeConflictException_ConflictDetected;
	public static String InvalidHandleException_HandleInvalid;
	public static String MEUrlResolutionException_ResolutionFailed;
	public static String NoChangesOnServerException_NoChangesOnServer;
	public static String ProjectUrlResolutionException_ResolutionFailed;
	public static String ServerUrlResolutionException_ResolutionFailed;
	public static String UnknownNotificationImplementationException_UnknownNotification;
	public static String UnkownProjectException_UnknownProject;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
