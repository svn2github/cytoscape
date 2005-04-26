package cytoscape.data.attr;

/**
 * A hook to receive notification when attribute definitions are created
 * and destroyed.
 */
public interface CyDataDefinitionListener
{

  /**
   * This method is called by a CyDataDefinition implementation as a result
   * of a new attribute being defined (CyDataDefinition.defineAttribute()).
   */
  public void attributeDefined(String attributeName);

  /**
   * This method is called by a CyDataDefinition implementation as a result
   * of an attribute being undefined (CyDataDefinition.undefineAttribute()).
   */
  public void attributeUndefined(String attributeName);

}
