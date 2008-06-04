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
	private double power;
	private int edgesToAdd;
	
   /**
	*	Creates a model for constructing random graphs according to the Barabasi-AlbertModel model.
	*
	*	@param pNumNodes<int> : # of nodes in Network
	*	@param pDirected<boolean> : Network is directed(TRUE) or undirected(FALSE)
    *
	*
	*/
	public BarabasiAlbertModel(int pNumNodes, boolean pAllowSelfEdge, boolean pDirected,int pInit, double pPower, int pEdgesToAdd)
	{
		super(pNumNodes,UNSPECIFIED,pAllowSelfEdge, pDirected);	
		init_num_nodes = pInit;		
		power = pPower;
		edgesToAdd = pEdgesToAdd;
	}

   /* 
    *
	*
	*/	
	public  void Generate()
	{

	    CyNetwork random_network = Cytoscape.createNetwork("Barabasi-Albert Network");
	    CyNetworkView view = Cytoscape.createNetworkView(random_network);
	
		long time = System.currentTimeMillis();

	    //Create N nodes
	    CyNode[] nodes = new CyNode[numNodes];


		double degrees[] = new double[numNodes];

		//For each edge
	    for(int i = 0; i < numNodes; i++)
		{
			//Create a new node nodeID = i, create = true
		    CyNode node = Cytoscape.getCyNode(time+"("+i +")", true);

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
			
			for(int j = (i+1); j < init_num_nodes; j++)
			{
				
				
				if(random.nextDouble() <= 1)
				{
					//Create and edge between node i and node j
					CyEdge edge = Cytoscape.getCyEdge(nodes[i], nodes[j], Semantics.INTERACTION, new String("("+Math.min(i,j) +"," +Math.max(i,j)+")"), true, directed);

					//Add this edge to the network
					random_network.addEdge(edge);
					
					degrees[i]++;
					degrees[j]++;
					
					numEdges++;
				}
			
			}
		}
	
		int maxDegree = 0;
		
		for(int i = init_num_nodes; i < numNodes; i++)
		{
		
			for(int m = 0; m < edgesToAdd; m++)
			{
				double prob = 0;
				double randNum = random.nextDouble();
				for(int j = 0; j < i; j++)
				{
					prob += (double)((double)degrees[j]) / ((double)(2.0d * (double)numEdges));
					
					
					if(randNum <= prob)
					{
						CyEdge edge = null;
					
						if(!directed)
						{
							//Create and edge between node i and node j
							edge = Cytoscape.getCyEdge(nodes[i], nodes[j], Semantics.INTERACTION, new String("("+Math.min(i,j) +"," +Math.max(i,j)+")"), true, directed);
						}
						else
						{
							if(random.nextDouble() < .5)
							{
								//Create and edge between node i and node j
								edge = Cytoscape.getCyEdge(nodes[i], nodes[j], Semantics.INTERACTION, new String("("+Math.min(i,j) +"," +Math.max(i,j)+")"), true, directed);	
							}
							else
							{
								//Create and edge between node i and node j
								edge = Cytoscape.getCyEdge(nodes[j], nodes[i], Semantics.INTERACTION, new String("("+Math.min(i,j) +"," +Math.max(i,j)+")"), true, directed);	
							}
						}
						

						//Add this edge to the network
						random_network.addEdge(edge);
						
						numEdges++;
						degrees[i]++;
						degrees[j]++;
					
						maxDegree = Math.max((int)degrees[i], maxDegree);
						maxDegree = Math.max((int)degrees[j], maxDegree);	
						j = i;		
						
					}
				}
			}
		}

		try
		{
		
			int pk[] = new int[numNodes];
			
			for(int i = 0; i < maxDegree; i++)
			{
				for(int j = 0; j < numNodes; j++)
				{
					if(degrees[j] >= i)
					{
						pk[i]++;
					}
				}
			}
			
			java.io.DataOutputStream dout  = new java.io.DataOutputStream(new java.io.FileOutputStream("output.dat"));
			for(int i = 0; i < maxDegree; i++)
			{
				dout.writeBytes(((double)pk[i])/((double)numNodes) + "\n");
			}
			
			dout.close();
		}catch(Exception e){e.printStackTrace();}
		

	
	
	}

	public  void Compare()
	{
	
	
	}



}