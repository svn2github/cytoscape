/* 
* Created on 16. November 2007
 * 
 */
package de.layclust.layout;

import de.layclust.layout.acc.ACCLayouter;
import de.layclust.layout.acc.ACCParameters;
import de.layclust.layout.acc.LayoutInitRandom;
import de.layclust.layout.data.CC2DArray;
import de.layclust.layout.data.CCHash;
import de.layclust.layout.data.CCSymmetricArray;
import de.layclust.layout.data.ICCEdges;
import de.layclust.layout.forcend.FORCEnDLayouter;
import de.layclust.layout.forcend.FORCEnDParameters;
import de.layclust.layout.forcend.LayoutInitCirclesInPlanes;
import de.layclust.layout.parameter_training.IParameters;
import de.layclust.taskmanaging.InvalidTypeException;
import de.layclust.taskmanaging.TaskConfig;

/**
 * This class contains several static methods that create the correct objects for
 * the different layouting algorithms. When a new algorithm that implements
 * {@link ILayouter} is created by a developer, these methods need to be updated!
 * 
 * @author sita
 */
public class LayoutFactory {
	
	/* all implemented class names for the gui */
	public final static String LAYOUTERS = "FORCEnDLayouter,ACCLayouter";
	public final static String CCEDGES = "CC2DArray,CCSymmetricArray,CCHash";
	
	/* layouting algorithms */
	public final static int FORCEND = 0;
	public final static int ACC = 1;
	
	public final static String FORCEND_CLASSNAME = "FORCEnDLayouter";
	public final static String ACC_CLASSNAME = "ACCLayouter";
	
	// ===============================//
	// ADD ADDITIONAL LAYOUTERS HERE!!						   //
	// ===============================//
	
	/* Implementations of the connected component edges data structures */
	public final static int CC2DARRAY = 0;
	public final static int CCSYMMETRICARRAY = 1;
	public final static int CCHASH = 2;
	
	public final static String CC2DARRAY_CLASSNAME = "CC2DArray";
	public final static String CCSYMMETRICARRAY_CLASSNAME = "CCSymmetricArray";
	public final static String CCHASH_CLASSNAME = "CCHash";
		
	// ==================================//
	// ADD ADDITIONAL CC EDGES IMPLEMENTATIONS HERE!!	   //
	// ==================================//
	
	/**
	 * Gets the input layouters from the config class and turns it into an int
	 * array. The layouters are in the order they should be carried out in.
	 * 
	 * e.g. {FORCEnDLayouter, ACCLayouter} => {0,1}
	 * 
	 * Each layouting algorithm has a fixed int value which is defined
	 * here.
	 * 
	 * @return The layouter type int array.
	 * @throws LayoutTypeException If the given layouter does not exist.
	 */
	public static int[] getTypeArrayFromLayoutersString() 
		throws InvalidTypeException{
		
		String[] inputLayouting = TaskConfig.layouterClasses.split("\\s*,\\s*");
		
		int[] layouterTypesInProcessingOrder = new int[inputLayouting.length];
		for(int i=0;i<inputLayouting.length;i++){
			if(inputLayouting[i].equals(FORCEND_CLASSNAME)){
				layouterTypesInProcessingOrder[i] = FORCEND;
			} else if (inputLayouting[i].equals(ACC_CLASSNAME)){
				layouterTypesInProcessingOrder[i] = ACC;
			}
			
			// ===============================//
			// ADD ADDITIONAL LAYOUTERS HERE!!						   //
			// ===============================//
			
			else{
				throw new InvalidTypeException("LayoutFactory: This layouting algorithm has not " +
						"been implemented: "+inputLayouting[i]);
			}
		}
		return layouterTypesInProcessingOrder;
	}
	
