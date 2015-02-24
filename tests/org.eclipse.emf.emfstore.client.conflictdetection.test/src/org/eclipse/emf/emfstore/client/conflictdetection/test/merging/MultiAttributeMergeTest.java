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

import java.util.Arrays;

import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.MultiAttributeConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.MultiAttributeSetConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.MultiAttributeSetSetConflict;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeSetOperation;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.Test;

/**
 * Merge testcases for {@link MultiAttributeOperation} and {@link MultiAttributeSetOperation}.
 * 
 * @author wesendon
 */
public class MultiAttributeMergeTest extends MergeTest {

	private static final String Y2 = "Y"; //$NON-NLS-1$
	private static final String GET_REFERENCED_VALUES = "getReferencedValues"; //$NON-NLS-1$
	private static final String GET_NEW_VALUE = "getNewValue"; //$NON-NLS-1$
	private static final String GET_INDEX = "getIndex"; //$NON-NLS-1$
	private static final String Y = "y"; //$NON-NLS-1$
	private static final String X2 = "x"; //$NON-NLS-1$
	private static final String IS_ADD = "isAdd"; //$NON-NLS-1$
	private static final String X = "X"; //$NON-NLS-1$
	private static final String C = "c"; //$NON-NLS-1$
	private static final String B = "b"; //$NON-NLS-1$
	private static final String A = "a"; //$NON-NLS-1$

