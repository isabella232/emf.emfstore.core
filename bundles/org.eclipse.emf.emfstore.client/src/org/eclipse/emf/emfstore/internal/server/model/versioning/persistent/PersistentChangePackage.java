package org.eclipse.emf.emfstore.internal.server.model.versioning.persistent;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.emfstore.internal.common.ResourceFactoryRegistry;
import org.eclipse.emf.emfstore.internal.common.api.APIDelegate;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.CloseableIterable;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.persistent.FileBasedOperationIterable.Direction;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.ESLogMessage;

// TODO: iterator
/**
 * @since 1.4
 */
public class PersistentChangePackage implements APIDelegate<ESChangePackage>, ESChangePackage {

	private static final String NEWLINE = "\n"; //$NON-NLS-1$
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE + "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\"/>" + NEWLINE; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String CHANGE_PACKAGE_START = "<org.eclipse.emf.emfstore.internal.server.model.versioning:ChangePackage xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:org.eclipse.emf.emfstore.internal.server.model.versioning=\"http://eclipse.org/emf/emfstore/server/model/versioning\" xmlns:org.eclipse.emf.emfstore.internal.server.model.versioning.operations=\"http://eclipse.org/emf/emfstore/server/model/versioning/operations\">" //$NON-NLS-1$
		+ NEWLINE;
	private static final String CHANGE_PACKAGE_END = "</org.eclipse.emf.emfstore.internal.server.model.versioning:ChangePackage>"; //$NON-NLS-1$
	private static final String VIRTUAL_RESOURCE_URI = "VIRTUAL"; //$NON-NLS-1$
	private final String operationsFilePath;
	private ESLogMessage logMessage;
	private boolean needsInit;
	private ESChangePackage apiImpl;

	public PersistentChangePackage(String operationsFilePath) {
		this.operationsFilePath = operationsFilePath;
	}

	/**
	 * @param fileString
	 * @param b
	 */
	public PersistentChangePackage(String fileString, boolean b) {
		operationsFilePath = fileString;
		needsInit = b;
	}

	public int count() {
		int cnt = 0;
		ReversedLinesFileReader r = null;
		try {
			r = new ReversedLinesFileReader(new File(operationsFilePath));
			String s;
			while ((s = r.readLine()) != null) {
				if (s.contains("</operations>")) {
					cnt++;
				}
			}
		} catch (final IOException ex) {
			ex.printStackTrace();
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (final IOException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		}
		return cnt;

	}

	public void addAll(List<? extends AbstractOperation> ops) {
		// TODO: LCP - file is reopened for each op
		for (final AbstractOperation op : ops) {
			add(op);
		}
	}

	public void add(final AbstractOperation op) {

		final ResourceSet rs = new ResourceSetImpl();
		rs.setResourceFactoryRegistry(new ResourceFactoryRegistry());
		final Resource resource = rs.createResource(URI.createURI(VIRTUAL_RESOURCE_URI));

		resource.getContents().add(op);

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		RandomAccessFile raf = null;
		try {
			outputStream.write((OperationEmitter.OPERATIONS_START_TAG + NEWLINE).getBytes());

			final Map<Object, Object> options = new LinkedHashMap<Object, Object>();
			options.put(XMLResource.OPTION_DECLARE_XML, Boolean.FALSE);
			options.put(XMLResource.OPTION_RECORD_ANY_TYPE_NAMESPACE_DECLARATIONS, Boolean.TRUE);

			resource.save(outputStream, options);
			outputStream.write((OperationEmitter.OPERATIONS_END_TAG + NEWLINE).getBytes());

			final File file = new File(operationsFilePath);

			if (needsInit) {
				needsInit = false;
				final FileWriter w = new FileWriter(file);
				w.write(XML_HEADER + CHANGE_PACKAGE_START);
				w.write(CHANGE_PACKAGE_END);
				w.close();
			}

			raf = new RandomAccessFile(operationsFilePath, "rw"); //$NON-NLS-1$

			raf.skipBytes((int) (raf.length() - CHANGE_PACKAGE_END.getBytes().length));

			raf.write(outputStream.toByteArray());
			raf.write(CHANGE_PACKAGE_END.getBytes());
		} catch (final IOException e) {
			// avoid checked exception since this would lead to cluttered code
			throw new RuntimeException(e);
		} finally {
			try {
				outputStream.close();
				if (raf != null) {
					raf.close();
				}
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public ESLogMessage getLogMessage() {
		return logMessage;
	}

	public void setCommitMessage(ESLogMessage logMessage) {
		this.logMessage = logMessage;
	}

	public void save() {
		// do nothing?
	}

	public void clear() {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(operationsFilePath, "rw"); //$NON-NLS-1$
			raf.seek(0);
			final String emptyCp = XML_HEADER + CHANGE_PACKAGE_START + CHANGE_PACKAGE_END;
			raf.write(emptyCp.getBytes());
			raf.setLength(emptyCp.length());
		} catch (final FileNotFoundException ex) {
			throw new RuntimeException(ex);
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (final IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 *
	 */
	public List<AbstractOperation> removeFromEnd(int n) {
		ReversedLinesFileReader r = null;
		int counter = n;

		try {
			r = new ReversedLinesFileReader(new File(operationsFilePath));
			final OperationEmitter operationEmitter = new OperationEmitter(Direction.Backward);
			AbstractOperation op;
			final List<AbstractOperation> ops = new ArrayList<AbstractOperation>();
			final ReadLineCapable create = ReadLineCapable.INSTANCE.create(r);
			while (counter > 0 && (op = operationEmitter.tryEmit(create)) != null) {
				ops.add(op);
				counter -= 1;
			}
			// TODO: reuse readlinecapable?

			final long offset = operationEmitter.getOffset();

			final RandomAccessFile raf = new RandomAccessFile(operationsFilePath, "rw");
			final long skip = raf.length() + 1 - offset;
			raf.seek(skip);
			// TODO: duplicate code
			final byte[] bytes = (NEWLINE + CHANGE_PACKAGE_END).getBytes();
			raf.write(bytes);
			raf.setLength(skip + bytes.length);
			raf.close();

			return ops;

		} catch (final IOException ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(r);
		}

		return null;

	}

	public boolean isEmpty() {
		BufferedReader reader = null;
		try {
			final File file = new File(operationsFilePath);
			if (!file.exists()) {
				return true;
			}
			// TODO: move reader into operationemitter
			reader = new BufferedReader(new FileReader(file));
			final OperationEmitter operationEmitter = new OperationEmitter(Direction.Forward);
			final ReadLineCapable create = ReadLineCapable.INSTANCE.create(reader);
			return operationEmitter.tryEmit(create) == null;
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (final IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#getCommitMessage()
	 */
	public ESLogMessage getCommitMessage() {
		return getLogMessage();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#operations()
	 */
	public CloseableIterable<AbstractOperation> operations() {
		return new FileBasedOperationIterable(operationsFilePath, Direction.Forward);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#reversedOperations()
	 */
	public CloseableIterable<AbstractOperation> reversedOperations() {
		return new FileBasedOperationIterable(operationsFilePath, Direction.Backward);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#size()
	 */
	public int size() {
		return count();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#toAPI()
	 */
	public ESChangePackage toAPI() {
		if (apiImpl == null) {
			apiImpl = createAPI();
		}
		return apiImpl;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#createAPI()
	 */
	public ESChangePackage createAPI() {
		return new ESPersistentChangePackageImpl(this);
	}

}
