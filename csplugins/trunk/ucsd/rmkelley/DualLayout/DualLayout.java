package csplugins.ucsd.rmkelley.DualLayout;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import java.awt.geom.Point2D;

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
import cytoscape.view.GraphViewController;
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
public class DualLayout extends AbstractPlugin{
    
    CyWindow cyWindow;
    
    
    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public DualLayout(CyWindow cyWindow) {
        this.cyWindow = cyWindow;
        cyWindow.getCyMenus().getOperationsMenu().add( new DualLayoutAction() );
    }
    
    /**
     * This class gets attached to the menu item.
     */
    public class DualLayoutAction extends AbstractAction {
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public DualLayoutAction() {super("Dual Layout");}
        
        /**
         * Gives a description of this plugin.
         */
        public String describe() {
            StringBuffer sb = new StringBuffer();
            sb.append("Split a compatability graph and try to lay it out");
            return sb.toString();
        }
        
	        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {
                       
	    //inform listeners that we're doing an operation on the network
       	    Thread t = new DualLayoutTask(cyWindow); 
	    t.start();
	    /*try{
	    	t.join();
	    }catch(Exception e){
		e.printStackTrace();
	    }*/
	}
    }
}

class DualLayoutTask extends Thread{
    	CyWindow cyWindow;
	private static String TITLE1 = "Split Graph";
	private static String SPLIT_STRING = "|";
	private double  springLength = 30;
  	private double  stiffness = 30;
  	//private double  electricalRepulsion = 200;
	private double electricalRepulsion = 10000;
  	private double	homologyLength = 10;
  	double increment  = .5;




