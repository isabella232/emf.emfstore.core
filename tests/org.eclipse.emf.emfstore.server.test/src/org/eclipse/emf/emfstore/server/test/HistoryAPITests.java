/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 * Edgar Mueller - Performance refactorings
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.test;

import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.addElement;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.branch;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.checkout;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.commit;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.defaultName;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.mergeWithBranch;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.rename;
import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.share;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.exceptions.ESServerStartFailedException;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.test.common.dsl.CreateAPI;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.common.model.ESModelElementId;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidVersionSpecException;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESHistoryInfo;
import org.eclipse.emf.emfstore.server.model.query.ESHistoryQuery;
import org.eclipse.emf.emfstore.server.model.query.ESModelElementQuery;
import org.eclipse.emf.emfstore.server.model.query.ESPathQuery;
import org.eclipse.emf.emfstore.server.model.query.ESRangeQuery;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.model.versionspec.ESVersionSpec;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Branches for history test
 *
 * <pre>
 *     b1    b2    b3
 *
 * v7              o
 * 				/  |
 * v6         /    o
 * v5 	      o   /
 * v4	o	  |  /
 * v3	|	  o
 * v2	o	 /
 * v1 	 \ o
 *     	   |
 * v0  	   o
 * </pre>
 *
 * @author wesendon
 * @author emueller
 */
public class HistoryAPITests {

	static final NullProgressMonitor NPM = new NullProgressMonitor();

	public static final ESPrimaryVersionSpec[] VERSIONS = {
		ESVersionSpec.FACTORY.createPRIMARY("trunk", 0), //$NON-NLS-1$
		ESVersionSpec.FACTORY.createPRIMARY("trunk", 1), //$NON-NLS-1$
		ESVersionSpec.FACTORY.createPRIMARY("b1", 2), //$NON-NLS-1$
		ESVersionSpec.FACTORY.createPRIMARY("b2", 3), //$NON-NLS-1$
		ESVersionSpec.FACTORY.createPRIMARY("b1", 4), //$NON-NLS-1$
		ESVersionSpec.FACTORY.createPRIMARY("b2", 5), //$NON-NLS-1$
		ESVersionSpec.FACTORY.createPRIMARY("b3", 6), //$NON-NLS-1$
		ESVersionSpec.FACTORY.createPRIMARY("b3", 7) }; //$NON-NLS-1$

	public static final String[] ELEMENT_NAMES = { "v0", //$NON-NLS-1$
		"v1", //$NON-NLS-1$
		"v2", //$NON-NLS-1$
		"v3", //$NON-NLS-1$
		"v4", //$NON-NLS-1$
		"v5", //$NON-NLS-1$
		"v6", //$NON-NLS-1$
		"v7" //$NON-NLS-1$
	};

	public static final String[] BRANCHES = { "b1", //$NON-NLS-1$
		"b2", //$NON-NLS-1$
		"b3" //$NON-NLS-1$
	};

	private static ESServer server;
	private static ESUsersession session;
	private static ESLocalProject readOnlyProject;

