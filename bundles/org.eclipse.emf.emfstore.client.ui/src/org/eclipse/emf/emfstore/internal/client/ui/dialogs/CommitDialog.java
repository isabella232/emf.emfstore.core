/*******************************************************************************
 * Copyright (c) 2008-2015 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Shterev, Hodaie - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPointException;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.ui.Activator;
import org.eclipse.emf.emfstore.internal.client.ui.views.changes.TabbedChangesComposite;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This class shows a ChangesTreeComposite and a Text control to enter commit
 * message.
 *
 * @author Hodaie
 * @author Shterev
 */
public class CommitDialog extends EMFStoreTitleAreaDialog implements
	KeyListener {

	private static final String COMMITDIALOG_TRAY_EXTENSION_POINT = "org.eclipse.emf.emfstore.client.ui.commitdialog.tray"; //$NON-NLS-1$
	private Text txtLogMsg;
	private String logMsg = StringUtils.EMPTY;
	private final AbstractChangePackage changes;
	private EList<String> oldLogMessages;
	private final ProjectSpace activeProjectSpace;
	private final Map<String, CommitDialogTray> trays;
	private Image commitImage;
	private final int numberOfChanges;
	private final ModelElementIdToEObjectMapping idToEObjectMapping;

	/**
	 * Constructor.
	 *
	 * @param parentShell
	 *            shell
	 * @param localChangePackage
	 *            the local {@link AbstractChangePackage} to be displayed
	 * @param activeProjectSpace
	 *            ProjectSpace that will be committed
	 * @param idToEObjectMapping
	 *            a mapping between ModelElementIds and EObjects. This is needed
	 *            correctly infer information about deleted model elements
	 */
	public CommitDialog(Shell parentShell, AbstractChangePackage localChangePackage, ProjectSpace activeProjectSpace,
		ModelElementIdToEObjectMapping idToEObjectMapping) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.idToEObjectMapping = idToEObjectMapping;
		changes = localChangePackage;
		this.activeProjectSpace = activeProjectSpace;
		numberOfChanges = localChangePackage.size();
		trays = new LinkedHashMap<String, CommitDialogTray>();

		for (final ESExtensionElement element : new ESExtensionPoint(
			COMMITDIALOG_TRAY_EXTENSION_POINT, true)
			.getExtensionElements()) {
			try {
				final CommitDialogTray tray = element.getClass("class", //$NON-NLS-1$
					CommitDialogTray.class);
				final String name = element.getAttribute("name"); //$NON-NLS-1$
				tray.init(CommitDialog.this);
				trays.put(name, tray);
			} catch (final ESExtensionPointException e) {
				// fail silently
			}
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.EMFStoreTitleAreaDialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.CommitDialog_Commit);
		commitImage = Activator.getImageDescriptor("icons/arrow_right.png") //$NON-NLS-1$
			.createImage();
		newShell.setImage(commitImage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		oldLogMessages = activeProjectSpace.getOldLogMessages();

		final Composite contents = new Composite(parent, SWT.NONE);
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		contents.setLayout(new GridLayout(2, false));
		String projectName = StringUtils.EMPTY;
		if (activeProjectSpace.getProjectName() != null
			&& activeProjectSpace.getProjectName().length() > 0) {
			projectName = Messages.CommitDialog_OfProject + "\"" + activeProjectSpace.getProjectName() + "\" "; //$NON-NLS-1$//$NON-NLS-2$
		}
		setTitle(Messages.CommitDialog_CommitLocalChanges + projectName + Messages.CommitDialog_ToServer);
		setMessage(Messages.CommitDialog_NumberOfCompositeChanges
			+ numberOfChanges + ", " //$NON-NLS-1$
			+ Messages.CommitDialog_NumberOverallChanges + numberOfChanges);

		// Log message
		final Label lblLogMsg = new Label(contents, SWT.NONE);
		lblLogMsg.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
			false, 2, 1));
		lblLogMsg.setText(Messages.CommitDialog_LogMessage);

		createLogMessageText(contents);

		// previous log messages
		final Label oldLabel = new Label(contents, SWT.NONE);
		oldLabel.setText(Messages.CommitDialog_PreviousMessage);
		final Combo oldMsg = new Combo(contents, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP)
			.grab(true, false).applyTo(oldMsg);

		final ArrayList<String> oldLogMessagesCopy = new ArrayList<String>();
		oldLogMessagesCopy.addAll(oldLogMessages);
		Collections.reverse(oldLogMessagesCopy);
		oldMsg.setItems(oldLogMessagesCopy.toArray(new String[0]));
		oldMsg.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here
			}

			public void widgetSelected(SelectionEvent e) {
				txtLogMsg.setText(oldMsg.getItem(oldMsg.getSelectionIndex()));
			}

		});

		if (oldLogMessages.size() > 0) {
			oldMsg.select(0);
		}

		// ChangesTree
		final ArrayList<AbstractChangePackage> changePackages = new ArrayList<AbstractChangePackage>();
		// changePackages.add(changes);

		// TODO LCP: fully loading change packages to display them..
		final FileBasedChangePackage cp = VersioningFactory.eINSTANCE.createFileBasedChangePackage();
		cp.initialize(FileUtil.createLocationForTemporaryChangePackage());
		final ESCloseableIterable<AbstractOperation> operations = changes.operations();
		try {
			for (final AbstractOperation operation : operations.iterable()) {
				cp.add(operation);
			}
		} finally {
			operations.close();
		}

		changePackages.add(cp);

		final TabbedChangesComposite changesComposite = new TabbedChangesComposite(
			contents, SWT.BORDER, changePackages, getActiveProjectSpace()
				.getProject(), idToEObjectMapping, true);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
			.grab(true, true).span(2, 1).applyTo(changesComposite);

		return contents;
	}

	/**
	 * Returns the active project space.
	 *
	 * @return the active project space
	 */
	public ProjectSpace getActiveProjectSpace() {
		return activeProjectSpace;
	}

	private void createLogMessageText(Composite contents) {
		txtLogMsg = new Text(contents, SWT.MULTI | SWT.LEAD | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
			.align(SWT.FILL, SWT.TOP).hint(1, 45).applyTo(txtLogMsg);
		String logMsg = StringUtils.EMPTY;
		final LogMessage logMessage = changes.getLogMessage();

		if (oldLogMessages.size() == 0) {
			// on first commit, use log message of change package
			logMsg = logMessage.getMessage();
		} else {
			// otherwise use the most recent log message
			logMsg = oldLogMessages.get(oldLogMessages.size() - 1);
		}

		txtLogMsg.setText(logMsg);
		txtLogMsg.selectAll();
		// to implement a shortcut for submitting the commit
		txtLogMsg.addKeyListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {

		logMsg = txtLogMsg.getText();

		for (final CommitDialogTray t : trays.values()) {
			t.okPressed();
		}

		super.okPressed();
	}

	@Override
	public boolean close() {
		commitImage.dispose();
		return super.close();
	}

	/**
	 * @return the log message that has been set by the user.
	 */
	public String getLogText() {
		return logMsg.equals(StringUtils.EMPTY) ? Messages.CommitDialog_EmptyMessage : logMsg;
	}

	/**
	 * handles the pressing of Ctrl+ENTER: OKpressed() is called. {@inheritDoc}
	 *
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		if (e.keyCode == SWT.CR && (e.stateMask & SWT.MOD1) != 0) {
			okPressed();
		}
	}

	/**
	 * does nothing. {@inheritDoc}
	 *
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		// nothing to do
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// final String notifyUsers = "Notify users";
		for (final ESExtensionElement c : new ESExtensionPoint(
			COMMITDIALOG_TRAY_EXTENSION_POINT)
			.getExtensionElements()) {
			final String name = c.getAttribute("name"); //$NON-NLS-1$
			final CommitDialogTray tray = trays.get(name);
			if (tray != null) {
				final Button notificationsButton = createButton(parent, 2138,
					name + " >>", false); //$NON-NLS-1$
				notificationsButton
					.addSelectionListener(new SelectionAdapter() {
						private boolean isOpen;

						@Override
						public void widgetSelected(SelectionEvent e) {
							if (!isOpen) {
								openTray(tray);
								notificationsButton.setText(name + " <<"); //$NON-NLS-1$
								final Rectangle bounds = getShell().getBounds();
								bounds.x -= 100;
								getShell().setBounds(bounds);
							} else {
								closeTray();
								notificationsButton.setText(name + " >>"); //$NON-NLS-1$
								final Rectangle bounds = getShell().getBounds();
								bounds.x += 100;
								getShell().setBounds(bounds);
							}
							isOpen = !isOpen;
						}
					});
			}
		}
		super.createButtonsForButtonBar(parent);
	}
}
