/**
 * 
 */
package org.isb.xmlrpc.handler.synonyms;

import org.isb.xmlrpc.handler.DataSource;

/**
 * @author jpark
 *
 */
public class SynonymsHandler implements SynonymsDataSource {
	/**
	 * @return a Vector of Strings that specify types of IDs that this SynonymsDataSource accepts
	 * for example, "ORF","GI", etc.
	 */
	public Vector getIDtypes () {
		return null;
	}
	
	/**
	 * @param genename
	 * @param source id system
	 * @param target id system
	 * @return a Vector of synonyms in target id system
	 */
	public Vector getAllSynonyms (String genename, String source, String target) {
		return null;
	}
	
	/**
	 * @param genenames in Vector type which contains some ids to be translated
	 * @param source id system
	 * @param target id system
	 * @return a Vector of Hashtables of synonyms in target id system 
	 */
	public Vector getAllSynonyms (Vector genenames, String source, String target) {
		return null;
	}
	

	/**
	 * @return the name of the data source, for example, "KEGG", "Prolinks", etc.
	 */
	public String getDataSourceName () {
		return null;
	}
	
	/**
	 * @return the type of backend implementation (how requests to the data source
	 * are implemented) one of WEB_SERVICE, LOCAL_DB, REMOTE_DB, MEMORY, MIXED
	 */
	public String getBackendType () {
		return null;
	}
	
	/**
	 * @return a Vector of Strings representing the species for which the data
	 * source contains information
	 */
	public Vector getSupportedSpecies () {
		return null;
	}
	
	/**
	 * @return a String denoting the version of the data source (could be a release date,
	 * a version number, etc).
	 */
	
	public String getVersion () {
		return null;
	}
	
	/**
	 * @return boolean whether or not this data source requires a password from the user
	 * in order to access it
	 */
	public boolean requiresPassword () {
		return null;
	}
	
	/**
	 * Runs tests on the data source
	 * @return a vector of results
	 */
	public Vector test () {
		return null;
	}
	
	/**
	 * If called, System.out.print statements will be called
	 * for debugging
	 */
	public Boolean printDebug () {
		return null;
	}
	
	/**
	 * If calles, no System.out.print statemets will be called
	 *
	 */
	public Boolean noPrintDebug () {
		return null;
	}
}
