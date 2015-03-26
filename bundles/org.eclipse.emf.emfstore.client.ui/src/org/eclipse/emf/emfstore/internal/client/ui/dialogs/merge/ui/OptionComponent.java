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

/**
 * Merge component showing an option for resolving a conflict.
 *
 * @author emueller
 *
 */
public interface OptionComponent extends MergeComponent {

	/**
	 * Refreshes the button color of the option.
	 */
	void refreshButtonColor();

}
