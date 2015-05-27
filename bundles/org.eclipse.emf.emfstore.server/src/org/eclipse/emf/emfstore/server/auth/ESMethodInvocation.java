/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * boehlke
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.auth;

import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidInputException;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.server.auth.ESMethod.MethodId;
import org.eclipse.emf.emfstore.server.model.ESSessionId;

/**
 * Represents an method invocation of a method contained in the EmfStore interface.
 *
 * @author boehlke
 * @since 1.5
 */
public class ESMethodInvocation {

	private final MethodId methodId;
	private final Object[] allParameters;
	private final SessionId sessionId;
	private final String methodName;

	/**
	 * Creates a method invocation with given parameters.
	 *
	 * @param methodName the name of the method
	 * @param params the parameters, including the session id
	 * @throws InvalidInputException throw if first parameter is not a session id
	 */
	public ESMethodInvocation(String methodName, Object[] params) throws InvalidInputException {
		sessionId = (SessionId) params[0];
		if (sessionId == null) {
			throw new InvalidInputException("operations requires session id"); //$NON-NLS-1$
		}
		allParameters = params;
		methodId = MethodId.valueOf(methodName.toUpperCase());
		this.methodName = methodName;
	}

	/**
	 * Get the methodId of the operation.
	 *
	 * @return the operation methodId which is an enumified method name
	 */
	public MethodId getType() {
		return methodId;
	}

	/**
	 * Get the invocation parameters.
	 *
	 * @return the parameters of the invocation
	 */
	public Object[] getParameters() {
		// return Arrays.copyOfRange(allParameters, 1, allParameters.length);
		if (allParameters.length > 1) {
			final Object[] result = new Object[allParameters.length - 1];
			for (int i = 1; i < allParameters.length; i++) {
				result[i - 1] = allParameters[i];
			}
			return result;
		}

		return new Object[0];
	}

	/**
	 * Get the invocation parameters, including the session id.
	 *
	 * @return the parameters of the invocation, including the session id
	 */
	public Object[] getAllParameters() {
		return allParameters;
	}

	/**
	 * Get the session id of the invocation.
	 *
	 * @return the session id
	 */
	public ESSessionId getSessionId() {
		return sessionId.toAPI();
	}

	/**
	 * Returns the original method name.
	 *
	 * @return the method name
	 */
	public String getMethodName() {
		return methodName;
	}
}
