
//============================================================================
// 
//  file: InteractionGraph.java
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



package nct.networkblast.graph;

import java.util.*;
import java.util.logging.Logger;
import java.io.*;

import nct.graph.basic.BasicDistanceGraph;
import nct.graph.SequenceGraph;
import nct.service.interactions.InteractionNetwork;
import nct.service.interactions.SIFInteractionNetwork;

/**
 * An implementation of a SequenceGraph that is specified by a SIF file.
 */
public class InteractionGraph extends BasicDistanceGraph<String,Double> 
	implements SequenceGraph<String,Double> {

	private static Logger log = Logger.getLogger("networkblast");

	/**
	 * Constructor.
	 */
	public InteractionGraph() {
		super();
	}

	/**
	 *  @param fileName The SIF input file containing the graph specification.
	 */
	public InteractionGraph(String fileName) throws FileNotFoundException, IOException {
		super(fileName);
		
		int numEdge = 0;
		int nonEdge = 0;
		int dupeEdge = 0;
		double totalProb = 0;
		InteractionNetwork<String,Double> in = new SIFInteractionNetwork( fileName );
		in.updateGraph( this );

		assert( weightMap.size() > 0 ) : "No edges added to graph!";
	}

	/**
	 * Instead of returning null when an edge doesn't exist, it returns -1.
	 * @param nodeA The source node of the edge.
	 * @param nodeB The target node of the edge.
	 * @return The edge weight if the edge exists, -1 otherwise.
	 */
	public Double getEdgeWeight(String nodeA, String nodeB) {
		Double d = super.getEdgeWeight(nodeA,nodeB);
		if ( d == null )
			d = new Double(-1.0);
		return d;
	}

	/**
	 * Dummy method needed to implement the SequenceGraph interface.
	 * TODO  Can we get rid of this somehow?
	 */
	public String getDBName() { return ""; }

	/**
	 * Dummy method needed to implement the SequenceGraph interface.
	 * TODO  Can we get rid of this somehow?
	 */
	public String getDBLocation() { return ""; }

	/**
	 * Dummy method needed to implement the SequenceGraph interface.
	 * TODO  Can we get rid of this somehow?
	 */
	public int getDBType() { return SequenceGraph.DUMMY; }

	/**
	 * Dummy method needed to implement the SequenceGraph interface.
	 * TODO  Can we get rid of this somehow?
	 */
	public void setDBType(int type) { };

	/**
	 * Dummy method needed to implement the SequenceGraph interface.
	 * TODO  Can we get rid of this somehow?
	 */
	public void setDBLocation(String loc) { };

	/**
	 * Dummy method needed to implement the SequenceGraph interface.
	 * TODO  Can we get rid of this somehow?
	 */
	public void setDBName(String name) { };

}
