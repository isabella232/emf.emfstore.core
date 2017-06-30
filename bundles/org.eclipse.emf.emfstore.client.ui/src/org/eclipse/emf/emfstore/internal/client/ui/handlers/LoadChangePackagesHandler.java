/*******************************************************************************
 * Copyright (c) 2016 Metus GmbH
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * mbarchfe
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.handlers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ConnectionManager;
import org.eclipse.emf.emfstore.internal.client.ui.views.historybrowserview.HistoryBrowserView;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for getting a list of change packages for given {@link HistoryInfo}s .
 *
 * @author mbarchfe
 *
 */
public class LoadChangePackagesHandler extends AbstractEMFStoreHandler {

	/**
	 * Gets the selection from an {@link ExecutionEvent}. Only elements which are instances of the given class will be
	 * returned.
	 *
	 * @param event the event
	 * @param clazz the type
	 * @return the selection
	 *
	 * @param <T> the type
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getSelection(ExecutionEvent event, Class<T> clazz) {
		final List<T> result = new ArrayList<T>();
		ISelection sel = HandlerUtil.getCurrentSelection(event);
		if (sel == null) {
			sel = HandlerUtil.getActiveMenuSelection(event);
		}
		if (sel instanceof IStructuredSelection) {
			final IStructuredSelection structuredSelection = (IStructuredSelection) sel;
			@SuppressWarnings("rawtypes")
			final Iterator it = structuredSelection.iterator();
			while (it.hasNext()) {
				final Object selectedElement = it.next();
				if (clazz.isInstance(selectedElement)) {
					result.add((T) selectedElement);
				}
			}
		}
		return result;
	}

	@Override
	public void handle() {

		final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage == null) {
			return;
		}

		if (!(activePage.getActivePart() instanceof HistoryBrowserView)) {
			return;
		}

		final HistoryBrowserView view = (HistoryBrowserView) activePage.getActivePart();
		final ProjectSpace projectSpace = view.getProjectSpace();
		final ESWorkspaceProviderImpl esWorkspaceProviderImpl = ESWorkspaceProviderImpl.getInstance();

		final ConnectionManager connectionManager = esWorkspaceProviderImpl.getConnectionManager();

		final ProjectId projectId = projectSpace.getProjectId();
		final SessionId sessionId = projectSpace.getUsersession().getSessionId();

		final List<HistoryInfo> historyInfos = getSelection(getEvent(), HistoryInfo.class);
		for (final HistoryInfo historyInfo : historyInfos) {
			final PrimaryVersionSpec fromSpec = ModelUtil.clone(historyInfo.getPreviousSpec());
			final PrimaryVersionSpec toSpec = ModelUtil.clone(historyInfo.getPrimarySpec());
			// e.g. local change would be invalid to send to server
			if (!isValid(fromSpec) || !isValid(toSpec)) {
				continue;
			}

			try {
				final List<AbstractChangePackage> changes = connectionManager.getChanges(sessionId, projectId,
					fromSpec,
					toSpec);
				// the result should be one change package, if there were more or none this would be suprising
				if (changes.size() != 1) {
					ModelUtil.log(
						MessageFormat.format("Expected to retrieve one change package but got {0}", changes.size()), //$NON-NLS-1$
						null, IStatus.ERROR);
				} else {
					historyInfo.setChangePackage(changes.get(0));
				}
			} catch (final ESException ex) {
				ModelUtil.log(
					MessageFormat.format("Could not load changes for history info {0}", historyInfo), //$NON-NLS-1$
					ex, IStatus.ERROR);
			}
			view.refresh(historyInfo);
		}

	}

	private boolean isValid(PrimaryVersionSpec spec) {
		return spec != null && spec.getIdentifier() > -1;
	}

}
