/**
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 */
package org.eclipse.emf.emfstore.test.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.emfstore.test.model.TestmodelFactory;
import org.eclipse.emf.emfstore.test.model.TestmodelPackage;
import org.eclipse.emf.emfstore.test.model.TypeWithFeatureMapNonContainment;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.emfstore.test.model.TypeWithFeatureMapNonContainment}
 * object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class TypeWithFeatureMapNonContainmentItemProvider extends TestTypeItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public TypeWithFeatureMapNonContainmentItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addFirstKeyPropertyDescriptor(object);
			addSecondKeyPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the First Key feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addFirstKeyPropertyDescriptor(Object object) {
		itemPropertyDescriptors
			.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
				getResourceLocator(),
				getString("_UI_TypeWithFeatureMapNonContainment_firstKey_feature"), //$NON-NLS-1$
				getString("_UI_PropertyDescriptor_description", "_UI_TypeWithFeatureMapNonContainment_firstKey_feature", //$NON-NLS-1$//$NON-NLS-2$
					"_UI_TypeWithFeatureMapNonContainment_type"), //$NON-NLS-1$
				TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__FIRST_KEY,
				true,
				false,
				true,
				null,
				null,
				null));
	}

	/**
	 * This adds a property descriptor for the Second Key feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addSecondKeyPropertyDescriptor(Object object) {
		itemPropertyDescriptors
			.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
				getResourceLocator(),
				getString("_UI_TypeWithFeatureMapNonContainment_secondKey_feature"), //$NON-NLS-1$
				getString("_UI_PropertyDescriptor_description", //$NON-NLS-1$
					"_UI_TypeWithFeatureMapNonContainment_secondKey_feature", //$NON-NLS-1$
					"_UI_TypeWithFeatureMapNonContainment_type"), //$NON-NLS-1$
				TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__SECOND_KEY,
				true,
				false,
				true,
				null,
				null,
				null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__MAP);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns TypeWithFeatureMapNonContainment.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/TypeWithFeatureMapNonContainment")); //$NON-NLS-1$
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((TypeWithFeatureMapNonContainment) object).getName();
		return label == null || label.length() == 0 ? getString("_UI_TypeWithFeatureMapNonContainment_type") : //$NON-NLS-1$
			getString("_UI_TypeWithFeatureMapNonContainment_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(TypeWithFeatureMapNonContainment.class)) {
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__MAP:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
			return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors
			.add(createChildParameter(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__MAP,
				FeatureMapUtil.createEntry(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__FIRST_KEY,
					TestmodelFactory.eINSTANCE.createTestType())));

		newChildDescriptors
			.add(createChildParameter(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__MAP,
				FeatureMapUtil.createEntry(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__FIRST_KEY,
					TestmodelFactory.eINSTANCE.createTypeWithFeatureMapNonContainment())));

		newChildDescriptors
			.add(createChildParameter(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__MAP,
				FeatureMapUtil.createEntry(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__FIRST_KEY,
					TestmodelFactory.eINSTANCE.createTypeWithFeatureMapContainment())));

		newChildDescriptors
			.add(createChildParameter(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__MAP,
				FeatureMapUtil.createEntry(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__SECOND_KEY,
					TestmodelFactory.eINSTANCE.createTestType())));

		newChildDescriptors
			.add(createChildParameter(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__MAP,
				FeatureMapUtil.createEntry(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__SECOND_KEY,
					TestmodelFactory.eINSTANCE.createTypeWithFeatureMapNonContainment())));

		newChildDescriptors
			.add(createChildParameter(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__MAP,
				FeatureMapUtil.createEntry(TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__SECOND_KEY,
					TestmodelFactory.eINSTANCE.createTypeWithFeatureMapContainment())));
	}

	/**
	 * This returns the label text for {@link org.eclipse.emf.edit.command.CreateChildCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getCreateChildText(Object owner, Object feature, Object child, Collection<?> selection) {
		Object childFeature = feature;
		Object childObject = child;

		if (childFeature instanceof EStructuralFeature
			&& FeatureMapUtil.isFeatureMap((EStructuralFeature) childFeature)) {
			FeatureMap.Entry entry = (FeatureMap.Entry) childObject;
			childFeature = entry.getEStructuralFeature();
			childObject = entry.getValue();
		}

		boolean qualify = childFeature == TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__FIRST_KEY ||
			childFeature == TestmodelPackage.Literals.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT__SECOND_KEY;

		if (qualify) {
			return getString("_UI_CreateChild_text2", //$NON-NLS-1$
				new Object[] { getTypeText(childObject), getFeatureText(childFeature), getTypeText(owner) });
		}
		return super.getCreateChildText(owner, feature, child, selection);
	}

}
