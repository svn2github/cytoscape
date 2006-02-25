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
