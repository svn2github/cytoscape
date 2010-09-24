package clusterMaker.algorithms.TransClust.de.layclust.geometric_clustering;

import clusterMaker.algorithms.TransClust.de.layclust.datastructure.ConnectedComponent;

public interface IGeometricClusterer {
	
	public void initGeometricClusterer(ConnectedComponent cc);
	
	public void run();
	
}
