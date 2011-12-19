package org.eclipse.emf.emfstore.client.model.controller.export;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Export interface.
 */
public interface IExport {

	/**
	 * These filter names are used to filter which files are displayed.
	 */
	static final String[] FILTER_NAMES = { "EMFStore change package (*.ecc)", "All Files (*.*)" };

	/**
	 * These filter extensions are used to filter which files are displayed.
	 */
	static final String[] FILTER_EXTS = { "*.ecc", "*.*" };

	/**
	 * The label that should be shown while exporting.
	 * 
	 * @return the label that best describes which entity is being exported
	 */
	String getLabel();

	/**
	 * Returns an array of names that should be filtered in the export dialog.
	 * 
	 * @return an array of filtered names
	 */
	String[] getFilteredNames();

	/**
	 * Returns an array of extensions that should be filtered in the export dialog.
	 * 
	 * @return an array of file extensions
	 */
	String[] getFilteredExtensions();

	/**
	 * Performs the actual export.
	 * 
	 * @param file the {@link File} the entity is exported to
	 * @param progressMonitor a {@link ProgressMonitor} for identifying the current state of the export
	 * 
	 * @throws IOException in case an error occurs while exporting
	 */
	void export(File file, IProgressMonitor progressMonitor) throws IOException;

	/**
	 * Returns the file name that is used for the export.
	 * 
	 * @return the file name
	 */
	String getFilename();

	/**
	 * Returns the key that is used to cache the last export location.
	 * 
	 * @return a property key identifier
	 */
	String getParentFolderPropertyKey();
}