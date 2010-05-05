import java.util.Iterator;

import cytoscape.Cytoscape;

import cytoscape.plugin.CytoscapePlugin;

import cytoscape.layout.CyLayouts;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;

import giny.view.NodeView;

import cytoscape.data.CyAttributes;

/**
 * This plugin relocates the labels of nodes in the network.
 * @author Victoria Mui
 *
 */
public class LabelPlugin extends CytoscapePlugin {
		
    /**
     * Constructor which adds this layout option to the Cytoscape Layouts menu.
     */
	public LabelPlugin() {
		CyLayouts.addLayout(new LabelPluginAlgorithm(), "Cytoscape Layouts");
	}
	
	
	public class LabelPluginAlgorithm extends AbstractLayout {

		LayoutProperties layoutProperties = null;
		
		/**
		 * Creates a new LabelPluginAlgorithm object.
		 */
	    public LabelPluginAlgorithm() {
			super();    
			layoutProperties = new LayoutProperties(getName());
			layoutProperties.initializeProperties();
	    }
		
	    /**
	     * Takes the current network and adjusts the label of each node to the
	     * desired position.
	     */
		public void construct() {
			
			// Iterator that iterates through all the node view objects of the
			// nodes in the current network
	        Iterator<NodeView> nodeViews = networkView.getNodeViewsIterator();
	        
	        // Object that allows access to a node's attributes
	        CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
        	
	        
	        // Iterate through each node and set their label position to the
	        // desired position
	        while (nodeViews.hasNext()) {
	        	NodeView current = nodeViews.next();
		        nodeAtts.setAttribute(current.getNode().getIdentifier(),
		        		"node.labelPosition","C,S,c,0,-25");
	        }
	        
	        networkView.redrawGraph(true, false);
			
		}
		
	    /** 
	     * Returns the name to construct property strings for this layout 
	     * option with */
		public String getName() {
			return "adjust-label";
		}
		
		/** Return the user-visible name of this layout option*/
		public String toString() {
			return "Adjust Label";
		}
	}
	
}