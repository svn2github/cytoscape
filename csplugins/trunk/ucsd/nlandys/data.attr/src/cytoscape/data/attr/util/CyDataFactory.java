package cytoscape.data.attr.util;

public final class CyDataFactory
{

  // "No constructor".
  private CyDataFactory() { }

  /**
   * The return object is an implementation of the following interfaces:
   * <blockquote>
   * - cytoscape.data.attr.CyNodeDataDefinition
   * - cytoscape.data.attr.CyNodeData
   * - cytoscape.data.attr.CyEdgeDataDefinition
   * - cytoscape.data.attr.CyEdgeData
   * </blockquote>
   * You will need to cast the return value to one of these to access the
   * corresponding functionality.
   */
  public static Object instantiateDataModel()
  {
    return new CyDataModel();
  }

}
