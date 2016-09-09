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
package org.eclipse.emf.emfstore.test.model.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.emfstore.test.model.TestType;
import org.eclipse.emf.emfstore.test.model.TestmodelPackage;
import org.eclipse.emf.emfstore.test.model.TypeWithFeatureMapContainment;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Type With Feature Map Containment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.test.model.impl.TypeWithFeatureMapContainmentImpl#getMapContainment
 * <em>Map Containment</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.test.model.impl.TypeWithFeatureMapContainmentImpl#getFirstKeyContainment
 * <em>First Key Containment</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.test.model.impl.TypeWithFeatureMapContainmentImpl#getSecondKeyContainment
 * <em>Second Key Containment</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TypeWithFeatureMapContainmentImpl extends TestTypeImpl implements TypeWithFeatureMapContainment {
	/**
	 * The cached value of the '{@link #getMapContainment() <em>Map Containment</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getMapContainment()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap mapContainment;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected TypeWithFeatureMapContainmentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_CONTAINMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public FeatureMap getMapContainment() {
		if (mapContainment == null) {
			mapContainment = new BasicFeatureMap(this,
				TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__MAP_CONTAINMENT);
		}
		return mapContainment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<TestType> getFirstKeyContainment() {
		return getMapContainment()
			.list(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_CONTAINMENT__FIRST_KEY_CONTAINMENT);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<TestType> getSecondKeyContainment() {
		return getMapContainment()
			.list(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_CONTAINMENT__SECOND_KEY_CONTAINMENT);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__MAP_CONTAINMENT:
			return ((InternalEList<?>) getMapContainment()).basicRemove(otherEnd, msgs);
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__FIRST_KEY_CONTAINMENT:
			return ((InternalEList<?>) getFirstKeyContainment()).basicRemove(otherEnd, msgs);
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__SECOND_KEY_CONTAINMENT:
			return ((InternalEList<?>) getSecondKeyContainment()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__MAP_CONTAINMENT:
			if (coreType) {
				return getMapContainment();
			}
			return ((FeatureMap.Internal) getMapContainment()).getWrapper();
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__FIRST_KEY_CONTAINMENT:
			return getFirstKeyContainment();
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__SECOND_KEY_CONTAINMENT:
			return getSecondKeyContainment();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__MAP_CONTAINMENT:
			((FeatureMap.Internal) getMapContainment()).set(newValue);
			return;
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__FIRST_KEY_CONTAINMENT:
			getFirstKeyContainment().clear();
			getFirstKeyContainment().addAll((Collection<? extends TestType>) newValue);
			return;
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__SECOND_KEY_CONTAINMENT:
			getSecondKeyContainment().clear();
			getSecondKeyContainment().addAll((Collection<? extends TestType>) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__MAP_CONTAINMENT:
			getMapContainment().clear();
			return;
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__FIRST_KEY_CONTAINMENT:
			getFirstKeyContainment().clear();
			return;
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__SECOND_KEY_CONTAINMENT:
			getSecondKeyContainment().clear();
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__MAP_CONTAINMENT:
			return mapContainment != null && !mapContainment.isEmpty();
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__FIRST_KEY_CONTAINMENT:
			return !getFirstKeyContainment().isEmpty();
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT__SECOND_KEY_CONTAINMENT:
			return !getSecondKeyContainment().isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) {
			return super.toString();
		}

		final StringBuffer result = new StringBuffer(super.toString());
		result.append(" (mapContainment: "); //$NON-NLS-1$
		result.append(mapContainment);
		result.append(')');
		return result.toString();
	}

} // TypeWithFeatureMapContainmentImpl
