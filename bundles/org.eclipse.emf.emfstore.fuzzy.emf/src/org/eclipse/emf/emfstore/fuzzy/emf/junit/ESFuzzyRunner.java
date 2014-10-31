/*******************************************************************************
 * Copyright (c) 2012-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Julian Sommerfeldt - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.emfstore.fuzzy.emf.internal.junit.FrameworkFields;
import org.eclipse.emf.emfstore.fuzzy.emf.internal.junit.FuzzyTestClassRunner;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Data;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.DataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Mutator;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Options;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Util;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.InitializationError;

/**
 * A {@link Runner} for JUnit, to realize multiple runs with different values
 * for a data field. <br/>
 * <br/>
 * Activate with the {@link org.junit.runner.RunWith} annotation: <code>@RunWith(ESFuzzyRunner.class)</code>. <br/>
 * <br/>
 * The test class must have a field, which is not static and annotated with {@link Data}, e.g.<br/>
 * <br/>
 * <code>@Data<br/>private Integer i;</code> <br/>
 * <br/>
 * To provide data an implementation of {@link ESFuzzyDataProvider} can be set via
 * the {@link DataProvider} annotation, e.g.<br/>
 * <br/>
 * <code>@DataProvider(IntDataProvider.class)</code><br/>
 * <br/>
 * This class must implement the interface {@link ESFuzzyDataProvider}. The
 * default value is the example implementation: IntDataProvider.<br/>
 * <br/>
 * The MyTest class illustrates an example usage of the {@link ESFuzzyRunner}.
 * 
 * @author Julian Sommerfeldt
 * @since 2.0
 * 
 */
public class ESFuzzyRunner extends Suite {

	private final ArrayList<Runner> runners = new ArrayList<Runner>();

	private final ESFuzzyDataProvider<?> dataProvider;

	/**
	 * The string representing a separation in a name (e.g. test name).
	 */
	public static final String NAME_SEPARATOR = " "; //$NON-NLS-1$

	/**
	 * Default constructor, called by JUnit.
	 * 
	 * @param clazz
	 *            The root class of the suite.
	 * @throws InitializationError
	 *             If there
	 */
	public ESFuzzyRunner(Class<?> clazz) throws InitializationError {
		super(clazz, Collections.<Runner> emptyList());
		dataProvider = getDataProvider();
		dataProvider.setTestClass(getTestClass());
		dataProvider.init();
		final ESFuzzyUtil util = dataProvider.getUtil();

		final FrameworkFields fields = FrameworkFields.create()
			.setDataField(getDataField())
			.setUtilField(getUtilField())
			.setMutatorField(getMutatorField())
			.setOptionsField(getOptionsField());

		for (int i = 0; i < dataProvider.size(); i++) {
			final FuzzyTestClassRunner runner = new FuzzyTestClassRunner(clazz,
				dataProvider, fields, util,
				i + 1);
			if (runner.getChildren().size() > 0) {
				runners.add(runner);
			}
		}
	}

	/*
	 * Override to add RunListeners of the ESFuzzyDataProvider (non-Javadoc)
	 * @see
	 * org.junit.runners.ParentRunner#run(org.junit.runner.notification.RunNotifier
	 * )
	 */
	@Override
	public void run(final RunNotifier notifier) {
		final List<RunListener> listener = dataProvider.getListener();
		if (listener != null) {
			for (final RunListener runListener : listener) {
				notifier.addListener(runListener);
			}
		}
		super.run(notifier);
	}

	/**
	 * @return The field annotated with {@link ESFuzzyUtil}.
	 * @throws Exception
	 *             If there is are more than one fitting fields.
	 */
	private FrameworkField getUtilField() {
		return getSingleStaticFrameworkField(Util.class);
	}

	private FrameworkField getMutatorField() {
		return getSingleStaticFrameworkField(Mutator.class);
	}

