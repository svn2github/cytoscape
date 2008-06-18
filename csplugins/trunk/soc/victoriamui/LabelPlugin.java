import java.util.Iterator;
import java.util.Set;

import cytoscape.CyNode;

import cytoscape.plugin.CytoscapePlugin;

import cytoscape.layout.CyLayouts;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;

import cytoscape.visual.LabelPosition;
import cytoscape.visual.Appearance;
import cytoscape.visual.VisualPropertyType;

import giny.view.Label;
import giny.view.NodeView;

/**
 *
 * @author Victoria Mui
 *
 */
public class LabelPlugin extends CytoscapePlugin {
		
	public LabelPlugin() {
		CyLayouts.addLayout(new LabelPluginAlgorithm(), "Cytoscape Layouts");
	}
	
	public class LabelPluginAlgorithm extends AbstractLayout {
		
		// The set of all nodes in the current graph
	    private Set<CyNode> cyNodes = null; 
	    
	    // Iterator over all the nodes in the current graph
	    private Iterator<CyNode> nodeIt = null;
		
		private LabelPosition lp = null;
		
		LayoutProperties layoutProperties = null;
		
		
	    public LabelPluginAlgorithm() {
			super();    
			layoutProperties = new LayoutProperties(getName());
			layoutProperties.initializeProperties();
			
			//LabelPosition lp = (LabelPosition) Appearance.this.get(VisualPropertyType.NODE_LABEL_POSITION);
			Appearance app = new Appearance();
			this.lp = (LabelPosition) app.get(VisualPropertyType.NODE_LABEL_POSITION);
			
	    }
		
		public void construct() {
			System.out.println("Hello world!!");
			
	        // Get the set of nodes in network and store in nodes
	        network.selectAllNodes();
	        cyNodes = network.getSelectedNodes();
	        nodeIt = cyNodes.iterator();
	        
	        Iterator<NodeView> nodeViews = networkView.getNodeViewsIterator();
			
	        while (nodeViews.hasNext()) {
	        	NodeView current = nodeViews.next();
	        	System.out.println("Label X Offset: " + current.getLabelOffsetX());
	        	System.out.println("Label Y Offset: " + current.getLabelOffsetY());
	        	
//	        	current.setNodeLabelAnchor(Label.NORTH);
//	        	current.setNodeLabelAnchor(Label.NORTHEAST);
	        	current.setNodeLabelAnchor(Label.EAST);
//	        	current.setNodeLabelAnchor(Label.SOUTHEAST);
//	        	current.setNodeLabelAnchor(Label.SOUTH);
//	        	current.setNodeLabelAnchor(Label.SOUTHWEST);
//	        	current.setNodeLabelAnchor(Label.WEST);
//	        	current.setNodeLabelAnchor(Label.NORTHWEST);
	        	
	        	current.setLabelOffsetX(100);
	        	//current.setLabelOffsetY(100);
	        	
	        	System.out.println("Label X Offset: " + current.getLabelOffsetX());
	        	System.out.println("Label Y Offset: " + current.getLabelOffsetY());
	        	System.out.println("======================================");
	        }
			
		}
		
		public String getName() {
			return "adjust-label";
		}
		
		public String toString() {
			return "Adjust Label";
		}
		
	}
	
}