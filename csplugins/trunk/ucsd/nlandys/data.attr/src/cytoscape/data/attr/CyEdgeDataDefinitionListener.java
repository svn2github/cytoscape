package cytoscape.data.attr;

public interface CyEdgeDataDefinitionListener
{

  public void edgeAttributeDefined(String attributeName);

  public void edgeAttributeUndefined(String attributeName);

}
