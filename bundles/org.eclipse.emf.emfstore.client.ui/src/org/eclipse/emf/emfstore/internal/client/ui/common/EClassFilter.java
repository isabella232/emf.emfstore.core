/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.common;

import static org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util.OperationUtil.isComposite;
import static org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util.OperationUtil.isCreateDelete;
import static org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util.OperationUtil.isFeature;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.client.ui.ESClassFilter;
import org.eclipse.emf.emfstore.client.ui.ESWhitelistFilter;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CreateDeleteOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.FeatureOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.UnkownFeatureException;

/**
 * Utility class for determing filtered types and operations that
 * involve only such types.
 *
 * @author emueller
 *
 */
public final class EClassFilter {

	private static final String POINT_ID = "org.eclipse.emf.emfstore.client.ui.filteredTypes"; //$NON-NLS-1$
	private static final String FILTERED_TYPES_ATTRIBUTE = "filteredTypes"; //$NON-NLS-1$

	/**
	 * Singleton.
	 */
	public static final EClassFilter INSTANCE = new EClassFilter();

	private final Set<EClass> filteredEClasses;
	private final Map<EClass, Collection<EStructuralFeature>> whitelist;
	private String filterLabel;

	/**
	 * Creates a new {@link EClassFilter} instance.
	 */
	/* package */ EClassFilter() {
		filteredEClasses = new LinkedHashSet<EClass>();
		whitelist = new LinkedHashMap<EClass, Collection<EStructuralFeature>>();
		initFilteredEClasses();
	}

	private void initFilteredEClasses() {

		final ESExtensionPoint extensionPoint = new ESExtensionPoint(POINT_ID);

		if (extensionPoint.size() == 0) {
			return;
		}

		for (final ESExtensionElement element : extensionPoint.getExtensionElements()) {
			final ESClassFilter filter = element.getClass(FILTERED_TYPES_ATTRIBUTE, ESClassFilter.class);

			registerFilter(filter);

			if (filterLabel == null) {
				filterLabel = filter.getLabel();
			}
		}
	}

	/**
	 * @param filter the {@link ESClassFilter} to register
	 */
	void registerFilter(final ESClassFilter filter) {
		if (filter != null) {
			filteredEClasses.addAll(filter.getFilteredEClasses());
		}

		if (ESWhitelistFilter.class.isInstance(filter)) {
			for (final Entry<EClass, Collection<EStructuralFeature>> entry : ESWhitelistFilter.class.cast(filter)
				.getNonFilteredFeaturesForEClass().entrySet()) {
				if (!whitelist.containsKey(entry.getKey())) {
					whitelist.put(entry.getKey(), new LinkedHashSet<EStructuralFeature>());
				}
				whitelist.get(entry.getKey()).addAll(entry.getValue());
			}
		}
	}

	/**
	 * Whether any {@link EClass} has been marked as filtered at all.
	 *
	 * @return true, if at least one {@link EClass} should be filtered
	 */
	public boolean isEnabled() {
		return filteredEClasses.size() > 0;
	}

	/**
	 * Whether the given {@link EClass} is considered as filtered.
	 *
	 * @param eClass
	 *            the class to check
	 * @return true, if the given {@link EClass} is considered as filtered
	 */
	public boolean isFilteredEClass(EClass eClass) {
		return filteredEClasses.contains(eClass);
	}

	/**
	 * Whether the given {@link EClass} is considered filtered taking the changed feature into account.
	 *
	 * @param eClass the EClass to check.
	 * @param feature the changed feature
	 * @return <code>true</code> of the EClass is considered as filtered, <code>false</code> otherwise
	 */
	public boolean isFiltered(EClass eClass, EStructuralFeature feature) {
		if (!isFilteredEClass(eClass)) {
			return false;
		}
		if (!whitelist.containsKey(eClass)) {
			return true;
		}
		return !whitelist.get(eClass).contains(feature);

	}

	/**
	 * Whether the given operation only involves types that are considered to be filtered.
	 *
	 * @param idToEObjectMapping
	 *            a mapping that is used to resolve the {@link EObject}s
	 *            contained in the operation
	 * @param operation
	 *            the operations to check
	 * @return true, if the operation only involves types that are considered to be filtered
	 */
	public boolean involvesOnlyFilteredEClasses(ModelElementIdToEObjectMapping idToEObjectMapping,
		AbstractOperation operation) {

		if (isComposite(operation)) {
			return compositeOperationInvolvesOnlyFilteredEClasses(idToEObjectMapping, operation);
		}

		if (isCreateDelete(operation)) {
			return createDeleteOperationInvolvesOnlyFilteredEClasses(operation);
		}

		final ModelElementId id = operation.getModelElementId();
		final EObject modelElement = idToEObjectMapping.get(id);

		if (modelElement == null) {
			return false;
		}

		if (!isFilteredEClass(modelElement.eClass())) {
			return false;
		}

		if (isFeature(operation)) {
			try {
				final EStructuralFeature feature = FeatureOperation.class.cast(operation).getFeature(modelElement);
				return isFiltered(modelElement.eClass(), feature);
			} catch (final UnkownFeatureException ex) {
				ModelUtil.log(
					MessageFormat.format("{0} could not access feature of a feature operation", getClass().getName()), //$NON-NLS-1$
					ex, IStatus.WARNING);
				return true;
			}
		}

		return true;
	}

	private boolean createDeleteOperationInvolvesOnlyFilteredEClasses(AbstractOperation operation) {
		final CreateDeleteOperation createDeleteOperation = (CreateDeleteOperation) operation;
		for (final EObject modelElement : createDeleteOperation.getEObjectToIdMap().keySet()) {
			if (modelElement != null && !isFilteredEClass(modelElement.eClass())) {
				return false;
			} else if (modelElement == null) {
				return false;
			}
		}

		return true;
	}

	private boolean compositeOperationInvolvesOnlyFilteredEClasses(ModelElementIdToEObjectMapping idToEObjectMapping,
		AbstractOperation operation) {
		final CompositeOperation composite = (CompositeOperation) operation;
		for (final AbstractOperation op : composite.getSubOperations()) {
			if (!involvesOnlyFilteredEClasses(idToEObjectMapping, op)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns the label that is used to group filtered types or operations that involve
	 * only such types.
	 *
	 * @return the label used for grouping filtered types
	 */
	public String getFilterLabel() {
		return filterLabel;
	}
}
