package ucsd.rmkelley.Bigraph;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.Edge;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.model.RootGraphChangeListener;
import giny.model.RootGraphChangeEvent;

import cytoscape.AbstractPlugin;
import cytoscape.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;

/**
 * This is a sample Cytoscape plugin using Giny graph structures. For each
 * currently selected node in the graph view, the action method of this plugin
 * additionally selects the neighbors of that node if their canonical name ends
 * with the same letter. (For yeast genes, whose names are of the form 'YOR167C',
 * this selects genes that are on the same DNA strand). This operation was
 * chosen to be illustrative, not necessarily useful.
 *
 * Note that selection is a property of the view of the graph, while neighbors
 * are a property of the graph itself. Thus this plugin must access both the
 * graph and its view.
 */
public class Bigraph extends AbstractPlugin {
    
    CyWindow cyWindow;
    
    
    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public Bigraph(CyWindow cyWindow) {
        this.cyWindow = cyWindow;
        cyWindow.getCyMenus().getOperationsMenu().add( new SamplePluginAction() );
    }
    
    /**
     * This class gets attached to the menu item.
     */
    public class SamplePluginAction extends AbstractAction {
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public SamplePluginAction() {super("Generate Bigraph");}
        
        /**
         * Gives a description of this plugin.
         */
        public String describe() {
            StringBuffer sb = new StringBuffer();
            sb.append("This graph converts a graph into a bigraph");
            return sb.toString();
        }
        
	        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {
            //get the graph view object from the window.
            GraphView graphView = cyWindow.getView();
            //get the network object; this contains the graph
            CyNetwork network = cyWindow.getNetwork();
            //can't continue if either of these is null
            if (graphView == null || network == null) {return;}
            
	    //inform listeners that we're doing an operation on the network
            String callerID = "SamplePluginAction.actionPerformed";
            network.beginActivity(callerID);
            //this is the graph structure; it should never be null,
            GraphPerspective graphPerspective = network.getGraphPerspective();
            if (graphPerspective == null) {
                System.err.println("In " + callerID + ":");
                System.err.println("Unexpected null graph perspective in network");
                network.endActivity(callerID);
                return;
            }
            //and the view should be a view on this structure
            if (graphView.getGraphPerspective() != graphPerspective) {
                System.err.println("In " + callerID + ":");
                System.err.println("graph view is not a view on network's graph perspective");
                network.endActivity(callerID);
                return;
            }
	    Thread t = new BigraphThread(cyWindow);
	    t.start();
	    network.endActivity(callerID);
           
	}
    }

    class BigraphThread extends Thread{
   	CyWindow cyWindow;
	public BigraphThread(CyWindow cyWindow){
		this.cyWindow = cyWindow;
	}
    	public void run(){
	   //this is the node attributes; never null
	   GraphObjAttributes oldEdgeAttributes = cyWindow.getNetwork().getEdgeAttributes();
	   GraphObjAttributes oldNodeAttributes = cyWindow.getNetwork().getNodeAttributes();
	   String [] attributeNames = oldEdgeAttributes.getAttributeNames();
	   String attributeName = AttributeChooser.getAttribute(attributeNames);
	
	   Iterator oldEdgeIt = cyWindow.getNetwork().getRootGraph().edgesList().iterator();
	   HashMap newEdgeNodes = new HashMap();
	   HashMap newNodeNodes = new HashMap();
	   CyNetwork biCyNetwork = new CyNetwork();
	   RootGraph biRootGraph = biCyNetwork.getRootGraph();
	   GraphObjAttributes biNodeAttributes = biCyNetwork.getNodeAttributes();
	   GraphPerspective biGraphPerspective = biCyNetwork.getGraphPerspective();

	   while(oldEdgeIt.hasNext()){
	   	Edge oldEdge = (Edge)oldEdgeIt.next();
	   	if(!oldEdge.isDirected()){
			System.out.println("Undirected edge");
		}
		String attribute = (String)oldEdgeAttributes.get(attributeName,oldEdgeAttributes.getCanonicalName(oldEdge));	
	   	//if we have not seen this class of edge before, make a new edge node for it
		if(!newEdgeNodes.containsKey(attribute)){
			//create the node for this edge
			Node newNode = biRootGraph.getNode(biRootGraph.createNode());
			newEdgeNodes.put(attribute,newNode);
			biNodeAttributes.addNameMapping(attribute,newNode);
			biGraphPerspective.restoreNode(newNode);
		}

		Node newNode = (Node)newEdgeNodes.get(attribute); 	
	   	
		Node oldTarget = oldEdge.getTarget();
		String targetName = oldNodeAttributes.getCanonicalName(oldTarget);
		if(!newNodeNodes.containsKey(targetName)){
			Node newTarget = biRootGraph.getNode(biRootGraph.createNode());
			newNodeNodes.put(targetName,newTarget);
			biNodeAttributes.addNameMapping(targetName,newTarget);
			biGraphPerspective.restoreNode(newTarget);
		}
		Node newTarget = (Node)newNodeNodes.get(targetName);
		biGraphPerspective.restoreEdge(biRootGraph.getEdge(biRootGraph.createEdge(newNode,newTarget)));
	   
		Node oldSource = oldEdge.getSource();
		String sourceName = oldNodeAttributes.getCanonicalName(oldSource);
		if(!newNodeNodes.containsKey(sourceName)){
			Node newSource = biRootGraph.getNode(biRootGraph.createNode());
			newNodeNodes.put(sourceName,newSource);
			biNodeAttributes.addNameMapping(sourceName,newSource);
			biGraphPerspective.restoreNode(newSource);
		}
		Node newSource = (Node)newNodeNodes.get(sourceName);
		biGraphPerspective.restoreEdge(biRootGraph.getEdge(biRootGraph.createEdge(newSource,newNode)));
		

	   }

	   System.out.println(""+biRootGraph.getNodeCount());
	   System.out.println(""+biCyNetwork.getGraphPerspective().getNodeCount());
 	   CyWindow newCyWindow = new CyWindow(cyWindow.getCytoscapeObj(),biCyNetwork,"Bigraph");
	   newCyWindow.showWindow(); 
	   newCyWindow.setNewNetwork(biCyNetwork);  	  
	}
	
	class MyRootGraphListener implements RootGraphChangeListener{
		GraphPerspective graphPerspective;
		public MyRootGraphListener(GraphPerspective graphPerspective){
			this.graphPerspective = graphPerspective;
		}
		public void rootGraphChanged(RootGraphChangeEvent event){
			graphPerspective.restoreNodes(Arrays.asList(event.getCreatedNodes()));
			graphPerspective.restoreEdges(Arrays.asList(event.getCreatedEdges()));
		}
	}

    }

	
}

