package cytoscape.filters;

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/


import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

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


