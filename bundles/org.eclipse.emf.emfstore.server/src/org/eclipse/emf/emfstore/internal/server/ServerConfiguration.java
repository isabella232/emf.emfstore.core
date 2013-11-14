/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Otto von Wesendonk, Maximilian Koegel - initial API and implementation
 * Johannes Faltermeier - adaptions for independent storage
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPointException;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.AuthenticationControlType;
import org.eclipse.emf.emfstore.internal.server.startup.PostStartupListener;
import org.eclipse.emf.emfstore.internal.server.startup.StartupListener;
import org.eclipse.emf.emfstore.server.ESLocationProvider;
import org.osgi.framework.Bundle;

/**
 * Represents the current server configuration.
 * 
 * @author koegel
 * @author wesendon
 * @author jfaltermeier
 */
public final class ServerConfiguration {

	/**
	 * Constant for boolean true string.
	 */
	public static final String TRUE = "true";

	/**
	 * Constant for boolean false string.
	 */
	public static final String FALSE = "false";

	/**
	 * Constant for the name of the Resource Storage Property.
	 */
	public static final String RESOURCE_STORAGE = "emfstore.persistence.resourceStorage";

	/**
	 * Constant for the Default Resource Storage.
	 */
	// TODO: OTS
	public static final String RESOURCE_STORAGE_DEFAULT = "org.eclipse.emf.emfstore.internal.server.storage.XMLStorage";

	/**
	 * RMI encryption property, possible values are true and false.
	 */
	public static final String RMI_ENCRYPTION = "emfstore.connection.rmi.encryption";

	/**
	 * Default RMI encryption property value.
	 */
	public static final String RMI_ENCRYPTION_DEFAULT = "true";

	/**
	 * Option for defining port of XML RPC.
	 */
	public static final String XML_RPC_PORT = "emfstore.connection.xmlrpc.port";

	/**
	 * Default port for XML RPC.
	 */
	public static final String XML_RPC_PORT_DEFAULT = "8080";

	/**
	 * Default name of server keystore file.
	 */
	public static final String SERVER_KEYSTORE_FILE = "emfstoreServer.keystore";

	/**
	 * Password of keystore, in which the certificate for rmi encryption and
	 * password decryption is saved.
	 * 
	 * @see #KEYSTORE_ALIAS
	 */
	public static final String KEYSTORE_PASSWORD = "emfstore.keystore.password";

	/**
	 * Default keystore password.
	 */
	public static final String KEYSTORE_PASSWORD_DEFAULT = "123456"; // av374tb$VBGGtrgwa7tosdfa";

	/**
	 * Alias for certificate in keystore.
	 * 
	 * @see #KEYSTORE_PASSWORD
	 */
	public static final String KEYSTORE_ALIAS = "emfstore.keystore.alias";

	/**
	 * Default alias, intentioned for developers.
	 */
	public static final String KEYSTORE_ALIAS_DEFAULT = "testkeygeneratedbyotto";

	/**
	 * Type of server certificate used for encryption.
	 */
	public static final String KEYSTORE_CERTIFICATE_TYPE = "emfstore.keystore.certificate.type";

	/**
	 * Default certificate.
	 */
	public static final String KEYSTORE_CERTIFICATE_TYPE_DEFAULT = "SunX509";

	/**
	 * Type of cipher algorithm used for encryption.
	 */
	public static final String KEYSTORE_CIPHER_ALGORITHM = "emfstore.keystore.cipher.algorithm";

	/**
	 * Default cipher algorithm.
	 */
	public static final String KEYSTORE_CIPHER_ALGORITHM_DEFAULT = "RSA";

	/**
	 * Property for projectstate persistence policy in versions. Possible values
	 * are <b>lastVersionOnly</b> and <b>everyVersion</b>. If you don't have
	 * every project state the server has to recalulate certain revisions if
	 * requested. On the other side saving every project state is quite
	 * redundant.
	 */
	public static final String PROJECTSTATE_VERSION_PERSISTENCE = "emfstore.persistence.version.projectstate";

	/**
	 * Only the project state from the first and last version is stored, the
	 * other states are calculated by the changes.
	 */
	public static final String PROJECTSTATE_VERSION_PERSISTENCE_FIRSTANDLASTVERSIONONLY = "firstAndLastVersionOnly";

	/**
	 * The projectstate of every x versions will be stored. This is used to save
	 * memory. Use x=1 to save every version.
	 */
	public static final String PROJECTSTATE_VERSION_PERSISTENCE_EVERYXVERSIONS = "everyXVersion";

