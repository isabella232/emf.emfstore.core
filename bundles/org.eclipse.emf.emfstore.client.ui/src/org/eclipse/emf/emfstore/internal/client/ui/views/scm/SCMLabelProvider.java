/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Shterev
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.scm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.ui.Activator;
import org.eclipse.emf.emfstore.internal.client.ui.common.EClassFilter;
import org.eclipse.emf.emfstore.internal.client.ui.views.changes.ChangePackageVisualizationHelper;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ImageProxy;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy;
import org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationId;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;

/**
 * Label provider for the SCM views.
 *
 * @author Shterev
 */
public class SCMLabelProvider extends ColumnLabelProvider {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm"); //$NON-NLS-1$

	private static final String ELEMENT_NOT_FOUND = Messages.SCMLabelProvider_InsufficientInformation;
	/**
	 * String to display as info for Local revisions.
	 */
	protected static final String LOCAL_REVISION = Messages.SCMLabelProvider_LocalRevision;

	private final List<OperationId> highlighted;

	private ChangePackageVisualizationHelper changePackageVisualizationHelper;
	private final Image baseRevision;
	private final Image currentRevision;
	private final Image headRevision;

	private final ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(
		ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
	private final AdapterFactoryLabelProvider adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(
		adapterFactory);
	private Project project;

	/**
	 * Default constructor.
	 *
	 * @param project
	 *            the project that is used to resolve revision numbers
	 */
	public SCMLabelProvider(Project project) {
		super();
		this.project = project;
		highlighted = new ArrayList<OperationId>();

		baseRevision = Activator.getImageDescriptor("icons/HistoryInfo_base.png").createImage(); //$NON-NLS-1$
		currentRevision = Activator.getImageDescriptor("icons/HistoryInfo_current.png").createImage(); //$NON-NLS-1$
		headRevision = Activator.getImageDescriptor("icons/HistoryInfo_head.png").createImage(); //$NON-NLS-1$
	}

	/**
	 * Default constructor.
	 */
	public SCMLabelProvider() {
		this(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {

		String ret = null;
		if (element instanceof OperationProxy) {
			final OperationProxy proxy = (OperationProxy) element;
			return getText(proxy);
		} else if (element instanceof HistoryInfo) {
			final HistoryInfo historyInfo = (HistoryInfo) element;
			return getText(historyInfo);
		} else if (element instanceof AbstractOperation
			&& changePackageVisualizationHelper != null) {
			ret = changePackageVisualizationHelper
				.getDescription((AbstractOperation) element);
		} else if (element instanceof ModelElementId
			&& changePackageVisualizationHelper != null) {
			final EObject modelElement = changePackageVisualizationHelper
				.getModelElement((ModelElementId) element);
			if (modelElement != null) {
				ret = adapterFactoryLabelProvider.getText(modelElement);
			} else {
				return ELEMENT_NOT_FOUND;
			}
		} else if (element instanceof ChangePackage) {
			final ChangePackage changePackage = (ChangePackage) element;
			return getText(changePackage);
		} else if (element instanceof FileBasedChangePackage) {
			final FileBasedChangePackage changePackage = (FileBasedChangePackage) element;
			return getText(changePackage);
		} else if (element instanceof EObject) {
			// TODO: rather reference virtual node directly??
			ret = adapterFactoryLabelProvider.getText(element);
		} else if (element instanceof VirtualNode<?>) {
			// TreeNode node = (TreeNode) element;
			// must be node containing filtered operations
			// if (node.getValue() instanceof ChangePackage) {
			return EClassFilter.INSTANCE.getFilterLabel();
			// }
		} else {
			ret = super.getText(element);
		}

		return ret;
	}

	private String getText(AbstractChangePackage changePackage) {
		final StringBuilder builder = new StringBuilder();
		builder.append(Messages.SCMLabelProvider_ChangePackage);
		if (changePackage.getLogMessage() != null) {
			final LogMessage logMessage = changePackage.getLogMessage();
			builder.append(" ["); //$NON-NLS-1$
			builder.append(logMessage.getAuthor());
			final Date clientDate = logMessage.getClientDate();
			if (clientDate != null) {
				builder.append(" @ "); //$NON-NLS-1$
				builder.append(dateFormat.format(clientDate));
			}
			builder.append("] "); //$NON-NLS-1$
			builder.append(logMessage.getMessage());
		}
		return builder.toString();
	}

	/**
	 * Gets the text for a history info. This may be overridden by subclasses to
	 * change the behavior (e.g. if info should be distributed across multiply
	 * label providers)
	 *
	 * @param historyInfo
	 *            The historInfo the text is retrieved for.
	 * @return The text for the given historyInfo.
	 */
	protected String getText(HistoryInfo historyInfo) {
		if (historyInfo.getPrimarySpec() != null
			&& historyInfo.getPrimarySpec().getIdentifier() == -1) {
			return LOCAL_REVISION;
		}

		String baseVersion = StringUtils.EMPTY;
		if (historyInfo.getPrimarySpec().getIdentifier() == ESWorkspaceProviderImpl
			.getProjectSpace(project).getBaseVersion().getIdentifier()) {
			baseVersion = "*"; //$NON-NLS-1$
		}
		final StringBuilder builder = new StringBuilder();

		if (!historyInfo.getTagSpecs().isEmpty()) {
			builder.append("["); //$NON-NLS-1$
			for (final TagVersionSpec versionSpec : historyInfo.getTagSpecs()) {
				builder.append(versionSpec.getName());
				builder.append(","); //$NON-NLS-1$
			}
			builder.replace(builder.length() - 1, builder.length(), "] "); //$NON-NLS-1$
		}

		builder.append(baseVersion);
		builder.append(Messages.SCMLabelProvider_Version);
		builder.append(historyInfo.getPrimarySpec().getIdentifier());
		LogMessage logMessage = null;

		if (historyInfo.getLogMessage() != null) {
			logMessage = historyInfo.getLogMessage();
		} else if (historyInfo.getChangePackage() != null
			&& historyInfo.getChangePackage().getLogMessage() != null) {
			logMessage = historyInfo.getChangePackage().getLogMessage();
		}
		if (logMessage != null) {
			builder.append(" ["); //$NON-NLS-1$
			builder.append(logMessage.getAuthor());
			final Date clientDate = logMessage.getClientDate();
			if (clientDate != null) {
				builder.append(" @ "); //$NON-NLS-1$
				builder.append(dateFormat.format(clientDate));
			}
			builder.append("] "); //$NON-NLS-1$
			builder.append(logMessage.getMessage());
		}
		return builder.toString();
	}

	private String getText(OperationProxy proxy) {
		if (!proxy.isLabelProviderReady()) {
			initProxy(proxy);
		}
		return proxy.getLabel();
	}

	private ImageData createImageFromProxy(OperationProxy proxy) {
		if (!proxy.isLabelProviderReady()) {
			initProxy(proxy);
		}
		final ImageProxy imageProxy = proxy.getImage();
		final ImageData imageData = new ImageData(imageProxy.getWidth(),
			imageProxy.getHeight(),
			imageProxy.getDepth(),
			new PaletteData(imageProxy.getRedMask(), imageProxy.getGreenMask(), imageProxy.getBlueMask()),
			imageProxy.getScanlinePad(),
			imageProxy.getData());
		return imageData;
	}

	private void initProxy(OperationProxy proxy) {
		final FileBasedChangePackage changePackage = getChangePackage(proxy);
		final AbstractOperation operation = changePackage.get(proxy.getIndex());
		prepareProxy(proxy, operation);
	}

	private void prepareProxy(OperationProxy proxy, AbstractOperation operation) {
		final ImageData imageData = changePackageVisualizationHelper
			.getImage(adapterFactoryLabelProvider, operation)
			.getImageData();
		final ImageProxy imageProxy = ImageProxy.create()
			.setWitdh(imageData.width)
			.setHeight(imageData.height)
			.setDepth(imageData.depth)
			.setRedMask(imageData.palette.redMask)
			.setGreenMask(imageData.palette.greenMask)
			.setBlueMask(imageData.palette.blueMask)
			.setScanlinePad(imageData.scanlinePad)
			.setData(imageData.data);
		proxy.setImage(imageProxy);
		proxy.setLabel(
			changePackageVisualizationHelper.getDescription(operation));

		if (CompositeOperation.class.isInstance(operation)) {
			final CompositeOperation compositeOperation = (CompositeOperation) operation;
			final EList<AbstractOperation> subOperations = compositeOperation.getSubOperations();
			final EList<OperationProxy> proxies = proxy.getProxies();

			final Iterator<AbstractOperation> subOperationsIterator = subOperations.iterator();
			final Iterator<OperationProxy> proxiesIterator = proxies.iterator();

			while (subOperationsIterator.hasNext()) {
				final AbstractOperation op = subOperationsIterator.next();
				final OperationProxy p = proxiesIterator.next();
				prepareProxy(p, op);
			}
		}
	}

	private FileBasedChangePackage getChangePackage(OperationProxy proxy) {
		return ModelUtil.getParent(FileBasedChangePackage.class, proxy);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getForeground(Object element) {

		if (element instanceof AbstractOperation) {
			final AbstractOperation operation = (AbstractOperation) element;
			if (highlighted.contains(operation.getOperationId())) {
				return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			}
		}

		return super.getForeground(element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Font getFont(Object element) {

		final Font italic = JFaceResources.getFontRegistry().getItalic(
			JFaceResources.DIALOG_FONT);
		final Font bold = JFaceResources.getFontRegistry().getBold(
			JFaceResources.DIALOG_FONT);

		String text = getText(element);
		if (text == null) {
			text = StringUtils.EMPTY;
		}
		if (element instanceof HistoryInfo) {
			if (text.equals(LOCAL_REVISION)) {
				return italic;
			}
			final HistoryInfo historyInfo = (HistoryInfo) element;
			if (historyInfo.getPrimarySpec().getIdentifier() == ESWorkspaceProviderImpl
				.getProjectSpace(project).getBaseVersion().getIdentifier()) {
				return bold;
			}
		} else if (element instanceof ModelElementId) {
			if (text.equals(ELEMENT_NOT_FOUND)) {
				return italic;
			}
		} else if (element instanceof VirtualNode<?>) {
			return italic;
		}
		if (element instanceof EObject
			&& ((EObject) element).eContainer() instanceof AbstractOperation) {
			final AbstractOperation op = (AbstractOperation) ((EObject) element)
				.eContainer();
			if (element instanceof ModelElementId
				&& element.equals(op.getModelElementId())) {
				return bold;
			}

			final EObject modelElement = (EObject) element;
			final Project project = ModelUtil.getProject(modelElement);
			if (project != null
				&& project.getModelElementId(modelElement).equals(
					op.getModelElementId())) {
				return bold;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage(Object element) {

		if (element instanceof OperationProxy) {
			final OperationProxy proxy = (OperationProxy) element;
			final ImageData imageData = createImageFromProxy(proxy);
			final Image swtImage = new Image(
				Display.getDefault(),
				imageData);
			return swtImage;
		} else if (element instanceof ModelElementId) {
			return adapterFactoryLabelProvider
				.getImage(changePackageVisualizationHelper
					.getModelElement((ModelElementId) element));
		} else if (element instanceof HistoryInfo) {
			final String text = getText(element);
			if (text.equals(LOCAL_REVISION)) {
				return currentRevision;
			}
			if (text.matches("\\[.*BASE.*\\].*")) { //$NON-NLS-1$
				return baseRevision;
			}
			if (text.matches("\\[.*HEAD.*\\].*")) { //$NON-NLS-1$
				return headRevision;
			}
		}
		if (element instanceof CompositeOperation
			&& ((CompositeOperation) element).getMainOperation() != null) {
			return changePackageVisualizationHelper.getImage(
				adapterFactoryLabelProvider,
				((CompositeOperation) element).getMainOperation());
		}

		if (element instanceof AbstractOperation) {
			return changePackageVisualizationHelper.getImage(
				adapterFactoryLabelProvider, (AbstractOperation) element);
		}

		return adapterFactoryLabelProvider.getImage(element);
	}

	/**
	 * @param changePackageVisualizationHelper
	 *            the changePackageVisualizationHelper to set
	 */
	public void setChangePackageVisualizationHelper(
		ChangePackageVisualizationHelper changePackageVisualizationHelper) {
		this.changePackageVisualizationHelper = changePackageVisualizationHelper;
	}

	/**
	 * @return the changePackageVisualizationHelper
	 */
	public ChangePackageVisualizationHelper getChangePackageVisualizationHelper() {
		return changePackageVisualizationHelper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToolTipText(Object element) {
		final HistoryInfo historyInfo = ModelUtil.getParent(HistoryInfo.class,
			(EObject) element);
		return getText(historyInfo);
	}

	/**
	 * @return the highlighted elements list.
	 */
	public List<OperationId> getHighlighted() {
		return highlighted;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		changePackageVisualizationHelper.dispose();
		headRevision.dispose();
		currentRevision.dispose();
		baseRevision.dispose();
		if (adapterFactory != null) {
			adapterFactory.dispose();
		}
	}

	/**
	 * @return The project this label provider provides labels for.
	 */
	protected Project getProject() {
		return project;
	}

	/**
	 * Sets the project that is used to resolve revision numbers that are
	 * possibly used within the labels.
	 *
	 * @param newProject
	 *            the project to be set
	 */
	public void setProject(Project newProject) {
		project = newProject;
	}
}
