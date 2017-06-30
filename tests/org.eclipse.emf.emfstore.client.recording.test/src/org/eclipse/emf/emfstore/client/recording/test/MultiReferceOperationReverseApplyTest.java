/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.recording.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.bowling.BowlingFactory;
import org.eclipse.emf.emfstore.bowling.League;
import org.eclipse.emf.emfstore.bowling.Player;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.callbacks.ESUpdateCallback;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithSharedProject;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.common.model.ESModelElementIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.ChecksumErrorHandler;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.server.ESConflict;
import org.eclipse.emf.emfstore.server.ESConflictSet;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.ESOperation;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MultiReferceOperationReverseApplyTest extends ESTestWithSharedProject {

	private static final int NUM_PLAYERS = 6;

	private League league;

	private final ESUpdateCallback keepMyChanges = new KeepMyChanges();
	private final ESUpdateCallback keepTheirChanges = new KeepTheirChanges();

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Override
	@Before
	public void before() {
		org.eclipse.emf.emfstore.internal.client.model.Configuration.getClientBehavior().setChecksumErrorHandler(
			ChecksumErrorHandler.CANCEL);

		super.before();
		league = BowlingFactory.eINSTANCE.createLeague();

		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				getProject().addModelElement(league);
			}
		});

		for (int i = 0; i < NUM_PLAYERS; i++) {
			final Player player = BowlingFactory.eINSTANCE.createPlayer();
			league.getPlayers().add(player);
		}

		try {
			ProjectUtil.commit(getLocalProject());
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void reverseTest() throws ESException, SerializationException {
		// final ESLocalProject copy = ProjectUtil.checkout(getLocalProject());

		final ESLocalProject copy = ProjectUtil.checkout(getLocalProject());
		final ProjectSpace copiedProjectSpace = ESLocalProjectImpl.class.cast(copy).toInternalAPI();
		final League copiedLeague = (League) copiedProjectSpace.getProject().getModelElements().get(0);

		// make parallel changes
		mutate(league, 1);
		mutate(league, 3);
		mutate(copiedLeague, 2);
		mutate(copiedLeague, 2);

		ProjectUtil.commit(getLocalProject());
		ProjectUtil.update(copy, keepMyChanges);

		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				league.getPlayers().get(0).setName("abc"); //$NON-NLS-1$
			}
		});
		ProjectUtil.commit(getLocalProject());

		ProjectUtil.update(copy, keepTheirChanges);

		ProjectUtil.commit(copy);

		ProjectUtil.update(getLocalProject(), keepTheirChanges);

		assertTrue(ModelUtil.areEqual(
			getProject(),
			copiedProjectSpace.getProject()));

	}

	private void mutate(final League league, final int deleteIndex) {
		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				league.getPlayers().remove(deleteIndex);
			}
		});
	}

	class KeepMyChanges implements ESUpdateCallback {
		public void noChangesOnServer() {
			// do nothing if there are no changes on the server (in this example we know
			// there are changes anyway)
		}

		public boolean inspectChanges(ESLocalProject project, List<ESChangePackage> changes,
			ESModelElementIdToEObjectMapping idToEObjectMapping) {
			// allow update to proceed, here we could also add some UI
			return true;
		}

		public boolean conflictOccurred(ESConflictSet changeConflictSet, IProgressMonitor monitor) {

			final Iterator<ESConflict> iterator = changeConflictSet.getConflicts().iterator();

			while (iterator.hasNext()) {
				final ESConflict conflict = iterator.next();
				conflict.resolveConflict(conflict.getLocalOperations(), conflict.getRemoteOperations());
			}

			return true;
		}
	}

	class KeepTheirChanges implements ESUpdateCallback {
		public void noChangesOnServer() {
			// do nothing if there are no changes on the server (in this example we know
			// there are changes anyway)
		}

		public boolean inspectChanges(ESLocalProject project, List<ESChangePackage> changes,
			ESModelElementIdToEObjectMapping idToEObjectMapping) {
			// allow update to proceed, here we could also add some UI
			return true;
		}

		public boolean conflictOccurred(ESConflictSet changeConflictSet, IProgressMonitor monitor) {

			// One or more conflicts have occured, they are delivered in a change conflict set
			// We know there is only one conflict so we grab it
			final Iterator<ESConflict> iterator = changeConflictSet.getConflicts().iterator();

			while (iterator.hasNext()) {
				final ESConflict conflict = iterator.next();
				conflict.resolveConflict(Collections.<ESOperation> emptySet(), Collections.<ESOperation> emptySet());
			}
			// Finally we claim to have resolved all conflicts so update will try to proceed.
			return true;
		}
	}
}
