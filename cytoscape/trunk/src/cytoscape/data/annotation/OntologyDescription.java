// OntologyDescription.java
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.annotation;
//-----------------------------------------------------------------------------------
import java.util.*;
import java.io.*;
//------------------------------------------------------------------------------
/**
 *  Distinguish among different ontologies, by curator and type.
 *  For example, KEGG maintains two ontologies:  metabolic pathways,
 *  and regulatory pathways.  GO has three: biological process, molecular 
 *  function, cellular component.
 *  This simple class captures these distinctions
 *  It will perhaps prove most useful when some data source (a biodata server,
 *  for example) contains a number of ontologies, and needs to communicate
 *  a summary of these to a client.
 */
public class OntologyDescription implements Serializable {

  protected String curator;       // KEGG, GO, ...
  protected String ontologyType;  // biological process, metabolic pathway, ...

//------------------------------------------------------------------------------
/**
 *  @param curator        The institute or group which maintains this ontology
 *  @param ontolotyType   The nature of this ontology, eg, "metabolic pathway",
 *                        "molecular function", or "cellular component"
 */
public OntologyDescription (String curator, String ontologyType)
{
  this.curator = curator;
  this.ontologyType = ontologyType;

} // ctor
//------------------------------------------------------------------------------
public String getCurator ()
{
  return curator;
}
//------------------------------------------------------------------------------
public String getType ()
{
  return ontologyType;
}
//------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append ("ontology: " + curator + ", " + ontologyType);

  return sb.toString ();

} // toString
//------------------------------------------------------------------------------
} // class OntologyDescription
