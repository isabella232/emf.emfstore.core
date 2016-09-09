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

import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.ReferenceConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.SingleReferenceConflict;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.SingleReferenceOperation;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.Test;

public class SingleReferenceMergeTest extends MergeTest {

	private static final String GET_NEW_VALUE = "getNewValue"; //$NON-NLS-1$
	private static final String THEIR_LINK = "theirLink"; //$NON-NLS-1$
	private static final String MY_LINK = "myLink"; //$NON-NLS-1$
	private static final String TARGET = "target"; //$NON-NLS-1$

	@Test
	public void setSameTarget() {
		final TestElement target = Create.testElement(TARGET);
		final TestElement myLink = Create.testElement(MY_LINK);
		final TestElement theirLink = Create.testElement(THEIR_LINK);

		final MergeCase mc = newMergeCase(target, myLink, theirLink);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(target).setReference(myLink);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(target).setReference(mc.getTheirItem(theirLink));
			}
		}.run(false);

		mc.hasConflict(SingleReferenceConflict.class)
			// My
			.myIs(SingleReferenceOperation.class).andReturns(GET_NEW_VALUE, getId(myLink)).andNoOtherMyOps()
			// Theirs
			.theirsIs(SingleReferenceOperation.class).andReturns(GET_NEW_VALUE, getId(theirLink)).andNoOtherTheirOps();
	}

	@Test
	public void setSameDifferentTarget() {
		final TestElement target = Create.testElement();
		final TestElement secondTarget = Create.testElement();
		final TestElement link = Create.testElement();

		final MergeCase mc = newMergeCase(target, secondTarget, link);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(target).setReference(link);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(secondTarget).setReference(mc.getTheirItem(link));
			}
		}.run(false);

		mc.hasConflict(null);
	}

	@Test
	public void setSameTargetWithNoise() {
		final TestElement target = Create.testElement();
		final TestElement decoy = Create.testElement();
		final TestElement myLink = Create.testElement();
		final TestElement theirLink = Create.testElement();

		final MergeCase mc = newMergeCase(target, decoy, myLink, theirLink);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(decoy).setReference(myLink);
				mc.getMyItem(target).setReference(myLink);
				mc.getMyItem(decoy).setOtherReference(myLink);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(target).setOtherReference(mc.getTheirItem(theirLink));
				mc.getTheirItem(target).setReference(mc.getTheirItem(theirLink));
			}
		}.run(false);

		mc.hasConflict(SingleReferenceConflict.class)
			// My
			.myIs(SingleReferenceOperation.class).andReturns(GET_NEW_VALUE, getId(myLink)).andNoOtherMyOps()
			// Theirs
			.theirsIs(SingleReferenceOperation.class).andReturns(GET_NEW_VALUE, getId(theirLink)).andNoOtherTheirOps();
	}

	/**
	 * CONTAINMENT TESTs
	 */

	@Test
	public void setSameTargetContainment() {
		final TestElement target = Create.testElement(TARGET);
		final TestElement myLink = Create.testElement(MY_LINK);
		final TestElement theirLink = Create.testElement(THEIR_LINK);

		final MergeCase mc = newMergeCase(target, myLink, theirLink);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(target).setContainedElement(myLink);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(target).setContainedElement(mc.getTheirItem(theirLink));
			}
		}.run(false);

		mc.hasConflict(ReferenceConflict.class)
			// My
			.myIs(SingleReferenceOperation.class).andReturns(GET_NEW_VALUE, getId(myLink)).andNoOtherMyOps()
			// Theirs
			.theirsIs(SingleReferenceOperation.class).andReturns(GET_NEW_VALUE, getId(theirLink)).andNoOtherTheirOps();
	}

	@Test
	public void setToDifferentParents() {
		final TestElement parent = Create.testElement();
		final TestElement secondParent = Create.testElement();
		final TestElement child = Create.testElement();

		final MergeCase mc = newMergeCase(parent, secondParent, child);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).setContainedElement(child);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(secondParent).setContainedElement(mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(ReferenceConflict.class)
			//
			.myIs(SingleReferenceOperation.class).andReturns(GET_NEW_VALUE, getId(child)).andNoOtherMyOps()
			// Theirs
			.theirsIs(SingleReferenceOperation.class).andReturns(GET_NEW_VALUE, getId(child)).andNoOtherTheirOps();
	}

	@Test
	public void setToParentAndGrandparent() {
		final TestElement grandParent = Create.testElement();
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();

		final MergeCase mc = newMergeCase(grandParent, parent, child);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent).setContainedElement(child);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(grandParent).setContainedElement(mc.getTheirItem(parent));
			}
		}.run(false);

		mc.hasConflict(null);
	}

}