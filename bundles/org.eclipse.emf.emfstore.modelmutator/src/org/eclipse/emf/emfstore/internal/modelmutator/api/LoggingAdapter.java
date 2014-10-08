/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Julian Sommerfeldt - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.modelmutator.api;

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

/**
 * Basic extension of {@link FilteredAdapter} to print out the notifications.
 * 
 * @author Julian Sommerfeldt
 * 
 */
public class LoggingAdapter extends FilteredAdapter {

	/**
	 * @param toLogClasses The {@link EClass}es to log. If <code>null</code> every {@link EClass} is logged.
	 * @param toLogReferences The {@link EReference}es of the toLogClasses to log. If <code>null</code> every
	 *            {@link EReference} is logged.
	 * @param references Log reference changes?
	 * @param attributes Log attribute changes?
	 */
	public LoggingAdapter(List<EClass> toLogClasses, List<EReference> toLogReferences, boolean references,
		boolean attributes) {
		super(toLogClasses, toLogReferences, references, attributes);
	}

	@Override
	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);
		if (filter(notification)) {
			return;
		}

		System.out.println("NOTIFY:"); //$NON-NLS-1$
		System.out.println(format(notification));
		System.out.println();
	}

	/**
	 * Convert a {@link Notification} to a string.
	 * 
	 * @param notification The {@link Notification} to convert.
	 * @return The string representing the {@link Notification}.
	 */
	protected String format(Notification notification) {
		final StringBuffer result = new StringBuffer();
		result.append("Notifier:  " + notification.getNotifier()); //$NON-NLS-1$
		result.append("\n"); //$NON-NLS-1$
		result.append("Feature:   " + notification.getFeature()); //$NON-NLS-1$
		result.append("\n"); //$NON-NLS-1$
		result.append("Position:  " + notification.getPosition()); //$NON-NLS-1$
		result.append("\n"); //$NON-NLS-1$
		result.append("EventType: " + getEventType(notification.getEventType())); //$NON-NLS-1$
		result.append("\n"); //$NON-NLS-1$
		result.append("OldValue:  " + notification.getOldValue()); //$NON-NLS-1$
		result.append("\n"); //$NON-NLS-1$
		result.append("NewValue:  " + notification.getNewValue()); //$NON-NLS-1$
		result.append("\n"); //$NON-NLS-1$
		return result.toString();
	}
}
