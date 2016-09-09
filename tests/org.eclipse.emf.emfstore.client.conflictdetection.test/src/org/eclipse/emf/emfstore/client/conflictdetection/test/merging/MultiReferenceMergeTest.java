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
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.MultiReferenceConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.MultiReferenceSetConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.MultiReferenceSetSetConflict;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceSetOperation;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.Test;

public class MultiReferenceMergeTest extends MergeTest {

	private static final String GET_INDEX = "getIndex"; //$NON-NLS-1$
	private static final String GET_REFERENCED_MODEL_ELEMENTS = "getReferencedModelElements"; //$NON-NLS-1$
	private static final String IS_ADD = "isAdd"; //$NON-NLS-1$

	@Test
	public void addRemoveSame() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().add(mc.getMyItem(child));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().add(mc.getTheirItem(child));
				mc.getTheirItem(parent).getReferences().remove(mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(MultiReferenceConflict.class)
			// my
			.myIs(MultiReferenceOperation.class).andReturns(IS_ADD, true).andNoOtherMyOps()
			// their
			.theirsIs(MultiReferenceOperation.class).andReturns(IS_ADD, false);
	}

	@Test
	public void addRemoveManySame() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement decoyChild = Create.testElement();
		parent.getReferences().add(decoyChild);

