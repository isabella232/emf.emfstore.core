/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictDescription;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.util.DecisionUtil;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.util.UIDecisionUtil;
import org.eclipse.emf.emfstore.internal.client.ui.views.changes.ChangePackageVisualizationHelper;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

/**
 * Common base class for a description component.
 *
 * @author emueller
 *
 */
final class DescriptionComponenptUtil {

	/**
	 * Internal split token for any conflict description text.
	 */
	static final String COLON_COLON = "::"; //$NON-NLS-1$
	private static final String CLOSING_BRACKET = "\\]"; //$NON-NLS-1$
	private static final String OPENING_BRACKET = "\\["; //$NON-NLS-1$

	private DescriptionComponenptUtil() {

	}

	/**
	 * Splits the description of the conflict .
	 *
	 * @param box
	 *            the {@link DecisionBox}
	 * @param conflict
	 *            the conflict holding the description to be splitted
	 * @return a list of strings containing the splitted description
	 */
	static List<String> splitText(DecisionBox box, ConflictDescription conflict) {
		final String description = conflict.getDescription();
		final ChangePackageVisualizationHelper visualHelper = UIDecisionUtil.getChangePackageVisualizationHelper(
			box.getDecisionManager());
		final ArrayList<String> result = new ArrayList<String>();

		for (final String string : description.split(OPENING_BRACKET)) {
			final String[] split = string.split(CLOSING_BRACKET);
			if (split.length > 1) {
				final Object obj = conflict.getValues().get(split[0]);
				String tmp = StringUtils.EMPTY;
				if (obj instanceof AbstractOperation) {
					tmp = visualHelper.getDescription((AbstractOperation) obj);
				} else if (obj instanceof EObject) {
					tmp = DecisionUtil.getModelElementName((EObject) obj);
				} else if (obj != null) {
					tmp = obj.toString();
					tmp = UIDecisionUtil.cutString(tmp, 85, true);
				} else {
					tmp = StringUtils.EMPTY;
				}
				tmp = UIDecisionUtil.stripNewLine(tmp);
				split[0] = COLON_COLON + tmp;
			}
			result.addAll(Arrays.asList(split));
		}
		return result;
	}
}