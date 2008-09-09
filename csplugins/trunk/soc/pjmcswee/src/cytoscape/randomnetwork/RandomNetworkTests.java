/* File: RandomNetworkTests.java
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.randomnetwork;

import junit.framework.TestCase;
import giny.model.*;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;

import java.util.*;
import cytoscape.*;


/**---------------------------------------------------------------------------------
 *  This code provides for JUnit tests on the RandomNetworks plugin.  This class
 *  is NOT used for completely verifying that the code correctly implements the described models.
 *
 *  This class only checks that the RandomNetworks plugin for null uses and the 
 *  some model specific parameters such as the number of nodes and edges.
 *
 *	@author Patrick J. McSweeney
 *	@version 1.0
 *---------------------------------------------------------------------------------*/
public class RandomNetworkTests extends TestCase
{

	//Messges for assert statements
	private static final String NODE_NUMBER_ERROR = new String("Wrong Number of nodes.");
	private static final String EDGE_NUMBER_ERROR = new String("Wrong Number of edges.");
	private static final String REFLEXIVE_EDGE_ERROR = new String("Reflexive edges found when not allowed.");
	private static final String DUPLICATE_EDGE_ERROR = new String("Duplicate edges found in network.");	
	private static final String EDGE_DIRECTION_ERROR = new String("Directed edge found in undirected network (or vice-versa).");	
	private static final String NULL_NETWORK = new String("Null Network detected.");
	private static final String NULL_OBJECT = new String("Null table object detected.");


	/**
 	 * Do nothing?
	 */
	public RandomNetworkTests()
	{
		
	}
	
	
	
	
	/**------------------------------------------------------------
	 *  Verify the number of nodes
	 *  @param pGraph The DynamicGraph object to check
	 *  @param pNumEdges The number of expected nodes in the graph.
	 *------------------------------------------------------------*/
	public void checkNumNodes(RandomNetwork pRandomNetwork, int pNumNodes) throws Exception
	{
		IntEnumerator nodeIterator = pRandomNetwork.nodes();
		assertEquals(NODE_NUMBER_ERROR, pNumNodes, nodeIterator.numRemaining());
	}
	
	/**------------------------------------------------------------
	 *  Verify the number of edges
	 *  @param pGraph The DynamicGraph object to check
	 *  @param pNumEdges The number of expected edges in the graph.
	 *------------------------------------------------------------*/
	public void checkNumEdges(RandomNetwork pRandomNetwork, int pNumEdges) throws Exception
	{
		IntEnumerator edgeIterator = pRandomNetwork.edges();
		assertEquals(EDGE_NUMBER_ERROR, pNumEdges, edgeIterator.numRemaining());
	}
	
	
	/**------------------------------------------------------------
	 *  Check edges for errors.  This function offers three different
	 *   forms of checking edges, not all of which will be applicable
	 *   to every type of graph, depending the parameters used for its creation.
	 *
	 *	 @param pCheckSelfEdge  Ensure there are no reflexive edges.
	 *   @param pDirected What the directionality of edges shoudl be.
	 *   @param pCheckDuplicate Ensure there are no duplicate edges
	 *   @param pGraoh The graph to check.
	 *------------------------------------------------------------*/
	public void checkEdges(RandomNetwork pRandomNetwork, boolean pCheckSelfEdge, boolean pDirected, boolean pCheckDuplicate) throws Exception
	{
		//Iterate over all of the edges
		IntEnumerator edgeIterator = pRandomNetwork.edges();
		while(edgeIterator.numRemaining() > 0)
		{
			//Get the edges
			int adjEdge = edgeIterator.nextInt();
			
			//Make sure edges have the correct directionality
			if(pDirected)
			{
				//Assert the direction is directed
				assertEquals(EDGE_DIRECTION_ERROR, pRandomNetwork.DIRECTED_EDGE,  pRandomNetwork.edgeType(adjEdge));
			}
			else
			{
				//Assert the direction is undirected
				assertEquals(EDGE_DIRECTION_ERROR, pRandomNetwork.UNDIRECTED_EDGE, pRandomNetwork.edgeType(adjEdge));
			}
			
			//Get the source  and target of this edge
			int source = pRandomNetwork.edgeSource(adjEdge);
			int target = pRandomNetwork.edgeTarget(adjEdge);
			
			//Make sure that there are no reflexive edges, if they are not allowed
			if(pCheckSelfEdge)
			{
				//assert that this is not a reflexive loop
				assertTrue(REFLEXIVE_EDGE_ERROR,source != target);
			}
			
			//If we should check for duplicate edges
			if(pCheckDuplicate)
			{
				//Iterate over the rest of the edges
				IntEnumerator duplicateIterator = pRandomNetwork.edges();
				while(duplicateIterator.numRemaining() > 0)
				{
					//Get the next edge
					int edge = duplicateIterator.nextInt();
					
					//Don't check  and edge against itself
					if(edge != adjEdge)
					{
						//Get the source and target
						int dupSource = pRandomNetwork.edgeSource(edge);
						int dupTarget = pRandomNetwork.edgeTarget(edge);
						
						//Assert that both the sources and targets of the two edges do not match
						assertTrue(DUPLICATE_EDGE_ERROR, !((source == dupSource)&& (target == dupTarget)));
					}
				}
			}
		}
	}
	
	
	
