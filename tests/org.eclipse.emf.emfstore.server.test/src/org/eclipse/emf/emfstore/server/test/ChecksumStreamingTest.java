/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.test;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUserMock;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChecksumStreamingTest extends ESTestWithLoggedInUserMock {
	@Override
	@Before
	public void before() {
		org.junit.Assume.assumeTrue(transactionalEditingDomainNotInUse());
		super.before();
	}

	public boolean transactionalEditingDomainNotInUse() {
		return !ESWorkspaceProviderImpl.getInstance().getEditingDomain().getClass().getName().contains("Transactional"); //$NON-NLS-1$
	}

	@Override
	@After
	public void after() {
		super.after();
	}

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Test
	public void testChecksumOptimization() throws SerializationException {
		final long checksumStreaming = computeChecksumStreamingOrNonStreaming(getLocalProject(), true);
		System.out.println("Checksum streaming: " + checksumStreaming); //$NON-NLS-1$
		final long checksum = computeChecksumStreamingOrNonStreaming(getLocalProject(), false);
		System.out.println("Checksum old: " + checksum); //$NON-NLS-1$
		assertEquals(checksum, checksumStreaming);

	}

	private static long computeChecksumStreamingOrNonStreaming(ESLocalProject localProject,
		boolean useStreamingCalculation)
			throws SerializationException {
		final ProjectSpace projectSpace = ((ESLocalProjectImpl) localProject).toInternalAPI();
		final long checksum = useStreamingCalculation ? ModelUtil.computeChecksum(projectSpace.getProject())
			: ModelUtil.computeChecksumLegacy(projectSpace.getProject());
		System.out.println(ModelUtil.eObjectToString(projectSpace.getProject()));
		return checksum;
	}
}
