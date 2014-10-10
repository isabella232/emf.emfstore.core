/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Langer - initial API and implementation
 * Edgar Mueller - API layer
 ******************************************************************************/
package org.eclipse.emf.emfstore.modelmutator;

/**
 * Mutation for mutating the containment tree of models.
 * 
 * @param <T> the actual containment change mutation type
 * 
 * @author Philip Langer
 * @author emueller
 * @since 2.0
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ESContainmentChangeMutation<T extends ESContainmentChangeMutation<?>> extends
	ESStructuralFeatureMutation<T> {

}
