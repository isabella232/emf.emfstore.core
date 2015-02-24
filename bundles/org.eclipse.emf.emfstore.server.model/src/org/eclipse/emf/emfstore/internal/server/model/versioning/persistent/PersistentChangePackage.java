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
import java.util.Iterator;
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
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
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

	// public void removeOperation()
	// throws ESException {
	//
	// int i;
	// final VTDGen vg = new VTDGen();
	// final AutoPilot ap = new AutoPilot();
	// try {
	// ap.selectXPath("//operations/*[last()]");
	// } catch (final XPathParseException e) {
	// throw new ESException(e);
	// }
	// final XMLModifier xm = new XMLModifier();
	//
	// if (vg.parseFile(operationsFilePath, true)) {
	// final VTDNav vn = vg.getNav();
	// ap.bind(vn);
	// try {
	// xm.bind(vn);
	// while ((i = ap.evalXPath()) != -1)
	// {
	// // remove the cursor element in the embedded VTDNav object
	// xm.remove();
	// }
	// xm.output(operationsFilePath);
	// } catch (final ModifyException e) {
	// throw new ESException(e);
	// } catch (final XPathEvalException e) {
	// throw new ESException(e);
	// } catch (final NavException e) {
	// throw new ESException(e);
	// } catch (final TranscodeException e) {
	// throw new ESException(e);
	// } catch (final IOException e) {
	// throw new ESException(e);
	// }
	//
	// }
	// }

	public void addAll(List<? extends AbstractOperation> ops) {
		for (final AbstractOperation op : ops) {
			add(op);
		}
	}

	public void add(AbstractOperation op) {

		final int i;

		final ResourceSet rs = new ResourceSetImpl();
		rs.setResourceFactoryRegistry(new ResourceFactoryRegistry());
		final Resource r = rs.createResource(URI.createURI(VIRTUAL_RESOURCE_URI));
		r.getContents().add(op);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		RandomAccessFile raf = null;
		try {
			outputStream.write((OperationEmitter.OPERATIONS_START_TAG + NEWLINE).getBytes());

			final Map<Object, Object> options = new LinkedHashMap<Object, Object>();
			options.put(XMLResource.OPTION_DECLARE_XML, Boolean.FALSE);
			options.put(XMLResource.OPTION_RECORD_ANY_TYPE_NAMESPACE_DECLARATIONS, Boolean.TRUE);

			r.save(outputStream, options);
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

	public Iterator<AbstractOperation> reverseIterator() {
		final ReversedLinesFileReader[] r = new ReversedLinesFileReader[1];
		try {
			r[0] = new ReversedLinesFileReader(new File(operationsFilePath));
		} catch (final IOException ex1) {
			ex1.printStackTrace();
		}

		return new Iterator<AbstractOperation>() {
			AbstractOperation operation;
			final OperationEmitter operationEmitter = new OperationEmitter(false);

			public boolean hasNext() {
				try {
					operation = operationEmitter.tryEmit(ReadLineCapable.INSTANCE.create(r[0]));
					final boolean hasNext = operation != null;
					if (!hasNext) {
						r[0].close();
					}
					return hasNext;
				} catch (final IOException ex) {
					// replace operations file
					ex.printStackTrace();
				}

				IOUtils.closeQuietly(r[0]);
				return false;
			}

			public AbstractOperation next() {
				return operation.reverse();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public Iterator<AbstractOperation> iterator() {
		final BufferedReader[] r = new BufferedReader[1];
		try {
			r[0] = new BufferedReader(new FileReader(new File(operationsFilePath)));
		} catch (final IOException ex1) {
			ex1.printStackTrace();
		}

		return new Iterator<AbstractOperation>() {
			AbstractOperation operation;
			final OperationEmitter operationEmitter = new OperationEmitter(true);

			public boolean hasNext() {
				try {
					operation = operationEmitter.tryEmit(ReadLineCapable.INSTANCE.create(r[0]));
					final boolean hasNext = operation != null;
					if (!hasNext) {
						r[0].close();
					}
					return hasNext;
				} catch (final IOException ex) {
					// replace operations file
					ex.printStackTrace();
				}

				IOUtils.closeQuietly(r[0]);
				return false;
			}

			public AbstractOperation next() {
				return operation;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
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
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(operationsFilePath, "rw"); //$NON-NLS-1$
			raf.seek(0);
			final String emptyCp = XML_HEADER + CHANGE_PACKAGE_START;
			raf.write(emptyCp.getBytes());
			raf.setLength(emptyCp.length());
			raf.close();
		} catch (final FileNotFoundException ex) {
			throw new RuntimeException(ex);
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
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
			final String s;
			final OperationEmitter operationEmitter = new OperationEmitter(false);
			AbstractOperation op;
			final List<AbstractOperation> ops = new ArrayList<AbstractOperation>();
			final ReadLineCapable create = ReadLineCapable.INSTANCE.create(r);
			while (counter > 0 && (op = operationEmitter.tryEmit(create)) != null) {
				ops.add(op);
				counter -= 1;
			}
			// TODO: reuse readlinecapable?
			r.close();

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
		BufferedReader r = null;
		try {
			final File file = new File(operationsFilePath);
			if (!file.exists()) {
				return true;
			}
			// TODO: move reader into operationemitter
			r = new BufferedReader(new FileReader(file));
			final OperationEmitter operationEmitter = new OperationEmitter(true);
			final ReadLineCapable create = ReadLineCapable.INSTANCE.create(r);
			return operationEmitter.tryEmit(create) == null;
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (r != null) {
					r.close();
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
	public Iterable<AbstractOperation> operations() {
		return new Iterable<AbstractOperation>() {
			public Iterator<AbstractOperation> iterator() {
				return PersistentChangePackage.this.iterator();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#reversedOperations()
	 */
	public Iterable<AbstractOperation> reversedOperations() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESChangePackage#size()
	 */
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#toAPI()
	 */
	public ESChangePackage toAPI() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#createAPI()
	 */
	public ESChangePackage createAPI() {
		// TODO Auto-generated method stub
		return null;
	}
}
