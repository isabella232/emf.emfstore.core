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
package org.eclipse.emf.emfstore.internal.common;

import java.util.List;

import com.google.common.base.Optional;

/**
 * Collections related utility for internal use.
 *
 * @author emueller
 *
 */
public final class ESCollections {

	private ESCollections() {
		// private ctor
	}

	/**
	 * Finds the first occurrence of a value that matches the given type within the given object array.
	 *
	 * @param array
	 *            the array to be searched
	 * @param clazz
	 *            the class type to be matched
	 * @return an {@link Optional} containing the matched value
	 *
	 * @param <T> the type to be matched
	 */
	@SuppressWarnings("unchecked")
	public static <T> Optional<T> find(Object[] array, Class<T> clazz) {
		for (final Object object : array) {
			if (clazz.isInstance(object)) {
				return (Optional<T>) Optional.of(object);
			}
		}

		return Optional.absent();
	}

	/**
	 * Finds the first occurrence of a value that matches the given type within the given list.
	 *
	 * @param list
	 *            the list to be searched
	 * @param clazz
	 *            the class type to be matched
	 * @return an {@link Optional} containing the matched value
	 *
	 * @param <T> the type to be matched
	 */
	@SuppressWarnings("unchecked")
	public static <T> Optional<T> find(List<?> list, Class<T> clazz) {
		for (final Object object : list) {
			if (clazz.isInstance(object)) {
				return (Optional<T>) Optional.of(object);
			}
		}

		return Optional.absent();
	}
}
