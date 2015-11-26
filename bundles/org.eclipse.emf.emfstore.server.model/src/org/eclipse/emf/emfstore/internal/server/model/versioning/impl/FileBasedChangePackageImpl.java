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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
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
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl;
import org.eclipse.emf.emfstore.internal.common.ResourceFactoryRegistry;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESFileBasedChangePackageImpl;
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

	/**
	 * @generated NOT
	 */
	private static final URI VIRTUAL_URI = URI.createURI("virtualResource.xmi"); // $NON-NLS-0$ //$NON-NLS-1$

	/**
	 * @generated NOT
	 */
	private static final String EMPTY_CHANGE_PACKAGE = XmlTags.XML_HEADER + XmlTags.CHANGE_PACKAGE_START
		+ XmlTags.CHANGE_PACKAGE_END;

	/**
	 * @generated NOT
	 */
	private static final String TEMP_FILE_PREFIX = "temp-"; //$NON-NLS-1$

	/**
	 * @generated NOT
	 */
	private static final String TEMP_SUFFIX = ".temp"; //$NON-NLS-1$

	// FIXME we also have a constant for this on the client side
	private static final String OPERATION_FILE_SUFFIX = ".eoc"; //$NON-NLS-1$

	/**
	 * Index of an operations file tuple consisting of the actual and the temporary file.
	 */
	public static final String FILE_OP_INDEX = ".1"; //$NON-NLS-1$

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
	private static Map<Object, Object> resourceOptions;

	/**
	 * The API representation of this change package.
	 *
	 * @generated NOT
	 */
	private ESChangePackage apiImpl;

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

	private Optional<Integer> cachedSize = Optional.absent();
	private Optional<Integer> cachedLeafSize = Optional.absent();

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
				NotificationChain msgs = oldLogMessage.eInverseRemove(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE, null, null);
				if (newLogMessage.eInternalContainer() == null) {
					msgs = newLogMessage.eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE, null, msgs);
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
				msgs = ((InternalEObject) logMessage).eInverseRemove(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE, null, msgs);
			}
			if (newLogMessage != null) {
				msgs = ((InternalEObject) newLogMessage).eInverseAdd(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE, null, msgs);
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
		return getTempFilePath(filePath);
	}

	private String getTempFilePath(String filePath) {
		return filePath + TEMP_SUFFIX;
	}

	/**
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
	 * @generated NOT
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
	 * @generated NOT
	 */
	public ESChangePackage createAPI() {
		return new ESFileBasedChangePackageImpl(this);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @generated NOT
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#addAll(java.util.List)
	 */
	public void addAll(List<AbstractOperation> ops) {
		for (final AbstractOperation op : ops) {
			add(op);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#add(org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation)
	 * @generated NOT
	 */
	public void add(AbstractOperation op) {

		updateCaches(1, op.getLeafOperations().size());

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final Resource resource = createVirtualResource();
		resource.getContents().add(op);

		Optional<RandomAccessFile> maybeRandomAccessFile = Optional.absent();

		try {
			outputStream.write(asBytes(XmlTags.OPERATIONS_START_TAG + XmlTags.NEWLINE));

			final XMLHelperImpl helper = new XMLHelperImpl((XMLResource) resource);
			final XMLSaveImpl save = new XMLSaveImpl(helper);
			save.save((XMLResource) resource, outputStream, resourceOptions());
			// resource.save(outputStream, resourceOptions());

			outputStream.write(asBytes(XmlTags.OPERATIONS_END_TAG + XmlTags.NEWLINE));

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
	 * @generated NOT
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

	/**
	 * Copies this change package.
	 *
	 * @return the copied change package
	 *
	 * @generated NOT
	 */
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

	/**
	 * Returns an {@link ESCloseableIterable} that returns the operations in a backwards fashion.
	 *
	 * @return {@link ESCloseableIterable} that must be close after iteration
	 *
	 * @generated NOT
	 */
	public ESCloseableIterable<AbstractOperation> reversedOperations() {
		return new FileBasedOperationIterable(getTempFilePath(), Direction.Backward);
	}

	/**
	 *
	 * @generated NOT
	 */
	private static Map<Object, Object> resourceOptions() {
		if (resourceOptions == null) {
			resourceOptions = new LinkedHashMap<Object, Object>();
			resourceOptions.put(XMLResource.OPTION_DECLARE_XML, Boolean.FALSE);
			resourceOptions.put(XMLResource.OPTION_RECORD_ANY_TYPE_NAMESPACE_DECLARATIONS, Boolean.TRUE);
		}
		return resourceOptions;
	}

	/**
	 *
	 * @generated NOT
	 */
	private static byte[] asBytes(String s) {
		return s.getBytes();
	}

	/**
	 *
	 * @generated NOT
	 */
	private static Resource createVirtualResource() {
		final ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.setResourceFactoryRegistry(new ResourceFactoryRegistry());
		return resourceSet.createResource(VIRTUAL_URI);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#size()
	 * @generated NOT
	 */
	public int size() {
		if (!cachedSize.isPresent()) {
			computeSize();
		}
		return cachedSize.get();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#operations()
	 * @generated NOT
	 */
	public ESCloseableIterable<AbstractOperation> operations() {
		return new FileBasedOperationIterable(getTempFilePath(), Direction.Forward);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#isEmpty()
	 * @generated NOT
	 */
	public boolean isEmpty() {
		if (!cachedSize.isPresent()) {
			computeSize();
		}
		return cachedSize.get() == 0;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#removeAtEnd(int)
	 * @generated NOT
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

			int removedOps = 0;
			int removedLeafOps = 0;
			while (counter > 0 && maybeOperation.isPresent()) {
				operation = maybeOperation.get();
				ops.add(operation);
				removedOps += 1;
				removedLeafOps += operation.getLeafOperations().size();
				counter -= 1;
			}

			updateCaches(-removedOps, -removedLeafOps);

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

	private void invalidateCaches() {
		cachedSize = Optional.absent();
		cachedLeafSize = Optional.absent();
	}

	private void updateCaches(int size, int leafSize) {
		final int lSize = cachedSize.isPresent() ? cachedSize.get() : 0;
		final int lLeafSize = cachedLeafSize.isPresent() ? cachedLeafSize.get() : 0;
		final int newSize = lSize + size;
		final int newLeafSize = lLeafSize + leafSize;
		cachedSize = Optional.of(newSize >= 0 ? newSize : 0);
		cachedLeafSize = Optional.of(newLeafSize >= 0 ? newLeafSize : 0);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#clear()
	 * @generated NOT
	 */
	public void clear() {
		invalidateCaches();
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
	 * @generated NOT
	 */
	public void attachToProjectSpace(ChangePackageContainer changePackageContainer) {
		final URI changePackageUri = changePackageContainer.getChangePackageUri();
		final String operationFileString = changePackageUri.toFileString();
		final File tempOperationFile = new File(operationFileString + FILE_OP_INDEX + TEMP_SUFFIX);
		final File thisFile = new File(getTempFilePath());
		try {
			FileUtil.copyFile(thisFile, tempOperationFile);
			thisFile.delete();
			setFilePath(operationFileString + FILE_OP_INDEX);
			changePackageContainer.setChangePackage(this);
		} catch (final IOException ex) {
			// ESException not available
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @generated NOT
	 */
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
	 * @generated NOT
	 */
	public int leafSize() {
		if (!cachedLeafSize.isPresent()) {
			computeSize();
		}
		return cachedLeafSize.get();
	}

	private void computeSize() {
		Optional<ReversedLinesFileReader> maybeReversedReader = Optional.absent();
		int size = 0;
		int leafSize = 0;
		try {
			final ReversedLinesFileReader reversedReader = new ReversedLinesFileReader(new File(getTempFilePath()));
			maybeReversedReader = Optional.of(reversedReader);
			String line;
			while ((line = reversedReader.readLine()) != null) {
				if (line.contains(XmlTags.OPERATIONS_END_TAG)) {
					size += 1;
				} else if (line.contains(XmlTags.SUB_OPERATIONS_END_TAG)) {
					leafSize += 1;
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
		updateCaches(size, leafSize);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#initialize(java.lang.String)
	 * @generated NOT
	 */
	public void initialize(String filePath) {
		setFilePath(filePath);
		initializeEmptyChangePackage();
	}

	/**
	 * @generated NOT
	 */
	private void initializeEmptyChangePackage() {
		Optional<FileWriter> maybeWriter = Optional.absent();
		Optional<FileWriter> maybeTempWriter = Optional.absent();
		try {
			final FileWriter fileWriter = new FileWriter(getFilePath());
			final FileWriter tempFileWriter = new FileWriter(getTempFilePath());
			maybeWriter = Optional.of(fileWriter);
			maybeTempWriter = Optional.of(tempFileWriter);
			fileWriter.write(EMPTY_CHANGE_PACKAGE);
			tempFileWriter.write(EMPTY_CHANGE_PACKAGE);
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
			if (maybeTempWriter.isPresent()) {
				try {
					maybeTempWriter.get().close();
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
	 * @generated NOT
	 */
	public void apply(Project project) {
		apply(project, false);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#apply(org.eclipse.emf.emfstore.internal.common.model.Project,
	 *      boolean)
	 * @generated NOT
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
	 * @generated NOT
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
	 * @generated NOT
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
	 * @generated NOT
	 */
	public void save() throws IOException {
		final File tempFile = new File(getTempFilePath());
		final File filePath = new File(getFilePath());
		FileUtil.copyFile(tempFile, filePath);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#move(java.lang.String)
	 * @generated NOT
	 */
	public void move(String newFilePath) throws IOException {
		if (newFilePath == null) {
			throw new IOException();
		}
		if (newFilePath.equals(getFilePath())) {
			return;
		}

		final File currentTempFile = new File(getTempFilePath());
		final File currentFilePath = new File(getFilePath());
		final File targetTempFile = new File(getTempFilePath(newFilePath));
		final File targetFilePath = new File(newFilePath);

		FileUtil.moveAndOverwrite(currentTempFile, targetTempFile);
		try {
			FileUtil.moveAndOverwrite(currentFilePath, targetFilePath);
		} catch (final IOException ex) {
			// if the temp-file could be moved, but the actual file fails,
			// we will try to recover by moving the temp file back
			FileUtil.moveAndOverwrite(targetTempFile, currentTempFile);
			throw ex;
		}

		// all is fine, set the new file path
		setFilePath(newFilePath);
	}

} // FileBasedChangePackageImpl
