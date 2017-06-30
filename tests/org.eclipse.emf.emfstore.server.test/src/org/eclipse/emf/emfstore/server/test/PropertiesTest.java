/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.emf.emfstore.internal.client.model.ModelPackage;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.EMFStorePropertiesOutdatedException;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.client.properties.PropertyManager;
import org.eclipse.emf.emfstore.internal.common.model.PropertyStringValue;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.test.model.TestmodelFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PropertiesTest extends TransmissionTests {

	private static final String SECOND_TEST_PROP = "SecondTest"; //$NON-NLS-1$
	private static final String FIRST_PROP_KEY = "FirstPropKey"; //$NON-NLS-1$
	private static PropertyManager propertyManager1;
	private static PropertyManager propertyManager2;

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Test
	public void testSharedProperties() throws ESException {

		propertyManager1 = getProjectSpace1().getPropertyManager();
		propertyManager2 = getProjectSpace2().getPropertyManager();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				propertyManager1.setSharedStringProperty(FIRST_PROP_KEY, "test1"); //$NON-NLS-1$
				propertyManager2.setSharedStringProperty(SECOND_TEST_PROP, "test2"); //$NON-NLS-1$

				try {
					propertyManager1.synchronizeSharedProperties();
					propertyManager2.synchronizeSharedProperties();
					propertyManager1.synchronizeSharedProperties();
				} catch (final ESException e) {
					throw new RuntimeException(e);
				} catch (final EMFStorePropertiesOutdatedException e) {
					throw new RuntimeException(e);
				}
			}
		}.run(false);

		// 1. Test, ob transmit funktioniert
		Assert.assertEquals("test1", propertyManager1.getSharedStringProperty(FIRST_PROP_KEY)); //$NON-NLS-1$
		Assert.assertEquals("test1", propertyManager2.getSharedStringProperty(FIRST_PROP_KEY)); //$NON-NLS-1$

		Assert.assertEquals("test2", propertyManager1.getSharedStringProperty(SECOND_TEST_PROP)); //$NON-NLS-1$
		Assert.assertEquals("test2", propertyManager2.getSharedStringProperty(SECOND_TEST_PROP)); //$NON-NLS-1$

		Assert.assertEquals(propertyManager1.getSharedStringProperty(FIRST_PROP_KEY),
			propertyManager2.getSharedStringProperty(FIRST_PROP_KEY));

		Assert.assertEquals(propertyManager2.getSharedStringProperty(SECOND_TEST_PROP),
			propertyManager1.getSharedStringProperty(SECOND_TEST_PROP));

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				propertyManager1.setSharedStringProperty(SECOND_TEST_PROP, "test4"); //$NON-NLS-1$
				propertyManager2.setSharedStringProperty(SECOND_TEST_PROP, "test5"); //$NON-NLS-1$

				try {
					propertyManager1.synchronizeSharedProperties();
					propertyManager2.synchronizeSharedProperties();
					propertyManager1.synchronizeSharedProperties();
				} catch (final ESException e) {
					throw new RuntimeException(e);
				} catch (final EMFStorePropertiesOutdatedException e) {
					throw new RuntimeException(e);
				}
			}
		}.run(false);

		// 2. Funktioniert update
		Assert.assertEquals("test5", propertyManager1.getSharedStringProperty(SECOND_TEST_PROP)); //$NON-NLS-1$
		Assert.assertEquals("test5", propertyManager2.getSharedStringProperty(SECOND_TEST_PROP)); //$NON-NLS-1$
	}

	@Test
	public void testVersionedProperty() {

		propertyManager1 = getProjectSpace1().getPropertyManager();
		propertyManager2 = getProjectSpace2().getPropertyManager();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				propertyManager1.setSharedVersionedStringProperty(SECOND_TEST_PROP, "test1"); //$NON-NLS-1$
				propertyManager2.setSharedVersionedStringProperty(SECOND_TEST_PROP, "test2"); //$NON-NLS-1$

				try {
					propertyManager1.synchronizeSharedProperties();
				} catch (final AccessControlException e) {
					fail(e.getMessage());
				} catch (final ESException e) {
					fail(e.getMessage());
				} catch (final EMFStorePropertiesOutdatedException e) {
					fail(e.getMessage());
				}

				try {
					propertyManager2.synchronizeSharedProperties();
					fail();
				} catch (final ESException e) {
					fail();
				} catch (final EMFStorePropertiesOutdatedException e) {
					assertEquals(1, e.getOutdatedProperties().size());
					assertEquals(
						propertyManager1.getSharedStringProperty(SECOND_TEST_PROP),
						((PropertyStringValue) e.getOutdatedProperties().get(0).getValue()).getValue());

				}
			}
		}.run(false);

		// check if rollback succeeded
		Assert.assertEquals("test1", propertyManager1.getSharedStringProperty(SECOND_TEST_PROP)); //$NON-NLS-1$
		Assert.assertEquals("test1", propertyManager2.getSharedStringProperty(SECOND_TEST_PROP)); //$NON-NLS-1$
	}

	@Test
	public void testLocalProperties() throws IOException {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProjectSpace1().getPropertyManager().setLocalProperty("foo", //$NON-NLS-1$
					TestmodelFactory.eINSTANCE.createTestElement());
			}
		}.run(false);

		((ProjectSpaceBase) getProjectSpace1()).save();
		final ProjectSpace loadedProjectSpace = ModelUtil.loadEObjectFromResource(
			ModelPackage.eINSTANCE.getProjectSpace(),
			getProjectSpace1().eResource().getURI(), false);

		assertNotNull(loadedProjectSpace.getPropertyManager().getLocalProperty("foo")); //$NON-NLS-1$
	}
}