/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.components;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictContext;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.util.DecisionUtil;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.DecisionBox;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.util.UIDecisionUtil;
import org.eclipse.emf.emfstore.internal.common.ESDisposable;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Displays the context bar in the decision box.
 *
 * @author wesendon
 */
public class ContextComponent extends Composite implements ESDisposable {

	private static final String CONTENT = "content"; //$NON-NLS-1$
	private static final String TITLE_LABEL = "titleLabel"; //$NON-NLS-1$
	private final ComposedAdapterFactory adapterFactory;

	/**
	 * Default constructor.
	 *
	 * @param parent
	 *            parent
	 * @param conflict
	 *            conflict
	 */
	public ContextComponent(DecisionBox parent, VisualConflict conflict) {
		super(parent, SWT.NONE);

		final ConflictContext context = conflict.getConflictContext();
		final PixelConverter pixelConverter = new PixelConverter(this);

		final GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = pixelConverter.convertVerticalDLUsToPixels(1);
		layout.horizontalSpacing = pixelConverter.convertHorizontalDLUsToPixels(20);
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		setBackground(parent.getBackground());

		final FontRegistry fontRegistry = UIDecisionUtil.getFontRegistry();

		final Label meTitle = new Label(this, SWT.NONE);
		meTitle.setText(context.getModelElementTitleLabel());
		meTitle.setFont(fontRegistry.get(TITLE_LABEL));
		meTitle.setBackground(getBackground());

		final Label attTitle = new Label(this, SWT.NONE);
		attTitle.setText(context.getAttributeTitleLabel());
		attTitle.setFont(fontRegistry.get(TITLE_LABEL));
		attTitle.setBackground(getBackground());

		final Label oppTitle = new Label(this, SWT.NONE);
		oppTitle.setText(context.getOpponentTitleLabel());
		oppTitle.setFont(fontRegistry.get(TITLE_LABEL));
		oppTitle.setBackground(getBackground());

		adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		final AdapterFactoryLabelProvider provider = new AdapterFactoryLabelProvider(adapterFactory);

		final CLabel meLabel = new CLabel(this, SWT.NONE);
		meLabel.setImage(provider.getImage(context.getModelElement()));
		meLabel.setText(UIDecisionUtil.cutString(provider.getText(context.getModelElement()), 40, true));
		meLabel.setToolTipText(DecisionUtil.getClassAndName(context.getModelElement()));
		meLabel.setFont(fontRegistry.get(CONTENT));
		meLabel.setBackground(getBackground());

		final Label attLabel = new Label(this, SWT.NONE);
		attLabel.setText(context.getAttribute());
		attLabel.setFont(fontRegistry.get(CONTENT));
		attLabel.setBackground(getBackground());

		final Label oppLable = new Label(this, SWT.NONE);
		oppLable.setText(context.getOpponent());
		oppLable.setFont(fontRegistry.get(CONTENT));
		oppLable.setBackground(getBackground());
	}

	@Override
	public void dispose() {
		if (adapterFactory != null) {
			adapterFactory.dispose();
		}
		super.dispose();
	}

}
