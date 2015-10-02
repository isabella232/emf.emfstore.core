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
package org.eclipse.emf.emfstore.internal.client.ui.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTest;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.ui.ESClassFilter;
import org.eclipse.emf.emfstore.client.ui.ESWhitelistFilter;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.CompositeOperationHandle;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.InvalidHandleException;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.eclipse.emf.emfstore.test.model.TestType;
import org.eclipse.emf.emfstore.test.model.TestmodelPackage;
import org.junit.Before;
import org.junit.Test;

public class EClassFilterTest extends ESTest {

	private static final String DESC = "desc"; //$NON-NLS-1$
	private static final String NAME = "name"; //$NON-NLS-1$
	protected static final String LABEL = "foo"; //$NON-NLS-1$

	private EClassFilter eClassFilter;

	@Override
	@Before
	public void before() {
		super.before();
		eClassFilter = new EClassFilter();
	}

	@Test
	public void testCreateDeleteOperationNoFilter() {
		final TestElement element = Create.testElement();
		Add.toProject(getLocalProject(), element);
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertFalse(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testCreateDeleteOperationEClassFiltered() {
		final ESClassFilter filter = createTestElementESClassFilter();
		eClassFilter.registerFilter(filter);
		final TestElement element = Create.testElement();
		Add.toProject(getLocalProject(), element);
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertTrue(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testCreateDeleteOperationEClassNotFiltered() {
		final ESClassFilter filter = createTestElementESClassFilter();
		eClassFilter.registerFilter(filter);
		final TestType element = Create.testType();
		Add.toProject(getLocalProject(), element);
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertFalse(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testCompositeOperationNoFilter() throws InvalidHandleException {
		final CompositeOperationHandle operationHandle = getProjectSpace().beginCompositeOperation();
		final TestElement testElement = Create.testElement();
		Add.toProject(getLocalProject(), testElement);
		Add.toProject(getLocalProject(), Create.testElement());
		operationHandle.end(NAME, DESC, getProject().getModelElementId(testElement));
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertFalse(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testCompositeOperationNotFiltered1() throws InvalidHandleException {
		final ESClassFilter filter = createTestElementESClassFilter();
		eClassFilter.registerFilter(filter);
		final CompositeOperationHandle operationHandle = getProjectSpace().beginCompositeOperation();
		final TestElement testElement = Create.testElement();
		Add.toProject(getLocalProject(), testElement);
		Add.toProject(getLocalProject(), Create.testType());
		operationHandle.end(NAME, DESC, getProject().getModelElementId(testElement));
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertFalse(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testCompositeOperationNotFiltered2() throws InvalidHandleException {
		final ESClassFilter filter = createTestElementESClassFilter();
		eClassFilter.registerFilter(filter);
		final CompositeOperationHandle operationHandle = getProjectSpace().beginCompositeOperation();
		final TestType testElement = Create.testType();
		Add.toProject(getLocalProject(), testElement);
		Add.toProject(getLocalProject(), Create.testElement());
		operationHandle.end(NAME, DESC, getProject().getModelElementId(testElement));
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertFalse(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testCompositeOperationNotFiltered3() throws InvalidHandleException {
		final ESClassFilter filter = createTestElementESClassFilter();
		eClassFilter.registerFilter(filter);
		final CompositeOperationHandle operationHandle = getProjectSpace().beginCompositeOperation();
		final TestType testElement = Create.testType();
		Add.toProject(getLocalProject(), testElement);
		Add.toProject(getLocalProject(), Create.testType());
		operationHandle.end(NAME, DESC, getProject().getModelElementId(testElement));
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertFalse(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testCompositeOperationFiltered() throws InvalidHandleException {
		final ESClassFilter filter = createTestElementESClassFilter();
		eClassFilter.registerFilter(filter);
		final CompositeOperationHandle operationHandle = getProjectSpace().beginCompositeOperation();
		final TestElement testElement = Create.testElement();
		Add.toProject(getLocalProject(), testElement);
		Add.toProject(getLocalProject(), Create.testElement());
		operationHandle.end(NAME, DESC, getProject().getModelElementId(testElement));
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertTrue(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testFeatureOperationNoFilter() {
		final TestElement testElement = Create.testElement();
		Add.toProject(getLocalProject(), testElement);
		clearOperations();
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				testElement.setName(NAME);
				return null;
			}
		});
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertFalse(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testFeatureOperationNotFiltered() {
		final ESClassFilter filter = createTestElementESClassFilter();
		eClassFilter.registerFilter(filter);
		final TestType testType = Create.testType();
		Add.toProject(getLocalProject(), testType);
		clearOperations();
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				testType.setName(NAME);
				return null;
			}
		});
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertFalse(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testFeatureOperationFiltered() {
		final ESClassFilter filter = createTestElementESClassFilter();
		eClassFilter.registerFilter(filter);
		final TestElement testElement = Create.testElement();
		Add.toProject(getLocalProject(), testElement);
		clearOperations();
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				testElement.setName(NAME);
				return null;
			}
		});
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertTrue(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testWhitelistFeatureOperationNotFiltered() {
		final ESClassFilter filter = createTestElementNameWhitelistFilter();
		eClassFilter.registerFilter(filter);
		final TestElement testElement = Create.testElement();
		Add.toProject(getLocalProject(), testElement);
		clearOperations();
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				testElement.setName(NAME);
				return null;
			}
		});
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertFalse(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testWhitelistFeatureOperationFiltered() {
		final ESClassFilter filter = createTestElementNameWhitelistFilter();
		eClassFilter.registerFilter(filter);
		final TestElement testElement = Create.testElement();
		Add.toProject(getLocalProject(), testElement);
		clearOperations();
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				testElement.setDescription(DESC);
				return null;
			}
		});
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertTrue(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testWhitelistCompositeOperationNotFiltered() throws InvalidHandleException {
		final ESClassFilter filter = createTestElementNameWhitelistFilter();
		eClassFilter.registerFilter(filter);
		final TestElement testElement = Create.testElement();
		Add.toProject(getLocalProject(), testElement);
		clearOperations();
		final CompositeOperationHandle operationHandle = getProjectSpace().beginCompositeOperation();
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				testElement.setDescription(DESC);
				testElement.setName(NAME);
				return null;
			}
		});
		operationHandle.end(NAME, DESC, getProject().getModelElementId(testElement));
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertFalse(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	@Test
	public void testWhitelistCompositeOperationFiltered() throws InvalidHandleException {
		final ESClassFilter filter = createTestElementNameWhitelistFilter();
		eClassFilter.registerFilter(filter);
		final TestElement testElement = Create.testElement();
		Add.toProject(getLocalProject(), testElement);
		clearOperations();
		final CompositeOperationHandle operationHandle = getProjectSpace().beginCompositeOperation();
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				testElement.setDescription(DESC);
				testElement.setDescription(NAME);
				return null;
			}
		});
		operationHandle.end(NAME, DESC, getProject().getModelElementId(testElement));
		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(1, operations.size());
		assertTrue(eClassFilter.involvesOnlyFilteredEClasses(getProject(), operations.get(0)));
	}

	private static ESClassFilter createTestElementNameWhitelistFilter() {
		final ESClassFilter filter = new ESWhitelistFilter() {

			public String getLabel() {
				return LABEL;
			}

			public Set<EClass> getFilteredEClasses() {
				return Collections.singleton(TestmodelPackage.eINSTANCE.getTestElement());
			}

			public Map<EClass, Collection<EStructuralFeature>> getNonFilteredFeaturesForEClass() {
				final Map<EClass, Collection<EStructuralFeature>> whitelist = new LinkedHashMap<EClass, Collection<EStructuralFeature>>();
				whitelist.put(TestmodelPackage.eINSTANCE.getTestElement(),
					Collections.<EStructuralFeature> singleton(TestmodelPackage.eINSTANCE.getTestElement_Name()));
				return whitelist;
			}
		};
		return filter;
	}

	private static ESClassFilter createTestElementESClassFilter() {
		final ESClassFilter esClassFilter = new ESClassFilter() {

			public String getLabel() {
				return LABEL;
			}

			public Set<EClass> getFilteredEClasses() {
				return Collections.singleton(TestmodelPackage.eINSTANCE.getTestElement());
			}
		};
		return esClassFilter;
	}

}
