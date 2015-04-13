/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.scm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.emfstore.internal.client.ui.views.changes.ChangePackageVisualizationHelper;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;

/**
 * Content provider for the SCM views.
 *
 * @author emueller
 */
public class SCMContentProvider extends AdapterFactoryContentProvider {

	private boolean showRootNodes = true;
	private boolean reverseNodes = true;
	private final Map<ChangePackage, VirtualNode<AbstractOperation>> changePackageToFilteredMapping;
	private final Map<ChangePackage, List<Object>> changePackageToNonFilteredMapping;
	private ModelElementIdToEObjectMapping idToEObjectMapping;

	/**
	 * Default constructor.
	 *
	 */
	public SCMContentProvider() {
		super(new ComposedAdapterFactory(
			ComposedAdapterFactory.Descriptor.Registry.INSTANCE));
		changePackageToFilteredMapping = new LinkedHashMap<ChangePackage, VirtualNode<AbstractOperation>>();
		changePackageToNonFilteredMapping = new LinkedHashMap<ChangePackage, List<Object>>();
	}

	/**
	 * @param idToEObjectMapping
	 *            a mapping from IDs to EObjects that is necessary to resolve
	 *            deleted EObjects
	 */
	public SCMContentProvider(
		ModelElementIdToEObjectMapping idToEObjectMapping) {
		this();
		this.idToEObjectMapping = idToEObjectMapping;
	}

	/**
	 * Sets the flag to reverse the order of the nodes. Default value is true -
	 * i.e. the more recent operations are on top.
	 *
	 * @param reverseNodes
	 *            the new value
	 */
	public void setReverseNodes(boolean reverseNodes) {
		this.reverseNodes = reverseNodes;
	}

	/**
	 * Returns if the nodes should be reversed.
	 *
	 * @return true if the nodes should be reversed in order
	 */
	public boolean isReverseNodes() {
		return reverseNodes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object object) {

		if (object instanceof List<?> && showRootNodes) {

			final List<?> list = (List<?>) object;
			final List<Object> result = new ArrayList<Object>(list.size());
			result.addAll(list);
			return ((List<?>) result).toArray();

		} else if (object instanceof List<?>) {
			// valid inputs are a list of HistoryInfos,
			// a list of operations as well as a list
			// of ChangePackages
			final List<?> list = (List<?>) object;

			if (list.size() == 0) {
				return list.toArray();
			}

			final List<Object> result = new ArrayList<Object>(list.size());

			if (isListOf(list, HistoryInfo.class)) {
				for (final HistoryInfo info : (List<HistoryInfo>) list) {
					if (info.getChangePackage() != null) {
						result.addAll(getReversedOperations(info
							.getChangePackage()));
					}
				}
			} else if (isListOf(list, AbstractOperation.class)) {
				final FilteredOperationsResult filteredOpsResult = new FilterOperations(
					idToEObjectMapping).filter(list.toArray());

				result.addAll(filteredOpsResult.getNonFiltered());

				if (filteredOpsResult.getFilteredOperations().size() > 0) {
					final VirtualNode<AbstractOperation> node = new VirtualNode<AbstractOperation>(
						filteredOpsResult.getFilteredOperations());
					result.add(node);
				}
			} else {
				for (final ChangePackage changePackage : (List<ChangePackage>) list) {
					result.addAll(getReversedOperations(changePackage));
				}
			}

			return result.toArray();

		} else if (object instanceof EObject) {
			return new Object[] { object };
		}

		return super.getElements(object);
	}

	private List<OperationProxy> getReversedOperations(AbstractChangePackage changePackage) {
		final ESCloseableIterable<AbstractOperation> operations = changePackage.operations();
		final List<OperationProxy> operationProxies = new ArrayList<OperationProxy>();
		final ChangePackageVisualizationHelper changePackageVisualizationHelper = new ChangePackageVisualizationHelper(
			idToEObjectMapping);
		try {
			final Iterable<AbstractOperation> operationIterable = operations.iterable();
			for (final AbstractOperation abstractOperation : operationIterable) {
				final OperationProxy operationProxy = VersioningFactory.eINSTANCE.createOperationProxy();
				operationProxy.setLabel(changePackageVisualizationHelper.getDescription(abstractOperation));
				operationProxies.add(0, operationProxy);
			}
		} finally {
			operations.close();
		}

		return operationProxies;
	}

