package org.isb.xmlrpc.handler.interactions;

import java.lang.*;
import java.sql.*;
import java.util.*;
import org.isb.xmlrpc.handler.*;
import org.isb.xmlrpc.handler.db.*;

/**
 * This class assumes:<br>
 * - mySQL DB is the source (either local or remote)<br>
 * - mySQL DB tables looks like this:<br>
 *
 * interaction tables<br>
 * +------------+-------------+------+-----+---------+-------+<br>
 * | Field      | Type        | Null | Key | Default | Extra |<br>
 * +------------+-------------+------+-----+---------+-------+<br>
 * | gene_id_a  | varchar(10) | YES  |     | NULL    |       |<br>
 * | gene_id_b  | varchar(10) | YES  |     | NULL    |       |<br>
 * | p          | float       | YES  |     | NULL    |       |<br>
 * | confidence | float       | YES  |     | NULL    |       |<br>
 * | method     | char(2)     | YES  |     | NULL    |       |<br>
 * | species    | int(11)     | YES  |     | 0       |       |<br>
 * +------------+-------------+------+-----+---------+-------+<br>
 * 
 * species<br>
 * +------------+-------------+------+-----+---------+-------+<br>
 * | Field      | Type        | Null | Key | Default | Extra |<br>
 * +------------+-------------+------+-----+---------+-------+<br>
 * | species    | varchar(70) | YES  |     | NULL    |       |<br>
 * | species_id | int(11)     | YES  |     | NULL    |       |<br>
 * +------------+-------------+------+-----+---------+-------+<br>
 * 
 *
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class ProlinksInteractionsSource extends SQLDBHandler 
	implements InteractionsDataSource{

	/**
	 * This is the same name as in the MyQSL DB
	 */
  public static final String NAME = "prolinks";
  public static final String GENE_ID_TYPE = "ProlinksID";
  
  
  /**
   * Empty constructor
   */
  public ProlinksInteractionsSource (){
	  //TODO: Remove, this should be read from somewhere!!!
	  this("jdbc:mysql://biounder.kaist.ac.kr/prolinks?user=bioinfo&password=qkdldhWkd");
  }
  
  /**
   * @param mysql_url the URL of the mySQL data base
   */
  public ProlinksInteractionsSource (String mysql_url){
    super(mysql_url,SQLDBHandler.MYSQL_JDBC_DRIVER);
  }//ProlinksInteractionsSource
  
  // Methods implementing DataSource interface:
  /**
   * @return a Vector of Strings that specify types of IDs that this InteractionsDataSource accepts
   * for example, "ORF","GI", etc.
   */
  public Vector getIDtypes (){
	  Vector ids = new Vector();
	  ids.add(GENE_ID_TYPE);
	  return ids;
  }//getIDTypes
  
  /**
   * @return the name of the data source, for example, "KEGG", "Prolinks", etc.
   */
  public String getDataSourceName (){
    return NAME;
  }//getDataSourceName
  
  /**
   * @return the type of backend implementation (how requests to the data source
   * are implemented) one of WEB_SERVICE, LOCAL_DB, REMOTE_DB, MEMORY, MIXED
   */
  public String getBackendType (){
    // need to figure out if mySQL DB is running locally or remotely
    return ""; // for now
  }

  /**
   * @return a Vector of Strings representing the species for which the data
   * source contains information
   */
  public Vector getSupportedSpecies (){
	  String sql = "SELECT species FROM species";
	  ResultSet rs = query(sql);
	  if(rs == null){
		  return new Vector();
	  }
	  Vector species = new Vector();
	  try{
		  while(rs.next()){
			  String sp = (String)rs.getObject(1);
			  species.add(sp);
		  }
	  }catch (SQLException e){
		  e.printStackTrace();
		  return species;
	  }
	  return species;
  }

  /**
   * @return a String denoting the version of the data source (could be a release date,
   * a version number, etc).
   */

  public String getVersion (){
	  // TODO:
	  // this is a problem because we connected to prolinks not metainfo!!!
      // db table should contains meta-information like this
	  // - Possible solution: MetaInfoHandler???
	  //String sql = "SELECT timestamp FROM when_updated WHERE db=\"metainfo\"";
	  //ResultSet rs = query(sql);
	  //if(rs == null)
		//  return "ERROR";
	  //try{
		//  if(rs.last()){
			//  Timestamp ts = rs.getTimestamp(1);
			  //return ts.toString();
		 // }
	  //}catch (SQLException e){
		//  e.printStackTrace();
		  //return "ERROR";
	  //}
    //return "ERROR";
	  return "NOT IMPLEMENTED"; // for now
  }

  /**
   * @return boolean whether or not this data source requires a password from the user
   * in order to access it
   */
  public boolean requiresPassword (){
    return false;
  }
  
  // Methods implementing InteractionsDataSource interface:

  //------------------------ get interactions en masse --------------------
  /**
   * @param species the name of the species (should be one of getSupportedSpecies)
   * @return a Vector of Hashtables, each hash contains information about an
   * interaction and is required to contain the following entries:<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables
   */
  public Vector getAllInteractions (String species){
	  // need to find out if the table for this species is divided into low/high
	  // TODO: For this, we need to once more, be able to get to meta_info...
	  // ask Jung to add this info to the prolinks DB, not meta-info
	  
    return new Vector();
  }
  
  /**
   * @param species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions, etc)
   * @return a Vector of Hashtables, each hash contains information about an
   * interaction and is required to contain the following entries:<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables
   */
  public Vector getAllInteractions (String species, Hashtable args){
    return new Vector();
  }
  
  // Method that implement InteractionsDataSource:
  
  //-------------------------- 1st neighbor methods ---------------------------
    
  /**
   * @param interactor an id that the data source understands
   * @param species the species
   * @return a Vector of Strings of all the nodes that
   * have a direct interaction with "interactor", or an empty vector
   * if no interactors are found, the interactor is not in the
   * data source, or, the species is not supported
   */
  public Vector getFirstNeighbors (String interactor, String species){
    return new Vector();
  }
  
  /**
   * @param interactor an id that the data source understands
   * @param species the species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions, etc)
   * @return a Vector of Strings of all the nodes that
   * have a direct interaction with "interactor" and that take into
   * account additional parameters given in the Hashtable (an empty
   * vector if the interactor is not found, the interactor has no interactions,
   * or the data source does not contain infomation for the given interactor)
   */
  public Vector getFirstNeighbors (String interactor, String species, Hashtable args){
    return new Vector();
  }


  /**
   * @param interactors a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @return a Vector of Vectors of String ids of all the nodes that
   * have a direct interaction with the interactors in the given input vector, positions
   * in the input and output vectors are matched (parallel vectors)
   */
  public Vector getFirstNeighbors (Vector interactors, String species){
    return new Vector();
  }
  
  /**
   * @param interactor a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions, etc)
   * @return a Vector of Vectors of String ids of all the nodes that
   * have a direct interaction with the interactors in the given input vector, positions
   * in the input and output vectors are matched (parallel vectors)
   */
  public Vector getFirstNeighbors (Vector interactors, String species, Hashtable args){
    return new Vector();
  }

  /**
   * @param interactor an id that the data source understands
   * @param species the species
   * @return a Vector of Hashtables, each hash contains information about an
   * interaction (they are required to contain the following entries:)<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables
   */
  public Vector getAdjacentInteractions (String interactor, String species){
    return new Vector();
  }


  /**
   * @param interactor an id that the data source understands
   * @param species the species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions only, etc)
   * @return a Vector of Hashtables, each hash contains information about an
   * interaction (they are required to contain the following entries:)<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables
   */
  public Vector getAdjacentInteractions (String interactor, String species, Hashtable args){return new Vector();}


    /**
   * @param interactors a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @return a Vector of Vectors of Hashtables, each hash contains information about an
   * interaction (they are required to contain the following entries:)<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables.<br>
   * The input and output vectors are parallel.
   */
  public Vector getAdjacentInteractions (Vector interactors, String species){return new Vector();}

  /**
   * @param interactor a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions only, etc)
   * @return a Vector of Vectors of Hashtables, each hash contains information about an
   * interaction (they are required to contain the following entries:)<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables.<br>
   * The input and output vectors are parallel.
   */
  public Vector getAdjacentInteractions (Vector interactors, String species, Hashtable args){return new Vector();}

  //-------------------------- connecting interactions methods -----------------------

  /**
   * @param interactor1
   * @param interactor2
   * @param species
   * @return a Vector of Hashtables, each hash contains information about an
   * interaction between the two interactors, each hash contains these entries:<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables 
   */
  public Vector getConnectingInteractions (String interactor1, String interactor2, String species){return new Vector();}
  
  /**
   * @param interactor1
   * @param interactor2
   * @param species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions only, etc)
   * @return a Vector of Hashtables, each hash contains information about an
   * interaction between the two interactors, each hash contains these entries:<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables 
   */
  public Vector getConnectingInteractions (String interactor1, String interactor2, 
                                           String species, Hashtable args){return new Vector();}
  /**
   * @param interactors
   * @param species
   * @return a Vector of Hashtables, each hash contains information about an
   * interaction between the two interactors, each hash contains these entries:<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables 
   */
  public Vector getConnectingInteractions (Vector interactors, String species){return new Vector();}
  
  /**
   * @param interactors
   * @param species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions only, etc)
   * @return a Vector of Hashtables, each hash contains information about an
   * interaction between the two interactors, each hash contains these entries:<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables 
   */
  public Vector getConnectingInteractions (Vector interactors, String species, Hashtable args){return new Vector();}
  
}//ProlinksInteractionsSource
