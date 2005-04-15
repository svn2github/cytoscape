package cytoscape.data.attr;

public interface CyNodeDataDefinitionListener
{

  public void nodeAttributeDefined(String attributeName);

  public void nodeAttributeUndefined(String attributeName);

}
