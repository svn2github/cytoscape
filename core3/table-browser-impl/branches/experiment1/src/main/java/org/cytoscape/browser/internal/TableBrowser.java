package org.cytoscape.browser.internal;


import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.Icon;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;


@SuppressWarnings("serial")
public class TableBrowser extends JPanel implements CytoPanelComponent {
	/**
	 * Returns the Component to be added to the CytoPanel. 
	 * @return The Component to be added to the CytoPanel. 
	 */
	public Component getComponent() { return this; }

	/**
	 * Returns the name of the CytoPanel that this component should be added to.
	 * @return the name of the CytoPanel that this component should be added to.
	 */
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	/**
	 * Returns the title of the tab within the CytoPanel for this component.
	 * @return the title of the tab within the CytoPanel for this component.
	 */
	public String getTitle() { return "Table Browser"; }

	/**
	 * @return null
	 */
	public Icon getIcon() { return null; }
}