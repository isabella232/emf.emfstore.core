/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * wesendon
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.util.UIDecisionUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Displays the description in the decision box.
 * 
 * @author wesendon
 */
public class DescriptionComponentImpl extends Composite {

	/**
	 * Default constructor.
	 * 
	 * @param parent
	 *            parent
	 * @param conflict
	 *            conflict
	 */
	public DescriptionComponentImpl(DecisionBox parent, VisualConflict conflict) {
		super(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 20;
		setLayout(layout);
		setLayoutData(new GridData(GridData.FILL_BOTH));

		final Label image = new Label(this, SWT.NONE);
		image.setImage(UIDecisionUtil.getImage(conflict.getConflictDescription().getImage()));
		image.setToolTipText(conflict.getClass().getSimpleName());
		image.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		image.setBackground(parent.getBackground());

		// TODO: RAP compat
		// final ArrayList<StyleRange> styleRanges = new ArrayList<StyleRange>();
		String description = StringUtils.EMPTY;
		for (final String tmp : DescriptionComponenptUtil.splitText(parent, conflict.getConflictDescription())) {
			if (tmp.startsWith(DescriptionComponenptUtil.COLON_COLON)) {
				// styleRanges.add(createStyleRange(description.length(), tmp.length() - 2));
				description += tmp.substring(2);
			} else {
				description += tmp;
			}
		}

		final Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		final FillLayout groupLayout = new FillLayout();
		groupLayout.marginHeight = 5;
		groupLayout.marginWidth = 6;
		group.setLayout(groupLayout);
		group.setBackground(parent.getBackground());
		group.setText(Messages.DescriptionComponentImpl_ConflictDescription);

		final Text textDescription = new Text(group, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY);
		textDescription.setEditable(false);
		textDescription.setEnabled(true);
		textDescription.setText(description + "\n"); //$NON-NLS-1$
		// RAP compat
		// styledDescription.setWordWrap(true);
		// styledDescription.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
		textDescription.setBackground(parent.getBackground());
	}

}
