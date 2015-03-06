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

import java.util.Date;

import org.eclipse.emf.emfstore.internal.common.api.AbstractAPIImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.server.model.ESLogMessage;

/**
 * API implementation class for a {@link LogMessage}.
 *
 * @author emueller
 *
 */
public class ESLogMessageImpl extends AbstractAPIImpl<ESLogMessage, LogMessage> implements ESLogMessage {

	/**
	 * Constructor.
	 *
	 * @param logMessage
	 *            the internal {@link LogMessage}
	 */
	public ESLogMessageImpl(LogMessage logMessage) {
		super(logMessage);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESLogMessage#getMessage()
	 */
	public String getMessage() {
		return toInternalAPI().getMessage();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESLogMessage#getAuthor()
	 */
	public String getAuthor() {
		return toInternalAPI().getAuthor();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESLogMessage#getClientDate()
	 */
	public Date getClientDate() {
		return toInternalAPI().getClientDate();
	}

}
