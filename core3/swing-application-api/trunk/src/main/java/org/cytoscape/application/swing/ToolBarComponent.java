package org.cytoscape.application.swing;

import java.awt.Component;
import javax.swing.Icon;


/**
 * An interface that allows a component to be registered as a service
 * that will then be added to the ToolBar.
 */
public interface ToolBarComponent {

	/**
	 * Returns the gravity used to place this component in the toolbar.
	 * @return The gravity used to place this component in the toolbar.
	 */
	float getToolBarGravity();
	
	/**
	 * Returns the Component to be added to the ToolBar. 
	 * @return The Component to be added to the ToolBar. 
	 */
	Component getComponent();

	/**
	 * Returns the name of the CytoPanel that this component should be added to.
	 * @return the name of the CytoPanel that this component should be added to.
	 */
	//CytoPanelName getCytoPanelName();

	/**
	 * Returns the title of the tab within the CytoPanel for this component.
	 * @return the title of the tab within the CytoPanel for this component.
	 */
	//String getTitle();

	/**
	 * Returns the Icon to be used along with the title in the tab for this
	 * this component. May be null!
	 * @return the Icon to be used along with the title in the tab for this
	 * this component. May be null!
	 */
	//Icon getIcon();
}