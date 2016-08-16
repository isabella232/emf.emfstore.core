/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.modelmutator.mutation;

import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.HAS_GROUP_FEATURE_MAP_ENTRY_TYPE;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.IS_NON_EMPTY_FEATURE_MAP;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.change.FeatureMapEntry;
import org.eclipse.emf.ecore.change.impl.FeatureMapEntryImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl.ContainmentUpdatingFeatureMapEntry;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl.SimpleFeatureMapEntry;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.ecore.util.FeatureMap.Entry.Internal;
import org.eclipse.emf.emfstore.modelmutator.ESFeatureMapValueMutation;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorUtil;
import org.eclipse.emf.emfstore.modelmutator.ESMutationException;

/**
 * A mutation, which changes the value of {@link org.eclipse.emf.ecore.change.FeatureMapEntry feature map entries}.
 *
 * @author emueller
 *
 */
public class FeatureMapValueMutation extends StructuralFeatureMutation<ESFeatureMapValueMutation>
	implements ESFeatureMapValueMutation {

	/**
	 * Creates a new mutation with the specified {@code util}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	public FeatureMapValueMutation(ESModelMutatorUtil util) {
		super(util);
		addTargetFeaturePredicate();
		addOriginalFeatureValuePredicate();
	}

	/**
	 * Creates a new mutation with the specified {@code util} and the {@code selector}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 * @param selector The target selector for selecting the target container and feature.
	 */
	public FeatureMapValueMutation(ESModelMutatorUtil util, MutationTargetSelector selector) {
		super(util, selector);
		addTargetFeaturePredicate();
		addOriginalFeatureValuePredicate();
	}

	private void addTargetFeaturePredicate() {
		getTargetContainerSelector().getTargetFeaturePredicates().add(HAS_GROUP_FEATURE_MAP_ENTRY_TYPE);
	}

	private void addOriginalFeatureValuePredicate() {
		getTargetContainerSelector().getOriginalFeatureValuePredicates().add(IS_NON_EMPTY_FEATURE_MAP);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation#clone()
	 */
	@Override
	public Mutation clone() {
		return new FeatureMapValueMutation(getUtil(), getTargetContainerSelector());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation#apply()
	 */
	@Override
	public void apply() throws ESMutationException {
		getTargetContainerSelector().doSelection();

		final List<FeatureMap.Entry> currentEntries = getFeatureMapEntries();
		final FeatureMap.Entry entry = getRandomFeatureMapEntryOfTarget(currentEntries);
		final EStructuralFeature currentFeatureKey = entry.getEStructuralFeature();

		if (isReference(currentFeatureKey) && !isContainmentReference(currentFeatureKey)) {

			final EObject currentValue = EObject.class.cast(entry.getValue());
			final EObject selectedEObject = selectEObject(currentValue.eClass());
			if (selectedEObject != null) {
				if (FeatureMapEntry.class.isInstance(entry)) {
					FeatureMapEntry.class.cast(entry).setReferenceValue(selectedEObject);
				} else if (SimpleFeatureMapEntry.class.isInstance(entry)) {
					final Internal createEntry = ((SimpleFeatureMapEntry) entry).createEntry(selectedEObject);
					if (!currentEntries.contains(createEntry)) {
						currentEntries.set(currentEntries.indexOf(entry), createEntry);
					}
				} else {
					// TODO: use logger
					// System.out.println(MessageFormat.format(
					// "Unhandled Feature Map Entry type {0}", entry.getClass()));
				}
			}
		} else if (isContainmentReference(currentFeatureKey)) {
			if (FeatureMapEntryImpl.class.isInstance(entry)) {
				final FeatureMapEntryImpl featureMapEntry = FeatureMapEntryImpl.class.cast(entry);
				featureMapEntry.setReferenceValue(
					createOfType(
						EReference.class.cast(featureMapEntry.getEStructuralFeature()).getEReferenceType()));
			}
		} else if (ContainmentUpdatingFeatureMapEntry.class.isInstance(entry)) {
			// TODO
			// final ContainmentUpdatingFeatureMapEntry featureMapEntry = ContainmentUpdatingFeatureMapEntry.class
			// .cast(entry);
			// System.out.println("FeatureMapEntry: " + featureMapEntry.getValue());
		} else if (EAttribute.class.isInstance(currentFeatureKey)) {
			// System.out.println("SET ATTRIBUTE");
		} else {
			// TODO
			// System.out.println(MessageFormat.format(
			// "Unhandled containment Feature Map Entry {0}", entry.getClass()));
		}
	}

	public EObject createOfType(EClass eClass) {
		final EObject eObjectToAdd = EcoreUtil.create(eClass);
		getUtil().setEObjectAttributes(eObjectToAdd);
		return eObjectToAdd;
	}

	private EObject selectEObject(EClass eClass) {
		final Map<EClass, List<EObject>> allObjects = ESModelMutatorUtil
			.getAllObjects(getUtil()
				.getModelMutatorConfiguration()
				.getRootEObject());
		final List<EObject> possibleObjects = allObjects.get(eClass);

		EObject selectedEObject = null;
		if (possibleObjects != null && !possibleObjects.isEmpty()) {
			while (!possibleObjects.isEmpty() && selectedEObject == null) {
				selectedEObject = possibleObjects.get(getRandom().nextInt(possibleObjects.size()));
				if (!eObjectFits(selectedEObject)) {
					possibleObjects.remove(selectedEObject);
					selectedEObject = null;
				}
			}
		}

		return selectedEObject;
	}

	private boolean eObjectFits(EObject eObject) {
		final EClass eClass = eObject.eClass();
		final EList<EStructuralFeature> eAllStructuralFeatures = eClass.getEAllStructuralFeatures();
		for (final EStructuralFeature feature : eAllStructuralFeatures) {
			if (feature.getEType().equals(EcorePackage.eINSTANCE.getEFeatureMap())) {
				return false;
			}
		}

		return true;
	}

	private static boolean isReference(EStructuralFeature feature) {
		return EReference.class.isInstance(feature);
	}

	private static boolean isContainmentReference(EStructuralFeature feature) {
		return EReference.class.isInstance(feature)
			&& EReference.class.cast(feature).isContainment();
	}

	@SuppressWarnings("unchecked")
	private List<Entry> getFeatureMapEntries() {
		return (List<FeatureMap.Entry>) getTargetObject().eGet(getTargetFeature());
	}

	private FeatureMap.Entry getRandomFeatureMapEntryOfTarget(List<Entry> currentEntries) {
		final int pickIndex = getRandom().nextInt(currentEntries.size());
		return currentEntries.get(pickIndex);
	}
}
