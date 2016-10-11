/*******************************************************************************
 * Copyright (c) 2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.ecore.test;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;

/**
 * Utility class for deleting certain features.
 *
 * @author emueller
 *
 */
public final class EDelete {

	private EDelete() {
		// private ctor
	}

	/**
	 * Delete all super types of the given {@link EClass}.
	 * 
	 * @param eClass the class from which to remove all super types
	 */
	public static void superType(final EClass eClass) {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				eClass.getESuperTypes().clear();
			}
		}.run(false);
	}
}
