package org.isb.bionet.datasource.interactions;

import java.util.*;
import org.isb.bionet.datasource.*;

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
  public static final String SOURCE = "src";
  public static final String DIRECTED = "dir";
  public static final String CONFIDENCE = "confidence";
  public static final int CONNECTING_EDGES = 0;
  public static final int ADJACENT_EDGES = 1;
  public static final int ALL_EDGES = 2;

  /**
   * @return String to specify type of ID that this InteractionsDataSource accepts
   * for example, SynonymsSource.GI_ID, etc.
   */
  public String getIDtype ();
  
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
   * @return the number of interactions
   */
  public Integer getNumAllInteractions (String species);
  
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
  
  /**
   * @param species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions, etc)
   * @return the number of interactions
   */
  public Integer getNumAllInteractions (String species, Hashtable args);
 
  
  
  //-------------------------- 1st neighbor methods ---------------------------
    
  /**
   * @param interactors a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @return a Vectors of String ids of all the nodes that
   * have a direct interaction with the interactors in the given input vector, positions
   * in the input and output vectors are matched (parallel vectors)
   */
  public Vector getFirstNeighbors (Vector interactors, String species);
  
  /**
   * @param interactors a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @return the number of interactors
   */
  public Integer getNumFirstNeighbors (Vector interactors, String species);
  
  
  /**
   * @param interactor a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions, etc)
   * @return a Vector of String ids of all the nodes that
   * have a direct interaction with the interactors in the given input vector, positions
   * in the input and output vectors are matched (parallel vectors)
   */
  public Vector getFirstNeighbors (Vector interactors, String species, Hashtable args);

  
  /**
   * @param interactor a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions, etc)
   * @return the number of interactors
   */
  public Integer getNumFirstNeighbors (Vector interactors, String species, Hashtable args);

  /**
   * @param interactors a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @return a Vector of Hashtables, each hash contains information about an
   * interaction (they are required to contain the following entries:)<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables.<br>
   * The input and output vectors are parallel.
   */
  public Vector getAdjacentInteractions (Vector interactors, String species);

  /**
   * @param interactors a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @return the number of adjacent interactions
   */
  public Integer getNumAdjacentInteractions (Vector interactors, String species);

  /**
   * @param interactor a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions only, etc)
   * @return a Vector of Hashtables, each hash contains information about an
   * interaction (they are required to contain the following entries:)<br>
   * INTERACTOR_1 --> String <br>
   * INTERACTOR_2 --> String <br>
   * INTERACTION_TYPE -->String <br>
   * Each implementing class can add additional entries to the Hashtables.<br>
   * The input and output vectors are parallel.
   */
  public Vector getAdjacentInteractions (Vector interactors, String species, Hashtable args);

  /**
   * @param interactor a Vector of Strings (ids that the data source understands)
   * @param species the species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions only, etc)
   * @return the number of adjacent interations
   */
  public Integer getNumAdjacentInteractions (Vector interactors, String species, Hashtable args);

  //-------------------------- connecting interactions methods -----------------------

  
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
   * @return the number of connecting interactions
   */
  public Integer getNumConnectingInteractions (Vector interactors, String species);
  
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
  
  /**
   * @param interactors
   * @param species
   * @param args a table of String->Object entries that the implementing
   * class understands (for example, p-value thresholds, directed interactions only, etc)
   * @return the number of connecting interactions
   */
  public Integer getNumConnectingInteractions (Vector interactors, String species, Hashtable args);
  
}//InteractionsDataSource
