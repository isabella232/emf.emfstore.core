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

import java.util.concurrent.Callable;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.emfstore.client.util.RunESCommand;

/**
 * Utility class for updating certain features.
 *
 * @author emueller
 *
 */
public final class EUpdate {

	private EUpdate() {
		// private ctor
	}

	/**
	 * Update the type of the given {@link EAttribute}.
	 *
	 * @param attr the attribute to be updated
	 * @param dataType the new type of the attribute
	 * @return the updated attribute
	 */
	public static EAttribute eAttr(final EAttribute attr, final EDataType dataType) {
		return run(new Callable<EAttribute>() {
			public EAttribute call() throws Exception {
				attr.setEType(dataType);
				return attr;
			}
		});
	}

	/**
	 * Update the type of the given {@link EReference}.
	 *
	 * @param ref the reference to be updated
	 * @param eClassifier the new type of the reference
	 * @return the updated reference
	 */
	public static EReference eRef(final EReference ref, final EClassifier eClassifier) {
		return run(new Callable<EReference>() {
			public EReference call() {
				ref.setEType(eClassifier);
				return ref;
			}
		});
	}

	/**
	 * Updated the super type of the given {@link EClass}.
	 *
	 * @param cls the class to be updated
	 * @param superType the new super type of the given class
	 * @return the updated class
	 */
	public static EClass superType(final EClass cls, final EClass superType) {
		return run(new Callable<EClass>() {
			public EClass call() throws Exception {
				cls.getESuperTypes().clear();
				cls.getESuperTypes().add(superType);
				return cls;
			}
		});
	}

	private static <T> T run(final Callable<T> callable) {
		return RunESCommand.runWithResult(callable);
	}
}
