
package org.cytoscape.io.read;

import java.io.InputStream;
import org.cytoscape.io.FileIOFactory;
import org.cytoscape.work.TaskFactory;

/**
 * A super interface that allows the input stream to be set for reader
 * task factories.
 */
public interface InputStreamTaskFactory extends TaskFactory, FileIOFactory {

	/**
	 * Sets the input stream that will be read by the Reader created from
	 * this factory.
	 * @param is The {@link java.io.InputStream} to be read.
	 */
	void setInputStream(InputStream is);
}
