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

import org.eclipse.emf.emfstore.fuzzy.emf.ESMutatorConfig;
import org.eclipse.emf.emfstore.fuzzy.emf.ESTestConfig;
import org.eclipse.emf.emfstore.internal.common.api.AbstractAPIImpl;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestConfig;

/**
 * The API implementation wrapper for {@link TestConfig}.
 *
 * @author emueller
 *
 */
public class ESTestConfigImpl extends AbstractAPIImpl<ESTestConfigImpl, TestConfig>
	implements ESTestConfig {

	/**
	 * Constructor.
	 *
	 * @param testConfig
	 *            the internal {@link TestConfig} instance to be wrapped
	 */
	public ESTestConfigImpl(TestConfig testConfig) {
		super(testConfig);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESTestConfig#getId()
	 */
	public String getId() {
		return toInternalAPI().getId();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESTestConfig#getSeed()
	 */
	public long getSeed() {
		return toInternalAPI().getSeed();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESTestConfig#getCount()
	 */
	public int getCount() {
		return toInternalAPI().getCount();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESTestConfig#getMutatorConfig()
	 */
	public ESMutatorConfig getMutatorConfig() {
		return new ESMutatorConfigImpl(toInternalAPI().getMutatorConfig());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.fuzzy.emf.ESTestConfig#getTestClass()
	 */
	public Class<?> getTestClass() {
		return toInternalAPI().getTestClass();
	}

}