	@Test
	public void addFirstVsRemove() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().add(1, X);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().remove(1);
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeConflict.class)
			// My
			.myIs(MultiAttributeOperation.class).andReturns(IS_ADD, true)
			// Theirs
			.theirsIs(MultiAttributeOperation.class).andReturns(IS_ADD, false);
	}

	@Test
	public void addVsRemove() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().add(X);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().remove(1);
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeConflict.class)
			// My
			.myIs(MultiAttributeOperation.class).andReturns(IS_ADD, true)
			// Theirs
			.theirsIs(MultiAttributeOperation.class).andReturns(IS_ADD, false);
	}

	@Test
	public void addManyVsRemove() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().addAll(1, Arrays.asList(X2, Y));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().remove(1);
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeConflict.class)
			// My
			.myIs(MultiAttributeOperation.class).andReturns(IS_ADD, true)
			// Theirs
			.theirsIs(MultiAttributeOperation.class).andReturns(IS_ADD, false);
	}

	@Test
	public void addManyVsRemoveMany() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().addAll(1, Arrays.asList(X2, Y));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().removeAll(Arrays.asList(B, C));
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeConflict.class)
			// My
			.myIs(MultiAttributeOperation.class).andReturns(IS_ADD, true)
			// Theirs
			.theirsIs(MultiAttributeOperation.class).andReturns(IS_ADD, false);
	}

	@Test
	public void removeVsRemove() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().remove(1);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().remove(2);
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeConflict.class)
			// My
			.myIs(MultiAttributeOperation.class).andReturns(IS_ADD, false)
			// Theirs
			.theirsIs(MultiAttributeOperation.class).andReturns(IS_ADD, false);
	}

	@Test
	public void removeVsRemoveSameNc() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().remove(1);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().remove(1);
			}
		}.run(false);

		// TODO: false positive, fix later
		mergeCase.hasConflict(MultiAttributeConflict.class);
	}

	@Test
	public void removeManyVsRemoveMany() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().removeAll(Arrays.asList(A, B));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().removeAll(Arrays.asList(B, C));
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeConflict.class)
			// My
			.myIs(MultiAttributeOperation.class).andReturns(IS_ADD, false)
			// Theirs
			.theirsIs(MultiAttributeOperation.class).andReturns(IS_ADD, false);
	}

	/**
	 * Remove and Set on the same element.
	 */
	@Test
	public void removeVsSet() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().remove(1);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().set(1, X);
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeSetConflict.class)
			// My
			.myIs(MultiAttributeOperation.class).andReturns(IS_ADD, false)
			// Theirs
			.theirsIs(MultiAttributeSetOperation.class).andReturns(GET_INDEX, 1).andReturns(GET_NEW_VALUE, X);
	}

	/**
	 * Remove on a lower remove index than Set.
	 */
	@Test
	public void removeVsSetLowerIndex() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().remove(0);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().set(1, X);
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeSetConflict.class)
			// My
			.myIs(MultiAttributeOperation.class).andReturns(IS_ADD, false)
			// Theirs
			.theirsIs(MultiAttributeSetOperation.class).andReturns(GET_INDEX, 1).andReturns(GET_NEW_VALUE, X);
	}

	/**
	 * Remove on a higher remove index than Set. That's a conflict.
	 */
	@Test
	public void removeVsSetHigherIndexNC() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().remove(1);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().set(0, X);
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeSetConflict.class);
	}

	/**
	 * Remove multiple elements individually vs. Set. Individually removing causes multiple remove operations.
	 */
	@Test
	public void multipleRemoveVsSet() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().remove(1);
				mergeCase.getMyItem(element).getStrings().remove(1);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().set(1, X);
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeSetConflict.class)
			// My first
			.myIs(MultiAttributeOperation.class).andReturns(IS_ADD, false)
			// My Second
			.myOtherContains(MultiAttributeOperation.class).andReturns(IS_ADD, false).andNoOtherMyOps()
			// Their
			.theirsIs(MultiAttributeSetOperation.class).andReturns(GET_INDEX, 1).andReturns(GET_NEW_VALUE, X)
			.andNoOtherTheirOps();
	}

	/**
	 * Remove multiple elements in one operation vs Set.
	 */
	@Test
	public void multiRemoveVsSet() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().removeAll(asList(B, C));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().set(1, X);
			}
		}.run(false);

		mergeCase
			.hasConflict(MultiAttributeSetConflict.class)
			// My first
			.myIs(MultiAttributeOperation.class).andReturns(IS_ADD, false)
			.andReturns(GET_REFERENCED_VALUES, asList(B, C)).andNoOtherMyOps()
			// Their
			.theirsIs(MultiAttributeSetOperation.class).andReturns(GET_INDEX, 1).andReturns(GET_NEW_VALUE, X)
			.andNoOtherTheirOps();
	}

	/**
	 * Remove multiple elements in one operation vs Set, with a lower remove index.
	 */
	@Test
	public void multiRemoveVsSetLowerIndex() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().removeAll(asList(A, B));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().set(2, X);
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeSetConflict.class)
			// My first
			.myIs(MultiAttributeOperation.class).andReturns(IS_ADD, false)
			.andReturns(GET_REFERENCED_VALUES, asList(A, B)).andNoOtherMyOps()
			// Their
			.theirsIs(MultiAttributeSetOperation.class).andReturns(GET_NEW_VALUE, X).andNoOtherTheirOps();
	}

	/**
	 * Remove multiple elements in one operation vs Set, with a higher remove index. (NC)
	 */
	@Test
	public void multiRemoveVsSetHigherIndexNC() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().removeAll(asList(B, C));
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().set(0, X);
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeSetConflict.class);
	}

	@Test
	public void setVsSet() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().set(1, Y2);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().set(1, X);
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeSetSetConflict.class)
			// My
			.myIs(MultiAttributeSetOperation.class).andReturns(GET_NEW_VALUE, Y2)
			// Theirs
			.theirsIs(MultiAttributeSetOperation.class).andReturns(GET_NEW_VALUE, X);
	}

	@Test
	public void setVsSetNC() {
		final TestElement element = Create.testElement();
		element.getStrings().add(A);
		element.getStrings().add(B);
		element.getStrings().add(C);

		final MergeCase mergeCase = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getMyItem(element).getStrings().set(1, Y2);
			}
		}.run(false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mergeCase.getTheirItem(element).getStrings().set(2, X);
			}
		}.run(false);

		mergeCase.hasConflict(MultiAttributeSetSetConflict.class);
	}
}
