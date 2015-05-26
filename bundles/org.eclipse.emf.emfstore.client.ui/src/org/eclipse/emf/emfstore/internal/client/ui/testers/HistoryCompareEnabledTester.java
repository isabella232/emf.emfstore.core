/*******************************************************************************
 * Copyright (c) 2012-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Julian Sommerfeldt
 * Philip Langer
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.testers;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.emf.emfstore.internal.client.ui.views.historybrowserview.HistoryCompare;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;

/**
 * Test if there is a history compare extension registered.
 *
 * @author jsommerfeldt
 *
 */
public class HistoryCompareEnabledTester extends PropertyTester {

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[],
	 *      java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		return HistoryCompare.hasRegisteredExtensions() && isHistoryInfoWithVersion(receiver);
	}

	/**
	 * Tests whether the given {@code object} is a {@link HistoryInfo} with an identifiable version.
	 * <p>
	 * A {@link HistoryInfo} doesn't have an identifiable version (i.e., -1), if it is the local revision.
	 * </p>
	 *
	 * @param object The object to test.
	 * @return <code>true</code> if {@code object} is a {@link HistoryInfo} with an identifiable version,
	 *         <code>false</code> otherwise.
	 */
	private boolean isHistoryInfoWithVersion(Object object) {
		if (object instanceof HistoryInfo) {
			final HistoryInfo historyInfo = (HistoryInfo) object;
			return historyInfo.getPrimarySpec().getIdentifier() != -1;
		}
		return false;
	}
}
