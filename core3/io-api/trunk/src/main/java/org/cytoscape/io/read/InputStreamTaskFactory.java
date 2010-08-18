
package org.cytoscape.io.read;

import java.io.InputStream;
import org.cytoscape.io.FileIOFactory;
import org.cytoscape.work.TaskFactory;

public interface InputStreamTaskFactory extends TaskFactory, FileIOFactory {

	void setInputStream(InputStream is);

}
