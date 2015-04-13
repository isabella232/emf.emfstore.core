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
package org.eclipse.emf.emfstore.server.test;

import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.addAndCommit;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.addElement;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.commit;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.commitToBranch;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.defaultName;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.share;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.tag;
import static org.eclipse.emf.emfstore.internal.common.APIUtil.toInternal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUserMock;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.test.common.dsl.CreateAPI;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ConnectionManager;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESUsersessionImpl;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidProjectIdException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidVersionSpecException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESSessionIdImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.test.model.TestmodelPackage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;

public class ServerInterfaceTest extends ESTestWithLoggedInUserMock {

	private static final String FOO_BRANCH = "foo-branch"; //$NON-NLS-1$
	private static final String FOOTAG = "footag"; //$NON-NLS-1$
	private static final String TRUNK = "trunk"; //$NON-NLS-1$

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() throws ESException {
		stopEMFStore();
	}

	@Test
	public void testCreateEmptyProject() throws ESException {

		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();

		final Usersession usersession = toInternal(Usersession.class, getUsersession());
		final ProjectId projectId = Create.projectId();
		projectId.setId(ProjectUtil.defaultName());

		final ProjectInfo projectInfo = connectionManager.createEmptyProject(
			usersession.getSessionId(),
			ProjectUtil.defaultName(), "Example Description", //$NON-NLS-1$
			Create.logMessage());
		final List<ProjectInfo> projectInfos = connectionManager.getProjectList(usersession.getSessionId());

		assertEquals(1, projectInfos.size());
		assertEquals(projectInfo.getName(), projectInfos.get(0).getName());
		assertEquals(projectInfo.getName(), ProjectUtil.defaultName());
	}

	@Test(expected = InvalidProjectIdException.class)
	public void testGetProjectWithInvalidProjectId() throws ESException {

		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();

		connectionManager.getProject(
			toInternal(Usersession.class, getUsersession()).getSessionId(),
			Create.projectId(),
			Create.primaryVersionSpec(0));
	}

	@Test(expected = InvalidProjectIdException.class)
	public void testGetProjectWithNullProjectId() throws ESException {

		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();

		connectionManager.getProject(
			toInternal(Usersession.class, getUsersession()).getSessionId(),
			Create.projectId(),
			Create.primaryVersionSpec(0));
	}

	@Test(expected = InvalidProjectIdException.class)
	public void testCreateVersion() throws ESException {

		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();

		final ESLocalProject localProject = CreateAPI.project(ProjectUtil.defaultName());
		final ProjectSpace projectSpace = APIUtil.toInternal(ProjectSpace.class, localProject);
		final Usersession usersession = APIUtil.toInternal(Usersession.class, getUsersession());
		final PrimaryVersionSpec baseVersionSpec = VersioningFactory.eINSTANCE.createPrimaryVersionSpec();
		final BranchVersionSpec branchVersionSpec = VersioningFactory.eINSTANCE.createBranchVersionSpec();
		final ProjectId projectId = Create.projectId();

		projectId.setId(ProjectUtil.defaultName());
		baseVersionSpec.setBranch(TRUNK);
		baseVersionSpec.setIdentifier(0);
		branchVersionSpec.setBranch(TRUNK);

		connectionManager.createVersion(
			usersession.getSessionId(),
			projectId,
			baseVersionSpec,
			VersioningFactory.eINSTANCE.createChangePackage(),
			branchVersionSpec,
			projectSpace.getMergedVersion(),
			VersioningFactory.eINSTANCE.createLogMessage());
		connectionManager.getProjectList(usersession.getSessionId());
		// TODO: assert missing
	}

	@Test
	public void testGetChanges() throws ESException {

		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();

		final ESLocalProject project = CreateAPI.project(defaultName());
		share(getUsersession(), project);

		final ESLocalProject localProject = commit(
			addElement(
				project,
				Create.testElement()));

		final List<AbstractChangePackage> changes = connectionManager.getChanges(
			toInternal(Usersession.class, getUsersession()).getSessionId(),
			toInternal(ProjectSpace.class, localProject).getProjectId(),
			Create.primaryVersionSpec(0),
			Create.primaryVersionSpec(1));

		assertEquals(1, changes.size());
	}

	@Test
	public void testResolvePagedUpdateVersionSpec() throws ESException {

		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();

		final ESLocalProject project = CreateAPI.project(defaultName());
		share(getUsersession(), project);
		final ESLocalProject localProject = addAndCommit(project).times(3);

		final PrimaryVersionSpec resolvedVersionSpec = connectionManager.resolveVersionSpec(
			toInternal(Usersession.class, getUsersession()).getSessionId(),
			toInternal(ProjectSpace.class, localProject).getProjectId(),
			Create.pagedUpdateVersionSpec(
				CreateAPI.primaryVersionSpec(0), 1));

		assertEquals(1, resolvedVersionSpec.getIdentifier());
	}

	@Test
	public void testResolveTagVersionSpec() throws ESException {

		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();

		final ESLocalProject project = CreateAPI.project(defaultName());
		share(getUsersession(), project);
		final ESLocalProject localProject = addAndCommit(project)
			.times(3);
		tag(localProject, CreateAPI.primaryVersionSpec(1), TRUNK, FOOTAG);

		final PrimaryVersionSpec resolvedVersionSpec = connectionManager.resolveVersionSpec(
			toInternal(Usersession.class, getUsersession()).getSessionId(),
			toInternal(ProjectSpace.class, localProject).getProjectId(),
			Create.tagVersionSpec(TRUNK, FOOTAG));

		assertEquals(1, resolvedVersionSpec.getIdentifier());
	}

