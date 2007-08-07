package cytoscape.filters;

import y.base.*;
import y.view.*;

import y.algo.GraphHider;

import java.util.Hashtable;

import cytoscape.data.*;
import cytoscape.*;
/**
 * Filter that flags according to expression only,
 * without no topological consideration.
 * <p>
 * Flaged nodes are those which don't have the given number of 
 * expression conditions meeting the given cutoff expression significance value.
 * 
 * @author namin@mit.edu
 * @version 2002-03-02
 */
public class ExpressionOnlyFilter extends Filter {
    /**
     * Cutoff value.
     */
    double cutoff;
    /**
     * Whether meeting conditions are < (true) or > (false) than cutoff.
     */
    boolean cutoffSmallerThan;
    /**
     * Required number of conditions meeting cutoff.
     */
    int nConds;
    ExpressionData expressionData;
    GraphObjAttributes nodeAttributes;

    /**
     * Whether to use significance or ratio as the comparison value.
     */
    boolean useRatio;
    public ExpressionOnlyFilter(Graph2D graph,
				ExpressionData expressionData,
				GraphObjAttributes nodeAttributes,
				double cutoff, boolean cutoffSmallerThan,
				int nConds,
				boolean useRatio) {
	super(graph);

	this.expressionData = expressionData;
	this.nodeAttributes = nodeAttributes;
	this.cutoff = cutoff;
	this.cutoffSmallerThan = cutoffSmallerThan;
	this.nConds = nConds;
	this.useRatio = useRatio;
    }


    public NodeList get(NodeList hidden) {
	NodeList flagged = new NodeList();

	String[] conditionNames = expressionData.getConditionNames();
	for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
	    Node node = nc.node();

	    if (nConds > 0 && !hidden.contains(node)) {
		
		// !!!TMP!!!
		String geneName = nodeAttributes.getCanonicalName(node);

		int countConds= 0;
		for (int i = 0; i < conditionNames.length; i++) {
		    try {
			String conditionName = conditionNames[i];
			mRNAMeasurement measurement = expressionData.getMeasurement(geneName,conditionName);
			
			double value;
			if (useRatio) {
			    value = measurement.getRatio();
			} else {
			    value = measurement.getSignificance();
			}

			if ((!cutoffSmallerThan && value >= cutoff)
			    ||
			    (cutoffSmallerThan && value <= cutoff)) {
			    countConds++;
			    
			    if (countConds >= nConds) {
				// No need to go on
				break;
			    }
			}
		    } catch (NullPointerException e) {
			// System.out.println("!!! ERROR !!! NullPointerException !!!");
		    }
		}

		if (countConds < nConds) {
		    flagged.add(node);
		}
	    }
	}

	return flagged;
    }

}
