/*******************************************************************************
 * Copyright (c) 2012-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Stephan Koehler, Eugen Neufeld, Philip Achenbach, DmitryLitvinov - initial API and implementation
 * Edgar Mueller - API layer
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.junit;

import java.util.Collections;
import java.util.Set;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.modelmutator.ESAbstractModelMutator;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;

/**
 * Implementation of AbstractModelMutator with empty preMutate and postMutate methods.
 * 
 * 
 * @author Eugen Neufeld
 * @author Stephan Koehler
 * @author Philip Achenbach
 * @author Dmitry Litvinov
 * 
 * @since 2.0
 * 
 */
public class ESDefaultModelMutator extends ESAbstractModelMutator {

	/**
	 * Generates a model as specified in the config.
	 * 
	 * @param config the configuration
	 */
	public static void generateModel(ESModelMutatorConfiguration config) {
		final ESDefaultModelMutator modelMutator = new ESDefaultModelMutator(config);
		modelMutator.generate();
	}

	/**
	 * Modifies a model as specified in the config.
	 * 
	 * @param config the configuration
	 */
	public static void changeModel(ESModelMutatorConfiguration config) {
		final ESDefaultModelMutator modelMutator = new ESDefaultModelMutator(config);
		modelMutator.mutate(Collections.<EStructuralFeature> emptySet());
	}

	/**
	 * Modifies a model as specified in the config.
	 * 
	 * @param config
	 *            the configuration
	 * @param ignoredFeatures
	 *            the features that are to be ignored while changing the model
	 */
	public static void changeModel(ESModelMutatorConfiguration config, Set<EStructuralFeature> ignoredFeatures) {
		final ESDefaultModelMutator modelMutator = new ESDefaultModelMutator(config);
		modelMutator.mutate(ignoredFeatures);
	}

	/**
	 * The constructor.
	 * 
	 * @param config
	 *            the configuration used in the process
	 */
	public ESDefaultModelMutator(ESModelMutatorConfiguration config) {
		super(config);
	}

	@Override
	public void preMutate() {
	}

	@Override
	public void postMutate() {
	}

	@Override
	public void mutate(Set<EStructuralFeature> ignoredFeatures) {
		super.mutate(ignoredFeatures);
	}
}
