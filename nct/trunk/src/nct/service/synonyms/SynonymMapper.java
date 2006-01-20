
//============================================================================
// 
//  file: SynonymMapper.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.service.synonyms;

import java.util.List;

/**
 * An interface that describes methods for transforming an identifier
 * into a synonym of a particular type.
 */
public interface SynonymMapper {

	/**
	 * Returns the unique id for the specified synonym.
	 * @param synonym The synonym to check the database for.
	 * @return The unique id for the input synonym.
	 */
	public String getIdFromSynonym( String synonym );

	/**
	 * Returns a synonym of a particular type for the specified id.
	 * @param id The id to check the database for.
	 * @param type The type of synonym desired for the specified id. 
	 * @return The requested synonym.
	 */
	public String getSynonymFromId( String id, String type );

	/**
	 * A shortcut method for calling:
	 * <code>
	 * String s = syns.getSynonymFromId(syns.getIdFromSynonym(synonym), type);
	 * </code>
	 * @param synonym The synonym to check the database for.
	 * @param type The type of synonym desired for the specified input synonym. 
	 * @return The requested synonym.
	 */
	public String getSynonym( String synonym, String type );

	/**
	 * This method will return a list of potential synonyms that match the
	 * input.
	 * @param input The input string that will be matched against the list of synonyms
	 * in the database.
	 * @return A list of potential synonyms.
	 */
	public List<String> getPotentialSynonyms( String input );
}