    	public DualLayoutTask(CyWindow cyWindow){
		this.cyWindow = cyWindow;
	}
    	public void run(){
    	
		//get the graph view object from the window.
            	GraphView graphView = cyWindow.getView();
            	//get the network object; this contains the graph
            	CyNetwork network = cyWindow.getNetwork();
            	//can't continue if either of these is null
            	if (graphView == null || network == null) {return;}

	
    		String callerID = "DualLayout.actionPerformed";
        	network.beginActivity(callerID);
        	//this is the graph structure; it should never be null,
        	GraphPerspective graphPerspective = network.getGraphPerspective();
	

		//first make a new network in which to put the result 
		CyWindow newWindow = new CyWindow(cyWindow.getCytoscapeObj(),new CyNetwork(),TITLE1);
		//CyWindow newWindow = cyWindow;
		RootGraph newRoot = newWindow.getNetwork().getRootGraph();

	
		//turn off the graph listener	
		//GraphViewController newController = newWindow.getGraphViewController();	
		//newController.stopListening();
		//newWindow.showWindow();
		//Map from the name of a node to the node itself
		//don't use graphObjAttributes here because
		//I want to keep the left nodes separated from the right
		//nodes
		HashMap left_name2node = new HashMap();
		HashMap right_name2node = new HashMap();
		//the view of a node a vector of node views that that node
		//has an established homologous relationship with
		HashMap view2ViewVec = new HashMap();
		GraphObjAttributes nodeAttributes = network.getNodeAttributes();
		Iterator compatNodeIt = graphPerspective.nodesList().iterator();
		GraphView newView = newWindow.getView();
		GraphPerspective newPerspective = newView.getGraphPerspective();

		while(compatNodeIt.hasNext()){
			Node current = (Node)compatNodeIt.next();
			String name = nodeAttributes.getCanonicalName(current);
			String [] names = split(name,SPLIT_STRING);
			Node leftNode = (Node)left_name2node.get(names[0]);
			if(leftNode == null){
				int nodeint = newRoot.createNode();
				newPerspective.restoreNode(nodeint);
				leftNode = newPerspective.getNode(newPerspective.getNodeIndex(nodeint));
				left_name2node.put(names[0],leftNode);
			}

			Node rightNode = (Node)right_name2node.get(names[1]);
			if(rightNode == null){
				int nodeint = newRoot.createNode();
				newPerspective.restoreNode(nodeint);
				rightNode = newPerspective.getNode(newPerspective.getNodeIndex(nodeint));
				right_name2node.put(names[1],rightNode);
			}

			//have to remember the homologies here, map from
			//a node view to a vector of associated node views
		
			NodeView leftView = newView.getNodeView(leftNode);
			NodeView rightView = newView.getNodeView(rightNode);
			if(view2ViewVec.get(leftView) == null){
				view2ViewVec.put(leftView, new Vector());
			}
			Vector temp = (Vector)view2ViewVec.get(leftView);
			temp.add(rightView);

			
			if(view2ViewVec.get(rightView) == null){
				view2ViewVec.put(rightView, new Vector());
			}
			temp = (Vector)view2ViewVec.get(rightView);
			temp.add(leftView);
			
		}

		Iterator compatEdgeIt = graphPerspective.edgesList().iterator();
		while(compatEdgeIt.hasNext()){
			Edge current = (Edge)compatEdgeIt.next();
			//get the end points for the edge and add the appropriate
			//edges into the other graph
			String [] sourceSplat = split(nodeAttributes.getCanonicalName(current.getSource()),SPLIT_STRING);
			String [] targetSplat = split(nodeAttributes.getCanonicalName(current.getTarget()),SPLIT_STRING);
			
			newRoot.createEdge((Node)left_name2node.get(sourceSplat[0]),(Node)left_name2node.get(targetSplat[0]),true);
			newRoot.createEdge((Node)right_name2node.get(sourceSplat[1]),(Node)right_name2node.get(targetSplat[1]),true);

		}

		//all the stuff I've done hasn't actually been updated in the current graph perspective, I probably need to add them to my graphPerspective
		//before I can run around and do things with the view
		//first get a list of all nodes from the root graph
		
		//GraphPerspective newPerspective = newWindow.getView().getGraphPerspective();
		//int [] nodeIndices = newRoot.getNodeIndicesArray();
		//for(int i=0;i<nodeIndices.length;i++){
		//	newPerspective.restoreNode(nodeIndices[i]);
		//}

		int [] edgeIndices = newRoot.getEdgeIndicesArray();
		for(int i=0;i<edgeIndices.length;i++){
			newPerspective.restoreEdge(edgeIndices[i]);
		}
		//now we also need to associate some canonical names here or it gets upset
		//and calls everything null; acutally, it seems to get upset anyway, I give up
		//on this for right now
		//GraphObjAttributes newAttributes = newWindow.getNetwork().getNodeAttributes();
		//Iterator nameIt = left_name2int.keySet().iterator();
		//while(nameIt.hasNext()){
		//	String name = (String)nameIt.next();
		//	newAttributes.addNameMapping(name,newRoot.getNode(((Integer)left_name2int.get(name)).intValue()));
		//}
		//nameIt = right_name2int.keySet().iterator();
		//while(nameIt.hasNext()){
		//	String name = (String)nameIt.next();
		//	newAttributes.addNameMapping(name,newRoot.getNode(((Integer)right_name2int.get(name)).intValue()));
		//}
		
		
		

		//have to make sure all the display stuff is initialized here
		//so I have something to work with when it is being layed out

		//newController.resumeListening();
		//newWindow.showWindow();
		//this is the amount the two graphs should be separated by, I should probably try to change
		//this value dynamically so everything looks pretty.

		//maybe move all the right nodes over by the offset to get things startedj?
		Vector leftNodeViews = new Vector();
		Iterator leftNodeIt = left_name2node.values().iterator();
		while(leftNodeIt.hasNext()){
			leftNodeViews.add(newView.getNodeView((Node)leftNodeIt.next()));
		}
		Vector rightNodeViews = new Vector();
		Iterator rightNodeIt = right_name2node.values().iterator();
		while(rightNodeIt.hasNext()){
			Node rightNode = (Node)rightNodeIt.next();
			rightNodeViews.add(newView.getNodeView(rightNode));
		}

		//initialize positions randomly
		initializePositions(newView,leftNodeViews);
		//initializePositions(newView,rightNodeViews);
		for(int i=0;i<1000;i++){
			System.out.println(""+i);
			//(stupidly) calculate the offset
			double offset = 0;
			Iterator viewIt = leftNodeViews.iterator();
			while(viewIt.hasNext()){
				offset = Math.max(offset,((NodeView)viewIt.next()).getXPosition());
			}
			offset *=2;
			//advancePositions(newView,rightNodeViews,view2ViewVec,false,offset);
			advancePositions(newView,leftNodeViews,view2ViewVec,true,offset);
		}	
			Iterator nodeViewIt = newView.getNodeViewsIterator();
			while(nodeViewIt.hasNext()){
				((NodeView)nodeViewIt.next()).setNodePosition(true);
			}

			
		newWindow.showWindow();			
		network.endActivity(callerID);
   	}

	private void initializePositions(GraphView graphView, Vector nodeViews){
		Iterator viewIt = nodeViews.iterator();
		Random rnd = new Random();
		while(viewIt.hasNext()){
			NodeView v = (NodeView)viewIt.next();
			v.setXPosition(500*rnd.nextDouble());
			v.setYPosition(500*rnd.nextDouble());
			System.out.println(""+graphView.getNodeDoubleProperty(v.getGraphPerspectiveIndex(),GraphView.NODE_X_POSITION));

		}
	      		
	}

