/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.server;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import junit.framework.Assert;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.callbacks.ESCommitCallback;
import org.eclipse.emf.emfstore.client.callbacks.ESUpdateCallback;
import org.eclipse.emf.emfstore.client.test.server.api.CoreServerTest;
import org.eclipse.emf.emfstore.client.test.testmodel.TestElement;
import org.eclipse.emf.emfstore.client.test.testmodel.TestmodelFactory;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.common.model.ESModelElementIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.ChecksumErrorHandler;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Versions;
import org.eclipse.emf.emfstore.server.ESConflictSet;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.junit.After;
import org.junit.Test;

public class ChecksumTest extends CoreServerTest {

	@Override
	@After
	public void teardown() throws IOException, SerializationException, ESException {
		super.teardown();
		Configuration.getClientBehavior().setChecksumErrorHandler(null);
	}

	@Test
	public void testRevert() throws SerializationException {
		// createTestElement automatically adds the element to the project
		final TestElement table = createTestElement("A");
		// final TestElement b = createTestElement("B");

		final TestElement value = createTestElement("value");
		final TestElement attributeName = createTestElement("attributeName");
		final TestElement attribute = createTestElement("attribute");
		attribute.getContainedElements().add(value);

		table.getElementMap().put(attributeName, value);
		// d.getReferences().add(attributeName);
		// attributeName.getReferences().add(b);

		// getProject().getModelElements().add(b);
		getProject().getModelElements().add(table);

		long computeChecksum = ModelUtil.computeChecksum(getProject());
		clearOperations();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				// getProjectSpace().getOperationManager().beginCompositeOperation();
				getProject().getModelElements().remove(attribute);
				// getProjectSpace().getOperationManager().endCompositeOperation();
			}
		}.run(getProject(), false);

		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				// getProjectSpace().getOperationManager().beginCompositeOperation();
				getProjectSpace().revert();
				// getProjectSpace().getOperationManager().endCompositeOperation();
				return null;
			}
		}, getProject());

		long checksum = ModelUtil.computeChecksum(getProject());

		Assert.assertEquals(computeChecksum, checksum);
	}

	@Test
	public void testOrderOfRootElementsInvariance() throws SerializationException {
		// createTestElement automatically adds the element to the project
		final TestElement a = createTestElement("A");
		final TestElement b = createTestElement("B");

		getProject().getModelElements().add(a);
		getProject().getModelElements().add(b);

		long computeChecksum = ModelUtil.computeChecksum(getProject());

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				getProject().getModelElements().clear();
				getProject().getModelElements().add(b);
				getProject().getModelElements().add(a);
			}
		}.run(getProject(), false);

		long checksum = ModelUtil.computeChecksum(getProject());

		Assert.assertEquals(computeChecksum, checksum);
	}

	@Test
	public void testAutocorrectErrorHandlerAtCommit() throws ESException, SerializationException {

		Assert.assertEquals(1, ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().size());

		Configuration.getClientBehavior().setChecksumErrorHandler(ChecksumErrorHandler.AUTOCORRECT);

		final TestElement testElement = createTestElement();
		getProject().addModelElement(testElement);
		share(getProjectSpace());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				testElement.setName("A");
			}
		}.run(getProject(), false);

		long expectedChecksum = ModelUtil.computeChecksum(getProject());
		Project clonedProject = ModelUtil.clone(getProject());

		getProjectSpace().getOperationManager().stopChangeRecording();
		testElement.setName("B");
		getProjectSpace().getOperationManager().startChangeRecording();

		Assert.assertEquals(1, getProjectSpace().getOperations().size());

		// re-checkout should be triggered
		PrimaryVersionSpec commit = commitWithoutCommand(getProjectSpace());
		Assert.assertEquals(1, ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().size());

		Project restoredProject = ESWorkspaceProviderImpl.getInstance().getWorkspace().toInternalAPI()
			.getProjectSpaces()
			.get(0).getProject();
		long computedChecksum = ModelUtil.computeChecksum(restoredProject);

		Assert.assertTrue(ModelUtil.areEqual(restoredProject, clonedProject));
		Assert.assertEquals(expectedChecksum, commit.getProjectStateChecksum());
		Assert.assertEquals(commit.getProjectStateChecksum(), computedChecksum);
	}

	@Test
	public void testChangeTrackingAfterAutocorrectErrorHandler() throws ESException, SerializationException {

		Assert.assertEquals(1, ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().size());

		Configuration.getClientBehavior().setChecksumErrorHandler(ChecksumErrorHandler.AUTOCORRECT);
		((ESWorkspaceProviderImpl) ESWorkspaceProviderImpl.INSTANCE).setConnectionManager(getConnectionMock());

		final TestElement testElement = createTestElement();
		share(getProjectSpace());

		ESLocalProject checkout = getProjectSpace()
			.toAPI()
			.getRemoteProject()
			.checkout(
				"testCheckout",
				getProjectSpace().getUsersession().toAPI(),
				getProjectSpace().resolveVersionSpec(Versions.createHEAD(), new NullProgressMonitor()).toAPI(),
				new NullProgressMonitor());
		final ProjectSpace checkedOutProjectSpace = ((ESLocalProjectImpl) checkout).toInternalAPI();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(testElement);
				testElement.setName("A");
			}
		}.run(getProject(), false);

		commitWithoutCommand(getProjectSpace());

		Assert.assertEquals(0, getProjectSpace().getOperations().size());

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
				testElement.setName("B");
				checkedOutProjectSpace.getProject().addModelElement(testElement);
			}
		}.run(getProject(), false);
		update(checkedOutProjectSpace);
		commitWithoutCommand(checkedOutProjectSpace);

		Assert.assertEquals(0, checkedOutProjectSpace.getOperations().size());
		Assert.assertEquals(0, getProjectSpace().getOperations().size());

		// this is the operation we would like to keep
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				testElement.setName("B");
			}
		}.run(getProject(), false);

		getProjectSpace().getOperationManager().stopChangeRecording();
		testElement.setName("C");
		getProjectSpace().getOperationManager().startChangeRecording();

		Assert.assertEquals(1, getProjectSpace().getOperations().size());

		// cancel should be triggered via exception
		getProjectSpace().update(Versions.createHEAD(getProjectSpace().getBaseVersion()), new MyUpdateCallback(),
			new NullProgressMonitor());

		Assert.assertEquals(1, getProjectSpace().getOperations().size());
	}

	@Test(expected = ESException.class)
	public void testCancelErrorHandlerAtCommit() throws ESException, SerializationException {

		Assert.assertEquals(1, ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().size());

		Configuration.getClientBehavior().setChecksumErrorHandler(ChecksumErrorHandler.CANCEL);

		final TestElement testElement = createTestElement();
		share(getProjectSpace());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(testElement);
				testElement.setName("A");
			}
		}.run(getProject(), false);

		getProjectSpace().getOperationManager().stopChangeRecording();
		testElement.setName("B");
		getProjectSpace().getOperationManager().startChangeRecording();

		// cancel should be triggered
		commitWithoutCommand(getProjectSpace());
	}

	@Test(expected = ESException.class)
	public void testCancelErrorHandlerAtUpdateAfterOneCommit() throws ESException, SerializationException {

		Assert.assertEquals(1, ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().size());

		Configuration.getClientBehavior().setChecksumErrorHandler(ChecksumErrorHandler.CANCEL);

		final TestElement testElement = createTestElement();
		share(getProjectSpace());

		ESLocalProject checkout = getProjectSpace().toAPI().getRemoteProject()
			.checkout(
				"testCheckout",
				getProjectSpace().getUsersession().toAPI(),
				getProjectSpace().resolveVersionSpec(Versions.createHEAD(), new NullProgressMonitor()).toAPI(),
				new NullProgressMonitor());
		final ProjectSpace checkedOutProjectSpace = ((ESLocalProjectImpl) checkout).toInternalAPI();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(testElement);
				testElement.setName("A");
			}
		}.run(getProject(), false);

		commitWithoutCommand(getProjectSpace());

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				checkedOutProjectSpace.getProject().addModelElement(createTestElement("B"));
			}
		}.run(getProject(), false);
		update(checkedOutProjectSpace);
		commitWithoutCommand(checkedOutProjectSpace);

		getProjectSpace().getOperationManager().stopChangeRecording();
		testElement.setName("B");
		getProjectSpace().getOperationManager().startChangeRecording();

		// cancel should be triggered via exception
		getProjectSpace().update(Versions.createHEAD(getProjectSpace().getBaseVersion()), new MyUpdateCallback(),
			new NullProgressMonitor());
	}

	@Test
	public void testCorrectChecksumsAtUpdate() throws ESException, SerializationException {

		Assert.assertEquals(1, ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().size());

		Configuration.getClientBehavior().setChecksumErrorHandler(ChecksumErrorHandler.CANCEL);

		final TestElement testElement = createTestElement();
		share(getProjectSpace());

		ESLocalProject checkout = getProjectSpace().toAPI().getRemoteProject()
			.checkout(
				"testCheckout",
				getProjectSpace().getUsersession().toAPI(),
				getProjectSpace().resolveVersionSpec(Versions.createHEAD(), new NullProgressMonitor()).toAPI(),
				new NullProgressMonitor());
		final ProjectSpace checkedOutProjectSpace = ((ESLocalProjectImpl) checkout).toInternalAPI();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(testElement);
				testElement.setName("A");
			}
		}.run(getProject(), false);

		commitWithoutCommand(getProjectSpace());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				checkedOutProjectSpace.getProject().addModelElement(TestmodelFactory.eINSTANCE.createTestElement());
			}
		}.run(getProject(), false);
		update(checkedOutProjectSpace);
		commitWithoutCommand(checkedOutProjectSpace);

		PrimaryVersionSpec update = update(getProjectSpace());
		Assert.assertTrue(ModelUtil.areEqual(getProject(), checkedOutProjectSpace.getProject()));
		Assert.assertEquals(ModelUtil.computeChecksum(getProject()), update.getProjectStateChecksum());
	}

	@Test
	public void testCorruptChecksumsAtUpdateWithLocalOperation() throws ESException, SerializationException {

		Assert.assertEquals(1, ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().size());

		Configuration.getClientBehavior().setChecksumErrorHandler(ChecksumErrorHandler.AUTOCORRECT);

		final TestElement testElement = createTestElement();
		share(getProjectSpace());

		ESLocalProject checkout = getProjectSpace().toAPI().getRemoteProject()
			.checkout(
				"testCheckout",
				getProjectSpace().getUsersession().toAPI(),
				getProjectSpace().resolveVersionSpec(Versions.createHEAD(), new NullProgressMonitor()).toAPI(),
				new NullProgressMonitor());
		final ProjectSpace checkedOutProjectSpace = ((ESLocalProjectImpl) checkout).toInternalAPI();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(testElement);
				testElement.setName("A");
			}
		}.run(getProject(), false);

		commitWithoutCommand(getProjectSpace());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				checkedOutProjectSpace.getProject().addModelElement(TestmodelFactory.eINSTANCE.createTestElement());
			}
		}.run(getProject(), false);

		update(checkedOutProjectSpace);
		commitWithoutCommand(checkedOutProjectSpace);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				testElement.setName("B");
			}
		}.run(getProject(), false);

		getProjectSpace().getOperationManager().stopChangeRecording();
		testElement.setName("C");
		getProjectSpace().getOperationManager().startChangeRecording();

		Assert.assertEquals(1, getProjectSpace().getOperations().size());

		// autocorrect should be triggered, will fail
		update(getProjectSpace());
		Assert.assertEquals(1, getProjectSpace().getOperations().size());
	}

	private class MyCommitCallback implements ESCommitCallback {

		public boolean baseVersionOutOfDate(ESLocalProject projectSpace, IProgressMonitor progressMonitor) {
			return ESCommitCallback.NOCALLBACK.baseVersionOutOfDate(projectSpace, progressMonitor);
		}

		public boolean inspectChanges(ESLocalProject projectSpace, ESChangePackage changePackage,
			ESModelElementIdToEObjectMapping idToEObjectMapping) {
			return ESCommitCallback.NOCALLBACK.inspectChanges(projectSpace, changePackage, idToEObjectMapping);
		}

		public void noLocalChanges(ESLocalProject projectSpace) {
			ESCommitCallback.NOCALLBACK.noLocalChanges(projectSpace);
		}

	}

	private class MyUpdateCallback implements ESUpdateCallback {

		public boolean inspectChanges(ESLocalProject projectSpace, List<ESChangePackage> changes,
			ESModelElementIdToEObjectMapping idToEObjectMapping) {
			return ESUpdateCallback.NOCALLBACK.inspectChanges(projectSpace, changes, idToEObjectMapping);
		}

		public void noChangesOnServer() {
			ESUpdateCallback.NOCALLBACK.noChangesOnServer();
		}

		public boolean conflictOccurred(ESConflictSet changeConflictException,
			IProgressMonitor progressMonitor) {
			return ESUpdateCallback.NOCALLBACK.conflictOccurred(changeConflictException, progressMonitor);
		}

	}

	protected PrimaryVersionSpec commitWithoutCommand(final ProjectSpace projectSpace) throws ESException {
		return projectSpace.commit("SomeCommitMessage", new MyCommitCallback(),
			new NullProgressMonitor());
	}

	protected PrimaryVersionSpec update(ProjectSpace projectSpace) throws ESException {
		return projectSpace.update(Versions.createHEAD(), new MyUpdateCallback(),
			new NullProgressMonitor());
	}
}
