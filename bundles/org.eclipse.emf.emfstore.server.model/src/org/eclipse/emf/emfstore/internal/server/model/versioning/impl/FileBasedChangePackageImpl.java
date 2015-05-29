/*******************************************************************************
 * Copyright (c) 2008-2015 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.emfstore.internal.common.ResourceFactoryRegistry;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESFileBasedChangePackageImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.ChangePackageContainer;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.Direction;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.FileBasedOperationIterable;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.OperationEmitter;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.ReadLineCapable;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.XmlTags;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util.ChangePackageUtil;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>File Based Change Package</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.FileBasedChangePackageImpl#getLogMessage
 * <em>Log Message</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.FileBasedChangePackageImpl#getFilePath <em>
 * File Path</em>}</li>
 * <li>
 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.FileBasedChangePackageImpl#getOperationProxies
 * <em>Operation Proxies</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FileBasedChangePackageImpl extends EObjectImpl implements FileBasedChangePackage {

	private static final String EMPTY_CHANGE_PACKAGE = XmlTags.XML_HEADER + XmlTags.CHANGE_PACKAGE_START
		+ XmlTags.CHANGE_PACKAGE_END;

	private static final String TEMP_FILE_PREFIX = "temp-"; //$NON-NLS-1$

	private static final String TEMP_SUFFIX = ".temp"; //$NON-NLS-1$

	// FIXME we also have a constant for this on the client side
	private static final String OPERATION_FILE_SUFFIX = ".eoc"; //$NON-NLS-1$

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
	 * Resource loading options.
	 *
	 * @generated NOT
	 */
	private static Map<Object, Object> loadingOptions;

	/**
	 * The API representation of this change package.
	 *
	 * @generated NOT
	 */
	private ESChangePackage apiImpl;

	/**
	 * Whether this change package has been initialized.
	 *
	 * FIXME: move to Ecore? Maybe we don't even need this
	 *
	 * @generated NOT
	 */
	private boolean needsInit;

	/**
	 * The default value of the '{@link #getFilePath() <em>File Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getFilePath()
	 * @generated
	 * @ordered
	 */
	protected static final String FILE_PATH_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getFilePath() <em>File Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getFilePath()
	 * @generated
	 * @ordered
	 */
	protected String filePath = FILE_PATH_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOperationProxies() <em>Operation Proxies</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getOperationProxies()
	 * @generated
	 * @ordered
	 */
	protected EList<OperationProxy> operationProxies;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected FileBasedChangePackageImpl() {
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
		return VersioningPackage.Literals.FILE_BASED_CHANGE_PACKAGE;
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
					- VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE, null, null);
				if (newLogMessage.eInternalContainer() == null) {
					msgs = newLogMessage.eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE, null, msgs);
				}
				if (msgs != null) {
					msgs.dispatch();
				}
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
						VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE, oldLogMessage, logMessage));
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
				VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE, oldLogMessage, newLogMessage);
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
					- VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE, null, msgs);
			}
			if (newLogMessage != null) {
				msgs = ((InternalEObject) newLogMessage).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
					- VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE, null, msgs);
			}
			msgs = basicSetLogMessage(newLogMessage, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE, newLogMessage, newLogMessage));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Returns the path to the temporary file
	 *
	 * @return the temporary file path
	 *
	 * @generated NOT
	 */
	public String getTempFilePath() {
		return filePath + TEMP_SUFFIX;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setFilePath(String newFilePath) {
		final String oldFilePath = filePath;
		filePath = newFilePath;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				VersioningPackage.FILE_BASED_CHANGE_PACKAGE__FILE_PATH, oldFilePath, filePath));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<OperationProxy> getOperationProxies() {
		if (operationProxies == null) {
			operationProxies = new EObjectContainmentEList.Resolving<OperationProxy>(OperationProxy.class, this,
				VersioningPackage.FILE_BASED_CHANGE_PACKAGE__OPERATION_PROXIES);
		}
		return operationProxies;
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
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE:
			return basicSetLogMessage(null, msgs);
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__OPERATION_PROXIES:
			return ((InternalEList<?>) getOperationProxies()).basicRemove(otherEnd, msgs);
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
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE:
			if (resolve) {
				return getLogMessage();
			}
			return basicGetLogMessage();
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__FILE_PATH:
			return getFilePath();
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__OPERATION_PROXIES:
			return getOperationProxies();
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
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE:
			setLogMessage((LogMessage) newValue);
			return;
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__FILE_PATH:
			setFilePath((String) newValue);
			return;
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__OPERATION_PROXIES:
			getOperationProxies().clear();
			getOperationProxies().addAll((Collection<? extends OperationProxy>) newValue);
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
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE:
			setLogMessage((LogMessage) null);
			return;
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__FILE_PATH:
			setFilePath(FILE_PATH_EDEFAULT);
			return;
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__OPERATION_PROXIES:
			getOperationProxies().clear();
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
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE:
			return logMessage != null;
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__FILE_PATH:
			return FILE_PATH_EDEFAULT == null ? filePath != null : !FILE_PATH_EDEFAULT.equals(filePath);
		case VersioningPackage.FILE_BASED_CHANGE_PACKAGE__OPERATION_PROXIES:
			return operationProxies != null && !operationProxies.isEmpty();
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
		result.append(" (filePath: "); //$NON-NLS-1$
		result.append(filePath);
		result.append(')');
		return result.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#toAPI()
	 */
	public ESChangePackage toAPI() {
		if (apiImpl == null) {
			apiImpl = createAPI();
		}
		return apiImpl;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#createAPI()
	 */
	public ESChangePackage createAPI() {
		return new ESFileBasedChangePackageImpl(this);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#addAll(java.util.List)
	 */
	public void addAll(List<AbstractOperation> ops) {
		// FIXME: LCP - file is reopened for each operation
		for (final AbstractOperation op : ops) {
			add(op);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#add(org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation)
	 */
	public void add(final AbstractOperation op) {

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final Resource resource = createVirtualResource();
		resource.getContents().add(op);

		Optional<RandomAccessFile> maybeRandomAccessFile = Optional.absent();

		try {
			outputStream.write(asBytes(XmlTags.OPERATIONS_START_TAG + XmlTags.NEWLINE));
			resource.save(outputStream, loadingOptions());
			outputStream.write(asBytes(XmlTags.OPERATIONS_END_TAG + XmlTags.NEWLINE));

			if (needsInit) {
				initializeEmptyChangePackage();
			}

			final RandomAccessFile randomAccessFile = new RandomAccessFile(getTempFilePath(), "rw"); //$NON-NLS-1$
			maybeRandomAccessFile = Optional.of(randomAccessFile);
			randomAccessFile.skipBytes((int) (randomAccessFile.length() - asBytes(XmlTags.CHANGE_PACKAGE_END).length));
			randomAccessFile.write(outputStream.toByteArray());
			randomAccessFile.write(asBytes(XmlTags.CHANGE_PACKAGE_END));
		} catch (final IOException e) {
			// ESException not available
			throw new IllegalStateException(e);
		} finally {
			try {
				outputStream.close();
				if (maybeRandomAccessFile.isPresent()) {
					maybeRandomAccessFile.get().close();
				}
			} catch (final IOException ex) {
				ModelUtil.logException(ex);
			}
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#reverse()
	 */
	public FileBasedChangePackage reverse() {
		// create a new temporary change package that will contain all reversed operations
		final FileBasedChangePackage reversedChangePackage = VersioningFactory.eINSTANCE.createFileBasedChangePackage();
		try {
			final File tempFile = File.createTempFile(TEMP_FILE_PREFIX, OPERATION_FILE_SUFFIX);
			tempFile.deleteOnExit();
			reversedChangePackage.initialize(tempFile.getAbsolutePath());
		} catch (final IOException ex) {
			// we cannot throw ESException, since it cannot be brought into scope
			throw new IllegalStateException(ex);
		}
		final ESCloseableIterable<AbstractOperation> operationsHandle = reversedOperations();
		try {
			final Iterable<AbstractOperation> operations = operationsHandle.iterable();
			for (final AbstractOperation operation : operations) {
				final AbstractOperation reversedOperation = operation.reverse();
				reversedChangePackage.add(reversedOperation);
			}
		} finally {
			operationsHandle.close();
		}
		return reversedChangePackage;
	}

	public FileBasedChangePackage copy() {
		final FileBasedChangePackage changePackage = VersioningFactory.eINSTANCE.createFileBasedChangePackage();
		try {
			final File tempFile = File.createTempFile(TEMP_FILE_PREFIX, OPERATION_FILE_SUFFIX);
			tempFile.deleteOnExit();
			changePackage.initialize(tempFile.getAbsolutePath());
		} catch (final IOException ex) {
			throw new IllegalStateException(ex);
		}
		final ESCloseableIterable<AbstractOperation> operationsHandle = operations();
		try {
			final Iterable<AbstractOperation> operations = operationsHandle.iterable();
			for (final AbstractOperation operation : operations) {
				changePackage.add(operation);
			}
		} finally {
			operationsHandle.close();
		}
		return changePackage;
	}

	public ESCloseableIterable<AbstractOperation> reversedOperations() {
		return new FileBasedOperationIterable(getTempFilePath(), Direction.Backward);
	}

	private static Map<Object, Object> loadingOptions() {
		if (loadingOptions == null) {
			loadingOptions = new LinkedHashMap<Object, Object>();
			loadingOptions.put(XMLResource.OPTION_DECLARE_XML, Boolean.FALSE);
			loadingOptions.put(XMLResource.OPTION_RECORD_ANY_TYPE_NAMESPACE_DECLARATIONS, Boolean.TRUE);
		}
		return loadingOptions;
	}

	private static byte[] asBytes(String s) {
		return s.getBytes();
	}

	private static Resource createVirtualResource() {
		final ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.setResourceFactoryRegistry(new ResourceFactoryRegistry());
		return resourceSet.createResource(URI.createURI("virtualResource.xmi")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#size()
	 */
	public int size() {
		int counter = 0;
		Optional<ReversedLinesFileReader> maybeReversedReader = Optional.absent();
		try {
			final ReversedLinesFileReader reversedReader = new ReversedLinesFileReader(new File(getTempFilePath()));
			maybeReversedReader = Optional.of(reversedReader);
			String line;
			while ((line = reversedReader.readLine()) != null) {
				if (line.contains(XmlTags.OPERATIONS_END_TAG)) {
					counter++;
				}
			}
		} catch (final IOException ex) {
			// ESException not available
			throw new IllegalStateException(ex);
		} finally {
			if (maybeReversedReader.isPresent()) {
				try {
					maybeReversedReader.get().close();
				} catch (final IOException ex) {
					ModelUtil.logException(ex);
				}
			}
		}
		return counter;

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#operations()
	 */
	public ESCloseableIterable<AbstractOperation> operations() {
		return new FileBasedOperationIterable(getTempFilePath(), Direction.Forward);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#isEmpty()
	 */
	public boolean isEmpty() {
		Optional<BufferedReader> maybeReader = Optional.absent();
		try {
			final File file = new File(getTempFilePath());
			if (!file.exists()) {
				return true;
			}
			// FIXME: move reader into OperationEmitter?
			maybeReader = Optional.of(new BufferedReader(new FileReader(file)));
			final OperationEmitter operationEmitter = new OperationEmitter(Direction.Forward);
			final ReadLineCapable create = ReadLineCapable.INSTANCE.create(maybeReader.get());
			// check if we have at least one operation
			final Optional<AbstractOperation> maybeOp = operationEmitter.tryEmit(create);
			return !maybeOp.isPresent();
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (maybeReader.isPresent()) {
					maybeReader.get().close();
				}
			} catch (final IOException ex) {
				ModelUtil.logException(ex);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#removeAtEnd(int)
	 */
	public List<AbstractOperation> removeAtEnd(int n) {
		final List<AbstractOperation> ops = new ArrayList<AbstractOperation>();
		final OperationEmitter operationEmitter = new OperationEmitter(Direction.Backward);
		Optional<ReversedLinesFileReader> maybeReversedReader = Optional.absent();
		int counter = n;

		try {
			final ReversedLinesFileReader reversedReader = new ReversedLinesFileReader(new File(getTempFilePath()));
			maybeReversedReader = Optional.of(reversedReader);
			AbstractOperation operation;
			final ReadLineCapable reader = ReadLineCapable.INSTANCE.create(reversedReader);
			final Optional<AbstractOperation> maybeOperation = operationEmitter.tryEmit(reader);

			while (counter > 0 && maybeOperation.isPresent()) {
				operation = maybeOperation.get();
				ops.add(operation);
				counter -= 1;
			}

			// FIXME: reuse ReadLineCapable?
			final long offset = operationEmitter.getOffset();

			final RandomAccessFile raf = new RandomAccessFile(getTempFilePath(), "rw"); //$NON-NLS-1$
			final long skip = raf.length() + 1 - offset;
			raf.seek(skip);
			final byte[] bytes = asBytes(XmlTags.NEWLINE + XmlTags.CHANGE_PACKAGE_END);
			raf.write(bytes);
			raf.setLength(skip + bytes.length);
			raf.close();

			return ops;

		} catch (final IOException ex) {
			// ESException not available
			throw new IllegalStateException(ex);
		} finally {
			if (maybeReversedReader.isPresent()) {
				try {
					maybeReversedReader.get().close();
				} catch (final IOException ex) {
					ModelUtil.logException(ex);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#clear()
	 */
	public void clear() {
		Optional<RandomAccessFile> maybeRandomAccessFile = Optional.absent();
		try {
			final RandomAccessFile randomAccessFile = new RandomAccessFile(getTempFilePath(), "rw"); //$NON-NLS-1$
			maybeRandomAccessFile = Optional.of(randomAccessFile);
			randomAccessFile.seek(0);
			randomAccessFile.write(asBytes(EMPTY_CHANGE_PACKAGE));
			randomAccessFile.setLength(EMPTY_CHANGE_PACKAGE.length());
		} catch (final FileNotFoundException ex) {
			throw new RuntimeException(ex);
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (maybeRandomAccessFile.isPresent()) {
				try {
					maybeRandomAccessFile.get().close();
				} catch (final IOException ex) {
					ModelUtil.logException(ex);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#attachToProjectSpace(org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.ChangePackageContainer)
	 */
	public void attachToProjectSpace(ChangePackageContainer changePackageHolder) {
		final URI changePackageUri = changePackageHolder.getChangePackageUri();
		final String operationFileString = changePackageUri.toFileString();
		final File operationFile = new File(operationFileString + TEMP_SUFFIX);
		// operationFile.delete();
		final File thisFile = new File(getTempFilePath());
		try {
			FileUtil.copyFile(thisFile, operationFile);
			thisFile.delete();
			setFilePath(operationFileString);
			changePackageHolder.setChangePackage(this);
		} catch (final IOException ex) {
			// ESException not available
			throw new IllegalStateException(ex);
		}
	}

	public void delete() {
		final File opFile = new File(getFilePath());
		final File tempOpFile = new File(getTempFilePath());
		opFile.delete();
		tempOpFile.delete();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#leafSize()
	 */
	public int leafSize() {
		return ChangePackageUtil.countLeafOperations(Collections.singletonList((AbstractChangePackage) this));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#initialize(java.lang.String)
	 */
	public void initialize(String filePath) {
		setFilePath(filePath);
		initializeEmptyChangePackage();
	}

	private void initializeEmptyChangePackage() {
		needsInit = false;
		Optional<FileWriter> maybeWriter = Optional.absent();
		try {
			final FileWriter fileWriter = new FileWriter(getTempFilePath());
			maybeWriter = Optional.of(fileWriter);
			fileWriter.write(EMPTY_CHANGE_PACKAGE);
		} catch (final IOException ex) {
			// ESException not available
			throw new IllegalStateException(ex);
		} finally {
			if (maybeWriter.isPresent()) {
				try {
					maybeWriter.get().close();
				} catch (final IOException ex) {
					ModelUtil.logException(ex);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#apply(org.eclipse.emf.emfstore.internal.common.model.Project)
	 */
	public void apply(Project project) {
		apply(project, false);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#apply(org.eclipse.emf.emfstore.internal.common.model.Project,
	 *      boolean)
	 */
	public void apply(Project project, boolean forceApplication) {
		final ESCloseableIterable<AbstractOperation> operations = operations();
		try {
			final Iterable<AbstractOperation> operationsIterable = operations.iterable();
			for (final AbstractOperation operation : operationsIterable) {
				try {
					operation.apply(project);
				} catch (final IllegalStateException e) {
					if (!forceApplication) {
						throw e;
					}
				}
			}
		} finally {
			operations.close();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#get(int)
	 */
	public AbstractOperation get(int index) {
		final ESCloseableIterable<AbstractOperation> operations = operations();
		try {
			final Iterable<AbstractOperation> iterable = operations.iterable();
			return Iterables.get(iterable, index);
		} finally {
			operations.close();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#toInMemoryChangePackage()
	 */
	public ChangePackage toInMemoryChangePackage() {
		final ChangePackage changePackage = VersioningFactory.eINSTANCE.createChangePackage();
		final ESCloseableIterable<AbstractOperation> operationsHandle = operations();
		try {
			for (final AbstractOperation operation : operationsHandle.iterable()) {
				changePackage.add(operation);
			}
		} finally {
			operationsHandle.close();
		}

		changePackage.setLogMessage(ModelUtil.clone(getLogMessage()));

		return changePackage;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#save()
	 */
	public void save() throws IOException {
		final File tempFile = new File(getTempFilePath());
		final File filePath = new File(getFilePath());
		FileUtil.copyFile(tempFile, filePath);
	}

} // FileBasedChangePackageImpl
