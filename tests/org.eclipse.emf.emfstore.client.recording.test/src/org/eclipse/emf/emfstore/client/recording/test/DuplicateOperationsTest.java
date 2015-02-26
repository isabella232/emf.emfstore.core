/*******************************************************************************
 * Copyright (c) 2011-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.recording.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.client.callbacks.ESUpdateCallback;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTest;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.controller.UpdateController;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESWorkspaceImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.CloseableIterable;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Versions;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsFactory;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.junit.Test;

import com.google.common.collect.Iterables;

/**
 * @author emueller
 *
 */
public class DuplicateOperationsTest extends ESTest {

	private static final String A = "A"; //$NON-NLS-1$
	private static final String B = "B"; //$NON-NLS-1$
	private static final String C = "C"; //$NON-NLS-1$
	private static final String TRUNK = "trunk"; //$NON-NLS-1$

	private UpdateController createDummyUpdateController() {
		final ProjectSpaceBase p = (ProjectSpaceBase) getProjectSpace();
		final Usersession u = org.eclipse.emf.emfstore.internal.client.model.ModelFactory.eINSTANCE.createUsersession();
		final ESWorkspaceImpl workspace = (ESWorkspaceImpl) ESWorkspaceProvider.INSTANCE.getWorkspace();
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				workspace.toInternalAPI().getUsersessions().add(u);
				p.setUsersession(u);
				p.setBaseVersion(Versions.createPRIMARY(TRUNK, 0));
				return null;
			}
		});
		return new UpdateController(p, null,
			ESUpdateCallback.NOCALLBACK, new NullProgressMonitor());
	}

	private static AbstractOperation createOperation(String identifer) {
		final AttributeOperation dummyOp = OperationsFactory.eINSTANCE.createAttributeOperation();
		dummyOp.setIdentifier(identifer);
		return dummyOp;
	}

	private static ChangePackage createChangePackage(AbstractOperation... ops) {
		final ChangePackage changePackage = VersioningFactory.eINSTANCE
			.createChangePackage();
		changePackage.getOperations().addAll(Arrays.asList(ops));
		return changePackage;
	}

	@Test
	public void testConflictingChangePackage() throws ESException {
		final UpdateController updateController = createDummyUpdateController();
		// final AbstractOperation a = createOperation(A);
		// final AbstractOperation b = createOperation(B);

		Add.toProject(getLocalProject(), Create.testElement());
		Add.toProject(getLocalProject(), Create.testElement());

		final ChangePackage cp = VersioningFactory.eINSTANCE.createChangePackage();
		for (final AbstractOperation operation : getProjectSpace().changePackage().operations().iterable()) {
			cp.getOperations().add(operation);
		}

		final boolean hasBeenRemoved = updateController
			.removeDuplicateOperations(cp, getProjectSpace().changePackage());
		assertEquals(0, getProjectSpace().changePackage().size());
		assertTrue(hasBeenRemoved);
	}

	@Test
	public void testConflictingChangePackageWithMoreOpsThanLocal() throws ESException {
		final UpdateController updateController = createDummyUpdateController();

		Add.toProject(getLocalProject(), Create.testElement("foo"));
		Add.toProject(getLocalProject(), Create.testElement("bar"));
		Add.toProject(getLocalProject(), Create.testElement("baz"));

		final ChangePackage cp = VersioningFactory.eINSTANCE.createChangePackage();
		final CloseableIterable<AbstractOperation> operations = getProjectSpace().changePackage().operations();
		final Iterable<AbstractOperation> operationIterable = operations.iterable();
		final AbstractOperation firstOp = Iterables.get(operationIterable, 0);
		final AbstractOperation secondOp = Iterables.get(operationIterable, 0);
		cp.add(ModelUtil.clone(firstOp));
		cp.add(ModelUtil.clone(secondOp));
		operations.close();

		final Iterable<AbstractOperation> iterable = getProjectSpace().changePackage().operations().iterable();

		final List<ESChangePackage> incoming = new ArrayList<ESChangePackage>();
		incoming.add(cp.toAPI());

		final boolean hasBeenRemoved = updateController
			.removeDuplicateOperations(cp, getProjectSpace().changePackage());
		assertEquals(1, getProjectSpace().changePackage().size());
		assertTrue(hasBeenRemoved);
	}

	@Test
	public void testNonConflictingChangePackage() throws ESException {
		final UpdateController updateController = createDummyUpdateController();
		final AbstractOperation a = createOperation(A);
		final AbstractOperation b = createOperation(B);
		final AbstractOperation c = createOperation(C);
		final ChangePackage cp = createChangePackage(a, b);
		final ChangePackage localCP = createChangePackage(c);
		final boolean hasBeenRemoved = updateController.removeDuplicateOperations(cp, localCP);
		assertFalse(hasBeenRemoved);
	}

	@Test
	public void testNoLocalChanges() throws ESException {
		final UpdateController updateController = createDummyUpdateController();
		final AbstractOperation a = createOperation(A);
		final AbstractOperation b = createOperation(B);
		final ChangePackage cp = createChangePackage(a, b);
		final ChangePackage localCP = createChangePackage();
		final boolean hasBeenRemoved = updateController.removeDuplicateOperations(cp, localCP);
		assertFalse(hasBeenRemoved);
	}

	@Test(expected = IllegalStateException.class)
	public void testIllegalIncomingChangePackage() throws ESException {
		final UpdateController updateController = createDummyUpdateController();
		final AbstractOperation a = createOperation(A);
		final AbstractOperation b = createOperation(B);
		final AbstractOperation c = createOperation(C);
		final ChangePackage cp = createChangePackage(a, b, c);
		final ChangePackage localCP = createChangePackage(
			ModelUtil.clone(a), ModelUtil.clone(b));
		updateController.removeDuplicateOperations(cp, localCP);
	}

	@Test(expected = IllegalStateException.class)
	public void testIllegalLocalChangePackage() throws ESException {
		final UpdateController updateController = createDummyUpdateController();
		final AbstractOperation a = createOperation(A);
		final AbstractOperation b = createOperation(B);
		final AbstractOperation c = createOperation(C);
		final ChangePackage cp = createChangePackage(a, b);
		final ChangePackage localCP = createChangePackage(
			ModelUtil.clone(a), c);
		updateController.removeDuplicateOperations(cp, localCP);
	}

	@Test
	public void testRemoveChangePackage() throws ESException {
		final UpdateController updateController = createDummyUpdateController();
		Add.toProject(getLocalProject(), Create.testElement());
		Add.toProject(getLocalProject(), Create.testElement());

		final ChangePackage changePackage = VersioningFactory.eINSTANCE.createChangePackage();
		final CloseableIterable<AbstractOperation> operations = getProjectSpace().changePackage().operations();
		for (final AbstractOperation op : operations.iterable()) {
			changePackage.getOperations().add(op);
		}
		operations.close();

		final List<ESChangePackage> incoming = new ArrayList<ESChangePackage>();
		incoming.add(changePackage.toAPI());
		final int delta = updateController.removeFromChangePackages(incoming);
		assertEquals(1, delta);
		assertEquals(0, getProjectSpace().changePackage().size());
		assertEquals(0, incoming.size());
	}

	@Test
	public void testRemoveChangePackageWithMoreIncomingChanges() throws ESException {
		final UpdateController updateController = createDummyUpdateController();

		Add.toProject(getLocalProject(), Create.testElement());
		Add.toProject(getLocalProject(), Create.testElement());

		final ChangePackage cp = VersioningFactory.eINSTANCE.createChangePackage();
		final CloseableIterable<AbstractOperation> operations = getProjectSpace().changePackage().operations();
		final Iterable<AbstractOperation> iterable = operations.iterable();
		final AbstractOperation firstOp = Iterables.get(iterable, 0);
		final AbstractOperation secondOp = Iterables.get(iterable, 0);
		final AbstractOperation thirdOp = createOperation("fooOp");
		operations.close();
		cp.getOperations().add(firstOp);
		cp.getOperations().add(secondOp);

		final ChangePackage cp2 = VersioningFactory.eINSTANCE.createChangePackage();
		cp2.getOperations().add(thirdOp);

		final List<ESChangePackage> incoming = new ArrayList<ESChangePackage>();
		incoming.add(cp.toAPI());
		incoming.add(cp2.toAPI());

		final int delta = updateController.removeFromChangePackages(
			incoming);

		assertEquals(1, delta);
		assertEquals(0, getProjectSpace().changePackage().size());
		assertEquals(1, incoming.size());
	}

}
