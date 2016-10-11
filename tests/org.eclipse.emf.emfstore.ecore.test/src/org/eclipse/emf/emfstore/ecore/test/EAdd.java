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
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;

/**
 * Utility class for adding elements to features.
 *
 * @author emueller
 *
 */
public final class EAdd {

	private EAdd() {
		// private ctor
	}

	/**
	 * Add one or more {@link EStructuralFeature}s to the given {@link EClass}.
	 *
	 * @param eClass to parent of the {@link EStructuralFeature}s
	 * @param features the {@link EStructuralFeature}s to be added
	 */
	public static EClass to(final EClass eClass, final EStructuralFeature... features) {
		run(new Runnable() {
			public void run() {
				for (final EStructuralFeature feature : features) {
					eClass.getEStructuralFeatures().add(feature);
				}
			}
		});
		return eClass;
	}

	/**
	 * Add one or more {@link EClassifier}s to the given {@link EPackage}.
	 *
	 * @param ePackage to parent of the {@link EClassifier}s
	 * @param eClassifiers the {@link EClassifier} to be added
	 */
	public static EPackage to(final EPackage ePackage, final EClassifier... eClassifiers) {
		run(new Runnable() {
			public void run() {
				for (final EClassifier eClassifier : eClassifiers) {
					ePackage.getEClassifiers().add(eClassifier);
				}
			}
		});
		return ePackage;
	}

	/**
	 * Add one or more {@link EEnumLiteral}s to the given {@link EEnum}.
	 *
	 * @param eEnum the {@link EEnum} to which the literals should be added
	 * @param literals the literals to be added
	 */
	public static void to(final EEnum eEnum, final EEnumLiteral... literals) {
		run(new Runnable() {
			public void run() {
				for (final EEnumLiteral literal : literals) {
					eEnum.getELiterals().add(literal);
				}
			}
		});
	}

	private static void run(final Runnable runnable) {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				runnable.run();
			}
		}.run(false);
	}
}
