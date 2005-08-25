package org.isb.xmlrpc.handler.synonyms;


import java.lang.*;
import java.util.*;
import org.isb.xmlrpc.handler.DataSource;


public interface SynonymsDataSource extends DataSource{
	public static final String INTERACTOR_1 = "i1";
	public static final String INTERACTOR_2 = "i2";
	public static final String INTERACTION_TYPE = "itype";
	public static final String SOURCE = "src";
	public static final String DIRECTED = "dir";
	public static final String CONFIDENCE = "confidence";
	
	/**
	 * @return a Vector of Strings that specify types of IDs that this SynonymsDataSource accepts
	 * for example, "ORF","GI", etc.
	 */
	public Vector getIDtypes ();
	
	/**
	 * @param genename
	 * @param source id system
	 * @param target id system
	 * @return a Vector of synonyms in target id system
	 */
	public Vector getAllSynonyms (String genename, String source, String target);
	
	/**
	 * @param genenames in Vector type which contains some ids to be translated
	 * @param source id system
	 * @param target id system
	 * @return a Vector of Hashtables of synonyms in target id system 
	 */
	public Vector getAllSynonyms (Vector genenames, String source, String target);
}