	/**
	 * Property for the count of versions, needed by the everyXVersion policy.
	 */
	public static final String PROJECTSTATE_VERSION_PERSISTENCE_EVERYXVERSIONS_X = "emfstore.persistence.version.projectstate.everyxversions";

	/**
	 * Default value for the everyXVersion policy.
	 */
	public static final String PROJECTSTATE_VERSION_PERSISTENCE_EVERYXVERSIONS_X_DEFAULT = "1";

	/**
	 * Default value for projectstate persistence policy in versions.
	 */
	public static final String PROJECTSPACE_VERSION_PERSISTENCE_DEFAULT = PROJECTSTATE_VERSION_PERSISTENCE_EVERYXVERSIONS;

	/**
	 * Property for timeout time of a user session.
	 */
	public static final String SESSION_TIMEOUT = "emfstore.accesscontrol.session.timeout";

	/**
	 * Default timeout (= 30 minutes).
	 */
	public static final String SESSION_TIMEOUT_DEFAULT = "1800000";

	/**
	 * Property for the super user.
	 */
	public static final String SUPER_USER = "emfstore.accesscontrol.authentication.superuser";

	/**
	 * Default super user name.
	 */
	public static final String SUPER_USER_DEFAULT = "super";

	/**
	 * Property for the super user's password.
	 */
	public static final String SUPER_USER_PASSWORD = "emfstore.accesscontrol.authentication.superuser.password";

	/**
	 * Default super user password.
	 */
	public static final String SUPER_USER_PASSWORD_DEFAULT = "super";

	/**
	 * Property for authentication policy used by server. E.g. ldap or property
	 * file.
	 */
	public static final String AUTHENTICATION_POLICY = "emfstore.accesscontrol.authentication.policy";

	/**
	 * Beginning tag of every LDAP property.
	 */
	public static final String AUTHENTICATION_LDAP_PREFIX = "emfstore.accesscontrol.authentication.ldap";

	/**
	 * Ldap url.
	 */
	public static final String AUTHENTICATION_LDAP_URL = "url";

	/**
	 * LDAP user authentication key.
	 */
	public static final String AUTHENTICATION_LDAP_AUTHUSER = "authuser";

	/**
	 * LDAP password authentication key.
	 */
	public static final String AUTHENTICATION_LDAP_AUTHPASS = "authpass";

	/**
	 * Ldap base.
	 */
	public static final String AUTHENTICATION_LDAP_BASE = "base";

	/**
	 * Searchdn for ldap.
	 */
	public static final String AUTHENTICATION_LDAP_SEARCHDN = "searchdn";

	/**
	 * Default authentication policy is simple property file aut.
	 */
	public static final AuthenticationControlType AUTHENTICATION_POLICY_DEFAULT = AuthenticationControlType.spfv;

	/**
	 * Path to property file for spfv authentication.
	 */
	public static final String AUTHENTICATION_SPFV_FILEPATH = "emfstore.accesscontrol.authentication.spfv";

	/**
	 * Property to validate server on start up.
	 */
	public static final String VALIDATION_PROJECT_EXCLUDE_DEFAULT = "";

	/**
	 * Property for loading startup listeners from extension point.
	 */
	public static final String LOAD_STARTUP_LISTENER = "emfstore.startup.loadlistener";

	/**
	 * Property for loading post startup listeners from extension point.
	 */
	public static final String LOAD_POST_STARTUP_LISTENER = "emfstore.startup.post.loadlistener";

	/**
	 * Default value for {@link #LOAD_STARTUP_LISTENER}.
	 */
	public static final String LOAD_STARTUP_LISTENER_DEFAULT = TRUE;

	/**
	 * Property name of accepted client versions. Enter the version's names or
	 * any, seperate multiple entries with {@link #MULTI_PROPERTY_SEPERATOR}.
	 */
	public static final String ACCEPTED_VERSIONS = "emfstore.acceptedversions";

	/**
	 * Allow any client version.
	 */
	public static final String ACCEPTED_VERSIONS_ANY = "any";

	/**
	 * Seperator for multiple properties. E.g. acceptedversions = 0.1,0.2
	 */
	public static final String MULTI_PROPERTY_SEPERATOR = ",";

	/**
	 * Prefix for EMFStore Home Startup Argument.
	 */
	public static final String EMFSTORE_HOME = "-EMFStoreHome";

