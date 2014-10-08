/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * JulianSommerfeldt
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.modelmutator.api;

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * Adapter to log changes of model in modelmutator. Also useful to filter notifications.
 * 
 * @author Julian Sommerfeldt
 * 
 */
public abstract class FilteredAdapter extends EContentAdapter {

	private static final String E_CLASS_SUFFIX = "Impl"; //$NON-NLS-1$
	private final List<EClass> toLogClasses;
	private final List<EReference> toLogReferences;
	private final boolean attributes;
	private final boolean references;

	/**
	 * @param toLogClasses The {@link EClass}es to log. If <code>null</code> every {@link EClass} is logged.
	 * @param toLogReferences The {@link EReference}es of the toLogClasses to log. If <code>null</code> every
	 *            {@link EReference} is logged.
	 * @param references Log reference changes?
	 * @param attributes Log attribute changes?
	 */
	public FilteredAdapter(List<EClass> toLogClasses, List<EReference> toLogReferences, boolean references,
		boolean attributes) {
		this.toLogClasses = toLogClasses;
		this.toLogReferences = toLogReferences;
		this.references = references;
		this.attributes = attributes;
	}

	/**
	 * @param notification The {@link Notification} to check.
	 * @return Filter a {@link Notification}?
	 */
	protected boolean filter(Notification notification) {
		// ignore if it is an attribute change and attributes should not be logged
		if (!attributes && notification.getFeature() instanceof EAttribute) {
			return true;
		}

		// ignore if it is a reference change and references should not be logged
		if (!references && notification.getFeature() instanceof EReference) {
			return true;
		}

		// if no log classes are configured ignore nothing
		if (toLogClasses == null) {
			return false;
		}

		// check if the notification message contains a class which should be logged
		for (final EClass eClass : toLogClasses) {
			final String string = notification.toString();
			if (string.contains("." + eClass.getName() + E_CLASS_SUFFIX)) { //$NON-NLS-1$
				if (toLogReferences == null) {
					return false;
				}
				for (final EReference ref : toLogReferences) {
					if (string.contains(ref.getName())) {
						return false;
					}
				}
			}
		}

		// ignore
		return true;
	}

	/**
	 * Convert an int eventType of a notification to a {@link String}.
	 * 
	 * @param eventType The eventType to convert.
	 * @return The {@link String} representing the eventType.
	 */
	public static String getEventType(int eventType) {
		switch (eventType) {
		case Notification.SET: {
			return "SET"; //$NON-NLS-1$
		}
		case Notification.UNSET: {
			return "UNSET"; //$NON-NLS-1$
		}
		case Notification.ADD: {
			return "ADD"; //$NON-NLS-1$
		}
		case Notification.ADD_MANY: {
			return "ADD_MANY"; //$NON-NLS-1$
		}
		case Notification.REMOVE: {
			return "REMOVE"; //$NON-NLS-1$
		}
		case Notification.REMOVE_MANY: {
			return "REMOVE_MANY"; //$NON-NLS-1$
		}
		case Notification.MOVE: {
			return "MOVE"; //$NON-NLS-1$
		}
		case Notification.REMOVING_ADAPTER: {
			return "REMOVING_ADAPTER"; //$NON-NLS-1$
		}
		case Notification.RESOLVE: {
			return "RESOLVE"; //$NON-NLS-1$
		}
		default: {
			return String.valueOf(eventType);
		}
		}
	}
}
