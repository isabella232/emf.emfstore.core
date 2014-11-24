/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
/*******************************************************************************
 *
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.emfstorebrowser.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredList;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * This is a copy of the org.eclipse.ui.dialogs.ElementListSelectionDialog
 * with its class hierarchy flattened. This is class is meant as a replacement
 * in order to avoid a dependency to org.eclipse.ui due to e4.
 *
 */
public abstract class ElementListSelectionDialog extends TrayDialog {

	// private static final String SELECT_ALL_TITLE =
	// Messages.ElementListSelectionDialog_SelectAll;

	// private static final String DESELECT_ALL_TITLE =
	// Messages.ElementListSelectionDialog_DeselectAll;

	private FilteredList fFilteredList;

	private MessageLine fStatusLine;

	private IStatus fLastStatus;

	private Image fImage;

	private ILabelProvider fRenderer;

	private boolean fIgnoreCase = true;

	private boolean fIsMultipleSelection;

	private boolean fMatchEmptyString = true;

	private boolean fAllowDuplicates = true;

	private Label fMessage;

	private Text fFilterText;

	private ISelectionStatusValidator fValidator;

	private String fFilter;

	private String fEmptyListMessage = ""; //$NON-NLS-1$

	private String fEmptySelectionMessage = ""; //$NON-NLS-1$

	private int fWidth = 60;

	private int fHeight = 18;

	private boolean fStatusLineAboveButtons;

	private Object[] fSelection = new Object[0];

	// the final collection of selected elements, or null if this dialog was
	// canceled
	private Object[] result;

	// a collection of the initially-selected elements
	private List<Object> initialSelections = new ArrayList<Object>();

	// title of dialog
	private String title;

	// message to show user
	private String message = ""; //$NON-NLS-1$

	// dialog bounds strategy (since 3.2)
	private int dialogBoundsStrategy = Dialog.DIALOG_PERSISTLOCATION | Dialog.DIALOG_PERSISTSIZE;

	// dialog settings for storing bounds (since 3.2)
	private IDialogSettings dialogBoundsSettings;

	/**
	 * Constructor.
	 *
	 * @param shell
	 *            the parent {@link Shell}
	 */
	protected ElementListSelectionDialog(Shell shell) {
		super(shell);
	}

	/**
	 * Controls whether status line appears to the left of the buttons (default)
	 * or above them.
	 *
	 * @param aboveButtons if <code>true</code> status line is placed above buttons; if <code>false</code> to the right
	 */
	public void setStatusLineAboveButtons(boolean aboveButtons) {
		fStatusLineAboveButtons = aboveButtons;
	}

	/*
	 * (non-Javadoc) Method declared in Window.
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
			if (fImage != null) {
				shell.setImage(fImage);
			}
		}
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
			true);
		createButton(parent, IDialogConstants.CANCEL_ID,
			IDialogConstants.CANCEL_LABEL, false);
	}

	/*
	 * @see Dialog#createButtonBar(Composite)
	 */
	@Override
	protected Control createButtonBar(Composite parent) {
		final Font font = parent.getFont();
		final Composite composite = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		if (!fStatusLineAboveButtons) {
			layout.numColumns = 2;
		}
		layout.marginHeight = 0;
		layout.marginLeft = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setFont(font);

		if (!fStatusLineAboveButtons && isHelpAvailable()) {
			createHelpControl(composite);
		}
		fStatusLine = new MessageLine(composite);
		fStatusLine.setAlignment(SWT.LEFT);
		final GridData statusData = new GridData(GridData.FILL_HORIZONTAL);
		fStatusLine.setErrorStatus(null);
		fStatusLine.setFont(font);
		if (fStatusLineAboveButtons && isHelpAvailable()) {
			statusData.horizontalSpan = 2;
			createHelpControl(composite);
		}
		fStatusLine.setLayoutData(statusData);

		/*
		 * Create the rest of the button bar, but tell it not to
		 * create a help button (we've already created it).
		 */
		final boolean helpAvailable = isHelpAvailable();
		setHelpAvailable(false);
		super.createButtonBar(composite);
		setHelpAvailable(helpAvailable);
		return composite;
	}

	/**
	 * Creates the message area for this dialog.
	 * <p>
	 * This method is provided to allow subclasses to decide where the message will appear on the screen.
	 * </p>
	 *
	 * @param composite
	 *            the parent composite
	 * @return the message label
	 */
	protected Label createMessageArea(Composite composite) {
		final Label label = new Label(composite, SWT.NONE);
		if (message != null) {
			label.setText(message);
		}
		label.setFont(composite.getFont());

		final GridData data = new GridData();
		data.grabExcessVerticalSpace = false;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.BEGINNING;
		label.setLayoutData(data);

		fMessage = label;
		return label;
	}

