package Properties;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
//import java.util.Properties;
import javax.swing.JPanel;
import TunableDefinition.Tunable;


public interface properties{
	/**
	 * This method is used to add a new Tunable to the LayoutProperties
	 * list.  The Tunable can later be retrieved by name using the
	 * <tt>get</tt> method.
	 *
	 * @param tunable The Tunable to add to this LayoutProperties
	 */
	public void add();

	/**
	 * This method is used to get the Tunable named <tt>name</tt>
	 * from this LayoutProperties.  In general, the name of a
	 * Tunable should correspond to the last component of the
	 * property that it is associated with.
	 *
	 * @param name The name of the Tunable to retrieve.
	 * @return Tunable associated with <tt>name</tt> or null if
	 *         there is no Tunable with that name.
	 */
	public Tunable get(String name);

	/**
	 * These methods provide some simple convenience methods for property
	 * handling.  They are intended to be used as a mechanism to track
	 * settings and tuneables.
	 */

	/**
	 * Used to extract properties from the props parameter. 
	 * getProperties should always be called first to initialize the property maps.
	 *
	 * @return HashMap containing the resulting properties
	 */
	public HashMap getProperties(Properties props);

	/**
	 * Used to add modified properties to props parameter 
	 * so they can be saved in the properties file.
	 *
	 */
	public void saveProperties(Properties props);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param property DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 */
	public void setProperty(String property, String value);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param property DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 */
	public void setSavedProperty(String property, String value);

	/**
	 * revertProperties is used primarily by the settings dialog mechanism when
	 * the user does a "Cancel".
	 */
	public void revertProperties();

	/**
	 * This method is used to read the properties from the Cytoscape properties
	 * file and set the values for that property in the appropriate Tunable.  If
	 * there is no value for the property, then the default value in the Tunable
	 * is used to initialize the property.
	 */
	public void initializeProperties(Properties props);
	
	public JPanel getDefaultValue();
	public JPanel getSavedValue();

}
