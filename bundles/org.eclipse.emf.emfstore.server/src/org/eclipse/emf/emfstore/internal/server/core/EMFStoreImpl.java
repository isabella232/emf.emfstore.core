/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 * Andreas Boehlke - method representation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.internal.common.ESCollections;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.EMFStore;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AuthorizationControl;
import org.eclipse.emf.emfstore.internal.server.core.helper.EmfStoreMethod;
import org.eclipse.emf.emfstore.internal.server.core.helper.EmfStoreMethod.MethodId;
import org.eclipse.emf.emfstore.internal.server.core.subinterfaces.ChangePackageFragmentUploadAdapter;
import org.eclipse.emf.emfstore.internal.server.core.subinterfaces.ChangePackageFragmentProviderAdapter;
import org.eclipse.emf.emfstore.internal.server.core.subinterfaces.EMFStorePropertiesSubInterfaceImpl;
import org.eclipse.emf.emfstore.internal.server.core.subinterfaces.EMFStoreVersionSubInterface;
import org.eclipse.emf.emfstore.internal.server.core.subinterfaces.EPackageSubInterfaceImpl;
import org.eclipse.emf.emfstore.internal.server.core.subinterfaces.FileTransferSubInterfaceImpl;
import org.eclipse.emf.emfstore.internal.server.core.subinterfaces.HistorySubInterfaceImpl;
import org.eclipse.emf.emfstore.internal.server.core.subinterfaces.ProjectPropertiesSubInterfaceImpl;
import org.eclipse.emf.emfstore.internal.server.core.subinterfaces.ProjectSubInterfaceImpl;
import org.eclipse.emf.emfstore.internal.server.core.subinterfaces.UserSubInterfaceImpl;
import org.eclipse.emf.emfstore.internal.server.core.subinterfaces.VersionSubInterfaceImpl;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.model.ServerSpace;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageProxy;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.observer.ESServerCallObserver;

import com.google.common.base.Optional;

/**
 * This is the main implementation of {@link EMFStore}.
 *
 * @author wesendon
 * @see EMFStore
 */
public class EMFStoreImpl extends AbstractEmfstoreInterface implements InvocationHandler {

	private static final String CLASS = "class"; //$NON-NLS-1$
	private static final String ORG_ECLIPSE_EMF_EMFSTORE_SERVER_SERVER_CALL_OBSERVER = "org.eclipse.emf.emfstore.server.serverCallObserver"; //$NON-NLS-1$

	/**
	 * Represents a method in a subinterface.
	 *
	 * @author boehlke
	 */
	private class SubInterfaceMethod {
		private final AbstractSubEmfstoreInterface iface;
		private final Method method;

		public SubInterfaceMethod(AbstractSubEmfstoreInterface iface, Method m) {
			method = m;
			this.iface = iface;
		}

		/**
		 * @return the iface
		 */
		public AbstractSubEmfstoreInterface getIface() {
			return iface;
		}

		/**
		 * @return the method
		 */
		public Method getMethod() {
			return method;
		}
	}

	private EnumMap<MethodId, SubInterfaceMethod> subInterfaceMethods;
	private final Set<ESServerCallObserver> serverCallObservers;

	/**
	 * Default constructor.
	 *
	 * @param serverSpace
	 *            the {@link ServerSpace}
	 * @param authorizationControl
	 *            the {@link AuthorizationControl}
	 * @throws FatalESException
	 *             in case of failure
	 */
	public EMFStoreImpl(ServerSpace serverSpace, AuthorizationControl authorizationControl)
		throws FatalESException {
		super(serverSpace, authorizationControl);
		serverCallObservers = initServerCallObservers();
	}

