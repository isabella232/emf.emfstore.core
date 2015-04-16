/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.emfstore.common.model.ESModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.impl.ESModelElementIdImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.query.ESModelElementQueryImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.query.ESRangeQueryImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.versionspec.ESPrimaryVersionSpecImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ModelElementQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PathQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery;
import org.eclipse.emf.emfstore.server.model.query.ESHistoryQueryFactory;
import org.eclipse.emf.emfstore.server.model.query.ESModelElementQuery;
import org.eclipse.emf.emfstore.server.model.query.ESPathQuery;
import org.eclipse.emf.emfstore.server.model.query.ESRangeQuery;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;

/**
 * Factory for creating {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery HistoryQueries}.
 */
public final class HistoryQueryFactoryImpl implements ESHistoryQueryFactory {

	/**
	 * The instance.
	 */
	public static final HistoryQueryFactoryImpl INSTANCE = new HistoryQueryFactoryImpl();

	private HistoryQueryFactoryImpl() {
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.query.ESHistoryQueryFactory#rangeQuery(org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec,
	 *      int, int, boolean, boolean, boolean, boolean)
	 */
	public ESRangeQuery<?> rangeQuery(ESPrimaryVersionSpec source, int upper, int lower, boolean allVersions,
		boolean incoming, boolean outgoing, boolean includeChangePackages) {

		final PrimaryVersionSpec sourceVersionSpec = ((ESPrimaryVersionSpecImpl) source).toInternalAPI();

		final RangeQuery<?> rangeQuery = HistoryQueryBuilder.rangeQuery(sourceVersionSpec, upper, lower, allVersions,
			incoming, outgoing, includeChangePackages);

		final ESRangeQueryImpl<?, ?> apiImpl = (ESRangeQueryImpl<?, ?>) rangeQuery.toAPI();
		return apiImpl;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.query.ESHistoryQueryFactory#pathQuery(org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec,
	 *      org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec, boolean, boolean)
	 */
	public ESPathQuery pathQuery(ESPrimaryVersionSpec source, ESPrimaryVersionSpec target, boolean allVersions,
		boolean includeChangePackages) {
		final PrimaryVersionSpec sourceVersionSpec = ((ESPrimaryVersionSpecImpl) source).toInternalAPI();
		final PrimaryVersionSpec targetVersionSpec = ((ESPrimaryVersionSpecImpl) target).toInternalAPI();
		final PathQuery pathQuery = HistoryQueryBuilder.pathQuery(sourceVersionSpec, targetVersionSpec, allVersions,
			includeChangePackages);
		return pathQuery.toAPI();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.query.ESHistoryQueryFactory#modelElementQuery(org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec,
	 *      java.util.List, int, int, boolean, boolean)
	 */
	public ESModelElementQuery modelElementQuery(ESPrimaryVersionSpec source, List<ESModelElementId> modelElements,
		int upper, int lower, boolean allVersions, boolean includeChangePackages) {

		// TODO: provide util method for mapping to internal classes
		final List<ModelElementId> modelElementIds = new ArrayList<ModelElementId>();
		for (final ESModelElementId id : modelElements) {
			modelElementIds.add(((ESModelElementIdImpl) id).toInternalAPI());
		}

		final PrimaryVersionSpec sourcePrimaryVersionSpec = ((ESPrimaryVersionSpecImpl) source).toInternalAPI();

		final ModelElementQuery modelelementQuery = HistoryQueryBuilder.modelelementQuery(sourcePrimaryVersionSpec,
			modelElementIds, upper, lower, allVersions, includeChangePackages);

		final ESModelElementQueryImpl apiImpl = (ESModelElementQueryImpl) modelelementQuery.toAPI();
		return apiImpl;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.query.ESHistoryQueryFactory#modelElementQuery(org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec,
	 *      org.eclipse.emf.emfstore.common.model.ESModelElementId, int, int, boolean, boolean)
	 */
	public ESModelElementQuery modelElementQuery(ESPrimaryVersionSpec source, ESModelElementId id, int upper,
		int lower, boolean allVersions, boolean includeCp) {
		final ModelElementQuery modelelementQuery = HistoryQueryBuilder.modelelementQuery(
			((ESPrimaryVersionSpecImpl) source).toInternalAPI(), ((ESModelElementIdImpl) id).toInternalAPI(), upper,
			lower, allVersions, includeCp);
		return modelelementQuery.toAPI();
	}

}
