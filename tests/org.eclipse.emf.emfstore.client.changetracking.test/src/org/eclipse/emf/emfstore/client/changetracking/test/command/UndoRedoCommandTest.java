/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.changetracking.test.command;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTest;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.client.model.util.AbstractEMFStoreCommand;
import org.eclipse.emf.emfstore.internal.client.observers.OperationObserver;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.Test;

/**
 * Test that demonstrates
 *
 *
 * @author emueller
 *
 */
public class UndoRedoCommandTest extends ESTest {

	public class MyCommand extends AbstractCommand {

		private final ProjectSpaceBase projectSpace;
		private final List<AbstractOperation> executedOperations;
		private boolean canUndo;
		private final Runnable runnable;

		public MyCommand(ProjectSpaceBase projectSpace, Runnable runnable) {
			this.projectSpace = projectSpace;
			this.runnable = runnable;
			executedOperations = new ArrayList<AbstractOperation>();
		}

		/**
		 * {@inheritDoc}
		 *
		 * @see org.eclipse.emf.common.command.Command#execute()
		 */
		@SuppressWarnings("restriction")
		public void execute() {
			final CommandStack commandStack = ESWorkspaceProviderImpl.getInstance()
				.getInternalWorkspace()
				.getEditingDomain()
				.getCommandStack();

			// add operation observer during command execution to track executed operations
			final OperationObserver operationObserver = createOperationObserver();
			ESWorkspaceProviderImpl.getObserverBus().register(operationObserver);
			commandStack.execute(new AbstractEMFStoreCommand() {
				@Override
				protected void commandBody() {
					runnable.run();
				}
			});
			ESWorkspaceProviderImpl.getObserverBus().unregister(operationObserver);

			canUndo = true;
		}

		private OperationObserver createOperationObserver() {
			final OperationObserver operationObserver = new OperationObserver() {

				public void operationExecuted(ProjectSpace projectSpace, AbstractOperation operation) {
					executedOperations.add(operation);
				}

				public void operationUndone(ProjectSpace projectSpace, AbstractOperation operation) {

				}
			};
			return operationObserver;
		}

		public void redo() {

			RunESCommand.run(new Callable<Void>() {
				public Void call() throws Exception {
					projectSpace.applyOperations(executedOperations, true);
					return null;
				}
			});

			canUndo = true;
		}

		@Override
		public boolean canUndo() {
			return canUndo;
		}

		@Override
		public void undo() {
			RunESCommand.run(new Callable<Void>() {
				public Void call() throws Exception {
					projectSpace.undoLastOperations(executedOperations.size());
					return null;
				}
			});

			canUndo = false;
		}

	}

	@Test
	public void undo() {

		final TestElement leafSection = Create.testElement();

		final MyCommand cmd = new MyCommand((ProjectSpaceBase) getProjectSpace(), new Runnable() {
			public void run() {
				ProjectUtil.addElement(getProjectSpace().toAPI(), leafSection);
			}
		});

		cmd.execute();
		cmd.undo();
		assertEquals(0, getLocalProject().getModelElements().size());
	}

	@Test
	public void redo() {

		final TestElement leafSection = Create.testElement();

		final MyCommand cmd = new MyCommand((ProjectSpaceBase) getProjectSpace(), new Runnable() {
			public void run() {
				ProjectUtil.addElement(getProjectSpace().toAPI(), leafSection);
			}
		});

		cmd.execute();
		cmd.undo();
		cmd.redo();
		assertEquals(1, getLocalProject().getModelElements().size());
	}
}
