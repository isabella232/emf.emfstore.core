/**
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 */
package org.eclipse.emf.emfstore.test.model;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Type With Feature Map Containment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.test.model.TypeWithFeatureMapContainment#getMapContainment <em>Map Containment
 * </em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.test.model.TypeWithFeatureMapContainment#getFirstKeyContainment <em>First Key
 * Containment</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.test.model.TypeWithFeatureMapContainment#getSecondKeyContainment <em>Second Key
 * Containment</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.emfstore.test.model.TestmodelPackage#getTypeWithFeatureMapContainment()
 * @model
 * @generated
 */
public interface TypeWithFeatureMapContainment extends TestType
{
	/**
	 * Returns the value of the '<em><b>Map Containment</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Map Containment</em>' attribute list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Map Containment</em>' attribute list.
	 * @see org.eclipse.emf.emfstore.test.model.TestmodelPackage#getTypeWithFeatureMapContainment_MapContainment()
	 * @model dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group'"
	 * @generated
	 */
	FeatureMap getMapContainment();

	/**
	 * Returns the value of the '<em><b>First Key Containment</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.emfstore.test.model.TestType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>First Key Containment</em>' containment reference list isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>First Key Containment</em>' containment reference list.
	 * @see org.eclipse.emf.emfstore.test.model.TestmodelPackage#getTypeWithFeatureMapContainment_FirstKeyContainment()
	 * @model containment="true" transient="true" volatile="true"
	 *        extendedMetaData="group='#mapContainment'"
	 * @generated
	 */
	EList<TestType> getFirstKeyContainment();

	/**
	 * Returns the value of the '<em><b>Second Key Containment</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.emfstore.test.model.TestType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Second Key Containment</em>' containment reference list isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Second Key Containment</em>' containment reference list.
	 * @see org.eclipse.emf.emfstore.test.model.TestmodelPackage#getTypeWithFeatureMapContainment_SecondKeyContainment()
	 * @model containment="true" transient="true" volatile="true"
	 *        extendedMetaData="group='#mapContainment'"
	 * @generated
	 */
	EList<TestType> getSecondKeyContainment();

} // TypeWithFeatureMapContainment
