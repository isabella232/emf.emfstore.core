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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Operation Proxy</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy#getProxies <em>Proxies</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getOperationProxy()
 * @model
 * @generated
 */
public interface OperationProxy extends EObject {
	/**
	 * Returns the value of the '<em><b>Proxies</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Proxies</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Proxies</em>' containment reference list.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getOperationProxy_Proxies()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	EList<OperationProxy> getProxies();

	/**
	 * Sets the label to be used by the label provider.
	 *
	 * @param label the label to be set
	 *
	 * @generated NOT
	 */
	void setLabel(String label);

	/**
	 * Sets the image to be used by the label provider.
	 *
	 * @param image the image to be set
	 *
	 * @generated NOT
	 */
	void setImage(Image image);

	/**
	 * Returns the image to be used by the label provider.
	 *
	 * @return the image
	 *
	 * @generated NOT
	 */
	Image getImage();

	/**
	 * Returns the label to be used by the label provider.
	 *
	 * @return the label
	 *
	 * @generated NOT
	 */
	String getLabel();

	/**
	 * Returns the index within the operations file.
	 *
	 * @return the index of the operation within the operations file
	 *
	 * @generated NOT
	 */
	int getIndex();

	/**
	 * Sets the index that corresponds to the location within the operations file.
	 *
	 * @param index the index within the operations file
	 */
	void setIndex(int index);

	/**
	 * Determines whether this proxy has everything needed to be visualized by an label provider.
	 *
	 * @return {@code true}, if this proxy has its label and image set, {@code false} otherwise
	 */
	boolean isLabelProviderReady();

} // OperationProxy
