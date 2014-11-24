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
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.DecisionManager;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.util.DecisionUtil;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.components.ContextComponent;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.components.DetailsComponent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.framework.Bundle;

/**
 * Generic container for conflicts.
 * 
 * @author wesendon
 */
public class DecisionBox extends Composite {

	private static final String EMFSTORE_CLIENT_UI_PLUGIN_ID = "org.eclipse.emf.emfstore.client.ui"; //$NON-NLS-1$
	private static final String OPTION_COMPONENT_CLASS = "org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.OptionComponentImpl"; //$NON-NLS-1$
	private static final String DESCRIPTION_COMPONENT_CLASS = "org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.DescriptionComponentImpl"; //$NON-NLS-1$
	private final VisualConflict conflict;
	private final DecisionManager decisionManager;
	private OptionComponent optionComponent;

	/**
	 * Default constructor.
	 * 
	 * @param parent
	 *            parent
	 * @param decisionManager
	 *            decisionManager
	 * @param color
	 *            background color
	 * @param conflict
	 *            the conflict
	 */
	public DecisionBox(Composite parent, DecisionManager decisionManager, Color color, VisualConflict conflict) {
		super(parent, SWT.BORDER);
		this.decisionManager = decisionManager;
		this.conflict = conflict;
		init(color);
	}

	private void init(Color color) {

		final GridLayout decisionLayout = new GridLayout(2, true);
		setLayout(decisionLayout);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		if (color != null) {
			setBackground(color);
		}

		new ContextComponent(this, conflict);
		optionComponent = newInstanceOf(OPTION_COMPONENT_CLASS);
		optionComponent.init(this, this, conflict);

		final DescriptionComponent descriptionComponent = newInstanceOf(DESCRIPTION_COMPONENT_CLASS);
		descriptionComponent.init(this, this, conflict);

		if (DecisionUtil.detailsNeeded(conflict)) {
			new DetailsComponent(this, conflict);
		}

		for (final Control control : getChildren()) {
			control.setBackground(getBackground());
		}
	}

	/**
	 * Returns the decisionmanager.
	 * 
	 * @return decisionmanager
	 */
	public DecisionManager getDecisionManager() {
		return decisionManager;
	}

	/**
	 * Set the solution of this conflict.
	 * 
	 * @param option
	 *            the option
	 */
	public void setSolution(ConflictOption option) {
		conflict.setSolution(option);
		optionComponent.refreshButtonColor();
	}

	/**
	 * Relayouts the box. Needed when box is dynamically resized. This is NOT a
	 * nice solution.
	 * 
	 * @param heightSizeChange
	 *            size delta
	 */
	public void layoutPage(int heightSizeChange) {
		final ScrolledComposite scrollArea = (ScrolledComposite) getParent().getParent();
		scrollArea.setMinSize(scrollArea.getMinWidth(), scrollArea.getMinHeight() + heightSizeChange);
		scrollArea.layout();
	}

	/**
	 * Returns the conflict of the box.
	 * 
	 * @return conflict
	 */
	public VisualConflict getConflict() {
		return conflict;
	}

	private <T> T newInstanceOf(String clazz) {
		try {
			final Class<T> c = loadClass(EMFSTORE_CLIENT_UI_PLUGIN_ID, clazz);
			final T newInstance = c.getConstructor().newInstance();
			return newInstance;
		} catch (final ClassNotFoundException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		} catch (final InstantiationException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		} catch (final IllegalAccessException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		} catch (final IllegalArgumentException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		} catch (final InvocationTargetException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		} catch (final NoSuchMethodException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		} catch (final SecurityException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> loadClass(String bundleName, String clazz) throws ClassNotFoundException {
		final Bundle bundle = Platform.getBundle(bundleName);
		if (bundle == null) {
			throw new ClassNotFoundException(clazz + " cannot be loaded because bundle " + bundleName //$NON-NLS-1$
				+ " cannot be resolved"); //$NON-NLS-1$
		}
		return (Class<T>) bundle.loadClass(clazz);
	}
}