	private FrameworkField getOptionsField() {
		return getSingleStaticFrameworkField(Options.class);
	}

	private FrameworkField getSingleStaticFrameworkField(
		Class<? extends Annotation> annotation) {
		final List<FrameworkField> fields = getTestClass().getAnnotatedFields(
			annotation);

		// Check if there are more than one Data field in the class
		if (fields.size() > 1) {
			throw new RuntimeException(
				MessageFormat.format(Messages.getString("ESFuzzyRunner.OneAnnotationOnly"), //$NON-NLS-1$
					annotation.getSimpleName(),
					getTestClass().getName(),
					fields.size()));
		}

		// get the field and check modifiers
		for (final FrameworkField field : fields) {
			final int modifiers = field.getField().getModifiers();
			if (!Modifier.isStatic(modifiers)) {
				return field;
			}
		}

		return null;
	}

	/**
	 * @return The field annotated with {@link Data}.
	 * @throws InitializationError
	 * @throws Exception
	 *             If there is not exact one fitting field.
	 */
	private FrameworkField getDataField() throws InitializationError {
		final FrameworkField field = getSingleStaticFrameworkField(Data.class);

		if (field == null) {
			throw new InitializationError(
				MessageFormat.format(
					Messages.getString("ESFuzzyRunner.NonStaticFieldMissing"), //$NON-NLS-1$
					Data.class.getSimpleName(),
					getTestClass().getName()));
		}

		return field;
	}

	/**
	 * @return The {@link ESFuzzyDataProvider} defined by the {@link DataProvider} annotation or the default one.
	 * @throws InitializationError
	 * @throws Exception
	 *             If the data provider does not implement the {@link ESFuzzyDataProvider} interface.
	 */
	private ESFuzzyDataProvider<?> getDataProvider() throws InitializationError {
		// Get the DataProvider Annotation
		final Annotation[] annotations = getTestClass().getAnnotations();

		// take default DataProvider, if there is no annotation
		Class<?> dataProviderClass = null;

		// check for the dataprovider annotation
		for (final Annotation annotation : annotations) {
			if (annotation instanceof DataProvider) {

				// Check if the given class is an implementation of
				// ESFuzzyDataProvider
				dataProviderClass = ((DataProvider) annotation).value();
				if (!ESFuzzyDataProvider.class
					.isAssignableFrom(dataProviderClass)) {
					throw new InitializationError(
						MessageFormat.format(
							Messages.getString("ESFuzzyRunner.NotAnInstanceOf"), //$NON-NLS-1$
							dataProviderClass,
							ESFuzzyDataProvider.class.getSimpleName()));
				}
			}
		}

		// create a new instance of the DataProvider
		try {
			return (ESFuzzyDataProvider<?>) dataProviderClass.getConstructor()
				.newInstance();
		} catch (final InstantiationException e) {
			throw new InitializationError(
				Messages.getString("ESFuzzyRunner.DataProviderCTorMissing")); //$NON-NLS-1$
		} catch (final IllegalAccessException e) {
			throw new InitializationError(
				Messages.getString("ESFuzzyRunner.DataProviderCTorMissing")); //$NON-NLS-1$
		} catch (final IllegalArgumentException e) {
			throw new InitializationError(
				Messages.getString("ESFuzzyRunner.DataProviderCTorMissing")); //$NON-NLS-1$
		} catch (final InvocationTargetException e) {
			throw new InitializationError(
				Messages.getString("ESFuzzyRunner.DataProviderCTorMissing")); //$NON-NLS-1$
		} catch (final NoSuchMethodException e) {
			throw new InitializationError(
				Messages.getString("ESFuzzyRunner.DataProviderCTorMissing")); //$NON-NLS-1$
		} catch (final SecurityException e) {
			throw new InitializationError(
				Messages.getString("ESFuzzyRunner.DataProviderCTorMissing")); //$NON-NLS-1$
		}
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}
}
