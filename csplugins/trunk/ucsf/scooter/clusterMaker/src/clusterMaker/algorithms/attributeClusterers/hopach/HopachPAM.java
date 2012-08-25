package clusterMaker.algorithms.attributeClusterers.hopach;

import clusterMaker.algorithms.attributeClusterers.BaseMatrix;
import clusterMaker.algorithms.attributeClusterers.DistanceMetric;
import clusterMaker.algorithms.attributeClusterers.pam.HopachablePAM;

/**
 * HopachPAM is a specialized a specialized Hopach algorithm that uses the 
 * PAM partitioner (Hopach-PAM).
 * Independent of Cytoscape.
 * @author djh.shih
 *
 */
public class HopachPAM extends Hopach {
	
	public HopachPAM(BaseMatrix data, DistanceMetric metric) {
		super(new HopachablePAM(data, metric));
	}
	
	public HopachPAM(HopachablePAM p) {
		super(p);
	}
	
}
