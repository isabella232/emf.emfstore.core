/*******************************************************************************
 * Copyright (c) 2012-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Julian Sommerfeldt - initial API and imlementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.internal.junit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Data;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESDefaultModelMutator;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyDataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyRunner;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyTest;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyUtil;
import org.eclipse.emf.emfstore.modelmutator.ESAbstractModelMutator;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * A {@link org.junit.runner.Runner} for each {@link org.junit.runners.model.TestClass TestClass}.
 * Used in the {@link ESFuzzyRunner}.
 * 
 * @author Julian Sommerfeldt
 * 
 */
public class FuzzyTestClassRunner extends BlockJUnit4ClassRunner {

	/**
	 * Which run is it?
	 */
	private final int counter;

	/**
	 * The {@link ESFuzzyDataProvider}, which "contains" the data for the test.
	 */
	private final ESFuzzyDataProvider<?> dataProvider;

	/**
	 * The {@link FrameworkField} of the {@link TestClass} where to put in the
	 * data.
	 */
	private final FrameworkField dataField;

	/**
	 * The {@link FrameworkField} for the {@link Util}.
	 */
	private final FrameworkField utilField;

	private final ESFuzzyUtil util;

	private final FrameworkField optionsField;

	private final FrameworkField mutatorField;

	/**
	 * Constructor.
	 * 
	 * @param type
	 *            The testclass
	 * @param dataProvider
	 *            The {@link ESFuzzyDataProvider} providing the data to put into
	 *            the dataField
	 * @param frameworkFields
	 *            the {@link FrameworkFields} instance holding different configuration options
	 * @param util
	 *            The {@link ESFuzzyUtil} class
	 * @param counter
	 *            The counter of the run
	 * @throws InitializationError
	 *             If there was a problem during the initialization of the test
	 */
	public FuzzyTestClassRunner(Class<?> type, ESFuzzyDataProvider<?> dataProvider,
		FrameworkFields frameworkFields, ESFuzzyUtil util, int counter)
		throws InitializationError {
		super(type);
		mutatorField = frameworkFields.getMutatorField();
		dataField = frameworkFields.getDataField();
		utilField = frameworkFields.getUtilField();
		optionsField = frameworkFields.getOptionsField();
		this.counter = counter;
		this.util = util;
		this.dataProvider = dataProvider;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object createTest() {
		try {
			// create a new instance of the testclass
			final Object testInstance = getTestClass().getOnlyConstructor()
				.newInstance();

			// set the options to dataprovider
			if (optionsField != null) {
				final Object options = getValueFromField(optionsField.getField(),
					testInstance);
				if (options == null) {
					throw new IllegalStateException(
						Messages.getString("FuzzyTestClassRunner.OptionsFieldIsNull")); //$NON-NLS-1$
				}
				try {
					dataProvider.setOptions((Map<String, Object>) options);
				} catch (final ClassCastException e) {
					throw new ClassCastException(
						Messages.getString("FuzzyTestClassRunner.OptionsFieldWrongType")); //$NON-NLS-1$
				}
			}

			ESAbstractModelMutator modelMutator;
			final ESModelMutatorConfiguration config = dataProvider.getModelMutatorConfiguration();

			if (mutatorField != null) {
				modelMutator = (ESAbstractModelMutator) mutatorField.getType().newInstance();
				modelMutator.setConfig(config);
				setValueToField(mutatorField.getField(),
					testInstance,
					modelMutator,
					Messages.getString("FuzzyTestClassRunner.MutatorFieldSetFailed")); //$NON-NLS-1$
			} else {
				modelMutator = new ESDefaultModelMutator(config);
			}

			dataProvider.setMutator(modelMutator);

			// get the new data from dataprovider
			final Object data = dataProvider.get(counter);

			// set the data to the datafield
			setValueToField(dataField.getField(), testInstance, data,
				MessageFormat.format(
					Messages.getString("FuzzyTestClassRunner.DataProviderTypeError"), //$NON-NLS-1$
					Data.class.getSimpleName(),
					dataProvider.getClass()));

			// set the util to the util field
			if (util != null && utilField != null) {
				setValueToField(utilField.getField(), testInstance, util,
					MessageFormat.format(
						Messages.getString("FuzzyTestClassRunner.UtilTypeError"), //$NON-NLS-1$
						ESFuzzyUtil.class.getSimpleName()));
			}

			return testInstance;
		} catch (final InstantiationException e) {
			throw new RuntimeException(e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private Object getValueFromField(Field field, Object instance)
		throws IllegalAccessException {
		try {
			field.setAccessible(true);
			final Object o = field.get(instance);
			return o;
		} finally {
			field.setAccessible(false);
		}
	}

	/**
	 * 
	 * @param field
	 *            field to be set
	 * @param instance
	 *            instance holding the field
	 * @param value
	 *            the value to be set
	 * @param errorMsg
	 * @throws IllegalAccessException
	 */
	private void setValueToField(Field field, Object instance, Object value,
		String errorMsg) throws IllegalAccessException {
		try {
			field.setAccessible(true);
			field.set(instance, value);
		} finally {
			field.setAccessible(false);
		}
	}

	@Override
	public List<FrameworkMethod> getChildren() {
		final List<ESFuzzyTest> testsToRun = dataProvider.getTestsToRun();
		final List<FrameworkMethod> allChildren = super.getChildren();

		// check if it should filter tests
		if (testsToRun != null) {
			final List<FrameworkMethod> filteredChildren = new ArrayList<FrameworkMethod>();
			for (final ESFuzzyTest test : testsToRun) {
				final String name = test.getName();
				final int seedCount = test.getSeedCount();
				for (final FrameworkMethod child : allChildren) {
					if (seedCount == counter && name.equals(child.getName())) {
						filteredChildren.add(child);
					}
				}
			}
			return filteredChildren;
		}

		// if not return all children
		return allChildren;
	}

	private String testName(String name) {
		return String.format("%s%s[%s]", name, ESFuzzyRunner.NAME_SEPARATOR, //$NON-NLS-1$
			counter);
	}

	@Override
	protected String testName(final FrameworkMethod method) {
		return testName(method.getName());
	}

	@Override
	protected String getName() {
		return String.format("%s%s[%s]", getTestClass().getName(), //$NON-NLS-1$
			ESFuzzyRunner.NAME_SEPARATOR, counter);
	}

	@Override
	protected Statement classBlock(RunNotifier notifier) {
		return childrenInvoker(notifier);
	}
}
