/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf;

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A mutator configuration contains the parameters for the model generation and mutation.
 * 
 * @author emueller
 * @since 2.0
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ESMutatorConfig {

	/**
	 * The {@link EClass} of the root.
	 * 
	 * @return {@link EClass} of the root
	 */
	EClass getRootEClass();

	/**
	 * Defines how many objects should be generated.
	 * 
	 * @return the number of objects to be generated
	 */
	int getMinObjectsCount();

	/**
	 * Whether the mutator should generate objects on the root level.
	 * 
	 * @return {@code true}, if the mutator should generate objects on the root level, {@code false} otherwise
	 */
	boolean isDoNotGenerateRoot();

	/**
	 * Returns a collection of {@link EClass EClasses} to be ignored during
	 * object generation.
	 * 
	 * @return a collection of {@link EClass EClasses} that are ignored during
	 *         object generation
	 */
	Collection<EClass> getEClassesToIgnore();

	/**
	 * Returns a collection of {@link EStructuralFeature EStructuralFeatures} to be ignored during object mutation.
	 * 
	 * @return a collection of {@link EStructuralFeature EStructuralFeatures} that are ignored during object generation
	 */
	Collection<EStructuralFeature> getEStructuralFeaturesToIgnore();

	/**
	 * Determines whether the mutator should ignore and log exception or whether it
	 * should fail with an exception.
	 * 
	 * @return {@code true}, if exceptions should only be logged, {@code false} otherwise
	 */
	boolean isIgnoreAndLog();

	/**
	 * Whether delete mutations should use {@link org.eclipse.emf.ecore.util.EcoreUtil#delete
	 * EcoreUtil#delete}.
	 * Note that if this is set to {@code true}, that might lead
	 * to bad performance with bigger models.
	 * 
	 * @return {@code true}, if {@link org.eclipse.emf.ecore.util.EcoreUtil#delete EcoreUtil#delete} should be
	 *         used to perform delete mutation, {@code false} otherwise
	 */
	boolean isUseEcoreUtilDelete();

	/**
	 * Specifies how many objects should be deleted at most?
	 * Only applies to mutation.
	 * 
	 * @return the number of objects that should be deleted at most
	 */
	int getMaxDeleteCount();

	/**
	 * The EPackages to used during object generation.
	 * Only {@link EClass EClasses} contained in these packages are
	 * considered to used for instantiating objects.
	 * 
	 * @return a collection of {@link EClass EClasses} that is considered
	 *         during object generation
	 */
	Collection<EPackage> getEPackages();

	/**
	 * Whether it is allowed to generate duplicate IDs.
	 * 
	 * @return {@code true}, if duplicate IDs should be allowed, {@code false} otherwise
	 */
	boolean isAllowDuplicateIDs();

	/**
	 * Specifies the exact number of mutations to be performed.
	 * Only applies to the mutation case.
	 * 
	 * @return the exact number of mutations to be performed
	 */
	int getMutationCount();

}
