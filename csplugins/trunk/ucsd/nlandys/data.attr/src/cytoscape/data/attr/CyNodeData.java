package cytoscape.data.attr;

/**
 * This API is one that plugin developers have visibility into - the ability
 * to define, delete, or change a node's name is not included in this API.
 */
public interface CyNodeData
{

  /**
   * @exception UnsupportedOperationException 
   */
  public void defineNodeAttribute(String attrName, byte attrType);

  /**
   * The "un"-define of an attribute node.
   * @exception UnsupportedOperationException if the specified attribute domain
   *   exists but permission to delete it is not granted; for example, the
   *   "nodeName" attribute domain always exists and can never be deleted.
   */
  public void removeNodeAttribute(String attrName);

}
