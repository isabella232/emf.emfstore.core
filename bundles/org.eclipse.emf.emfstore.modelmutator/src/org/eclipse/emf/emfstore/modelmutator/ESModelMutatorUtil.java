/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Julian Sommerfeldt, Stephan Koehler, Eugen Neufeld,
 * Philip Achenbach, Dmitry Litvinov - initial API and implementation
 * Philip Langer - Allow configuration of the numbers/types of changes being
 * applied by model mutator
 ******************************************************************************/
package org.eclipse.emf.emfstore.modelmutator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.change.FeatureMapEntry;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AbstractOverrideableCommand;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.emfstore.internal.modelmutator.api.Messages;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetter;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEBigDecimal;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEBigInteger;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEBoolean;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEByte;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEByteArray;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEChar;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEDate;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEDouble;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEEnum;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEFeatureMapEntry;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEFloat;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEInt;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterELong;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEShort;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetterEString;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Util class for the ModelMutator.
 *
 * @author Eugen Neufeld
 * @author Stephan Koehler
 * @author Philip Achenbach
 * @author Dmitry Litvinov
 * @author Julian Sommerfeldt
 * @since 2.0
 *
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class ESModelMutatorUtil {

	/**
	 * Indicates deleting with the {@link DeleteCommand}.
	 */
	public static final int DELETE_DELETE_COMMAND = 0;

	/**
	 * Indicates deleting through removing containment references.
	 */
	public static final int DELETE_CUT_CONTAINMENT = 1;

	/**
	 * Indicates deleting with the {@link EcoreUtil#delete(EObject)} method.
	 */
	public static final int DELETE_ECORE = 2;

	private final Set<Object> registeredIDs = new LinkedHashSet<Object>();

	private Map<EClassifier, AttributeSetter<?>> attributeSetters;

	private final Map<EObject, List<EReference>> validContainmentReferences = new LinkedHashMap<EObject, List<EReference>>();

	private final Map<EObject, List<EReference>> validCrossReferencesByEObject = new LinkedHashMap<EObject, List<EReference>>();

	private final Map<EReference, List<EClass>> allContainments = new LinkedHashMap<EReference, List<EClass>>();

	private final Map<EClass, List<EClass>> allSubClasses = new LinkedHashMap<EClass, List<EClass>>();

	private final Map<EPackage, List<EClass>> allClassesInPackage = new LinkedHashMap<EPackage, List<EClass>>();

	private final ESModelMutatorConfiguration config;

	private final Map<EStructuralFeature, List<EObject>> featureToOfferingObjects = new LinkedHashMap<EStructuralFeature, List<EObject>>();

	private final Map<EStructuralFeature, List<EObject>> featureToSuitableObjects = new LinkedHashMap<EStructuralFeature, List<EObject>>();

	private List<EClass> allEClasses;

	/**
	 * A new {@link ESModelMutatorUtil}.
	 *
	 * @param config The {@link ESModelMutatorConfiguration} of the {@link ESModelMutatorUtil}.
	 */
	public ESModelMutatorUtil(ESModelMutatorConfiguration config) {
		this.config = config;
	}

	/**
	 * Returns the EPackage to the specified <code>nsURI</code>.
	 *
	 * @param nsURI
	 *            the NsUri of the EPackage to get
	 * @return the EPackage belonging to <code>nsURI</code>
	 * @see Registry#getEPackage(String)
	 */
	public static EPackage getEPackage(String nsURI) {
		return EPackage.Registry.INSTANCE.getEPackage(nsURI);
	}

	/**
	 * Returns all valid containment references for an EObject. A reference is valid if it is neither
	 * derived nor volatile and if it is changeable and either many-valued or
	 * not already set.
	 *
	 * @param eObject
	 *            the EObject to get references for
	 *
	 * @return all valid references as a list
	 */
	public List<EReference> getValidContainmentReferences(EObject eObject) {
		List<EReference> list = validContainmentReferences.get(eObject);
		if (list == null) {
			list = new ArrayList<EReference>();
			for (final EReference reference : eObject.eClass().getEAllReferences()) {
				if (reference.isContainment() && isValid(reference, eObject)) {
					list.add(reference);
				}
			}
			validContainmentReferences.put(eObject, list);
		}
		return list;
	}

	/**
	 * Returns all valid references for an EObject. This excludes
	 * container/containment references. A reference is valid if it is neither
	 * derived nor volatile and if it is changeable and either many-valued or
	 * not already set.
	 *
	 * @param eObject
	 *            the EObject to get references for
	 * @return all valid references as a list
	 */
	public List<EReference> getValidCrossReferences(EObject eObject) {
		List<EReference> crossReferences = validCrossReferencesByEObject.get(eObject);
		if (crossReferences == null) {
			crossReferences = new ArrayList<EReference>();
			for (final EReference reference : eObject.eClass().getEAllReferences()) {
				if (!reference.isContainer() && !reference.isContainment() && isValid(reference, eObject)) {
					crossReferences.add(reference);
				}
			}
			validCrossReferencesByEObject.put(eObject, crossReferences);
		}
		return crossReferences;
	}

	/**
	 * Returns whether an EStructuralFeature is valid for an EObject or not. A
	 * reference is valid, if it can be set or added to.
	 *
	 * @param feature
	 *            the EStructuralFeature in question
	 * @param eObject
	 *            the EObject to check the feature for
	 * @return if the feature can be set or added to
	 */
	public boolean isValid(EStructuralFeature feature, EObject eObject) {
		boolean result = false;
		try {
			if (feature.isMany()) {
				// has the maximum amount of referenced objects been reached?
				final Collection<?> referencedItems = (Collection<?>) eObject.eGet(feature);
				if (feature.getUpperBound() >= 0 && referencedItems.size() >= feature.getUpperBound()) {
					return false;
				}
			}
			// can the feature be changed reflectively?
			result = feature.isChangeable() && !feature.isVolatile() && !feature.isDerived();
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final RuntimeException e) {
			// END SUPRESS CATCH EXCEPTION
			handle(e, config);
		}
		return result;
	}

	/**
	 * Handles <code>exception</code>, meaning it is thrown if <code>ignoreAndLog</code> is <code>false</code>.
	 * Otherwise <code>exception</code> is ignored and added to <code>exceptionLog</code>.
	 *
	 * @param exception the exception to handle
	 * @param exceptionLog the current log of exceptions
	 * @param ignoreAndLog should exceptions be ignored and added to <code>exceptionLog</code>?
	 */
	private static void handle(RuntimeException exception, ESModelMutatorConfiguration config) {
		if (config.isIgnoreAndLog()) {
			config.getExceptionLog().add(exception);
		} else {
			throw exception;
		}
	}

	/**
	 * Get all containments of a reference.
	 *
	 * @param reference The {@link EReference} for which to get all containments.
	 * @return All containments of the {@link EReference}.
	 */
	public List<EClass> getAllEContainments(EReference reference) {
		List<EClass> list = allContainments.get(reference);
		if (list == null) {
			list = new ArrayList<EClass>();

			final EClass referenceType = reference.getEReferenceType();
			if (EcorePackage.eINSTANCE.getEObject().equals(referenceType)) {
				for (final EPackage ePackage : config.getModelPackages()) {
					list.addAll(getAllEClasses(ePackage));
				}
			}

			if (canHaveInstance(referenceType)) {
				list.add(referenceType);
			}

			list.addAll(getAllSubEClasses(referenceType));

			allContainments.put(reference, list);
		}
		return list;
	}

	/**
	 * Returns whether <code>eClass</code> can be instantiated or not. An EClass
	 * can be instantiated, if it is neither an interface nor abstract.
	 *
	 * @param eClass
	 *            the EClass in question
	 * @return whether <code>eClass</code> can be instantiated or not.
	 */
	public static boolean canHaveInstance(EClass eClass) {
		return !eClass.isInterface() && !eClass.isAbstract();
	}

	/**
	 * Returns all subclasses of an EClass, excluding abstract classes and
	 * interfaces.
	 *
	 * @param eClass
	 *            the EClass to get subclasses for
	 * @return all subclasses of <code>eClass</code>
	 */
	public List<EClass> getAllSubEClasses(EClass eClass) {
		List<EClass> list = allSubClasses.get(eClass);
		if (list == null) {
			list = new ArrayList<EClass>();
			final List<EClass> allEClasses = getAllEClasses(config.getModelPackages());
			for (final EClass possibleSubClass : allEClasses) {
				// is the EClass really a subClass, while not being abstract or an interface?
				if (eClass.isSuperTypeOf(possibleSubClass) && canHaveInstance(possibleSubClass)) {
					list.add(possibleSubClass);
				}
			}

			allSubClasses.put(eClass, list);
		}

		return list;
	}

	/**
	 * Iterates over all registered EPackages in order to retrieve all available
	 * EClasses, excluding abstract classes and interfaces, and returns them as
	 * a Set.
	 *
	 * @return a set of all EClasses that are contained in registered EPackages
	 * @see Registry
	 */
	public List<EClass> getAllEClasses() {
		if (allEClasses == null) {
			allEClasses = new ArrayList<EClass>();
			final Registry registry = EPackage.Registry.INSTANCE;
			// for all registered EPackages
			for (final Entry<String, Object> entry : new LinkedHashSet<Entry<String, Object>>(registry.entrySet())) {
				final EPackage ePackage = registry.getEPackage(entry.getKey());
				for (final EClass eClass : getAllEClasses(ePackage)) {
					// no abstracts or interfaces
					if (canHaveInstance(eClass)) {
						allEClasses.add(eClass);
					}
				}
			}
		}
		return allEClasses;
	}

	/**
	 * Get all {@link EClass}es in the {@link EPackage}s.
	 *
	 * @param ePackages The {@link EPackage}s containg the {@link EClass}es.
	 * @return The {@link EClass}es contained in the {@link EPackage}s.
	 */
	public List<EClass> getAllEClasses(Collection<EPackage> ePackages) {
		final List<EClass> eClasses = new ArrayList<EClass>();
		for (final EPackage ePackage : ePackages) {
			eClasses.addAll(getAllEClasses(ePackage));
		}
		return eClasses;
	}

	/**
	 * Retrieve all EClasses that are contained in <code>ePackage</code>.
	 *
	 * @param ePackage
	 *            the package to get contained EClasses from
	 * @return a set of EClasses contained in <code>ePackage</code>
	 */
	public List<EClass> getAllEClasses(EPackage ePackage) {
		List<EClass> list = allClassesInPackage.get(ePackage);
		if (list == null) {
			list = new ArrayList<EClass>();
			// obtain all EClasses from sub packages
			for (final EPackage subPackage : ePackage.getESubpackages()) {
				list.addAll(getAllEClasses(subPackage));
			}
			// obtain all EClasses that are direct contents of the EPackage
			for (final EClassifier classifier : ePackage.getEClassifiers()) {
				if (classifier instanceof EClass) {
					list.add((EClass) classifier);
				}
			}
			allClassesInPackage.put(ePackage, list);
		}

		return list;
	}

	/**
	 * Returns all direct and indirect contents of <code>rootObject</code> as a
	 * map. All EObjects that appear in these contents are mapped to their
	 * corresponding EClass.<br>
	 *
	 * NOTE: this is a very expensive method!
	 *
	 *
	 * @param rootObject
	 *            the EObject to get contents for
	 * @return all contents as a map from EClass to lists of EObjects
	 */
	public static Map<EClass, List<EObject>> getAllClassesAndObjects(EObject rootObject) {
		// initialize the computation process
		final Map<EClass, List<EObject>> result = new LinkedHashMap<EClass, List<EObject>>();
		final TreeIterator<EObject> allContents = rootObject.eAllContents();
		List<EObject> newList = new ArrayList<EObject>();
		newList.add(rootObject);
		result.put(rootObject.eClass(), newList);
		// iterate over all direct and indirect contents
		while (allContents.hasNext()) {
			final EObject eObject = allContents.next();
			// did this EObject's EClass appear before?
			if (result.containsKey(eObject.eClass())) {
				result.get(eObject.eClass()).add(eObject);
			} else {
				newList = new ArrayList<EObject>();
				newList.add(eObject);
				result.put(eObject.eClass(), newList);
			}
		}
		return result;
	}

	/**
	 * Adds <code>newValue</code> to the many-valued feature of <code>eObject</code> using an AddCommand. Exceptions are
	 * caught if <code>ignoreAndLog</code> is true, otherwise a RuntimeException might be
	 * thrown if the command fails.
	 *
	 * @param eObject the EObject to which <code>newObject</code> shall be added
	 * @param feature the EStructuralFeature that <code>newObject</code> shall be added to
	 * @param newValue the Object that shall be added to <code>feature</code>
	 * @param index the index where to add the object or null if it should be added to the end.
	 * @see AddCommand#AddCommand(EditingDomain, EObject, EStructuralFeature, Object)
	 */
	public void addPerCommand(EObject eObject, EStructuralFeature feature, Object newValue, Integer index) {
		try {
			if (feature.isUnique() && ((Collection<?>) eObject.eGet(feature)).contains(newValue)) {
				// unique feature already contains object -> nothing to do
				return;
			}
			final EditingDomain domain = config.getEditingDomain();
			if (index == null) {
				domain.getCommandStack().execute(new AddCommand(domain, eObject, feature, newValue));
			} else {
				domain.getCommandStack().execute(new AddCommand(domain, eObject, feature, newValue, index));
			}

			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final RuntimeException e) {
			// END SUPRESS CATCH EXCEPTION
			handle(e, config);
		}
	}

	/**
	 * Adds all <code>objects</code> to the many-valued feature of <code>eObject</code> using an AddCommand. Exceptions
	 * are caught if <code>ignoreAndLog</code> is true, otherwise a RuntimeException might be
	 * thrown if the command fails.
	 *
	 * @param eObject the EObject to which <code>objects</code> shall be added
	 * @param feature the EReference that <code>objects</code> shall be added to
	 * @param objects collection of objects that shall be added to <code>feature</code>
	 */
	public void addPerCommand(EObject eObject, EStructuralFeature feature, Collection<?> objects) {
		try {
			for (final Object object : objects) {
				if (feature.isUnique() && ((Collection<?>) eObject.eGet(feature)).contains(object)) {
					// object already exists in unique feature, don't add it again
					objects.remove(object);
				}
			}
			// no objects to add left
			if (objects.isEmpty()) {
				return;
			}
			final EditingDomain domain = config.getEditingDomain();
			domain.getCommandStack().execute(new AddCommand(domain, eObject, feature, objects));
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final RuntimeException e) {
			// END SUPRESS CATCH EXCEPTION
			handle(e, config);
		}
	}

	/**
	 * Move an object.
	 *
	 * @param parent The parent object.
	 * @param feature The feature of the parent object.
	 * @param objectToMove The object to move within the parents feature.
	 * @param index The index where to move the object to.
	 */
	public void movePerCommand(EObject parent, EStructuralFeature feature, Object objectToMove, Integer index) {
		try {

			final Collection<?> containments = (Collection<?>) parent.eGet(feature);
			if (!containments.contains(objectToMove)) {
				return;
			}
			final EditingDomain domain = config.getEditingDomain();
			domain.getCommandStack().execute(new MoveCommand(domain, parent, feature, objectToMove, index));
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final RuntimeException e) {
			// END SUPRESS CATCH EXCEPTION
			handle(e, config);
		}
	}

	/**
	 * Sets a feature between <code>eObject</code> and <code>newValue</code> using a SetCommand. Exceptions are caught
	 * if <code>ignoreAndLog</code> is
	 * true, otherwise a RuntimeException might be thrown if the command fails.
	 *
	 * @param eObject the EObject for which <code>feature</code> shall be set
	 * @param feature the EStructuralFeature that shall be set
	 * @param newValue the Object that shall be set as a feature in <code>parentEObject</code>
	 * @param index Where to set to object to or null if it should be set to the end.
	 * @return <code>newValue</code> if the <code>SetCommand</code> was performed successful or <code>null</code> if it
	 *         failed
	 * @see SetCommand
	 */
	public EObject setPerCommand(EObject eObject, EStructuralFeature feature, Object newValue, Integer index) {
		// no new value to set? -> unset the feature or set null
		if (newValue == null && config.getRandom().nextBoolean()) {
			newValue = SetCommand.UNSET_VALUE;
		}
		try {
			final EditingDomain domain = config.getEditingDomain();
			if (index != null) {
				if (feature.isUnique() && ((Collection<?>) eObject.eGet(feature)).contains(newValue)) {
					return null;
				}
				domain.getCommandStack().execute(new SetCommand(domain, eObject, feature, newValue, index));
			} else {
				domain.getCommandStack().execute(new SetCommand(domain, eObject, feature, newValue));
			}
			if (newValue instanceof EObject) {
				return (EObject) newValue;
			}

			return null;
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final RuntimeException e) {
			// END SUPRESS CATCH EXCEPTION
			handle(e, config);
			return null;
		}
	}

	/**
	 * Sets a feature between <code>eObject</code> and <code>newValue</code> using a SetCommand. Exceptions are caught
	 * if <code>ignoreAndLog</code> is
	 * true, otherwise a RuntimeException might be thrown if the command fails.
	 *
	 * @param eObject the EObject for which <code>feature</code> shall be set
	 * @param feature the EStructuralFeature that shall be set
	 * @param newValue the Object that shall be set as a feature in <code>parentEObject</code>
	 * @return <code>newValue</code> if the <code>SetCommand</code> was
	 *         performed successful or <code>null</code> if it failed
	 * @see SetCommand
	 */
	public EObject setPerCommand(EObject eObject, EStructuralFeature feature, Object newValue) {
		return setPerCommand(eObject, feature, newValue, null);
	}

	/**
	 * Removes <code>objects</code> from a feature of <code>eObject</code> using
	 * a RemoveCommand. Exceptions are caught if <code>ignoreAndLog</code> is
	 * true, otherwise a RuntimeException might be thrown if the command fails.
	 *
	 * @param eObject
	 *            the EObject to remove <code>objects</code> from
	 * @param feature
	 *            the EStructuralFeature <code>objects</code> shall be removed
	 *            from
	 * @param objects
	 *            collection of Objects that shall be removed
	 * @see RemoveCommand
	 */
	public void removePerCommand(EObject eObject, EStructuralFeature feature, Collection<?> objects) {
		try {
			final EditingDomain domain = config.getEditingDomain();
			domain.getCommandStack().execute(new RemoveCommand(domain, eObject, feature, objects));
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final RuntimeException e) {
			// END SUPRESS CATCH EXCEPTION
			handle(e, config);
		}
	}

	/**
	 * Deletes the {@link EObject} using the specified <code>howToDelete</code>.
	 *
	 * @param eObject The {@link EObject} to delete.
	 * @param howToDelete The way to delete: {@link #DELETE_ECORE}, {@link #DELETE_DELETE_COMMAND} or
	 *            {@link #DELETE_CUT_CONTAINMENT}.
	 */
	@SuppressWarnings("unchecked")
	public void removeFullPerCommand(final EObject eObject, int howToDelete) {
		try {
			final EditingDomain domain = config.getEditingDomain();
			// delete with DeleteCommand
			if (DELETE_DELETE_COMMAND == howToDelete) {
				domain.getCommandStack().execute(new DeleteCommand(domain, Collections.singleton(eObject)));

				// delete through cutting containment (RemoveCommand)
			} else if (DELETE_CUT_CONTAINMENT == howToDelete) {
				final EStructuralFeature feature = eObject.eContainingFeature();
				if (feature == null) {
					EcoreUtil.delete(eObject, true);
					return;
				}

				final EObject eContainer = eObject.eContainer();
				if (feature.isMany()) {
					((EList<Object>) eContainer.eGet(feature)).remove(eObject);
				} else {
					eContainer.eSet(feature, null);
				}

				// delete with EcoreUtil
			} else if (DELETE_ECORE == howToDelete) {
				EcoreUtil.delete(eObject, false);

				// no valid delete mode
			} else {
				throw new IllegalArgumentException(
					MessageFormat.format(
						Messages.getString("ModelMutatorUtil.InvalidDeleteMode"), howToDelete)); //$NON-NLS-1$
			}
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final RuntimeException e) {
			// END SUPRESS CATCH EXCEPTION
			handle(e, config);
		}
	}

	/**
	 * Sets all possible attributes of known types to random values using {@link AttributeSetter} and
	 * SetCommands/AddCommands.
	 *
	 * @param eObject
	 *            the EObject to set attributes for
	 * @see AttributeSetter
	 */
	public void setEObjectAttributes(EObject eObject) {
		setEObjectAttributes(eObject, Integer.MAX_VALUE, Collections.<EStructuralFeature> emptySet());
	}

	/**
	 * Sets all possible attributes of known types to random values using {@link AttributeSetter} and
	 * SetCommands/AddCommands.
	 *
	 * @param eObject
	 *            the EObject to set attributes for
	 * @param maxNumber
	 *            the maximal number of attributes to mutate
	 * @param ignoredFeatures
	 *            a set of attributes to be ignored
	 * @see AttributeSetter
	 */
	public void setEObjectAttributes(EObject eObject, int maxNumber, Set<EStructuralFeature> ignoredFeatures) {

		final Random random = config.getRandom();
		int numAttrLeft = maxNumber;

		for (final EAttribute attribute : eObject.eClass().getEAllAttributes()) {

			if (ignoredFeatures.contains(attribute)) {
				continue;
			}

			final EClassifier attributeType = attribute.getEAttributeType();

			// randomly remove attributes
			if (random.nextBoolean() && eObject.eIsSet(attribute) && attribute.isMany()) {
				removePerCommand(eObject, attribute, (Collection<?>) eObject.eGet(attribute));
			}

			if (!isValid(attribute, eObject)) {
				continue;
			}

			// the attribute setter used to create new attributes
			final AttributeSetter<?> attributeSetter = getAttributeSetter(attributeType);

			// was there a fitting attribute setter?
			if (attributeSetter == null) {
				continue;
			}

			if (attribute.isMany()) {
				setManyAttribute(eObject, random, attribute, attributeSetter);
			} else {
				setMonoAttribute(eObject, attribute, attributeSetter);
			}

			numAttrLeft--;

			// maxNumber has been reached
			if (numAttrLeft == 0) {
				return;
			}
		}
	}

	private void setMonoAttribute(EObject eObject, final EAttribute attribute,
		final AttributeSetter<?> attributeSetter) {
		setPerCommand(eObject, attribute, attributeSetter.createNewAttribute());
	}

	private void setManyAttribute(EObject eObject, final Random random, final EAttribute attribute,
		final AttributeSetter<?> attributeSetter) {

		final int numberOfAttributes = computeFeatureAmount(attribute, random);

		// vary between empty attributes and already value containing ones
		final int size = ((Collection<?>) eObject.eGet(attribute)).size();

		if (size == 0) {
			if (isFeatureMap(attribute)) {

				final EClass eClass = (EClass) attribute.eContainer();

				@SuppressWarnings("unchecked")
				final List<FeatureMapEntry> entries = initFeatureMapEntries(random,
					((AttributeSetter<FeatureMapEntry>) attributeSetter).createNewAttributes(numberOfAttributes),
					numberOfAttributes, eClass);

				if (!entries.isEmpty()) {
					final List<Object> toBeRemoved = new ArrayList<Object>();
					entries.removeAll(toBeRemoved);
					// TODO: warning
					if (!entries.isEmpty()) {
						addPerCommand(eObject, attribute, entries);
					}
				}
			} else {
				// not a feature map
				addPerCommand(eObject, attribute, attributeSetter.createNewAttributes(numberOfAttributes));
			}
		} else {
			if (isFeatureMap(attribute)) {
				@SuppressWarnings("unchecked")
				final FeatureMapEntry entry = initializeFeatureMapEntry(eObject, random, attribute,
					((AttributeSetter<FeatureMapEntry>) attributeSetter).createNewAttribute());

				setPerCommand(eObject, attribute, entry);
				return;
			}

			// vary between set and and move for higher coverage
			for (int i = 0; i < size; i++) {
				if (random.nextBoolean()) {
					setPerCommand(eObject, attribute, attributeSetter.createNewAttribute(), i);
				} else {
					final Object attributeToMove = ((Collection<?>) eObject.eGet(attribute))
						.toArray()[random.nextInt(size)];
					movePerCommand(eObject, attribute, attributeToMove, random.nextInt(size));
				}
			}
		}
	}

	private boolean isReference(EStructuralFeature feature) {
		return EReference.class.isInstance(feature);
	}

	private boolean isContaimentReference(EStructuralFeature feature) {
		return EReference.class.isInstance(feature) && EReference.class.cast(feature).isContainment();
	}

	private FeatureMapEntry initializeFeatureMapEntry(EObject eObject, final Random random, final EAttribute attribute,
		FeatureMapEntry uninitializedFeatureMapEntry) {
		final EClass eClass = (EClass) attribute.eContainer();

		final List<FeatureMapEntry> entries = initFeatureMapEntries(random,
			Collections.singleton(uninitializedFeatureMapEntry), 1, eClass);
		return entries.get(0);
	}

	private static boolean isFeatureMap(EAttribute eAttribute) {
		return eAttribute.getEAttributeType().equals(EcorePackage.eINSTANCE.getEFeatureMapEntry());
	}

	private List<EStructuralFeature> getAvailableFeatureKeys(EClass eClass) {
		final List<EStructuralFeature> eStructuralFeatures = new ArrayList<EStructuralFeature>(
			eClass.getEStructuralFeatures());
		final Iterator<EStructuralFeature> iterator = eStructuralFeatures.iterator();
		while (iterator.hasNext()) {
			final EStructuralFeature eStructuralFeature = iterator.next();
			if (eStructuralFeature.getEType().equals(EcorePackage.eINSTANCE.getEFeatureMapEntry())) {
				iterator.remove();
			}
		}
		Collections.shuffle(eStructuralFeatures);
		return eStructuralFeatures;
	}

	/**
	 * @param random
	 * @param uninitializedFeatureMapEntries
	 * @param addThese
	 * @param used
	 * @param eStructuralFeatures
	 * @param generatedEObjects
	 */
	private List<FeatureMapEntry> initFeatureMapEntries(final Random random,
		Collection<FeatureMapEntry> uninitializedFeatureMapEntries, int numberOfAttributes, EClass eClass) {

		final List<EStructuralFeature> features = getAvailableFeatureKeys(eClass);
		final Map<EStructuralFeature, List<EObject>> occupiedEObjects = Maps
			.<EStructuralFeature, List<EObject>> newLinkedHashMap();
		final List<FeatureMapEntry> entries = Lists.<FeatureMapEntry> newArrayList();

		for (final FeatureMapEntry entry : uninitializedFeatureMapEntries) {

			Collections.shuffle(features);
			final EStructuralFeature feature = features.get(0);

			if (isReference(feature) && !isContaimentReference(feature)) {

				final EReference reference = EReference.class.cast(feature);

				final EObject selectedEObject = selectUnusedEObject(random, reference.getEReferenceType(),
					getAllObjects(getModelMutatorConfiguration().getRootEObject()), occupiedEObjects);

				if (selectedEObject == null) {
					continue;
				}

				entry.setFeature(feature);
				entry.setReferenceValue(selectedEObject);

				if (!occupiedEObjects.containsKey(reference)) {
					occupiedEObjects.put(reference, Lists.<EObject> newArrayList());
				}

				if (occupiedEObjects.get(reference).contains(selectedEObject)) {
					continue;
				}

				occupiedEObjects.get(reference).add(selectedEObject);
				entries.add(entry);
			} else if (isContaimentReference(feature)) {
				entry.setFeature(feature);
				entry.setReferenceValue(
					createOfType(
						EReference.class.cast(feature).getEReferenceType()));
				entries.add(entry);
			} else {
				final EAttribute attribute = EAttribute.class.cast(feature);
				final AttributeSetter<?> setter = getAttributeSetter(attribute.getEAttributeType());
				entry.setFeature(feature);
				Object attributeValue = null;
				while (attributeValue == null) {
					// FIXME: entry attribute must not be null
					attributeValue = setter.createNewAttribute();
				}
				entry.setDataValue(attributeValue.toString());
				entries.add(entry);
			}
		}

		return entries;
	}

	private EObject selectUnusedEObject(final Random random, EClass desiredClass,
		Map<EClass, List<EObject>> allEObjects, Map<EStructuralFeature, List<EObject>> eObjectsInUse) {

		final List<EObject> p = allEObjects.get(desiredClass);

		if (desiredClass == null || p == null || p.isEmpty()) {
			return null;
		}

		final List<EObject> possibleEObjects = new ArrayList<EObject>(p);
		List<EObject> reservedEObjects = eObjectsInUse.get(desiredClass);

		if (reservedEObjects == null) {
			reservedEObjects = Lists.<EObject> newArrayList();
		}

		while (true) {
			if (possibleEObjects.isEmpty()) {
				return null;
			}
			final EObject eObject = possibleEObjects.get(random.nextInt(possibleEObjects.size()));
			if (reservedEObjects.contains(eObject)) {
				possibleEObjects.remove(eObject);
				continue;
			}
			return eObject;
		}
	}

	private EObject createOfType(EClass eClass) {
		final EObject eObjectToAdd = EcoreUtil.create(eClass);
		setEObjectAttributes(eObjectToAdd);
		return eObjectToAdd;
	}

	/**
	 * Returns all contents of the given {@link EObject} sorted by
	 * the {@link EClass} of the contained children.
	 *
	 * @param root The root object containing the direct and indirect children.
	 * @return The count of all direct and indirect children of the object.
	 */
	public static Map<EClass, List<EObject>> getAllObjects(EObject root) {

		final Map<EClass, List<EObject>> map = new LinkedHashMap<EClass, List<EObject>>();
		final TreeIterator<EObject> eAllContents = root.eAllContents();

		while (eAllContents.hasNext()) {
			final EObject eObject = eAllContents.next();
			final EClass eClass = eObject.eClass();
			if (!map.containsKey(eClass)) {
				map.put(eClass, Lists.<EObject> newArrayList());
			}
			map.get(eClass).add(eObject);
		}

		return map;
	}

	/**
	 * Returns whether <code>attributeType</code> is an instance of EEnum.
	 *
	 * @param attributeType
	 *            the EClassifier in question
	 * @return is <code>attributeType</code> an instance of EEnum?
	 */
	private static boolean isEnum(EClassifier attributeType) {
		return EcorePackage.eINSTANCE.getEEnum().isInstance(attributeType);
	}

	/**
	 * Computes the random amount of objects to add to a feature.
	 *
	 * @param feature
	 *            the feature to compute the amount of objects for
	 * @param random
	 *            the Random object used to obtain random values
	 * @return 1 if the feature is single valued,<br>
	 *         a random value from 0 to 10 if the feature is many-valued and has
	 *         no upper bound,<br>
	 *         a random value between the feature's lower and upper bound
	 *         otherwise
	 */
	private static int computeFeatureAmount(EStructuralFeature feature, Random random) {
		if (!feature.isMany()) {
			return 1;
		}
		if (feature.getUpperBound() < feature.getLowerBound()) {
			return random.nextInt(10);
		}
		return feature.getLowerBound() + random.nextInt(feature.getUpperBound() - feature.getLowerBound() + 1);
	}

	/**
	 * Returns a map containing an AttributeSetter-instance for each attribute
	 * type, granting access to all AttributeSetters.
	 *
	 * @return the map that maps every attribute type to its attribute setter
	 * @see AttributeSetter
	 */
	public Map<EClassifier, AttributeSetter<?>> getAttributeSetters() {

		if (attributeSetters == null) {

			final Random random = config.getRandom();

			final EcorePackage ecoreInstance = EcorePackage.eINSTANCE;

			attributeSetters = new LinkedHashMap<EClassifier, AttributeSetter<?>>();
			AttributeSetter<?> oAttributeSetter;

			oAttributeSetter = new AttributeSetterEBoolean(random);
			attributeSetters.put(ecoreInstance.getEBoolean(), oAttributeSetter);
			attributeSetters.put(ecoreInstance.getEBooleanObject(), oAttributeSetter);

			attributeSetters.put(ecoreInstance.getEByteArray(), new AttributeSetterEByteArray(random, 100));

			attributeSetters.put(ecoreInstance.getEString(), new AttributeSetterEString(random));

			oAttributeSetter = new AttributeSetterEInt(random);
			attributeSetters.put(ecoreInstance.getEInt(), oAttributeSetter);
			attributeSetters.put(ecoreInstance.getEIntegerObject(), oAttributeSetter);
			attributeSetters.put(ecoreInstance.getEFeatureMapEntry(), new AttributeSetterEFeatureMapEntry(random));

			attributeSetters.put(ecoreInstance.getEDate(), new AttributeSetterEDate(random));

			oAttributeSetter = new AttributeSetterELong(random);
			attributeSetters.put(ecoreInstance.getELong(), oAttributeSetter);
			attributeSetters.put(ecoreInstance.getELongObject(), oAttributeSetter);

			oAttributeSetter = new AttributeSetterEByte(random);
			attributeSetters.put(ecoreInstance.getEByte(), oAttributeSetter);
			attributeSetters.put(ecoreInstance.getEByteObject(), oAttributeSetter);

			oAttributeSetter = new AttributeSetterEChar(random);
			attributeSetters.put(ecoreInstance.getEChar(), oAttributeSetter);
			attributeSetters.put(ecoreInstance.getECharacterObject(), oAttributeSetter);

			oAttributeSetter = new AttributeSetterEDouble(random);
			attributeSetters.put(ecoreInstance.getEDouble(), oAttributeSetter);
			attributeSetters.put(ecoreInstance.getEDoubleObject(), oAttributeSetter);

			oAttributeSetter = new AttributeSetterEFloat(random);
			attributeSetters.put(ecoreInstance.getEFloat(), oAttributeSetter);
			attributeSetters.put(ecoreInstance.getEFloatObject(), oAttributeSetter);

			oAttributeSetter = new AttributeSetterEShort(random);
			attributeSetters.put(ecoreInstance.getEShort(), oAttributeSetter);
			attributeSetters.put(ecoreInstance.getEShortObject(), oAttributeSetter);

			attributeSetters.put(ecoreInstance.getEBigInteger(), new AttributeSetterEBigInteger(random));

			attributeSetters.put(ecoreInstance.getEBigDecimal(), new AttributeSetterEBigDecimal(random));

		}

		return attributeSetters;
	}

	private AttributeSetter<?> getAttributeSetter(EClassifier attributeType) {
		getAttributeSetters();
		if (attributeSetters.containsKey(attributeType)) {
			return attributeSetters.get(attributeType);
		} else if (isEnum(attributeType)) {
			return new AttributeSetterEEnum((EEnum) attributeType, config.getRandom());
		}
		return null;
	}

	/**
	 * Creates a random attribute value for the given {@link EAttribute}.
	 *
	 * @param eAttribute
	 *            the attribute to create a new value for
	 * @return the created attribute value
	 */
	public Object createNewAttribute(EAttribute eAttribute) {
		final AttributeSetter<?> setter = getAttributeSetter(eAttribute.getEType());
		return setter.createNewAttribute();
	}

	/**
	 * Retrieves all EClasses from <code>allEClasses</code> that can possibly be
	 * referenced by <code>reference</code> and returns them as a set.
	 *
	 * @param reference
	 *            the EReference to get EClasses for
	 * @param allEClasses
	 *            set of all potentially possible EClasses
	 * @return set of all EClasses that can be referenced by <code>reference</code>
	 */
	public Set<EClass> getReferenceClasses(EReference reference, Set<EClass> allEClasses) {
		final Set<EClass> result = new LinkedHashSet<EClass>();
		final EClass referenceType = reference.getEReferenceType();
		// 'referenceType: EObject' allows all kinds of EObjects
		if (referenceType.equals(EcorePackage.eINSTANCE.getEObject())) {
			return allEClasses;
		}
		for (final EClass eClass : allEClasses) {
			// can eClass be referenced by reference
			if (referenceType.equals(eClass) || referenceType.isSuperTypeOf(eClass)) {
				result.add(eClass);
			}
		}
		return result;
	}

	/**
	 * Sets or adds to a reference for an EObject with any generated instance of <code>referenceClass</code> using
	 * SetCommand/AddCommand. If the reference
	 * is not required, <code>random</code> decides whether the reference is set
	 * or how many EObjects are added to it.
	 *
	 * @param eObject the EObject to set the reference for
	 * @param referenceClass the EClass all referenced EObject shall be instances of
	 * @param reference the reference to set
	 * @param allEObjects Map containing all available EObjects mapped to their EClasses.
	 * @see #addPerCommand(EObject, EStructuralFeature, Collection)
	 * @see #addPerCommand(EObject, EStructuralFeature, Object, Integer)
	 * @see #setPerCommand(EObject, EStructuralFeature, Object)
	 * @see #setPerCommand(EObject, EStructuralFeature, Object, Integer)
	 */
	public void setReference(EObject eObject, EClass referenceClass, EReference reference,
		Map<EClass, List<EObject>> allEObjects) {
		final Random random = config.getRandom();

		final List<EObject> possibleReferenceObjects = allEObjects.get(referenceClass);
		Collections.shuffle(possibleReferenceObjects, random);

		if (possibleReferenceObjects.isEmpty()) {
			return;
		}

		int index = 0;
		if (reference.isMany()) {
			int numberOfReferences = computeFeatureAmount(reference, random);
			numberOfReferences -= ((EList<?>) eObject.eGet(reference)).size();
			for (int i = 0; i < numberOfReferences; i++) {
				final EList<Object> ownerList = AbstractOverrideableCommand.getOwnerList(eObject, reference);

				// change between set, move and add for higher operation coverage
				final int size = ownerList.size();
				if (size > 0 && random.nextBoolean()) {
					if (random.nextBoolean()) {
						// if the reference is ordered, do not use an index
						final Integer newIndex = reference.isOrdered() ? null : random.nextInt(size);
						final EObject target = possibleReferenceObjects.get(index);
						// if (!EMFCycleDetector.wouldBeCycle(config.getRootEObject(), eObject, target)) {
						setPerCommand(eObject, reference, target, newIndex);
						// } else {
						// TODO
						// }

					} else {
						final Object objectToMove = ownerList.get(random.nextInt(size));
						movePerCommand(eObject, reference, objectToMove, index);
					}
				} else {
					final EObject target = possibleReferenceObjects.get(index);
					// if (!EMFCycleDetector.wouldBeCycle(config.getRootEObject(), eObject, target)) {
					addPerCommand(eObject, reference, target, random.nextBoolean()
						&& size > 0 ? random.nextInt(size) : null);
					// } else {
					// TODO
					// }
				}

				// ensures every EObject is set at most once
				if (++index == possibleReferenceObjects.size()) {
					break;
				}
			}
		} else if (random.nextBoolean() || reference.isRequired()) {
			final EObject target = possibleReferenceObjects.get(index);
			// if (!EMFCycleDetector.wouldBeCycle(config.getRootEObject(), eObject, target)) {
			setPerCommand(eObject, reference, target);
			// } else {
			// TODO
			// }
		}
	}

	/**
	 * @param obj The root object containing the direct and indirect children.
	 * @return The count of all direct and indirect children of the object.
	 */
	public static int getAllObjectsCount(EObject obj) {
		final TreeIterator<EObject> eAllContents = obj.eAllContents();
		int i = 0;
		while (eAllContents.hasNext()) {
			i++;
			eAllContents.next();
		}
		return i;
	}

	/**
	 * Sets all possible attributes of known types to random values using {@link AttributeSetter} and
	 * SetCommands/AddCommands.
	 *
	 * @param eObject
	 *            the EObject to set attributes for
	 * @param ignoredFeatures
	 *            a set of attributes to be ignored
	 *
	 * @see AttributeSetter
	 */
	public void setEObjectAttributes(EObject eObject, Set<EStructuralFeature> ignoredFeatures) {
		setEObjectAttributes(eObject, Integer.MAX_VALUE, ignoredFeatures);
	}

	/**
	 * Returns the model mutator configuration.
	 *
	 * @return the model mutator configuration.
	 */
	public ESModelMutatorConfiguration getModelMutatorConfiguration() {
		return config;
	}

	private Map<EStructuralFeature, List<EObject>> getOrBuildFeatureToOfferingObjectsMap() {
		if (featureToOfferingObjects.isEmpty()) {
			buildFeatureToOfferingObjectsMap();
		}
		return featureToOfferingObjects;
	}

	/**
	 * Returns the features that are currently available for mutation.
	 *
	 * @return The features that can be mutated.
	 */
	public Iterable<EStructuralFeature> getAvailableFeatures() {
		return getOrBuildFeatureToOfferingObjectsMap().keySet();
	}

	/**
	 * Returns the features that are currently available for mutation filtered by the given {@code predicate}.
	 *
	 * @param predicate The predicate to be used for filtering the available features.
	 * @return The filtered features that can be mutated.
	 */
	public Iterable<EStructuralFeature> getAvailableFeatures(Predicate<? super EStructuralFeature> predicate) {
		return Iterables.filter(getAvailableFeatures(), predicate);
	}

	/**
	 * Returns the currently existing objects to be mutated that offer the given {@code feature}.
	 *
	 * @param feature The feature to get existing objects for.
	 * @return The existing objects offering the given {@code feature}.
	 */
	public Iterable<EObject> getOfferingEObjectsForAvailableFeature(EStructuralFeature feature) {
		final List<EObject> objectsOfferingFeature = getOrBuildFeatureToOfferingObjectsMap().get(feature);
		if (objectsOfferingFeature == null) {
			return Collections.emptyList();
		}
		return objectsOfferingFeature;
	}

	/**
	 * Returns the currently existing objects to be mutated that offer the given {@code feature} filtered by the given
	 * {@code predicate}.
	 *
	 * @param feature The feature to get existing objects for.
	 * @param predicate The predicate to be used for filtering the objects.
	 * @return The filtered existing objects offering the given {@code feature}.
	 */
	public Iterable<EObject> getOfferingEObjectsForAvailableFeature(EStructuralFeature feature,
		Predicate<? super EObject> predicate) {
		return Iterables.filter(getOfferingEObjectsForAvailableFeature(feature), predicate);
	}

	private void buildFeatureToOfferingObjectsMap() {
		featureToOfferingObjects.clear();
		final EObject rootEObject = config.getRootEObject();
		addToFeatureToOfferingObjectsMap(rootEObject);
		for (final Iterator<EObject> iter = rootEObject.eAllContents(); iter.hasNext();) {
			addToFeatureToOfferingObjectsMap(iter.next());
		}
	}

	private void addToFeatureToOfferingObjectsMap(EObject eObject) {
		final EClass eClassOfObject = eObject.eClass();
		final EList<EStructuralFeature> featuresOfObject = eClassOfObject.getEAllStructuralFeatures();
		for (final EStructuralFeature feature : featuresOfObject) {
			addToFeatureToOfferingObjectsMap(feature, eObject);
		}
	}

	private void addToFeatureToOfferingObjectsMap(EStructuralFeature feature, EObject eObject) {
		if (!featureToOfferingObjects.containsKey(feature)) {
			featureToOfferingObjects.put(feature, new ArrayList<EObject>());
		}
		featureToOfferingObjects.get(feature).add(eObject);
	}

	private void removeFromFeatureToOfferingObjectsMap(EStructuralFeature feature, EObject eObject) {
		if (featureToOfferingObjects.containsKey(feature)) {
			featureToOfferingObjects.get(feature).remove(eObject);
		}
	}

	/**
	 * Returns currently existing objects that are suitable to act as values for the given {@code feature} filtered by
	 * the given {@code predicate}.
	 *
	 * @param feature The feature to get suitable objects for.
	 * @return The existing objects that can be used as values for the given {@code feature}.
	 */
	public Iterable<EObject> getSuitableEObjectsForAvailableFeature(EStructuralFeature feature) {
		final List<EObject> suitableEObjectsForFeature = getOrBuildFeatureToSuitableObjectsMap().get(feature);
		if (suitableEObjectsForFeature == null) {
			return Collections.emptyList();
		}
		return suitableEObjectsForFeature;
	}

	/**
	 * Returns currently existing objects that are suitable to act as values for the given {@code feature}.
	 *
	 * @param feature The feature to get suitable objects for.
	 * @param predicate The predicate to be used for filtering the objects.
	 * @return The existing objects that can be used as values for the given {@code feature}.
	 */
	public Iterable<EObject> getSuitableEObjectsForAvailableFeature(EStructuralFeature feature,
		Predicate<? super EObject> predicate) {
		return Iterables.filter(getSuitableEObjectsForAvailableFeature(feature), predicate);
	}

	private Map<EStructuralFeature, List<EObject>> getOrBuildFeatureToSuitableObjectsMap() {
		if (featureToSuitableObjects.isEmpty()) {
			buildFeatureToSuitableObjectsMap();
		}
		return featureToSuitableObjects;
	}

	private void buildFeatureToSuitableObjectsMap() {
		featureToSuitableObjects.clear();
		final EObject rootEObject = config.getRootEObject();
		addToFeatureToSuitableObjectsMap(rootEObject);
		for (final Iterator<EObject> iter = rootEObject.eAllContents(); iter.hasNext();) {
			addToFeatureToSuitableObjectsMap(iter.next());
		}
	}

	private void addToFeatureToSuitableObjectsMap(EObject eObject) {
		for (final EStructuralFeature feature : getAvailableFeatures(MutationPredicates.IS_REFERENCE)) {
			if (feature.getEType().isInstance(eObject)) {
				addToFeatureToSuitableObjectsMap(feature, eObject);
			}
		}
	}

	private void addToFeatureToSuitableObjectsMap(EStructuralFeature feature, EObject eObject) {
		if (!featureToSuitableObjects.containsKey(feature)) {
			featureToSuitableObjects.put(feature, new ArrayList<EObject>());
		}
		featureToSuitableObjects.get(feature).add(eObject);
	}

	/**
	 * Notifies this util that a new object has been added to maintain the index of offering and suitable objects.
	 *
	 * @param addedEObject The added object.
	 */
	public void addedEObject(EObject addedEObject) {
		addToFeatureToOfferingObjectsMap(addedEObject);
		addToFeatureToSuitableObjectsMap(addedEObject);
	}

	/**
	 * Notifies this util that an object has been deleted to maintain the index of offering and suitable objects.
	 *
	 * @param deletedEObject The added object.
	 */
	public void deletedEObject(EObject deletedEObject) {
		removeFromFeatureToOfferingObjectsMap(deletedEObject);
		removeFromFeatureToSuitableObjectMap(deletedEObject);
	}

	private void removeFromFeatureToOfferingObjectsMap(EObject deletedEObject) {
		final EClass eClassOfDeletedObject = deletedEObject.eClass();
		final List<EStructuralFeature> objectsFeatures = eClassOfDeletedObject.getEAllStructuralFeatures();
		for (final EStructuralFeature feature : objectsFeatures) {
			removeFromFeatureToOfferingObjectsMap(feature, deletedEObject);
		}
	}

	private void removeFromFeatureToSuitableObjectMap(EObject deletedEObject) {
		for (final EStructuralFeature feature : featureToSuitableObjects.keySet()) {
			featureToSuitableObjects.get(feature).remove(deletedEObject);
		}
	}

	/**
	 * Returns the delete modes.
	 *
	 * @return The delete modes.
	 */
	public List<Integer> getDeleteModes() {
		final List<Integer> deleteModes = new ArrayList<Integer>();
		deleteModes.add(ESModelMutatorUtil.DELETE_DELETE_COMMAND);
		deleteModes.add(ESModelMutatorUtil.DELETE_CUT_CONTAINMENT);
		if (config.isUseEcoreUtilDelete()) {
			deleteModes.add(ESModelMutatorUtil.DELETE_ECORE);
		}
		return deleteModes;
	}

	/**
	 * Returns a random delete mode.
	 *
	 * @return A random delete mode.
	 */
	public int getRandomDeleteMode() {
		return getDeleteModes().get(config.getRandom().nextInt(getDeleteModes().size()));
	}

	/**
	 * @param id the ID to check for uniqueness
	 * @return true if {@code id} is unique, false otherwise
	 */
	public boolean isUniqueID(Object id) {
		return !registeredIDs.contains(id);
	}

	/**
	 * @param id the ID to register
	 */
	public void registerID(Object id) {
		registeredIDs.add(id);
	}
}
