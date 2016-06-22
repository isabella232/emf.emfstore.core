/*******************************************************************************
 * Copyright (c) 2008-2016 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Aleksander Shterev, Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.emfstorebrowser.views;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Create project dialog.
 *
 * @author shterev
 * @author emueller
 */
// TODO: remove description
public class CreateProjectDialog extends TitleAreaDialog {

	/**
	 * Serial version UID for RAP.
	 */
	public static final long serialVersionUID = 1L;

	private Text txtProjectName;
	private String name;
	private String labelText;

	/**
	 * Default constructor.
	 *
	 * @param parent
	 *            the parent shell
	 */
	public CreateProjectDialog(Shell parent) {
		super(parent);
		name = StringUtils.EMPTY;
	}

	/**
	 * Default constructor.
	 *
	 * @param parent
	 *            the parent shell
	 * @param labelText
	 *            the text that will be shown as part of the dialog
	 */
	public CreateProjectDialog(Shell parent, String labelText) {
		this(parent);
		this.labelText = labelText;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite contents = new Composite(parent, SWT.NONE);
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setTitle(Messages.CreateProjectDialog_CreateNewProject);
		getShell().setText(Messages.CreateProjectDialog_CreateNewProject);

		if (labelText != null) {
			setMessage(labelText);
		} else {
			setMessage(Messages.CreateProjectDialog_EnterName);
		}

		final Label name = new Label(contents, SWT.NULL);
		name.setText(Messages.CreateProjectDialog_Name);
		txtProjectName = new Text(contents, SWT.SINGLE | SWT.BORDER);
		txtProjectName.setSize(150, 20);

		final Point defaultMargins = LayoutConstants.getMargins();
		GridLayoutFactory.fillDefaults().numColumns(2).margins(defaultMargins.x, defaultMargins.y)
			.generateLayout(contents);

		return contents;
	}

	@Override
	protected void okPressed() {
		name = txtProjectName.getText();
		super.okPressed();
	}

	/**
	 * Returns the name of the project as entered by the user.
	 *
	 * @return the name of the project that is going to be created
	 */
	public String getName() {
		return name;
	}
}