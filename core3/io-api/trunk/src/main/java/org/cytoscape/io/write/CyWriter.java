
package org.cytoscape.io.write;

import org.cytoscape.io.FileIOFactory;
import java.io.File;

/**
 * An interface that should be extended to write any type of 
 * data. Writers for specific types of data, like networks or
 * attributes, will extend this interface and provide setter
 * methods to specify the output content.  Writers may be
 * composed of several child interfaces to support writing of
 * heterogenous data types (i.e. networks AND attributes).
 */
public interface CyWriter extends FileIOFactory {

	/**
	 * When called this method will write whatever data it has been
	 * provided by its extending interfaces to the specified {@link File}.
	 * @param f The {@link File} that will be written to by this writer.
	 */
	public void write(File f);
}
