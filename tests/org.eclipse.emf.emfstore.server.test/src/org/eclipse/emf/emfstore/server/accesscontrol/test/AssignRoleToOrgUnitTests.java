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
package org.eclipse.emf.emfstore.server.accesscontrol.test;

import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.share;
import static org.eclipse.emf.emfstore.client.test.common.util.ServerUtil.createUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.emf.emfstore.client.test.common.dsl.Roles;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESUsersessionImpl;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ReaderRole;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.RolesPackage;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESGlobalProjectIdImpl;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the {@link ESProjectAdminPrivileges#AssignRoleToOrgUnit} privilege of a
 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ProjectAdminRole ProjectAdminRole}.
 *
 * @author emueller
 *
 */
public class AssignRoleToOrgUnitTests extends ProjectAdminTest {

	@BeforeClass
	public static void beforeClass() {
		startEMFStoreWithPAProperties(
			ESProjectAdminPrivileges.ShareProject,
			ESProjectAdminPrivileges.AssignRoleToOrgUnit);
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@After
	@Override
	public void after() {
		try {
			ServerUtil.deleteGroup(getSuperUsersession(), getNewGroupName());
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
		super.after();
	}

	@Test
	public void assignReaderRolePA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		getAdminBroker().assignRole(newUser, RolesPackage.eINSTANCE.getReaderRole());

		final ACUser user = ServerUtil.getUser(getSuperUsersession(), getNewUsername());
		assertTrue(hasReaderRole(user.getId()));
	}

	@Test
	public void changeRoleAsPAWithUserBeingMemberOfOtherProject() throws ESException {
		final ACOrgUnitId newUser = createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		share(getUsersession(), getLocalProject());
		final ProjectId projectId = ESGlobalProjectIdImpl.class.cast(
			getLocalProject().getGlobalProjectId()).toInternalAPI();

		final ProjectId secondProjectId = ESGlobalProjectIdImpl.class.cast(
			share(getSuperUsersession(), getLocalProject())).toInternalAPI();

		getAdminBroker().addParticipant(projectId, newUser, Roles.writer());
		getSuperAdminBroker().addParticipant(secondProjectId, newUser, Roles.writer());

		getAdminBroker().changeRole(projectId, newUser, Roles.reader());
		final ACUser user = ServerUtil.getUser(getSuperUsersession(), getNewUsername());
		assertTrue(hasReaderRole(user.getId()));
	}

	/**
	 * Tries to remove an user as a project admin, where the user to be removed
	 * is also a project admin.
	 */
	@Test(expected = AccessControlException.class)
	public void removeParticipantAsPAWithParticipantBeingPAToo() throws ESException {
		final ACOrgUnitId newUserId = createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		share(getUsersession(), getLocalProject());
		final ProjectId projectId = ESGlobalProjectIdImpl.class.cast(
			getLocalProject().getGlobalProjectId()).toInternalAPI();

		getAdminBroker().addParticipant(projectId, newUserId, Roles.projectAdmin());
		getAdminBroker().removeParticipant(projectId, newUserId);
	}

	/**
	 * Tries to change the role of an user as a project admin, where the user whose roles
	 * to be changed is also a project admin.
	 */
	@Test(expected = AccessControlException.class)
	public void changeRoleAsPAWithUserBeingPAToo() throws ESException {
		final ACOrgUnitId newUserId = createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		share(getUsersession(), getLocalProject());
		final ProjectId projectId = ESGlobalProjectIdImpl.class.cast(
			getLocalProject().getGlobalProjectId()).toInternalAPI();

		getAdminBroker().addParticipant(projectId, newUserId, Roles.projectAdmin());
		getAdminBroker().changeRole(projectId, newUserId, Roles.writer());
	}

	@Test
	public void changeRoleAsPAWithGroupBeingMemberOfOtherProject() throws ESException {
		final ACOrgUnitId newGroup = ServerUtil.createGroup(getSuperUsersession(), getNewGroupName());
		makeUserPA();
		share(getUsersession(), getLocalProject());
		final ProjectId projectId = ESGlobalProjectIdImpl.class.cast(
			getLocalProject().getGlobalProjectId()).toInternalAPI();

		final ProjectId secondProjectId = ESGlobalProjectIdImpl.class.cast(
			share(getSuperUsersession(), getLocalProject())).toInternalAPI();

		getAdminBroker().addParticipant(projectId, newGroup, Roles.writer());
		getSuperAdminBroker().addParticipant(secondProjectId, newGroup, Roles.writer());

		getAdminBroker().changeRole(projectId, newGroup, Roles.reader());
		final ACGroup group = ServerUtil.getGroup(getSuperUsersession(), getNewGroupName());
		assertTrue(hasReaderRole(group.getId()));
	}

	@Test
	public void changeRoleToReaderPA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		ProjectUtil.share(getUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser, Roles.reader());
		final ACUser user = ServerUtil.getUser(getUsersession(), getNewUsername());
		assertTrue(hasReaderRole(user.getId()));
	}

