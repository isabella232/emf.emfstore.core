/*******************************************************************************
 * Copyright (c) 2011-2017 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.accesscontrol;

import java.util.Map.Entry;

import org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.ACUserContainer;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESUserImpl;
import org.eclipse.emf.emfstore.server.auth.ESSessions;
import org.eclipse.emf.emfstore.server.model.ESSessionId;
import org.eclipse.emf.emfstore.server.model.ESUser;

/**
 * Internal {@link ESSessions} implementation.
 */
public class EMFStoreSessions extends ESSessions {

	/**
	 * Given an user ID, tries to find a session.
	 *
	 * @param user the ID of the user whose session should be retrieved
	 * @return the session ID, if any, {@code null} otherwise
	 * @since 1.9
	 */
	public ESSessionId resolveByUser(ESUser user) {
		final ACOrgUnitId userId = ((ESUserImpl) user).toInternalAPI().getId();
		for (final Entry<SessionId, ACUserContainer> entry : sessionUserMap.entrySet()) {
			final ACUserContainer container = entry.getValue();
			if (container.getRawUser().getId().equals(userId)) {
				return entry.getKey().toAPI();
			}
		}

		return null;
	}
}
