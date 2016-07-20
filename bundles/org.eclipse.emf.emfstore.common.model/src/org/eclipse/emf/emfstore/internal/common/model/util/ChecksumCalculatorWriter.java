/*******************************************************************************
 * Copyright (c) 2011-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * meegenm - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.common.model.util;

import java.io.IOException;
import java.io.Writer;

/**
 * calculates the checksum of all bytes streamed without wasting space
 *
 * @author Marco van Meegen
 *
 */
public class ChecksumCalculatorWriter extends Writer {
	private long checksum = 1125899906842597L; // prime
	private long trimmedStringChecksum = -1;
	private boolean trimmingLeading = true;

	/**
	 * update checksum with bytes written
	 *
	 * @see java.io.Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		for (int idx = 0; idx < len; idx++) {
			final char c = cbuf[idx + off];
			// trim leading whitespace <= ' '
			if (!trimmingLeading || c > ' ') {
				checksum = 31 * checksum + c;
				if (c > ' ') {
					// trimmdStringChecksum will always be the last checksum calculated where a non-whitespace was found
					trimmedStringChecksum = checksum;
				}
				trimmingLeading = false;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.io.Writer#flush()
	 */
	@Override
	public void flush() throws IOException {
		// ignore

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.io.Writer#close()
	 */
	@Override
	public void close() throws IOException {
		// ignore
	}

	/**
	 * @return the checksum calculated for all characters written to the writer
	 */
	public long getChecksum() {
		return trimmedStringChecksum != -1 ? trimmedStringChecksum : checksum;
	}

}
