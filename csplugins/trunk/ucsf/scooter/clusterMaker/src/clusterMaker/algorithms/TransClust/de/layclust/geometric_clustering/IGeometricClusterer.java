package de.layclust.geometric_clustering;

import de.layclust.datastructure.ConnectedComponent;

public interface IGeometricClusterer {
	
	public void initGeometricClusterer(ConnectedComponent cc);
	
	public void run();
	
}
