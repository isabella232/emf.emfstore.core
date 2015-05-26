/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.impl;

import org.eclipse.emf.emfstore.client.handler.ESOperationModifier;

/**
 * Encapsulates configuration options for the operation recorder.
 *
 * @author emueller
 */
public class OperationRecorderConfig {

	private boolean isCutOffIncomingCrossReferences;
	private boolean isDenyAddCutElementsToModelElements;
	private boolean isRollBackInCaseOfCommandFailure;
	private boolean isForceCommands;
	private boolean isEmitOperationsUponCommandCompletion = true;
	private ESOperationModifier operationModifier;

	/**
	 * Whether to cut off incoming cross references upon deletion.
	 *
	 * @return true, if incoming cross references are cut off, false otherwise
	 */
	public Boolean isCutOffIncomingCrossReferences() {
		return isCutOffIncomingCrossReferences;
	}

	/**
	 * Whether cut elements are added as regular model elements at the end of a command.
	 *
	 * @return false, if cut elements should get added as regular model elements to the project at the end of
	 *         command,
	 *         true otherwise
	 */
	public Boolean isDenyAddCutElementsToModelElements() {
		return isDenyAddCutElementsToModelElements;
	}

	/**
	 * Whether the usage of commands should be enforced.
	 *
	 * @return
	 * 		true, if the usage of commands is mandatory, false otherwise
	 */
	public Boolean isForceCommands() {
		return isForceCommands;
	}

	/**
	 * Whether a rollback should be performed in case a command fails.
	 *
	 * @return true, if a rollback should be performed, false otherwise
	 */
	public Boolean isRollbackAtCommandFailure() {
		return isRollBackInCaseOfCommandFailure;
	}

	/**
	 * Whether recoreded operations are emitted instantly or at command completion.
	 *
	 * @return true, if operations are emitted when a command completes, false otherwise
	 */
	public Boolean isEmitOperationsUponCommandCompletion() {
		return isEmitOperationsUponCommandCompletion;
	}

	/**
	 * Whether to emit the recorded instantly or at command completion.
	 *
	 * @param shouldEmitOperationsUponCommandCompletion
	 *            true, if operations should only be emitted when a command completes, false otherwise
	 */

	/**
	 * Whether to cut off incoming cross references upon deletion.
	 *
	 * @param shouldCutOffIncomingCrossReferences
	 *            true, if incoming cross references should be cut off, false otherwise
	 */
	public void setCutOffIncomingCrossReferences(boolean shouldCutOffIncomingCrossReferences) {
		isCutOffIncomingCrossReferences = shouldCutOffIncomingCrossReferences;
	}

	/**
	 * Whether cut elements are added as regular model elements at the end of a command.
	 *
	 * @param shouldDenyAddCutElementsToModelElements
	 *            true, if cut elements should get added as regular model elements at the end of a command, false
	 *            otherwise
	 */
	// TODO: rename configuration option
	public void setDenyAddCutElementsToModelElements(Boolean shouldDenyAddCutElementsToModelElements) {
		isDenyAddCutElementsToModelElements = shouldDenyAddCutElementsToModelElements;
	}

	/**
	 * Whether the usage of commands should be enforced.
	 *
	 * @param shouldForceCommands
	 *            true, if the usage of commands should be mandatory, false otherwise
	 */
	public void setForceCommands(Boolean shouldForceCommands) {
		isForceCommands = shouldForceCommands;
	}

	/**
	 * Whether a rollback should be performed in case a command fails.
	 *
	 * @param shouldPerformRollback
	 *            true, if a rollback should be performed false otherwise
	 */
	public void setRollBackInCaseOfCommandFailure(Boolean shouldPerformRollback) {
		isRollBackInCaseOfCommandFailure = shouldPerformRollback;
	}

	/**
	 * Whether to emit the recorded instantly or at command completion.
	 *
	 * @param shouldEmitOperationsUponCommandCompletion
	 *            true, if operations should only be emitted when a command completes, false otherwise
	 */
	// TODO: call this from the operationManager initialization?
	public void setEmitOperationsUponCommandCompletion(Boolean shouldEmitOperationsUponCommandCompletion) {
		isEmitOperationsUponCommandCompletion = shouldEmitOperationsUponCommandCompletion;
	}

	/**
	 * Set the operation modifier that enables to modify operations whenever they have been recored
	 *
	 * @param operationModifier
	 *            the operation modifier to be set
	 */
	public void setOperationModifier(ESOperationModifier operationModifier) {
		this.operationModifier = operationModifier;
	}

	/**
	 * Returns the operation modifier in use.
	 *
	 * @return the operation in use
	 */
	public ESOperationModifier getOperationModifier() {
		return operationModifier;
	}
}