	private boolean isListOf(List<?> list, Class<? extends EObject> clazz) {
		final Object firstElement = list.get(0);

		return clazz.isInstance(firstElement);
	}

	private void filter(ChangePackage changePackage, Object[] input,
		Class<? extends EObject> clazz) {

		// check whether we already filtered this change package
		if (changePackageHasBeenFiltered(changePackage)) {
			return;
		}

		final FilteredOperationsResult result = new FilterOperations(
			idToEObjectMapping, clazz).filter(input);
		final VirtualNode<AbstractOperation> node = new VirtualNode<AbstractOperation>(
			result.getFilteredOperations());
		changePackageToNonFilteredMapping.put(changePackage,
			result.getNonFiltered());
		changePackageToFilteredMapping.put(changePackage, node);
	}

	private boolean changePackageHasBeenFiltered(AbstractChangePackage changePackage) {
		return changePackageToNonFilteredMapping.containsKey(changePackage);
	}

	@Override
	public boolean hasChildren(Object object) {
		if (object instanceof FileBasedChangePackage) {
			return true;
		}
		return getChildren(object).length > 0;
	}

	@Override
	public Object[] getChildren(Object object) {

		if (object instanceof OperationProxy) {
			return OperationProxy.class.cast(object).getProxies().toArray();
		} else if (object instanceof HistoryInfo) {
			final HistoryInfo historyInfo = (HistoryInfo) object;
			return getChildren(historyInfo.getChangePackage());
		} else if (object instanceof FileBasedChangePackage) {
			final FileBasedChangePackage changePackage = (FileBasedChangePackage) object;
			final ESCloseableIterable<AbstractOperation> operations = changePackage.operations();
			int opIndex = 0;
			try {
				for (final Iterator<AbstractOperation> iterator = operations.iterable().iterator(); iterator.hasNext();) {
					final AbstractOperation operation = iterator.next();
					final OperationProxy newProxy = createProxy(operation);
					newProxy.setIndex(opIndex);
					changePackage.getOperationProxies().add(newProxy);

					opIndex += 1;
				}
			} finally {
				operations.close();
			}

			return changePackage.getOperationProxies().toArray();
		} else if (object instanceof ChangePackage) {

			final List<Object> result = new ArrayList<Object>();
			final ChangePackage changePackage = (ChangePackage) object;

			filter(changePackage, super.getChildren(object), LogMessage.class);
			result.addAll(changePackageToNonFilteredMapping.get(changePackage));
			final VirtualNode<AbstractOperation> node = changePackageToFilteredMapping
				.get(changePackage);

			if (node.getContent().size() > 0) {
				result.add(node);
			}

			return result.toArray();

		} else if (object instanceof VirtualNode<?>) {
			return ((VirtualNode<?>) object).getContent().toArray();
		} else if (object instanceof CompositeOperation) {
			return ((CompositeOperation) object).getSubOperations().toArray();
		}

		return super.getChildren(object);
	}

	private static OperationProxy createProxy(AbstractOperation operation) {
		final OperationProxy newProxy = VersioningFactory.eINSTANCE.createOperationProxy();

		if (CompositeOperation.class.isInstance(operation)) {
			final CompositeOperation compositeOperation = (CompositeOperation) operation;
			final List<AbstractOperation> leafOperations = compositeOperation.getSubOperations();
			for (final AbstractOperation op : leafOperations) {
				newProxy.getProxies().add(createProxy(op));
			}
		}

		return newProxy;
	}

	/**
	 * Whether to show root nodes.
	 *
	 * @return true, if root nodes are shown, false otherwise
	 */
	public boolean isShowRootNodes() {
		return showRootNodes;
	}

	/**
	 * Determines whether root nodes are shown.
	 *
	 * @param showRootNodes
	 *            if true, root nodes will be shown
	 */
	public void setShowRootNodes(boolean showRootNodes) {
		this.showRootNodes = showRootNodes;
	}

}
