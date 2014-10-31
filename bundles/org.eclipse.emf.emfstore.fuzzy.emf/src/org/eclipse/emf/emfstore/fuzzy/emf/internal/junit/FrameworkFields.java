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
package org.eclipse.emf.emfstore.fuzzy.emf.internal.junit;

import org.junit.runners.model.FrameworkField;

/**
 * Simple utility class to ease passing of framework fields.
 * 
 * @author emueller
 * 
 */
public final class FrameworkFields {

	private FrameworkField dataField;
	private FrameworkField utilField;
	private FrameworkField mutatorField;
	private FrameworkField optionsField;

	/**
	 * Private constructor.
	 */
	private FrameworkFields() {
		dataField = null;
		utilField = null;
		mutatorField = null;
		optionsField = null;
	}

	/**
	 * Creates a fresh instance.
	 * 
	 * @return a fresh uninitialized {@link FrameworkFields} instance.
	 */
	public static FrameworkFields create() {
		return new FrameworkFields();
	}

	/**
	 * @return the optionsField
	 */
	public FrameworkField getOptionsField() {
		return optionsField;
	}

	/**
	 * @return the mutatorField
	 */
	public FrameworkField getMutatorField() {
		return mutatorField;
	}

	/**
	 * @return the utilField
	 */
	public FrameworkField getUtilField() {
		return utilField;
	}

	/**
	 * @return the dataField
	 */
	public FrameworkField getDataField() {
		return dataField;
	}

	/**
	 * Sets the data field.
	 * 
	 * @param dataField
	 *            the data field to be set
	 * @return this {@link FrameworkFields} instance
	 */
	public FrameworkFields setDataField(FrameworkField dataField) {
		this.dataField = dataField;
		return this;
	}

	/**
	 * Sets the options field.
	 * 
	 * @param optionsField
	 *            the options field to be set
	 * @return this {@link FrameworkFields} instance
	 */
	public FrameworkFields setOptionsField(FrameworkField optionsField) {
		this.optionsField = optionsField;
		return this;
	}

	/**
	 * Sets the mutator field.
	 * 
	 * @param mutatorField
	 *            the mutator field to be set
	 * @return this {@link FrameworkFields} instance
	 */
	public FrameworkFields setMutatorField(FrameworkField mutatorField) {
		this.mutatorField = mutatorField;
		return this;
	}

	/**
	 * Sets the util field.
	 * 
	 * @param utilField
	 *            the util field to be set
	 * @return this {@link FrameworkFields} instance
	 */
	public FrameworkFields setUtilField(FrameworkField utilField) {
		this.utilField = utilField;
		return this;
	}
}
