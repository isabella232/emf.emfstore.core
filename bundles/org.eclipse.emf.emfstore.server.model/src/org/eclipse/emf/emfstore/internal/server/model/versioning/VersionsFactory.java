/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning;

import org.eclipse.emf.emfstore.server.model.versionspec.ESVersionSpec;

/**
 * Factory for creating {@link VersionSpec VersionSpecs}.
 * 
 */
public class VersionsFactory {

	/**
	 * The instance.
	 */
	public static final VersionsFactory INSTANCE = new VersionsFactory();

	private VersionsFactory() {
	}

	/**
	 * Creates a HEAD revision for the branch "trunk".
	 * 
	 * @return head version
	 */
	public HeadVersionSpec createHEAD() {
		return Versions.createHEAD();
	}

	/**
	 * Creates a {@link HeadVersionSpec}.
	 * 
	 * @param branch name of the branch
	 * @return the head version spec
	 */
	public HeadVersionSpec createHEAD(String branch) {
		return Versions.createHEAD(branch);
	}

	/**
	 * Creates a {@link HeadVersionSpec}.
	 * 
	 * @param versionSpec which is used for resolving branch
	 * @return the head version spec
	 */
	public HeadVersionSpec createHEAD(ESVersionSpec versionSpec) {
		return Versions.createHEAD((VersionSpec) versionSpec);
	}

	/**
	 * Create {@link BranchVersionSpec}.
	 * 
	 * @param value name of the branch
	 * @return the branch version spec
	 */
	public BranchVersionSpec createBRANCH(String value) {
		return Versions.createBRANCH(value);
	}

	/**
	 * Creates a {@link BranchVersionSpec}.
	 * 
	 * @param spec versionSpec which is used for resolving branch
	 * @return the branch version spec
	 */
	public BranchVersionSpec createBRANCH(ESVersionSpec spec) {
		return Versions.createBRANCH((VersionSpec) spec);
	}

	/**
	 * Checks whether two versions spec target the same branch.
	 * 
	 * @param spec1 the first version spec
	 * @param spec2 the second version spec
	 * @return <code>true</code> if same branch, <code>false</code> otherwise
	 */
	public boolean isSameBranch(VersionSpec spec1, VersionSpec spec2) {
		return Versions.isSameBranch(spec1, spec2);
	}

	/**
	 * Creates a {@link TagVersionSpec}.
	 * 
	 * @param tag the tag to use
	 * @param branch the branch
	 * @return the tag version spec
	 */
	public TagVersionSpec createTAG(String tag, String branch) {
		return Versions.createTAG(tag, branch);
	}

}
