package cytoscape.data.attr;

public interface CyDataDefinitionListener
{

  public void attributeDefined(String attributeName);

  public void attributeUndefined(String attributeName);

}