	/**
	 * Relaxation step. Moves all nodes a smidge.
	 */
  	public void advancePositions(GraphView graphView, Vector nodeViews,HashMap view2ViewVec, boolean left,double offset) {
    		
		for (Iterator iter = nodeViews.iterator();iter.hasNext();){
			NodeView v = ( NodeView ) iter.next();
      			double xForce = 0;
      			double yForce = 0;

      			double distance;
      			double spring;
      			double repulsion;
			double homology;
      			double xSpring = 0;
      			double ySpring = 0;
      			double xRepulsion = 0;
      			double yRepulsion = 0;
			double xHomology = 0;
			double yHomology = 0;

      			double adjacentDistance = 0;

     			
      			double thisX = v.getXPosition();
      			double thisY = v.getYPosition();
     		 	double adjX = thisX;
      			double adjY = thisY;

			//here we are going to calculate the offset to use in determining
			//the spring force between homologous nodes
			if(left){
				offset = -offset;
			}

      			int[] adjacent_nodes;
     			NodeView adjacent_node_view;
      
      			// Get the spring force between all of its adjacent vertices.
      			adjacent_nodes = graphView.getGraphPerspective().neighborsArray( v.getGraphPerspectiveIndex() );
      			for( int i = 0; i < adjacent_nodes.length; ++i ) {
        			adjacent_node_view = graphView.getNodeView( adjacent_nodes[i] );
               
	       			adjX = adjacent_node_view.getXPosition();
           			adjY = adjacent_node_view.getYPosition(); 
        			
				distance = Point2D.distance( adjX, adjY, thisX, thisY );
        			if( distance == 0 )
          			distance = .0001;
        
        			//spring = this.stiffness * ( distance - this.springLength ) *
        			//    (( thisX - adjX ) / ( distance ));
        			spring = this.stiffness * Math.log( distance / this.springLength ) * (( thisX - adjX ) / ( distance ));
        
        			xSpring += spring;
        
        			//spring = this.stiffness * ( distance - this.springLength ) *
        			//    (( thisY - adjY ) / ( distance ));
        			spring = this.stiffness * Math.log( distance / this.springLength ) *
          			(( thisY - adjY ) / ( distance ));
        
        			ySpring += spring;
        
      			}

     
      			// Get the electrical repulsion between all vertices,
      			// including those that are not adjacent.
      			for(Iterator ite = nodeViews.iterator();ite.hasNext(); ) {
        			NodeView other_v = ( NodeView )ite.next();
        
        			if( v == other_v )
          				continue;
        
        			adjX = other_v.getXPosition();
				adjY = other_v.getYPosition();
        
        			distance = Point2D.distance( adjX, adjY, thisX, thisY );
        			if( distance == 0 ){
          				distance = .0001;
				}
        
        			repulsion = ( this.electricalRepulsion / distance ) * (( thisX - adjX ) / ( distance ));
        
        			xRepulsion += repulsion;
        
        			repulsion = ( this.electricalRepulsion / distance ) * (( thisY - adjY ) / ( distance ));
        			yRepulsion += repulsion;
      			}


			//Calculate the homology attraction force, here we calculate
			//an attractive force between each node and all of the nodes it has 
			//a homology with
			/*for(Iterator ite = ((Vector)view2ViewVec.get(v)).iterator();ite.hasNext();){
				NodeView other_v = (NodeView)ite.next();
				
				adjX = other_v.getXPosition()+offset;
        			adjY = other_v.getYPosition();
       				
       				distance = Point2D.distance( adjX, adjY, thisX, thisY );
        			if( distance == 0 )
          			distance = .0001;
        
        			//spring = this.stiffness * ( distance - this.springLength ) *
        			//    (( thisX - adjX ) / ( distance ));
        			homology = this.stiffness * Math.log( distance / this.homologyLength ) * (( thisX - adjX ) / ( distance ));
        
        			xHomology += homology;
        
        			//spring = this.stiffness * ( distance - this.springLength ) *
        			//    (( thisY - adjY ) / ( distance ));
        			homology = this.stiffness * Math.log( distance / this.homologyLength ) * (( thisY - adjY ) / ( distance ));
        
        			yHomology += homology;

			}*/


      			// Combine the two to produce the total force exerted on the vertex.
      			xForce = xSpring - xRepulsion + xHomology;
      			yForce = ySpring - yRepulsion + yHomology;

      			// Move the vertex in the direction of "the force" --- thinking of star wars :-)
      			// by a small proportion
      			double xadj = 0 - ( xForce * this.increment );
      			double yadj = 0 - ( yForce * this.increment );

      			double newX = thisX + adjX;
      			double newY = thisY + adjY;
      
      			// Ensure the vertex's position is never negative.
      			if( newX >= 0 && newY >= 0 ) {
				v.setXPosition(xadj);
				v.setYPosition(yadj);
      
    	  		} else if( newX < 0 && newY >= 0 ) {
        			if( thisX > 0 ) {
          				xadj = 0 - thisX;
        			}  else { 
          				xadj = 0;
	      				v.setXPosition(xadj);
					v.setYPosition(yadj);
				}
      			} else if( newY < 0 && newX >= 0 ) {
        			if ( thisY > 0 ) {
          				yadj = 0 - thisY;
        			} else {
          				yadj = 0;
       	 			}
				v.setXPosition(xadj);
				v.setYPosition(yadj);
      			}
		}
	}

	private String [] split(String s,String split){
		String [] result = new String [2];
		int index = s.indexOf(split);
		result[0] = s.substring(0,index);
		result[1] = s.substring(index + 1,s.length());
		return result;
	}
  }

