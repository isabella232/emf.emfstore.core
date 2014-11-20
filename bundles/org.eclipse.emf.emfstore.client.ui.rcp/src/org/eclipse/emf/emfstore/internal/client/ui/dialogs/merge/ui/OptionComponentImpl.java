/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Otto von Wesendonk
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.CustomConflictOption;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.util.DecisionUtil;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.components.DetailsDialog;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.util.UIDecisionConfig;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.util.UIDecisionUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

/**
 * This component of the decision box dynamically displays the possible options.
 * 
 * @author wesendon
 */
public class OptionComponentImpl {

	private final Group group;
	private final VisualConflict conflict;
	private final DecisionBox dBox;

	/**
	 * Default constructor.
	 * 
	 * @param dBox
	 *            parent
	 * @param conflict
	 *            conflict.
	 */
	public OptionComponentImpl(DecisionBox dBox, VisualConflict conflict) {
		this.dBox = dBox;
		this.conflict = conflict;
		group = new Group(dBox, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 1;
		layout.verticalSpacing = 1;
		group.setLayout(layout);
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.verticalSpan = 2;
		group.setLayoutData(gridData);
		group.setText(Messages.OptionComponentImpl_ChooseYourOption);

		for (final ConflictOption option : conflict.getOptions()) {
			new OptionContainer(conflict, option);
		}

		refreshButtonColor();
	}

	private String generatePrefix(ConflictOption option) {
		String result = StringUtils.EMPTY;
		final int operationCount = option.getOperations().size();
		final String countInfo = operationCount > 1 ? "s (" + operationCount + ")" : StringUtils.EMPTY; //$NON-NLS-1$ //$NON-NLS-2$
		switch (option.getType()) {
		case MyOperation:
			result = dBox.getDecisionManager().isBranchMerge() ? Messages.OptionComponentImpl_IncomingBranch : Messages.OptionComponentImpl_KeepMyChange + countInfo
				+ ": "; //$NON-NLS-1$
			break;
		case TheirOperation:
			result = dBox.getDecisionManager().isBranchMerge() ? Messages.OptionComponentImpl_CurrentBranch : Messages.OptionComponentImpl_KeepTheirChange + countInfo
				+ ": "; //$NON-NLS-1$
			break;
		case Custom:
			if (option instanceof CustomConflictOption) {
				final String optionPrefix = ((CustomConflictOption) option).getOptionPrefix();
				if (optionPrefix != null) {
					result = optionPrefix;
				}
			}
			break;
		default:
			break;
		}
		return result;
	}

	private void addMouseListener(Composite composite, Listener listener) {
		composite.addListener(SWT.MouseEnter, listener);
		composite.addListener(SWT.MouseExit, listener);
		composite.addListener(SWT.MouseDown, listener);
		composite.addListener(SWT.MouseUp, listener);
		for (final Control child : composite.getChildren()) {
			child.addListener(SWT.MouseEnter, listener);
			child.addListener(SWT.MouseExit, listener);
			child.addListener(SWT.MouseDown, listener);
			child.addListener(SWT.MouseUp, listener);
		}
	}

	/**
	 * Updates the color of the buttons.
	 */
	public void refreshButtonColor() {
		for (final Control composite : group.getChildren()) {
			if (composite instanceof OptionContainer) {
				if (conflict.isResolved() && conflict.getSolution() == ((OptionContainer) composite).getOption()) {
					setColor((Composite) composite, UIDecisionConfig.getOptionSelectedBack(),
						UIDecisionConfig.getOptionSelectedFor());
				} else {
					setColor((Composite) composite, UIDecisionConfig.getDefaultColor(),
						UIDecisionConfig.getDefaultTextColor());
				}
			}
		}
	}

	private void setColor(Composite composite, Color background, Color foreground) {
		composite.setBackground(background);
		composite.setForeground(foreground);
		for (final Control control : composite.getChildren()) {
			control.setBackground(background);
			control.setForeground(foreground);
		}
	}

	private void extraAction(OptionContainer composite) {
		if (composite.getOption().optionChosen()) {
			composite.setText();
			composite.layout();
		}
	}

	/**
	 * Graphical container for an option.
	 * 
	 * @author wesendon
	 */
	private final class OptionContainer extends Composite {

		private final ConflictOption option;
		private final StyledText styledText;

		private OptionContainer(VisualConflict conflict, final ConflictOption option) {
			super(group, SWT.BORDER | SWT.INHERIT_FORCE);
			this.option = option;
			final GridLayout layout = new GridLayout(2, false);
			layout.verticalSpacing = 1;
			setLayout(layout);
			setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			styledText = new StyledText(this, SWT.READ_ONLY);
			styledText.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
			styledText.setEditable(false);
			styledText.setEnabled(false);
			styledText.setBackground(getBackground());
			styledText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
			setText();

			final Button detailsButton = new Button(this, SWT.NONE);
			detailsButton.setText(Messages.OptionComponentImpl_Details);
			detailsButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
			detailsButton.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					final DetailsDialog detailsDialog = new DetailsDialog(getShell(), dBox.getDecisionManager(), option);
					detailsDialog.open();
				}

				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});

			final OptionMouseListener listener = new OptionMouseListener(this);
			OptionComponentImpl.this.addMouseListener(this, listener);
		}

		private void setText() {
			final String prefix = generatePrefix(option);
			final String result = UIDecisionUtil.cutString(option.getStrippedOptionLabel(), DecisionUtil.OPTION_LENGTH,
				true);

			styledText.setText(prefix + " " + result); //$NON-NLS-1$

			if (prefix != StringUtils.EMPTY) {
				final StyleRange prefixRange = new StyleRange();
				prefixRange.start = 0;
				prefixRange.length = prefix.length();
				prefixRange.fontStyle = SWT.ITALIC;
				styledText.setStyleRange(prefixRange);
			}
		}

		public ConflictOption getOption() {
			return option;
		}
	}

	/**
	 * Option mouse listener.
	 * 
	 * @author wesendon
	 */
	private final class OptionMouseListener implements Listener {
		private final OptionContainer composite;

		public OptionMouseListener(OptionContainer composite) {
			this.composite = composite;
			composite.setCursor(new Cursor(composite.getDisplay(), SWT.CURSOR_HAND));
		}

		public void handleEvent(Event event) {
			switch (event.type) {

			case SWT.MouseExit:
				refreshButtonColor();
				break;

			case SWT.MouseEnter:
				if (conflict.isResolved() && conflict.getSolution() == composite.getOption()) {
					setColor(composite, UIDecisionConfig.getOptionSelectedBackEnter(),
						UIDecisionConfig.getDefaultTextColor());
				} else {
					setColor(composite, UIDecisionConfig.getOptionEnteredColor(),
						UIDecisionConfig.getDefaultTextColor());
				}
				break;

			case SWT.MouseUp:

				// do not set selection in case the details button has been clicked
				if (event.widget instanceof Button) {
					return;
				}

				if (composite.getOption().hasExtraOptionAction()) {
					extraAction(composite);
				}
				if (conflict.isResolved() && conflict.getSolution() == composite.getOption()) {
					conflict.setSolution(null);
				} else {
					conflict.setSolution(composite.getOption());
				}
				refreshButtonColor();
				break;

			case SWT.MouseDown:
				break;
			default:
				break;
			}
		}
	}
}