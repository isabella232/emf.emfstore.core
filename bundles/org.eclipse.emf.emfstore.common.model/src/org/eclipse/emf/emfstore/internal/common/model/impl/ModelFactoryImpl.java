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
package org.eclipse.emf.emfstore.internal.common.model.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.emfstore.internal.common.model.EMFStoreProperty;
import org.eclipse.emf.emfstore.internal.common.model.EMFStorePropertyType;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.common.model.ModelPackage;
import org.eclipse.emf.emfstore.internal.common.model.ModelVersion;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.PropertyStringValue;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!--
 * end-user-doc -->
 *
 * @generated
 */
public class ModelFactoryImpl extends EFactoryImpl implements ModelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	public static ModelFactory init() {
		try {
			final ModelFactory theModelFactory = (ModelFactory) EPackage.Registry.INSTANCE
				.getEFactory("http://eclipse.org/emf/emfstore/common/model"); //$NON-NLS-1$
			if (theModelFactory != null) {
				return theModelFactory;
			}
		} catch (final Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ModelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	public ModelFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case ModelPackage.PROJECT:
			return createProject();
		case ModelPackage.MODEL_ELEMENT_ID:
			return createModelElementId();
		case ModelPackage.MODEL_VERSION:
			return createModelVersion();
		case ModelPackage.EMF_STORE_PROPERTY:
			return createEMFStoreProperty();
		case ModelPackage.PROPERTY_MAP_ENTRY:
			return (EObject) createPropertyMapEntry();
		case ModelPackage.PROPERTY_STRING_VALUE:
			return createPropertyStringValue();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
		case ModelPackage.EMF_STORE_PROPERTY_TYPE:
			return createEMFStorePropertyTypeFromString(eDataType, initialValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case ModelPackage.EMF_STORE_PROPERTY_TYPE:
			return convertEMFStorePropertyTypeToString(eDataType, instanceValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Project createProject() {
		final ProjectImpl project = new ProjectImpl();
		return project;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ModelElementId createModelElementId() {
		final ModelElementIdImpl modelElementId = new ModelElementIdImpl();
		return modelElementId;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ModelVersion createModelVersion() {
		final ModelVersionImpl modelVersion = new ModelVersionImpl();
		return modelVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EMFStoreProperty createEMFStoreProperty() {
		final EMFStorePropertyImpl emfStoreProperty = new EMFStorePropertyImpl();
		return emfStoreProperty;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Map.Entry<String, EMFStoreProperty> createPropertyMapEntry() {
		final PropertyMapEntryImpl propertyMapEntry = new PropertyMapEntryImpl();
		return propertyMapEntry;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public PropertyStringValue createPropertyStringValue() {
		final PropertyStringValueImpl propertyStringValue = new PropertyStringValueImpl();
		return propertyStringValue;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EMFStorePropertyType createEMFStorePropertyTypeFromString(EDataType eDataType, String initialValue) {
		final EMFStorePropertyType result = EMFStorePropertyType.get(initialValue);
		if (result == null)
		{
			throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" //$NON-NLS-1$ //$NON-NLS-2$
				+ eDataType.getName() + "'"); //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertEMFStorePropertyTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ModelPackage getModelPackage() {
		return (ModelPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static ModelPackage getPackage() {
		return ModelPackage.eINSTANCE;
	}

} // ModelFactoryImpl