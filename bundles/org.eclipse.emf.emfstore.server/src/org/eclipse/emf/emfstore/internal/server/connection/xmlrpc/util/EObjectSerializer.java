/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Otto von Wesendonk, Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.ws.commons.util.Base64;
import org.apache.ws.commons.util.Base64.Encoder;
import org.apache.ws.commons.util.Base64.EncoderOutputStream;
import org.apache.xmlrpc.serializer.TypeSerializerImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.internal.common.CommonUtil;
import org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.SerializationException;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Serializer for EObjects.
 * 
 * @author ovonwesen
 * @author emueller
 */
public class EObjectSerializer extends TypeSerializerImpl {

	private static final String SELF_CONTAINMENT_CHECK_OPTION = "SelfContainmentCheck"; //$NON-NLS-1$
	private static final String HREF_CHECK_OPTION = "HrefCheck"; //$NON-NLS-1$
	private static final String SERIALIZATION_OPTIONS_EXT = "org.eclipse.emf.emfstore.common.model.serializationOptions"; //$NON-NLS-1$
	/**
	 * EObject Tag for parsing.
	 */
	public static final String EOBJECT_TAG = "EObject"; //$NON-NLS-1$
	private static final String EX_EOBJECT_TAG = "ex:" + EOBJECT_TAG; //$NON-NLS-1$
	private static boolean hrefCheckEnabled;
	private static boolean containmentCheckEnabled;
	private static boolean serializationOptionsInitialized;

	/**
	 * {@inheritDoc}
	 */
	public void write(ContentHandler contentHandler, Object pObject) throws SAXException {
		initSerializationOptions();
		contentHandler.startElement(StringUtils.EMPTY, VALUE_TAG, VALUE_TAG, ZERO_ATTRIBUTES);
		contentHandler.startElement(StringUtils.EMPTY, EOBJECT_TAG, EX_EOBJECT_TAG, ZERO_ATTRIBUTES);
		final char[] buffer = new char[1024];
		final Encoder encoder = new Base64.SAXEncoder(buffer, 0, null, contentHandler);
		try {
			URIConverter.WriteableOutputStream uws = null;
			final OutputStream ostream = new EncoderOutputStream(encoder);
			final BufferedOutputStream bos = new BufferedOutputStream(ostream);
			try {
				EObject eObject = (EObject) pObject;
				XMIResource resource = (XMIResource) eObject.eResource();

				if (eObject instanceof FileBasedChangePackage) {
					// TODO LCP
					final ChangePackage changePackage = toInMemoryChangePackage(FileBasedChangePackage.class
						.cast(eObject));
					eObject = changePackage;
				}
				// uws = writeDirectly(bos, changePackage);
				if (eObject instanceof IdEObjectCollection && resource != null) {
					uws = writeDirectly(bos, eObject);
				} else {
					resource = (XMIResource) new ResourceSetImpl().createResource(ModelUtil.VIRTUAL_URI);
					((ResourceImpl) resource).setIntrinsicIDToEObjectMap(new HashMap<String, EObject>());
					EObject copy;

					if (eObject instanceof IdEObjectCollection) {
						copy = ModelUtil.copyIdEObjectCollection((IdEObjectCollection) eObject, resource);
					} else {
						copy = ModelUtil.clone(eObject);
					}

					if (copy instanceof IdEObjectCollection) {
						final IdEObjectCollection collection = (IdEObjectCollection) eObject;
						setIDsOnResource(resource, collection);
					}

					resource.getContents().add(copy);
					final StringWriter writer = new StringWriter();
					uws = new URIConverter.WriteableOutputStream(writer, CommonUtil.getEncoding());
					// save string into Stringwriter
					checkResource(resource);
					resource.save(uws, ModelUtil.getResourceSaveOptions());
					final String string = writer.toString();
					hrefCheck(string);
					bos.write(string.getBytes(CommonUtil.getEncoding()));
				}
			} catch (final SerializationException e) {
				throw new SAXException(e);
			} finally {
				bos.close();
				if (uws != null) {
					uws.close();
				}
			}
		} catch (final Base64.SAXIOException e) {
			throw e.getSAXException();
		} catch (final IOException e) {
			throw new SAXException(e);
		}
		contentHandler.endElement(StringUtils.EMPTY, EOBJECT_TAG, EX_EOBJECT_TAG);
		contentHandler.endElement(StringUtils.EMPTY, VALUE_TAG, VALUE_TAG);
	}

