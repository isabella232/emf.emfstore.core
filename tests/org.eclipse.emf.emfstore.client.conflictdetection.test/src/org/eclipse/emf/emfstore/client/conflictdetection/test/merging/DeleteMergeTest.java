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

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.DeletionConflict;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CreateDeleteOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.SingleReferenceOperation;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.Test;

public class DeleteMergeTest extends MergeTest {

	private static final String CHILD3 = "child3"; //$NON-NLS-1$
	private static final String CHILD2 = "child2"; //$NON-NLS-1$
	private static final String CHILD = "child"; //$NON-NLS-1$
	private static final String PARENT = "parent"; //$NON-NLS-1$
	private static final String JA = "Ja."; //$NON-NLS-1$
	private static final String BLUB = "Blub"; //$NON-NLS-1$

	@Test
	public void attVsDel() {
		final TestElement element = Create.testElement();

		final MergeCase mc = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(element).setName(BLUB);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				EcoreUtil.delete(mc.getTheirItem(element));
			}
		}.run(false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(AttributeOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class).andNoOtherTheirOps();
	}

	@Test
	public void multiAttVsDel() {
		final TestElement element = Create.testElement();

		final MergeCase mc = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(element).getStrings().add(BLUB);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				EcoreUtil.delete(mc.getTheirItem(element));
			}
		}.run(false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(MultiAttributeOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class).andNoOtherTheirOps();
	}

	@Test
	public void attVsDelDifferentNC() {
		final TestElement element = Create.testElement();
		final TestElement element2 = Create.testElement();

		final MergeCase mc = newMergeCase(element, element2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(element).setName(BLUB);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				EcoreUtil.delete(mc.getTheirItem(element2));
			}
		}.run(false);

		mc.hasConflict(null);
	}

	@Test
	public void singleVsDel() {
		final TestElement element = Create.testElement();
		final TestElement link = Create.testElement();

		final MergeCase mc = newMergeCase(element, link);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(element).setReference(mc.getMyItem(link));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				EcoreUtil.delete(mc.getTheirItem(element));
			}
		}.run(false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(SingleReferenceOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class).andNoOtherTheirOps();
	}

	@Test
	public void multiRefVsDel() {
		final TestElement element = Create.testElement();
		final TestElement link = Create.testElement();

		final MergeCase mc = newMergeCase(element, link);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(element).getReferences().add(mc.getMyItem(link));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				EcoreUtil.delete(mc.getTheirItem(element));
			}
		}.run(false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(MultiReferenceOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class).andNoOtherTheirOps();
	}

	@Test
	public void multiRefContainmentVsDel() {
		final TestElement parent = Create.testElement();
		final TestElement parent2 = Create.testElement();
		final TestElement child = Create.testElement();
		parent.getContainedElements().add(child);

		final MergeCase mc = newMergeCase(parent, parent2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent2).getContainedElements().add(mc.getMyItem(child));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				EcoreUtil.delete(mc.getTheirItem(parent));
			}
		}.run(false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(CompositeOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class).andNoOtherTheirOps();
	}

	@Test
	public void attVsDelInSteps() {
		final TestElement parent = Create.testElement();
		final TestElement child = Create.testElement();
		final TestElement child2 = Create.testElement();
		parent.getContainedElements().add(child);
		child.getContainedElements().add(child2);

		final MergeCase mc = newMergeCase(parent);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(child2).setName(JA);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(child).getContainedElements().remove(mc.getTheirItem(child2));
				mc.getTheirItem(parent).getContainedElements().remove(mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(AttributeOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class);
	}

	@Test
	public void attVsDelIndirectInSteps() {
		final TestElement parent = Create.testElement(PARENT);
		final TestElement child = Create.testElement(CHILD);
		final TestElement child2 = Create.testElement(CHILD2);
		final TestElement child3 = Create.testElement(CHILD3);
		parent.getContainedElements().add(child);
		child.getContainedElements().add(child2);
		child2.getContainedElements().add(child3);

		final MergeCase mc = newMergeCase(parent);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(child3).setName(JA);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(child).getContainedElements().remove(mc.getTheirItem(child2));
				mc.getTheirItem(parent).getContainedElements().remove(mc.getTheirItem(child));
			}
		}.run(false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(AttributeOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class);
	}
}