	/**
	 * Whether user names should be matched case insensitively.
	 */
	public static final String AUTHENTICATION_MATCH_USERS_IGNORE_CASE = "emfstore.accesscontrol.authentication.matchusers.ignorecase";

	private static final List<PostStartupListener> POST_STARTUP_LISTENERS = new ArrayList<PostStartupListener>();
	private static final List<StartupListener> STARTUP_LISTENERS = new ArrayList<StartupListener>();

	private static boolean testing;

	private static Properties properties;

	private ServerConfiguration() {
		// nothing to do
	}

	/**
	 * Return the configuration directory location.
	 * 
	 * @return the dir path string
	 */
	public static String getConfDirectory() {
		final StringBuffer sb = new StringBuffer(getServerHome());
		sb.append(".");
		sb.append(File.separatorChar);
		sb.append("conf");
		sb.append(File.separatorChar);
		return sb.toString();
	}

	/**
	 * Return the configuration file location.
	 * 
	 * @return the file path string
	 */
	public static String getConfFile() {
		return getConfDirectory() + "es.properties";
	}

	private static ESLocationProvider locationProvider;
	private static Boolean isChecksumComputationOnCommitActive;

	/**
	 * Return the server home directory location.
	 * 
	 * @return the dir path string
	 */
	public static String getServerHome() {
		final String workspaceDirectory = getLocationProvider().getWorkspaceDirectory();
		final File workspace = new File(workspaceDirectory);
		if (!workspace.exists()) {
			workspace.mkdirs();
		}
		if (!workspaceDirectory.endsWith(File.separator)) {
			return workspaceDirectory + File.separatorChar;
		}

		return workspaceDirectory;
	}

	/**
	 * Returns the registered {@link ESLocationProvider} or if not existent, the
	 * {@link org.eclipse.emf.emfstore.internal.server.DefaultServerWorkspaceLocationProvider}.
	 * 
	 * @return workspace location provider
	 */
	public static synchronized ESLocationProvider getLocationProvider() {
		if (locationProvider == null) {
			// TODO EXPT PRIO
			try {
				locationProvider = new ESExtensionPoint("org.eclipse.emf.emfstore.server.locationProvider", true)
					.getClass("providerClass", ESLocationProvider.class);
			} catch (final ESExtensionPointException e) {
				final String message = "No location provider or error while instantiating location provider, switching to default location!";
				ModelUtil.logWarning(message);
			}

			if (locationProvider == null) {
				locationProvider = new DefaultServerWorkspaceLocationProvider();
			}
		}

		return locationProvider;
	}

	/**
	 * Gets startup parameter from {@link Platform#getApplicationArgs()} which
	 * are in the form of -[parameterkey]=[parametervalue].
	 * 
	 * @param parameter
	 *            name of parameter key
	 * @return parameter as string or null
	 */
	public static String getStartArgument(String parameter) {
		for (final String arg : Platform.getApplicationArgs()) {
			if (arg.startsWith(parameter) && arg.length() > parameter.length() && arg.charAt(parameter.length()) == '=') {
				return arg.substring(parameter.length() + 1, arg.length());
			}
		}
		return null;
	}