	/**
	 * Returns the list of initial element selections.
	 *
	 * @return List
	 */
	protected List<Object> getInitialElementSelections() {
		return initialSelections;
	}

	/**
	 * Returns the message for this dialog.
	 *
	 * @return the message for this dialog
	 */
	protected String getMessage() {
		return message;
	}

	/**
	 * Returns the ok button.
	 *
	 * @return the ok button or <code>null</code> if the button is not created
	 *         yet.
	 */
	public Button getOkButton() {
		return getButton(IDialogConstants.OK_ID);
	}

	/**
	 * Returns the list of selections made by the user, or <code>null</code> if the selection was canceled.
	 *
	 * @return the array of selected elements, or <code>null</code> if Cancel
	 *         was pressed
	 */
	public Object[] getResult() {
		return result;
	}

	/**
	 * Sets the initial selection in this selection dialog to the given
	 * elements.
	 *
	 * @param selectedElements
	 *            the array of elements to select
	 */
	public void setInitialSelections(Object[] selectedElements) {
		initialSelections = new ArrayList<Object>(selectedElements.length);
		for (int i = 0; i < selectedElements.length; i++) {
			initialSelections.add(selectedElements[i]);
		}
	}

	/**
	 * Sets the initial selection in this selection dialog to the given
	 * elements.
	 *
	 * @param selectedElements
	 *            the List of elements to select
	 */
	public void setInitialElementSelections(List<Object> selectedElements) {
		initialSelections = selectedElements;
	}

	/**
	 * Sets the message for this dialog.
	 *
	 * @param message
	 *            the message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Set the selections made by the user, or <code>null</code> if the
	 * selection was canceled.
	 *
	 * @param newResult
	 *            list of selected elements, or <code>null</code> if Cancel
	 *            was pressed
	 */
	protected void setResult(List<Object> newResult) {
		if (newResult == null) {
			result = null;
		} else {
			result = new Object[newResult.size()];
			newResult.toArray(result);
		}
	}

	/**
	 * Set the selections made by the user, or <code>null</code> if the
	 * selection was canceled.
	 * <p>
	 * The selections may accessed using <code>getResult</code>.
	 * </p>
	 *
	 * @param newResult -
	 *            the new values
	 * @since 2.0
	 */
	protected void setSelectionResult(Object[] newResult) {
		result = newResult;
	}

	/**
	 * Sets the title for this dialog.
	 *
	 * @param title
	 *            the title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Set the dialog settings that should be used to save the bounds of this
	 * dialog. This method is provided so that clients that directly use
	 * SelectionDialogs without subclassing them may specify how the bounds of
	 * the dialog are to be saved.
	 *
	 * @param settings
	 *            the {@link IDialogSettings} that should be used to store the
	 *            bounds of the dialog
	 *
	 * @param strategy
	 *            the integer constant specifying how the bounds are saved.
	 *            Specified using {@link Dialog#DIALOG_PERSISTLOCATION} and {@link Dialog#DIALOG_PERSISTSIZE}.
	 *
	 * @since 3.2
	 *
	 * @see Dialog#getDialogBoundsStrategy()
	 * @see Dialog#getDialogBoundsSettings()
	 */
	public void setDialogBoundsSettings(IDialogSettings settings, int strategy) {
		dialogBoundsStrategy = strategy;
		dialogBoundsSettings = settings;
	}

	/**
	 * Gets the dialog settings that should be used for remembering the bounds
	 * of the dialog, according to the dialog bounds strategy. Overridden to
	 * provide the dialog settings that were set using {@link #setDialogBoundsSettings(IDialogSettings, int)}.
	 *
	 * @return the dialog settings used to store the dialog's location and/or
	 *         size, or <code>null</code> if the dialog's bounds should not be
	 *         stored.
	 *
	 * @since 3.2
	 *
	 * @see Dialog#getDialogBoundsStrategy()
	 * @see #setDialogBoundsSettings(IDialogSettings, int)
	 */
	@Override
	protected IDialogSettings getDialogBoundsSettings() {
		return dialogBoundsSettings;
	}