		final MergeCase mc = newMergeCase(parent, child, decoyChild);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().add(mc.getMyItem(child));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().add(mc.getTheirItem(child));
				mc.getTheirItem(parent).getReferences()
					.removeAll(asList(mc.getTheirItem(child), mc.getTheirItem(decoyChild)));
			}
		}.run(false);

		mc.hasConflict(MultiReferenceConflict.class)
			// my
			.myIs(MultiReferenceOperation.class).andReturns(IS_ADD, true).andNoOtherMyOps()
			// their
			.theirsIs(MultiReferenceOperation.class).andReturns(IS_ADD, false);

	}

	@Test
	public void addRemoveDifferentNC() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement child2 = Create.testElement();
		parent.getReferences().add(child2);

		final MergeCase mc = newMergeCase(parent, child, child2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().add(mc.getMyItem(child));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().remove(mc.getTheirItem(child2));
			}
		}.run(false);

		mc.hasConflict(null);
	}

	@Test
	public void addRemoveSameWithNoise() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement child2 = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child, child2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().add(mc.getMyItem(child));
				mc.getMyItem(parent).getReferences().add(mc.getMyItem(child2));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().add(mc.getTheirItem(child));
				mc.getTheirItem(parent).getReferences().remove(mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(MultiReferenceConflict.class)
			// my
			.myIs(MultiReferenceOperation.class).andReturns(IS_ADD, true).andNoOtherMyOps()
			// their
			.theirsIs(MultiReferenceOperation.class).andReturns(IS_ADD, false);
	}

	// Many

	@Test
	public void addManyRemoveSame() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement child2 = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child, child2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().addAll(asList(mc.getMyItem(child), mc.getMyItem(child2)));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().add(mc.getTheirItem(child));
				mc.getTheirItem(parent).getReferences().remove(mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(MultiReferenceConflict.class)
			// my
			.myIs(MultiReferenceOperation.class).andReturns(IS_ADD, true).andNoOtherMyOps()
			// their
			.theirsIs(MultiReferenceOperation.class).andReturns(IS_ADD, false);
	}

	@Test
	public void addManyRemoveManySame() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement child2 = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child, child2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().addAll(asList(mc.getMyItem(child), mc.getMyItem(child2)));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().add(mc.getTheirItem(child));
				mc.getTheirItem(parent).getReferences().add(mc.getTheirItem(child2));
				mc.getTheirItem(parent).getReferences()
					.removeAll(asList(mc.getTheirItem(child), mc.getTheirItem(child2)));
			}
		}.run(false);

		mc.hasConflict(MultiReferenceConflict.class)
			// my
			.myIs(MultiReferenceOperation.class).andReturns(IS_ADD, true)
			.andReturns(GET_REFERENCED_MODEL_ELEMENTS, getIds(child, child2)).andNoOtherMyOps()
			// their
			.theirsIs(MultiReferenceOperation.class).andReturns(IS_ADD, false)
			.andReturns(GET_REFERENCED_MODEL_ELEMENTS, getIds(child, child2));
	}

	@Test
	public void addManyRemoveDifferentNC() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement child2 = Create.testElement();
		final TestElement child3 = Create.testElement();
		parent.getReferences().add(child3);

		final MergeCase mc = newMergeCase(parent, child, child2, child3);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().addAll(asList(mc.getMyItem(child), mc.getMyItem(child2)));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().remove(mc.getTheirItem(child3));
			}
		}.run(false);

		mc.hasConflict(null);
	}

	@Test
	public void addManyRemoveSameWithNoise() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement child2 = Create.testElement();
		final TestElement decoy1 = Create.testElement();
		final TestElement decoy2 = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child, child2, decoy1, decoy2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().addAll(asList(mc.getMyItem(child), mc.getMyItem(child2)));
				mc.getMyItem(parent).getReferences().addAll(asList(mc.getMyItem(decoy1), mc.getMyItem(decoy2)));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().add(mc.getTheirItem(child));
				mc.getTheirItem(parent).getReferences().remove(mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(MultiReferenceConflict.class)
			// my
			.myIs(MultiReferenceOperation.class).andReturns(IS_ADD, true)
			.andReturns(GET_REFERENCED_MODEL_ELEMENTS, getIds(child, child2)).andNoOtherMyOps()
			// their
			.theirsIs(MultiReferenceOperation.class).andReturns(IS_ADD, false)
			.andReturns(GET_REFERENCED_MODEL_ELEMENTS, getIds(child));
	}

	// Remove

	@Test
	public void removeVsSet() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		parent.getReferences().add(child);
		final TestElement newChild = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child, newChild);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().remove(mc.getMyItem(child));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().set(0, mc.getTheirItem(newChild));
			}
		}.run(false);

		mc.hasConflict(MultiReferenceSetConflict.class)
			// my
			.myIs(MultiReferenceOperation.class).andReturns(IS_ADD, false).andNoOtherMyOps()
			// their
			.theirsIs(MultiReferenceSetOperation.class).andReturns(GET_INDEX, 0).andNoOtherTheirOps();
	}

	@Test
	public void removeManyVsSet() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement child2 = Create.testElement();
		parent.getReferences().add(child);
		parent.getReferences().add(child2);
		final TestElement newChild = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child, child2, newChild);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().removeAll(asList(mc.getMyItem(child), mc.getTheirItem(child2)));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().set(0, mc.getTheirItem(newChild));
			}
		}.run(false);

		mc.hasConflict(MultiReferenceSetConflict.class)
			// my
			.myIs(MultiReferenceOperation.class).andReturns(IS_ADD, false).andNoOtherMyOps()
			// their
			.theirsIs(MultiReferenceSetOperation.class).andReturns(GET_INDEX, 0).andNoOtherTheirOps();
	}

	@Test
	public void removeVsSetDifferentNC() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement child2 = Create.testElement();
		final TestElement child3 = Create.testElement();
		parent.getReferences().add(child);
		parent.getReferences().add(child2);
		parent.getReferences().add(child3);
		final TestElement newChild = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child, child2, child3, newChild);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().removeAll(asList(mc.getMyItem(child), mc.getTheirItem(child2)));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().set(2, mc.getTheirItem(newChild));
			}
		}.run(false);

		mc.hasConflict(null);
	}

	// Set

	@Test
	public void setVsSet() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		parent.getReferences().add(child);
		final TestElement newChild = Create.testElement();
		final TestElement newChild2 = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child, newChild, newChild2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().set(0, mc.getMyItem(newChild));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().set(0, mc.getTheirItem(newChild2));
			}
		}.run(false);

		mc.hasConflict(MultiReferenceSetSetConflict.class)
			// my
			.myIs(MultiReferenceSetOperation.class).andReturns(GET_INDEX, 0).andNoOtherMyOps()
			// their
			.theirsIs(MultiReferenceSetOperation.class).andReturns(GET_INDEX, 0).andNoOtherTheirOps();
	}

	@Test
	public void setVsSetSameNC() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		parent.getReferences().add(child);
		final TestElement newChild = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child, newChild);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().set(0, mc.getMyItem(newChild));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().set(0, mc.getTheirItem(newChild));
			}
		}.run(false);

		mc.hasConflict(MultiReferenceSetSetConflict.class);
	}

	@Test
	public void setVsSetDifferentNC() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement child2 = Create.testElement();
		parent.getReferences().add(child);
		parent.getReferences().add(child2);
		final TestElement newChild = Create.testElement();
		final TestElement newChild2 = Create.testElement();

		final MergeCase mc = newMergeCase(parent, child, child2, newChild, newChild2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).getReferences().set(0, mc.getMyItem(newChild));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(parent).getReferences().set(1, mc.getTheirItem(newChild2));
			}
		}.run(false);

		mc.hasConflict(null);
	}
}