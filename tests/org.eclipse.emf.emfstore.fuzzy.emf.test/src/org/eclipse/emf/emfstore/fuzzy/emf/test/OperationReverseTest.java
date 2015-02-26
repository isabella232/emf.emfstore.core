/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Julian Sommerfeldt - initial API and implementation
 * Edgar Mueller - bug fixing
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.test;

import org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.DataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyRunner;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.CloseableIterable;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Fuzzy ESFuzzyTest for the reverse functionality of operations.
 *
 * @author Julian Sommerfeldt
 *
 */
@RunWith(ESFuzzyRunner.class)
@DataProvider(ESEMFDataProvider.class)
public class OperationReverseTest extends FuzzyProjectTest {

	/***/
	@Test
	public void reverseTest() {
		final ProjectSpaceBase projectSpace = (ProjectSpaceBase) getProjectSpace();
		final ESModelMutatorConfiguration mmc = getModelMutatorConfiguration(projectSpace
			.getProject());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getUtil().mutate(mmc);
			}
		}.run(false);

		final CloseableIterable<AbstractOperation> reversedOperations = projectSpace.changePackage()
			.reversedOperations();

		try {
			new EMFStoreCommand() {
				@Override
				protected void doRun() {
					projectSpace.applyOperations(reversedOperations.iterable(), false);
				}
			}.run(false);
		} finally {
			reversedOperations.close();
		}

		compareIgnoreOrder(projectSpace.getProject(), getCopyProjectSpace().getProject());
	}
}
