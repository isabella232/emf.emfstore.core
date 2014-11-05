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
package org.eclipse.emf.emfstore.client.test.common.util;

import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

public class Times {

	private ESLocalProject localProject;

	public Times(ESLocalProject localProject) {
		this.localProject = localProject;

	}

	public ESLocalProject times(int n) throws ESException {
		for (int i = 0; i < n; i++) {
			localProject = ProjectUtil.commit(ProjectUtil.addElement(localProject, Create.testElement()));
		}
		return localProject;
	}

}