/* 
* Created on 16. November 2007
 * 
 */
package de.layclust.layout;

import de.layclust.datastructure.CC2DArray;
import de.layclust.datastructure.CCHash;
import de.layclust.datastructure.CCSymmetricArray;
import de.layclust.datastructure.ICCEdges;
import de.layclust.layout.acc.ACCLayouter;
import de.layclust.layout.acc.ACCParameters;
import de.layclust.layout.acc.LayoutInitRandom;
import de.layclust.layout.forcend.FORCEnDLayouter;
import de.layclust.layout.forcend.FORCEnDParameters;
import de.layclust.layout.forcend.LayoutInitCirclesInPlanes;
import de.layclust.taskmanaging.InvalidTypeException;
import de.layclust.taskmanaging.TaskConfig;

/**
 * This class contains several static methods that create the correct objects for
 * the different layouting algorithms as well as enum type classes that do most of the job. 
 * When a new algorithm that implements
 * {@link ILayouter} or {@link ICCEdges} is created by a developer, the enum classes 
 * need to be updated!
 * 
 * @author Sita Lange
 */
public class LayoutFactory {
	
	/**
	 * enum type for {@link ILayouter} implementations
	 * 
	 */
	public enum EnumLayouterClass {
		FORCEND("FORCEnDLayouter", 0),
		ACC("ACCLayouter", 1);	
		// ===============================//
		// ADD ADDITIONAL LAYOUTERS HERE!!						   //
		// ===============================//
		
		private final String classname;
		private final int intvalue;
		
		EnumLayouterClass(String classname, int intvalue){
			this.classname = classname;
			this.intvalue = intvalue;
		}
		 
		public String getClassname() {return classname;}
		public int getIntvalue() {return intvalue;}
		
		/**
		 * Initialises the correct ILayouter implementation according to type.
		 * @return The correct {@link ILayouter} implementation.
		 */
		public ILayouter createLayouter(){
			if(intvalue == 0){ return new FORCEnDLayouter(); } 
			else if(intvalue == 1){ return new ACCLayouter(); }		
			// ===============================//
			// ADD ADDITIONAL LAYOUTERS HERE!!						   //
			// ===============================//			
			else return null;
		}
		
		/**
		 * Initialises the correct IParameters implementation according to type.
		 * @return The correct {@link IParameters} implementation.
		 */
		public IParameters createIParameters(){
			if(intvalue == 0){ return new FORCEnDParameters(); } 
			else if (intvalue == 1){ return new ACCParameters(); } 
			// ===============================//
			// ADD ADDITIONAL LAYOUTERS HERE!!						   //
			// ===============================//	
			else return null;
		}
		
		/**
		 * Initialises the correct {@link ILayoutInitialiser} implementation according to type.
		 * @return The correct {@link ILayoutInitialiser} implementation.
		 */
		public ILayoutInitialiser createLayoutInitialiser(){
			if(intvalue == 0){ return new LayoutInitCirclesInPlanes(); }
			else if (intvalue == 1){ return new LayoutInitRandom(); }
			// ===============================//
			// ADD ADDITIONAL LAYOUTERS HERE!!						   //
			// ===============================//	
			else return null;
		}
		
		/**
		 * Gets all the class names for the layouting algorithms and returns
		 * these in a String array.
		 * @return Array containing all existing class names of the {@link ILayouter} implementation.
		 */
		public static String[] getClassnames(){
			EnumLayouterClass[] values = EnumLayouterClass.values();
			String[] classnames = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				classnames[i] = values[i].getClassname();
			}
			return classnames;
		}
	}
	
	/**
	 * Gets the input layouters from the config class and turns it into an
	 * array of enum types respective to the layouters in the order they should be
	 * carried out in.
	 * 
	 * @return An array of {@link EnumLayouterClass}.
	 * @throws LayoutTypeException If the given layouter does not exist.
	 */
	public static EnumLayouterClass[] getEnumArrayFromLayoutersString() 
		throws InvalidTypeException{
		
		String[] inputLayouting = TaskConfig.layouterClasses.split("\\s*,\\s*");
		EnumLayouterClass[] allPossLayouters = EnumLayouterClass.values();
		
		EnumLayouterClass[] layouterEnumsInProcessingOrder = 
			new EnumLayouterClass[inputLayouting.length];
		for(int i=0;i<inputLayouting.length;i++){
			boolean layouterexists = false;
			for (EnumLayouterClass layclass : allPossLayouters) {
				if(inputLayouting[i].equals(layclass.getClassname())){
					layouterEnumsInProcessingOrder[i] = layclass;
					layouterexists = true;
				}
			}
			if(!layouterexists){
				throw new InvalidTypeException("LayoutFactory: This layouting algorithm has not " +
						"been implemented: "+inputLayouting[i]);
			}
		}
		return layouterEnumsInProcessingOrder;
	}

	
	/**
	 * enum type for {@link ICCEdges} implementations
	 * 
	 */
	public enum EnumCCEdgesStructure {
		CC2DARRAY("CC2DArray", 0),
		CCSYMMETRICARRAY("CCSymmetricArray", 1),
		CCHASH("CCHash", 2);
		
		// ==================================//
		// ADD ADDITIONAL CC EDGES IMPLEMENTATIONS HERE!!	   //
		// ==================================//
		
		private final String classname;
		private final int intvalue;
		
		EnumCCEdgesStructure(String classname, int intvalue){
			this.classname = classname;
			this.intvalue = intvalue;
		}
		 
		public String getClassname() {return classname;}
		public int getIntvalue() {return intvalue;}
		
		/**
		 * Initialises the correct {@link ICCEdges} according to type.
		 * @return The correct {@link ICCEdges} implementation.
		 */
		public ICCEdges createCCEdges(int size){
			if(intvalue == 0){ return new CC2DArray(size); } 
			else if(intvalue == 1){ return new CCSymmetricArray(size); }
			else if(intvalue == 2){ return new CCHash(size); }
			// ==================================//
			// ADD ADDITIONAL CC EDGES IMPLEMENTATIONS HERE!!	   //
			// ==================================//
			else return null;
		}
		
		/**
		 * Gets all the class names for the {@link ICCEdges} implementations and returns
		 * these in a String array.
		 * @return Array containing all existing class names of the {@link ICCEdges} implementation.
		 */
		public static String[] getClassnames(){
			EnumLayouterClass[] values = EnumLayouterClass.values();
			String[] classnames = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				classnames[i] = values[i].getClassname();
			}
			return classnames;
		}
	}
	
	/**
	 * Gets the correct {@link EnumCCEdgesStructure} enum type from the name of the class.
	 * @param className
	 * @return EnumCCEdgesStructure for the CC edges.
	 * @throws InvalidTypeException If the given class name is incorrect or does not exist.
	 */
	public static EnumCCEdgesStructure getCCEdgesEnumByClass(String className) 
			throws InvalidTypeException{
		EnumCCEdgesStructure[] allPossStructureEnums = EnumCCEdgesStructure.values();
		for (EnumCCEdgesStructure structure : allPossStructureEnums) {
			if(className.equals(structure.getClassname())){
				return structure;
			}
		}
		throw new InvalidTypeException("LayoutFactory: This edges class has not yet been implemented: "+className
				+".\nOr it has not been correctly bound into the program.");
	}
	
}