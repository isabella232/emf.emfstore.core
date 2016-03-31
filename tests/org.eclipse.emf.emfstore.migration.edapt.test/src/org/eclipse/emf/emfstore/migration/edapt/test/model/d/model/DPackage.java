/**
 * Copyright (c) 2011-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * EclipseSource Munich - initial API and implementation
 */
package org.eclipse.emf.emfstore.migration.edapt.test.model.d.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each operation of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.DFactory
 * @model kind="package"
 * @generated
 */
public interface DPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	String eNAME = "d";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	String eNS_URI = "http://eclipse.org/emf/emfstore/migration/edapt/test/d/2";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	String eNS_PREFIX = "org.eclipse.emfstore.migration.edapt.test.d";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	DPackage eINSTANCE = org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.impl.DPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.impl.DImpl
	 * <em>D</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.impl.DImpl
	 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.impl.DPackageImpl#getD()
	 * @generated
	 */
	int D = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int D__NAME = 0;

	/**
	 * The feature id for the '<em><b>Atts</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int D__ATTS = 1;

	/**
	 * The feature id for the '<em><b>Refs</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int D__REFS = 2;

	/**
	 * The number of structural features of the '<em>D</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int D_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>D</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int D_OPERATION_COUNT = 0;

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D
	 * <em>D</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>D</em>'.
	 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D
	 * @generated
	 */
	EClass getD();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D#getName()
	 * @see #getD()
	 * @generated
	 */
	EAttribute getD_Name();

	/**
	 * Returns the meta object for the attribute list '
	 * {@link org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D#getAtts <em>Atts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute list '<em>Atts</em>'.
	 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D#getAtts()
	 * @see #getD()
	 * @generated
	 */
	EAttribute getD_Atts();

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D#getRefs <em>Refs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the reference list '<em>Refs</em>'.
	 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D#getRefs()
	 * @see #getD()
	 * @generated
	 */
	EReference getD_Refs();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	DFactory getDFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each operation of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.impl.DImpl <em>D</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.impl.DImpl
		 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.impl.DPackageImpl#getD()
		 * @generated
		 */
		EClass D = eINSTANCE.getD();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute D__NAME = eINSTANCE.getD_Name();

		/**
		 * The meta object literal for the '<em><b>Atts</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute D__ATTS = eINSTANCE.getD_Atts();

		/**
		 * The meta object literal for the '<em><b>Refs</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference D__REFS = eINSTANCE.getD_Refs();

	}

} // DPackage
