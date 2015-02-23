/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * wesendon
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.conflictdetection.test.merging;

import static java.util.Arrays.asList;

import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.DeletionConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.MultiReferenceSingleConflict;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CreateDeleteOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.SingleReferenceOperation;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.Test;

/**
 * These only conflict through <b>containment</b> side effects.
 * 
 * @author wesendon
 */
public class SingleReferenceVsMultiMergeTets extends MergeTest {

	@Test
	public void setVsMultiAdd() {
		final TestElement parent = Create.testElement();
		final TestElement secondparent = Create.testElement();
		final TestElement child = Create.testElement();

		final MergeCase mc = newMergeCase(parent, secondparent, child);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).setContainedElement_NoOpposite(child);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(secondparent).getContainedElements_NoOpposite().add(mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(MultiReferenceSingleConflict.class)
			// My
			.myIs(SingleReferenceOperation.class).andNoOtherMyOps()
			// Theirs
			.theirsIs(MultiReferenceOperation.class).andNoOtherTheirOps();
	}

	@Test
	public void setVsMultiAddNC() {
		final TestElement parent = Create.testElement();
		final TestElement secondparent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement secondChild = Create.testElement();

		final MergeCase mc = newMergeCase(parent, secondparent, child, secondChild);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).setContainedElement(child);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(secondparent).getContainedElements().add(mc.getTheirItem(secondChild));
			}
		}.run(false);

		mc.hasConflict(null);
	}

	@Test
	public void setVsMultiAddMany() {
		final TestElement parent = Create.testElement();
		final TestElement secondparent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement secondChild = Create.testElement();

		final MergeCase mc = newMergeCase(parent, secondparent, child, secondChild);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).setContainedElement_NoOpposite(child);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(secondparent).getContainedElements_NoOpposite()
					.addAll(asList(mc.getTheirItem(child), mc.getTheirItem(secondChild)));
			}
		}.run(false);

		mc.hasConflict(MultiReferenceSingleConflict.class)
			// My
			.myIs(SingleReferenceOperation.class).andNoOtherMyOps()
			// Theirs
			.theirsIs(MultiReferenceOperation.class).andNoOtherTheirOps();
	}

	@Test
	public void setVsMultiSet() {
		final TestElement parent = Create.testElement();
		final TestElement secondparent = Create.testElement();
		secondparent.getContainedElements_NoOpposite().add(Create.testElement());
		final TestElement child = Create.testElement();

		final MergeCase mc = newMergeCase(parent, secondparent, child);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).setContainedElement_NoOpposite(child);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(secondparent).getContainedElements_NoOpposite().set(0, mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(DeletionConflict.class)
			// My
			.myIs(SingleReferenceOperation.class).andNoOtherMyOps()
			// Theirs
			.theirsIs(CreateDeleteOperation.class);
	}
}
