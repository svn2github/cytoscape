
//============================================================================
// 
//  file: SequenceDatabase.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.service.sequences;

/**
 * An interface describing a way to query a sequence database
 * for sequence and existence information.
 */
public interface SequenceDatabase {

	/**
	 * Determines whether or not the specified id points to a
	 * sequence in the database.
	 * @param id The id to search for in the database.
	 * @return True if the id is found in the database, false otherwise.
	 */
	public boolean contains(String id);

	/**
	 * Returns the sequence associated with the specified id from 
	 * the database.
	 * @param id The id of the sequence to retrieve from the database.
	 * @return A String representation of the sequence found in the database.
	 */
	public String getSequence(String id);

}
