/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.core.subinterfaces;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util.ChangePackageUtil;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * Adapter that is supposed to be attached to a session and split incoming complete change packages
 * into single fragments.
 *
 * @author emueller
 *
 */
public class ChangePackageFragmentProviderAdapter extends AdapterImpl {

	private final Map<String, Map<Integer, List<AbstractOperation>>> proxyIdToChangePackageFragments =
		new LinkedHashMap<String, Map<Integer, List<AbstractOperation>>>();

	/**
	 * Splits the given change package into fragments and stores them.
	 *
	 * @param proxyId
	 *            an ID that needs to be unique for the change package to be splitted.
	 *            Necessary when there are multiple change packages available per project
	 * @param changePackage
	 *            the change package to be splitted
	 */
	public void addAsFragments(String proxyId, AbstractChangePackage changePackage) {

		final Iterator<ChangePackageEnvelope> envelopes = ChangePackageUtil.splitChangePackage(
			changePackage,
			ServerConfiguration.getChangePackageFragmentSize().get());

		while (envelopes.hasNext()) {
			addFragment(proxyId, envelopes.next().getFragment());
		}
	}

	private void addFragment(String proxyId, List<AbstractOperation> fragment) {
		Map<Integer, List<AbstractOperation>> map = proxyIdToChangePackageFragments.get(proxyId);
		if (map == null) {
			map = new LinkedHashMap<Integer, List<AbstractOperation>>();
			proxyIdToChangePackageFragments.put(proxyId, map);
		}
		final int currentSize = map.size();
		map.put(currentSize, fragment);
	}

	/***
	 * Returns a single fragment.
	 *
	 * @param proxyId
	 *            ID that identifies a list of fragments
	 * @param fragmentIndex
	 *            the index of the fragment to be returned
	 * @return the fragment
	 * @throws ESException in case no fragments for the given proxy ID are present
	 */
	public List<AbstractOperation> getFragment(String proxyId, int fragmentIndex) throws ESException {
		final Map<Integer, List<AbstractOperation>> fragments = proxyIdToChangePackageFragments.get(proxyId);
		if (fragments == null) {
			throw new ESException(Messages.ChangePackageFragmentProviderAdapter_NoFragmentsFound);
		}
		return fragments.get(fragmentIndex);
	}

	/**
	 * Returns the number of fragments for the given ID.
	 *
	 * @param proxyId
	 *            the ID identifying the list of change package fragments
	 * @return the number of available fragments
	 */
	public int getFragmentSize(String proxyId) {
		final Map<Integer, List<AbstractOperation>> map = proxyIdToChangePackageFragments.get(proxyId);
		if (map == null) {
			return -1;
		}
		return map.size();
	}

	/**
	 * Removes the fragments that belong to given ID.
	 *
	 * @param proxyId
	 *            identifies the change package fragments to be removed
	 */
	public void markAsConsumed(String proxyId) {
		proxyIdToChangePackageFragments.remove(proxyId);
	}

}
