/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for creating a new {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit
 * ACOrgUnit}.
 *
 * @author emueller
 *
 */
public class NewOrgUnitDialog extends Dialog {

	private final Set<String> fieldNames;
	private final Map<String, Text> fieldToTextMapping;
	private final Map<String, String> fieldValues;

	/**
	 * Constructor.
	 *
	 * @param parentShell
	 *            the parent shell
	 * @param fieldNames
	 *            the name of the input fields
	 */
	public NewOrgUnitDialog(Shell parentShell, Set<String> fieldNames) {
		super(parentShell);
		this.fieldNames = fieldNames;
		fieldToTextMapping = new LinkedHashMap<String, Text>();
		fieldValues = new LinkedHashMap<String, String>();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		final StringBuilder builder = new StringBuilder(Messages.NewOrgUnitDialog_Title_Prefix);
		builder.append(StringUtils.join(fieldNames, Messages.NewOrgUnitDialog_Title_JoinSeparator));
		newShell.setText(StringUtils.capitalize(builder.toString().toLowerCase()));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 */
	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(
			convertHorizontalDLUsToPixels(250),
			convertVerticalDLUsToPixels(90));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite comp = (Composite) super.createDialogArea(parent);

		final GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 2;

		for (final String fieldName : fieldNames) {
			final Label label = new Label(comp, SWT.LEFT);
			label.setText(fieldName + ": "); //$NON-NLS-1$
			final Text text = new Text(comp, SWT.SINGLE | SWT.BORDER);
			fieldToTextMapping.put(fieldName, text);
			GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.CENTER)
				.grab(true, false)
				.applyTo(text);
		}

		return comp;
	}

	@Override
	protected void okPressed() {
		for (final Map.Entry<String, Text> field : fieldToTextMapping.entrySet()) {
			fieldValues.put(field.getKey(), field.getValue().getText());
		}
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		fieldValues.values().clear();
		super.cancelPressed();
	}

	/**
	 * Returns the field value.
	 *
	 * @param fieldName
	 *            the name of the field whose value shold be retrieved.
	 * @return the field's value
	 */
	public String getFieldValue(String fieldName) {
		return fieldValues.get(fieldName);
	}
}