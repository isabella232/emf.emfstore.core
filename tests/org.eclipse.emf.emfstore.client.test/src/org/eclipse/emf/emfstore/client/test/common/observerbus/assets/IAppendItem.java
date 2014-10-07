/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.common.observerbus.assets;

import java.util.List;

import org.eclipse.emf.emfstore.common.ESObserver;

/**
 * @author Edgar
 * 
 */
public interface IAppendItem extends ESObserver {

	/**
	 * Appends an item to the given list of items and returns a new list.
	 * 
	 * @param items
	 *            the current list of items
	 * 
	 */
	void appendItem(List<String> items);

}
