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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.parser.ByteArrayParser;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileChunk;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileTransferInformation;

public class FileTransferInformationParser extends ByteArrayParser {
	@Override
	public Object getResult() throws XmlRpcException {
		try {
			final byte[] res = (byte[]) super.getResult();
			final ByteArrayInputStream bais = new ByteArrayInputStream(res);
			final ObjectInputStream ois = new ObjectInputStream(bais) {
				@Override
				protected java.lang.Class<?> resolveClass(java.io.ObjectStreamClass desc)
					throws IOException, ClassNotFoundException {
					final Class<?> resolveClass = super.resolveClass(desc);
					if (resolveClass == FileTransferInformation.class) {
						return resolveClass;
					}
					if (resolveClass == FileChunk.class) {
						return resolveClass;
					}
					if (resolveClass == byte[].class) {
						// needed because of byte array inputstream usage above
						return resolveClass;
					}
					throw new IllegalArgumentException("Deserialzation is not supported"); //$NON-NLS-1$
				}
			};
			return ois.readObject();
		} catch (final IOException e) {
			throw new XmlRpcException("Failed to read result object: " + e.getMessage(), e); //$NON-NLS-1$
		} catch (final ClassNotFoundException e) {
			throw new XmlRpcException("Failed to load class for result object: " + e.getMessage(), e); //$NON-NLS-1$
		}
	}
}
