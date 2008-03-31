/* 
* Created on 16. November 2007
 * 
 */
package de.layclust.layout.geometric_clustering;

import de.layclust.layout.data.ConnectedComponent;
import de.layclust.taskmanaging.InvalidTypeException;

/**
 *  This class contains several static methods that create the correct objects for
 * the different geometric clustering algorithms. When a new algorithm that implements
 * {@link IGeometricClusterer} is created by a developer, these methods need to be updated!
 * 
 * @author sita
 *
 */
public class GeometricClusteringFactory {
	
	/* all implemented geometric clusterers */
	public final static String GEOMETRIC_CLUSTERERS = "SingleLinkageClusterer,KmeansClusterer";
	
	public final static int SINGLELINKAGECLUSTERING = 0;
	public final static String SINGLELINKAGECLUSTERING_CLASSNAME = "SingleLinkageClusterer";
	
	public final static int KMEANSCLUSTERER = 1;
	public final static String KMEANSCLUSTERER_CLASSNAME = "KmeansClusterer";
	
	
	
	// ===============================//
	// ADD ADDITIONAL GEOMETRIC CLUSTERERS HERE!!   //
	// ===============================//


	/**
	 * Here the correct implementation of the {@link IGeometricClusterer}
	 * interface is returned according to the given type.
	 * @param type The type of geometric clusterer to create.
	 * @param cc The {@link ConnectedComponent} object to cluster.
	 * @param configFileName The name of the path to the config file.
	 */
	public static IGeometricClusterer getGeometricClusterByType(int type, 
			ConnectedComponent cc)
		throws InvalidTypeException{
		
		if(type==SINGLELINKAGECLUSTERING){
			return new SingleLinkageClusterer(cc);
		} else if (type==KMEANSCLUSTERER){
			return new KmeansClusterer(cc);
			
			// ===============================//
			// ADD ADDITIONAL GEOMETRIC CLUSTERERS HERE!!   //
			// ===============================//
			
		} else {
			throw new InvalidTypeException("GeometricClusteringFactory: This geometric clustering type " +
					"does not exist: "+type);
		}
	}
	
	/**
	 * This method takes the given class name and returns the internal int value for this
	 * class. 
	 * @param className Class name of the geometric clustering implementation.
	 * @return Int value for the geometric clustering implementation.
	 * @throws InvalidTypeException If The class name does not exist or has not been bound into the program correctly.
	 */
	public static int getClustererTypeByClass(String className) 
		throws InvalidTypeException{
		
		if(className.equals(SINGLELINKAGECLUSTERING_CLASSNAME)){
			return SINGLELINKAGECLUSTERING;
		} else if (className.equals(KMEANSCLUSTERER_CLASSNAME)){
			return KMEANSCLUSTERER;
		
	
		// ===============================//
		// ADD ADDITIONAL GEOMETRIC CLUSTERERS HERE!!   //
		// ===============================//
		
		} else {
			throw new InvalidTypeException("GeometricClusteringFactory: This geometric clustering class " +
					"does not exist: "+className+".\nOr it has not been bound into the program correctly.");
		}	
	}
	
}