	@Test
	public void getRolePA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		ProjectUtil.share(getUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser, Roles.reader());
		final ACUser user = ServerUtil.getUser(getUsersession(), getNewUsername());
		assertTrue(getAdminBroker().getRole(getProjectSpace().getProjectId(), user.getId()) instanceof ReaderRole);
	}

	@Test
	public void changeRoleToWriterPA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		ProjectUtil.share(getUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser, Roles.writer());
		final ACUser user = ServerUtil.getUser(getUsersession(), getNewUsername());
		assertTrue(hasWriterRole(user.getId()));
	}

	@Test
	public void changeRoleToProjectAdminPA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		ProjectUtil.share(getUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser, Roles.projectAdmin());
		final ACUser user = ServerUtil.getUser(getUsersession(), getNewUsername());
		assertTrue(hasProjectAdminRole(user.getId()));
	}

	@Test(expected = AccessControlException.class)
	public void changeRoleToProjectAdminNotPA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		ProjectUtil.share(getSuperUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser, Roles.projectAdmin());
	}

	@Test
	public void assignPAAsPA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		getAdminBroker().assignRole(newUser, Roles.projectAdmin());
		final ACUser user = ServerUtil.getUser(getSuperUsersession(), getNewUsername());
		assertTrue(hasProjectAdminRole(user.getId()));
	}

	@Test(expected = AccessControlException.class)
	public void assignRoleServerAdminPA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		getAdminBroker().assignRole(newUser, Roles.serverAdmin());
	}

	@Test
	public void changeRoleToWriterAsPA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		ProjectUtil.share(getUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser, Roles.writer());
		final ACUser user = ServerUtil.getUser(getUsersession(), getNewUsername());
		assertTrue(hasRole(user.getId(), Roles.writer()));
	}

	@Test
	public void changeRoleToSAAsSA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserSA();
		ProjectUtil.share(getUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser, Roles.serverAdmin());
		final ACUser user = ServerUtil.getUser(getUsersession(), getNewUsername());
		assertTrue(hasRole(user.getId(), Roles.serverAdmin()));
	}

	@Test
	public void changeRoleToSAViaGroupAsSA() throws ESException {
		final ACOrgUnitId newGroup = ServerUtil.createGroup(getSuperUsersession(), getNewGroupName());
		makeUserPA();
		ProjectUtil.share(getSuperUsersession(), getLocalProject());
		final ACOrgUnitId id = ((ESUsersessionImpl) getUsersession()).toInternalAPI().getACUser().getId();
		getSuperAdminBroker().addMember(newGroup, id);
		getSuperAdminBroker().changeRole(getProjectSpace().getProjectId(), newGroup, Roles.serverAdmin());

		final ACOrgUnitId createUser2 = getAdminBroker().createUser("foo"); //$NON-NLS-1$
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), createUser2, Roles.writer());
	}

	@Test
	public void changeRoleToWriterOnDifferentProjectsAsPA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		ProjectUtil.share(getUsersession(), getLocalProject());

		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final ProjectId projectId = ESGlobalProjectIdImpl.class.cast(
			share(getUsersession(), clonedProjectSpace.toAPI())).toInternalAPI();

		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser, Roles.writer());
		getAdminBroker().changeRole(
			projectId,
			newUser,
			Roles.writer());
		final ACUser user = ServerUtil.getUser(getUsersession(), getNewUsername());
		assertEquals(2, user.getRoles().get(0).getProjects().size());
	}

	@Test
	public void changeRoleToWriterAndThenToPAAsPA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		ProjectUtil.share(getUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser, RolesPackage.eINSTANCE.getWriterRole());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser,
			RolesPackage.eINSTANCE.getProjectAdminRole());
		final ACUser user = ServerUtil.getUser(getUsersession(), getNewUsername());
		assertFalse(hasWriterRole(user.getId()));
	}

	@Test(expected = AccessControlException.class)
	public void changeRoleToSAAsPA() throws ESException {
		final ACOrgUnitId newUser = createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		share(getUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser, Roles.serverAdmin());
	}

	@Test(expected = AccessControlException.class)
	public void assignProjectAdminRoleNotPA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		getAdminBroker().assignRole(newUser, Roles.projectAdmin());
	}

	@Test(expected = AccessControlException.class)
	public void assignWriterRoleNotPA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		getAdminBroker().assignRole(newUser, Roles.writer());
	}

	@Test(expected = AccessControlException.class)
	public void assignReaderRoleNotPA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		getAdminBroker().assignRole(newUser, Roles.reader());
	}

	@Test
	public void assignReaderRoleTwicePA() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		getAdminBroker().assignRole(newUser, RolesPackage.eINSTANCE.getReaderRole());
		getAdminBroker().assignRole(newUser, RolesPackage.eINSTANCE.getReaderRole());

		final ACUser user = ServerUtil.getUser(getSuperUsersession(), getNewUsername());
		assertTrue(hasReaderRole(user.getId()));
	}

}