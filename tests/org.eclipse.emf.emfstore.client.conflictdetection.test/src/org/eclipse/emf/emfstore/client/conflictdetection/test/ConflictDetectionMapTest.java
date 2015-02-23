/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - intial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.conflictdetection.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.test.common.util.TestLogListener;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.impl.IdEObjectCollectionImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CreateDeleteOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.SingleReferenceOperation;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.eclipse.emf.emfstore.test.model.TestmodelFactory;
import org.eclipse.emf.emfstore.test.model.impl.TestElementToStringMapImpl;
import org.eclipse.emf.emfstore.test.model.impl.TestmodelFactoryImpl;
import org.junit.Test;

/**
 * Conflict detection tests for map entries.
 * 
 * @author emueller
 */
public class ConflictDetectionMapTest extends ConflictDetectionTest {

	private static final String HELLO2 = "hello2"; //$NON-NLS-1$
	private static final String HELLO1 = "hello1"; //$NON-NLS-1$
	private static final String BAR2 = "bar2"; //$NON-NLS-1$
	private static final String KEY_IS_NULL_CAN_NOT_BE_USED_FOR_CONFLICT_DETECTION = "Key is null. Can not be used for conflict detection."; //$NON-NLS-1$
	private static final String FOO = "foo"; //$NON-NLS-1$
	private static final String ORG_ECLIPSE_EMF_EMFSTORE_COMMON_MODEL = "org.eclipse.emf.emfstore.common.model"; //$NON-NLS-1$
	private static final String SINGLE_REFERENCE_SUB_OPERATION_OF_CREATE_OPERATION = "Single reference sub operation of create operation"; //$NON-NLS-1$
	private static final String QUUX = "quux"; //$NON-NLS-1$
	private static final String BAR = "bar"; //$NON-NLS-1$

