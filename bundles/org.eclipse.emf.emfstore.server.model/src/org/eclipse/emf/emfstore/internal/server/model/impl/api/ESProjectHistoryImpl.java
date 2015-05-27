/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.impl.api;

import org.eclipse.emf.emfstore.internal.common.api.AbstractAPIImpl;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.server.model.ESProjectHistory;

/**
 * The API implementation class for an {@link ESProjectHistory}.
 *
 * @author emueller
 *
 */
public class ESProjectHistoryImpl extends AbstractAPIImpl<ESProjectHistory, ProjectHistory> implements ESProjectHistory {

	/**
	 * Constructor.
	 *
	 * @param projectHistory
	 *            the internal representation of a project history
	 */
	public ESProjectHistoryImpl(ProjectHistory projectHistory) {
		super(projectHistory);
	}

}
