/*
File: WattsStrogatzModel

References:




Author: Patrick J. McSweeney
Creation Date: 5/07/08

*/

package cytoscape.randomnetwork;
import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.view.*;
import giny.view.*;
import cytoscape.data.*;



public class WattsStrogatzModel extends RandomNetworkModel   
{

	private double beta;
	private double degree;


   /**
	*	Creates a model for constructing random graphs according to the watts-strogatz model.
	*
	*	@param pNumNodes<int> : # of nodes in Network
	*	@param pDirected<boolean> : Network is directed(TRUE) or undirected(FALSE)
	*	@param pProbability<double> : probability of an edge
	*
	*/
	public WattsStrogatzModel(int pNumNodes, boolean pAllowSelfEdge, boolean pDirected, double pBeta, double pDegree)
	{
		super(pNumNodes,UNSPECIFIED,pAllowSelfEdge, pDirected);
	
		//TODO: Is it common practice to throw exceptions in these cases?
		//For now just force to valid range
		
		degree = pDegree;
		beta = pBeta;
	}

   /* 
	*	Generates a random graph based on the model specified by the constructor: 
	*	G(n,m) or G(n,p)
	*/	
	public  void Generate()
	{

	    CyNetwork random_network = Cytoscape.createNetwork("Watts-Strogatz Network");
	    CyNetworkView view = Cytoscape.createNetworkView(random_network);


	    //Create N nodes
	    CyNode[] nodes = new CyNode[numNodes];


		//For each edge
	    for(int i = 0; i < numNodes; i++)
		{
			//Create a new node nodeID = i, create = true
		    CyNode node = Cytoscape.getCyNode(""+i, true);

			//Add this node to the network
		    random_network.addNode(node);
			
			//Save node in array
		    nodes[i] = node;
		    
			//Create a Node view
			NodeView nv = view.addNodeView(node.getRootGraphIndex());
			
			double x_pos = random.nextDouble();
			double y_pos = random.nextDouble();
			nv.setXPosition(x_pos * 200.0d);
			nv.setYPosition(y_pos * 200.0d);

		}



	
	}

	public  void Compare()
	{
	
	
	}



}