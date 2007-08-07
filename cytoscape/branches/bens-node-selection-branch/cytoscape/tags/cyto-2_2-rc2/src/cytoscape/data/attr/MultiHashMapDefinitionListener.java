package cytoscape.data.attr;

/**
 * A hook to receive notification when attribute definitions are created
 * and destroyed.
 */
public interface MultiHashMapDefinitionListener
{

  /**
   * This method is called by a MultiHashMapDefinition implementation as a
   * result of a new attribute being defined
   * (MultiHashMapDefinition.defineAttribute()).
   */
  public void attributeDefined(String attributeName);

  /**
   * This method is called by a MultiHashMapDefinition implementation as a
   * result of an attribute being undefined
   * (MultiHashMapDefinition.undefineAttribute()).
   */
  public void attributeUndefined(String attributeName);

}
