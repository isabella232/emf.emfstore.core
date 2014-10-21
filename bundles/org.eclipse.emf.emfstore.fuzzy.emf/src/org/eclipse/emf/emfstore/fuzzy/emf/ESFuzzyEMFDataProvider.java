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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.emfstore.fuzzy.ESFuzzyDataProvider;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;

/**
 * Common interface for {@link ESFuzzyDataProvider ESFuzzyDataProviders} that generate {@link EObject}s.
 * 
 * @author emueller
 * @since 2.0
 * 
 */
public interface ESFuzzyEMFDataProvider extends ESFuzzyDataProvider<EObject> {

	/**
	 * Returns a collection of {@link EPackage EPackages} that are
	 * being considered to be generated/mutated.
	 * 
	 * @return the {@link EPackage} of the model to generate/mutate.
	 */
	Collection<EPackage> getEPackages();

	/**
	 * Returns the currently active {@link ESModelMutatorConfiguration}.
	 * 
	 * @return the currently active {@link ESModelMutatorConfiguration}
	 */
	ESModelMutatorConfiguration getModelMutatorConfiguration();

	/**
	 * Returns the current seed for this data provider.
	 * 
	 * @return the current seed for this data provider
	 */
	long getSeed();

	/**
	 * Returns the current seed that is used to generate the model.
	 * 
	 * @return the current seed that is used to generate the model.
	 */
	int getCurrentSeedCount();

	/**
	 * Returns the config specifying this run of this provider.
	 * 
	 * @return the config specifying this run of this provider
	 * 
	 */
	ESTestConfig getConfig();

}
