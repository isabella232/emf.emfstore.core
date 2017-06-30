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
package org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets;

import java.util.List;

/**
 * Implementation of an prioritized observer with priority 1.
 *
 * @author emueller
 *
 */
public class PImpl implements P {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.observer.ESPrioritizedObserver#getPriority()
	 */
	public int getPriority() {
		return 2;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.IAppendItem#appendItem(java.util.List)
	 */
	public void appendItem(List<String> items) {
		items.add("P");
	}

}
