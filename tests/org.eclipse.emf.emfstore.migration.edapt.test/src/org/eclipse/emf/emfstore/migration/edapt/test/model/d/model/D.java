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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>D</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D#getAtts <em>Atts</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D#getRefs <em>Refs</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.DPackage#getD()
 * @model
 * @generated
 */
public interface D extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.DPackage#getD_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D#getName <em>Name</em>
	 * }' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Atts</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Atts</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Atts</em>' attribute list.
	 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.DPackage#getD_Atts()
	 * @model
	 * @generated
	 */
	EList<String> getAtts();

	/**
	 * Returns the value of the '<em><b>Refs</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Refs</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Refs</em>' reference list.
	 * @see org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.DPackage#getD_Refs()
	 * @model
	 * @generated
	 */
	EList<D> getRefs();

} // D
