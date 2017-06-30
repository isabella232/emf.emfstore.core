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
package org.eclipse.emf.emfstore.client.test.ui.controllers;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIAddTagController;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIRemoveTagController;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESHistoryInfo;
import org.eclipse.emf.emfstore.server.model.query.ESHistoryQuery;
import org.eclipse.emf.emfstore.server.model.query.ESPathQuery;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.junit.Test;

public class UIAddTagControllerTest extends AbstractUIControllerTestWithCommit {

	@Override
	@Test
	public void testController() throws ESException {
		final ESPathQuery pathQuery = createTag();
		final List<ESHistoryInfo> historyInfos = getLocalProject().getHistoryInfos(pathQuery,
			new NullProgressMonitor());
		assertEquals(2, historyInfos.size());
		final ESHistoryInfo historyInfo2 = historyInfos.get(1);
		assertEquals(3, historyInfo2.getTagSpecs().size());
		removeTag(historyInfo2, pathQuery);
	}

	private void removeTag(final ESHistoryInfo historyInfo, ESPathQuery pathQuery) throws ESException {
		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				final UIRemoveTagController removeTagController = new UIRemoveTagController(
					getBot().getDisplay().getActiveShell(), historyInfo);
				removeTagController.execute();
			}
		});
		final List<ESHistoryInfo> historyInfos = getLocalProject().getHistoryInfos(pathQuery,
			new NullProgressMonitor());
		assertEquals(2, historyInfos.size());
	}

	private ESPathQuery createTag() throws ESException {
		final ESPrimaryVersionSpec baseVersion = getLocalProject().getBaseVersion();
		createPlayerAndCommit();
		final ESPathQuery pathQuery = ESHistoryQuery.FACTORY
			.pathQuery(baseVersion, getLocalProject().getBaseVersion(), true, true);
		final List<ESHistoryInfo> historyInfos = getLocalProject().getHistoryInfos(pathQuery,
			new NullProgressMonitor());
		assertEquals(2, historyInfos.size());
		final ESHistoryInfo historyInfo = historyInfos.get(1);
		assertEquals(2, historyInfo.getTagSpecs().size());
		UIThreadRunnable.asyncExec(
			new VoidResult() {
				public void run() {
					final UIAddTagController addTagController = new UIAddTagController(
						getBot().getDisplay().getActiveShell(),
						getLocalProject(), historyInfo);
					addTagController.execute();
				}
			});

		getBot().table(0).select(0);
		final SWTBotButton button = getBot().button("OK");
		button.click();
		return pathQuery;
	}
}