	/**
	 * Get the integer constant that describes the strategy for persisting the
	 * dialog bounds. Overridden to provide the dialog bounds strategy that was
	 * set using {@link #setDialogBoundsSettings(IDialogSettings, int)}.
	 *
	 * @return the constant describing the strategy for persisting the dialog
	 *         bounds.
	 *
	 * @since 3.2
	 * @see Dialog#DIALOG_PERSISTLOCATION
	 * @see Dialog#DIALOG_PERSISTSIZE
	 * @see Dialog#getDialogBoundsSettings()
	 * @see #setDialogBoundsSettings(IDialogSettings, int)
	 */
	@Override
	protected int getDialogBoundsStrategy() {
		return dialogBoundsStrategy;
	}

	/**
	 * Sets the image for this dialog.
	 *
	 * @param image the image.
	 */
	public void setImage(Image image) {
		fImage = image;
	}

	/**
	 * Returns the first element from the list of results. Returns <code>null</code> if no element has been selected.
	 *
	 * @return the first result element if one exists. Otherwise <code>null</code> is
	 *         returned.
	 */
	public Object getFirstResult() {
		final Object[] result = getResult();
		if (result == null || result.length == 0) {
			return null;
		}
		return result[0];
	}

	/**
	 * Sets a result element at the given position.
	 *
	 * @param position
	 *            the position of the result
	 * @param element
	 *            the result element
	 */
	protected void setResult(int position, Object element) {
		final Object[] result = getResult();
		result[position] = element;
		setResult(Arrays.asList(result));
	}

	/**
	 * Update the dialog's status line to reflect the given status. It is safe to call
	 * this method before the dialog has been opened.
	 *
	 * @param status
	 *            the current status
	 */
	protected void updateStatus(IStatus status) {
		fLastStatus = status;
		if (fStatusLine != null && !fStatusLine.isDisposed()) {
			updateButtonsEnableState(status);
			fStatusLine.setErrorStatus(status);
		}
	}

	/**
	 * Update the status of the OK button to reflect the given status. Subclasses
	 * may override this method to update additional buttons.
	 *
	 * @param status
	 *            the current status
	 */
	protected void updateButtonsEnableState(IStatus status) {
		final Button okButton = getOkButton();
		if (okButton != null && !okButton.isDisposed()) {
			okButton.setEnabled(!status.matches(IStatus.ERROR));
		}
	}

	/*
	 * @see Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		computeResult();
		super.okPressed();
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

	/**
	 * Handles default selection (double click).
	 * By default, the OK button is pressed.
	 */
	protected void handleDefaultSelected() {
		if (validateCurrentSelection()) {
			buttonPressed(IDialogConstants.OK_ID);
		}
	}

	/**
	 * Specifies if sorting, filtering and folding is case sensitive.
	 *
	 * @param ignoreCase
	 *            whether casing should be ignored
	 */
	public void setIgnoreCase(boolean ignoreCase) {
		fIgnoreCase = ignoreCase;
	}

	/**
	 * Returns if sorting, filtering and folding is case sensitive.
	 *
	 * @return boolean
	 */
	public boolean isCaseIgnored() {
		return fIgnoreCase;
	}

	/**
	 * Specifies whether everything or nothing should be filtered on
	 * empty filter string.
	 *
	 * @param matchEmptyString boolean
	 */
	public void setMatchEmptyString(boolean matchEmptyString) {
		fMatchEmptyString = matchEmptyString;
	}

	/**
	 * Specifies if multiple selection is allowed.
	 *
	 * @param isMultipleSelectionAllowed
	 *            whether multiple selection is allowed
	 */
	public void setMultipleSelection(boolean isMultipleSelectionAllowed) {
		fIsMultipleSelection = isMultipleSelectionAllowed;
	}

	/**
	 * Specifies whether duplicate entries are displayed or not.
	 *
	 * @param allowDuplicates
	 *            whether duplicates are allowed
	 */
	public void setAllowDuplicates(boolean allowDuplicates) {
		fAllowDuplicates = allowDuplicates;
	}

	/**
	 * Sets the list size in unit of characters.
	 *
	 * @param width the width of the list.
	 * @param height the height of the list.
	 */
	public void setSize(int width, int height) {
		fWidth = width;
		fHeight = height;
	}

	/**
	 * Sets the message to be displayed if the list is empty.
	 *
	 * @param message the message to be displayed.
	 */
	public void setEmptyListMessage(String message) {
		fEmptyListMessage = message;
	}

