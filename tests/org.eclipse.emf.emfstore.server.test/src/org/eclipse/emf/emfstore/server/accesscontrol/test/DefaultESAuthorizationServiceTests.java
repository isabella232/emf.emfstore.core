/*******************************************************************************
 * Copyright (c) 2011-2017 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.accesscontrol.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;

import org.eclipse.emf.emfstore.client.test.common.mocks.DAOFacadeMock;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.DefaultESAuthorizationService;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.DefaultESOrgUnitResolverService;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.Messages;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.ACUserContainer;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.SessionTimedOutException;
import org.eclipse.emf.emfstore.internal.server.impl.api.ESOrgUnitProviderImpl;
import org.eclipse.emf.emfstore.internal.server.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.AccesscontrolFactory;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.RolesFactory;
import org.eclipse.emf.emfstore.internal.server.model.impl.ProjectIdImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.SessionIdImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESOrgUnitRepositoryImpl;
import org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.auth.ESSessions;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link DefaultESAuthorizationService}.
 */
@SuppressWarnings("restriction")
public class DefaultESAuthorizationServiceTests {

	private TestSession sessions;
	private DAOFacadeMock acDAOFacade;
	private ESOrgUnitRepositoryImpl orgUnitRepository;
	private ESOrgUnitProviderImpl orgUnitProvider;
	private DefaultESOrgUnitResolverService orgUnitResolver;
	private SessionIdImpl sessionId;
	private ProjectIdImpl projectId;

	@Before
	public void before() {
		sessions = new TestSession();

		acDAOFacade = new DAOFacadeMock();
		orgUnitRepository = new ESOrgUnitRepositoryImpl(acDAOFacade);
		orgUnitProvider = new ESOrgUnitProviderImpl(orgUnitRepository);

		orgUnitResolver = new DefaultESOrgUnitResolverService();
		orgUnitResolver.init(orgUnitProvider);

		sessionId = (SessionIdImpl) ModelFactory.eINSTANCE.createSessionId();
		projectId = (ProjectIdImpl) ModelFactory.eINSTANCE.createProjectId();
	}

	@Test(expected = SessionTimedOutException.class)
	public void testCheckProjectAdminAccessInvalidUserSession() throws AccessControlException {
		/* setup */
		final DefaultESAuthorizationService defaultESAuthorizationService = createServiceUnderTest(sessions,
			orgUnitProvider, orgUnitResolver);

		/* act */
		try {
			defaultESAuthorizationService.checkProjectAdminAccess(
				sessionId.toAPI(),
				projectId.toAPI(),
				ESProjectAdminPrivileges.AssignRoleToOrgUnit);
		}

		/* assert */
		catch (final SessionTimedOutException ex) {
			assertEquals(Messages.AccessControlImpl_SessionID_Unknown, ex.getMessage());
			throw ex;
		}
	}

	@Test
	public void testCheckProjectAdminAccessServerAdminDirect() throws AccessControlException {
		/* setup */
		final DefaultESAuthorizationService defaultESAuthorizationService = createServiceUnderTest(sessions,
			orgUnitProvider, orgUnitResolver);

		/* setup user */
		final ACUser user = AccesscontrolFactory.eINSTANCE.createACUser();
		acDAOFacade.add(user);
		user.getRoles().add(writerRole(projectId));
		user.getRoles().add(RolesFactory.eINSTANCE.createServerAdmin());

		sessions.addUser(user, sessionId);

		/* act */
		assertTrue(defaultESAuthorizationService.checkProjectAdminAccess(
			sessionId.toAPI(),
			projectId.toAPI(),
			ESProjectAdminPrivileges.AssignRoleToOrgUnit));

	}

	@Test
	public void testCheckProjectAdminAccessServerAdminViaGroup() throws AccessControlException {
		/* setup */
		final DefaultESAuthorizationService defaultESAuthorizationService = createServiceUnderTest(sessions,
			orgUnitProvider, orgUnitResolver);

		/* setup user */
		final ACUser user = AccesscontrolFactory.eINSTANCE.createACUser();
		acDAOFacade.add(user);
		user.getRoles().add(writerRole(projectId));

		final ACGroup group = AccesscontrolFactory.eINSTANCE.createACGroup();
		acDAOFacade.add(group);
		group.getMembers().add(user);
		group.getRoles().add(readerRole(projectId));
		group.getRoles().add(RolesFactory.eINSTANCE.createServerAdmin());

		sessions.addUser(user, sessionId);

		/* act */
		assertTrue(defaultESAuthorizationService.checkProjectAdminAccess(
			sessionId.toAPI(),
			projectId.toAPI(),
			ESProjectAdminPrivileges.AssignRoleToOrgUnit));
	}

