/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.conflictdetection.test.merging;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.emf.emfstore.client.test.common.cases.ESTest;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.ChangeConflictException;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ChangeConflictSet;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictBucket;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictDetector;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.Test;

/**
 * @author emueller
 *
 */
public class MergeResolvedConflictsWithSyntheticOpTest extends ESTest {

	private static final String BLUB = "blub"; //$NON-NLS-1$
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String BAR = "bar"; //$NON-NLS-1$
	private static final String BAR2 = "bar2"; //$NON-NLS-1$
	private static final String FOO = "foo"; //$NON-NLS-1$
	private static final String NAME = "name"; //$NON-NLS-1$

	@Test
	public void testMerge() throws ChangeConflictException {

		final TestElement testElement = Create.testElement();
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				getProject().getModelElements().add(testElement);
				return null;
			}
		});
		final ModelElementId modelElementId = getProject().getModelElementId(testElement);

		final AbstractChangePackage myChangePackage = Create.changePackage();
		final AbstractChangePackage theirChangePackage = Create.changePackage();

		final AttributeOperation myAttributeOp = Create.attributeOp();
		myAttributeOp.setOldValue(FOO);
		myAttributeOp.setNewValue(BAR);
		myAttributeOp.setFeatureName(NAME);
		myAttributeOp.setModelElementId(ModelUtil.clone(modelElementId));

		final AttributeOperation theirAttributeOp = Create.attributeOp();
		theirAttributeOp.setOldValue(FOO);
		theirAttributeOp.setNewValue(BAR2);
		theirAttributeOp.setFeatureName(NAME);
		theirAttributeOp.setModelElementId(ModelUtil.clone(modelElementId));

		myChangePackage.add(myAttributeOp);
		theirChangePackage.add(theirAttributeOp);

		final List<AbstractChangePackage> myChangePackages = Arrays.asList(myChangePackage);
		final List<AbstractChangePackage> theirChangePackages = Arrays.asList(theirChangePackage);

		final ConflictDetector conflictDetector = new ConflictDetector();
		final ChangeConflictSet conflictSet = conflictDetector.calculateConflicts(
			myChangePackages,
			theirChangePackages,
			getProject());
		final ConflictBucket next = conflictSet.getConflictBuckets().iterator().next();

		// fake operation during merge
		final AttributeOperation generatedAttributeOp = Create.attributeOp();
		generatedAttributeOp.setFeatureName(DESCRIPTION);
		generatedAttributeOp.setNewValue(BLUB);
		generatedAttributeOp.setModelElementId(ModelUtil.clone(modelElementId));

		final Set<AbstractOperation> acceptedLocal = new LinkedHashSet<AbstractOperation>();
		final Set<AbstractOperation> rejectedTheirs = new LinkedHashSet<AbstractOperation>();
		acceptedLocal.add(myAttributeOp);
		acceptedLocal.add(generatedAttributeOp);
		rejectedTheirs.add(theirAttributeOp);
		next.resolveConflict(acceptedLocal, rejectedTheirs);

		// myChangePackage.getOperations().add(generatedAttributeOp);

		final AbstractChangePackage mergeResolvedConflicts = getProjectSpace().mergeResolvedConflicts(
			conflictSet,
			myChangePackages,
			theirChangePackages);

		assertTrue(containsOperations(mergeResolvedConflicts, generatedAttributeOp));
	}

	private static boolean containsOperations(AbstractChangePackage changePackage, AbstractOperation operation) {
		final ESCloseableIterable<AbstractOperation> operations = changePackage.operations();
		try {
			for (final AbstractOperation op : operations.iterable()) {
				if (op.equals(operation)) {
					return true;
				}
			}
		} finally {
			operations.close();
		}

		return false;
	}
}
