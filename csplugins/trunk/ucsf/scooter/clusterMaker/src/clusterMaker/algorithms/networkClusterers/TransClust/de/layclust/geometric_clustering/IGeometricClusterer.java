package clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.geometric_clustering;

import clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.datastructure.ConnectedComponent;

public interface IGeometricClusterer {
	
	public void initGeometricClusterer(ConnectedComponent cc);
	
	public void run();
	
}
