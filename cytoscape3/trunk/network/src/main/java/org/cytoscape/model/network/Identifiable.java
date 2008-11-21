
package org.cytoscape.model.network;


/**
 * Any object that implements this interface shall return a session unique
 * identifier that shall be unique among all instances of objects that 
 * implement this interface.  The identifier should be greater than 0.
 */
public interface Identifiable { 
	public long getSUID();
}