	/**
	 * Checks whether a parameter is set.
	 * 
	 * @param parameter
	 *            checks existence of parameter
	 * @return boolean
	 */
	public static boolean isStartArgSet(String parameter) {
		for (final String arg : Platform.getApplicationArgs()) {
			if (arg.equals(parameter)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Default filepath for spfv authentication.
	 * 
	 * @return path as string
	 */
	public static String getDefaultSPFVFilePath() {
		return getConfDirectory() + "user.properties";
	}

	/**
	 * Gets the server's properties.
	 * 
	 * @return properties
	 */
	public static synchronized Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}

	/**
	 * This method calls {@link Properties#getProperty(String)} and splits the
	 * resulting string, using {@link #MULTI_PROPERTY_SEPERATOR}.
	 * 
	 * @param property
	 *            property key
	 * @return String array or null
	 */
	public static String[] getSplittedProperty(String property) {
		final String result = getProperties().getProperty(property);
		return result == null ? null : splitProperty(result);
	}

	/**
	 * This method calls {@link Properties#getProperty(String, String)} and
	 * splits the resulting string, using {@link #MULTI_PROPERTY_SEPERATOR}.
	 * 
	 * @param property
	 *            property key
	 * @param defaultValue
	 *            default value
	 * @return String array or null
	 */
	public static String[] getSplittedProperty(String property, String defaultValue) {
		final String result = getProperties().getProperty(property, defaultValue);
		return result == null ? null : splitProperty(result);
	}

	private static String[] splitProperty(String property) {
		final ArrayList<String> result = new ArrayList<String>();
		for (final String str : property.split(ServerConfiguration.MULTI_PROPERTY_SEPERATOR)) {
			result.add(str.trim());
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Sets the server's properties. All already contained properties will be set to the values before.
	 * 
	 * @param prop
	 *            properties
	 */
	public static void setProperties(Properties prop) {
		setProperties(prop, true);
	}

	/**
	 * Sets the servers properties.
	 * 
	 * @param prop The properties to set.
	 * @param keepExisting Keep already contained properties?
	 */
	public static void setProperties(Properties prop, boolean keepExisting) {
		final Properties beforeProperties = properties;
		properties = prop;
		if (keepExisting && beforeProperties != null) {
			properties.putAll(beforeProperties);
		}
	}

	/**
	 * Returns the path to the server's keystore.
	 * 
	 * @return path to keystore
	 */
	public static String getServerKeyStorePath() {
		return getServerHome() + SERVER_KEYSTORE_FILE;
	}

	/**
	 * Get the server version as in the org.eclipse.emf.emfstore.internal.server manifest
	 * file.
	 * 
	 * @return the server version number
	 */
	@SuppressWarnings("cast")
	public static String getServerVersion() {
		final Bundle emfStoreBundle = Platform.getBundle("org.eclipse.emf.emfstore.server");
		final String emfStoreVersionString = (String) emfStoreBundle.getHeaders().get(
			org.osgi.framework.Constants.BUNDLE_VERSION);
		return emfStoreVersionString;
	}

	/**
	 * Determine if this is a release version or not.
	 * 
	 * @return true if it is a release version
	 */
	public static boolean isReleaseVersion() {
		return !getServerVersion().endsWith("qualifier") && !isInternalReleaseVersion();
	}

	/**
	 * Determines if this is an internal release or not.
	 * 
	 * @return true if it an internal release
	 */
	public static boolean isInternalReleaseVersion() {
		return getServerVersion().endsWith("internal");
	}

	/**
	 * @param testing
	 *            if server is running for testing
	 */
	public static void setTesting(boolean testing) {
		ServerConfiguration.testing = testing;
	}

	/**
	 * @return if server is running for testing
	 */
	public static boolean isTesting() {
		return testing;
	}

	/**
	 * Whether the server should compute a checksum for the project state when a
	 * commit has happened. If the server does compute a checksum it will be
	 * sent back to the client who then can check whether there are any
	 * differences between his and the server's project state.
	 * 
	 * @return true, if the server does compute a checksum in case a commit has
	 *         happened, false otherwise
	 */
	public static boolean isComputeChecksumOnCommitActive() {
		if (isChecksumComputationOnCommitActive == null) {
			try {
				isChecksumComputationOnCommitActive = new ESExtensionPoint(
					"org.eclipse.emf.emfstore.server.computeChecksum", true)
					.getBoolean("shouldComputeChecksumOnCommit");
			} catch (final ESExtensionPointException e) {
				final String message = "Can not determine whether to compute checksums on commit, default is true.";
				ModelUtil.logWarning(message);
				isChecksumComputationOnCommitActive = true;
			}
		}

		return isChecksumComputationOnCommitActive;
	}

	/**
	 * Returns the list of all {@link StartupListener}s.
	 * 
	 * @return the List of all registered {@link StartupListener}.
	 */
	public static List<StartupListener> getStartupListeners() {
		return STARTUP_LISTENERS;
	}

	/**
	 * Adds a {@link StartupListener} to the list of {@link StartupListener} which gets notified on start of the
	 * EMFStore.
	 * 
	 * @param listener
	 *            the {@link StartupListener} to add
	 */
	public static void addStartupListener(StartupListener listener) {
		STARTUP_LISTENERS.add(listener);
	}

	/**
	 * Returns the list of all {@link PostStartupListener}s.
	 * 
	 * @return the List of all registered {@link PostStartupListener}.
	 */
	public static List<PostStartupListener> getPostStartupListeners() {
		return POST_STARTUP_LISTENERS;
	}

	/**
	 * Adds a {@link PostStartupListener} to the list of {@link PostStartupListener} which gets notified on start of the
	 * EMFStore.
	 * 
	 * @param listener
	 *            the {@link PostStartupListener} to add
	 */

	public static void addPostStartupListener(PostStartupListener listener) {
		POST_STARTUP_LISTENERS.add(listener);
	}

}
