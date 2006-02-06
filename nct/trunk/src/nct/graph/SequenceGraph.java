
//============================================================================
// 
//  file: SequenceGraph.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.graph;

import java.lang.*;

/**
 * An interface that describes accessing a sequence database
 * associated with the graph. 
 */
public interface SequenceGraph< NodeType extends Comparable<NodeType>,
		                WeightType extends Comparable<WeightType>> 
	extends DistanceGraph<NodeType,WeightType> {

	/**
	 * A dummy (non-existant) database type.
	 */
	public static final int DUMMY = 0;

	/**
	 * A Fasta database, usually referring to a multiple fasta
	 * formatted file.
	 */
	public static final int FASTA = 1;

	/**
	 * A Blast database. 
	 */
	public static final int BLAST = 2;

	/**
	 * Returns the name of the database used.
	 * @return The name of the database used.  
	 */
	public String getDBName(); 

	/**
	 * Returns the location of the database used.
	 * @return The location of the database used.
	 */
	public String getDBLocation(); 

	/**
	 * Returns an integer identifying the type of database used.
	 * @return An integer identifying the type of database used.
	 */
	public int getDBType(); 

	/**
	 * Sets the name of the database.
	 * @param name The new name of the sequence database.
	 */
	public void setDBName(String name); 

	/**
	 * Sets the location of the database.
	 * @param location The new location of the sequence database.
	 */
	public void setDBLocation(String loc); 

	/**
	 * Sets the type of the database.
	 * @param type The new type of the sequence database.
	 */
	public void setDBType(int type); 
}
