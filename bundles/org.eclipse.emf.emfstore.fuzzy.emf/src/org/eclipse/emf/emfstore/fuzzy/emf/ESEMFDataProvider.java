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
 * Philip Langer - configuring of numbers of changes during mutation
 * Edgar Mueller - API layer
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.emfstore.fuzzy.FuzzyDataProvider;
import org.eclipse.emf.emfstore.fuzzy.Test;
import org.eclipse.emf.emfstore.fuzzy.Util;
import org.eclipse.emf.emfstore.fuzzy.emf.internal.diff.HudsonTestRunProvider;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.EMFRunListener;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.FuzzyUtil;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.Messages;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.api.ESTestConfigImpl;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.ConfigFactory;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.ConfigPackage;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.DiffReport;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestConfig;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestDiff;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestResult;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestRun;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;
import org.eclipse.emf.emfstore.modelmutator.ESDefaultModelMutator;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.TestClass;

/**
 * This implementation of a {@link FuzzyDataProvider} provides generated models
 * using the functionality of {@link ESDefaultModelMutator}. <br>
 * <br>
 * The run of a test is based on a {@link TestConfig}, defining model etc. <br>
 * <br>
 * During the run it records {@link TestResult}s to create a test run for
 * reporting purpose.
 * 
 * @author Julian Sommerfeldt
 * @since 2.0
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
// TODO: review javadoc for internal types
public class ESEMFDataProvider implements FuzzyDataProvider<EObject> {

	private Random random;

	private int count;

	private int seedCount;

	private TestClass testClass;

	private TestRun testRun;

	private ESTestConfig config;

	private boolean filterTests;

	private String configFile;

	private long nextSeed;

	private EClass rootEClass;

	private ESModelMutatorConfiguration modelMutatorConfig;

	private Resource diffResource;

	/**
	 * Prefix of the properties concerning the {@link ESEMFDataProvider}.
	 */
	public static final String PROP_EMFDATAPROVIDER = ".emfdataprovider"; //$NON-NLS-1$

	/**
	 * Property specifying the path to the config file for the {@link ESEMFDataProvider}.
	 */
	public static final String PROP_CONFIGS_FILE = ".configsFile"; //$NON-NLS-1$

	/**
	 * Options constant for the exception log set for the mutator. Has to be
	 * filled with a <code>Set</code> of <code>RuntimeException</code>.
	 */
	public static final String MUTATOR_EXC_LOG = "mutatorExcLog"; //$NON-NLS-1$

	/**
	 * Options constant for the {@link EditingDomain} for the {@link ESDefaultModelMutator}.
	 */
	public static final String MUTATOR_EDITINGDOMAIN = "mutatorEditingDomain"; //$NON-NLS-1$

	/**
	 * Init the {@link ESEMFDataProvider}.
	 */
	public void init() {
		// fill properties like the config file
		fillProperties();

		// load test config from file
		final Resource configResource = FuzzyUtil.createResource(configFile);
		try {
			configResource.load(null);
		} catch (final IOException e) {
			throw new RuntimeException(
				MessageFormat.format(Messages.EMFDataProvider_ConfigFileLoadFailed, configFile), e);
		}

		// get the testconfig fitting to the current testclass
		config = FuzzyUtil.getTestConfig(configResource, testClass);

		// add the config to the configs file
		addToConfigFile();

		// init variables
		random = new Random(config.getSeed());
		count = config.getCount();
		seedCount = 0;

		// read variables out of mutatorConfig and write into modelmutatorConfig
		final ESMutatorConfig mutatorConfig = config.getMutatorConfig();
		rootEClass = mutatorConfig.getRootEClass();
		if (rootEClass == null) {
			rootEClass = ConfigPackage.Literals.ROOT;
		}
		modelMutatorConfig = new ESModelMutatorConfiguration();
		modelMutatorConfig.setMinObjectsCount(mutatorConfig
			.getMinObjectsCount());
		modelMutatorConfig.setDoNotGenerateRoot(mutatorConfig
			.isDoNotGenerateRoot());
		modelMutatorConfig.seteClassesToIgnore(mutatorConfig
			.getEClassesToIgnore());
		modelMutatorConfig.seteStructuralFeaturesToIgnore(mutatorConfig
			.getEStructuralFeaturesToIgnore());
		modelMutatorConfig.setIgnoreAndLog(mutatorConfig.isIgnoreAndLog());
		modelMutatorConfig.setUseEcoreUtilDelete(mutatorConfig
			.isUseEcoreUtilDelete());
		modelMutatorConfig.setMaxDeleteCount(mutatorConfig.getMaxDeleteCount());
		modelMutatorConfig.setModelPackages(mutatorConfig.getEPackages());
		modelMutatorConfig.setMutationCount(mutatorConfig.getMutationCount());
		modelMutatorConfig.setAllowDuplicateIDs(mutatorConfig.isAllowDuplicateIDs());

		testRun = ConfigFactory.eINSTANCE.createTestRun();
		testRun.setTime(new Date());
		testRun.setConfig(ESTestConfigImpl.class.cast(config).toInternalAPI());
	}

	/**
	 * Add the config to the file containing all configs.
	 */
	private void addToConfigFile() {
		final Resource resource = FuzzyUtil.createResource(FuzzyUtil.ROOT_FOLDER
			+ FuzzyUtil.TEST_CONFIG_FILE);
		try {
			if (FuzzyUtil.resourceExists(resource)) {
				resource.load(null);
			}

			final TestConfig internalConfig =
				ESTestConfigImpl.class.cast(config).toInternalAPI();

			if (!FuzzyUtil.containsConfig(resource, internalConfig)) {
				resource.getContents().add(internalConfig);
				resource.save(null);
			}
		} catch (final IOException e) {
			throw new RuntimeException(Messages.EMFDataProvider_ConfigFileSaveFailed, e);
		}
	}

	/**
	 * See {@link FuzzyDataProvider}.
	 * 
	 * @param count
	 *            Which run is it?
	 * @return The new {@link EObject}.
	 */
	public EObject get(int count) {
		seedCount++;

		// adjust the seed
		fitSeed(count);

		// generate the model
		nextSeed = random.nextLong();

		// get the root eclass for the generation
		final EObject root = EcoreUtil.create(rootEClass);

		// generate the model
		modelMutatorConfig.reset();
		modelMutatorConfig.setRootEObject(root);
		modelMutatorConfig.setSeed(nextSeed);
		ESDefaultModelMutator.generateModel(modelMutatorConfig);

		return root;
	}

	private void fitSeed(int count) {
		if (count == seedCount) {
			return;
		} else if (count < seedCount) {
			random = new Random(config.getSeed());
			seedCount = 0;
		}

		while (seedCount < count) {
			random.nextLong();
			seedCount++;
		}
	}

	/**
	 * Call finish as last action of the {@link ESEMFDataProvider}. Used for
	 * saving the results.
	 */
	public void finish() {
		// create run resource
		final Resource runResource = FuzzyUtil
			.createResource(FuzzyUtil.ROOT_FOLDER + FuzzyUtil.RUN_FOLDER
				+ config.getId() + FuzzyUtil.FILE_SUFFIX);
		runResource.getContents().add(testRun);

		try {
			runResource.save(null);
		} catch (final IOException e) {
			throw new RuntimeException(
				Messages.EMFDataProvider_SaveRunResultFailed, e);
		}
	}

	/**
	 * @return How much objects will be created?
	 */
	public int size() {
		return count;
	}

	/**
	 * @param testClass
	 *            The {@link TestClass} of this run.
	 */
	public void setTestClass(TestClass testClass) {
		this.testClass = testClass;
	}

	/**
	 * @return The {@link RunListener} of this {@link ESEMFDataProvider}.
	 */
	public List<RunListener> getListener() {
		return Arrays.asList(new RunListener[] {
			new EMFRunListener(this, testRun)
		});
	}

	/**
	 * @return all {@link Test}s to run or null if all tests should run.
	 */
	public List<Test> getTestsToRun() {
		if (!filterTests) {
			return null;
		}

		// first time load diffResource
		if (diffResource == null) {
			try {
				diffResource = HudsonTestRunProvider.getDiffResource();
				diffResource.load(null);
			} catch (final IOException e) {
				throw new RuntimeException(Messages.EMFDataProvider_DiffFileLoadFailed, e);
			} catch (final DocumentException e) {
				throw new RuntimeException(Messages.EMFDataProvider_DiffFileLoadFailed, e);
			}
		}

		// filter for correct config
		final EList<EObject> contents = diffResource.getContents();
		final List<Test> tests = new ArrayList<Test>();
		for (final EObject obj : contents) {
			if (obj instanceof DiffReport) {
				for (final TestDiff diff : ((DiffReport) obj).getDiffs()) {
					if (diff.getConfig().getId().equals(config.getId())) {
						final TestResult result = FuzzyUtil.getValidTestResult(diff);
						tests.add(new Test(result.getTestName(), result
							.getSeedCount()));
					}
				}
			}
		}

		return tests;
	}

	/**
	 * @return The current seed used to create the model
	 */
	public int getCurrentSeedCount() {
		return seedCount;
	}

	/**
	 * @return The current seed for the {@link ESDefaultModelMutator}.
	 */
	public long getSeed() {
		return nextSeed;
	}

	/**
	 * @return The {@link EPackage} of the model to generate/mutate.
	 */
	public Collection<EPackage> getEPackages() {
		return modelMutatorConfig.getModelPackages();
	}

	private void fillProperties() {
		final String filterTests = System.getProperty("filterTests"); //$NON-NLS-1$
		if (filterTests == null) {
			this.filterTests = false;
		} else {
			this.filterTests = Boolean.parseBoolean(filterTests);
		}
		configFile = FuzzyUtil.getProperty(PROP_EMFDATAPROVIDER
			+ PROP_CONFIGS_FILE, FuzzyUtil.TEST_CONFIG_PATH);
	}

	/**
	 * @return The a new {@link ESMutateUtil} for this {@link ESEMFDataProvider}.
	 */
	public Util getUtil() {
		return new ESMutateUtil(this);
	}

	/**
	 * @return The config specifying this run of the {@link ESEMFDataProvider}.
	 * @since 2.0
	 */
	public ESTestConfig getConfig() {
		return config;
	}

	/**
	 * Set the options for the {@link ESEMFDataProvider}.
	 * 
	 * @param options
	 *            the options.
	 */
	@SuppressWarnings("unchecked")
	public void setOptions(Map<String, Object> options) {
		// exc log
		Object o = options.get(MUTATOR_EXC_LOG);
		if (o != null && o instanceof Set<?>) {
			modelMutatorConfig.setExceptionLog((Set<RuntimeException>) o);
		}

		// model mutator editing domain
		o = options.get(MUTATOR_EDITINGDOMAIN);
		if (o != null && o instanceof EditingDomain) {
			modelMutatorConfig.setEditingDomain((EditingDomain) o);
		}
	}

	/**
	 * @return The currently active {@link ESModelMutatorConfiguration}.
	 * @since 2.0
	 */
	public ESModelMutatorConfiguration getModelMutatorConfiguration() {
		return modelMutatorConfig;
	}
}
