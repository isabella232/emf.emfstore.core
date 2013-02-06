package org.eclipse.emf.emfstore.client.api;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.client.model.ServerInfo;
import org.eclipse.emf.emfstore.client.model.Usersession;
import org.eclipse.emf.emfstore.server.exceptions.EmfStoreException;
import org.eclipse.emf.emfstore.server.model.ProjectInfo;

public interface IServer {

	String getName();

	void setName(String name);

	int getPort();

	void setPort(int port);

	String getUrl();

	void setUrl(String url);

	String getCertificateAlias();

	void setCertificateAlias(String alias);

	List<? extends IRemoteProject> getRemoteProjects() throws EmfStoreException;

	IUsersession getLastUsersession();

	/**
	 * Creates an empty project on the server.
	 * 
	 * @param serverInfo
	 *            The {@link ServerInfo} that contains information about the
	 *            server on which the project should be created.
	 * @param projectName
	 *            The name of the project.
	 * @param projectDescription
	 *            A description of the project to be created.
	 * @param monitor
	 *            a progress monitor instance that is used to indicate progress
	 *            about creating the remote project
	 * @return a {@link ProjectInfo} object containing information about the
	 *         created project
	 * @throws EmfStoreException
	 *             If an error occurs while creating the remote project
	 */
	IRemoteProject createRemoteProject(final String projectName, final String projectDescription,
		final IProgressMonitor monitor) throws EmfStoreException;

	/**
	 * Creates an empty project on the server.
	 * 
	 * @param usersession
	 *            The {@link Usersession} that should be used to create the
	 *            remote project.<br/>
	 *            If <code>null</code>, the session manager will search for a
	 *            session.
	 * @param projectName
	 *            The name of the project.
	 * @param projectDescription
	 *            A description of the project to be created.
	 * @param monitor a monitor to show the progress
	 * @return a {@link ProjectInfo} object containing information about the
	 *         created project
	 * @throws EmfStoreException
	 *             If an error occurs while creating the remote project
	 */
	IRemoteProject createRemoteProject(IUsersession usersession, final String projectName,
		final String projectDescription, final IProgressMonitor monitor) throws EmfStoreException;
}