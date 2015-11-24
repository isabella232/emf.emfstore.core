/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.changes;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.emfstore.internal.client.ui.common.OperationCustomLabelProvider;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.swt.graphics.Image;

/**
 * Default label provider for operations.
 *
 * @author emueller
 *
 */
public class DefaultOperationLabelProvider implements OperationCustomLabelProvider {

	/**
	 * The label to be shown for unknown element.
	 */
	protected static final String UNKOWN_ELEMENT = Messages.DefaultOperationLabelProvider_UnknownElement;

	private AdapterFactoryLabelProvider adapterFactoryLabelProvider;
	private ComposedAdapterFactory adapterFactory;

	/**
	 * Constructor.
	 */
	public DefaultOperationLabelProvider() {
		init();
	}

	protected void init() {
		if (adapterFactory == null) {
			adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		}
		if (adapterFactoryLabelProvider == null) {
			adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(adapterFactory);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.OperationCustomLabelProvider#getDescription(org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation)
	 */
	public String getDescription(AbstractOperation operation) {
		if (operation instanceof CompositeOperation) {
			final CompositeOperation compositeOperation = (CompositeOperation) operation;
			// artificial composite because of opposite reference,
			// take description of main operation
			if (compositeOperation.getMainOperation() != null) {
				return getDescription(compositeOperation.getMainOperation());
			}
		}

		return adapterFactoryLabelProvider.getText(operation);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.OperationCustomLabelProvider#getImage(org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation)
	 */
	public Object getImage(AbstractOperation operation) {
		init();
		final Image image = adapterFactoryLabelProvider.getImage(operation);
		dispose();
		return image;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.OperationCustomLabelProvider#canRender(org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation)
	 */
	public CanRender canRender(AbstractOperation operation) {
		return CanRender.Yes;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.OperationCustomLabelProvider#getModelElementName(org.eclipse.emf.ecore.EObject)
	 */
	public String getModelElementName(EObject modelElement) {
		init();
		if (modelElement == null) {
			return UNKOWN_ELEMENT;
		}

		// TODO: provide sensible label for given model element
		final String trimmedText = trim(adapterFactoryLabelProvider.getText(modelElement));
		dispose();
		return trimmedText;
	}

	private String trim(Object object) {
		final String string = object.toString();
		final String result = string.trim();

		if (result.length() == 0) {
			return Messages.DefaultOperationLabelProvider_EmptyName;
		}

		return result;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.ESDisposable#dispose()
	 */
	public void dispose() {
		if (adapterFactory != null) {
			adapterFactory.dispose();
			adapterFactory = null;
		}
		if (adapterFactoryLabelProvider != null) {
			adapterFactoryLabelProvider.dispose();
			adapterFactoryLabelProvider = null;
		}
	}

	/**
	 * Returns the label provider.
	 *
	 * @return the label provider
	 */
	protected AdapterFactoryLabelProvider getAdapterFactoryLabelProvider() {
		return adapterFactoryLabelProvider;
	}
}