	@Test
	public void testERegisterEPackage() throws ESException {

		final Object pckg = EPackage.Registry.INSTANCE.get(TestmodelPackage.eNS_URI);
		EPackage.Registry.INSTANCE.remove(TestmodelPackage.eNS_URI);
		assertNull(EPackage.Registry.INSTANCE.getEPackage(TestmodelPackage.eNS_URI));

		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();
		final TestmodelPackage testmodelPackage = TestmodelPackage.eINSTANCE;
		final ESSessionIdImpl sessionId = ESSessionIdImpl.class.cast(getSuperUsersession().getSessionId());
		connectionManager.registerEPackage(sessionId.toInternalAPI(), testmodelPackage);
		assertNotNull(EPackage.Registry.INSTANCE.getEPackage(TestmodelPackage.eNS_URI));

		EPackage.Registry.INSTANCE.put(TestmodelPackage.eNS_URI, pckg);
	}

	@Test
	public void testResolveBranchVersionSpec() throws ESException {

		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();

		final ESLocalProject project = CreateAPI.project(defaultName());
		share(getUsersession(), project);

		final ESLocalProject localProject = addAndCommit(project)
			.times(3);

		final PrimaryVersionSpec resolvedVersionSpec = connectionManager.resolveVersionSpec(
			toInternal(Usersession.class, getUsersession()).getSessionId(),
			toInternal(ProjectSpace.class, localProject).getProjectId(),
			Create.branchVersionSpec());

		assertEquals(3, resolvedVersionSpec.getIdentifier());
	}

	@Test
	public void testResolveAncestorVersionSpec() throws ESException {

		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();

		final ESLocalProject project = CreateAPI.project(defaultName());
		share(getUsersession(), project);

		final ESLocalProject localProject = addAndCommit(project).times(1);

		commitToBranch(addElement(localProject, Create.testElement()), FOO_BRANCH);

		final PrimaryVersionSpec resolvedVersionSpec = connectionManager.resolveVersionSpec(
			toInternal(Usersession.class, getUsersession()).getSessionId(),
			toInternal(ProjectSpace.class, localProject).getProjectId(),
			Create.ancestorVersionSpec(
				Create.primaryVersionSpec(1),
				Create.primaryVersionSpec(2, FOO_BRANCH)));

		assertEquals(1, resolvedVersionSpec.getIdentifier());
	}

	@Test
	public void testResolveDateVersionSpec() throws ESException {
		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();

		final ESLocalProject project = CreateAPI.project(defaultName());
		share(getUsersession(), project);
		final ESLocalProject localProject = addAndCommit(project).times(1);

		final Date now = new Date();
		commit(addElement(localProject, Create.testElement()));

		final PrimaryVersionSpec resolvedVersionSpec = connectionManager.resolveVersionSpec(
			toInternal(Usersession.class, getUsersession()).getSessionId(),
			toInternal(ProjectSpace.class, localProject).getProjectId(),
			Create.dateVersionSpec(now));

		assertEquals(1, resolvedVersionSpec.getIdentifier());
	}

	@Test
	public void testDeleteProject() throws ESException {
		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();

		final ESLocalProject project = CreateAPI.project(defaultName());
		share(getUsersession(), project);
		final ESLocalProject localProject = addAndCommit(project).times(1);

		connectionManager.deleteProject(
			toInternal(Usersession.class, getUsersession()).getSessionId(),
			toInternal(ProjectSpace.class, localProject).getProjectId(),
			true);

		assertEquals(0, connectionManager.getProjectList(
			toInternal(Usersession.class, getUsersession()).getSessionId()).size());
	}

	@Test(expected = InvalidVersionSpecException.class)
	public void testAddTag() throws ESException {

		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();

		// add more elements in order to create different VERSIONS
		final ESLocalProject project = CreateAPI.project(defaultName());
		share(getUsersession(), project);
		final ESLocalProject localProject = addAndCommit(project).times(3);

		connectionManager.addTag(
			toInternal(Usersession.class, getUsersession()).getSessionId(),
			toInternal(ProjectSpace.class, localProject).getProjectId(),
			Create.primaryVersionSpec(1),
			Create.tagVersionSpec(TRUNK, FOOTAG));

		connectionManager.removeTag(
			toInternal(Usersession.class, getUsersession()).getSessionId(),
			toInternal(ProjectSpace.class, localProject).getProjectId(),
			Create.primaryVersionSpec(1),
			Create.tagVersionSpec(TRUNK, FOOTAG));

		connectionManager.resolveVersionSpec(
			toInternal(Usersession.class, getUsersession()).getSessionId(),
			toInternal(ProjectSpace.class, localProject).getProjectId(),
			Create.tagVersionSpec(TRUNK, FOOTAG));
	}

	@Test
	public void testGetVersion() throws MalformedURLException, ESException {
		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();
		final Bundle emfStoreBundle = Platform.getBundle("org.eclipse.emf.emfstore.server"); //$NON-NLS-1$
		final String versionId = emfStoreBundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);

		assertEquals(versionId,
			connectionManager.getVersion(getServerInfo()));
		assertEquals(versionId,
			connectionManager.getVersion(
				ESUsersessionImpl.class.cast(
					getUsersession()).toInternalAPI().getSessionId()));
	}
}
