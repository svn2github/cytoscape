
package org.cytoscape.io.read;

/**
 * The basic input interface that specifies what is to be read and when it is to
 * be read. This interface should be extended by other interfaces to provide
 * access to the data that gets read. One class can then implement multiple
 * CyProducer interfaces to support reading files that contain multiple types of
 * data (like networks that contain both attribute and view model information).
 * 
 */
public interface CyDataTableProducerFactory extends InputStreamTaskFactory {

	CyDataTableProducer getTask();
}
