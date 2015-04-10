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
package org.eclipse.emf.emfstore.internal.server.model.versioning.operations.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsPackage;

/**
 * This is the item provider adapter for a
 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation} object.
 * <!-- begin-user-doc
 * --> <!-- end-user-doc -->
 *
 * @generated
 */
public class AttributeOperationItemProvider extends FeatureOperationItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public AttributeOperationItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addOldValuePropertyDescriptor(object);
			addNewValuePropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Old Value feature. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addOldValuePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
			((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
			getResourceLocator(),
			getString("_UI_AttributeOperation_oldValue_feature"), //$NON-NLS-1$
			getString("_UI_PropertyDescriptor_description", "_UI_AttributeOperation_oldValue_feature", //$NON-NLS-1$ //$NON-NLS-2$
				"_UI_AttributeOperation_type"), //$NON-NLS-1$
			OperationsPackage.Literals.ATTRIBUTE_OPERATION__OLD_VALUE, true, false, false,
			ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the New Value feature. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addNewValuePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
			((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
			getResourceLocator(),
			getString("_UI_AttributeOperation_newValue_feature"), //$NON-NLS-1$
			getString("_UI_PropertyDescriptor_description", "_UI_AttributeOperation_newValue_feature", //$NON-NLS-1$ //$NON-NLS-2$
				"_UI_AttributeOperation_type"), //$NON-NLS-1$
			OperationsPackage.Literals.ATTRIBUTE_OPERATION__NEW_VALUE, true, false, false,
			ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	// begin of custom code
	/**
	 * @param object
	 *            the object
	 * @return This returns the image.
	 * @generated NOT
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/AttributeOperation.png")); //$NON-NLS-1$
	}

	// end of custom code

	/**
	 * {@inheritDoc} This returns the label text for the adapted class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public String getText(Object object) {
		if (object instanceof AttributeOperation) {
			final AttributeOperation op = (AttributeOperation) object;

			String oldValue;
			String newValue;
			if (op.getFeatureName().equals("description")) { //$NON-NLS-1$
				oldValue = op.getOldValue() == null ? null : (String) op.getOldValue();
				newValue = op.getNewValue() == null ? null : (String) op.getNewValue();
			} else {
				oldValue = op.getOldValue() == null ? null : op.getOldValue().toString();
				newValue = op.getNewValue() == null ? null : op.getNewValue().toString();
			}
			final String elemNameAndClass = getModelElementClassAndName(op.getModelElementId());
			if (oldValue == null && newValue == null) {
				return Messages.AttributeOperationItemProvider_Text_Unset + op.getFeatureName()
					+ Messages.AttributeOperationItemProvider_Text_In + elemNameAndClass;
			} else if (oldValue == null && newValue != null) {
				return Messages.AttributeOperationItemProvider_Text_Set + op.getFeatureName()
					+ Messages.AttributeOperationItemProvider_Text_In + elemNameAndClass
					+ Messages.AttributeOperationItemProvider_Text_To + "'" + trim(newValue) + "'"; //$NON-NLS-1$//$NON-NLS-2$
			} else if (oldValue != null && newValue == null) {
				return Messages.AttributeOperationItemProvider_Text_Unset + op.getFeatureName()
					+ Messages.AttributeOperationItemProvider_Text_In + elemNameAndClass
					+ Messages.AttributeOperationItemProvider_Text_FromPreviousValue + "'" + trim(oldValue) + "'"; //$NON-NLS-1$//$NON-NLS-2$
			} else {
				return Messages.AttributeOperationItemProvider_Text_Set + op.getFeatureName()
					+ Messages.AttributeOperationItemProvider_Text_In + elemNameAndClass
					+ Messages.AttributeOperationItemProvider_Text_From + "'" + trim(oldValue) + "'" //$NON-NLS-1$ //$NON-NLS-2$
					+ Messages.AttributeOperationItemProvider_Text_To + "'" + trim(newValue) + "'"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return super.getText(object);
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(AttributeOperation.class)) {
		case OperationsPackage.ATTRIBUTE_OPERATION__OLD_VALUE:
		case OperationsPackage.ATTRIBUTE_OPERATION__NEW_VALUE:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
			return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s
	 * describing the children that can be created under this object. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);
	}

}