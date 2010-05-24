
//============================================================================
// 
//  file: SIFInteractionNetwork.java
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



package nct.service.interactions;

import java.util.*;
import nct.graph.Graph;
import nct.graph.basic.BasicGraph;
import nct.parsers.SIFParser;

/**
 * An implementation of the InteractionNetwork that updates a graph based
 * on the information in a SIF file.
 */
public class SIFInteractionNetwork implements InteractionNetwork<String,Double> {

	/**
	 * Each String array represents a line in the SIF file. Element 0 is
	 * the first node, element 1 is the weight and element 2 is the second node.
	 */
	protected List<String[]> intList; 

	/**
	 * The SIF file name used to build the interaction network.
	 */
	protected String SIFFileName; 

	/**
	 * @param SIFFileName The SIF file containing the network/graph information. 
	 */
	public SIFInteractionNetwork( String SIFFileName ) {
		this.SIFFileName = SIFFileName;
		
		try {
			intList = SIFParser.parse(SIFFileName);
		} catch ( Exception e ) {
			e.printStackTrace();
			intList = null;
		}
	}

	/**
	 * @param graph The graph to be udpated with the node and edge information found
	 * in the SIF file specified in the constructor.
	 */
	public void updateGraph(Graph<String,Double> graph ) { 
		if ( intList != null ) {
			for ( String[] s : intList ) {
				graph.addNode(s[0]);
				graph.addNode(s[2]);
				graph.addEdge(s[0],s[2],Double.parseDouble(s[1]));
			}
		} else 
			System.out.println("no interactions read from file: " + SIFFileName);
	}
}
