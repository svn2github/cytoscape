package cytoscape.tutorial17;


import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.AbstractLayout;
/**
 * 
 */
public class Tutorial17 extends CytoscapePlugin {

	/**
	 * 
	 */
	public Tutorial17() {
		CyLayouts.addLayout(new MyLayout(), "My Layouts");
	}
	
	class MyLayout extends AbstractLayout{
		
		
		/**
		 * These abstract methods must be overridden.
		 */
		public  void construct() {
			
		}

		/**
		 * getName is used to construct property strings
		 * for this layout.
		 */
		public  String getName() {
			return "My Layout";
		}

		/**
		 * toString is used to get the user-visible name
		 * of the layout
		 */
		public  String toString(){
			return "My Layout menu item";
		}

		
	}
	
	
}
