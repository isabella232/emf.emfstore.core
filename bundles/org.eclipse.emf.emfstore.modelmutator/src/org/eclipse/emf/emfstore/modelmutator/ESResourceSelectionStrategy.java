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
 * A {@link ESResourceSelectionStrategy} selects a resource from a given list of resources.
 * 
 * @author emueller
 * @since 2.0
 * 
 */
public interface ESResourceSelectionStrategy {

	/**
	 * Selects a {@link Resource} from a list of given resources.
	 * 
	 * @param resources
	 *            the resources to select a resource from
	 * @return the selected resource
	 */
	Resource selectResource(List<Resource> resources);

}
