/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.common.model;

import org.eclipse.emf.emfstore.common.model.ESIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.common.api.APIDelegate;
import org.eclipse.emf.emfstore.internal.common.model.impl.ESModelElementIdToEObjectMappingImpl;

/**
 * @author emueller
 *
 */
public interface ModelElementIdToEObjectMapping
	extends ESIdToEObjectMapping<ModelElementId>, APIDelegate<ESModelElementIdToEObjectMappingImpl> {

}
