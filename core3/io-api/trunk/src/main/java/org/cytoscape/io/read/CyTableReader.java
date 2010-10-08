package org.cytoscape.io.read;

import org.cytoscape.model.CyTable;
import org.cytoscape.work.Task;

/**
 * An extension of the Task interface that returns an array of 
 * {@link CyTable} objects. 
 * Instances of this interface are created by InputStreamTaskFactory
 * objects registered as OSGi services, which are in turn processed
 * by associated reader manager objects that distinguish 
 * InputStreamTaskFactories based on the DataCategory associated with
 * the CyFileFilter.
 */
public interface CyTableReader extends Task{

	/**
	 * @return An array of CyTable objects.
	 */
	public CyTable[] getCyDataTables();
}
