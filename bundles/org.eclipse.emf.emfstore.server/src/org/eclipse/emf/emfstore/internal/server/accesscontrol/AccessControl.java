/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.accesscontrol;

import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPointException;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.impl.api.ESOrgUnitProviderImpl;
import org.eclipse.emf.emfstore.internal.server.model.ServerSpace;
import org.eclipse.emf.emfstore.server.auth.ESAuthenticationControlType;
import org.eclipse.emf.emfstore.server.auth.ESAuthorizationService;
import org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;

/**
 * Access control class holding references to the customizable access control related services.
 *
 * @author emueller
 *
 */
public class AccessControl {

	private static final String ACCESSCONTROL_EXTENSION_ID = "org.eclipse.emf.emfstore.server.accessControl"; //$NON-NLS-1$

	private final ESOrgUnitProvider orgUnitProvider;

	private final ESAuthorizationService authorizationService;
	private final ESOrgUnitResolver orgUnitResolver;

	private final Sessions sessions;

	private final LoginService loginService;

	private final ServerSpace serverSpace;

	private ESAuthenticationControlType authenticationControlType;

	public AccessControl(ServerSpace serverSpace) {
		this.serverSpace = serverSpace;
		sessions = new Sessions();

		orgUnitProvider = initOrgUnitProviderService();
		orgUnitResolver = initOrgUnitResolverService();
		authorizationService = initAuthorizationService();
		loginService = initLoginService();
	}

	public AccessControl(
		ESAuthenticationControlType authenticationControlType,
		ServerSpace serverSpace) {

		this.authenticationControlType = authenticationControlType;
		this.serverSpace = serverSpace;
		sessions = new Sessions();

		orgUnitProvider = initOrgUnitProviderService();
		orgUnitResolver = initOrgUnitResolverService();
		authorizationService = initAuthorizationService();
		loginService = initLoginService();
	}

	/**
	 * @return
	 */
	private LoginService initLoginService() {

		if (authenticationControlType == null) {
			final String[] splittedProperty = ServerConfiguration
				.getSplittedProperty(ServerConfiguration.AUTHENTICATION_POLICY);
			authenticationControlType = ESAuthenticationControlType.valueOf(splittedProperty[0]);
		}
		return new LoginService(
			authenticationControlType,
			sessions,
			orgUnitProvider,
			getOrgUnitResolverServive());
	}

	/**
	 * @return
	 */
	private ESAuthorizationService initAuthorizationService() {
		ESAuthorizationService authorizationService;
		try {
			final int size = new ESExtensionPoint(ACCESSCONTROL_EXTENSION_ID, true).size();
			if (size == 1) {
				authorizationService = new ESExtensionPoint(ACCESSCONTROL_EXTENSION_ID, true)
					.getClass("authorizationServiceClass", ESAuthorizationService.class); //$NON-NLS-1$
			} else if (size > 1) {
				throw new RuntimeException("Multiple extensions for "
					+ "org.eclipse.emf.emfstore.server.accessControl.authorizationServiceClass discovered."
					+ "Only one allowed.");
			} else {
				authorizationService = new DefaultESAuthorizationService();
			}
		} catch (final ESExtensionPointException e) {
			final String message = "Custom authorization class not be initialized";
			ModelUtil.logException(message, e);
			authorizationService = new DefaultESAuthorizationService();
		}

		authorizationService.init(
			sessions,
			getOrgUnitResolverServive(),
			orgUnitProvider);

		return authorizationService;
	}

	private ESOrgUnitResolver initOrgUnitResolverService() {
		ESOrgUnitResolver resolver;
		try {
			final int size = new ESExtensionPoint(ACCESSCONTROL_EXTENSION_ID, true).size();
			if (size == 1) {
				resolver = new ESExtensionPoint(ACCESSCONTROL_EXTENSION_ID, true).getClass(
					"orgUnitResolverServiceClass", ESOrgUnitResolver.class); //$NON-NLS-1$
			} else if (size > 1) {
				throw new RuntimeException("Multiple extensions for "
					+ "org.eclipse.emf.emfstore.server.accessControl.orgUnitResolverServiceClass discovered."
					+ "Only one allowed.");
			} else {
				resolver = new DefaultESOrgUnitResolverService();
			}
		} catch (final ESExtensionPointException e) {
			final String message = "Custom org unit resolver class not be initialized";
			ModelUtil.logException(message, e);
			resolver = new DefaultESOrgUnitResolverService();
		}

		resolver.init(orgUnitProvider);
		return resolver;
	}

	private ESOrgUnitProvider initOrgUnitProviderService() {
		ESOrgUnitProvider orgUnitProvider;
		try {
			final int size = new ESExtensionPoint(ACCESSCONTROL_EXTENSION_ID, true).size();
			if (size == 1) {
				orgUnitProvider = new ESExtensionPoint(ACCESSCONTROL_EXTENSION_ID, true).getClass(
					"orgUnitProviderClass", ESOrgUnitProvider.class); //$NON-NLS-1$
			} else if (size > 1) {
				throw new RuntimeException("Multiple extensions for "
					+ "org.eclipse.emf.emfstore.server.accessControl.orgUnitProviderClass discovered."
					+ "Only one allowed.");
			} else {
				orgUnitProvider = new ESOrgUnitProviderImpl();
			}
		} catch (final ESExtensionPointException e) {
			final String message = "Custom org unit provider class not be initialized";
			ModelUtil.logException(message, e);
			orgUnitProvider = new ESOrgUnitProviderImpl();
		}

		orgUnitProvider.init(serverSpace);
		return orgUnitProvider;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl#getOrgUnitResolverServive()
	 */
	public ESOrgUnitResolver getOrgUnitResolverServive() {
		return orgUnitResolver;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl#getAuthorizationService()
	 */
	public ESAuthorizationService getAuthorizationService() {
		return authorizationService;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl#getSessions()
	 */
	public Sessions getSessions() {
		return sessions;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl#getLoginService()
	 */
	public LoginService getLoginService() {
		return loginService;
	}

}
