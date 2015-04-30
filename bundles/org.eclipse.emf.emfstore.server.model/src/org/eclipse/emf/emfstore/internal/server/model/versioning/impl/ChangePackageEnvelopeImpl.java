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
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Change Package Envelope</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageEnvelopeImpl#getFragmentIndex
 * <em>Fragment Index</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageEnvelopeImpl#getFragmentCount
 * <em>Fragment Count</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageEnvelopeImpl#getFragment <em>
 * Fragment</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ChangePackageEnvelopeImpl extends EObjectImpl implements ChangePackageEnvelope {
	/**
	 * The default value of the '{@link #getFragmentIndex() <em>Fragment Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getFragmentIndex()
	 * @generated
	 * @ordered
	 */
	protected static final int FRAGMENT_INDEX_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getFragmentIndex() <em>Fragment Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getFragmentIndex()
	 * @generated
	 * @ordered
	 */
	protected int fragmentIndex = FRAGMENT_INDEX_EDEFAULT;

	/**
	 * The default value of the '{@link #getFragmentCount() <em>Fragment Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getFragmentCount()
	 * @generated
	 * @ordered
	 */
	protected static final int FRAGMENT_COUNT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getFragmentCount() <em>Fragment Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getFragmentCount()
	 * @generated
	 * @ordered
	 */
	protected int fragmentCount = FRAGMENT_COUNT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getFragment() <em>Fragment</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getFragment()
	 * @generated
	 * @ordered
	 */
	protected EList<AbstractOperation> fragment;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected ChangePackageEnvelopeImpl() {
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
		return VersioningPackage.Literals.CHANGE_PACKAGE_ENVELOPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public int getFragmentIndex() {
		return fragmentIndex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setFragmentIndex(int newFragmentIndex) {
		final int oldFragmentIndex = fragmentIndex;
		fragmentIndex = newFragmentIndex;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT_INDEX, oldFragmentIndex, fragmentIndex));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public int getFragmentCount() {
		return fragmentCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setFragmentCount(int newFragmentCount) {
		final int oldFragmentCount = fragmentCount;
		fragmentCount = newFragmentCount;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT_COUNT, oldFragmentCount, fragmentCount));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<AbstractOperation> getFragment() {
		if (fragment == null) {
			fragment = new EObjectContainmentEList.Resolving<AbstractOperation>(AbstractOperation.class, this,
				VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT);
		}
		return fragment;
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
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT:
			return ((InternalEList<?>) getFragment()).basicRemove(otherEnd, msgs);
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
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT_INDEX:
			return getFragmentIndex();
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT_COUNT:
			return getFragmentCount();
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT:
			return getFragment();
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
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT_INDEX:
			setFragmentIndex((Integer) newValue);
			return;
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT_COUNT:
			setFragmentCount((Integer) newValue);
			return;
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT:
			getFragment().clear();
			getFragment().addAll((Collection<? extends AbstractOperation>) newValue);
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
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT_INDEX:
			setFragmentIndex(FRAGMENT_INDEX_EDEFAULT);
			return;
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT_COUNT:
			setFragmentCount(FRAGMENT_COUNT_EDEFAULT);
			return;
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT:
			getFragment().clear();
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
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT_INDEX:
			return fragmentIndex != FRAGMENT_INDEX_EDEFAULT;
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT_COUNT:
			return fragmentCount != FRAGMENT_COUNT_EDEFAULT;
		case VersioningPackage.CHANGE_PACKAGE_ENVELOPE__FRAGMENT:
			return fragment != null && !fragment.isEmpty();
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
		result.append(" (fragmentIndex: "); //$NON-NLS-1$
		result.append(fragmentIndex);
		result.append(", fragmentCount: "); //$NON-NLS-1$
		result.append(fragmentCount);
		result.append(')');
		return result.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope#isLast()
	 */
	public boolean isLast() {
		return fragmentIndex == fragmentCount - 1;
	}

} // ChangePackageEnvelopeImpl