	private void setIDsOnResource(XMIResource resource, final IdEObjectCollection collection) {
		for (final EObject element : collection.getAllModelElements()) {
			if (ModelUtil.isIgnoredDatatype(element)) {
				continue;
			}
			final ModelElementId elementId = collection.getModelElementId(element);
			resource.setID(element, elementId.getId());
		}
	}

	/**
	 * @param cast
	 * @return
	 */
	private ChangePackage toInMemoryChangePackage(FileBasedChangePackage fileBasedChangePackage) {
		final ChangePackage changePackage = VersioningFactory.eINSTANCE.createChangePackage();
		final ESCloseableIterable<AbstractOperation> operationsHandle = fileBasedChangePackage.operations();
		try {
			for (final AbstractOperation operation : operationsHandle.iterable()) {
				changePackage.add(operation);
			}
		} finally {
			operationsHandle.close();
		}

		changePackage.setLogMessage(fileBasedChangePackage.getLogMessage());

		return changePackage;
	}

	private static URIConverter.WriteableOutputStream writeDirectly(final BufferedOutputStream bufferedOutputStream,
		final EObject eObject)
		throws UnsupportedEncodingException, SerializationException, IOException {
		URIConverter.WriteableOutputStream uws;
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(bufferedOutputStream, CommonUtil.getEncoding());
			uws = new URIConverter.WriteableOutputStream(writer, CommonUtil.getEncoding());
			final Resource res = eObject.eResource();
			checkResource(res);
			res.save(uws, ModelUtil.getResourceSaveOptions());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		return uws;
	}

	private static void checkResource(Resource resource) throws SerializationException {
		if (!containmentCheckEnabled) {
			return;
		}

		if (resource.getContents().size() != 1) {
			throw new SerializationException(Messages.EObjectSerializer_UnexpectedNumberOfEObjects);
		}
		final EObject root = resource.getContents().get(0);
		final Set<EObject> allChildEObjects = CommonUtil.getNonTransientContents(root);
		final Set<EObject> allEObjects = new LinkedHashSet<EObject>(allChildEObjects);
		allEObjects.add(root);
		for (final EObject eObject : allEObjects) {
			if (resource != eObject.eResource()) {
				throw new SerializationException(Messages.EObjectSerializer_NonSelfContainedResource);
			}
			if (eObject.eIsProxy()) {
				throw new SerializationException(Messages.EObjectSerializer_UnresolvedProxy);
			}
		}
	}

	private static void hrefCheck(String result) throws SerializationException {
		if (!hrefCheckEnabled) {
			return;
		}
		final char[] needle = "href".toCharArray(); //$NON-NLS-1$
		int pointer = 0;
		boolean insideQuotes = false;
		for (final char character : result.toCharArray()) {
			if (character == '"') {
				insideQuotes = !insideQuotes;
			}
			if (!insideQuotes && character == needle[pointer]) {
				if (++pointer == needle.length) {
					throw new SerializationException(Messages.EObjectSerializer_HrefDetectionFailed);
				}
			} else {
				pointer = 0;
			}
		}
	}

	/**
	 * Initializes the serialization options.
	 */
	private static void initSerializationOptions() {

		if (serializationOptionsInitialized) {
			return;
		}
		final ESExtensionElement element = new ESExtensionPoint(
			SERIALIZATION_OPTIONS_EXT).getFirst();

		if (element != null) {
			hrefCheckEnabled = element.getBoolean(HREF_CHECK_OPTION);
			containmentCheckEnabled = element.getBoolean(SELF_CONTAINMENT_CHECK_OPTION);
		}

		serializationOptionsInitialized = true;
	}
}