	/**
	 * Sets the message to be displayed if the selection is empty.
	 *
	 * @param message the message to be displayed.
	 */
	public void setEmptySelectionMessage(String message) {
		fEmptySelectionMessage = message;
	}

	/**
	 * Sets an optional validator to check if the selection is valid.
	 * The validator is invoked whenever the selection changes.
	 *
	 * @param validator the validator to validate the selection.
	 */
	public void setValidator(ISelectionStatusValidator validator) {
		fValidator = validator;
	}

	/**
	 * Sets the elements of the list (widget).
	 * To be called within open().
	 *
	 * @param elements the elements of the list.
	 */
	protected void setListElements(Object[] elements) {
		Assert.isNotNull(fFilteredList);
		fFilteredList.setElements(elements);
	}

	/**
	 * Sets the filter pattern.
	 *
	 * @param filter the filter pattern.
	 */
	public void setFilter(String filter) {
		if (fFilterText == null) {
			fFilter = filter;
		} else {
			fFilterText.setText(filter);
		}
	}

	/**
	 * Returns the current filter pattern.
	 *
	 * @return returns the current filter pattern or {@code null} if filter was not set.
	 */
	public String getFilter() {
		if (fFilteredList == null) {
			return fFilter;
		}
		return fFilteredList.getFilter();
	}

	/**
	 * Returns the indices referring the current selection.
	 * To be called within open().
	 *
	 * @return returns the indices of the current selection.
	 */
	protected int[] getSelectionIndices() {
		Assert.isNotNull(fFilteredList);
		return fFilteredList.getSelectionIndices();
	}

	/**
	 * Returns an index referring the first current selection.
	 * To be called within open().
	 *
	 * @return returns the indices of the current selection.
	 */
	protected int getSelectionIndex() {
		Assert.isNotNull(fFilteredList);
		return fFilteredList.getSelectionIndex();
	}

	/**
	 * Sets the selection referenced by an array of elements.
	 * Empty or null array removes selection.
	 * To be called within open().
	 *
	 * @param selection the indices of the selection.
	 */
	protected void setSelection(Object[] selection) {
		Assert.isNotNull(fFilteredList);
		fFilteredList.setSelection(selection);
	}

	/**
	 * Returns an array of the currently selected elements.
	 * To be called within or after open().
	 *
	 * @return returns an array of the currently selected elements.
	 */
	protected Object[] getSelectedElements() {
		Assert.isNotNull(fFilteredList);
		return fFilteredList.getSelection();
	}

	/**
	 * Returns all elements which are folded together to one entry in the list.
	 *
	 * @param index the index selecting the entry in the list.
	 * @return returns an array of elements folded together.
	 */
	public Object[] getFoldedElements(int index) {
		Assert.isNotNull(fFilteredList);
		return fFilteredList.getFoldedElements(index);
	}

	/**
	 * Handles a selection changed event.
	 * By default, the current selection is validated.
	 */
	protected void handleSelectionChanged() {
		validateCurrentSelection();
	}

