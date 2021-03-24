/*******************************************************************************
 * Copyright (c) 2011-2021 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.ws.commons.util.Base64;
import org.apache.ws.commons.util.Base64.Encoder;
import org.apache.ws.commons.util.Base64.EncoderOutputStream;
import org.apache.xmlrpc.serializer.TypeSerializerImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class FileTransferInformationSerializer extends TypeSerializerImpl {
	/**
	 * Tag name of a base64 value.
	 */
	public static final String FTI_TAG = "filetransferinformation"; //$NON-NLS-1$
	private static final String EX_FTI_TAG = "ex:" + FTI_TAG; //$NON-NLS-1$

	public void write(final ContentHandler pHandler, Object pObject) throws SAXException {
		pHandler.startElement("", VALUE_TAG, VALUE_TAG, ZERO_ATTRIBUTES); //$NON-NLS-1$
		pHandler.startElement("", FTI_TAG, EX_FTI_TAG, ZERO_ATTRIBUTES); //$NON-NLS-1$
		final char[] buffer = new char[1024];
		final Encoder encoder = new Base64.SAXEncoder(buffer, 0, null, pHandler);
		try {
			final OutputStream ostream = new EncoderOutputStream(encoder);
			final ObjectOutputStream oos = new ObjectOutputStream(ostream);
			oos.writeObject(pObject);
			oos.close();
		} catch (final Base64.SAXIOException e) {
			throw e.getSAXException();
		} catch (final IOException e) {
			throw new SAXException(e);
		}
		pHandler.endElement("", FTI_TAG, EX_FTI_TAG); //$NON-NLS-1$
		pHandler.endElement("", VALUE_TAG, VALUE_TAG); //$NON-NLS-1$
	}
}