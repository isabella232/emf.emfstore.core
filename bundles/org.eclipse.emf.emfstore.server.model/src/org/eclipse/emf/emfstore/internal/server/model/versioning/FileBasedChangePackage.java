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
package org.eclipse.emf.emfstore.internal.server.model.versioning;

import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.internal.common.api.APIDelegate;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>File Based Change Package</b></em>'.
 *
 * @extends APIDelegate<ESChangePackage>
 *          <!-- end-user-doc -->
 *
 *          <p>
 *          The following features are supported:
 *          <ul>
 *          <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#getFilePath <em>
 *          File Path</em>}</li>
 *          <li>
 *          {@link org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#getOperationProxies
 *          <em>Operation Proxies</em>}</li>
 *          </ul>
 *          </p>
 *
 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getFileBasedChangePackage()
 * @model
 * @generated
 */
public interface FileBasedChangePackage extends AbstractChangePackage, APIDelegate<ESChangePackage> {

	/**
	 * Returns the value of the '<em><b>File Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File Path</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>File Path</em>' attribute.
	 * @see #setFilePath(String)
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getFileBasedChangePackage_FilePath()
	 * @model
	 * @generated
	 */
	String getFilePath();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#getFilePath
	 * <em>File Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>File Path</em>' attribute.
	 * @see #getFilePath()
	 * @generated
	 */
	void setFilePath(String value);

	/**
	 * Returns the value of the '<em><b>Operation Proxies</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operation Proxies</em>' containment reference list isn't clear, there really should be
	 * more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Operation Proxies</em>' containment reference list.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getFileBasedChangePackage_OperationProxies()
	 * @model containment="true" resolveProxies="true" transient="true"
	 * @generated
	 */
	EList<OperationProxy> getOperationProxies();

	/**
	 * Initializes this change package.
	 *
	 * @param filePath
	 *            the file path where the change package should be initialized
	 *
	 * @generated NOT
	 */
	void initialize(String filePath);

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#reverse()
	 */
	FileBasedChangePackage reverse();

	/**
	 * Converts this file-based change package to an in-memory change package.<br>
	 * <b>NOTE</b>: for very big change packages this might cause a serious
	 * performance hit and also cause {@link OutOfMemoryError}s.
	 *
	 * @return an in-memory change-package
	 */
	ChangePackage toInMemoryChangePackage();

	/**
	 * Returns the file path to the temporary file that is used
	 * in between saves.
	 *
	 * @return the absolute path to the temporary file
	 */
	String getTempFilePath();

	/**
	 * Moves this change package.
	 *
	 * @param newFilePath
	 *            the file path where the change package should be moved to
	 * @throws IOException in case of an error during move
	 *
	 * @generated NOT
	 */
	void move(String newFilePath) throws IOException;

} // FileBasedChangePackage