	@BeforeClass
	public static void beforeClass() {
		try {
			server = ServerUtil.startMockServer().getServer();
			session = server.login(
				ServerUtil.superUser(),
				ServerUtil.superUserPassword());
			readOnlyProject = createHistory();
		} catch (final IllegalArgumentException ex) {
			fail(ex.getMessage());
		} catch (final ESServerStartFailedException ex) {
			fail(ex.getMessage());
		} catch (final FatalESException ex) {
			fail(ex.getMessage());
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
	}

	@AfterClass
	public static void afterClass() {
		ServerUtil.stopServer();
	}

	public static ESLocalProject getProject() {
		final ProjectSpace clone = ModelUtil.clone(((ESLocalProjectImpl) readOnlyProject).toInternalAPI());
		clone.setProject(ModelUtil.clone(((ESLocalProjectImpl) readOnlyProject).toInternalAPI().getProject()));
		return clone.toAPI();
	}

	public static ESLocalProject createHistory() throws ESException {
		final ESLocalProject localProject = CreateAPI.project(defaultName());
		share(session, localProject);

		// v0
		final TestElement testElement = Create.testElement(ELEMENT_NAMES[0]);
		addElement(localProject, testElement);
		share(session, localProject);
		assertEquals(VERSIONS[0], localProject.getBaseVersion());

		// v1
		rename(localProject, ELEMENT_NAMES[1]);
		assertEquals(VERSIONS[1], commit(localProject).getBaseVersion());
		final ESLocalProject localProject2 = checkout(localProject);

		// v2
		rename(localProject, ELEMENT_NAMES[2]);
		final ESLocalProject branch2 = branch(localProject, BRANCHES[0]);
		assertEquals(VERSIONS[2], branch2.getBaseVersion());

		// v3
		rename(localProject2, ELEMENT_NAMES[3]);
		final ESLocalProject branch3 = branch(localProject2, BRANCHES[1]);
		assertEquals(VERSIONS[3], branch3.getBaseVersion());

		// v4
		rename(localProject, ELEMENT_NAMES[4]);
		assertEquals(VERSIONS[4], commit(localProject).getBaseVersion());

		// v5
		rename(localProject2, ELEMENT_NAMES[5]);
		assertEquals(VERSIONS[5], commit(localProject2).getBaseVersion());

		// v6
		final ESLocalProject localProject3 = checkout(localProject, VERSIONS[3]);
		rename(localProject3, ELEMENT_NAMES[6]);
		assertEquals(VERSIONS[6], branch(localProject3, BRANCHES[2]).getBaseVersion());

		// v7
		mergeWithBranch(localProject3, VERSIONS[5], 1);
		rename(localProject3, ELEMENT_NAMES[7]);
		final ESLocalProject commit2 = commit(localProject3);
		assertEquals(VERSIONS[7], commit2.getBaseVersion());

		return localProject;
	}

	@Test
	public void rangeQuery() throws ESException {
		final ESLocalProject localProject = getProject();

		final ESRangeQuery<?> rangeQuery = ESHistoryQuery.FACTORY.rangeQuery(VERSIONS[3], 5, 25, false, false, false,
			false);

		final List<ESHistoryInfo> result = localProject
			.getHistoryInfos(rangeQuery, NPM);

		assertEquals(4, result.size());
		assertEquals(VERSIONS[5], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[3], result.get(1).getPrimarySpec());
		assertEquals(VERSIONS[1], result.get(2).getPrimarySpec());
		assertEquals(VERSIONS[0], result.get(3).getPrimarySpec());
	}

	@Test
	public void rangequeryAllVersions() throws ESException {
		final ESLocalProject localProject = getProject();

		final ESRangeQuery<?> rangeQuery = ESHistoryQuery.FACTORY.rangeQuery(VERSIONS[3], 5, 25, true, false, false,
			false);

		final List<ESHistoryInfo> result = localProject.getHistoryInfos(rangeQuery, NPM);

		assertEquals(8, result.size());
		assertEquals(VERSIONS[7], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[6], result.get(1).getPrimarySpec());
		assertEquals(VERSIONS[5], result.get(2).getPrimarySpec());
		assertEquals(VERSIONS[4], result.get(3).getPrimarySpec());
		assertEquals(VERSIONS[3], result.get(4).getPrimarySpec());
		assertEquals(VERSIONS[2], result.get(5).getPrimarySpec());
		assertEquals(VERSIONS[1], result.get(6).getPrimarySpec());
		assertEquals(VERSIONS[0], result.get(7).getPrimarySpec());
	}

	@Test
	public void rangequeryIncludeCp() throws ESException {
		final ESLocalProject localProject = getProject();

		final ESRangeQuery<?> rangeQuery = ESHistoryQuery.FACTORY.rangeQuery(VERSIONS[3], 1,
			25, false, false, false, true);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(rangeQuery, NPM);

		assertEquals(4, result.size());
		assertEquals(VERSIONS[5], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[3], result.get(1).getPrimarySpec());
		assertEquals(VERSIONS[1], result.get(2).getPrimarySpec());
		assertEquals(VERSIONS[0], result.get(3).getPrimarySpec());

		assertTrue(result.get(0).getChangePackage() != null);
		assertTrue(result.get(1).getChangePackage() != null);
		assertTrue(result.get(2).getChangePackage() != null);
		// version 0
		assertTrue(result.get(3).getChangePackage() == null);
	}

	@Test
	public void rangequeryNoUpper() throws ESException {
		final ESLocalProject localProject = getProject();

		final ESRangeQuery<?> rangeQuery = ESHistoryQuery.FACTORY.rangeQuery(VERSIONS[5], 5,
			1, false, false, false, false);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(rangeQuery, NPM);

		assertEquals(2, result.size());
		assertEquals(VERSIONS[5], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[3], result.get(1).getPrimarySpec());
	}

	@Test
	public void rangequeryNoLower() throws ESException {
		final ESLocalProject localProject = getProject();

		final ESRangeQuery<?> rangeQuery = ESHistoryQuery.FACTORY.rangeQuery(VERSIONS[0], 1,
			20, false, false, false, false);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(rangeQuery, NPM);

		assertEquals(2, result.size());
		assertEquals(VERSIONS[1], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[0], result.get(1).getPrimarySpec());
	}

	@Test
	public void rangequeryLimitZero() throws ESException {
		final ESLocalProject localProject = getProject();
		final ESRangeQuery<?> rangeQuery = ESHistoryQuery.FACTORY.rangeQuery(VERSIONS[0], 0,
			0, false, false, false, false);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(rangeQuery, NPM);

		assertEquals(1, result.size());
		assertEquals(VERSIONS[0], result.get(0).getPrimarySpec());
	}

	@Test
	public void rangequeryIncoming() throws ESException {
		final ESLocalProject localProject = getProject();

		final ESRangeQuery<?> rangeQuery = ESHistoryQuery.FACTORY.rangeQuery(VERSIONS[7], 0,
			2, false, true, false, false);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(rangeQuery, NPM);

		assertEquals(3, result.size());
		assertEquals(VERSIONS[7], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[6], result.get(1).getPrimarySpec());
		assertEquals(VERSIONS[5], result.get(2).getPrimarySpec());
	}

	@Test
	public void rangequeryOutgoing() throws ESException {
		final ESLocalProject localProject = getProject();

		final ESRangeQuery<?> rangeQuery = ESHistoryQuery.FACTORY.rangeQuery(VERSIONS[3], 2,
			0, false, false, true, false);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(rangeQuery, NPM);

		assertEquals(3, result.size());
		assertEquals(VERSIONS[6], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[5], result.get(1).getPrimarySpec());
		assertEquals(VERSIONS[3], result.get(2).getPrimarySpec());
	}

	@Test
	public void pathQuery() throws ESException {
		final ESLocalProject localProject = getProject();

		final ESPathQuery pathQuery = ESHistoryQuery.FACTORY.pathQuery(VERSIONS[0], VERSIONS[5], false, false);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(pathQuery, NPM);

		assertEquals(4, result.size());
		assertEquals(VERSIONS[0], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[1], result.get(1).getPrimarySpec());
		assertEquals(VERSIONS[3], result.get(2).getPrimarySpec());
		assertEquals(VERSIONS[5], result.get(3).getPrimarySpec());
	}

	@Test
	public void pathQueryInverse() throws ESException {
		final ESLocalProject localProject = getProject();

		final ESPathQuery pathQuery = ESHistoryQuery.FACTORY.pathQuery(VERSIONS[5], VERSIONS[0], false, false);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(pathQuery, NPM);

		assertEquals(4, result.size());
		assertEquals(VERSIONS[5], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[3], result.get(1).getPrimarySpec());
		assertEquals(VERSIONS[1], result.get(2).getPrimarySpec());
		assertEquals(VERSIONS[0], result.get(3).getPrimarySpec());
	}

	@Test
	public void pathQueryAllVersions() throws ESException {
		final ESLocalProject localProject = getProject();

		final ESPathQuery pathQuery = ESHistoryQuery.FACTORY.pathQuery(VERSIONS[1], VERSIONS[3], true, false);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(pathQuery, NPM);

		assertEquals(3, result.size());
		assertEquals(VERSIONS[1], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[2], result.get(1).getPrimarySpec());
		assertEquals(VERSIONS[3], result.get(2).getPrimarySpec());
	}

	@Test(expected = InvalidVersionSpecException.class)
	public void invalidPathQuery() throws ESException {
		final ESLocalProject ploEsLocalProject = getProject();
		final ESPathQuery pathQuery = ESHistoryQuery.FACTORY.pathQuery(VERSIONS[2], VERSIONS[3], false, false);
		ploEsLocalProject.getHistoryInfos(pathQuery, NPM);
	}

	@Test
	public void meQuery() throws ESException {
		final ESLocalProject localProject = getProject();
		final EObject element = localProject.getModelElements().get(0);
		final ESModelElementId modelElementId = localProject.getModelElementId(element);

		final ESModelElementQuery modelElementQuery = ESHistoryQuery.FACTORY.modelElementQuery(VERSIONS[3],
			modelElementId, 0, 0, false, false);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(modelElementQuery, NPM);

		assertEquals(1, result.size());
		assertEquals(VERSIONS[3], result.get(0).getPrimarySpec());
	}

	@Test
	public void meQueryLimit() throws ESException {
		final ESLocalProject localProject = getProject();
		final EObject element = localProject.getModelElements().get(0);
		final ESModelElementId id = localProject.getModelElementId(element);

		final ESModelElementQuery modelElementQuery = ESHistoryQuery.FACTORY.modelElementQuery(VERSIONS[1],
			id, 0, 1, false, false);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(modelElementQuery, NPM);

		assertEquals(2, result.size());
		assertEquals(VERSIONS[1], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[0], result.get(1).getPrimarySpec());
	}

	@Test
	public void meQueryDifferentBranch() throws ESException {
		final ESLocalProject localProject = getProject();
		final EObject element = localProject.getModelElements().get(0);
		final ESModelElementId id = localProject.getModelElementId(element);

		final ESModelElementQuery modelelementQuery = ESHistoryQuery.FACTORY.modelElementQuery(VERSIONS[3],
			id, 1, 1, false, false);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(modelelementQuery, NPM);

		assertEquals(3, result.size());
		assertEquals(VERSIONS[5], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[3], result.get(1).getPrimarySpec());
		assertEquals(VERSIONS[1], result.get(2).getPrimarySpec());
	}

	@Test
	public void meQueryDifferentBranchIncludeAll() throws ESException {
		final ESLocalProject localProject = getProject();
		final EObject element = localProject.getModelElements().get(0);
		final ESModelElementId id = localProject.getModelElementId(element);

		final ESModelElementQuery modelElementQuery = ESHistoryQuery.FACTORY.modelElementQuery(VERSIONS[3],
			id, 1, 1, true, false);
		final List<ESHistoryInfo> result = localProject.getHistoryInfos(modelElementQuery, NPM);

		assertEquals(3, result.size());
		assertEquals(VERSIONS[4], result.get(0).getPrimarySpec());
		assertEquals(VERSIONS[3], result.get(1).getPrimarySpec());
		assertEquals(VERSIONS[2], result.get(2).getPrimarySpec());
	}

	@Test
	public void getSingleVersionViaRangeQuery() throws ESException {
		final ESLocalProject project = Create.project("Test Project"); //$NON-NLS-1$
		project.shareProject(NPM);

		// Create three versions
		Add.toProject(project, Create.testElement("A")); //$NON-NLS-1$
		project.commit(NPM);
		Add.toProject(project, Create.testElement("B")); //$NON-NLS-1$
		project.commit(NPM);
		Add.toProject(project, Create.testElement("C")); //$NON-NLS-1$
		project.commit(NPM);
		Add.toProject(project, Create.testElement("D")); //$NON-NLS-1$
		project.commit(NPM);

		final ESPrimaryVersionSpec wantedVersion = ESVersionSpec.FACTORY.createPRIMARY(1);

		final ESRangeQuery<?> rangeQuery = ESHistoryQuery.FACTORY.rangeQuery(wantedVersion, 1, 0, false, false, false,
			false);
		final List<ESHistoryInfo> historyInfos = project.getHistoryInfos(rangeQuery, NPM);
		assertEquals(2, historyInfos.size());
		assertEquals(2, historyInfos.get(0).getPrimarySpec().getIdentifier());
		assertEquals(1, historyInfos.get(1).getPrimarySpec().getIdentifier());
	}

	@Test
	public void modelElementQueryWithoutIncludingChangePackages() throws ESException {
		final ESLocalProject project = Create.project("Test Project"); //$NON-NLS-1$
		project.shareProject(NPM);

		// Create four versions
		Add.toProject(project, Create.testElement("A")); //$NON-NLS-1$
		project.commit(NPM);
		final TestElement b = Create.testElement("B"); //$NON-NLS-1$
		Add.toProject(project, b);
		project.commit(NPM);
		Add.toProject(project, Create.testElement("C")); //$NON-NLS-1$
		project.commit(NPM);
		Add.toProject(project, Create.testElement("D")); //$NON-NLS-1$
		project.commit(NPM);

		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				b.setName("BB"); //$NON-NLS-1$
			}
		});
		project.commit(NPM);

		final ESModelElementId modelElementId = project.getModelElementId(b);

		final ESModelElementQuery query = ESHistoryQuery.FACTORY.modelElementQuery(
			project.getBaseVersion(), modelElementId, 0, 50, false, false);

		final List<ESHistoryInfo> historyInfos = project.getHistoryInfos(query, NPM);
		assertNull(historyInfos.get(0).getChangePackage());
		assertNull(historyInfos.get(1).getChangePackage());
		assertEquals(historyInfos.size(), 2);
	}

	@Test
	public void modelElementQueryWithIncludingChangePackages() throws ESException {
		final ESLocalProject project = Create.project("Test Project"); //$NON-NLS-1$
		project.shareProject(NPM);

		// Create four versions
		Add.toProject(project, Create.testElement("A")); //$NON-NLS-1$
		project.commit(NPM);
		final TestElement b = Create.testElement("B"); //$NON-NLS-1$
		Add.toProject(project, b);
		project.commit(NPM);
		Add.toProject(project, Create.testElement("C")); //$NON-NLS-1$
		project.commit(NPM);
		Add.toProject(project, Create.testElement("D")); //$NON-NLS-1$
		project.commit(NPM);

		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				b.setName("BB"); //$NON-NLS-1$
			}
		});
		project.commit(NPM);

		final ESModelElementId modelElementId = project.getModelElementId(b);

		final ESModelElementQuery query = ESHistoryQuery.FACTORY.modelElementQuery(
			project.getBaseVersion(), modelElementId, 0, 50, false, true);

		final List<ESHistoryInfo> historyInfos = project.getHistoryInfos(query, NPM);
		assertNotNull(historyInfos.get(0).getChangePackage());
		assertNotNull(historyInfos.get(1).getChangePackage());
		assertEquals(historyInfos.size(), 2);
	}
}
