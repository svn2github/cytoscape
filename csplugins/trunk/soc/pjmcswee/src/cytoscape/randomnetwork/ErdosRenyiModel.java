/*
File: RandomNetworkPlugin

References:
Erdos, P.; Renyi A. (1959). "On Random Graphs. I.".  Publications Matheaticae 6: 290-297.
Erdos, P.; Renyi A. (1960). "The Evolution of Random Graphs".  Magyar Tud. Akad. Math. Kutato INt. Koxl. 5:17-61.
Gilber, E.N. (1959). "Random Graphs".  Annals of Mathematical Statistics 30: 1141 - 1144.



Author: Patrick J. McSweeney
Creation Date: 5/07/08

*/

package cytoscape.randomnetwork;

import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.view.*;
import giny.view.*;
import cytoscape.data.*;
import java.util.*;



public class ErdosRenyiModel extends RandomNetworkModel   
{
	private double probability;

   /**
	*	Creates a model for constructing random graphs according to the erdos-renyi model.
	*   This constructor will create random graphs with a given number of edges. 
	*	Each call to generate will create networks with the specified number of edges.
	*	G(n,m) model
	*
	*	@param pNumNodes<int> : # of nodes in Network
	*	@param pNumEdges<int> : # of edges in Network
	*	@param pDirected<boolean> : Network is directed(TRUE) or undirected(FALSE)
	*
	*/
	public ErdosRenyiModel(int pNumNodes, int pNumEdges, boolean pAllowSelfEdge, boolean pDirected)
	{
		super(pNumNodes,pNumEdges, pAllowSelfEdge, pDirected);
		probability = UNSPECIFIED;
	}

   /**
	*	Creates a model for constructing random graphs according to the erdos-renyi model.
	*   This constructor will create random graphs with a given probability.  So that
	*	each call to generate can create networks with a different number of edges.
	*   G(n,p)
	*
	*	@param pNumNodes<int> : # of nodes in Network
	*	@param pDirected<boolean> : Network is directed(TRUE) or undirected(FALSE)
	*	@param pProbability<double> : probability of an edge
	*
	*/
	public ErdosRenyiModel(int pNumNodes,boolean pAllowSelfEdge, boolean pDirected, double pProbability)
	{
		super(pNumNodes,UNSPECIFIED,pAllowSelfEdge, pDirected);
	
		//TODO: Is it common practice to throw exceptions in these cases?
		//For now just force to valid range
		if(probability < 0d)
		{
			 probability = 0d;
		}
		if(probability > 1.0d)
		{
			probability = 1.0d;
		}
		
		probability = pProbability;
	}

