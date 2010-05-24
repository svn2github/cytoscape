
//============================================================================
// 
//  file: GraphProbs.java
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

/**
 * A simple class that encapsulates various probability values calculated for a graph.
 */
class GraphProbs {

        /**
	 * Probability of truth or the background true probability. The sum of all edge
	 * probabilities divided by the number of total possible edges in the graph. 
	 *	   True=1 True=0
	 *        ---------------	
	 *  Obs=1 |  XX  |      |
	 *        ---------------	
	 *  Obs=0 |  XX  |      |
	 *        ---------------	
	 */
    	double pTrue = 0; 

        /** 
	 * Probability of an observed interaction. The number of edges in the graph
	 * divided by the number of total possible edges in the graph.
	 *	   True=1 True=0
	 *        ---------------	
	 *  Obs=1 |      |      |
	 *        ---------------	
	 *  Obs=0 |  XX  |  XX  |
	 *        ---------------	
	 */
    	double pObs = 0; 

        /**
	 * Probability of truth given not observed.
	 *	   True=1 True=0
	 *        ---------------	
	 *  Obs=1 |      |      |
	 *        ---------------	
	 *  Obs=0 |  XX  |      |
	 *        ---------------	
	 */
    	double pTrueGivenNotObs = 0; 

	/**
	 * Expected number of interactions in the graph.
	 */
	long exNumInt = 0; 

	/**
	 * Returns a string listing each value contained in the class.
	 * @return A string listing each value contained in the class.
	 */
	public String toString() {
	    String s = "pTrue: " + Double.toString(pTrue) +
	    " pObs: " + Double.toString(pObs) +
	    " pTrueGivenNotObs: " + Double.toString(pTrueGivenNotObs) +
	    " exNumInt: " + Double.toString(exNumInt);
	    return s;
	}
}

