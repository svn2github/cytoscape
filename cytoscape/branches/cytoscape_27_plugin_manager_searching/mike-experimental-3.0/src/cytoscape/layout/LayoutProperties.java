// vim: set ts=2: */
package cytoscape.layout;

import cytoscape.CytoscapeInit;
import cytoscape.util.ModulePropertiesImpl;

import java.awt.GridLayout;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.JPanel;


/**
 * The LayoutProperties class is a helper class to support the management
 * of settings and properties for layout algorithms that implement
 * CyLayoutAlgorithm or extend AbstractLayout.  LayoutProperties objects
 * maintain a list of Tunables that are supplied by the individual
 * algorithms.  Each Tunable represents a value that should be loaded
 * from the Cytoscape properties file, and made available as a setting
 * in the LayoutSettingsDialog.  Tunables are added to the LayoutProperties
 * using the <tt>add</tt> method and are retrieved with the <tt>get</tt>
 * method.
 */
public class LayoutProperties extends ModulePropertiesImpl {
	

	/**
	 * Constructor.
	 *
	 * @param propertyPrefix String representing the prefix to be used
	 *                       when pulling properties from the property
	 *                       list.
	 */
	public LayoutProperties(String propertyPrefix) {
		super(propertyPrefix, null);
	}


	/**
	 * This method returns a JPanel that represents the all of the Tunables
	 * associated with this LayoutProperties object.
	 *
	 * @return JPanel that contains all of the Tunable widgets
	 */
	public JPanel getTunablePanel() {
		JPanel tunablesPanel = new JPanel(new GridLayout(0, 1));

		for (Iterator iter = tunablesList.iterator(); iter.hasNext();) {
			Tunable tunable = (Tunable) iter.next();
			JPanel p = tunable.getPanel();

			if (p != null)
				tunablesPanel.add(p);
		}

		return tunablesPanel;
	}

	protected String getPrefix() {
		String prefix = "layout." + propertyPrefix;

		if (prefix.lastIndexOf('.') != prefix.length())
			prefix = prefix + ".";

		return prefix;
	}
}
