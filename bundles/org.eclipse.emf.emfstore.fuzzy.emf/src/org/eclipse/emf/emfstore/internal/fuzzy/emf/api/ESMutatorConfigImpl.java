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
package org.eclipse.emf.emfstore.internal.fuzzy.emf.api;

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig;
import org.eclipse.emf.emfstore.internal.common.api.AbstractAPIImpl;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.MutatorConfig;

/**
 * The API implementation wrapper for {@link MutatorConfig}.
 *
 * @author emueller
 *
 */
public class ESMutatorConfigImpl extends AbstractAPIImpl<ESMutatorConfigImpl, MutatorConfig>
	implements ESMutatorConfig {

	/**
	 * Constructor.
	 *
	 * @param mutatorConfig
	 *            the internal {@link MutatorConfig} instance to be wrapped
	 */
	public ESMutatorConfigImpl(MutatorConfig mutatorConfig) {
		super(mutatorConfig);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig#getRootEClass()
	 */
	public EClass getRootEClass() {
		return toInternalAPI().getRootEClass();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig#getMinObjectsCount()
	 */
	public int getMinObjectsCount() {
		return toInternalAPI().getMinObjectsCount();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig#isDoNotGenerateRoot()
	 */
	public boolean isDoNotGenerateRoot() {
		return toInternalAPI().isDoNotGenerateRoot();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig#getEClassesToIgnore()
	 */
	public Collection<EClass> getEClassesToIgnore() {
		return toInternalAPI().getEClassesToIgnore();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig#getEStructuralFeaturesToIgnore()
	 */
	public Collection<EStructuralFeature> getEStructuralFeaturesToIgnore() {
		return toInternalAPI().getEStructuralFeaturesToIgnore();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig#isIgnoreAndLog()
	 */
	public boolean isIgnoreAndLog() {
		return toInternalAPI().isIgnoreAndLog();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig#isUseEcoreUtilDelete()
	 */
	public boolean isUseEcoreUtilDelete() {
		return toInternalAPI().isUseEcoreUtilDelete();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig#getMaxDeleteCount()
	 */
	public Integer getMaxDeleteCount() {
		return toInternalAPI().getMaxDeleteCount();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig#getEPackages()
	 */
	public Collection<EPackage> getEPackages() {
		return toInternalAPI().getEPackages();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig#isAllowDuplicateIDs()
	 */
	public boolean isAllowDuplicateIDs() {
		return toInternalAPI().isAllowDuplicateIDs();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig#getMutationCount()
	 */
	public int getMutationCount() {
		return toInternalAPI().getMutationCount();
	}

}