	/**------------------------------------------------------------
	 *  Probably not necssary as a function, but keeps checking done in sub-functions.
	 * @param pGraph the graph to check if null
	 *------------------------------------------------------------*/
	public void checkNetwork(RandomNetwork pRandomNetwork)
	{
		assertNotNull(NULL_NETWORK, pRandomNetwork);
	}
	
	
	/**------------------------------------------------------------
	 * Checks a table of objects and its contents to make sure there
	 * is no null pointer.
	 * @param pData The table to check for null entries.
	 *------------------------------------------------------------*/	
	public void checkTable(Object [][] pData)
	{	
		//Make sure the table itself is not null
		assertNotNull(NULL_OBJECT, pData);
		
		int x = pData.length;
		int y = pData[0].length;
	
		//Check each object to see if it is null
		for(int i = 0; i < x; i++)
		{
			for(int j = 0; j < y; j++)
			{
				assertNotNull(NULL_OBJECT, pData[i][j]);
			}
		}
	}
	
	/**------------------------------------------------------------
	 * Checks a table of objects and its contents to make sure there
	 * is no null pointer.
	 * @param pData The table to check for null entries.
	 *------------------------------------------------------------*/	
	public void checkConverts(CyNetwork pNetwork, RandomNetwork pRandomNetwork, boolean pDirected)
	{	
		//Make sure the network is not null
		assertNotNull(NULL_OBJECT, pNetwork);
		assertNotNull(NULL_OBJECT, pRandomNetwork);		
		
		//Make sure the number of nodes is the same
		assertEquals(NODE_NUMBER_ERROR, pNetwork.getNodeCount() ,pRandomNetwork.getNumNodes());
		
		//Make sure the number of edges is the same, only if the DynamicGraph is directed
		//As edges may be compressed if undirected
		if(pDirected)
		{
			assertEquals(EDGE_NUMBER_ERROR, pNetwork.getEdgeCount() , pRandomNetwork.getNumEdges());
		}
	}
		
		
	
	/**------------------------------------------------------------
	 *  Test the Watts strogatz model for errors.
	 *------------------------------------------------------------*/
	public void testWattsStrogatzModel() throws Exception
	{
		int nodes = 50;
		double beta = .5;
		int degree = 2;
		boolean allowReflexive = false;
		boolean directed = false;
		double probability = .3;
		
		WattsStrogatzModel wsm = new WattsStrogatzModel(nodes, allowReflexive, directed, beta, degree);
		RandomNetwork random_network = wsm.generate();
	
		int edges = 2 * degree * nodes;
		
		if(!directed)
		{
			edges /= 2.0;
		}	
		checkNetwork(random_network);
		checkNumEdges(random_network, edges);
		checkNumNodes(random_network, nodes);
		checkEdges(random_network, !allowReflexive, directed, true);		

	}
	
	
	
	
	/**------------------------------------------------------------
	 *  Test the Barabasi albert model for errors.
	 *------------------------------------------------------------*/
	public void testBarabasiAlbertModel() throws Exception
	{
				
		//Model specific variabels
		int nodes = 50;
		int degree = 2;
		boolean allowReflexive = false;
		boolean directed = false;
		int init = 3;
		int edges = ((init)*(init-1)/2 + (degree * (nodes - init)));
		
		//Create the model
		BarabasiAlbertModel bam = new BarabasiAlbertModel(nodes, allowReflexive, directed,  init, degree);
		
		//generate the graph
		RandomNetwork random_network = bam.generate();
	
		//Run all checks
		checkNetwork(random_network);
		checkNumEdges(random_network, edges);
		checkNumNodes(random_network, nodes);
		checkEdges(random_network, !allowReflexive, directed, true);		

	}
	
	
	/**------------------------------------------------------------
	 *  Test the Erdos Renyi strogatz model for errors.
	 *------------------------------------------------------------*/
	public void testErdosRenyiModel_GNP() throws Exception
	{
	
		//Model specific variables
		int nodes = 50;
		boolean allowReflexive = false;
		boolean directed = false;
		double probability = .3;
		
		//create teh model
		ErdosRenyiModel erm = new ErdosRenyiModel(nodes, probability, allowReflexive, directed);
		
		//generate the graph
		RandomNetwork random_network = erm.generate();
		
		//run the checks
		checkNetwork(random_network);
		checkNumNodes(random_network, nodes);
		checkEdges(random_network, !allowReflexive, directed, true);		
	}
	
	
	/**------------------------------------------------------------
	 *  Test the Erdos Renyi strogatz model for errors.
	 *------------------------------------------------------------*/
	public void testErdosRenyiModel_GNM() throws Exception
	{
		//model specific variables
		int nodes = 50;
		boolean allowReflexive = false;
		boolean directed = false;
		int edges = 100;
		
		//Create the model
		ErdosRenyiModel erm = new ErdosRenyiModel(nodes, edges, allowReflexive, directed);
		
		//Generate the graph
		RandomNetwork random_network = erm.generate();

		//Run the checks
		checkNetwork(random_network);
		checkNumEdges(random_network,edges);
		checkNumNodes(random_network, nodes);
		checkEdges(random_network, !allowReflexive, directed, true);		
	}
	


	
	/**-------------------------------------------------------------------------
	 *  Check the randomization method
	 *-------------------------------------------------------------------------*/
	public void testRandomizeNetwork() throws Exception
	{
	
		int nodes = 100;
		int degree = 1;
		boolean allowReflexive = false;
		boolean directed = true;
		int init = 3;
		int edges = ((init)*(init-1)/2 + (degree * (nodes - init)));
		int shuffles = edges * 4;
		
		//Create the model
		BarabasiAlbertModel bam = new BarabasiAlbertModel(nodes, allowReflexive, directed,  init, degree);
		
		//generate the graph
		RandomNetwork random_network = bam.generate();
		
		//Creat the randomizer model
		DegreePreservingNetworkRandomizer drps = new DegreePreservingNetworkRandomizer(random_network, directed, shuffles);
		
		//System.out.println("TESTING");
	
		//randomize the graph
		RandomNetwork shuffled_network = drps.generate();


		//run all checks
		checkNetwork(shuffled_network);
		checkNumNodes(shuffled_network, nodes);
		checkNumEdges(shuffled_network, edges);
		checkEdges(shuffled_network, !allowReflexive, directed, true);
	}
	
