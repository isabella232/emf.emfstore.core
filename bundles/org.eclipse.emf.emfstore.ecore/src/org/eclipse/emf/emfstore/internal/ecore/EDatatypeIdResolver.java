/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.ecore;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.emfstore.common.model.ESModelElementId;
import org.eclipse.emf.emfstore.common.model.ESSingletonIdResolver;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelFactory;

/**
 * An implementation of a {@link ESSingletonIdResolver} that treats all {@link EDataType}s as singletons.
 * 
 * @author emueller
 * 
 */
public class EDatatypeIdResolver implements ESSingletonIdResolver {

	private final Map<String, EDataType> datatypes = new LinkedHashMap<String, EDataType>();

	/**
	 * Default constructor.
	 */
	public EDatatypeIdResolver() {
		datatypes.put("Literal", EcorePackage.eINSTANCE.getEString()); //$NON-NLS-1$
		// String
		datatypes.put("String", EcorePackage.eINSTANCE.getEString()); //$NON-NLS-1$
		datatypes.put("EString", EcorePackage.eINSTANCE.getEString()); //$NON-NLS-1$
		// Date
		datatypes.put("Date", EcorePackage.eINSTANCE.getEDate()); //$NON-NLS-1$
		datatypes.put("EDate", EcorePackage.eINSTANCE.getEDate()); //$NON-NLS-1$
		// integer
		datatypes.put("Int", EcorePackage.eINSTANCE.getEInt()); //$NON-NLS-1$
		datatypes.put("EInt", EcorePackage.eINSTANCE.getEInt()); //$NON-NLS-1$
		datatypes.put("Integer", EcorePackage.eINSTANCE.getEIntegerObject()); //$NON-NLS-1$
		datatypes.put("EInteger", EcorePackage.eINSTANCE.getEIntegerObject()); //$NON-NLS-1$
		datatypes.put("EIntegerObject", EcorePackage.eINSTANCE.getEIntegerObject()); //$NON-NLS-1$
		// double
		datatypes.put("Double", EcorePackage.eINSTANCE.getEDouble()); //$NON-NLS-1$
		datatypes.put("EDouble", EcorePackage.eINSTANCE.getEDouble()); //$NON-NLS-1$
		datatypes.put("EDoubleObject", EcorePackage.eINSTANCE.getEDoubleObject()); //$NON-NLS-1$
		// long
		datatypes.put("Long", EcorePackage.eINSTANCE.getELong()); //$NON-NLS-1$
		datatypes.put("ELong", EcorePackage.eINSTANCE.getELong()); //$NON-NLS-1$
		datatypes.put("ELongObject", EcorePackage.eINSTANCE.getELongObject()); //$NON-NLS-1$
		// float
		datatypes.put("Float", EcorePackage.eINSTANCE.getEFloat()); //$NON-NLS-1$
		datatypes.put("EFloat", EcorePackage.eINSTANCE.getEFloat()); //$NON-NLS-1$
		datatypes.put("EFloatObject", EcorePackage.eINSTANCE.getEFloatObject()); //$NON-NLS-1$
		// short
		datatypes.put("Short", EcorePackage.eINSTANCE.getEShort()); //$NON-NLS-1$
		datatypes.put("EShort", EcorePackage.eINSTANCE.getEShort()); //$NON-NLS-1$
		datatypes.put("EShortObject", EcorePackage.eINSTANCE.getEShortObject()); //$NON-NLS-1$
		// boolean
		datatypes.put("Boolean", EcorePackage.eINSTANCE.getEBoolean()); //$NON-NLS-1$
		datatypes.put("EBoolean", EcorePackage.eINSTANCE.getEBoolean()); //$NON-NLS-1$
		datatypes.put("EBooleanObject", EcorePackage.eINSTANCE.getEBooleanObject()); //$NON-NLS-1$
		// byte
		datatypes.put("Byte", EcorePackage.eINSTANCE.getEByte()); //$NON-NLS-1$
		datatypes.put("EByte", EcorePackage.eINSTANCE.getEByte()); //$NON-NLS-1$
		datatypes.put("EByteObject", EcorePackage.eINSTANCE.getEByteObject()); //$NON-NLS-1$
		datatypes.put("EByteArray", EcorePackage.eINSTANCE.getEByteArray()); //$NON-NLS-1$
		// char
		datatypes.put("EChar", EcorePackage.eINSTANCE.getEChar()); //$NON-NLS-1$
		datatypes.put("ECharacterObject", EcorePackage.eINSTANCE.getECharacterObject()); //$NON-NLS-1$
		datatypes.put("EBigDecimal", EcorePackage.eINSTANCE.getEBigDecimal()); //$NON-NLS-1$
		datatypes.put("EBigInteger", EcorePackage.eINSTANCE.getEBigInteger()); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public EObject getSingleton(ESModelElementId singletonId) {

		if (singletonId == null) {
			return null;
		}

		return datatypes.get(singletonId.getId());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link ESModelElementId} of the the singleton object or <code>null</code> if the given
	 *         {@link EObject} is not a singleton, is not an instance of {@link EDataType} or is <code>null</code>
	 */
	public ESModelElementId getSingletonModelElementId(EObject singleton) {

		if (!EDataType.class.isInstance(singleton)) {
			return null;
		}

		// TODO: EM, provide 2nd map for performance reasons
		for (final Map.Entry<String, EDataType> entry : datatypes.entrySet()) {
			if (entry.getValue() != singleton) {
				continue;
			}

			// TODO: don't create IDs on the fly rather put them directly into the map
			final ModelElementId id = ModelFactory.eINSTANCE.createModelElementId();
			id.setId(entry.getKey());
			return id.toAPI();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSingleton(EObject eDataType) {

		if (!EDataType.class.isInstance(eDataType)) {
			return false;
		}

		return datatypes.containsValue(eDataType);
	}
}
