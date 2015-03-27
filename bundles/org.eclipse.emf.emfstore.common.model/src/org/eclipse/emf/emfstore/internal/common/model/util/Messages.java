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
package org.eclipse.emf.emfstore.internal.common.model.util;

import org.eclipse.osgi.util.NLS;

/**
 * Common util related messages.
 *
 * @author emueller
 * @generated
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.common.model.util.messages"; //$NON-NLS-1$
	public static String FileUtil_Comparing;
	public static String FileUtil_DeleteFaild;
	public static String FileUtil_DestinationExists;
	public static String FileUtil_SourceMustBeFolder;
	public static String FileUtil_SourceOrDestinationIsNull;
	public static String ModelUtil_Incoming_CrossRef_Is_Map_Key;
	public static String ModelUtil_ModelElement_Is_In_Containment_Cycle;
	public static String ModelUtil_Resource_Contains_Multiple_Objects;
	public static String ModelUtil_Resource_Contains_No_Objects;
	public static String ModelUtil_Resource_Contains_No_Objects_Of_Given_Class;
	public static String ModelUtil_Save_Options_Initialized;
	public static String ModelUtil_SingletonIdResolver_Not_Instantiated;
	public static String NotificationValidator_ErrorDuringMove;
	public static String NotificationValidator_FeatureCantBeUnset;
	public static String NotificationValidator_NonListNewValue_REMOVE_MANY;
	public static String NotificationValidator_NonListOldValue_REMOVE_MANY;
	public static String NotificationValidator_NonTransientFeatureDetected;
	public static String NotificationValidator_NotificationInfoMustNotBeNull;
	public static String NotificationValidator_NullDetected;
	public static String NotificationValidator_UnknownNotificationState_ADD;
	public static String NotificationValidator_UnknownNotificationState_ADD_MANY;
	public static String NotificationValidator_UnknownNotificationState_REMOVE;
	public static String NotificationValidator_UnknownNotificationState_REMOVE_MANY;
	public static String NotificationValidator_UnknownNotificationType;
	public static String SerializationException_Failed;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
