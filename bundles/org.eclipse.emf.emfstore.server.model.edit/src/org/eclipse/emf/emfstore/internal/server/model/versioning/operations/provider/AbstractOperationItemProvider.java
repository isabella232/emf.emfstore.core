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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.provider.IdentifiableElementItemProvider;
import org.eclipse.emf.emfstore.internal.server.model.provider.ServerEditPlugin;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsPackage;

/**
 * This is the item provider adapter for a
 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class AbstractOperationItemProvider extends IdentifiableElementItemProvider {
	/**
	 * Constant for name and class tag separator.
	 */
	public static final String NAME_CLASS_TAG_SEPARATOR = "&&&"; //$NON-NLS-1$
	/**
	 * Constant for name tag separator.
	 */
	public static final String NAME_TAG__SEPARATOR = "%%%"; //$NON-NLS-1$

	/**
	 * Constant for reference type separator
	 */
	public static final String REFERENCE_TYPE_TAG_SEPARATOR = "#REFERENCE_TYPE_NAME#"; //$NON-NLS-1$

	/**
	 * This constructs an instance from a factory and a notifier. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public AbstractOperationItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @generated NOT
	 * @see org.eclipse.emf.edit.provider.ItemProviderAdapter#getChildren(java.lang.Object)
	 */
	@Override
	public Collection<?> getChildren(Object object) {
		if (object instanceof AbstractOperation) {
			final AbstractOperation operation = (AbstractOperation) object;
			final ArrayList<ModelElementId> result = new ArrayList<ModelElementId>();
			final ModelElementId modelElementId = operation.getModelElementId();
			if (modelElementId != null) {
				result.add(modelElementId);
			}
			result.addAll(operation.getOtherInvolvedModelElements());
			return result;
		}
		return super.getChildren(object);
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

			addModelElementIdPropertyDescriptor(object);
			addAcceptedPropertyDescriptor(object);
			addClientDatePropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Model Element Id feature. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addModelElementIdPropertyDescriptor(Object object) {
		itemPropertyDescriptors
			.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
				getResourceLocator(),
				getString("_UI_AbstractOperation_modelElementId_feature"), //$NON-NLS-1$
				getString(
					"_UI_PropertyDescriptor_description", "_UI_AbstractOperation_modelElementId_feature", "_UI_AbstractOperation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				OperationsPackage.Literals.ABSTRACT_OPERATION__MODEL_ELEMENT_ID, true, false, true, null, null, null));
	}

	/**
	 * This adds a property descriptor for the Accepted feature. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addAcceptedPropertyDescriptor(Object object) {
		itemPropertyDescriptors
			.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
				getResourceLocator(),
				getString("_UI_AbstractOperation_accepted_feature"), //$NON-NLS-1$
				getString(
					"_UI_PropertyDescriptor_description", "_UI_AbstractOperation_accepted_feature", "_UI_AbstractOperation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				OperationsPackage.Literals.ABSTRACT_OPERATION__ACCEPTED, true, false, false,
				ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Client Date feature. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addClientDatePropertyDescriptor(Object object) {
		itemPropertyDescriptors
			.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
				getResourceLocator(),
				getString("_UI_AbstractOperation_clientDate_feature"), //$NON-NLS-1$
				getString(
					"_UI_PropertyDescriptor_description", "_UI_AbstractOperation_clientDate_feature", "_UI_AbstractOperation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				OperationsPackage.Literals.ABSTRACT_OPERATION__CLIENT_DATE, true, false, false,
				ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This returns AbstractOperation.gif.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/AbstractOperation")); //$NON-NLS-1$
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		final String label = ((AbstractOperation) object).getIdentifier();
		return label == null || label.length() == 0 ? getString("_UI_AbstractOperation_type") : //$NON-NLS-1$
			getString("_UI_AbstractOperation_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

		switch (notification.getFeatureID(AbstractOperation.class)) {
		case OperationsPackage.ABSTRACT_OPERATION__ACCEPTED:
		case OperationsPackage.ABSTRACT_OPERATION__CLIENT_DATE:
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

	/**
	 * Return the resource locator for this item provider's resources. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return ServerEditPlugin.INSTANCE;
	}

	public static String getModelElementName(ModelElementId modelElementId) {
		return NAME_TAG__SEPARATOR + modelElementId.getId() + NAME_TAG__SEPARATOR;
	}

	public static String getModelElementNames(List<ModelElementId> modelElementIds) {
		final StringBuilder builder = new StringBuilder();
		for (final ModelElementId modelElementId : modelElementIds) {
			builder.append(NAME_TAG__SEPARATOR);
			builder.append(modelElementId.getId());
			builder.append(NAME_TAG__SEPARATOR);
			builder.append(", "); //$NON-NLS-1$
		}
		if (modelElementIds.size() > 0) {
			builder.replace(builder.lastIndexOf(NAME_TAG__SEPARATOR + ", "), builder.length(), NAME_TAG__SEPARATOR); //$NON-NLS-1$
		}
		if (modelElementIds.size() > 1) {
			builder.replace(builder.lastIndexOf(NAME_TAG__SEPARATOR + ", "), //$NON-NLS-1$
				builder.lastIndexOf(NAME_TAG__SEPARATOR + ", ") + 3, NAME_TAG__SEPARATOR + " and "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return builder.toString();
	}

	public static String getModelElementClassAndName(ModelElementId modelElementId) {
		if (modelElementId == null) {
			return "(Unkown Element)"; //$NON-NLS-1$
		}
		return NAME_CLASS_TAG_SEPARATOR + modelElementId.getId() + NAME_CLASS_TAG_SEPARATOR;
	}

	/**
	 * Returns a comma separated list of class names and model names. {id1, id2}
	 * will become "Comment 'some comment', LeafSection 'section title'"
	 *
	 * @param idList
	 *            the list of model element IDs to return the names for
	 * @return classes and names as a string
	 */
	public static String getModelElementClassesAndNames(EList<ModelElementId> idList, String typeName) {

		final StringBuilder sb = new StringBuilder();

		if (idList.size() > 2) {
			return idList.size() + " " + typeName + "s"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		for (int i = 0; i < idList.size(); i++) {
			if (i > 0 && i == idList.size() - 1) {
				sb.append(Messages.AbstractOperationItemProvider_And);
			} else if (i > 0) {
				sb.append(", "); //$NON-NLS-1$
			}
			final ModelElementId id = idList.get(i);
			sb.append(getModelElementClassAndName(id));

		}
		return sb.toString();
	}

	protected String trim(Object object) {

		if (object == null) {
			return "(null)"; //$NON-NLS-1$
		}

		final String string = object.toString();
		final String result = string.trim();

		if (result.length() == 0) {
			return "(empty)"; //$NON-NLS-1$
		}

		return result;
	}

}