	/**
	 *
	 */
	private Set<ESServerCallObserver> initServerCallObservers() {
		final Set<ESServerCallObserver> result = new LinkedHashSet<ESServerCallObserver>();
		for (final ESExtensionElement e : new ESExtensionPoint(ORG_ECLIPSE_EMF_EMFSTORE_SERVER_SERVER_CALL_OBSERVER)
			.getExtensionElements()) {
			final ESServerCallObserver observer = e.getClass(CLASS, ESServerCallObserver.class);
			if (observer != null) {
				result.add(observer);
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initSubInterfaces() throws FatalESException {
		subInterfaceMethods = new EnumMap<MethodId, SubInterfaceMethod>(MethodId.class);
		addSubInterface(new HistorySubInterfaceImpl(this));
		addSubInterface(new ProjectSubInterfaceImpl(this));
		addSubInterface(new UserSubInterfaceImpl(this));
		addSubInterface(new VersionSubInterfaceImpl(this));
		addSubInterface(new FileTransferSubInterfaceImpl(this));
		addSubInterface(new ProjectPropertiesSubInterfaceImpl(this));
		addSubInterface(new EMFStorePropertiesSubInterfaceImpl(this));
		addSubInterface(new EPackageSubInterfaceImpl(this));
		addSubInterface(new EMFStoreVersionSubInterface(this));
	}

	@Override
	protected void addSubInterface(AbstractSubEmfstoreInterface iface) {
		super.addSubInterface(iface);
		for (final Method method : iface.getClass().getMethods()) {
			final EmfStoreMethod implSpec = method.getAnnotation(EmfStoreMethod.class);
			if (implSpec != null) {
				subInterfaceMethods.put(implSpec.value(), new SubInterfaceMethod(iface, method));
			}
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object obj, final Method method, final Object[] args) throws ESException {
		final MethodInvocation methodInvocation = new MethodInvocation(method.getName(), args);
		getAuthorizationControl().checkAccess(methodInvocation);

		final Object[] adjustedArgs = adjustParameters(args);

		notifyServerCallObservers(new ServerCallObserverNotifier() {
			public void notify(ESServerCallObserver observer) {
				observer.notifyPreServerCallExecution(method, adjustedArgs);
			}
		});
		final SubInterfaceMethod subIfaceMethod = subInterfaceMethods.get(methodInvocation.getType());
		try {
			final Object result = subIfaceMethod.getIface().execute(subIfaceMethod.getMethod(), adjustedArgs);
			final Object adjustedResult = adjustResult(
				ESCollections.find(adjustedArgs, SessionId.class),
				result);

			notifyServerCallObservers(new ServerCallObserverNotifier() {
				public void notify(ESServerCallObserver observer) {
					observer.notifyPostServerCallExecution(method, args, adjustedResult);
				}
			});

			return adjustedResult;
			// notify observers about exceptions and rethrow exception
		} catch (final ESException esException) {
			notifyServerCallObservers(new ServerCallObserverNotifier() {
				public void notify(ESServerCallObserver observer) {
					observer.notifyServerCallExecutionESExceptionFailure(method, args, esException);
				}
			});
			throw esException;
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final RuntimeException runtimeException) {
			// END SUPRESS CATCH EXCEPTION
			notifyServerCallObservers(new ServerCallObserverNotifier() {
				public void notify(ESServerCallObserver observer) {
					observer.notifyServerCallExecutionRuntimeExceptionFailure(method, args, runtimeException);
				}
			});
			throw runtimeException;
		}
	}

	private Object[] adjustParameters(final Object[] args) throws ESException {
		for (int i = 0; i < args.length; i++) {
			final Object object = args[i];
			if (ChangePackageProxy.class.isInstance(object)) {
				args[i] = resolveChangePackageProxy(
					ESCollections.find(args, SessionId.class),
					ChangePackageProxy.class.cast(object));
			}
		}
		return args;

	}

	private Object adjustResult(Optional<SessionId> maybeSessionId, Object result) throws ESException {
		if (!ServerConfiguration.getChangePackageFragmentSize().isPresent()) {
			return result;
		}

		if (!maybeSessionId.isPresent()) {
			throw new ESException(Messages.EMFStoreImpl_Missing_SessionAndOrProjectId);
		}

		return adjustResult(maybeSessionId.get(), result);

	}

	@SuppressWarnings("unchecked")
	private Object adjustResult(SessionId sessionId, Object result) {

		if (result instanceof List<?>) {
			@SuppressWarnings("rawtypes")
			final List results = (List) result;
			for (int i = 0; i < results.size(); i++) {
				final Object item = results.get(i);
				results.set(i, adjustResult(sessionId, item));
			}
			return results;
		} else if (result instanceof Object[]) {
			final Object[] results = (Object[]) result;
			for (int i = 0; i < results.length; i++) {
				results[i] = adjustResult(sessionId, results[i]);
			}
			return results;
		} else if (AbstractChangePackage.class.isInstance(result) && !ChangePackageProxy.class.isInstance(result)) {
			final ChangePackageProxy changePackageProxy = createAndRegisterChangePackageProxy(
				sessionId,
				AbstractChangePackage.class.cast(result));
			return changePackageProxy;
		}

		return result;
	}

	/**
	 * Fragments a given change package and attaches all fragments to the given session.
	 *
	 * @param maybeSession
	 *            an {@link Optional} containing a {@link SessionId}
	 * @param changePackageSuffix
	 *            a suffix that must be unique for the current change package within the respective project.
	 *            Relevant if multiple change packages need to be fragmented successively
	 * @param changePackage
	 *            the change package to be fragmented
	 * @return
	 */
	private ChangePackageProxy createAndRegisterChangePackageProxy(final SessionId session,
		final AbstractChangePackage changePackage) {

		final SessionId sessionId = getAuthorizationControl()
			.resolveSessionById(session.getId());

		ChangePackageFragmentProviderAdapter adapter;
		final Optional<ChangePackageFragmentProviderAdapter> maybeAdapter = ESCollections.find(
			sessionId.eAdapters(), ChangePackageFragmentProviderAdapter.class);

		if (!maybeAdapter.isPresent()) {
			adapter = new ChangePackageFragmentProviderAdapter();
			sessionId.eAdapters().add(adapter);
		} else {
			adapter = maybeAdapter.get();
		}

		final String proxyId = generateProxyId();
		adapter.addAsFragments(proxyId, changePackage);

		final ChangePackageProxy proxy = VersioningFactory.eINSTANCE.createChangePackageProxy();
		proxy.setId(proxyId);
		proxy.setLogMessage(
			ModelUtil.clone(changePackage.getLogMessage()));
		return proxy;
	}

	private String generateProxyId() {
		return UUID.randomUUID().toString();
	}

	private ChangePackage resolveChangePackageProxy(
		final Optional<SessionId> maybeSession,
		final ChangePackageProxy changePackageProxy) throws ESException {

		if (!maybeSession.isPresent()) {
			throw new ESException(
				Messages.EMFStoreImpl_NoValidSessiondIdFound);
		}

		final SessionId sessionId = getAuthorizationControl()
			.resolveSessionById(maybeSession.get().getId());

		final Optional<ChangePackageFragmentUploadAdapter> maybeAdapter =
			ESCollections.find(sessionId.eAdapters(), ChangePackageFragmentUploadAdapter.class);

		if (!maybeAdapter.isPresent()) {
			throw new ESException(
				Messages.EMFStoreImpl_NoFragmentAdapterFound);
		}

		final ChangePackageFragmentUploadAdapter adapter = maybeAdapter.get();

		final Optional<ChangePackage> maybeCompletedChangePackage = adapter
			.getCompletedChangePackage(changePackageProxy.getId());

		if (!maybeCompletedChangePackage.isPresent()) {
			throw new ESException(
				Messages.EMFStoreImpl_NoCompletedChangePackageFound);
		}

		final ChangePackage changePackage = maybeCompletedChangePackage.get();
		adapter.clearCompleted(changePackageProxy.getId());
		changePackage.setLogMessage(
			ModelUtil.clone(changePackageProxy.getLogMessage()));
		return changePackage;
	}

	/**
	 * Notify the observers with the given notifier.
	 *
	 * @param serverCallObserverNotifier the notifier
	 */
	private void notifyServerCallObservers(ServerCallObserverNotifier serverCallObserverNotifier) {
		for (final ESServerCallObserver callObserver : serverCallObservers) {
			try {
				serverCallObserverNotifier.notify(callObserver);
				// BEGIN SUPRESS CATCH EXCEPTION
			} catch (final RuntimeException runtimeException) {
				// END SUPRESS CATCH EXCEPTION
				ModelUtil.logWarning(Messages.EMFStoreImpl_ServerCallObserverNotifier_Failed, runtimeException);
			}
		}
	}

	/**
	 * creates a dynamic proxy backed by EmfStoreImpl.
	 *
	 * @param serverSpace the server space
	 * @param accessControl an access control instance
	 * @return an instance of emfstore
	 * @throws IllegalArgumentException thrown by Proxy.newInstance
	 * @throws FatalESException thrown if something fatal happens
	 */
	public static EMFStore createInterface(ServerSpace serverSpace, AuthorizationControl accessControl)
		throws IllegalArgumentException, FatalESException {
		return (EMFStore) Proxy.newProxyInstance(EMFStoreImpl.class.getClassLoader(), new Class[] { EMFStore.class },
			new EMFStoreImpl(serverSpace, accessControl));
	}

}
