/* 
* Created on 4. November 2007
 * 
 */

package de.layclust.layout.postprocessing;

import de.layclust.taskmanaging.InvalidTypeException;


/**
 *  This class contains static methods that create the correct objects for
 * the different post processing algorithms. When a new algorithm that implements
 * {@link IPostProcessing} is created by a developer, these methods need to be updated!
 * 
 * @author sita
 *
 */
public class PostProcessingFactory {
	
	public final static int PP_REARRANGE_AND_MERGE_BEST = 0;
	
	public final static int POSTPROCESSING_TOBI = 1;
	
	public final static int PP_DIVIDE_INTO_SUBGRAPHS_USING_RESULTS = 2;
	
	public final static int  PP_DIVIDE_AND_RECLUSTER_RECURSIVELY = 3;
	
	// ===============================//
	// ADD ADDITIONAL POST PROCESSORS HERE!!             //
	// ===============================//
	
	/**
	 * Here the correct implementation of the {@link IPostProcessing}
	 * interface is returned according to the given type. The objects
	 * still need to be initialised before they can be run.
	 * @param type The type of post processor to create.
	 */
	public static IPostProcessing getPostProcessorByType(int type)
		throws InvalidTypeException{
		
		if(type==PP_REARRANGE_AND_MERGE_BEST){
			return new PP_RearrangeAndMergeBest();	
		
		} else if (type == POSTPROCESSING_TOBI){
			return new PostProcessingTobi();
		
		}else if(type == PP_DIVIDE_INTO_SUBGRAPHS_USING_RESULTS){
			/* need to initialise the object with IParameter object and layouting type */
			return new PP_DivideIntoSubgraphsUsingClusterResults();
		
		}else if(type == PP_DIVIDE_AND_RECLUSTER_RECURSIVELY){
			/* need to initialise the object with IParameter object and layouting type */
			return new PP_DivideAndReclusterRecursively();
		}
		
		// ===============================//
		// ADD ADDITIONAL POST PROCESSORS HERE!!             //
		// ===============================//
		
		else {
			throw new InvalidTypeException("PostProcessingFactory: This post processor type " +
					"does not exist: "+type);
		}
	}
	
	public static int getPostProcessorTypeByClass(String className) throws InvalidTypeException{
		if(className.equals("PP_RearrangeAndMergeBest")){
			return PP_REARRANGE_AND_MERGE_BEST;
		} else if (className.equals("PostProcessingTobi")){
			return POSTPROCESSING_TOBI;
		} else if (className.equals("PP_DivideAndReclusterRecursively")){
			return PP_DIVIDE_AND_RECLUSTER_RECURSIVELY;
		} else if (className.equals("PP_DivideIntoSubgraphsUsingClusterResults")){
			return PP_DIVIDE_INTO_SUBGRAPHS_USING_RESULTS;
		}
		
		// ===============================//
		// ADD ADDITIONAL POST PROCESSORS HERE!!             //
		// ===============================//
		
		else {
			throw new InvalidTypeException("PostProcessingFactory: This post processor class " +
					"does not exist: "+className+".\nOr it has not been bound into the program properly");
		}
	}
}