/*******************************************************************************
 * Copyright (c) 2008-2014 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Maximilian Koegel - initial API and implementation
 * Edgar Mueller - ObserverBus refactoring
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.changeTracking.commands;

import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.observer.ObserverCall;
import org.eclipse.emf.emfstore.internal.common.observer.ObserverCall.Result;

/**
 * Notifier for Commands. Notifies Observers about command start, completion and failure.
 * 
 * @author koegel
 */
public class EMFStoreCommandNotifier {

	/**
	 * Notify all registered listeners about command start.
	 * 
	 * @param startedCommand
	 *            the command that has been started
	 */
	public void notifiyListenersAboutStart(final Command startedCommand) {
		final ESCommandObserver commandObserver = ESWorkspaceProviderImpl
			.getObserverBus()
			.notify(ESCommandObserver.class);
		commandObserver.commandStarted(startedCommand);

		logExceptions(commandObserver);
	}

	/**
	 * Notify all registered listeners about command failure.
	 * 
	 * @param command the command
	 * @param exception the exception that triggered the failure
	 */
	public void notifiyListenersAboutCommandFailed(final Command command, final Exception exception) {

		final ESCommandObserver commandObserver = ESWorkspaceProviderImpl
			.getObserverBus()
			.notify(ESCommandObserver.class);
		commandObserver.commandFailed(command, exception);

		logExceptions(commandObserver);
	}

	/**
	 * Notify all registered listeners about command completion.
	 * 
	 * @param completedCommand
	 *            the completed command
	 */
	public void notifiyListenersAboutCommandCompleted(final Command completedCommand) {
		final ESCommandObserver commandObserver = ESWorkspaceProviderImpl
			.getObserverBus()
			.notify(ESCommandObserver.class);
		commandObserver.commandCompleted(completedCommand);

		logExceptions(commandObserver);
	}

	private void logExceptions(final ESCommandObserver commandObserver) {
		final ObserverCall observerCall = ObserverCall.class.cast(commandObserver);

		final List<Result> results = observerCall.getObserverCallResults();

		for (final Result result : results) {
			if (result.getException() != null) {
				ModelUtil.logWarning(Messages.EMFStoreCommandNotifier_CommandObserverException, result.getException());
			}
		}
	}

	/**
	 * Add a command stack observer.
	 * 
	 * @param observer
	 *            the observer to be added
	 */
	public void addCommandStackObserver(ESCommandObserver observer) {
		ESWorkspaceProviderImpl
			.getObserverBus()
			.register(observer);
	}

	/**
	 * Remove a command stack observer.
	 * 
	 * @param observer
	 *            the observer to be removed
	 */
	public void removeCommandStackObserver(ESCommandObserver observer) {
		ESWorkspaceProviderImpl
			.getObserverBus()
			.unregister(observer);
	}

}
