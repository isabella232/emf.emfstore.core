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
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.HasChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Abstract Change Package</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#getLogMessage <em>Log
 * Message</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getAbstractChangePackage()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface AbstractChangePackage extends EObject {

	/**
	 * Returns the value of the '<em><b>Log Message</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Log Message</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Log Message</em>' containment reference.
	 * @see #setLogMessage(LogMessage)
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getAbstractChangePackage_LogMessage()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	LogMessage getLogMessage();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#getLogMessage
	 * <em>Log Message</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value the new value of the '<em>Log Message</em>' containment reference.
	 * @see #getLogMessage()
	 * @generated
	 */
	void setLogMessage(LogMessage value);

	/**
	 * Add a single operation to this change package.
	 * 
	 * @param operation
	 *            the operation to be added
	 * 
	 * @generated NOT
	 */
	void add(AbstractOperation operation);

	/**
	 * Adds all given operations to this change package.
	 * 
	 * @param operations
	 *            the operations to be added
	 * 
	 * @generated NOT
	 */
	void addAll(List<AbstractOperation> operations);

	/**
	 * Creates a copy of the change package and reverses it.
	 * 
	 * @return the reversed change packages
	 * 
	 * @generated NOT
	 */
	AbstractChangePackage reverse();

	/**
	 * Returns the size of this change package, that is, how many operations are contained in it.
	 * 
	 * @return the number of operations contained in this change package
	 * 
	 * @generated NOT
	 */
	int size();

	/**
	 * Returns the number of leaf operations that are contained within this change package.
	 * 
	 * @return the number of leaf operations contained in this change package
	 * 
	 * @generated NOT
	 */
	int leafSize();

	/**
	 * Returns a handle for iterating through operations contained in this change package.<br>
	 * <strong>NOTE</strong>: Callers must call {@code close} on the returned handle.
	 * 
	 * @return a handle that enables iterating through all operations of this change package
	 * 
	 * @generated NOT
	 */
	ESCloseableIterable<AbstractOperation> operations();

	/**
	 * Returns a handle for iterating through operations contained in this change package backwards.<br>
	 * <strong>NOTE</strong>: Callers must call {@code close} on the returned handle.
	 * 
	 * @return a handle that enables iterating through all operations of this change package
	 * 
	 * @generated NOT
	 */
	ESCloseableIterable<AbstractOperation> reversedOperations();

	/**
	 * Whether this change package has any operations.
	 * 
	 * @return {@code true}, if this change package is empty, {@code false} otherwise
	 * 
	 * @generated NOT
	 */
	boolean isEmpty();

	/**
	 * Removes the given number of operations starting from the end.
	 * 
	 * @param n
	 *            the number of operations to be removed
	 * 
	 * @return the removed operations
	 * 
	 * @generated NOT
	 */
	List<AbstractOperation> removeAtEnd(int n);

	/**
	 * Clears all operations from this change package.
	 * 
	 * @generated NOT
	 */
	void clear();

	/**
	 * Attaches this change package to the given value.
	 * 
	 * @param changePackageHolder
	 *            the value to which this change package will be attached to
	 * 
	 * @generated NOT
	 */
	void attachToProjectSpace(HasChangePackage changePackageHolder);

	/**
	 * Returns the API representation of this change package.
	 * 
	 * @return the API representation of this change package.
	 * 
	 * @generated NOT
	 */
	// type does not implement APIDelegate
	ESChangePackage toAPI();

	/**
	 * Applies this change package to the given project.
	 * 
	 * @param project
	 *            the project upon which the change package should be applied
	 */
	void apply(Project project);

	/**
	 * Apply all operations in the change package to the given project.
	 * Additional you can force the operations to be applied with illegal
	 * operations being ignored.
	 * 
	 * @param project
	 *            the project
	 * @param forceApplication
	 *            if true, illegal Operations won't stop the other to be applied
	 */
	void apply(Project project, boolean forceApplication);

	/**
	 * Save this change package.
	 * 
	 * @throws IOException in case saving fails
	 */
	void save() throws IOException;

} // AbstractChangePackage
