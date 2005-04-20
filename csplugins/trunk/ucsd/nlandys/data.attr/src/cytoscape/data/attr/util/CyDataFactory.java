package cytoscape.data.attr.util;

public final class CyDataFactory
{

  // "No constructor".
  private CyDataFactory() { }

  /**
   * The return object implements both cytoscape.data.attr.CyDataDefinition
   * and cytoscape.data.attr.CyData.
   * You will need to cast the return value to one of these to access the
   * corresponding functionality.
   */
  public final static Object instantiateDataModel()
  {
    return new CyDataModel();
  }

}
