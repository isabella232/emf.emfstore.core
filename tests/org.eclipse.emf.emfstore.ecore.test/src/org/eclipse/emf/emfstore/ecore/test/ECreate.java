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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * Utility class for creating ECore related types.
 *
 * @author emueller
 *
 */
public final class ECreate {

	private ECreate() {
		// private ctor
	}

	/**
	 * Create an {@link EPackage} with the given name.
	 *
	 * @param name the name of the package
	 * @return the created package
	 */
	public static EPackage ePackage(String name) {
		final EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName(name);
		return ePackage;
	}

	/**
	 * Create an {@link EClass} with the given name.
	 *
	 * @param name the name of the {@link EClass}
	 * @return the created class
	 */
	public static EClass eClass(String name) {
		final EClass cls = EcoreFactory.eINSTANCE.createEClass();
		cls.setName(name);
		return cls;
	}

	/**
	 * Create an {@link EAttribute} with the given name and type.
	 *
	 * @param name the name of the attribute
	 * @param eType the type of the attribute
	 * @return the created attribute
	 */
	public static EAttribute eAttr(String name, EClassifier eType) {
		final EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
		attr.setName(name);
		attr.setEType(eType);
		return attr;
	}

	/**
	 * Create an {@link EReference} with the given name and type.
	 *
	 * @param name the name of the reference
	 * @param eType the type of the reference
	 * @return the created reference
	 */
	public static EReference eRef(String name, EClassifier eType) {
		final EReference ref = EcoreFactory.eINSTANCE.createEReference();
		ref.setName(name);
		ref.setEType(eType);
		return ref;
	}

	/**
	 * Create a boolean {@link EDataType}.
	 * 
	 * @return a boolean data type
	 */
	public static EDataType eBooleanType() {
		return EcorePackage.eINSTANCE.getEBooleanObject();
	}

	/**
	 * Create a string {@link EDataType}.
	 * 
	 * @return the string data type
	 */
	public static EDataType eStringType() {
		return EcorePackage.eINSTANCE.getEString();
	}

	/**
	 * Create an {@link EEnum} with the given name.
	 * 
	 * @param name the name of the enum
	 * @return the created enum
	 */
	public static EEnum eEnum(String name) {
		final EEnum eEnum = EcoreFactory.eINSTANCE.createEEnum();
		eEnum.setName(name);
		return eEnum;
	}

	/**
	 * Create a {@link EEnumLiteral} with the given name and value.
	 * 
	 * @param name the name of the literal
	 * @param value the value of the literal
	 * @return the created literal
	 */
	public static EEnumLiteral eLiteral(String name, int value) {
		final EEnumLiteral literal = EcoreFactory.eINSTANCE.createEEnumLiteral();
		literal.setName(name);
		literal.setValue(value);
		return literal;
	}
}
