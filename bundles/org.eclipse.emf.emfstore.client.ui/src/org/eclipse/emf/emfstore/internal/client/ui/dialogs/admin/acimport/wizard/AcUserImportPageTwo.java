/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * deser,karakoc
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.wizard;

import java.util.ArrayList;

import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.ImportItemWrapper;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.ImportLabelProvider;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.ImportSource;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author deser, karakoc
 */
public class AcUserImportPageTwo extends WizardPage {

	private TreeViewer tv;
	private ArrayList<ImportItemWrapper> wrappedOrgUnits;

	/**
	 * Page for displaying the OrgUnits that can be imported.
	 */
	public AcUserImportPageTwo() {
		super(Messages.AcUserImportPageTwo_PageTwo);
	}

	/**
	 * @param parent composite, that will be filled.
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);

		composite.setLayout(new GridLayout(2, false));

		final Button selectAll = new Button(composite, SWT.CHECK);
		selectAll.setText(Messages.AcUserImportPageTwo_Select_Deselect_All);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		selectAll.setLayoutData(gridData);

		selectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final boolean selection = ((Button) event.widget).getSelection();
				traverseCheck(tv.getTree(), selection);
				setPageComplete(selection);
			}
		});

		// How to add a filter to a treeviewer:
		// http://jmesnil.net/weblog/2007/02/26/add-a-filter-to-a-treeviewer/
		// Due to compatibility to ganymede
		final PatternFilter patternFilter = new PatternFilter();
		@SuppressWarnings("deprecation")
		final FilteredTree filter = new FilteredTree(composite, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
			| SWT.V_SCROLL, patternFilter);

		gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.heightHint = convertHorizontalDLUsToPixels(280);
		filter.setLayoutData(gridData);

		tv = filter.getViewer();

		tv.getTree().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					final TreeItem item = (TreeItem) event.item;
					final boolean checked = item.getChecked();
					traverseCheck(item, checked);

					// TODO not perfect:
					setPageComplete(true);
					// walk through the whole tree and look, if sth. is
					// selected now, if this is the case, then we can
					// finish otherwise not.
				}
			}
		});

		final Button expandAllButton = new Button(composite, SWT.PUSH);
		expandAllButton.setText(Messages.AcUserImportPageTwo_ExpandAll);
		expandAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				tv.expandAll();
			}
		});

		final Button collapseAllButton = new Button(composite, SWT.PUSH);
		collapseAllButton.setText(Messages.AcUserImportPageTwo_CollapseAll);
		collapseAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				tv.collapseAll();
			}
		});

		setControl(composite);
		setPageComplete(false);
	}

	/**
	 * @return Returns all items, that are checked in the tree.
	 */
	public ArrayList<ImportItemWrapper> getCheckedItems() {
		// first clear old checked items
		wrappedOrgUnits = new ArrayList<ImportItemWrapper>();
		this.traverse(tv.getTree());
		return wrappedOrgUnits;
	}

	private void traverseCheck(TreeItem item, boolean check) {
		if (check) {
			tv.expandToLevel(item, AbstractTreeViewer.ALL_LEVELS);
		} else {
			tv.collapseToLevel(item, AbstractTreeViewer.ALL_LEVELS);
		}
		final TreeItem[] items = item.getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].setChecked(check);
			traverseCheck(items[i], check);
		}
	}

	private void traverseCheck(Tree tree, boolean check) {
		final TreeItem[] items = tree.getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].setChecked(check);
			traverseCheck(items[i], check);
		}
	}

	private void traverse(TreeItem item) {
		final TreeItem[] items = item.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked()) {
				wrappedOrgUnits.add((ImportItemWrapper) items[i].getData());
			}
			traverse(items[i]);
		}

	}

	private void traverse(Tree tree) {
		final TreeItem[] items = tree.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked()) {
				wrappedOrgUnits.add((ImportItemWrapper) items[i].getData());
			}
			traverse(items[i]);
		}

	}

	/**
	 * Initializes the page, i.e. this method is not called at the time this class gets instantiated but later, when the
	 * page is going to get displayed.
	 *
	 * @param src the selected ImportSource
	 */
	public void init(ImportSource src) {
		final AcUserImportWizard wizard = (AcUserImportWizard) getWizard();

		tv.setContentProvider(src);
		tv.setLabelProvider(new ImportLabelProvider(wizard.getController()));
		tv.setInput(src.getElements(null)); // argument is a non-null that will
		// be ignored

		setTitle(wizard.getController().getTitle());
		this.setMessage(wizard.getController().getMessage());
	}
}
