/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Julian Sommerfeldt - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESDefaultModelMutator;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyUtil;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.FuzzyUtil;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;

/**
 * A {@link ESFuzzyUtil} class for tests using the {@link ESEMFDataProvider}.
 * 
 * @author Julian Sommerfeldt
 * @since 2.0
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ESMutateUtil implements ESFuzzyUtil {

	/**
	 * The {@link org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyDataProvider ESFuzzyDataProvider} that generates data.
	 */
	private final ESFuzzyEMFDataProvider dataProvider;

	/**
	 * For internal use.
	 * 
	 * @param dataProvider
	 *            The {@link ESEMFDataProvider} of the test.
	 */
	public ESMutateUtil(ESFuzzyEMFDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	/**
	 * @return The {@link EPackage} of the {@link ESEMFDataProvider}.
	 */
	public Collection<EPackage> getEPackages() {
		return dataProvider.getEPackages();
	}

	/**
	 * @return The minimum objects count of the current {@link ESModelMutatorConfiguration} of the
	 *         {@link ESEMFDataProvider} .
	 */
	public int getMinObjectsCount() {
		return dataProvider.getModelMutatorConfiguration().getMinObjectsCount();
	}

	/**
	 * @return The seed of the {@link ESEMFDataProvider}.
	 */
	public long getSeed() {
		return dataProvider.getSeed();
	}

	/**
	 * @return The current seed (run) of the {@link ESEMFDataProvider}.
	 */
	public int getSeedCount() {
		return dataProvider.getCurrentSeedCount();
	}

	/**
	 * @return The EClasses to ignore in the current {@link ESModelMutatorConfiguration}.
	 */
	public Collection<EClass> getEClassesToIgnore() {
		return dataProvider.getModelMutatorConfiguration()
			.geteClassesToIgnore();
	}

	/**
	 * @return The {@link EStructuralFeature}s to ignore in the current {@link ESModelMutatorConfiguration}.
	 */
	public Collection<EStructuralFeature> getEStructuralFeaturesToIgnore() {
		return dataProvider.getModelMutatorConfiguration()
			.geteStructuralFeaturesToIgnore();
	}

	/**
	 * Mutate with a {@link ESModelMutatorConfiguration}.
	 * 
	 * @param mmc
	 *            The {@link ESModelMutatorConfiguration} to use for mutation.
	 */
	public void mutate(final ESModelMutatorConfiguration mmc) {
		ESDefaultModelMutator.changeModel(mmc,
			new LinkedHashSet<EStructuralFeature>(getEStructuralFeaturesToIgnore()));
	}

	/**
	 * Mutate with the {@link ESModelMutatorConfiguration} from configuration file.
	 * 
	 */
	public void mutate() {
		ESDefaultModelMutator.changeModel(getDataProvider().getModelMutatorConfiguration());
	}

	/**
	 * @see #saveEObject(EObject, String, boolean)
	 * 
	 * @param obj
	 *            The {@link EObject} to save.
	 */
	public void saveEObject(EObject obj) {
		saveEObject(obj, null, true);
	}

	/**
	 * Save an {@link EObject} in the folder: artifacts/runs/configID.
	 * 
	 * Use it for instance to save objects, when an error occurs.
	 * 
	 * The file name is always: COUNT_SUFFIX.xml so e.g. 3_testFile.xml
	 * 
	 * @param obj
	 *            The EObject to save.
	 * @param suffix
	 *            The suffix of the file: e.g. testFile. <code>null</code> permitted.
	 * @param discardDanglingHREF
	 *            Should the save ignore dangling hrefs?
	 */
	public void saveEObject(EObject obj, String suffix,
		boolean discardDanglingHREF) {
		final Resource resource = FuzzyUtil
			.createResource(getRunResourcePath(suffix));
		resource.getContents().add(obj);

		try {
			final Map<Object, Object> options = new HashMap<Object, Object>();
			if (discardDanglingHREF) {
				options.put(XMLResource.OPTION_PROCESS_DANGLING_HREF,
					XMLResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
			}
			resource.save(options);
		} catch (final IOException e) {
			throw new RuntimeException(Messages.MutateUtil_SaveFailed + obj, e);
		}
	}

	/**
	 * @param suffix
	 *            The suffix for the file: e.g. testFile. <code>null</code> permitted.
	 * @return A file path to the current run folder.
	 */
	public String getRunResourcePath(String suffix) {
		final String toAdd = suffix == null || StringUtils.EMPTY.equals(suffix) ? StringUtils.EMPTY : "_" //$NON-NLS-1$
			+ suffix;
		return FuzzyUtil.ROOT_FOLDER + FuzzyUtil.RUN_FOLDER
			+ dataProvider.getConfig().getId() + "/" //$NON-NLS-1$
			+ dataProvider.getCurrentSeedCount() + toAdd
			+ FuzzyUtil.FILE_SUFFIX;
	}

	/**
	 * 
	 * @param suffix
	 *            The suffix for the file: e.g. testFile. <code>null</code> permitted.
	 * @return A file {@link URI} to the current run folder.
	 */
	public URI getRunResourceURI(String suffix) {
		return URI.createFileURI(getRunResourcePath(suffix));
	}

	/**
	 * Returns the {@link ESFuzzyEMFDataProvider} that is being
	 * used to generate data.
	 * 
	 * @return the dataProvider
	 */
	protected ESFuzzyEMFDataProvider getDataProvider() {
		return dataProvider;
	}
}
