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

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageProxy;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.ChangePackageContainer;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Change Package Proxy</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageProxyImpl#getLogMessage <em>
 * Log Message</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageProxyImpl#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ChangePackageProxyImpl extends EObjectImpl implements ChangePackageProxy {
	/**
	 * The cached value of the '{@link #getLogMessage() <em>Log Message</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getLogMessage()
	 * @generated
	 * @ordered
	 */
	protected LogMessage logMessage;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected ChangePackageProxyImpl() {
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
		return VersioningPackage.Literals.CHANGE_PACKAGE_PROXY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public LogMessage getLogMessage() {
		if (logMessage != null && logMessage.eIsProxy()) {
			final InternalEObject oldLogMessage = (InternalEObject) logMessage;
			logMessage = (LogMessage) eResolveProxy(oldLogMessage);
			if (logMessage != oldLogMessage) {
				final InternalEObject newLogMessage = (InternalEObject) logMessage;
				NotificationChain msgs = oldLogMessage.eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- VersioningPackage.CHANGE_PACKAGE_PROXY__LOG_MESSAGE, null, null);
				if (newLogMessage.eInternalContainer() == null) {
					msgs = newLogMessage.eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- VersioningPackage.CHANGE_PACKAGE_PROXY__LOG_MESSAGE, null, msgs);
				}
				if (msgs != null) {
					msgs.dispatch();
				}
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
						VersioningPackage.CHANGE_PACKAGE_PROXY__LOG_MESSAGE, oldLogMessage, logMessage));
				}
			}
		}
		return logMessage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public LogMessage basicGetLogMessage() {
		return logMessage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetLogMessage(LogMessage newLogMessage, NotificationChain msgs) {
		final LogMessage oldLogMessage = logMessage;
		logMessage = newLogMessage;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				VersioningPackage.CHANGE_PACKAGE_PROXY__LOG_MESSAGE, oldLogMessage, newLogMessage);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setLogMessage(LogMessage newLogMessage) {
		if (newLogMessage != logMessage) {
			NotificationChain msgs = null;
			if (logMessage != null) {
				msgs = ((InternalEObject) logMessage).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- VersioningPackage.CHANGE_PACKAGE_PROXY__LOG_MESSAGE, null, msgs);
			}
			if (newLogMessage != null) {
				msgs = ((InternalEObject) newLogMessage).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
					- VersioningPackage.CHANGE_PACKAGE_PROXY__LOG_MESSAGE, null, msgs);
			}
			msgs = basicSetLogMessage(newLogMessage, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.CHANGE_PACKAGE_PROXY__LOG_MESSAGE,
				newLogMessage, newLogMessage));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setId(String newId) {
		final String oldId = id;
		id = newId;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.CHANGE_PACKAGE_PROXY__ID, oldId, id));
		}
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
		case VersioningPackage.CHANGE_PACKAGE_PROXY__LOG_MESSAGE:
			return basicSetLogMessage(null, msgs);
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
		case VersioningPackage.CHANGE_PACKAGE_PROXY__LOG_MESSAGE:
			if (resolve) {
				return getLogMessage();
			}
			return basicGetLogMessage();
		case VersioningPackage.CHANGE_PACKAGE_PROXY__ID:
			return getId();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case VersioningPackage.CHANGE_PACKAGE_PROXY__LOG_MESSAGE:
			setLogMessage((LogMessage) newValue);
			return;
		case VersioningPackage.CHANGE_PACKAGE_PROXY__ID:
			setId((String) newValue);
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
		case VersioningPackage.CHANGE_PACKAGE_PROXY__LOG_MESSAGE:
			setLogMessage((LogMessage) null);
			return;
		case VersioningPackage.CHANGE_PACKAGE_PROXY__ID:
			setId(ID_EDEFAULT);
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
		case VersioningPackage.CHANGE_PACKAGE_PROXY__LOG_MESSAGE:
			return logMessage != null;
		case VersioningPackage.CHANGE_PACKAGE_PROXY__ID:
			return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
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
		result.append(" (id: "); //$NON-NLS-1$
		result.append(id);
		result.append(')');
		return result.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#add(org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation)
	 */
	public void add(AbstractOperation operation) {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#addAll(java.util.List)
	 */
	public void addAll(List<AbstractOperation> operations) {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#reverse()
	 */
	public AbstractChangePackage reverse() {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#size()
	 */
	public int size() {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#leafSize()
	 */
	public int leafSize() {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#operations()
	 */
	public ESCloseableIterable<AbstractOperation> operations() {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#reversedOperations()
	 */
	public ESCloseableIterable<AbstractOperation> reversedOperations() {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#isEmpty()
	 */
	public boolean isEmpty() {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#removeAtEnd(int)
	 */
	public List<AbstractOperation> removeAtEnd(int n) {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#clear()
	 */
	public void clear() {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#attachToProjectSpace(org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.ChangePackageContainer)
	 */
	public void attachToProjectSpace(ChangePackageContainer changePackageHolder) {
		throw new NotImplementedException();

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#toAPI()
	 */
	public ESChangePackage toAPI() {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#apply(org.eclipse.emf.emfstore.internal.common.model.Project)
	 */
	public void apply(Project project) {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#apply(org.eclipse.emf.emfstore.internal.common.model.Project,
	 *      boolean)
	 */
	public void apply(Project project, boolean forceApplication) {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#save()
	 */
	public void save() throws IOException {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#get(int)
	 */
	public AbstractOperation get(int index) {
		throw new NotImplementedException();
	}

} // ChangePackageProxyImpl
