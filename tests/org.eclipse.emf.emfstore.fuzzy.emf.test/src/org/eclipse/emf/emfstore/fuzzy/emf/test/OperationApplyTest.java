/*******************************************************************************
 * Copyright (c) 2012-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Julian Sommerfeldt - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.test;

import org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.DataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyRunner;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.model.ESOperation;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ESFuzzyTest which mutates a project and then applies its changes to another (copied)
 * project. Then compares the two mutated projects.
 *
 * @author Julian Sommerfeldt
 *
 */
@RunWith(ESFuzzyRunner.class)
@DataProvider(ESEMFDataProvider.class)
public class OperationApplyTest extends FuzzyProjectTest {

	/***/
	@Test
	public void applyTest() {

		final ProjectSpace projectSpace = getProjectSpace();
		final ESModelMutatorConfiguration mmc = getModelMutatorConfiguration(projectSpace
			.getProject());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getUtil().mutate(mmc);
			}
		}.run(false);

		final ProjectSpace copyProjectSpace = getCopyProjectSpace();
		final ESCloseableIterable<ESOperation> operations = projectSpace.changePackage().operations();
		try {
			new EMFStoreCommand() {
				@Override
				protected void doRun() {
					((ProjectSpaceBase) copyProjectSpace).applyOperations(
						operations.iterable(), false);
				}
			}.run(false);
		} finally {
			operations.close();
		}

		compareIgnoreOrder(projectSpace.getProject(),
			copyProjectSpace.getProject());
	}
}
