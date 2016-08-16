/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent;

/**
 * Specifies in which direction a file based change package implementation should be traversed.
 *
 * @author emueller
 *
 */
public enum Direction {
	/**
	 * For iterating operations from the beginning.
	 */
	Forward,
	/**
	 * For iterating operations from the back.
	 */
	Backward
}