	/**-------------------------------------------------------------------------
	 *  Check the  Comparison method for errors
	 *-------------------------------------------------------------------------*/
	public void testComparison() throws Exception
	{
	
		//model specific variables
		int nodes = 100;
		int degree = 1;
		boolean allowReflexive = false;
		boolean directed = true;
		int init = 3;
		int edges = ((init)*(init-1)/2 + (degree * (nodes - init)));
		int threads = Runtime.getRuntime().availableProcessors();
		int rounds = 50;
		int shuffles =edges * 4;
		
		
		//Create the model
		BarabasiAlbertModel bam = new BarabasiAlbertModel(nodes, allowReflexive, directed,  init, degree);
		
		//generate the graph
		RandomNetwork random_network = bam.generate();

		//Creat the randomizer model
		DegreePreservingNetworkRandomizer drps = new DegreePreservingNetworkRandomizer(random_network, directed, shuffles);

		//Create the metrics and add them to a list
		ClusteringCoefficientMetric coef = new ClusteringCoefficientMetric();
		AverageDegreeMetric ad = new AverageDegreeMetric();
		DegreeDistributionMetric dd = new DegreeDistributionMetric();
		MeanShortestPathMetric msp = new MeanShortestPathMetric();
		LinkedList list = new LinkedList();
		list.add(ad);
		list.add(dd);
		list.add(msp);
		list.add(coef);
		
		
		//Create teh analyzer
		RandomNetworkAnalyzer rna = new RandomNetworkAnalyzer(list, random_network, drps, directed, threads,rounds);
		
		//Run the analyzer
		rna.run();

		//Get the results
		Object [][] data = rna.getData();
		
		//Check the talbe for null
		checkTable(data);
		
	}
	
	/**------------------------------------------------------------------------------
	 * Check the conversion methods
	 *------------------------------------------------------------------------------*/	
	public void testCytoscapeConversions() throws Exception
	{
		//model specific variables
		int nodes = 100;
		int degree = 1;
		boolean allowReflexive = false;
		boolean directed = true;
		int init = 3;
		int edges = ((init)*(init-1)/2 + (degree * (nodes - init)));
		
		
		//Create the model
		BarabasiAlbertModel bam = new BarabasiAlbertModel(nodes, allowReflexive, directed,  init, degree);
		
		//generate the graph
		RandomNetwork random_network = bam.generate();
		
		CyNetwork cynetwork = random_network.toCyNetwork();
		
		checkConverts(cynetwork, random_network, directed);
		
		RandomNetwork randGraph = new RandomNetwork(cynetwork,directed);
		
		
		
		checkConverts(cynetwork,randGraph, directed);
		
		//Run all checks
		checkNetwork(randGraph);
		checkNumEdges(randGraph, edges);
		checkNumNodes(randGraph, nodes);
		checkEdges(randGraph, !allowReflexive, directed, true);		
	}
}
		

	