   /* 
	*	Generates a random graph based on the model specified by the constructor: 
	*	G(n,m) or G(n,p)
	*/	
	public  void Generate()
	{

	    CyNetwork random_network = Cytoscape.createNetwork("Erdos-Renyi network");
	    CyNetworkView view = Cytoscape.createNetworkView(random_network);

		System.out.println(random_network);
	    //Create N nodes
	    CyNode[] nodes = new CyNode[numNodes];

		//Time t = new Time();
		long time = System.currentTimeMillis();

		//For each edge
	    for(int i = 0; i < numNodes; i++)
		{
			//Create a new node nodeID = i, create = true
		    CyNode node = Cytoscape.getCyNode(time +"("+i +")", true);

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
		
		//If we are creating a random network with a specified number of edges
		//G(n,m) Model
		if(probability == UNSPECIFIED) 
		{
			//System.out.println("old:" + numEdges );
			if(directed)
			{
				if(allowSelfEdge)
				{
					numEdges = Math.min(numEdges, numNodes * numNodes);
				}
				else
				{
					numEdges = Math.min(numEdges, numNodes * (numNodes - 1));
				}
			}
			
			else
			{
				if(allowSelfEdge)
				{
					numEdges = Math.min(numEdges, (int)((numNodes * (numNodes - 1))/2.0) + numNodes);
				}
				else
				{
					numEdges = Math.min(numEdges, (int)((numNodes * (numNodes - 1))/2.0));
				}
			
			}
			
			/*
			LinkedList edges = new LinkedList();
			
			for(int i = 0; i < numNodes; i++)
			{
			
				int start = 0;
				if(!directed)
				{
					start = i;
				}
				
				for(int j = start; j < numNodes; j++)
				{
					if((i != j) || (allowSelfEdge))
					{
						Integer pair = new Integer(i * numNodes + j);
						edges.addLast(pair);
					}
					
				}
			}
			
			java.util.Collections.shuffle(edges,random);
			//System.out.println("new:" + numEdges );

			for(int i = 0; i < numEdges; i++)
			{
				int index = ((Integer)edges.removeFirst()).intValue();
				
				int source = index / numNodes;
				int target = index % numNodes;
				
				
				
				//Check to see if this edge already exists
				CyEdge edge = Cytoscape.getCyEdge(nodes[source], nodes[target], Semantics.INTERACTION, new String(time + "("+source +"," +target+")"), true, directed);
				//Add this edge to the network
				random_network.addEdge(edge);
					
			
			}
			
			*/
			
		
			for(int i = 0; i < numEdges; i++)
			{
				//Select two nodes (source and target only apply if directed)
				int source = Math.abs(random.nextInt()) % numNodes;
				int target = Math.abs(random.nextInt()) % numNodes;
				
				
				
				//Check to see if this edge already exists
				CyEdge check = Cytoscape.getCyEdge(nodes[source], nodes[target], Semantics.INTERACTION, new String(time + "("+Math.min(source,target) +"," +Math.max(source,target)+")"), false, directed);
				
				
				
				//System.out.println(source + "\t" + target);
				
				int higher = source * numNodes + target + 1;
				int  lower = source * numNodes + target - 1;
				
				//If this edge already exists ... I hope this method won't take too long... we may need a faster way of checking...?
				while((check != null) || ((!allowSelfEdge) && (source == target)))
				{		
					if(lower < 0)
					{
						lower = (numNodes * numNodes - 1);
					}
					if(higher == numNodes * numNodes)
					{
						higher = 0;
					}
				
					int source_lo = lower / numNodes;
					int target_lo = lower % numNodes;
					
					int source_hi = higher /numNodes;
					int target_hi = higher % numNodes;
					
					//System.out.println(i+ " low:  " +source_lo +"\t" + target_lo + "\t" + lower);
					//System.out.println(i+ " High: " +source_hi +"\t" + target_hi + "\t" + higher);
					
					if(((allowSelfEdge) && (source_lo == target_lo)) ||(source_lo != target_lo))
					{
						check = Cytoscape.getCyEdge(nodes[source_lo], nodes[target_lo], Semantics.INTERACTION,new String(time + "("+Math.min(source_lo,target_lo) +"," +Math.max(source_lo,target_lo)+")"), false, directed);
					
						if(check == null)
						{
							source = source_lo;
							target = target_lo;
							break;
						}
					}
					
					
					if(((allowSelfEdge) && (source_hi == target_hi)) ||(source_hi != target_hi))
					{
						check = Cytoscape.getCyEdge(nodes[source_hi], nodes[target_hi], Semantics.INTERACTION, new String(time + "("+Math.min(source_hi,target_hi) +"," +Math.max(source_hi,target_hi)+")"), false, directed);
					
						if(check == null)
						{
							source = source_hi;
							target = target_hi;
							break;
						}
					}


					higher++;
					lower--;

				}					
				
			
				
				//Create and edge between node i and node j
				CyEdge edge = Cytoscape.getCyEdge(nodes[source], nodes[target], Semantics.INTERACTION,new String(time + "("+Math.min(source,target) +"," +Math.max(source,target)+")"), true, directed);

				

				//Add this edge to the network
				random_network.addEdge(edge);
				
					
				
			}
		}
		//G(n,p) Model
		else
		{
			//TODO:  This algorithm runs in N^2 time.  With millions of edges this is not optimal.
			//A faster algorithm will randomly select a number of edges 'm' based on p and then do the G(n,m) model
		
		
			//For each node
			for(int i = 0; i < numNodes; i++)
			{
				int start = 0;
				if(!directed)
				{
					start = i + 1;
					if(allowSelfEdge)
					{
						start = i;
					}
				}
				

				//For every other node
				for(int j = start; j < numNodes; j++)
				{
					if((!allowSelfEdge) && (i == j))
					{
						continue;
					}
				
					//If random indicates this edge exists
					if(random.nextDouble() <= probability)
					{
					
						//Create and edge between node i and node j
						CyEdge edge = Cytoscape.getCyEdge(nodes[i], nodes[j], Semantics.INTERACTION, new String("("+i +"," +j+")"), true, directed);

						//Add this edge
						random_network.addEdge(edge);
					}
				}
			}

		}
	
	
	}

	public  void Compare()
	{
	
	
	}



}