/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.modelmutator;

/**
 * The modes in which an attribute can be changed.
 *
 * @author emueller
 * @since 2.0
 *
 */
public enum ESRandomChangeMode {
	/** Adding or setting a new attribute or reference value. */
	ADD,
	/** Deleting or unsetting an existing attribute or reference value. */
	DELETE,
	/** Reordering existing values of a multi-valued attributes or references. */
	REORDER
}