/*
File: BarabasiAlbertModel

References:




Author: Patrick J. McSweeney
Creation Date: 5/07/08

*/

package cytoscape.randomnetwork;

import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import giny.view.*;






public class BarabasiAlbertModel extends RandomNetworkModel   
{
	private int init_num_nodes;
	



   /**
	*	Creates a model for constructing random graphs according to the Barabasi-AlbertModel model.
	*
	*	@param pNumNodes<int> : # of nodes in Network
	*	@param pDirected<boolean> : Network is directed(TRUE) or undirected(FALSE)
    *
	*
	*/
	public BarabasiAlbertModel(int pNumNodes, boolean pDirected,int pInit)
	{
		super(pNumNodes,UNSPECIFIED,pDirected);
	
		init_num_nodes = pInit;
		
	}

   /* 
    *
	*
	*/	
	public  void Generate()
	{

	    CyNetwork random_network = Cytoscape.createNetwork("Barabasi-Albert Network");
	    CyNetworkView view = Cytoscape.createNetworkView(random_network);


	    //Create N nodes
	    CyNode[] nodes = new CyNode[numNodes];


		int degrees[] = new int[numNodes];

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


		numEdges = 0;
		for(int i = 0; i < init_num_nodes; i++)
		{
			
			for(int j = 0; j < init_num_nodes; j++)
			{
				
				
				if(random.nextDouble() <= 1)
				{
					//Create and edge between node i and node j
					CyEdge edge = Cytoscape.getCyEdge(nodes[i], nodes[j], Semantics.INTERACTION, new String("("+i +"," +j+")"), true, directed);

					//Add this edge to the network
					random_network.addEdge(edge);
					
					degrees[i]++;
					degrees[j]++;
					
					numEdges++;
				}
			
			}
		}

		for(int i = init_num_nodes; i < numNodes; i++)
		{
			
			for(int j = 0; j < i; j++)
			{
			
				double prob = ((double)degrees[i]) / ((double)(2.0d * numEdges));
			
				if(random.nextDouble() <= prob)
				{
				
					//Create and edge between node i and node j
					CyEdge edge = Cytoscape.getCyEdge(nodes[i], nodes[j], Semantics.INTERACTION, new String("("+i +"," +j+")"), true, directed);

					//Add this edge to the network
					random_network.addEdge(edge);
					
					numEdges++;
				
				}
			
			}
		}


	
	}

	public  void Compare()
	{
	
	
	}



}