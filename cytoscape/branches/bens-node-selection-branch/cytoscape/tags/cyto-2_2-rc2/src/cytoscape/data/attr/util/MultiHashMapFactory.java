package cytoscape.data.attr.util;

/**
 * This class provides access to implementations of MultiHashMap and
 * MultiHashMapDefinition.
 */
public final class MultiHashMapFactory
{

  // "No constructor".
  private MultiHashMapFactory() { }

  /**
   * The return object implements both
   * cytoscape.data.attr.MultiHashMapDefinition
   * and cytoscape.data.attr.MultiHashMap.
   * You will need to cast the return value to one of these to access the
   * corresponding functionality.
   */
  public final static Object instantiateDataModel()
  {
    return new MultiHashMapModel();
  }

}
