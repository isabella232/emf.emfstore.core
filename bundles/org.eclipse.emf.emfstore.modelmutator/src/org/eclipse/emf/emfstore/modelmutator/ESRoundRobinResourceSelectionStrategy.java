/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.modelmutator;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * A {@link ESResourceSelectionStrategy} that selects each resource from the list of resources in a round-robin fashion.
 *
 * @author emueller
 * @since 2.0
 *
 */
public class ESRoundRobinResourceSelectionStrategy implements ESResourceSelectionStrategy {

	private int currentResourceIndex;

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESResourceSelectionStrategy#selectResource(java.util.List)
	 */
	public Resource selectResource(List<Resource> resources) {
		final Resource selectedResource = resources.get(currentResourceIndex % resources.size());
		currentResourceIndex += 1;
		return selectedResource;
	}

}
