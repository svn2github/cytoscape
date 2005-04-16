package cytoscape.data.attr.util;

public final class CyDataFactory
{

  // "No constructor".
  private CyDataFactory() { }

  /**
   * The return object is an implementation of the following interfaces:
   * <blockquote>
   * cytoscape.data.attr.CyNodeDataDefinition<br />
   * cytoscape.data.attr.CyNodeData<br />
   * cytoscape.data.attr.CyEdgeDataDefinition<br />
   * cytoscape.data.attr.CyEdgeData<br />
   * </blockquote>
   * You will need to cast the return value to one of these to access the
   * corresponding functionality.
   */
  public static Object instantiateDataModel()
  {
    return new CyDataModel();
  }

}
