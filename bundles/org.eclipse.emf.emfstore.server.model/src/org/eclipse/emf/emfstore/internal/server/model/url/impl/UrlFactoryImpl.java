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
package org.eclipse.emf.emfstore.internal.server.model.url.impl;

import java.net.MalformedURLException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.server.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.url.ModelElementUrl;
import org.eclipse.emf.emfstore.internal.server.model.url.ModelElementUrlFragment;
import org.eclipse.emf.emfstore.internal.server.model.url.ProjectUrlFragment;
import org.eclipse.emf.emfstore.internal.server.model.url.ServerUrl;
import org.eclipse.emf.emfstore.internal.server.model.url.UrlFactory;
import org.eclipse.emf.emfstore.internal.server.model.url.UrlPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!--
 * end-user-doc -->
 *
 * @generated
 */
public class UrlFactoryImpl extends EFactoryImpl implements UrlFactory {

	/**
	 * The prefix for all EMFStore URLs.
	 *
	 * @generated NOT
	 */
	public static final String PREFIX = "emfstore://"; //$NON-NLS-1$

	/**
	 * The standard parsing exception message.
	 *
	 * @generated NOT
	 */
	private static final String EXCEPTION_MESSAGE = "Invalid EMFStore URL!"; //$NON-NLS-1$

	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	public static UrlFactory init() {
		try {
			final UrlFactory theUrlFactory = (UrlFactory) EPackage.Registry.INSTANCE.getEFactory(UrlPackage.eNS_URI);
			if (theUrlFactory != null) {
				return theUrlFactory;
			}
		} catch (final Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new UrlFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	public UrlFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case UrlPackage.SERVER_URL:
			return createServerUrl();
		case UrlPackage.PROJECT_URL_FRAGMENT:
			return createProjectUrlFragment();
		case UrlPackage.MODEL_ELEMENT_URL_FRAGMENT:
			return createModelElementUrlFragment();
		case UrlPackage.MODEL_ELEMENT_URL:
			return createModelElementUrl();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ServerUrl createServerUrl() {
		final ServerUrlImpl serverUrl = new ServerUrlImpl();
		return serverUrl;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ProjectUrlFragment createProjectUrlFragment() {
		final ProjectUrlFragmentImpl projectUrlFragment = new ProjectUrlFragmentImpl();
		return projectUrlFragment;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ModelElementUrlFragment createModelElementUrlFragment() {
		final ModelElementUrlFragmentImpl modelElementUrlFragment = new ModelElementUrlFragmentImpl();
		return modelElementUrlFragment;
	}

	// begin of custom code

	/**
	 * {@inheritDoc}
	 */
	public ModelElementUrl createModelElementUrl(String url) throws MalformedURLException {

		final ModelElementUrl modelURL = createModelElementUrl();

		final ModelElementUrlFragment modelFragment = createModelElementUrlFragment();
		final ProjectUrlFragment projectFragment = createProjectUrlFragment();
		final ServerUrl serverFragment = createServerUrl();
		modelURL.setModelElementUrlFragment(modelFragment);
		modelURL.setProjectUrlFragment(projectFragment);
		modelURL.setServerUrl(serverFragment);
		if (url.startsWith(PREFIX)) {
			int trail = 0;
			if (url.endsWith("/")) { //$NON-NLS-1$
				trail = 1;
			}
			final String text = url.substring(PREFIX.length(), url.length() - trail);
			final String[] elements = text.split("/"); //$NON-NLS-1$
			if (elements.length >= 3) {
				final String[] server = elements[0].split(":"); //$NON-NLS-1$
				if (server.length != 2) {
					throw new MalformedURLException(EXCEPTION_MESSAGE);
				}
				serverFragment.setHostName(server[0]);
				try {
					serverFragment.setPort(Integer.parseInt(server[1]));
				} catch (final NumberFormatException e) {
					throw new MalformedURLException(EXCEPTION_MESSAGE);
				}

				final String[] project = elements[1].split("%"); //$NON-NLS-1$
				if (project.length != 2) {
					throw new MalformedURLException(EXCEPTION_MESSAGE);
				}
				projectFragment.setName(project[0]);
				final ProjectId projectId = ModelFactory.eINSTANCE.createProjectId();
				projectId.setId(project[1]);
				projectFragment.setProjectId(projectId);

				final StringBuilder model = new StringBuilder();
				for (int i = 2; i < elements.length; i++) {
					model.append(elements[i]);
				}
				final String string = model.toString();
				final int p = string.lastIndexOf("%"); //$NON-NLS-1$
				if (p == -1) {
					throw new MalformedURLException(EXCEPTION_MESSAGE);
				}
				modelFragment.setName(string.substring(0, p));
				final ModelElementId modelElementId = org.eclipse.emf.emfstore.internal.common.model.ModelFactory.eINSTANCE
					.createModelElementId();
				modelElementId.setId(string.substring(p + 1));
				modelFragment.setModelElementId(modelElementId);
			}
		}
		return modelURL;
	}

	// end of custom code

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ModelElementUrl createModelElementUrl() {
		final ModelElementUrlImpl modelElementUrl = new ModelElementUrlImpl();
		return modelElementUrl;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public UrlPackage getUrlPackage() {
		return (UrlPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static UrlPackage getPackage() {
		return UrlPackage.eINSTANCE;
	}

} // UrlFactoryImpl