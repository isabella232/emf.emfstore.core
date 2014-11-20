/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui;

import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;

/**
 * Interface for a singe merge component.
 * 
 * @author emueller
 * 
 */
public interface MergeComponent {

	/**
	 * Sets the parent of this merge component.
	 * 
	 * @param decisionBox
	 *            the parent {@link DecisionBox}
	 */
	void setParent(DecisionBox decisionBox);

	/**
	 * Sets the conflict this merge component belongs to.
	 * 
	 * @param visualConflict
	 *            the {@link VisualConflict} this merge component belongs to
	 */
	void setVisualConflict(VisualConflict visualConflict);
}
