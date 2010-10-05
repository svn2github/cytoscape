package org.cytoscape.io.write;


/**
 * Returns a Task that will write
 */
public interface PropertyWriterFactory extends CyWriterFactory {

	void setProperty(Object property);
}