	@Test(expected = AccessControlException.class)
	public void testCheckProjectAdminAccessNotProjectAdmin() throws AccessControlException {
		/* setup */
		final DefaultESAuthorizationService defaultESAuthorizationService = createServiceUnderTest(sessions,
			orgUnitProvider, orgUnitResolver);

		/* setup user */
		final ACUser user = AccesscontrolFactory.eINSTANCE.createACUser();
		acDAOFacade.add(user);
		user.getRoles().add(writerRole(projectId));

		final ACGroup group = AccesscontrolFactory.eINSTANCE.createACGroup();
		acDAOFacade.add(group);
		group.getMembers().add(user);
		group.getRoles().add(readerRole(projectId));

		sessions.addUser(user, sessionId);

		/* act */
		try {
			defaultESAuthorizationService.checkProjectAdminAccess(
				sessionId.toAPI(),
				projectId.toAPI(),
				ESProjectAdminPrivileges.AssignRoleToOrgUnit);
		}
		/* assert */
		catch (final AccessControlException ex) {
			assertEquals(Messages.AccessControlImpl_Insufficient_Rights, ex.getMessage());
			throw ex;
		}
	}

	@Test(expected = AccessControlException.class)
	public void testCheckProjectAdminAccessInvalidPrivileg() throws AccessControlException {
		/* setup */
		final DefaultESAuthorizationService defaultESAuthorizationService = createServiceUnderTest(sessions,
			orgUnitProvider, orgUnitResolver, Collections.singleton(ESProjectAdminPrivileges.AssignRoleToOrgUnit));

		/* setup user */
		final ACUser user = AccesscontrolFactory.eINSTANCE.createACUser();
		acDAOFacade.add(user);
		user.getRoles().add(writerRole(projectId));
		user.getRoles().add(projectAdminRole(projectId));

		final ACGroup group = AccesscontrolFactory.eINSTANCE.createACGroup();
		acDAOFacade.add(group);
		group.getMembers().add(user);
		group.getRoles().add(readerRole(projectId));

		sessions.addUser(user, sessionId);

		/* act */
		try {
			defaultESAuthorizationService.checkProjectAdminAccess(
				sessionId.toAPI(),
				projectId.toAPI(),
				ESProjectAdminPrivileges.AssignRoleToOrgUnit);
		}
		/* assert */
		catch (final AccessControlException ex) {
			assertEquals(Messages.AccessControlImpl_PARole_Missing_Privilege, ex.getMessage());
			throw ex;
		}
	}

	@Test
	public void testCheckProjectAdminAccessNoProjectId() throws AccessControlException {
		/* setup */
		final DefaultESAuthorizationService defaultESAuthorizationService = createServiceUnderTest(sessions,
			orgUnitProvider, orgUnitResolver);

		/* setup user */
		final ACUser user = AccesscontrolFactory.eINSTANCE.createACUser();
		acDAOFacade.add(user);
		user.getRoles().add(writerRole(projectId));
		user.getRoles().add(projectAdminRole(projectId));

		final ACGroup group = AccesscontrolFactory.eINSTANCE.createACGroup();
		acDAOFacade.add(group);
		group.getMembers().add(user);
		group.getRoles().add(readerRole(projectId));

		sessions.addUser(user, sessionId);

		/* act */
		assertFalse(defaultESAuthorizationService.checkProjectAdminAccess(
			sessionId.toAPI(),
			null,
			ESProjectAdminPrivileges.AssignRoleToOrgUnit));
	}

	@Test(expected = AccessControlException.class)
	public void testCheckProjectAdminAccessCannotAdministrate() throws AccessControlException {
		/* setup */
		final DefaultESAuthorizationService defaultESAuthorizationService = createServiceUnderTest(sessions,
			orgUnitProvider, orgUnitResolver);

		/* setup user */
		final ACUser user = AccesscontrolFactory.eINSTANCE.createACUser();
		acDAOFacade.add(user);
		user.getRoles().add(writerRole(projectId));
		user.getRoles().add(projectAdminRole(ModelFactory.eINSTANCE.createProjectId()));

		final ACGroup group = AccesscontrolFactory.eINSTANCE.createACGroup();
		acDAOFacade.add(group);
		group.getMembers().add(user);
		group.getRoles().add(readerRole(projectId));
		group.getRoles().add(projectAdminRole(ModelFactory.eINSTANCE.createProjectId()));

		sessions.addUser(user, sessionId);

		/* act */
		try {
			defaultESAuthorizationService.checkProjectAdminAccess(
				sessionId.toAPI(),
				projectId.toAPI(),
				ESProjectAdminPrivileges.AssignRoleToOrgUnit);
		} catch (final AccessControlException ex) {
			assertEquals(Messages.AccessControlImpl_PARole_Missing_Privilege, ex.getMessage());
			throw ex;
		}
	}

