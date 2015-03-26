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
package org.eclipse.emf.emfstore.internal.fuzzy.emf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.emfstore.fuzzy.emf.ESTestConfig;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.api.ESTestConfigImpl;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestConfig;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestDiff;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestResult;
import org.junit.runners.model.TestClass;

/**
 * Utility class for different methods used in {@link org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider
 * EMFDataProvider} context.
 *
 * @author Julian Sommerfeldt
 *
 */
public final class FuzzyUtil {

	// TODO make folders etc configurable
	/**
	 * The main folder containing all files.
	 */
	public static final String FUZZY_FOLDER = "fuzzy/"; //$NON-NLS-1$

	/**
	 * The folder where to put the artifacts.
	 */
	public static final String ROOT_FOLDER = "../" + FUZZY_FOLDER; //$NON-NLS-1$

	/**
	 * The folder where to store the test runs.
	 */
	public static final String RUN_FOLDER = "testruns/"; //$NON-NLS-1$

	/**
	 * The file suffix for the files.
	 */
	public static final String FILE_SUFFIX = ".xml"; //$NON-NLS-1$

	/**
	 * The file containing the {@link TestConfig}s.
	 */
	public static final String TEST_CONFIG_FILE = "fuzzyConfig.fuzzy"; //$NON-NLS-1$

	/**
	 * The path to the TEST_CONFIG_FILE.
	 */
	public static final String TEST_CONFIG_PATH = FUZZY_FOLDER
		+ TEST_CONFIG_FILE;

	/**
	 * The path to the file containing the {@link TestDiff}.
	 */
	public static final String DIFF_FILE = ROOT_FOLDER + "diff" + FILE_SUFFIX; //$NON-NLS-1$

	/**
	 * The path to the properties file.
	 */
	public static final String PROPERTIES_FILE = FUZZY_FOLDER
		+ "fuzzy.properties"; //$NON-NLS-1$

	/**
	 * The prefix for all fuzzy properties in the properties file.
	 */
	public static final String PROP_PRE = "fuzzy"; //$NON-NLS-1$

	private static final AdapterFactoryEditingDomain EDITING_DOMAIN = new AdapterFactoryEditingDomain(
		new ComposedAdapterFactory(
			ComposedAdapterFactory.Descriptor.Registry.INSTANCE),
		new BasicCommandStack());

	private static Properties properties;

	private FuzzyUtil() {
	}

	/**
	 * Searches in the resource for a {@link TestConfig} fitting to the given {@link TestClass}.
	 *
	 * @param resource
	 *            The resource where to search in.
	 * @param testClass
	 *            The TestClass to which the {@link TestConfig} should fit.
	 * @return The {@link TestConfig} fitting to the {@link TestClass}.
	 */
	public static ESTestConfig getTestConfig(Resource resource, TestClass testClass) {
		// TODO add a standard TestConfig? e.g. where clazz = null / or
		// testconfig for complete packages
		for (final EObject object : resource.getContents()) {
			if (object instanceof TestConfig) {
				final TestConfig config = (TestConfig) object;
				final Class<?> clazz = config.getTestClass();
				if (clazz.getName().equals(testClass.getJavaClass().getName())) {
					return new ESTestConfigImpl(config);
				}
			}
		}

		throw new IllegalArgumentException(
			MessageFormat.format(
				Messages.FuzzyUtil_NoTestConfigFound, testClass.getName(), resource.getURI()));
	}

	/**
	 * Checks if a resource contains a {@link TestConfig}.
	 *
	 * @param resource
	 *            The resource where to search in.
	 * @param config
	 *            The {@link TestConfig} to check.
	 * @return <code>true</code> if the resource contains the {@link TestConfig} , else <code>false</code>.
	 */
	public static boolean containsConfig(Resource resource, TestConfig config) {
		for (final EObject obj : resource.getContents()) {
			if (obj instanceof TestConfig) {
				final TestConfig c = (TestConfig) obj;
				if (c.getId().equals(config.getId())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the resource exists.
	 *
	 * @param resource
	 *            The {@link Resource} to check.
	 * @return <code>true</code> if the resource exists, <code>false</code> otherwise.
	 */
	public static boolean resourceExists(Resource resource) {
		return resource.getResourceSet().getURIConverter()
			.exists(resource.getURI(), null);
	}

	/**
	 * Create a new {@link Resource}.
	 *
	 * @param fileNameURI
	 *            The uri of the resource.
	 * @return The newly created {@link Resource}.
	 */
	public static Resource createResource(String fileNameURI) {
		return EDITING_DOMAIN.createResource(fileNameURI);
	}

	/**
	 * Get a valid {@link TestResult} out of a {@link TestDiff}. Valid means non
	 * null.
	 *
	 * @param diff
	 *            The {@link TestDiff} containing the {@link TestResult}.
	 * @return The valid {@link TestResult} of the {@link TestDiff}.
	 */
	public static TestResult getValidTestResult(TestDiff diff) {
		TestResult result = diff.getOldResult();
		if (result != null) {
			return result;
		}
		result = diff.getNewResult();
		if (result != null) {
			return result;
		}
		throw new RuntimeException(
			Messages.FuzzyUtil_WrontTestDiffConfiguration);
	}

	/**
	 * Get a property out of the properties file.
	 *
	 * @param key
	 *            The key of the property.
	 * @param defaultValue
	 *            The value if the properties do not contain the key.
	 * @return The value if the properties contain the key or the defaultValue
	 *         if not.
	 */
	public static String getProperty(String key, String defaultValue) {
		initProperties();
		return properties.getProperty(PROP_PRE + key, defaultValue);
	}

	private static void initProperties() {
		if (properties != null) {
			return;
		}

		final File file = new File(PROPERTIES_FILE);
		properties = new Properties();

		if (file.exists()) {
			try {
				final FileInputStream fs = new FileInputStream(file);
				properties.load(fs);
				fs.close();
			} catch (final IOException e) {
				throw new RuntimeException(
					MessageFormat.format(Messages.FuzzyUtil_LoadPropertiesFailed,
						file.getAbsolutePath()), e);
			}
		}
	}
}
