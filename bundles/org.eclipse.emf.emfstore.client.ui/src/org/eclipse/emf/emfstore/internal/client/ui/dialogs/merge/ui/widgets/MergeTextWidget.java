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
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.widgets;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.options.MergeTextOption;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.DecisionBox;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui.components.DetailsComponent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import diff.match.patch.diff_match_patch;
import diff.match.patch.diff_match_patch.Diff;
import diff.match.patch.diff_match_patch.Operation;

/**
 * Is used to display longer conflicting text and to merge them.
 *
 * @author wesendon
 */
public class MergeTextWidget implements Observer {

	private final DecisionBox decisionBox;
	private final ArrayList<ConflictOption> options;
	private TabFolder tabFolder;
	private final DetailsComponent detailsComponent;

	/**
	 * Default constructor.
	 *
	 * @param decisionBox container
	 * @param detailsComponent details component
	 */
	public MergeTextWidget(DecisionBox decisionBox, DetailsComponent detailsComponent) {
		this.decisionBox = decisionBox;
		this.detailsComponent = detailsComponent;
		options = new ArrayList<ConflictOption>();
		decisionBox.getConflict().addObserver(this);
	}

	/**
	 * Add involved ConflictOptions.
	 *
	 * @param option option
	 */
	public void addOption(ConflictOption option) {
		options.add(option);
	}

	/**
	 * Called by container in order to build gui.
	 *
	 * @param parent container
	 */
	public void createContent(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setBackground(parent.getBackground());
		tabFolder.setLayout(new TableWrapLayout());

		for (final ConflictOption option : options) {
			createTab(tabFolder, option);
		}
	}

	private void createTab(TabFolder tabFolder, ConflictOption option) {
		final TabItem tab = new TabItem(tabFolder, SWT.NONE);
		tab.setText(getTitle(option));
		final Text text = new Text(tabFolder, SWT.MULTI | SWT.WRAP);
		setText(option, text);
		text.setBackground(tabFolder.getBackground());
		text.setEditable(isEditable(option));
		tab.setControl(text);

	}

	private void setText(ConflictOption option, final Text styledText) {
		if (option instanceof MergeTextOption) {
			handleMergeTextOption(option, styledText);
		} else {
			styledText.setText(option.getFullOptionLabel());
		}
	}

	private void handleMergeTextOption(ConflictOption option, final Text styledText) {
		final MergeTextOption mergeOption = (MergeTextOption) option;
		final diff_match_patch dmp = new diff_match_patch();
		dmp.Diff_EditCost = 10;
		final LinkedList<Diff> diffMain = dmp.diff_main(mergeOption.getMyText(), mergeOption.getTheirString());
		dmp.diff_cleanupEfficiency(diffMain);

		final StringBuffer description = new StringBuffer();

		for (final Diff diff : diffMain) {
			final String text = diff.text;
			if (!diff.operation.equals(Operation.EQUAL)) {
			}
			description.append(text);
		}
		styledText.setText(description.toString());
		styledText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				final String newText = styledText.getText();
				final String oldText = mergeOption.getMergedText();
				if (newText != null && !newText.equals(oldText)) {
					mergeOption.setMergedText(newText);
					decisionBox.setSolution(mergeOption);
				}
			}
		});
	}

	private boolean isEditable(ConflictOption option) {
		return option instanceof MergeTextOption;
	}

	private String getTitle(ConflictOption option) {
		switch (option.getType()) {
		case MyOperation:
			return "My Version";
		case TheirOperation:
			return "Version from Repository";
		case Custom:
		case MergeText:
			return option.getOptionLabel();
		default:
			return StringUtils.EMPTY;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		final VisualConflict conflict = decisionBox.getConflict();
		if (conflict != null && conflict == o) {
			final ConflictOption solution = conflict.getSolution();
			if (solution instanceof MergeTextOption) {
				for (int i = 0; i < options.size(); i++) {
					if (options.get(i) == solution) {
						detailsComponent.setExpanded(true);
						tabFolder.setSelection(i);
					}
				}
			}
		}
	}
}
