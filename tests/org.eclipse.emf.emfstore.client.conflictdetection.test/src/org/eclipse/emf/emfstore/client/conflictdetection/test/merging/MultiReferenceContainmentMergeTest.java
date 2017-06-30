/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.conflictdetection.test.merging;

import static java.util.Arrays.asList;

import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.DeletionConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.ReferenceConflict;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CreateDeleteOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceOperation;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.Test;

public class MultiReferenceContainmentMergeTest extends MergeTest {

	private static final String IS_DELETE = "isDelete"; //$NON-NLS-1$
	private static final String IS_ADD = "isAdd"; //$NON-NLS-1$

	@Test
	public void addSameToDifferent() {
		final TestElement parent = Create.testElement();
		final TestElement parent2 = Create.testElement();
		final TestElement child = Create.testElement();

		final MergeCase mc = newMergeCase(parent, parent2, child);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getContainedElements().add(mc.getMyItem(child));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent2).getContainedElements().add(mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(ReferenceConflict.class)
			// My
			.myIs(MultiReferenceOperation.class).andReturns(IS_ADD, true).andNoOtherMyOps()
			// Theirs
			.theirsIs(MultiReferenceOperation.class).andReturns(IS_ADD, true).andNoOtherTheirOps();
	}

	@Test
	public void addSameToSameNC() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getContainedElements().add(mc.getMyItem(child));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getContainedElements().add(mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(ReferenceConflict.class);
	}

	@Test
	public void addSameManyToDifferent() {
		final TestElement parent = Create.testElement();
		final TestElement parent2 = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement child2 = Create.testElement();

		final MergeCase mc = newMergeCase(parent, parent2, child, child2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getContainedElements().addAll(asList(mc.getMyItem(child), mc.getMyItem(child2)));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent2).getContainedElements()
					.addAll(asList(mc.getTheirItem(child), mc.getTheirItem(child2)));
			}
		}.run(false);

		mc.hasConflict(ReferenceConflict.class)
			// My
			.myIs(MultiReferenceOperation.class).andReturns(IS_ADD, true).andNoOtherMyOps()
			// Theirs
			.theirsIs(MultiReferenceOperation.class).andReturns(IS_ADD, true).andNoOtherTheirOps();
	}

	@Test
	public void addSameManyToSameNC() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement child2 = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child, child2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getContainedElements().addAll(asList(mc.getMyItem(child), mc.getMyItem(child2)));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getContainedElements()
					.addAll(asList(mc.getTheirItem(child), mc.getTheirItem(child2)));
			}
		}.run(false);

		mc.hasConflict(ReferenceConflict.class);
	}

	@Test
	public void addRemoveSame() {
		final TestElement parent = Create.testElement();
		final TestElement parent2 = Create.testElement();
		final TestElement child = Create.testElement();
		// parent2.getContainedElements().add(child);

		final MergeCase mc = newMergeCase(parent, parent2, child);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getContainedElements_NoOpposite().add(mc.getMyItem(child));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				// V1
				mc.getTheirItem(parent2).getContainedElements_NoOpposite().add(mc.getTheirItem(child));
				mc.getTheirItem(parent2).getContainedElements_NoOpposite().remove(mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(DeletionConflict.class)
			// My
			.myIs(MultiReferenceOperation.class).andReturns(IS_ADD, true)
			// Theirs
			.theirsIs(CreateDeleteOperation.class).andReturns(IS_DELETE, true);
	}

	@Test
	public void addSetSameToDifferent() {
		final TestElement parent = Create.testElement();
		final TestElement parent2 = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement decoy = Create.testElement();
		parent2.getContainedElements_NoOpposite().add(decoy);

		final MergeCase mc = newMergeCase(parent, parent2, child);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getContainedElements_NoOpposite().add(mc.getMyItem(child));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent2).getContainedElements_NoOpposite().set(0, mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(DeletionConflict.class)
			// My
			.myIs(MultiReferenceOperation.class).andReturns(IS_ADD, true).andNoOtherMyOps()
			// Theirs
			.theirsIs(CreateDeleteOperation.class);
	}

	@Test
	public void addSetSameToSameNC() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement decoy = Create.testElement();
		parent.getContainedElements().add(decoy);

		final MergeCase mc = newMergeCase(parent, child);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				// remove element from containedElements lists
				mc.getMyItem(parent).setContainedElement(mc.getMyItem(decoy));
				clearOperations();
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getContainedElements().add(mc.getMyItem(child));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getContainedElements().set(0, mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(DeletionConflict.class);
	}

	@Test
	public void setSetSameToDifferent() {
		final TestElement parent = Create.testElement();
		final TestElement parent2 = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement decoy = Create.testElement();
		final TestElement decoy2 = Create.testElement();
		parent.getContainedElements().add(decoy);
		parent2.getContainedElements().add(decoy2);

		final MergeCase mc = newMergeCase(parent, parent2, child);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getContainedElements().set(0, mc.getMyItem(child));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent2).getContainedElements().set(0, mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(DeletionConflict.class)
			// My
			.myIs(CreateDeleteOperation.class)
			// Theirs
			.theirsIs(CreateDeleteOperation.class);
	}

	@Test
	public void setSetSameToSameNC() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement decoy = Create.testElement();
		parent.getContainedElements().add(decoy);

		final MergeCase mc = newMergeCase(parent, child);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getContainedElements().set(0, mc.getMyItem(child));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getContainedElements().set(0, mc.getTheirItem(child));
			}
		}.run(false);

		// false negative
		mc.hasConflict(DeletionConflict.class, 1);
	}
}