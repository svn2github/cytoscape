package datamap;

/**
 *    Objects who implement SharedIdentifiable are willing to
 * possibly share an identifier with other objects.  Objects
 * will only share an identifier if they represent the same
 * real-life object.  It is also not necessary that every 
 * object that shares an indentifier implement this class.  
 * Since implementing SharedIdentifiable only guarentees that
 * objects sharing an Indentifier get a unique identifier in 
 * the @see{DataPropertyMap}, it is possible that an object might 
 * be known only by a reference, like nodes and edges are in GINY.
 */
public interface SharedIdentifiable {

  /**
   * Returns the Identifier for this object.
   */
  public String getIdentifier ();

}
