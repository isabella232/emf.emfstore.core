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
 * A representation of the model object '<em><b>Type With Feature Map Non Containment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.test.model.TypeWithFeatureMapNonContainment#getMap <em>Map</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.test.model.TypeWithFeatureMapNonContainment#getFirstKey <em>First Key</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.test.model.TypeWithFeatureMapNonContainment#getSecondKey <em>Second Key</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.emfstore.test.model.TestmodelPackage#getTypeWithFeatureMapNonContainment()
 * @model
 * @generated
 */
public interface TypeWithFeatureMapNonContainment extends TestType
{
	/**
	 * Returns the value of the '<em><b>Map</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Map</em>' attribute list isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Map</em>' attribute list.
	 * @see org.eclipse.emf.emfstore.test.model.TestmodelPackage#getTypeWithFeatureMapNonContainment_Map()
	 * @model dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group'"
	 * @generated
	 */
	FeatureMap getMap();

	/**
	 * Returns the value of the '<em><b>First Key</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.emfstore.test.model.TestType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>First Key</em>' reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>First Key</em>' reference list.
	 * @see org.eclipse.emf.emfstore.test.model.TestmodelPackage#getTypeWithFeatureMapNonContainment_FirstKey()
	 * @model transient="true" volatile="true" derived="true"
	 *        extendedMetaData="group='#map'"
	 * @generated
	 */
	EList<TestType> getFirstKey();

	/**
	 * Returns the value of the '<em><b>Second Key</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.emfstore.test.model.TestType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Second Key</em>' reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Second Key</em>' reference list.
	 * @see org.eclipse.emf.emfstore.test.model.TestmodelPackage#getTypeWithFeatureMapNonContainment_SecondKey()
	 * @model transient="true" volatile="true" derived="true"
	 *        extendedMetaData="group='#map'"
	 * @generated
	 */
	EList<TestType> getSecondKey();

} // TypeWithFeatureMapNonContainment