	@Test
	public void testCheckProjectAdminAccessCanAdministrateByUser() throws AccessControlException {
		/* setup */
		final DefaultESAuthorizationService defaultESAuthorizationService = createServiceUnderTest(sessions,
			orgUnitProvider, orgUnitResolver);

		/* setup user */
		final ACUser user = AccesscontrolFactory.eINSTANCE.createACUser();
		acDAOFacade.add(user);
		user.getRoles().add(writerRole(projectId));
		user.getRoles().add(projectAdminRole(projectId));

		final ACGroup group = AccesscontrolFactory.eINSTANCE.createACGroup();
		acDAOFacade.add(group);
		group.getMembers().add(user);
		group.getRoles().add(readerRole(projectId));
		group.getRoles().add(projectAdminRole(ModelFactory.eINSTANCE.createProjectId()));

		sessions.addUser(user, sessionId);

		/* act */
		assertFalse(defaultESAuthorizationService.checkProjectAdminAccess(
			sessionId.toAPI(),
			projectId.toAPI(),
			ESProjectAdminPrivileges.AssignRoleToOrgUnit));
	}

	@Test
	public void testCheckProjectAdminAccessCanAdministrateByGroup() throws AccessControlException {
		/* setup */
		final DefaultESAuthorizationService defaultESAuthorizationService = createServiceUnderTest(sessions,
			orgUnitProvider, orgUnitResolver);

		/* setup user */
		final ACUser user = AccesscontrolFactory.eINSTANCE.createACUser();
		acDAOFacade.add(user);
		user.getRoles().add(writerRole(projectId));
		user.getRoles().add(projectAdminRole(ModelFactory.eINSTANCE.createProjectId()));

		final ACGroup group = AccesscontrolFactory.eINSTANCE.createACGroup();
		acDAOFacade.add(group);
		group.getMembers().add(user);
		group.getRoles().add(readerRole(projectId));
		group.getRoles().add(projectAdminRole(projectId));

		sessions.addUser(user, sessionId);

		/* act */
		assertFalse(defaultESAuthorizationService.checkProjectAdminAccess(
			sessionId.toAPI(),
			projectId.toAPI(),
			ESProjectAdminPrivileges.AssignRoleToOrgUnit));
	}

	private static Role projectAdminRole(ProjectId projectId) {
		return addProjectToRole(projectId, RolesFactory.eINSTANCE.createProjectAdminRole());
	}

	private static Role readerRole(ProjectId projectId) {
		return addProjectToRole(projectId, RolesFactory.eINSTANCE.createReaderRole());
	}

	private static Role writerRole(ProjectId projectId) {
		return addProjectToRole(projectId, RolesFactory.eINSTANCE.createWriterRole());
	}

	private static Role addProjectToRole(ProjectId projectId, final Role role) {
		role.getProjects().add(projectId);
		return role;
	}

	private static DefaultESAuthorizationService createServiceUnderTest(
		final ESSessions sessions,
		final ESOrgUnitProvider orgUnitProvider,
		final ESOrgUnitResolver orgUnitResolver) {
		return createServiceUnderTest(
			sessions,
			orgUnitProvider,
			orgUnitResolver,
			Collections.<ESProjectAdminPrivileges> emptySet());
	}

	private static DefaultESAuthorizationService createServiceUnderTest(
		final ESSessions sessions,
		final ESOrgUnitProvider orgUnitProvider,
		final ESOrgUnitResolver orgUnitResolver,
		final Set<ESProjectAdminPrivileges> invalidPrivileges) {
		final DefaultESAuthorizationService defaultESAuthorizationService = new DefaultESAuthorizationService() {

			@Override
			protected boolean isProjectAdminPrivileg(ESProjectAdminPrivileges privileg) {
				return !invalidPrivileges.contains(privileg);
			}
		};
		defaultESAuthorizationService.init(sessions, orgUnitResolver, orgUnitProvider);
		return defaultESAuthorizationService;
	}

	private static class TestSession extends ESSessions {
		public void addUser(ACUser user, SessionId sessionId) {
			sessionUserMap.put(
				sessionId,
				new ACUserContainer(user));
		}
	}

}
