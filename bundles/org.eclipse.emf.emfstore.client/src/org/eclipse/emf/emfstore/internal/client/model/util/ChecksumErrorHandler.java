/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.handler.ESChecksumErrorHandler;
import org.eclipse.emf.emfstore.internal.client.common.UnknownEMFStoreWorkloadCommand;
import org.eclipse.emf.emfstore.internal.client.model.Activator;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESGlobalProjectIdImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESSessionIdImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.versionspec.ESVersionSpecImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;

/**
 * Pre-defined error handlers.
 *
 * @author emueller
 * @author Lucas Koehler
 *
 */
public enum ChecksumErrorHandler implements ESChecksumErrorHandler {

	/**
	 * Logs the checksum comparison failure and continues execution of the caller.
	 */
	LOG {
		/**
		 * {@inheritDoc}
		 */
		public boolean execute(ESLocalProject project, ESPrimaryVersionSpec versionSpec, IProgressMonitor monitor)
			throws ESException {
			WorkspaceUtil.logWarning(Messages.ChecksumErrorHandler_ChecksumComparisionFailed, null);
			return true;
		}
	},

	/**
	 * Logs the checksum comparison failure detailed: the serialization of both project spaces is written in seperate
	 * files. The execution of the caller will then be continued.
	 */
	LOG_DETAILED {
		/**
		 * {@inheritDoc}
		 */
		public boolean execute(ESLocalProject localProject, ESPrimaryVersionSpec versionSpec, IProgressMonitor monitor)
			throws ESException {
			return LogDetailed.execute(localProject, versionSpec, monitor, false);
		}
	},

	/**
	 * Logs the checksum comparison failure detailed: the serialization of both project spaces is written in seperate
	 * files. The execution of the caller will then be discontinued.
	 */
	LOG_DETAILED_AND_CANCEL

	{

		/**
		 * {@inheritDoc}
		 */
		public boolean execute(ESLocalProject localProject, ESPrimaryVersionSpec versionSpec, IProgressMonitor monitor)
			throws ESException {
			return LogDetailed.execute(localProject, versionSpec, monitor, true);
		}
	},

	/**
	 * Aborts execution of the caller.
	 */
	CANCEL {
		/**
		 * {@inheritDoc}
		 */
		public boolean execute(ESLocalProject project, ESPrimaryVersionSpec versionSpec, IProgressMonitor monitor)
			throws ESException {
			return false;
		}
	},

	/**
	 * Fixes the checksum comparison failure by deleting the {@link ProjectSpace} that got
	 * in an inconsistent state and checking it out again.<br>
	 * <b>Note</b>: all references to the project space that will be deleted should to be taken care of.
	 */
	AUTOCORRECT {
		/**
		 * {@inheritDoc}
		 */
		public boolean execute(final ESLocalProject project, final ESPrimaryVersionSpec versionSpec,
			IProgressMonitor monitor) throws ESException {

			final ESLocalProjectImpl localProjectImpl = (ESLocalProjectImpl) project;
			final ProjectSpace projectSpace = localProjectImpl.toInternalAPI();
			final Resource projectResource = localProjectImpl.toInternalAPI().getProject().eResource();

			final Project fetchedProject = new UnknownEMFStoreWorkloadCommand<Project>(monitor) {
				@Override
				public Project run(IProgressMonitor monitor) throws ESException {

					final ESSessionIdImpl sessionIdImpl = (ESSessionIdImpl) project.getUsersession().getSessionId();
					final ESGlobalProjectIdImpl globalProjectIdImpl = (ESGlobalProjectIdImpl) project.getRemoteProject()
						.getGlobalProjectId();
					final ESVersionSpecImpl<?, ? extends VersionSpec> versionSpecImpl = (ESVersionSpecImpl<?, ?>) versionSpec;

					return ESWorkspaceProviderImpl.getInstance().getConnectionManager().getProject(
						sessionIdImpl.toInternalAPI(), globalProjectIdImpl.toInternalAPI(),
						ModelUtil.clone(versionSpecImpl.toInternalAPI()));
				}
			}.execute();

			if (fetchedProject == null) {
				throw new ESException(Messages.ChecksumErrorHandler_ServerReturnedNullProject);
			}

			projectResource.getContents().clear();
			projectResource.getContents().add(fetchedProject);
			try {
				projectResource.save(ModelUtil.getResourceSaveOptions());
			} catch (final IOException ex) {
				throw new ESException(Messages.ChecksumErrorHandler_SaveFailedWhileAutocorrect);
			}
			projectSpace.setProject(fetchedProject);
			projectSpace.init();

			return true;
		}
	}
}

/**
 * Logs the checksum comparison failure detailed: the serialization of both project spaces is written in separate
 * files.
 */
final class LogDetailed {

	private LogDetailed() {
		// private ctor
	}

	/**
	 * Execute the detailed comparison.
	 *
	 * @param localProject the {@link ESLocalProject} whose contents should be checked
	 * @param versionSpec the version for which the comparison should be checked
	 * @param monitor a {@link IProgressMonitor} instance
	 * @param shouldFail whether to continue with in case the comparison failed
	 * @return the inverse of {@code shouldFail} meaning that we indicate an error occurred
	 *         in case the comparison failed
	 *
	 * @throws ESException in case the comparison fails for technical reasons
	 */
	public static boolean execute(ESLocalProject localProject, ESPrimaryVersionSpec versionSpec,
		IProgressMonitor monitor, boolean shouldFail) throws ESException {

		WorkspaceUtil.logWarning("Checksum comparison failed.", null); //$NON-NLS-1$

		final Project project = ESLocalProjectImpl.class.cast(localProject).toInternalAPI().getProject();
		final ESLocalProject serverESProject = localProject
			.getRemoteProject()
			.checkout(
				"log_error_checksum_debug_checkout", localProject.getUsersession(), versionSpec, //$NON-NLS-1$
				new NullProgressMonitor());
		final Project serverProject = ESLocalProjectImpl.class.cast(serverESProject).toInternalAPI()
			.getProject();
		try {

			final Map<Object, Object> formatOptions = new LinkedHashMap<Object, Object>();
			formatOptions.put(XMLResource.OPTION_DECLARE_XML, Boolean.TRUE);
			formatOptions.put(XMLResource.OPTION_FORMATTED, Boolean.TRUE);

			final String localSerialization = ModelUtil
				.eObjectToString(project, ModelUtil.getResourceSaveOptions());
			final String serverSerialization = ModelUtil.eObjectToString(serverProject,
				ModelUtil.getResourceSaveOptions());

			serverESProject.delete(new NullProgressMonitor());

			final File localFile = Activator.getDefault().getBundle().getDataFile("localProjectSerialization.txt"); //$NON-NLS-1$
			final File serverFile = Activator.getDefault().getBundle()
				.getDataFile("serverProjectSerialization.txt"); //$NON-NLS-1$

			final FileWriter fileWriterLocal = new FileWriter(localFile);
			fileWriterLocal.write(localSerialization);
			fileWriterLocal.close();
			final FileWriter fileWriterServer = new FileWriter(serverFile);
			fileWriterServer.write(serverSerialization);
			fileWriterServer.close();
		} catch (final SerializationException ex) {
			WorkspaceUtil.logException("Couldn't log the serializations.", ex); //$NON-NLS-1$
		} catch (final IOException ex) {
			WorkspaceUtil.logException("Couldn't save the serializations.", ex); //$NON-NLS-1$
		}
		return !shouldFail;
	}
}