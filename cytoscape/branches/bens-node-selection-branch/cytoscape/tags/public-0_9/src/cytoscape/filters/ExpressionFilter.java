package cytoscape.filters;

import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

import java.util.Hashtable;

import cytoscape.data.*;
import cytoscape.*;
/**
 * Filter that flags some nodes 
 * based on topology relative to nodes that meet expression requirements.
 * <p>
 * Flaged nodes are those which don't have the given number of 
 * expression conditions meeting the given cutoff expression significance value
 * and don't have the required number of neighbors that do.
 * 
 * @author namin@mit.edu
 * @version 2002-03-02
 */
public class ExpressionFilter extends Filter {
    double cutoff;
    boolean cutoffSmallerThan;
    int nConds;
    ExpressionData expressionData;
    GraphObjAttributes nodeAttributes;
    boolean hideSingletons;
    int nNeighbors;
    int maxDepth;
    Filter neighborF;
    boolean useRatio;

    public ExpressionFilter(Graph2D graph,
			    ExpressionData expressionData,
			    GraphObjAttributes nodeAttributes,
			    double cutoff, boolean cutoffSmallerThan, int nConds,
			    boolean useRatio,
			    int nNeighbors, int maxDepth,
			    boolean hideSingletons) {
	super(graph);

	this.cutoff = cutoff;
	this.cutoffSmallerThan = cutoffSmallerThan;
	this.nConds = nConds;
	this.useRatio = useRatio;
	this.expressionData = expressionData;
	this.nodeAttributes = nodeAttributes;
	this.hideSingletons = hideSingletons;
	this.nNeighbors = nNeighbors;
	this.maxDepth = maxDepth;
	this.neighborF = neighborF;
    }

    public NodeList get(NodeList hidden) {
	Filter unaffF = new ExpressionOnlyFilter(graph,
						 expressionData,
						 nodeAttributes,
						 cutoff, cutoffSmallerThan, 
						 nConds,
						 useRatio);
	
	Filter memoUnaffF = new ListFilter(graph,
					   unaffF.get(hidden));

	Filter memoAffF = new NegFilter(graph,
					memoUnaffF);


	Filter unaffTopoF = new TopologyOnePassFilter(graph,
						      memoUnaffF,
						      nNeighbors, maxDepth,
						      memoAffF);

	NodeList flagged = unaffTopoF.get(hidden);

	if (hideSingletons) {
	    Filter singletonF = new TopologyOnePassFilter(graph, 1, 1);
	    flagged.splice(singletonF.get(flagged));
	}

	return flagged;
    }
}
