/*******************************************************************************
 * Copyright (c) 2008-2015 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers;

import java.text.MessageFormat;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.connection.ServerKeyStoreManager;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;

/**
 * Verifies user name/password using LDAP.
 *
 * @author Wesendonk
 */
public class LDAPUserVerifier extends UserVerifier {

	private final String ldapUrl;
	private final String ldapBase;
	private final String searchDn;
	private boolean useSSL;

	private static final String DEFAULT_CTX = "com.sun.jndi.ldap.LdapCtxFactory"; //$NON-NLS-1$
	private final String authUser;
	private final String authPassword;

	/**
	 * Default constructor.
	 *
	 * @param orgUnitProvider
	 *            provides access to users and groups
	 * @param ldapUrl
	 *            URL, if the URL starts with {@code ldaps://}, SSL is used.
	 * @param ldapBase base
	 * @param searchDn dn
	 * @param authUser user to allow access to server
	 * @param authPassword password of user to allow access to server
	 */
	// TODO: recheck orgUnitProvider
	public LDAPUserVerifier(ESOrgUnitProvider orgUnitProvider,
		String ldapUrl, String ldapBase, String searchDn, String authUser, String authPassword) {
		super(orgUnitProvider);
		this.ldapUrl = ldapUrl;
		this.ldapBase = ldapBase;
		this.searchDn = searchDn;
		this.authUser = authUser;
		this.authPassword = authPassword;

		if (ldapUrl.startsWith("ldaps://")) { //$NON-NLS-1$
			useSSL = true;
			ServerKeyStoreManager.getInstance().setJavaSSLProperties();
		}
	}

	/**
	 * This method must be implemented by subclasses in order to verify a pair of username and password.
	 * When using authentication you should use {@link org.eclipse.emf.emfstore.server.auth.ESUserVerifier
	 * ESUserVerifier#verifyUser(String, String, ESClientVersionInfo)} in order to gain a session id.
	 *
	 * @param username
	 *            the user name as entered by the client; may differ from the user name of the {@code resolvedUser}
	 * @param password
	 *            the password as entered by the client
	 * @return boolean {@code true} if authentication was successful, {@code false} if not
	 * @throws AccessControlException
	 *             if an exception occurs during the verification process
	 */
	@Override
	public boolean verifyPassword(String username, String password) throws AccessControlException {
		DirContext dirContext = null;

		// anonymous bind and resolve user
		try {
			if (authUser != null && authPassword != null) {
				// authenticated bind and resolve user
				final Properties authenticatedBind = authenticatedBind(authUser, authPassword);
				authenticatedBind.put(Context.SECURITY_PRINCIPAL, authUser);
				dirContext = new InitialDirContext(authenticatedBind);
			} else {
				// anonymous bind and resolve user
				dirContext = new InitialDirContext(anonymousBind());
			}
		} catch (final NamingException e) {
			ModelUtil.logWarning(MessageFormat.format(
				Messages.LDAPVerifier_LDAPDirectoryNotFound, ldapUrl), e);
			return false;
		}
		final String resolvedName = resolveUser(username, dirContext);
		if (resolvedName == null) {
			return false;
		}

		// Authenticated bind and check user's password
		try {
			dirContext = new InitialDirContext(authenticatedBind(resolvedName, password));
		} catch (final NamingException e) {
			ModelUtil.logWarning(
				MessageFormat.format(Messages.LDAPVerifier_LoginFailed, ldapBase), e);
			return false;
		}

		return true;
	}

	private Properties anonymousBind() {
		final Properties props = new Properties();
		props.put("java.naming.ldap.version", "3"); //$NON-NLS-1$ //$NON-NLS-2$
		props.put(Context.INITIAL_CONTEXT_FACTORY, DEFAULT_CTX);
		props.put(Context.PROVIDER_URL, ldapUrl);

		if (useSSL()) {
			props.put("java.naming.ldap.factory.socket", //$NON-NLS-1$
				LDAPSSLSocketFactory.class.getCanonicalName());
			props.put(Context.SECURITY_PROTOCOL, "ssl"); //$NON-NLS-1$
		}

		return props;
	}

	private boolean useSSL() {
		return useSSL;
	}

	private Properties authenticatedBind(String principal, String credentials) {
		final Properties bind = anonymousBind();
		bind.put(Context.SECURITY_AUTHENTICATION, "simple"); //$NON-NLS-1$
		bind.put(Context.SECURITY_PRINCIPAL, principal + "," + ldapBase); //$NON-NLS-1$
		bind.put(Context.SECURITY_CREDENTIALS, credentials);

		return bind;
	}

	private String resolveUser(String username, DirContext dirContext) {
		final SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> results = null;
		try {
			results = dirContext.search(ldapBase, "(& (" + //$NON-NLS-1$
				searchDn + "=" + username //$NON-NLS-1$
				+ ") (objectclass=*))", //$NON-NLS-1$
				constraints);
		} catch (final NamingException e) {
			ModelUtil.logWarning(MessageFormat.format(
				Messages.LDAPVerifier_SearchFailed, ldapBase), e);
			return null;
		}

		if (results == null) {
			return null;
		}

		String resolvedName = null;
		try {
			while (results.hasMoreElements()) {
				final SearchResult sr = results.next();
				if (sr != null) {
					resolvedName = sr.getName();
				}
				break;
			}
		} catch (final NamingException e) {
			ModelUtil.logException(MessageFormat.format(
				Messages.LDAPVerifier_InvalidResults, ldapBase), e);
			return null;
		}

		if (resolvedName == null) {
			ModelUtil.logWarning(MessageFormat.format(Messages.LDAPVerifier_DistinguishedNameNotFound, ldapBase));
			return null;
		}
		return resolvedName;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESUserVerifier#init(org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider)
	 */
	public void init(ESOrgUnitProvider orgUnitProvider) {

	}

}
