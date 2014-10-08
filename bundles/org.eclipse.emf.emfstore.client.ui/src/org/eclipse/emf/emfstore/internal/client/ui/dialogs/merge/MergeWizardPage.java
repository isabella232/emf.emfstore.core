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
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.DecisionManager;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption.OptionType;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.DecisionBox;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.util.UIDecisionConfig;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Represents the main page of the merge wizard.
 * 
 * @author wesendon
 */
public class MergeWizardPage extends WizardPage {

	/**
	 * Name of wizard page.
	 */
	public static final String PAGE_NAME = Messages.MergeWizardPage_PageName;

	private ArrayList<DecisionBox> decisionBoxes;
	private final DecisionManager decisionManager;

	/**
	 * Default Constructor.
	 * 
	 * @param decisionManager
	 *            a decisionManager
	 */
	protected MergeWizardPage(DecisionManager decisionManager) {
		super(PAGE_NAME);
		this.decisionManager = decisionManager;
		setTitle(Messages.MergeWizardPage_Title);
		final int countMyOperations = decisionManager.countMyOperations();
		final int countTheirOperations = decisionManager.countTheirOperations();
		final int countMyLeafOperations = decisionManager.countMyLeafOperations();
		final int countTheirLeafOperations = decisionManager.countTheirLeafOperations();
		setDescription(MessageFormat.format(
			Messages.MergeWizardPage_Description_1
				+ Messages.MergeWizardPage_Description_2
				+ Messages.MergeWizardPage_Description_3
				+ Messages.MergeWizardPage_Description_4,
			countMyOperations,
			countMyLeafOperations,
			countTheirOperations,
			countTheirLeafOperations));
	}

	/**
	 * {@inheritDoc}
	 */
	public void createControl(final Composite parent) {
		parent.setLayout(new GridLayout());

		final Composite topBar = createTopBar(parent);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(topBar);
		final ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, true).applyTo(scrolledComposite);

		final Composite client = new Composite(scrolledComposite, SWT.NONE);
		client.setLayout(new GridLayout());
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		client.setLayoutData(gridData);

		final ColorSwitcher colorSwitcher = new ColorSwitcher();

		decisionBoxes = new ArrayList<DecisionBox>();

		// show only unresolved conflicts
		final List<VisualConflict> unresolvedConflicts = new ArrayList<VisualConflict>();
		for (final VisualConflict conflict : decisionManager.getConflicts()) {
			if (!conflict.isResolved()) {
				unresolvedConflicts.add(conflict);
			}
		}

		for (final VisualConflict conflict : unresolvedConflicts) {
			decisionBoxes.add(new DecisionBox(client, decisionManager, colorSwitcher.getColor(), conflict));
		}

		// Listener to check for changes in the width of the client. A changed width can influence the height
		// of the client. Thus one has to adjust the height of the scrolledComposite accordingly.
		client.addListener(SWT.Resize, new Listener() {
			private int widthBeforeChange = -1; // initial negative value

			public void handleEvent(Event event) {
				final int widthAfterChange = client.getSize().x;
				if (widthBeforeChange != widthAfterChange) {
					scrolledComposite.setMinHeight(client.computeSize(widthAfterChange, SWT.DEFAULT).y);
					widthBeforeChange = widthAfterChange;
				}
			}
		});

		scrolledComposite.setContent(client);

		final Point computeSize = calcParentSize(parent, topBar);
		scrolledComposite.setMinSize(computeSize);

		setControl(scrolledComposite);
	}

	private Point calcParentSize(final Composite parent, Composite topBar) {
		final Point computeSize = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		computeSize.x = parent.getBounds().width;
		// Due to resizing issues give a bit of extra space.
		computeSize.y = computeSize.y + topBar.getSize().y + 50;
		return computeSize;
	}

	private Composite createTopBar(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		composite.setSize(SWT.DEFAULT, 200);

		final Button acceptMine = new Button(composite, SWT.PUSH);
		acceptMine.setText(Messages.MergeWizardPage_KeepMyChanges);
		acceptMine.addSelectionListener(new SelectAllSelectionListener(OptionType.MyOperation));

		final Button accecptTheirs = new Button(composite, SWT.PUSH);
		accecptTheirs.setText(Messages.MergeWizardPage_KeepTheirChanges);
		accecptTheirs.addSelectionListener(new SelectAllSelectionListener(OptionType.TheirOperation));

		return composite;
	}

	/**
	 * Listener for select all mine and all their buttons.
	 * 
	 * @author wesendon
	 */
	private final class SelectAllSelectionListener implements SelectionListener {

		private final OptionType type;

		public SelectAllSelectionListener(OptionType type) {
			this.type = type;
		}

		public void widgetSelected(SelectionEvent e) {
			select();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			select();
		}

		private void select() {
			if (type.equals(OptionType.MyOperation)) {
				final boolean confirm = MessageDialog.openConfirm(getShell(),
					Messages.MergeWizardPage_Override_Title,
					MessageFormat.format(
						Messages.MergeWizardPage_Override_Message_1
							+ Messages.MergeWizardPage_Override_Message_2
							+ Messages.MergeWizardPage_Override_Message_3,
						decisionManager.countTheirOperations(),
						decisionManager.countTheirLeafOperations()));
				if (!confirm) {
					return;
				}
			}
			for (final DecisionBox box : decisionBoxes) {
				for (final ConflictOption option : box.getConflict().getOptions()) {
					if (option.getType().equals(type)) {
						box.setSolution(option);
						break;
					}
				}
			}
		}
	}

	/**
	 * Small class which switches colors from row to row.
	 * 
	 * @author wesendon
	 */
	private final class ColorSwitcher {
		private boolean color;

		public ColorSwitcher() {
			color = false;
		}

		public Color getColor() {
			color = !color;
			return color ? UIDecisionConfig.getFirstDecisionBoxColor() : UIDecisionConfig.getSecondDecisionBoxColor();
		}
	}
}
