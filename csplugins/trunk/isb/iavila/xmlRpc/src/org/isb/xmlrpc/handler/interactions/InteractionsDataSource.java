package org.isb.xmlrpc.handlers.interactions;


import java.lang.*;
import java.util.*;
import org.isb.xmlrpc.handlers.DataSource;

/**
 * Some methods take a Vector of interactors so that time is saved in making one XML-RPC
 * request as opposed to Vector.size() requests.
 *
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */

public interface InteractionsDataSource extends DataSource {

  public static final String INTERACTOR_1 = "i1";
  public static final String INTERACTOR_2 = "i2";
  public static final String INTERACTION_TYPE = "itype";

  //TODO: Need to create a vocabulary of ID types????
  /**
   * @return a Vector of Strings that specify types of IDs that this InteractionsDataSource accepts
   * for example, "ORF","GI", etc.
   */
  public Vector getIDtypes ();
  
  //------------------------ get interactions en masse --------------------
  /**
   * @param species
   * @return a Vector of Hashtables, each hash contains information about an
   * interaction and is required to contain the following entries:<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables
   */
  public Vector getAllInteractions (String species);
  
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
  public Vector getAllInteractions (String species, Hashtable args);
  
  
  //-------------------------- 1st neighbor methods ---------------------------
    
  /**
   * @param interactor an id that the data source understands
   * @param species the species
   * @return a Vector of Strings of all the nodes that
   * have a direct interaction with "interactor", or an empty vector
   * if no interactors are found, the interactor is not in the
   * data source, or, the species is not supported
   */
  public Vector getFirstNeighbors (String interactor, String species);
  
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
  public Vector getFirstNeighbors (String interactor, String species, Hashtable args);


  /**
   * @param interactors a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @return a Vector of Vectors of String ids of all the nodes that
   * have a direct interaction with the interactors in the given input vector, positions
   * in the input and output vectors are matched (parallel vectors)
   */
  public Vector getFirstNeighbors (Vector interactors, String species);
  
  /**
   * @param interactor a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions, etc)
   * @return a Vector of Vectors of String ids of all the nodes that
   * have a direct interaction with the interactors in the given input vector, positions
   * in the input and output vectors are matched (parallel vectors)
   */
  public Vector getFirstNeighbors (Vector interactors, String species, Hashtable args);

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
  public Vector getAdjacentInteractions (String interactor, String species);


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
  public Vector getAdjacentInteractions (String interactor, String species, Hashtable args);


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
  public Vector getAdjacentInteractions (Vector interactors, String species);


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
  public Vector getAdjacentInteractions (Vector interactors, String species, Hashtable args);

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
  public Vector getConnectingInteractions (String interactor1, String interactor2, String species);
  
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
                                           String species, Hashtable args);
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
  public Vector getConnectingInteractions (Vector interactors, String species);
  
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
  public Vector getConnectingInteractions (Vector interactors, String species, Hashtable args);
  
}//InteractionsDataSource
