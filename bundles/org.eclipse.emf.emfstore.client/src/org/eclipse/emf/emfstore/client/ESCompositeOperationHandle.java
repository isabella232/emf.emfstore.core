/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client;

import org.eclipse.emf.emfstore.client.exceptions.ESInvalidCompositeOperationException;
import org.eclipse.emf.emfstore.common.model.ESModelElementId;

/**
 * A CompositeOperationHandle allows to control a composite operation during the recording process.
 *
 * @author Johannes Faltermeier
 * @since 1.7
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 *
 */
public interface ESCompositeOperationHandle {

	/**
	 * Returns whether the handle is still valid.
	 *
	 * @return <code>false</code> if the composite operation has {@link #end(String, String, ESModelElementId)
	 *         completed} or was {@link #abort() aborted}, <code>true</code> otherwise.
	 */
	boolean isValid();

	/**
	 * Aborts the composite operation. The state before starting the composite operation will be recovered.
	 *
	 * @throws ESInvalidCompositeOperationException
	 *             if the handle is {@link #isValid() invalid}
	 */
	void abort() throws ESInvalidCompositeOperationException;

	/**
	 * Completes a composite operation.
	 *
	 * @param name
	 *            the name for the operation
	 * @param description
	 *            the description of the operation
	 * @param modelElementId
	 *            the {@link ESModelElementId id} of the model element that is most important for the
	 *            operation
	 * @throws ESInvalidCompositeOperationException
	 *             if the handle is {@link #isValid() invalid}
	 */
	void end(String name, String description, ESModelElementId modelElementId)
		throws ESInvalidCompositeOperationException;

}
