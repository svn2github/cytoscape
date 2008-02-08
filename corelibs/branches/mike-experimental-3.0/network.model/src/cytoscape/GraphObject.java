package cytoscape;

import java.util.*;

public interface GraphObject {

  /**
   * @return The Unique Identifier of this node
   */
  public String getIdentifier ();

  /**
   * <B>There is no check to make sure that this is a unique id</B>
   * @param new_id The new Identifier for this node
   */
  public boolean setIdentifier ( String new_id );

   /**
   * @return the index in the RootGraph of this Edge
   */
  public int getRootGraphIndex ();

  
  /**
   * @return the RootGraph that this Node is in
   */
  public RootGraph getRootGraph ();


}
