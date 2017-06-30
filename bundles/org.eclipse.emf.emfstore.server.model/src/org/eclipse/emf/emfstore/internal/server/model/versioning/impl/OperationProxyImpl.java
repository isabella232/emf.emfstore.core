/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ImageProxy;
import org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Operation Proxy</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.OperationProxyImpl#getProxies
 * <em>Proxies</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OperationProxyImpl extends EObjectImpl implements OperationProxy {
	/**
	 * The cached value of the '{@link #getProxies() <em>Proxies</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getProxies()
	 * @generated
	 * @ordered
	 */
	protected EList<OperationProxy> proxies;

	private ImageProxy imageProxy;
	private String label = ""; //$NON-NLS-1$
	private int index = -1;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected OperationProxyImpl() {
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
		return VersioningPackage.Literals.OPERATION_PROXY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<OperationProxy> getProxies() {
		if (proxies == null) {
			proxies = new EObjectContainmentEList.Resolving<OperationProxy>(OperationProxy.class, this,
				VersioningPackage.OPERATION_PROXY__PROXIES);
		}
		return proxies;
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
		case VersioningPackage.OPERATION_PROXY__PROXIES:
			return ((InternalEList<?>) getProxies()).basicRemove(otherEnd, msgs);
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
		case VersioningPackage.OPERATION_PROXY__PROXIES:
			return getProxies();
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
		case VersioningPackage.OPERATION_PROXY__PROXIES:
			getProxies().clear();
			getProxies().addAll((Collection<? extends OperationProxy>) newValue);
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
		case VersioningPackage.OPERATION_PROXY__PROXIES:
			getProxies().clear();
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
		case VersioningPackage.OPERATION_PROXY__PROXIES:
			return proxies != null && !proxies.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy#setLabel(java.lang.String)
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy#setImage(org.eclipse.emf.emfstore.internal.server.model.versioning.ImageProxy)
	 */
	public void setImage(ImageProxy imageProxy) {
		this.imageProxy = imageProxy;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy#getImage()
	 */
	public ImageProxy getImage() {
		return imageProxy;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy#getLabel()
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy#getIndex()
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy#setIndex(int)
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy#isLabelProviderReady()
	 */
	public boolean isLabelProviderReady() {
		return !label.equals(StringUtils.EMPTY) && getImage() != null;
	}

} // OperationProxyImpl
