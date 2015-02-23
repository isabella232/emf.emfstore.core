/*******************************************************************************
 * Copyright (c) 2008-2015 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * chodnick - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.conflictdetection.test;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.test.common.dsl.TestElementFeatures;
import org.eclipse.emf.emfstore.client.test.common.dsl.Update;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictBucket;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsFactory;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.Test;

/**
 * Tests conflict detection behaviour on attributes.
 *
 * @author chodnick
 */
public class ConflictDetectionAttributeTest extends ConflictDetectionTest {

	private static final String UNRELATED_CHANGE = "unrelated change"; //$NON-NLS-1$
	private static final String CHANGE_1 = "change 1"; //$NON-NLS-1$
	private static final String CHANGE_2 = "change 2"; //$NON-NLS-1$
	private static final String OLD_NAME = "old name"; //$NON-NLS-1$

	/**
	 * Tests if overwriting of attributes is detected as conflict.
	 */
	@Test
	public void conflictAttribute() {

		final TestElement section = Create.testElement();
		final TestElement actor = Create.testElement();
		actor.setName(OLD_NAME);

		Add.toProject(getLocalProject(), section);
		Add.toContainedElements(section, actor);

		clearOperations();

		final ProjectSpace ps2 = cloneProjectSpace(getProjectSpace());
		final Project project2 = ps2.getProject();

		final ModelElementId actor1Id = getProject().getModelElementId(actor);
		final ModelElementId actor2Id = actor1Id;

		final TestElement actor1 = (TestElement) getProject().getModelElement(actor1Id);
		final TestElement actor2 = (TestElement) project2.getModelElement(actor2Id);

		Update.testElement(TestElementFeatures.name(), actor1, CHANGE_1);
		Update.testElement(TestElementFeatures.name(), actor2, CHANGE_2);

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(ps2);

		final List<ConflictBucket> conflicts = getConflicts(ops1, ops2);
		assertEquals(conflicts.size(), 1);

		assertEquals(getConflicts(ops1, ops2).size(), getConflicts(ops2, ops1).size());
	}

	/**
	 * Tests if overwriting of attributes is detected as conflict.
	 */
	@Test
	public void noConflictAttributeSameValue() {

		final TestElement testElement = Create.testElement();
		final TestElement containedElement = Create.testElement();
		containedElement.setName(OLD_NAME);
		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				getProject().addModelElement(testElement);
				testElement.getContainedElements().add(containedElement);
				clearOperations();
			}
		}.run(false);

		final ProjectSpace ps2 = cloneProjectSpace(getProjectSpace());
		final Project project2 = ps2.getProject();

		final ModelElementId actor1Id = getProject().getModelElementId(containedElement);
		final ModelElementId actor2Id = actor1Id;

		final TestElement testElement1 = (TestElement) getProject().getModelElement(actor1Id);
		final TestElement testElement2 = (TestElement) project2.getModelElement(actor2Id);
		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				testElement1.setName(CHANGE_1);
				testElement2.setName(CHANGE_1);
			}
		}.run(false);

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(ps2);

		final List<ConflictBucket> conflicts = getConflicts(ops1, ops2);
		final ConflictBucket bucket = conflicts.get(0);

		assertEquals(1, conflicts.size());
		assertEquals(1, bucket.getMyOperations().size());
		assertEquals(1, bucket.getTheirOperations().size());
	}

	/**
	 * Tests if overwriting of attributes is detected as conflict.
	 */
	@Test
	public void noConflictAttribute() {

		final TestElement section = Create.testElement();
		final TestElement actor = Create.testElement(OLD_NAME);

		Add.toProject(getLocalProject(), section);
		Add.toContainedElements(section, actor);
		clearOperations();

		final ProjectSpace ps2 = cloneProjectSpace(getProjectSpace());
		final Project project2 = ps2.getProject();

		final ModelElementId actorId = getProject().getModelElementId(actor);

		final TestElement actor1 = (TestElement) getProject().getModelElement(actorId);
		final TestElement actor2 = (TestElement) project2.getModelElement(actorId);

		Update.testElement(TestElementFeatures.name(), actor1, CHANGE_1);
		Update.testElement(TestElementFeatures.description(), actor2, UNRELATED_CHANGE);

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(ps2);

		final List<ConflictBucket> conflicts = getConflicts(ops1, ops2);
		assertEquals(conflicts.size(), 0);
	}

	private static final String OLDE_VALUE = "oldeValue"; //$NON-NLS-1$
	private static final String OLD_VALUE = "oldValue"; //$NON-NLS-1$
	private static final String ID1 = "id1"; //$NON-NLS-1$
	private static final String SAME_FEATURE = "same Feature"; //$NON-NLS-1$

	/**
	 * Test for conflicts on two attribute Operations.
	 */
	@Test
	public void testAttributeWithAttributeConflict() {
		final TestElement testElement = Create.testElement();

		Add.toProject(getLocalProject(), testElement);

		final String featureName = SAME_FEATURE;
		final AttributeOperation attributeOperation1 = OperationsFactory.eINSTANCE.createAttributeOperation();
		attributeOperation1.setClientDate(new Date());
		attributeOperation1.setFeatureName(featureName);
		attributeOperation1.setIdentifier(ID1);
		attributeOperation1.setModelElementId(getProject().getModelElementId(testElement));
		attributeOperation1.setOldValue(OLD_VALUE);
		attributeOperation1.setNewValue(OLDE_VALUE);

		final AttributeOperation attributeOperation2 = OperationsFactory.eINSTANCE.createAttributeOperation();
		attributeOperation2.setClientDate(new Date());
		attributeOperation2.setFeatureName(featureName);
		attributeOperation2.setIdentifier(ID1);
		attributeOperation2.setModelElementId(org.eclipse.emf.emfstore.internal.common.model.ModelFactory.eINSTANCE
			.createModelElementId());
		attributeOperation2.setOldValue(OLD_VALUE);
		attributeOperation2.setNewValue(OLDE_VALUE);

		assertEquals(false, doConflict(attributeOperation1, attributeOperation2));

		attributeOperation2.setModelElementId(getProject().getModelElementId(testElement));
		attributeOperation2.setFeatureName(featureName + "2"); //$NON-NLS-1$

		assertEquals(false, doConflict(attributeOperation1, attributeOperation2));

		attributeOperation2.setFeatureName(featureName);

		assertEquals(true, doConflict(attributeOperation1, attributeOperation2));
	}
}
