package cytoscape;

import giny.model.*;

public interface CyEdge extends Edge {


  public String getIdentifier () ;

  public boolean setIdentifier ( String new_id ) ;
  
  public String toString () ;

  public int getUniqueIdentifier () ;

  public boolean isDirected () ;

  public CyNode getSourceNode () ;
           
  public CyNode getTargetNode () ;
  
 

}
