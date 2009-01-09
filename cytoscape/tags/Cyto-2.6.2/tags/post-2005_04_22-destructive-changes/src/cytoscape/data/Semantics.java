package cytoscape.data;

import java.util.*;

import giny.model.Edge;

import cytoscape.*;
import cytoscape.CytoscapeInit;
import cytoscape.data.GraphObjAttributes;

/**
 * This class defines names for certain data attributes that are commonly used
 * within Cytoscape. The constants defined here are provided to enable different
 * modules to use the same name when referring to the same conceptual attribute.
 *
 * This class also defines some static methods for assigning these attributes
 * to a network, given the objects that serve as the source for this information.
 */
public class Semantics {

  public static final String IDENTIFIER = "identifier";
  public static final String COMMON_NAME = "commonName";
  public static final String SPECIES = "species";
  public static final String INTERACTION = "interaction";
  public static final String MOLECULE_TYPE = "molecule_type";
  public static final String PROTEIN = "protein";
  public static final String DNA = "DNA";
  public static final String RNA = "RNA";
  public static final String MOLECULAR_FUNCTION = "molecular_function";
  public static final String BIOLOGICAL_PROCESS = "biological_process";
  public static final String CELLULAR_COMPONENT = "cellular_component";

  public static final String CANONICAL_NAME = "canonical_name";
  
}

