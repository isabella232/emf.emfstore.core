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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
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
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESFileBasedChangePackageImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.Direction;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.FileBasedOperationIterable;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.HasChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.OperationEmitter;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.ReadLineCapable;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.XmlTags;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;

import com.google.common.base.Optional;

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
public class FileBasedChangePackageImpl extends EObjectImpl implements FileBasedChangePackage
{

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
	 * TODO: move to Ecore? Maybe we don't even need this
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
	protected FileBasedChangePackageImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass()
	{
		return VersioningPackage.Literals.FILE_BASED_CHANGE_PACKAGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public LogMessage getLogMessage()
	{
		if (logMessage != null && logMessage.eIsProxy())
		{
			final InternalEObject oldLogMessage = (InternalEObject) logMessage;
			logMessage = (LogMessage) eResolveProxy(oldLogMessage);
			if (logMessage != oldLogMessage)
			{
				final InternalEObject newLogMessage = (InternalEObject) logMessage;
				NotificationChain msgs = oldLogMessage.eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- VersioningPackage.FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE, null, null);
				if (newLogMessage.eInternalContainer() == null)
				{
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
	public LogMessage basicGetLogMessage()
	{
		return logMessage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetLogMessage(LogMessage newLogMessage, NotificationChain msgs)
	{
		final LogMessage oldLogMessage = logMessage;
		logMessage = newLogMessage;
		if (eNotificationRequired())
		{
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
	public void setLogMessage(LogMessage newLogMessage)
	{
		if (newLogMessage != logMessage)
		{
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
		}
		else if (eNotificationRequired()) {
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
	public String getFilePath()
	{
		return filePath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setFilePath(String newFilePath)
	{
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
	public EList<OperationProxy> getOperationProxies()
	{
		if (operationProxies == null)
		{
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
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
	{
		switch (featureID)
		{
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
	public Object eGet(int featureID, boolean resolve, boolean coreType)
	{
		switch (featureID)
		{
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
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
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
	public void eUnset(int featureID)
	{
		switch (featureID)
		{
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
	public boolean eIsSet(int featureID)
	{
		switch (featureID)
		{
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
	public String toString()
	{
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
		// TODO: LCP - file is reopened for each op
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

		final Resource resource = createVirtualResource();

		resource.getContents().add(op);

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		RandomAccessFile raf = null;
		try {
			outputStream.write(asBytes(XmlTags.OPERATIONS_START_TAG + XmlTags.NEWLINE));
			resource.save(outputStream, loadingOptions());
			outputStream.write((XmlTags.OPERATIONS_END_TAG + XmlTags.NEWLINE).getBytes());

			final File file = new File(getFilePath());

			if (needsInit) {
				needsInit = false;
				final FileWriter writer = new FileWriter(file);
				writer.write(XmlTags.XML_HEADER + XmlTags.CHANGE_PACKAGE_START);
				writer.write(XmlTags.CHANGE_PACKAGE_END);
				writer.close();
			}

			raf = new RandomAccessFile(getFilePath(), "rw"); //$NON-NLS-1$

			raf.skipBytes((int) (raf.length() - XmlTags.CHANGE_PACKAGE_END.getBytes().length));

			raf.write(outputStream.toByteArray());
			raf.write(XmlTags.CHANGE_PACKAGE_END.getBytes());
		} catch (final IOException e) {
			// avoid checked exception since this would lead to cluttered code
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(raf);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#reverse()
	 */
	public FileBasedChangePackage reverse() {
		final FileBasedChangePackage reversedChangePackage = VersioningFactory.eINSTANCE.createFileBasedChangePackage();
		// TODO LCP: where should we put the file
		try {
			final File tempFile = File.createTempFile("temp-", ".eoc"); //$NON-NLS-1$ //$NON-NLS-2$
			tempFile.deleteOnExit();
			reversedChangePackage.initialize(tempFile.getAbsolutePath());
		} catch (final IOException ex) {
			// TODO
			ex.printStackTrace();
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
		// TODO LCP: where should we put the file
		try {
			final File tempFile = File.createTempFile("temp-", ".eoc"); //$NON-NLS-1$ //$NON-NLS-2$
			tempFile.deleteOnExit();
			changePackage.initialize(tempFile.getAbsolutePath());
		} catch (final IOException ex) {
			// TODO
			ex.printStackTrace();
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
		return new FileBasedOperationIterable(getFilePath(), Direction.Backward);
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
		ReversedLinesFileReader reversedLinesFileReader = null;
		try {
			reversedLinesFileReader = new ReversedLinesFileReader(new File(getFilePath()));
			String line;
			while ((line = reversedLinesFileReader.readLine()) != null) {
				if (line.contains(XmlTags.OPERATIONS_END_TAG)) {
					counter++;
				}
			}
		} catch (final IOException ex) {
			// TODO
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reversedLinesFileReader);
		}
		return counter;

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#operations()
	 */
	public ESCloseableIterable<AbstractOperation> operations() {
		return new FileBasedOperationIterable(getFilePath(), Direction.Forward);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#isEmpty()
	 */
	public boolean isEmpty() {
		BufferedReader reader = null;
		try {
			final File file = new File(getFilePath());
			if (!file.exists()) {
				return true;
			}
			// TODO: move reader into operationemitter
			reader = new BufferedReader(new FileReader(file));
			final OperationEmitter operationEmitter = new OperationEmitter(Direction.Forward);
			final ReadLineCapable create = ReadLineCapable.INSTANCE.create(reader);
			return !operationEmitter.tryEmit(create).isPresent();
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (final IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#removeAtEnd(int)
	 */
	public List<AbstractOperation> removeAtEnd(int n) {
		ReversedLinesFileReader reversedLinesFileReader = null;
		int counter = n;

		try {
			reversedLinesFileReader = new ReversedLinesFileReader(new File(getFilePath()));
			final OperationEmitter operationEmitter = new OperationEmitter(Direction.Backward);
			AbstractOperation operation;
			final List<AbstractOperation> ops = new ArrayList<AbstractOperation>();
			final ReadLineCapable reader = ReadLineCapable.INSTANCE.create(reversedLinesFileReader);
			final Optional<AbstractOperation> optionalOperation = operationEmitter.tryEmit(reader);
			while (counter > 0 && optionalOperation.isPresent()) {
				operation = optionalOperation.get();
				ops.add(operation);
				counter -= 1;
			}
			// TODO: reuse readlinecapable?
			final long offset = operationEmitter.getOffset();

			final RandomAccessFile raf = new RandomAccessFile(getFilePath(), "rw"); //$NON-NLS-1$
			final long skip = raf.length() + 1 - offset;
			raf.seek(skip);
			// TODO: duplicate code
			final byte[] bytes = (XmlTags.NEWLINE + XmlTags.CHANGE_PACKAGE_END).getBytes();
			raf.write(bytes);
			raf.setLength(skip + bytes.length);
			raf.close();

			return ops;

		} catch (final IOException ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reversedLinesFileReader);
		}

		return null;

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#clear()
	 */
	public void clear() {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(getFilePath(), "rw"); //$NON-NLS-1$
			raf.seek(0);
			final String emptyChangePackage =
				XmlTags.XML_HEADER + XmlTags.CHANGE_PACKAGE_START + XmlTags.CHANGE_PACKAGE_END;
			raf.write(emptyChangePackage.getBytes());
			raf.setLength(emptyChangePackage.length());
		} catch (final FileNotFoundException ex) {
			throw new RuntimeException(ex);
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (final IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#attachToProjectSpace(org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.HasChangePackage)
	 */
	public void attachToProjectSpace(HasChangePackage changePackageHolder) {
		// final URI operationsURI = ESClientURIUtil.createOperationsURI(getProjectSpace());
		// final URI normalizedOperationUri = ESWorkspaceProviderImpl.getInstance().getInternalWorkspace()
		// .getResourceSet().getURIConverter().normalize(operationsURI);
		final URI changePackageUri = changePackageHolder.getChangePackageUri();
		final String operationFileString = changePackageUri.toFileString();
		final File operationFile = new File(operationFileString);
		operationFile.delete();
		final File thisFile = new File(getFilePath());
		try {
			FileUtil.moveAndOverwrite(thisFile, operationFile);
			setFilePath(operationFileString);
			changePackageHolder.setChangePackage(this);
		} catch (final IOException ex) {
			// TODO: LCP
			ex.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#leafSize()
	 */
	public int leafSize() {
		final ESCloseableIterable<AbstractOperation> operationHandle = operations();
		try {
			return countLeafOperations(operationHandle.iterable());
		} finally {
			operationHandle.close();
		}
	}

	private static int getSize(CompositeOperation compositeOperation) {
		int ret = 0;
		final EList<AbstractOperation> subOperations = compositeOperation.getSubOperations();
		for (final AbstractOperation abstractOperation : subOperations) {
			if (abstractOperation instanceof CompositeOperation) {
				ret = ret + getSize((CompositeOperation) abstractOperation);
			} else {
				ret++;
			}
		}
		return ret;
	}

	private static int countLeafOperations(Iterable<AbstractOperation> operations) {
		int ret = 0;
		for (final AbstractOperation operation : operations) {
			if (operation instanceof CompositeOperation) {
				ret = ret + getSize((CompositeOperation) operation);
			} else {
				ret++;
			}
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#initialize(java.lang.String)
	 */
	public void initialize(String filePath) {
		setFilePath(filePath);
		needsInit = false;
		try {
			final FileWriter writer = new FileWriter(filePath);
			writer.write(XmlTags.XML_HEADER + XmlTags.CHANGE_PACKAGE_START);
			writer.write(XmlTags.CHANGE_PACKAGE_END);
			writer.close();
		} catch (final IOException ex) {
			// TODO LCP
			ex.printStackTrace();
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

} // FileBasedChangePackageImpl
