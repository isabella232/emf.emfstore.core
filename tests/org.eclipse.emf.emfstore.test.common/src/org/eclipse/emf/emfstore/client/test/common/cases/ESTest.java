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
package org.eclipse.emf.emfstore.client.test.common.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.test.common.dsl.Delete;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.notification.recording.NotificationRecording;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.WorkspaceBase;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommandWithResult;
import org.eclipse.emf.emfstore.internal.common.CommonUtil;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util.OperationsCanonizer;
import org.eclipse.emf.emfstore.internal.server.model.versioning.persistent.CloseableIterable;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.After;
import org.junit.Before;

/**
 * Common base class for all EMFStore tests.
 *
 * @author emueller
 *
 */
public abstract class ESTest {

	private static final String CLONED_PROJECT_NAME = "clonedProject"; //$NON-NLS-1$

	private ProjectSpaceBase projectSpace;

	public <T extends AbstractOperation> T checkAndCast(AbstractOperation op, Class<T> clazz) {
		assertTrue(clazz.isInstance(op));
		return clazz.cast(op);
	}

	/**
	 * Clones a {@link ProjectSpace} including the project.
	 *
	 * @param projectSpace
	 *            the project space to be cloned
	 * @return the cloned project space
	 */
	public ProjectSpace cloneProjectSpace(final ProjectSpace projectSpace) {
		return new EMFStoreCommandWithResult<ProjectSpace>() {
			@Override
			protected ProjectSpace doRun() {
				final WorkspaceBase workspace = (WorkspaceBase) ESWorkspaceProviderImpl.getInstance().getWorkspace()
					.toInternalAPI();
				return workspace.cloneProject(CLONED_PROJECT_NAME, projectSpace.getProject());
			}
		}.run(false);
	}

	@Before
	public void before() {
		try {
			Delete.allLocalProjects();
		} catch (final IOException ex) {
			fail(ex.getMessage());
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
		CommonUtil.setTesting(true);
		final ESLocalProject project = Create.project(ProjectUtil.defaultName());
		projectSpace = (ProjectSpaceBase) ESLocalProjectImpl.class.cast(project
			).toInternalAPI();
	}

	@After
	public void after() {
		try {
			Delete.allLocalProjects();
		} catch (final IOException ex) {
			// ignore, before will try to delete again
			// fail(ex.getMessage());
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Convenience to get an operation by type.
	 *
	 * @param clazz class of operation
	 * @return operation
	 */
	protected AbstractOperation checkAndGetOperation(Class<? extends AbstractOperation> clazz) {
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertTrue(clazz.isInstance(operations.get(0)));
		final AbstractOperation operation = operations.get(0);
		clearOperations();
		assertEquals(forceGetOperations().size(), 0);
		return operation;
	}

	public ProjectSpace getProjectSpace() {
		return projectSpace;
	}

	public ESLocalProject getLocalProject() {
		return projectSpace.toAPI();
	}

	public Project getProject() {
		return projectSpace.getProject();
	}

	public void clearOperations() {
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				getProjectSpace().changePackage().clear();
				getProjectSpace().getOperationManager().clearOperations();
				return null;
			}
		});
	}

	public NotificationRecording getRecording() {
		return ((ProjectSpaceImpl) getProjectSpace()).getNotificationRecorder().getRecording();
	}

	public void canonize(final List<AbstractOperation> operations) {
		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				OperationsCanonizer.canonize(operations);
			}
		});
	}

	public List<AbstractOperation> forceGetOperations() {
		return forceGetOperations(projectSpace);
	}

	public List<AbstractOperation> forceGetOperations(ProjectSpace projectSpace) {
		final List<AbstractOperation> ops = new ArrayList<AbstractOperation>();
		final CloseableIterable<AbstractOperation> operations = projectSpace.changePackage().operations();

		try {
			for (final AbstractOperation operation : operations.iterable()) {
				ops.add(operation);
			}
		} finally {
			operations.close();
		}

		return ops;
	}
}
