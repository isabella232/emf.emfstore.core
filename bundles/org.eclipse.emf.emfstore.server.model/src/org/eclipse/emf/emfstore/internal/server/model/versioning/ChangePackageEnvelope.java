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
package org.eclipse.emf.emfstore.internal.server.model.versioning;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Change Package Envelope</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope#getFragmentIndex <em>
 * Fragment Index</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope#getFragmentCount <em>
 * Fragment Count</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope#getFragment <em>Fragment
 * </em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getChangePackageEnvelope()
 * @model
 * @generated
 */
public interface ChangePackageEnvelope extends EObject {
	/**
	 * Returns the value of the '<em><b>Fragment Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fragment Index</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Fragment Index</em>' attribute.
	 * @see #setFragmentIndex(int)
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getChangePackageEnvelope_FragmentIndex()
	 * @model required="true"
	 * @generated
	 */
	int getFragmentIndex();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope#getFragmentIndex
	 * <em>Fragment Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>Fragment Index</em>' attribute.
	 * @see #getFragmentIndex()
	 * @generated
	 */
	void setFragmentIndex(int value);

	/**
	 * Returns the value of the '<em><b>Fragment Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fragment Count</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Fragment Count</em>' attribute.
	 * @see #setFragmentCount(int)
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getChangePackageEnvelope_FragmentCount()
	 * @model required="true"
	 * @generated
	 */
	int getFragmentCount();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope#getFragmentCount
	 * <em>Fragment Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>Fragment Count</em>' attribute.
	 * @see #getFragmentCount()
	 * @generated
	 */
	void setFragmentCount(int value);

	/**
	 * Returns the value of the '<em><b>Fragment</b></em>' containment reference list.
	 * The list contents are of type
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fragment</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Fragment</em>' containment reference list.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getChangePackageEnvelope_Fragment()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	EList<AbstractOperation> getFragment();

	/**
	 * Whether the fragment contained in this envelope is the last one in a series of fragments.
	 *
	 * @return {@code true}, if the fragment contained in this envelope is the last one, {@code false}
	 *
	 * @generated NOT
	 */
	boolean isLast();

} // ChangePackageEnvelope
