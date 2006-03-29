
//============================================================================
// 
//  file: ThresholdRandomizer.java
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

import nct.graph.util.*;
import java.util.*;

public class ThresholdRandomizer 
	extends DegreePreservingRandomizer<String,Double> {

	protected double threshold;

	public ThresholdRandomizer( Random r, double thresholdPercentage ) {
		super(r,false);
		this.threshold = thresholdPercentage;

		if ( threshold > 1 ) {
			threshold = 1;
			System.err.println("Threshold must be between 0 and 1, changing to 1!!!");
		}

		if ( threshold < 0 ) {
			threshold = 0;
			System.err.println("Threshold must be between 0 and 1, changing to 0!!!");
		}
	}

	public boolean weightsSimilar(Double A, Double B) {
		int a = (int)(A.doubleValue()/threshold); 
		int b = (int)(B.doubleValue()/threshold); 
		if ( a == b )
			return true;
		else
			return false;
	}
}
