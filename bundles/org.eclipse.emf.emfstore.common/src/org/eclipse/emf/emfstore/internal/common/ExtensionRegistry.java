/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;

/**
 * The extension registry may be used as simple replacement for the extension
 * point mechanism in case one does not want to expose internal types.
 *
 * It is capable of retrieving actual extension point information, but currently
 * does not support contributing such.
 *
 * @author emueller
 */
public final class ExtensionRegistry {

	/**
	 * The singleton instance.
	 */
	public static final ExtensionRegistry INSTANCE = new ExtensionRegistry();

	private final Map<String, ESConfigElement> configElements;

	private ExtensionRegistry() {
		configElements = new HashMap<String, ExtensionRegistry.ESConfigElement>();
	}

	/**
	 * Returns the requested extension point.
	 *
	 * @param id
	 *            the ID of the extension point to be returned
	 * @param clazz
	 *            the expected type
	 * @param defaultInstance
	 *            a default instance to be returned in case no extension point has been found
	 * @param shouldSetDefault
	 *            whether the default instance should be registered
	 * @return the requested extension which might be the default instance specified
	 *
	 * @param <T> the expected type
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String id, Class<T> clazz, T defaultInstance, boolean shouldSetDefault) {

		final T extensionPointInstnace = getExtensionElement(id, clazz);

		if (extensionPointInstnace != null) {
			return extensionPointInstnace;
		}

		final ESConfigElement configElement = configElements.get(id);
		T t;

		if (configElement != null) {
			t = (T) configElement.get();
		} else {
			t = defaultInstance;
			if (shouldSetDefault) {
				set(id, t);
			}
		}

		return t;
	}

	/**
	 * Returns the requested extension point.
	 *
	 * @param id
	 *            the ID of the extension point to be returned
	 * @param clazz
	 *            the expected type
	 * @return the requested extension which might be the default instance specified
	 *
	 * @param <T> the expected type
	 */
	public <T> T get(String id, Class<T> clazz) {
		return get(id, clazz, null, false);
	}

	/**
	 * Set the extension.
	 *
	 * @param id
	 *            the ID of the extension point to be returned
	 * @param t
	 *            the extension to be set
	 *
	 * @param <T> the type of the extension to be set
	 */
	public <T> void set(String id, T t) {
		// TODO: if already present?
		configElements.put(id, new ESConfigElement(t));
	}

	private <T> T getExtensionElement(String id, Class<T> t) {

		final int idx = id.lastIndexOf('.');
		final String extensionPointId = id.substring(0, idx);
		final String attributeName = id.substring(idx + 1, id.length());

		final ESExtensionPoint extensionPoint = new ESExtensionPoint(extensionPointId);

		if (extensionPoint.getFirst() == null) {
			return null;
		}

		return extensionPoint.getFirst().getClass(attributeName, t);
	}

	/**
	 * Simple wrapper around an object to provide
	 * a setter and a getter.
	 *
	 * Might be extended with additional information in the future.
	 *
	 * @author emueller
	 *
	 */
	class ESConfigElement {

		private Object t;

		/**
		 * Constructor.
		 *
		 * @param o
		 *            the actual extension to be wrapped
		 */
		public ESConfigElement(Object o) {
			t = o;
		}

		/**
		 * Returns the wrapped extension.
		 *
		 * @return the extension
		 */
		public Object get() {
			return t;
		}

		/**
		 * Set the extension.
		 *
		 * @param t
		 *            the extension to be set
		 */
		public void set(Object t) {
			this.t = t;
		}
	}

	/**
	 * Remove the extension with the given ID.
	 *
	 * @param id
	 *            the ID of the extension to be removed
	 */
	public void remove(String id) {
		configElements.remove(id);
	}
}
