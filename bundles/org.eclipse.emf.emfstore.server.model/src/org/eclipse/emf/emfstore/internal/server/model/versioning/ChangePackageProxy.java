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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Change Package Proxy</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageProxy#getId <em>Id</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getChangePackageProxy()
 * @model
 * @generated
 */
public interface ChangePackageProxy extends AbstractChangePackage {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getChangePackageProxy_Id()
	 * @model
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageProxy#getId
	 * <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

} // ChangePackageProxy