	/**
	 * Creates the correct implementation of the ILayouter interface according to
	 * the given type.
	 * @param type The type of layouter.
	 * @return The correct implementation of Layouter according to the type.
	 * @throws LayoutTypeException If the given type does not exist.
	 */
	public static ILayouter getLayouterByType(int type) throws InvalidTypeException{
		 if(type == FORCEND){
			 return new FORCEnDLayouter();
		 } else if (type == ACC){
			 return new ACCLayouter();
		 } 

			// ===============================//
			// ADD ADDITIONAL LAYOUTERS HERE!!						   //
			// ===============================//		 
		 
		 else{
			 throw new InvalidTypeException("LayoutFactory: This layouting type does not exist: "+type);
		 }
	}
	
	/**
	 * Here the correct implementation of the IParameters is created according
	 * to the given type.
	 * @param type The type of layouter.
	 * @return The correct implementation of IParameters according to the type.
	 * @throws LayoutTypeException If the given type does not exist.
	 */
	public static IParameters getParametersByType(int type) throws 
		InvalidTypeException{
		
		if(type == FORCEND){
			return new FORCEnDParameters();
		}else if (type == ACC){
			return new ACCParameters();
		}
		
		// ===============================//
		// ADD ADDITIONAL LAYOUTERS HERE!!						   //
		// ===============================//
		
		else {
			throw new InvalidTypeException("LayoutFactory: This layouting type does not exist: "+type);
		}
	}
	
	/**
	 * Here the correct implementation of the {@link ILayoutInitialiser} is created 
	 * according to the given type.
	 * @param type The type of layouter.
	 * @return The correct implementation of ILayoutInitialiser according to the type.
	 * @throws LayoutTypeException If the given type does not exist.
	 */
	public static ILayoutInitialiser getLayouterInitialiserByType(int type) throws InvalidTypeException{
		if(type == FORCEND){
			return new LayoutInitCirclesInPlanes();
//			return new LayoutInitHSphere();
		}else if (type == ACC){
			return new LayoutInitRandom();
		}
		
		// ===============================//
		// ADD ADDITIONAL LAYOUTERS HERE!!						   //
		// ===============================//
		
		else {
			throw new InvalidTypeException("LayoutFactory: This layouting type does not exist: "+type);
		}
	}
	
	/**
	 * Here the correct implementation of the {@link ICCEdges} Interface
	 * is created according to the given type.
	 * @param type The type of the edges object
	 * @param size The size of the edges object i.e. the number of nodes.
	 * @return The correct {@link ICCEdges} implementation.
	 * @throws LayoutTypeException When a wrong type is given.
	 */
	public static ICCEdges getCCEdgesByType(int type, int size) throws InvalidTypeException{
		if(type == CC2DARRAY){
			return new CC2DArray(size);
		} else if (type == CCHASH){
			return new CCHash(size);
		} else if (type == CCSYMMETRICARRAY){
			return new CCSymmetricArray(size);
		}
		
		// ==================================//
		// ADD ADDITIONAL CC EDGES IMPLEMENTATIONS HERE!!	   //
		// ==================================//
		
		else {
			throw new InvalidTypeException("LayoutFactory: This edges data structure type does not exist: "+type);
		}
	}
	
	/**
	 * Gets the type in int form for internal calculations from the input String form, which 
	 * is the name of the class.
	 * @param className
	 * @return
	 * @throws InvalidTypeException
	 */
	public static int getCCEdgesTypeByClass(String className) throws InvalidTypeException{
		if(className.equals(CC2DARRAY_CLASSNAME)){
			return CC2DARRAY;
		} else if(className.equals(CCHASH_CLASSNAME)){
			return CCHASH;
		} else if (className.equals(CCSYMMETRICARRAY_CLASSNAME)){
			return CCSYMMETRICARRAY;
		}
		
		// ==================================//
		// ADD ADDITIONAL CC EDGES IMPLEMENTATIONS HERE!!	   //
		// ==================================//		
		
		else {
			throw new InvalidTypeException("LayoutFactory: This edges class has not yet been implemented: "+className
					+".\nOr it has not been correctly bound into the program.");
		}
		
	}
	
}
