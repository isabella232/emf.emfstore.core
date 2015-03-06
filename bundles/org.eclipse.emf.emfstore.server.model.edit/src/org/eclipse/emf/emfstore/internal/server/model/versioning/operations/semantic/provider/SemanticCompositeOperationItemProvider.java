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
package org.eclipse.emf.emfstore.internal.server.model.versioning.operations.semantic.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.server.model.provider.ServerEditPlugin;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.ModelElementGroup;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationGroup;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.provider.CompositeOperationItemProvider;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.semantic.SemanticCompositeOperation;

/**
 * This is the item provider adapter for a
 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.semantic.SemanticCompositeOperation}
 * object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class SemanticCompositeOperationItemProvider extends CompositeOperationItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public SemanticCompositeOperationItemProvider(AdapterFactory adapterFactory) {
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
		if (itemPropertyDescriptors == null)
		{
			super.getPropertyDescriptors(object);

		}
		return itemPropertyDescriptors;
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
		String label = ((SemanticCompositeOperation) object).getCompositeName();
		return label == null || label.length() == 0 ?
			getString("_UI_SemanticCompositeOperation_type") : //$NON-NLS-1$
			getString("_UI_SemanticCompositeOperation_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

	/**
	 * {@inheritDoc}
	 * 
	 * @generated NOT
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.operations.provider.CompositeOperationItemProvider#getChildren(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<?> getChildren(Object object) {
		if (object instanceof SemanticCompositeOperation) {
			final SemanticCompositeOperation operation = (SemanticCompositeOperation) object;
			final ArrayList<Object> result = new ArrayList<Object>();

			final OperationsFactory factory = OperationsFactory.eINSTANCE;
			for (final EStructuralFeature feature : operation.eClass().getEStructuralFeatures()) {
				if (feature instanceof EReference) {
					final EReference reference = (EReference) feature;

					final ModelElementGroup referenceGroup = factory.createModelElementGroup();
					final String key = "_UI_" + reference.getEContainingClass().getName() + "_" + reference.getName() //$NON-NLS-1$ //$NON-NLS-2$
						+ "_feature"; //$NON-NLS-1$
					referenceGroup.setName(getString(key));
					if (reference.isMany()) {
						final List<ModelElementId> value = (List<ModelElementId>) operation.eGet(reference);
						referenceGroup.getModelElements().addAll(value);
					} else {
						final ModelElementId value = (ModelElementId) operation.eGet(reference);
						referenceGroup.getModelElements().add(value);
					}
					result.add(referenceGroup);
				}
			}

			final OperationGroup detailsGroup = factory.createOperationGroup();
			detailsGroup.setName("Sub Operations"); //$NON-NLS-1$
			detailsGroup.getOperations().addAll(operation.getSubOperations());
			result.add(detailsGroup);

			return result;
		}
		return super.getChildren(object);
	}
}