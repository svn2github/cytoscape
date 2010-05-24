
//============================================================================
// 
//  file: LogLikelihoodScoreModel.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================



package nct.networkblast.score;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import nct.graph.Graph;
import nct.graph.Edge;

/**
 * This class implements the log likelihood edge scoring scheme described in
 * the supplemental to Sharan, et al., 2005, Conserved patterns of protein
 * interaction in multiple species, PNAS, 102(6).
 */
public class LogLikelihoodScoreModel<NodeType extends Comparable<? super NodeType>> implements ScoreModel<NodeType,Double> {

    /**
     * The background probability to be used in cases where the distance
     * between two nodes is too great (>= 3)
     */
    private double backgroundProbability;

    /**
     * Factor to multiply the predicted probabilities by to obtain the 
     * background truth distribution. Arbitrary value.
     */
    private double truthFactor;

    /**
     * Truth of the model. P(truth|model) refered to as Beta in paper.
     */
    private double modelTruth;

    /**
     * Map to hold each graph's respective probabilities.
     */
    Map<Graph<NodeType,Double>, GraphProbs> graphMap;

    /**
     * Map to hold each graph's node's expected degreees. 
     */
    private Map<Graph<NodeType,Double>, Map<NodeType,Double>> exNodeDegrees;

    /**
     * The logger object we will log to.
     */
    private static Logger log = Logger.getLogger("networkblast");

    /**
     * Used to indicate whether its ok to log or not. 
     */
    private boolean logFineOk;

    /**
     * Sets the truthFactor accordingly (ensures > 0) and initializes the 
     * graphMap object.
     * @param truthFactor factor used to consider the background truth distribution
     * @param model Truth for the model (assumed between 0 and 1)
     */
    public LogLikelihoodScoreModel(double truthFactor, double model, double backgroundProbability) {
	graphMap = new Hashtable<Graph<NodeType,Double>, GraphProbs>();
	exNodeDegrees = new HashMap<Graph<NodeType,Double>,Map<NodeType,Double>>();
	this.truthFactor = truthFactor;
	modelTruth = model;
	this.backgroundProbability = backgroundProbability;
	assert(truthFactor > 0);  // otherwise everything is always false
	assert(model >= 0 && model <= 1);
/*	// TODO
	int l1 = log.getLevel().intValue();
	int l2 = Level.FINE.intValue();
	if (log.getLevel().intValue() <= Level.FINE.intValue()) 
		logFineOk = true;
	else 
	*/
		logFineOk = false;
	
    }

    /**
     * A method that calculates the necessary probabilities for the given graph.
     * @param graph The graph to calculate the probabilities for.
     */
    private void scoreGraph( Graph<NodeType,Double> graph) {
	assert(graph != null);

	if (!graphMap.containsKey(graph)) {  // set up the probabilities
	    GraphProbs probs = new GraphProbs();
	    Integer val;
	    double totalEdgeProbability = 0.0;
	    for (Edge<NodeType,Double> edge: graph.getEdges()) 
		totalEdgeProbability += edge.getWeight().doubleValue();
	    int numOfNodes = graph.numberOfNodes();

	    log.info("truthFactor " + truthFactor);
	    log.info("totalEdgeProbability " + totalEdgeProbability);
	    log.info("number of nodes " + numOfNodes);

	    // there are n choose 2 possible edges for a graph with n nodes
	    double numPossibleEdges = (numOfNodes * (numOfNodes - 1))/2.0; 

	    // See GraphProbs class for further descrptions of these variables.
	    //
	    // probability of truth - the probability that an edge represents a true interaction
	    // truthFactor is an arbitrary fudge factor.  Use wisely!
	    // P(T)
	    probs.pTrue = (totalEdgeProbability * truthFactor) / numPossibleEdges; 

	    // probability of observed - the probability that an edge was observed in this graph
	    // P(O)
	    probs.pObs = graph.numberOfEdges() / numPossibleEdges; 

	    // probability of truth given not observed 
	    // (what we haven't seen that is true/what we haven't seen)
	    // ( P(T) - P(T|O) ) / ( 1 - P(O) )
	    probs.pTrueGivenNotObs = (probs.pTrue - (probs.pObs * totalEdgeProbability / graph.numberOfEdges())) / (1 - probs.pObs);

	    double sum = 0.0;
	    for (NodeType node: graph.getNodes()) 
		sum += (numOfNodes - graph.degreeOfNode(node) - 1) * probs.pTrueGivenNotObs;
	    
	    // divide by two because each edge is accounted for twice, once for each node 
	    sum /= 2.0;   
	
	    // expected number of interactions in the graph
	    probs.exNumInt = Math.round(sum + totalEdgeProbability); 
	    graphMap.put(graph, probs);	    
	} 

	if ( !exNodeDegrees.containsKey(graph) )
		exNodeDegrees.put(graph,new HashMap<NodeType,Double>());

    }

