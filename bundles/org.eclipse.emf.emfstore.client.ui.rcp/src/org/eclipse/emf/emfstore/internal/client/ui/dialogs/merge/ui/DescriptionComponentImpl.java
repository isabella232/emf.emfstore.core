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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.util.UIDecisionUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * Displays the description in the decision box.
 *
 * @author wesendon
 */
public class DescriptionComponentImpl implements DescriptionComponent {

	/**
	 * Constructor.
	 */
	public DescriptionComponentImpl() {
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.MergeComponent#init(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.DecisionBox,
	 *      org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict)
	 */
	public void init(Composite parent, DecisionBox decisionBox, VisualConflict visualConflict) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 20;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Label image = new Label(composite, SWT.NONE);
		image.setImage(UIDecisionUtil.getImage(visualConflict.getConflictDescription().getImage()));
		image.setToolTipText(visualConflict.getClass().getSimpleName());
		image.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		image.setBackground(parent.getBackground());

		final ArrayList<StyleRange> styleRanges = new ArrayList<StyleRange>();
		String description = StringUtils.EMPTY;
		for (final String tmp : DescriptionComponenptUtil.splitText(decisionBox,
			visualConflict.getConflictDescription())) {
			if (tmp.startsWith("::")) { //$NON-NLS-1$
				styleRanges.add(createStyleRange(description.length(), tmp.length() - 2));
				description += tmp.substring(2);
			} else {
				description += tmp;
			}
		}

		final Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		final FillLayout groupLayout = new FillLayout();
		groupLayout.marginHeight = 5;
		groupLayout.marginWidth = 6;
		group.setLayout(groupLayout);
		group.setBackground(parent.getBackground());
		group.setText(Messages.DescriptionComponentImpl_ConflictDescription);

		final StyledText styledDescription = new StyledText(group, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY);
		styledDescription.setEditable(false);
		styledDescription.setEnabled(false);
		styledDescription.setText(description + "\n"); //$NON-NLS-1$
		styledDescription.setWordWrap(true);
		styledDescription.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
		styledDescription.setBackground(parent.getBackground());
	}

	private StyleRange createStyleRange(int start, int length) {
		final StyleRange styleRange = new StyleRange();
		styleRange.start = start;
		styleRange.length = length;
		styleRange.fontStyle = SWT.BOLD;
		return styleRange;
	}
}
