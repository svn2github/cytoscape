
package org.cytoscape.view.model; 

/**
 * The interface used to provide a common interface that allows
 * String to be parsed into ViewModel objects.
 */
public interface Saveable {
	
	/**
	 * Any class that implements this method or interface that extends
	 * it <b>must</b> provide a description of the format of the string
	 * that will be written.  The goal is to provide as much information
	 * as an implementer will need to create a definition string that will
	 * be work seamlessly with other implementions of this interface.
	 * You might even consider defining a regular expression that defines
	 * the string.
	 *
	 * @return A String that defines the state of this object that
	 * is suitable for serialization.
	 */
	public String getStateDefinition();

	/**
	 * @param def A String that is defined according state definition
	 * found in Saveable#getStateDefinition().
	 */
	public void parseStateDefinition(String def);

	/**
	 * This meant to return a human readable string representation of this
	 * implementation that would be suitable for presentation in a user
	 * interface.  This can be an arbitrary string but shouldn't be more
	 * than a few words long. 
	 * <p>
	 * <b>This String is <i>NOT</i> meant to be parsed or otherwise interpreted
	 * by a computer. It is <i>STRICTLY</i> for humans to read!</b>
	 * @return A human readable string identifying this object. 
	 */
	public String getName();
}