	/**
	 * Validates the current selection and updates the status line
	 * accordingly.
	 *
	 * @return boolean <code>true</code> if the current selection is
	 *         valid.
	 */
	protected boolean validateCurrentSelection() {
		Assert.isNotNull(fFilteredList);

		IStatus status;
		final Object[] elements = getSelectedElements();

		if (elements.length > 0) {
			if (fValidator != null) {
				status = fValidator.validate(elements);
			} else {
				status = new Status(IStatus.OK, PlatformUI.PLUGIN_ID,
					IStatus.OK, "", //$NON-NLS-1$
					null);
			}
		} else {
			if (fFilteredList.isEmpty()) {
				status = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID,
					IStatus.ERROR, fEmptyListMessage, null);
			} else {
				status = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID,
					IStatus.ERROR, fEmptySelectionMessage, null);
			}
		}

		updateStatus(status);

		return status.isOK();
	}

	/*
	 * @see Dialog#cancelPressed
	 */
	@Override
	protected void cancelPressed() {
		setResult(null);
		super.cancelPressed();
	}

	/**
	 * Creates a filtered list.
	 *
	 * @param parent the parent composite.
	 * @return returns the filtered list widget.
	 */
	@SuppressWarnings("serial")
	protected FilteredList createFilteredList(Composite parent) {
		final int flags = SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
			| (fIsMultipleSelection ? SWT.MULTI : SWT.SINGLE);

		final FilteredList list = new FilteredList(parent, flags, fRenderer,
			fIgnoreCase, fAllowDuplicates, fMatchEmptyString);

		final GridData data = new GridData();
		data.widthHint = convertWidthInCharsToPixels(fWidth);
		data.heightHint = convertHeightInCharsToPixels(fHeight);
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		list.setLayoutData(data);
		list.setFont(parent.getFont());
		list.setFilter(fFilter == null ? "" : fFilter); //$NON-NLS-1$

		list.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				handleDefaultSelected();
			}

			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected();
			}
		});

		fFilteredList = list;

		return list;
	}

	// 3515
	private void handleWidgetSelected() {
		final Object[] newSelection = fFilteredList.getSelection();

		if (newSelection.length != fSelection.length) {
			fSelection = newSelection;
			handleSelectionChanged();
		} else {
			for (int i = 0; i != newSelection.length; i++) {
				if (!newSelection[i].equals(fSelection[i])) {
					fSelection = newSelection;
					handleSelectionChanged();
					break;
				}
			}
		}
	}

	private Text createFilterText(Composite parent) {
		final Text text = new Text(parent, SWT.BORDER);

		final GridData data = new GridData();
		data.grabExcessVerticalSpace = false;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.BEGINNING;
		text.setLayoutData(data);
		text.setFont(parent.getFont());

		text.setText(fFilter == null ? "" : fFilter); //$NON-NLS-1$

		@SuppressWarnings("serial")
		final Listener listener = new Listener() {
			public void handleEvent(Event e) {
				fFilteredList.setFilter(fFilterText.getText());
			}
		};
		text.addListener(SWT.Modify, listener);

		// RAP [rh] missing Key events
		// text.addKeyListener(new KeyListener() {
		// public void keyPressed(KeyEvent e) {
		// if (e.keyCode == SWT.ARROW_DOWN) {
		// fFilteredList.setFocus();
		// }
		// }
		//
		// public void keyReleased(KeyEvent e) {
		// }
		// });

		fFilterText = text;

		return text;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#open()
	 */
	@Override
	public int open() {
		super.open();
		return getReturnCode();
	}

	private void accessSuperCreate() {
		super.create();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#create()
	 */
	@Override
	public void create() {

		BusyIndicator.showWhile(null, new Runnable() {
			public void run() {
				accessSuperCreate();

				Assert.isNotNull(fFilteredList);

				if (fFilteredList.isEmpty()) {
					handleEmptyList();
				} else {
					validateCurrentSelection();
					fFilterText.selectAll();
					fFilterText.setFocus();
				}
			}
		});

		if (fLastStatus != null) {
			updateStatus(fLastStatus);
		}
	}

	/**
	 * Handles empty list by disabling widgets.
	 */
	protected void handleEmptyList() {
		fMessage.setEnabled(false);
		fFilterText.setEnabled(false);
		fFilteredList.setEnabled(false);
		updateOkState();
	}

	/**
	 * Update the enablement of the OK button based on whether or not there
	 * is a selection.
	 *
	 */
	protected void updateOkState() {
		final Button okButton = getOkButton();
		if (okButton != null) {
			okButton.setEnabled(getSelectedElements().length != 0);
		}
	}

	/**
	 * Gets the optional validator used to check if the selection is valid.
	 * The validator is invoked whenever the selection changes.
	 *
	 * @return the validator to validate the selection, or <code>null</code> if no validator has been set.
	 *
	 * @since 1.4
	 */
	protected ISelectionStatusValidator getValidator() {
		return fValidator;
	}

	private Object[] fElements;

	/**
	 * Creates a list selection dialog.
	 *
	 * @param parent the parent widget.
	 * @param renderer the label renderer.
	 */
	public ElementListSelectionDialog(Shell parent, ILabelProvider renderer) {
		super(parent);
		fRenderer = renderer;
	}

	/**
	 * Sets the elements of the list.
	 *
	 * @param elements the elements of the list.
	 */
	public void setElements(Object[] elements) {
		fElements = elements;
	}

	/**
	 * Compute the result and return it.
	 */
	protected void computeResult() {
		setResult(Arrays.asList(getSelectedElements()));
	}

	/*
	 * @see Dialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite contents = (Composite) super.createDialogArea(parent);

		createMessageArea(contents);
		createFilterText(contents);
		createFilteredList(contents);

		setListElements(fElements);

		setSelection(getInitialElementSelections().toArray());

		return contents;
	}

	/**
	 * Returns the filtered list.
	 *
	 * @return the {@link FilteredList}
	 */
	public FilteredList getFilteredList() {
		return fFilteredList;
	}

}
