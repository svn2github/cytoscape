package org.isb.bionet.datasource.interactions;

import java.util.*;
import org.isb.bionet.datasource.*;

/**
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */

public interface PathwayDataSource extends DataSource {


  //----- Methods to map pathway ids to their human understandable names --
  
  /**
   * @param species the species for which the pathways are returned
   * @return a Vector of all the pathway ids in this data source
   */
  public Vector getAllPathwayIDs (String species);
  
  /**
   * @param species the species for which the pathway names are returned
   * @return the human understandable names of the available pathways for
   * the given organism
   */
  public Vector getAllPathwayNames (String species);

  /**
   * In KEGG, the pathway id contains the organism
   * @param pathway_id the id that the data source understands
   * @return a String with a name for the given pathway id, e.g. "Glycolysis"
   */
  public String getPathwayName (String pathway_id);
    
  /**
   * @return a parallel vector with the names for the given pathway_ids
   */
  public Vector getPathwayNames (Vector pathway_ids);

  //---- Methods to get the pathways in which genes/gene products are involved --
  
  // QUESTION: Do we need to get pathway ids by enzymes, compounds, glycans, 
  // or reactions???
  
  /**
   * @return all the pathway ids in which the given gene participates
   */
  public Vector getPathwayIDsByGene (String gene_id);

  /**
   * @return parallel Vector of Vectors of all the pathway ids in which 
   * the given gene participates
   */
  public Vector getPathwayIDsByGenes (Vector gene_ids);

  //----- Methods to get the genes involved in a pathway ------------------
 
  /**
   * @return gene ids in the given pathway
   */
  public Vector getGenesInPathway (String pathway_id);

  /**
   * @return a parallel Vector of Vectors with gene ids in the given pathways
   */
  public Vector getGenesInPathways (Vector pathway_ids);
  
  
}//PathwayDataSource
