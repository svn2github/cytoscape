
//============================================================================
// 
//  file: SIFHomologyReader.java
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



package nct.service.homology.sif;

import java.lang.*;
import java.util.*;
import java.io.*;

import nct.graph.SequenceGraph;
import nct.service.homology.HomologyModel;
import nct.parsers.SIFParser;

/**
 * A class that implements the HomologyModel interface by reading a SIF file
 * that contains nodes from two separate graphs.  The format of the file is:
 * "nodeA expectationValue nodeB" where nodeA and nodeB are from separate graphs.
 */
public class SIFHomologyReader implements HomologyModel { 
	
	/**
	 * The file that contains the blast output to be parsed.
	 */
	protected String sifFile; 

	/**
	 * Constructor.
	 * @param sifFile The SIF file used to specify the homology values.
	 */
	public SIFHomologyReader(String sifFile) { 
		this.sifFile = sifFile;
	}

	/**
	 * Returns a map of expectation values between nodes of the specified graphs.
	 * @param sg1 The first graph containing nodes expected to be found in the SIF file. 
	 * @param sg2 The second graph containing nodes expected to be found in the SIF file. 
	 * @return A map of expectation values between nodes. 
	 */
	public Map<String,Map<String,Double>> expectationValues( SequenceGraph<String,Double> sg1, SequenceGraph<String,Double> sg2 ) {
		Map<String,Map<String,Double>> homologyMap = new HashMap<String,Map<String,Double>>(); 

		List<String[]> lines = SIFParser.parse(sifFile);

		for (String[] buffer : lines ) {
			if ( (!sg1.isNode(buffer[0]) && !sg2.isNode(buffer[0]) ) ||
			     (!sg1.isNode(buffer[2]) && !sg2.isNode(buffer[2]) ) ) 
			     continue;
	
			double blastVal = Double.parseDouble(buffer[1]);

			if ( !homologyMap.containsKey(buffer[0]) )
				homologyMap.put(buffer[0],new HashMap<String,Double>());

			if ( !homologyMap.containsKey(buffer[2]) ) 
				homologyMap.put(buffer[2],new HashMap<String,Double>());
			
			
			homologyMap.get(buffer[0]).put(buffer[2],blastVal);
			homologyMap.get(buffer[2]).put(buffer[0],blastVal);
		}
		assert(homologyMap.size() > 0) : "homology map empty (not created)";

		return homologyMap;
	}
}
