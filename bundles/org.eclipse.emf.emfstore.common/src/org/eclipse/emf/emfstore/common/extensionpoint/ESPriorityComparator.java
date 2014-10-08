/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.common.extensionpoint;

import java.util.Comparator;

/**
 * A comparator for {@link ESExtensionElement}. This allows to sort the elements in the {@link ESExtensionPoint} in
 * order to represent priority of registered elements.
 * 
 * This comparator by default uses a field priority, which is expected to hold an priority number and then sort
 * by by this number.
 * 
 * @author wesendon
 * 
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class ESPriorityComparator implements Comparator<ESExtensionElement> {

	private final String fieldname;
	private final boolean desc;

	/**
	 * Default constructor.
	 */
	public ESPriorityComparator() {
		this("priority", false); //$NON-NLS-1$
	}

	/**
	 * Constructor which allows to config the ordering.
	 * 
	 * @param descending if true, priorities are sorted in descending order, ascending otherwise
	 */
	public ESPriorityComparator(final boolean descending) {
		this("priority", descending); //$NON-NLS-1$
	}

	/**
	 * Constructor allows to config fieldname and ordering.
	 * 
	 * @param fieldname
	 *            the attribute id of the priority field
	 * @param descending
	 *            if true, priorities are sorted in descending order, ascending otherwise
	 */
	public ESPriorityComparator(final String fieldname, final boolean descending) {
		this.fieldname = fieldname;
		desc = descending;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public int compare(ESExtensionElement element1, ESExtensionElement element2) {
		try {
			element1.setThrowException(true);
			element2.setThrowException(true);
			return element1.getInteger(fieldname).compareTo(element2.getInteger(fieldname))
				* (desc ? -1 : 1);
		} catch (final ESExtensionPointException e) {
			return 0;
		}
	}

}
