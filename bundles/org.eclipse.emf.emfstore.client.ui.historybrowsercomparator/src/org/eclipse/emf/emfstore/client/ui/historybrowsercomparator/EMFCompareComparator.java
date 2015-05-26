/*******************************************************************************
 * Copyright (c) 2013-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * jsommerfeldt - initial API and implementation
 * Johannes Faltermeier - JavaDoc
 * Philip Langer - Upgrade to EMF Compare API version 3
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.ui.historybrowsercomparator;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.domain.ICompareEditingDomain;
import org.eclipse.emf.compare.domain.impl.EMFCompareEditingDomain;
import org.eclipse.emf.compare.ide.ui.internal.configuration.EMFCompareConfiguration;
import org.eclipse.emf.compare.ide.ui.internal.editor.ComparisonEditorInput;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory.Descriptor.Registry;
import org.eclipse.emf.emfstore.client.ui.ESCompare;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Implementation of {@link ESCompare} using the EMF Compare Framework to compare to {@link Project}s.
 *
 * @author jsommerfeldt
 *
 */
// Some EMF Compare classes are internal API
@SuppressWarnings("restriction")
public class EMFCompareComparator implements ESCompare {

	private Comparison comparison;
	private EObject eObject1;
	private EObject eObject2;

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.ui.ESCompare#compare(org.eclipse.emf.ecore.EObject,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public void compare(EObject e1, EObject e2) {
		if (!(e1 instanceof Project) || !(e2 instanceof Project)) {
			throw new IllegalArgumentException("The objects have to be Projects!");
		}

		eObject1 = e1;
		eObject2 = e2;
		comparison = EMFCompare.builder().build().compare(twoWayScope(eObject1, eObject2));
		comparison.setDiagnostic(new BasicDiagnostic());
	}

	private IComparisonScope twoWayScope(EObject e1, EObject e2) {
		return new DefaultComparisonScope(e1, e2, null);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.ui.ESCompare#display()
	 */
	public void display() {
		if (comparison.getDifferences().isEmpty()) {
			final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
			MessageDialog.openInformation(shell, "No changes", "There are no changes in the comparison.");
			return;
		}

		final ICompareEditingDomain editingDomain = EMFCompareEditingDomain.create(eObject1, eObject2, null);
		final Registry adapterFactoryRegistry = ComposedAdapterFactory.Descriptor.Registry.INSTANCE;
		final AdapterFactory adapterFactory = new ComposedAdapterFactory(adapterFactoryRegistry);
		final EMFCompareConfiguration config = createDefaultCompareConfiguration();
		final CompareEditorInput input = new ComparisonEditorInput(config, comparison, editingDomain, adapterFactory);

		CompareUI.openCompareEditor(input);
	}

	private EMFCompareConfiguration createDefaultCompareConfiguration() {
		final CompareConfiguration config = new CompareConfiguration();
		config.setLeftEditable(false);
		config.setRightEditable(false);
		return new EMFCompareConfiguration(config);
	}
}