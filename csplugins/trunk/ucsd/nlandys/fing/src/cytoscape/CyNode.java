package cytoscape;

import giny.model.*;



public interface CyNode extends Node {

  /**
   * Gets the Identifier for this Node, this is often used 
   * for getting the Visible Label
   */
  public String getIdentifier () ;
  
  /**
   * Sets the Identifier for this Node, this is often used 
   * for getting the Visible Label
   */
  public boolean setIdentifier ( String new_id ) ;
  
  /**
   * Gets the Identifier for this Node, this is often used 
   * for getting the Visible Label
   */
  public String toString () ;


  /**
   * The UID for this node. Use it to get info from NetworkData via
   * the NetworkData.getNodeData( int, string ) methods
   */
  public int getUniqueIdentifier () ;

   
       
  
} 
