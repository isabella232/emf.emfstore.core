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
package org.eclipse.emf.emfstore.client.test.server.api;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.client.test.WorkspaceTest;
import org.eclipse.emf.emfstore.client.test.server.api.util.AuthControlMock;
import org.eclipse.emf.emfstore.client.test.server.api.util.ConnectionMock;
import org.eclipse.emf.emfstore.client.test.server.api.util.ResourceFactoryMock;
import org.eclipse.emf.emfstore.client.test.server.api.util.TestConflictResolver;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ConnectionManager;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.xmlrpc.XmlRpcConnectionManager;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESRemoteProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommandWithResult;
import org.eclipse.emf.emfstore.internal.server.EMFStore;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.core.EMFStoreImpl;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.ServerSpace;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Versions;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.After;
import org.junit.Before;

public abstract class CoreServerTest extends WorkspaceTest {

	private EMFStore emfStore;
	private AuthControlMock authMock;
	private ServerSpace serverSpace;
	private ConnectionMock connectionMock;

	@Override
	protected void configureCompareAtEnd() {
		setCompareAtEnd(false);
	}

	@Before
	public void before() {
		try {
			initServer();
		} catch (final FatalESException e) {
			throw new RuntimeException(e);
		}
	}

	@After
	public void after() {
		ESWorkspaceProviderImpl.getInstance().setConnectionManager(new XmlRpcConnectionManager());
	}

	public void initServer() throws FatalESException {
		ServerConfiguration.setTesting(true);
		serverSpace = initServerSpace();
		authMock = new AuthControlMock();
		emfStore = EMFStoreImpl.createInterface(serverSpace, authMock);
		connectionMock = new ConnectionMock(emfStore, authMock);
		ESWorkspaceProviderImpl.getInstance().setConnectionManager(connectionMock);
	}

	private ServerSpace initServerSpace() {
		final ResourceSetImpl set = new ResourceSetImpl();
		set.setResourceFactoryRegistry(new ResourceFactoryMock());
		final Resource resource = set.createResource(URI.createURI(""));
		final ServerSpace serverSpace = ModelFactory.eINSTANCE.createServerSpace();
		resource.getContents().add(serverSpace);
		return serverSpace;
	}

	public ConnectionManager initConnectionManager() {
		return connectionMock;
	}

	public EMFStore getEmfStore() {
		return emfStore;
	}

	public ConnectionMock getConnectionMock() {
		return connectionMock;
	}

	public ServerSpace getServerSpace() {
		return serverSpace;
	}

	protected ProjectHistory getProjectHistory(ProjectSpace ps) {
		final ProjectId id = ps.getProjectId();
		for (final ProjectHistory history : getServerSpace().getProjects()) {
			if (history.getProjectId().equals(id)) {
				return history;
			}
		}
		throw new RuntimeException("Project History not found");
	}

	protected PrimaryVersionSpec branch(final ProjectSpace ps, final String branchName) {
		return new EMFStoreCommandWithResult<PrimaryVersionSpec>() {
			@Override
			protected PrimaryVersionSpec doRun() {
				try {
					// TODO: TQ cast
					return ps.commitToBranch(Versions.createBRANCH(branchName), null, null, null);
				} catch (final ESException e) {
					throw new RuntimeException(e);
				}
			}
		}.run(getProject(), false);
	}

	protected PrimaryVersionSpec share(final ProjectSpace ps) {
		return new EMFStoreCommandWithResult<PrimaryVersionSpec>() {
			@Override
			protected PrimaryVersionSpec doRun() {
				try {
					ps.shareProject(new NullProgressMonitor());
					return ps.getBaseVersion();
				} catch (final ESException e) {
					throw new RuntimeException(e);
				}
			}
		}.run(getProject(), false);
	}

	protected PrimaryVersionSpec commit(final ProjectSpace ps) {
		return new EMFStoreCommandWithResult<PrimaryVersionSpec>() {
			@Override
			protected PrimaryVersionSpec doRun() {
				try {
					return ps.commit(new NullProgressMonitor());
				} catch (final ESException e) {
					throw new RuntimeException(e);
				}
			}
		}.run(getProject(), false);
	}

	protected ProjectSpace reCheckout(final ProjectSpace projectSpace) {
		return new EMFStoreCommandWithResult<ProjectSpace>() {
			@Override
			protected ProjectSpace doRun() {
				try {
					((ESWorkspaceProviderImpl) ESWorkspaceProvider.INSTANCE)
						.setConnectionManager(getConnectionMock());
					// TODO: TQ
					final ESLocalProject checkout = projectSpace.toAPI().getRemoteProject().checkout(
						"testCheckout",
						projectSpace.getUsersession().toAPI(),
						projectSpace.getBaseVersion().toAPI(),
						new NullProgressMonitor());
					return ((ESLocalProjectImpl) checkout).toInternalAPI();
				} catch (final ESException e) {
					throw new RuntimeException(e);
				}
			}
		}.run(getProject(), false);
	}

	protected ProjectSpace checkout(final ESRemoteProjectImpl remoteProject, final PrimaryVersionSpec baseVersion) {
		return new EMFStoreCommandWithResult<ProjectSpace>() {
			@Override
			protected ProjectSpace doRun() {
				try {
					((ESWorkspaceProviderImpl) ESWorkspaceProvider.INSTANCE)
						.setConnectionManager(getConnectionMock());
					// TODO: TQ
					final ESLocalProject checkout = remoteProject.checkout(
						"testCheckout",
						getProjectSpace().getUsersession().toAPI(),
						baseVersion.toAPI(),
						new NullProgressMonitor());
					return ((ESLocalProjectImpl) checkout).toInternalAPI();
				} catch (final ESException e) {
					throw new RuntimeException(e);
				}
			}
		}.run(getProject(), false);
	}

	protected void mergeWithBranch(final ProjectSpace trunk, final PrimaryVersionSpec latestOnBranch,
		final int expectedConflicts) {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				try {
					// the conflict resolver always prefers the changes from the incoming branch
					((ProjectSpaceBase) trunk).mergeBranch(latestOnBranch, new TestConflictResolver(true,
						expectedConflicts), new NullProgressMonitor());
				} catch (final ESException e) {
					throw new RuntimeException(e);
				}
			}
		}.run(getProject(), false);
	}
}