	/**
	 * Tests if creating map entries with the same key conflict.
	 */
	@Test
	public void testConflictCreateVSCreateMapEntryNonContainedKey() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		final TestElement key = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);
		addTestElement(key);

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);
		final ModelElementId keyId = getProjectSpace().getProject().getModelElementId(key);
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);
		final TestElement clonedKey = (TestElement) clonedProjectSpace.getProject().getModelElement(keyId);

		putIntoMapEntryWithNonContainedKey(testElement, key, BAR);
		putIntoMapEntryWithNonContainedKey(clonedTestElement, clonedKey, QUUX);

		final Set<AbstractOperation> conflicts = getConflicts(
			forceGetOperations(),
			forceGetOperations(clonedProjectSpace), getProject());

		assertTrue(conflicts.size() > 0);

	}

	/**
	 * Tests if creating map entries with the same key conflict.
	 */
	@Test
	public void testConflictCreateVsMoveNonContainedKey() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		final TestElement key = TestmodelFactory.eINSTANCE.createTestElement();
		final TestmodelFactoryImpl factory = (TestmodelFactoryImpl) TestmodelFactory.eINSTANCE;
		final TestElementToStringMapImpl newEntry = (TestElementToStringMapImpl) factory.createTestElementToStringMap();
		addTestElement(testElement);
		addTestElement(key);
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				getProject().getModelElements().add(newEntry);
				clearOperations();
				return null;
			}
		});

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);
		final ModelElementId keyId = getProjectSpace().getProject().getModelElementId(key);
		final ModelElementId newEntryId = getProjectSpace().getProject().getModelElementId(newEntry);
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);
		final TestElementToStringMapImpl clonedNewEntry = (TestElementToStringMapImpl) clonedProjectSpace.getProject()
			.getModelElement(newEntryId);

		final TestElement clonedKey = (TestElement) clonedProjectSpace.getProject().getModelElement(keyId);

		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				newEntry.setKey(key);
				testElement.getElementToStringMap().add(newEntry);
				return null;
			}
		});

		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				clonedNewEntry.setKey(clonedKey);
				clonedTestElement.getElementToStringMap().add(clonedNewEntry);
				return null;
			}
		});

		final Set<AbstractOperation> conflicts = getConflicts(
			forceGetOperations(),
			forceGetOperations(clonedProjectSpace),
			getProject());

		assertTrue(conflicts.size() > 0);

	}

	/**
	 * Tests if creating map entries with the same key conflict.
	 */
	@Test
	public void testConflictCreateVSCreateMapEntryNonContainedKeySingleRefSubOpMissing() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		final TestElement key = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);
		addTestElement(key);

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);
		final ModelElementId keyId = getProjectSpace().getProject().getModelElementId(key);
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);
		final TestElement clonedKey = (TestElement) clonedProjectSpace.getProject().getModelElement(keyId);

		putIntoMapEntryWithNonContainedKey(testElement, key, BAR);
		putIntoMapEntryWithNonContainedKey(clonedTestElement, clonedKey, QUUX);

		final List<AbstractOperation> operations = forceGetOperations();
		final CreateDeleteOperation createDeleteOperation = CreateDeleteOperation.class.cast(operations.get(0));
		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				createDeleteOperation.getSubOperations().clear();
			}
		});

		// expect part of the log message
		final TestLogListener logListener = new TestLogListener(SINGLE_REFERENCE_SUB_OPERATION_OF_CREATE_OPERATION);
		Platform.getLog(Platform
			.getBundle(ORG_ECLIPSE_EMF_EMFSTORE_COMMON_MODEL)).addLogListener(logListener);

		getConflicts(
			forceGetOperations(),
			forceGetOperations(clonedProjectSpace),
			getProject());

		assertTrue(logListener.didReceive());
	}

	/**
	 * Tests if creating map entries with the same key conflict.
	 */
	@Test
	public void testConflictCreateVSCreateMapEntryNonContainedKeyKeyIsNull() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		final TestElement key = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);
		addTestElement(key);

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);
		final ModelElementId keyId = getProjectSpace().getProject().getModelElementId(key);
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);
		final TestElement clonedKey = (TestElement) clonedProjectSpace.getProject().getModelElement(keyId);

		putIntoMapEntryWithNonContainedKey(testElement, key, BAR);
		putIntoMapEntryWithNonContainedKey(clonedTestElement, clonedKey, QUUX);

		final List<AbstractOperation> operations = ModelUtil.clone(
			forceGetOperations());
		final CreateDeleteOperation createDeleteOperation = CreateDeleteOperation.class.cast(operations.get(0));
		final SingleReferenceOperation singleReferenceOperation = SingleReferenceOperation.class.cast(
			createDeleteOperation.getSubOperations().get(0));
		// causes null to be returned when trying to find the key
		singleReferenceOperation.getOtherInvolvedModelElements().iterator().next().setId(FOO);

		final List<AbstractOperation> operations2 = ModelUtil.clone(
			forceGetOperations(clonedProjectSpace));
		// causes null to be returned when trying to find the key
		singleReferenceOperation.getOtherInvolvedModelElements().iterator().next().setId(BAR);

		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				getProject().getModelElements().remove(key);
				clonedProjectSpace.getProject().getModelElements().remove(clonedKey);
				return null;
			}
		});

		// expect part of the log message
		final TestLogListener logListener = new TestLogListener(KEY_IS_NULL_CAN_NOT_BE_USED_FOR_CONFLICT_DETECTION);
		Platform.getLog(Platform
			.getBundle(ORG_ECLIPSE_EMF_EMFSTORE_COMMON_MODEL)).addLogListener(logListener);

		getConflicts(operations, operations2, getProject());

		assertTrue(logListener.didReceive());
	}

	@Test
	public void testNonConflictingCreateVsCreate() {
		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		final TestElement key = TestmodelFactory.eINSTANCE.createTestElement();
		final TestElement secondKey = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);
		addTestElement(key);
		addTestElement(secondKey);

		final ModelElementId testElementId = getProject().getModelElementId(testElement);
		final ModelElementId secondKeyId = getProject().getModelElementId(secondKey);

		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			testElementId);
		final TestElement clonedSecondKey = (TestElement) clonedProjectSpace.getProject().getModelElement(secondKeyId);

		putIntoMapEntryWithNonContainedKey(testElement, key, FOO);
		putIntoMapEntryWithNonContainedKey(clonedTestElement, clonedSecondKey, BAR);

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(clonedProjectSpace);

		final Set<AbstractOperation> conflicts = getConflicts(ops1, ops2, getProject());

		assertEquals(0, conflicts.size());
	}

	@Test
	public void testNonConflictingRemoveVsRemove() {
		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		final TestElement key = TestmodelFactory.eINSTANCE.createTestElement();
		final TestElement secondKey = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);
		addTestElement(key);
		addTestElement(secondKey);

		final ModelElementId testElementId = getProject().getModelElementId(testElement);
		final ModelElementId secondKeyId = getProject().getModelElementId(secondKey);

		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			testElementId);
		final TestElement clonedSecondKey = (TestElement) clonedProjectSpace.getProject().getModelElement(secondKeyId);

		putIntoMapEntryWithNonContainedKey(testElement, key, FOO);
		putIntoMapEntryWithNonContainedKey(clonedTestElement, clonedSecondKey, BAR);

		clearOperations();

		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				testElement.getElementToStringMap().clear();
				clonedTestElement.getElementToStringMap().clear();
				return null;
			}
		});

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(clonedProjectSpace);

		final Set<AbstractOperation> conflicts = getConflicts(ops1, ops2, getProject());

		assertEquals(0, conflicts.size());
	}

	@Test
	public void testConflictCreateVSDeleteMapEntryNonContainedKey() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		final TestElement key = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);
		addTestElement(key);

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);
		final ModelElementId keyId = getProjectSpace().getProject().getModelElementId(key);

		putIntoMapEntryWithNonContainedKey(testElement, key, BAR);
		clearOperations();
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);
		final TestElement clonedKey = (TestElement) clonedProjectSpace.getProject().getModelElement(keyId);

		putIntoMapEntryWithNonContainedKey(testElement, key, BAR2);
		deleteMapEntryNonContainedKey(clonedTestElement, clonedKey);

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(clonedProjectSpace);

		final Set<AbstractOperation> conflicts = getConflicts(ops1, ops2, getProject());

		assertTrue(conflicts.size() > 0);
	}

	@Test
	public void testConflictUpdateVSUpdateMapEntryNonContainedKey() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		final TestElement key = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);
		addTestElement(key);

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);
		final ModelElementId keyId = getProjectSpace().getProject().getModelElementId(key);

		putIntoMapEntryWithNonContainedKey(testElement, key, BAR);
		clearOperations();
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);
		final TestElement clonedKey = (TestElement) clonedProjectSpace.getProject().getModelElement(
			keyId);

		putIntoMapEntryWithNonContainedKey(testElement, key, HELLO1);
		putIntoMapEntryWithNonContainedKey(clonedTestElement, clonedKey, HELLO2);

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(clonedProjectSpace);

		final Set<AbstractOperation> conflicts = getConflicts(ops1, ops2, getProject());

		assertTrue(conflicts.size() > 0);
	}

	@Test
	public void testConflictUpdateVSDeleteMapEntryNonContainedKey() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		final TestElement key = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);
		addTestElement(key);

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);
		final ModelElementId keyId = getProjectSpace().getProject().getModelElementId(key);

		putIntoMapEntryWithNonContainedKey(testElement, key, BAR);
		clearOperations();
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);
		final TestElement clonedKey = (TestElement) clonedProjectSpace.getProject().getModelElement(
			keyId);

		putIntoMapEntryWithNonContainedKey(testElement, key, HELLO1);
		deleteMapEntryNonContainedKey(clonedTestElement, clonedKey);

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(clonedProjectSpace);

		final Set<AbstractOperation> conflicts = getConflicts(ops1, ops2, getProject());

		assertTrue(conflicts.size() > 0);
	}

	@Test
	public void testConflictDeleteVSDeleteMapEntryNonContainedKey() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		final TestElement key = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);
		addTestElement(key);

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);
		final ModelElementId keyId = getProjectSpace().getProject().getModelElementId(key);

		putIntoMapEntryWithNonContainedKey(testElement, key, BAR);
		clearOperations();
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);
		final TestElement clonedKey = (TestElement) clonedProjectSpace.getProject().getModelElement(
			keyId);

		deleteMapEntryNonContainedKey(testElement, key);
		deleteMapEntryNonContainedKey(clonedTestElement, clonedKey);

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(clonedProjectSpace);

		final Set<AbstractOperation> conflicts = getConflicts(ops1, ops2, getProject());

		assertEquals(1, conflicts.size());
	}

	/**
	 * Tests if creating map entries with the same key conflict.
	 */
	@Test
	public void testConflictCreateVSCreateMapEntry() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);

		putIntoMapEntry(testElement, FOO, BAR);
		putIntoMapEntry(clonedTestElement, FOO, QUUX);

		final Set<AbstractOperation> conflicts = getConflicts(
			forceGetOperations(),
			forceGetOperations(clonedProjectSpace));

		assertTrue(conflicts.size() > 0);

	}

	@Test
	public void testConflictCreateVSDeleteMapEntry() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);

		putIntoMapEntry(testElement, FOO, BAR);
		clearOperations();
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);

		putIntoMapEntry(testElement, FOO, BAR2);
		deleteMapEntry(clonedTestElement, FOO);

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(clonedProjectSpace);

		final Set<AbstractOperation> conflicts = getConflicts(ops1, ops2);

		assertTrue(conflicts.size() > 0);
	}

	@Test
	public void testDeleteMapEntry() {

		final Map<EObject, String> deletedElements = new LinkedHashMap<EObject, String>();

		final TestElement testElement = Create.testElement();
		final TestElement keyElement = Create.testElement();
		addTestElement(testElement);
		addTestElement(keyElement);

		final ModelElementId testElementId = getProject().getModelElementId(testElement);

		putIntoMapEntryWithNonContainedKey(testElement, keyElement, "foo"); //$NON-NLS-1$

		clearOperations();

		// TOOD: provide adapter
		addObserverTo(getLocalProject(), new IdEObjectCollectionChangeObserver() {
			public void modelElementRemoved(IdEObjectCollection collection, EObject eObject) {
				final String id = IdEObjectCollectionImpl.class.cast(collection).getDeletedModelElementId(eObject)
					.getId();
				deletedElements.put(eObject, id);
			}

			public void notify(Notification notification, IdEObjectCollection collection, EObject modelElement) {
			}

			public void modelElementAdded(IdEObjectCollection collection, EObject eObject) {
			}

			public void collectionDeleted(IdEObjectCollection collection) {
			}
		});

		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				getLocalProject().getModelElements().remove(testElement);
			}
		});

		assertEquals(1, getLocalProject().getAllModelElements().size());
		assertEquals(2, deletedElements.size());
		assertTrue(testElement.getElementToStringMap().isEmpty());

		deletedElements.clear();

		final List<AbstractOperation> operations = forceGetOperations();
		assertEquals(2, operations.size());
		final CreateDeleteOperation createDeleteOperation = checkAndCast(operations.get(0), CreateDeleteOperation.class);

		final MultiReferenceOperation multiRefOp = checkAndCast(
			createDeleteOperation.getSubOperations().get(0),
			MultiReferenceOperation.class);
		final SingleReferenceOperation singleRefOp = checkAndCast(
			createDeleteOperation.getSubOperations().get(1),
			SingleReferenceOperation.class);

		assertEquals("key", singleRefOp.getFeatureName()); //$NON-NLS-1$
		assertNull(singleRefOp.getNewValue());

		assertEquals("elementToStringMap", multiRefOp.getFeatureName()); //$NON-NLS-1$
		assertFalse(multiRefOp.isAdd());

		// revert
		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				getProjectSpace().revert();
			}
		});

		final TestElement element = (TestElement) getProject().getModelElement(testElementId);

		// forward
		assertEquals(1,
			element.getElementToStringMap().size());
		assertEquals("foo", //$NON-NLS-1$
			element.getElementToStringMap().get(keyElement));
	}

	/**
	 * @param localProject
	 * @param idEObjectCollectionChangeObserver
	 */
	@SuppressWarnings("restriction")
	private void addObserverTo(ESLocalProject localProject,
		IdEObjectCollectionChangeObserver idEObjectCollectionChangeObserver) {
		ESLocalProjectImpl.class.cast(
			localProject
			).toInternalAPI().getProject().addIdEObjectCollectionChangeObserver(
				idEObjectCollectionChangeObserver
			);
	}

	@Test
	public void testConflictUpdateVSUpdateMapEntry() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);

		putIntoMapEntry(testElement, FOO, BAR);
		clearOperations();
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);

		putIntoMapEntry(testElement, FOO, HELLO1);
		putIntoMapEntry(clonedTestElement, FOO, HELLO2);

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(clonedProjectSpace);

		final Set<AbstractOperation> conflicts = getConflicts(ops1, ops2);

		assertTrue(conflicts.size() > 0);
	}

	@Test
	public void testConflictUpdateVSDeleteMapEntry() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);

		putIntoMapEntry(testElement, FOO, BAR);
		clearOperations();
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);

		putIntoMapEntry(testElement, FOO, HELLO1);
		deleteMapEntry(clonedTestElement, FOO);

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(clonedProjectSpace);

		final Set<AbstractOperation> conflicts = getConflicts(ops1, ops2);

		assertTrue(conflicts.size() > 0);
	}

	@Test
	public void testConflictDeleteVSDeleteMapEntry() {

		final TestElement testElement = TestmodelFactory.eINSTANCE.createTestElement();
		addTestElement(testElement);

		final ModelElementId modelElementId = getProjectSpace().getProject().getModelElementId(testElement);

		putIntoMapEntry(testElement, FOO, BAR);
		clearOperations();
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		final TestElement clonedTestElement = (TestElement) clonedProjectSpace.getProject().getModelElement(
			modelElementId);

		deleteMapEntry(testElement, FOO);
		deleteMapEntry(clonedTestElement, FOO);

		final List<AbstractOperation> ops1 = forceGetOperations();
		final List<AbstractOperation> ops2 = forceGetOperations(clonedProjectSpace);

		final Set<AbstractOperation> conflicts = getConflicts(ops1, ops2);

		assertEquals(1, conflicts.size());
	}

	private void deleteMapEntry(final TestElement testElement, final String key) {
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				testElement.getStringToStringMap().remove(key);
				return null;
			}
		});
	}

	private void deleteMapEntryNonContainedKey(final TestElement testElement, final TestElement key) {
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				testElement.getElementToStringMap().remove(key);
				return null;
			}
		});
	}

	private void putIntoMapEntry(final TestElement testElement, final String key, final String value) {
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				testElement.getStringToStringMap().put(key, value);
				return null;
			}
		});
	}

	private void putIntoMapEntryWithNonContainedKey(final TestElement testElement, final TestElement key,
		final String value) {
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				testElement.getElementToStringMap().put(key, value);
				return null;
			}
		});
	}

	private void addTestElement(final TestElement testElement) {
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				getProject().addModelElement(testElement);
				clearOperations();
				return null;
			}
		});
	}
}