    /**
     * For the two nodes from the specified graph graph, return the log
     * likelihood score between the complex model and the null model. 
     * @param srcNode node bewteen which it and destNode generates a score
     * @param destNode node between which it and srcNode generates a score
     * @param graph graph containing both srcNode and destNode
     * @return the score of the pathway between the nodes or 0 if the same node
     */
    public double scoreEdge(NodeType srcNode, NodeType destNode, Graph<NodeType,Double> graph) {

	assert(srcNode != null && destNode != null && graph != null);

	if ( graph == null )
		return Double.MIN_VALUE; 

	assert(graph.isNode(srcNode) && graph.isNode(destNode));

	if (srcNode == destNode) 
	    return 0.0; // is this right?
	
	if ( !graph.isEdge(srcNode,destNode) )
		return 0.0; // is this right?

	// ok, now actually start doing stuff
	if (!graphMap.containsKey(graph)) 
		scoreGraph(graph);

    	GraphProbs probs = graphMap.get(graph);
	int numOfNodes = graph.numberOfNodes();

	// formula from paper
	double weight = graph.getEdgeWeight(srcNode, destNode);
	double exDegSrcNode = calcExpectedNodeDegree(srcNode,graph); 
	double exDegDestNode = calcExpectedNodeDegree(destNode,graph); 
	double nullTruth = 1.0 / (1 + (2 * (Math.round(probs.exNumInt - exDegSrcNode - exDegDestNode + 1)) / (exDegSrcNode * exDegDestNode))); 

	double complexModel = modelTruth * weight * (1 - probs.pTrue) + (1 - modelTruth) * (1 - weight) * probs.pTrue;
	double nullModel = nullTruth * weight * (1 - probs.pTrue) + (1 - nullTruth) * (1 - weight) * probs.pTrue;

	// On the off chance that complexModel and nullModel are both negative, 
	// this method still works, while Math.log(complexModel) - Math.log(nullModel) doesn't.
	double ret =  Math.log(complexModel/nullModel); 

	// If we don't do this check, then log.fine will actually run every time
	// even if the level is > fine.  The problem is that all of the strings
	// are created as part of this and for as often as this method is called, 
	// that is a lot of work.
	if ( logFineOk ) {
		log.info(srcNode + "   " + destNode);
		log.info(probs.toString());
        	log.info("exNumInt " + probs.exNumInt);
        	log.info("degSrc " + exDegSrcNode);
        	log.info("degDst " + exDegDestNode);
		log.info("weight " + weight); 
		log.info("modelTruth " + modelTruth); 
		log.info("complexModel " + complexModel); 
		log.info("nullTruth " + nullTruth); 
		log.info("nullModel " + nullModel); 
		log.info("return " + ret);
	} 

	return ret;
    }

    public double scoreNode(NodeType node, Graph<NodeType,Double> g) {
    	return 0;
    }

    /**
     * Calculates the expected degree of a given node.  The valued calculated is
     * based on the actual degree of the node, the number of nodes in the graph, 
     * the weight of the neighbor edges, and the overall probabilit that given a
     * node, an edge exists (pTrueGivenNotObs).
     */
    private double calcExpectedNodeDegree(NodeType node, Graph<NodeType,Double> graph) {

	Double d = exNodeDegrees.get(graph).get(node);

	if ( d == null ) {

		// we assume that scoreGraph() has been run
    		GraphProbs probs = graphMap.get(graph);
		double exDegNode = ((double)(graph.numberOfNodes() - graph.degreeOfNode(node) - 1))*probs.pTrueGivenNotObs;
		for ( NodeType neighbor: graph.getNeighbors(node) ) 
			exDegNode += graph.getEdgeWeight(node,neighbor);

		d = new Double(exDegNode);

		exNodeDegrees.get(graph).put(node,d);
	}
	
	return  d.doubleValue();
    }
}

