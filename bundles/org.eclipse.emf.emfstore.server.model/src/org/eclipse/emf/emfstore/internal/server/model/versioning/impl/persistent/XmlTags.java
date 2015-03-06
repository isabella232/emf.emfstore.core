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
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent;

/**
 * @author emueller
 *
 */
public final class XmlTags {

	private XmlTags() {
		// private ctor
	}

	/**
	 * Newline constant.
	 */
	public static final String NEWLINE = "\n"; //$NON-NLS-1$

	/**
	 * XML header.
	 */
	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE + //$NON-NLS-1$
		"<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\"/>" + NEWLINE; //$NON-NLS-1$

	/**
	 * Change package opening tag.
	 */
	public static final String CHANGE_PACKAGE_START = "<org.eclipse.emf.emfstore.internal.server.model.versioning:ChangePackage " //$NON-NLS-1$
		+ "xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:org.eclipse.emf.emfstore.internal.server.model.versioning=\"http://eclipse.org/emf/emfstore/server/model/versioning\" xmlns:org.eclipse.emf.emfstore.internal.server.model.versioning.operations=\"http://eclipse.org/emf/emfstore/server/model/versioning/operations\">" //$NON-NLS-1$
		+ NEWLINE;

	/**
	 * Change package closing tag.
	 */
	public static final String CHANGE_PACKAGE_END = "</org.eclipse.emf.emfstore.internal.server.model.versioning:ChangePackage>"; //$NON-NLS-1$

	/**
	 * Operations opening tag ({@code <operations>}).
	 */
	public static final String OPERATIONS_START_TAG = "<operations>"; //$NON-NLS-1$

	/**
	 * Operations closing tag (<&#47;operations>).
	 */
	public static final String OPERATIONS_END_TAG = "</operations>"; //$NON-NLS-1$
}
