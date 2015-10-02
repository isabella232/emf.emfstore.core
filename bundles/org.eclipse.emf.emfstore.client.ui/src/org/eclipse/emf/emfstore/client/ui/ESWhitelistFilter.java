/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.ui;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * <p>
 * As the {@link ESClassFilter} this filter interface is used to separate specific types and mark them
 * as 'filtered' in the UI. Filtered types are considered as non-critical for
 * an understanding of the problem domain and therefore are treated special in the UI,
 * e.g. by grouping all operations involving only filtered type as is the case in the
 * update, commit and merge details dialog.
 * </p>
 * <p>
 * The difference to the {@link ESClassFilter} is that some {@link EStructuralFeature features} of a filtered
 * {@link EClass} may be marked as critical for understanding the problem domain, meaning that operations involving the
 * marked features will not be treated different.
 * </p>
 *
 * @author Johannes Faltermeier
 * @noextend This interface is not intended to be extended by clients.
 * @since 1.6
 *
 */
public interface ESWhitelistFilter extends ESClassFilter {

	/**
	 * Returns the {@link EStructuralFeature features} which will be regarded as non-filtered if they appear in an
	 * operation. It is expected that all keys from this map have also been added to {@link #getFilteredEClasses()} by
	 * either this filter or a different one.
	 *
	 * @return the whitelist
	 */
	Map<EClass, Collection<EStructuralFeature>> getNonFilteredFeaturesForEClass();

}
