/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.diff.test;

import org.eclipse.emf.emfstore.client.test.common.util.FilteredSuite;
import org.eclipse.emf.emfstore.client.test.common.util.FilteredSuite.FilteredSuiteParameter;
import org.eclipse.emf.emfstore.fuzzy.emf.internal.diff.CreateAllDiffs;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Edgar
 * 
 */
@RunWith(FilteredSuite.class)
@FilteredSuiteParameter({ "createDiffs" })
@SuiteClasses({ CreateAllDiffs.class })
public class CreateAllDiffsSuite {

}
