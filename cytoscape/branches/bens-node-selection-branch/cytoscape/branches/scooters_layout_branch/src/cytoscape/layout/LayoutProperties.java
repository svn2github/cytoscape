// vim: set ts=2: */
package cytoscape.layout;

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import cytoscape.CytoscapeInit;

/**
 * The LayoutProperties class is a helper class to support the management
 * of settings and properties for layout algorithms that implement 
 * LayoutAlgorithm or extend AbstractLayout.  LayoutProperties objects
 * maintain a list of Tunables that are supplied by the individual
 * algorithms.  Each Tunable represents a value that should be loaded
 * from the Cytoscape properties file, and made available as a setting
 * in the LayoutSettingsDialog.  Tunables are added to the LayoutProperties
 * using the <tt>add</tt> method and are retrieved with the <tt>get</tt>
 * method.
 */

public class LayoutProperties {
  private HashMap<String,String> propertyMap = null;
  private HashMap<String,String> savedPropertyMap = null;
  private HashMap<String,Tunable> tunablesMap = null;
  private List<Tunable> tunablesList = null;
  private String propertyPrefix = null;
	

	/**
	 * Constructor.
	 *
	 * @param propertyPrefix String representing the prefix to be used
	 *                       when pulling properties from the property
	 *                       list.
	 */
	public LayoutProperties (String propertyPrefix) {
		this.propertyPrefix = propertyPrefix;
		this.tunablesMap = new HashMap();
		this.tunablesList = new ArrayList();
	}

	/**
	 * This method is used to add a new Tunable to the LayoutProperties
	 * list.  The Tunable can later be retrieved by name using the 
	 * <tt>get</tt> method.
	 *
	 * @param tunable The Tunable to add to this LayoutProperties
	 */
	public void add (Tunable tunable) {
		tunablesMap.put (tunable.getName(), tunable);
		tunablesList.add (tunable);
	}

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
	public Tunable get (String name) {
		if (tunablesMap.containsKey(name))
			return (Tunable)tunablesMap.get(name);
		return null;
	}

	/**
	 * This method is used to get the value from the Tunable 
	 * named <tt>name</tt> from this LayoutProperties.  The
	 * value is always returned as a String.
	 *
	 * @param name The name of the Tunable whose value you 
	 *             want to retrieve.
	 * @return String value from the Tunable or null if
	 *         there is no Tunable with that name.
	 */
	public String getValue (String name) {
		if (tunablesMap.containsKey(name)) {
			Tunable t = (Tunable)tunablesMap.get(name);
			return t.getValue().toString();
		}
		return null;
	}

	/**
	 * This method calls the <tt>updateValues</tt> method of each
	 * Tunable that is part of this LayoutProperty.
	 */
	public void updateValues() {
		for (Iterator iter = tunablesList.iterator(); iter.hasNext(); ) {
			Tunable tunable = (Tunable)iter.next();
			tunable.updateValue();
		}
	}

  /**
   * These methods provide some simple convenience methods for property
   * handling.  They are intended to be used as a mechanism to track
   * settings and tuneables.
   */

  /**
   * getProperties is used to extract properties from the Cytoscape properties
   * file.  getProperties should always be called first to initialize the property
   * maps.
   *
   * @return HashMap containing the resulting properties
   */
  public HashMap getProperties() {
    String prefix = getPrefix();
    Properties props = CytoscapeInit.getProperties();
    propertyMap = new HashMap();
    savedPropertyMap = new HashMap();

    // Find all properties with this prefix
    Enumeration iter = props.propertyNames();
    while (iter.hasMoreElements()) {
      String property = (String)iter.nextElement();
      if (property.startsWith(prefix)) {
        int start = prefix.length()+1;
        propertyMap.put(property.substring(start+1), props.getProperty(property));
        savedPropertyMap.put(property.substring(start+1), props.getProperty(property));
      }
    }
    return propertyMap;
  }

  /**
   * saveProperties is used to add modified properties to the Cytoscape properties
   * so they can be saved in the properties file.
   *
   */
  public void saveProperties() {
    String prefix = getPrefix();
    Properties props = CytoscapeInit.getProperties();
    for (Iterator iter = propertyMap.keySet().iterator(); iter.hasNext();) {
      String key = (String)iter.next();
      props.setProperty(prefix+key, (String)propertyMap.get(key));
    }
  }

  public void setProperty(String property, String value) {
    propertyMap.put(property, value);
  }

  public void setSavedProperty(String property, String value) {
    savedPropertyMap.put(property, value);
  }

  /**
   * revertProperties is used primarily by the settings dialog mechanism when
   * the user does a "Cancel".
   */
  public void revertProperties() {
    propertyMap = new HashMap();
    Set keys = savedPropertyMap.keySet();
    for (Iterator iter = keys.iterator(); iter.hasNext();) {
      String key = (String)iter.next();
      propertyMap.put(new String(key),
                      new String((String)savedPropertyMap.get(key)));
			Tunable t = (Tunable)tunablesMap.get(key);
			if (t != null) t.setValue((String)savedPropertyMap.get(key));
    }
  }

	/**
	 * This method is used to read the properties from the Cytoscape properties
	 * file and set the values for that property in the appropriate Tunable.  If
	 * there is no value for the property, then the default value in the Tunable
	 * is used to initialize the property.
	 */
	public void initializeProperties() {
		getProperties();

		for (Iterator iter = tunablesList.iterator(); iter.hasNext(); ) {
			Tunable tunable = (Tunable)iter.next();
			String property =  tunable.getName();
			// Do we have this property?
			if (propertyMap.containsKey(property)) {
				// Yes -- set it in our array
				tunable.setValue(propertyMap.get(property));
			} else {
				// No, set the default
				setProperty(property, tunable.getValue().toString());
				setSavedProperty(property, tunable.getValue().toString());
			}
		}
	}

	/**
	 * This method returns a JPanel that represents the all of the Tunables
	 * associated with this LayoutProperties object.
	 *
	 * @return JPanel that contains all of the Tunable widgets
	 */
	public JPanel getTunablePanel() {
		JPanel tunablesPanel = new JPanel(new GridLayout(0,1));
		for (Iterator iter = tunablesList.iterator(); iter.hasNext(); ) {
			Tunable tunable = (Tunable)iter.next();
			JPanel p = tunable.getPanel();
			if (p != null) 
				tunablesPanel.add(p);
		}
		return tunablesPanel;
	}

  private String getPrefix() {
    String prefix = "layout."+propertyPrefix;
    if (prefix.lastIndexOf('.') != prefix.length())
      prefix = prefix+".";
    return prefix;
  }

}
