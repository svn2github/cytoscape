// AnnotationDescription.java
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
public class AnnotationDescription implements Serializable {

  protected String species;         // halopbacterium sp, saccharomyces cerevisiae, ...
  protected String curator;         // KEGG, GO, ...
  protected String annotationType;  // biological process, metabolic pathway, ...

//------------------------------------------------------------------------------
/**
 *  @param species        The formal Linnaean name (genus & species) is preferred
 *  @param curator        The institute or group which maintains this annotation
 *  @param ontolotyType   The nature of this annotation, eg, "metabolic pathway",
 *                        "molecular function", or "cellular component"
 */
public AnnotationDescription (String species, String curator, String annotationType)
{
  this.species = species;
  this.curator = curator;
  this.annotationType = annotationType;

} // ctor
//------------------------------------------------------------------------------
public String getSpecies ()
{
  return species;
}
//------------------------------------------------------------------------------
public String getCurator ()
{
  return curator;
}
//------------------------------------------------------------------------------
public String getType ()
{
  return annotationType;
}
//------------------------------------------------------------------------------
/**
 *  redefine equals so that instances with the same contents are equal.
 *  this emulates the way that String equality is judged.
 */
public boolean equals (Object obj)
{
  if (!obj.getClass().equals (this.getClass()))
    return false;

  AnnotationDescription other = (AnnotationDescription) obj;

  if (species.equals (other.getSpecies ()) &&
      curator.equals (other.getCurator ()) &&
      annotationType.equals (other.getType ()))
    return true;

  return false;

} // equals
//------------------------------------------------------------------------------
/**
 *  redefine hashCode so that instances with the same contents have the
 *  same hashCode.  this will make them identical when used as the keys
 *  in HashMaps, the purpose for which they were invented.
 */
public int hashCode ()
{
  return species.hashCode () +
         curator.hashCode () +
         annotationType.hashCode ();

} // hashCode
//------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append ("annotation: " + curator + ", " + annotationType + ", " + species);

  return sb.toString ();

} // toString
//------------------------------------------------------------------------------
} // class AnnotationDescription
