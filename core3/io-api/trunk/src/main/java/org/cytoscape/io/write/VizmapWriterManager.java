package org.cytoscape.io.write;

import java.io.File;
import java.io.OutputStream;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.view.vizmap.model.Vizmap;

/**
 * A {@link CyWriterManager} specific to writing {@link org.cytoscape.view.vizmap.model.Vizmap} objects. 
 */
public interface VizmapWriterManager extends CyWriterManager {

	/**
	 * @param vizmap The {@link org.cytoscape.view.vizmap.model.Vizmap} to be written.
	 * @param filter The {@link org.cytoscape.io.CyFileFilter} that defines the type of file to be written.
	 * @param file The file to be written. 
	 * @return The {@link CyWriter} Task that will attempt to write the specified vizmap to the
	 * specified file of the specified file type. 
	 */
	CyWriter getWriter(Vizmap vizmap, CyFileFilter filter, File file) throws Exception;

	/**
	 * @param vizmap The {@link org.cytoscape.view.vizmap.model.Vizmap} to be written.
	 * @param filter The {@link org.cytoscape.io.CyFileFilter} that defines the type of file to be written.
	 * @param os The output stream to be written. 
	 * @return The {@link CyWriter} Task that will attempt to write the specified vizmap to the
	 * specified output stream of the specified file type. 
	 */
	CyWriter getWriter(Vizmap vizmap, CyFileFilter filter, OutputStream os) throws Exception;
}
