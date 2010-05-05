import cytoscape.plugin.CytoscapePlugin;

import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import cytoscape.layout.CyLayouts;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.CyNode;

import giny.view.NodeView;

/**
 * Takes the current network of nodes and edges, and rearranges the nodes
 * so that they are in a grid layout.
 * @author Victoria Mui
 *
 */
public class MyGridLayout extends CytoscapePlugin {
	
	// The set of all nodes in the current graph
    private Set<CyNode> nodes = null; 
    
    // Iterator over all the nodes in the current graph
    private Iterator<CyNode> nodeIt = null;
    
    // Maps Node objects to NodeView objects.
    // Used when rearranging the nodes to place into a graph layout.
    private Map<MyGridLayoutAlgorithm.Node, NodeView> nodeMap 
    	= new HashMap<MyGridLayoutAlgorithm.Node, NodeView>();
    
    /**
     * Constructor which adds this layout to Cytoscape Layouts.  This in turn
     * adds it to the Cytoscape menus as well.
     */
	public MyGridLayout() {
		// Add this layout to the Layout menu under Cytoscape Layouts.
		CyLayouts.addLayout(new MyGridLayoutAlgorithm(), "Cytoscape Layouts");
	}
	
	
	/** The Grid Layout Algorithm for MyGridLayout. */
	public class MyGridLayoutAlgorithm extends AbstractLayout {
		
		// The distance between adjacent nodes
		private final double DISTANCE = 70.0;
		
		LayoutProperties layoutProperties = null;
	    
	    /** 
	     * Constructs a new grid layout algorithm object and initializes all
	     * necessary values.
	     */
	    public MyGridLayoutAlgorithm() {
			super();    
			layoutProperties = new LayoutProperties(getName());
			layoutProperties.initializeProperties();
	    }
	    
	    /**
	     * Constructs the grid layout.  Nodes can be of different sizes.
	     */
	    public void construct() {
	    	
	        // Get the set of nodes in network and store in nodes
	        network.selectAllNodes();
	        nodes = network.getSelectedNodes();
	        nodeIt = nodes.iterator();
	        
	        int columns = 0, rows = 0;
	        double widest = 0; // The width of the widest node
	        double tallest = 0; // The height of the tallest node
	        double startX = 0, startY = 0;
	        int numNodes = networkView.nodeCount();
	        
	        Iterator<NodeView> nodeViews = networkView.getNodeViewsIterator();
	        
	        /* Traverse through all the nodes in the graph and add them to
	         * nodeMap if they are not already in there.  The Node objects are
	         * the keys of nodeMap, and the corresponding NodeView objects are
	         * the values. */
	        while (nodeIt.hasNext()) {
	        	CyNode thisNode = nodeIt.next();
	        	NodeView thisView = nodeViews.next();
	        	
	        	if (!nodeMap.containsValue(thisView)) {
	        		
		        	// Create a new Node for this current node.
		        	Node current = new Node(thisNode.getIdentifier(), 
		        			thisView.getXPosition(), thisView.getYPosition(), 
		        			thisView.getWidth(), thisView.getHeight());
		        	nodeMap.put(current, thisView);
	        	}
	        } 
	        
	        // Finding the number of rows and columns that are needed
	        columns = (int) Math.ceil(Math.sqrt(numNodes));
	        rows = (int) Math.ceil(numNodes * 1.0 / columns);
	        
	        // Finding the widest and tallest nodes
	        nodeViews = networkView.getNodeViewsIterator();
	        while(nodeViews.hasNext()) {
	        	NodeView nv = nodeViews.next();
	        	if (nv.getWidth() > widest) {
	        		widest = nv.getWidth();
	        	}
	        	if (nv.getHeight() > tallest) {
	        		tallest = nv.getHeight();
	        	}
	        }
	        
	        /* Calculating the starting point for the first node. Namely,
	         * the left-most node on the first row from the top. */
	        startX = 0 - ((widest * columns + DISTANCE * (columns - 1)) / 2);
	        startY = 0 - ((tallest * rows + DISTANCE * (rows - 1)) / 2);
	        
	        // Iterate through the nodes and give them new locations
	        Set<Node> keys = nodeMap.keySet();
	        keys = nodeMap.keySet();
	        Iterator<Node> nodes = keys.iterator();
	        for(int r = 0; r < rows; r++) {
	        	for(int c = 0; c < columns; c++) {
	        		if (nodes.hasNext()) {
	        			Node thisNode = nodes.next();
		        		NodeView thisView = nodeMap.get(thisNode);
		        		thisView.setOffset(startX 
		        				+ (c * DISTANCE) 
		        				+ (c * widest), 
		        				startY 
		        				+ (r * DISTANCE) 
		        				+ (r * tallest));
		        		thisNode.setX(startX + (c * DISTANCE) + (widest / 2));
		        		thisNode.setY(startY + (r * DISTANCE) + (tallest / 2));
	        		}
	        	}
	        }
	        
	    } //end construct()
	    
	    /** 
	     * Returns the name to construct property strings 
	     * for this layout with */
	    public String getName() {
			return "victoria-grid-layout";
		}
	    
		/** Return the user-visible name of this layout */
		public String toString() {
			return "Victoria's Grid Layout";
		}
		
		public class Node {
			private String name = null;
			private double xCoordinate = Double.POSITIVE_INFINITY;
			private double yCoordinate = Double.POSITIVE_INFINITY;
			private double width = Double.POSITIVE_INFINITY;
			private double height = Double.POSITIVE_INFINITY;
			
			public Node(String name, double x, double y, double width, double height) {
				this.name = name;
				this.xCoordinate = x;
				this.yCoordinate = y;
				this.width = width;
				this.height = height;
			}
			
			public double getWidth() {
				return this.width;
			}
			
			public double getHeight() {
				return this.height;
			}
			
			public double getX() {
				return xCoordinate;
			}
			
			public double getY() {
				return yCoordinate;
			}
			
			public String getName() {
				return this.name;
			}
			
			public void setX(double x) {
				this.xCoordinate = x;
			}
			
			public void setY(double y) {
				this.yCoordinate = y;
			}
			
			public void setName(String name) {
				this.name = name;
			}
			
			public String toString() {
				return "Name: " + this.name + "; X: " 
				+ xCoordinate + "; Y: " + yCoordinate + "\n";
			}
		}
		
	}
}