package cytoscape.data.attr.util;

final class CyDataModel
  implements CyNodeDataDefinition, CyNodeData, CyEdgeDataDefinition, CyEdgeData
{

  CyDataModel()
  {
  }

  public void defineNodeAttribute(String attributeName,
                                  byte valueType,
                                  byte[] keyTypes,
                                  String[] keyNames)
  {
  }

  public byte getNodeAttributeValueType(String attributeName)
  {
    return -1;
  }

  public int getNodeAttributeKeyspaceDimensionality(String attributeName)
  {
    return -1;
  }

  public void getNodeAttributeKeyspaceInfo(String attributeName,
                                           byte[] keyTypes, String[] keyNames)
  {
  }

  public void undefineNodeAttribute(String attributeName)
  {
  }

  public void addNodeDataDefinitionListener(
                                         CyNodeDataDefinitionListener listener)
  {
  }

  public void removeNodeDataDefinitionListener(
                                         CyNodeDataDefinitionListener listener)
  {
  }

  public void setNodeAttributeValue(String nodeKey, String attributeName,
                                    Object attributeValue,
                                    Object[] keyIntoValue)
  {
  }

  public Object getNodeAttributeValue(String nodeKey, String attributeName,
                                      Object[] keyIntoValue)
  {
    return null;
  }

  public int getNodeAttributeKeyspanCount(String nodeKey, String attributeName,
                                          Object[] keyPrefix)
  {
    return -1;
  }

  public Enumeration getNodeAttributeKeyspan(String nodeKey,
                                             String attributeName,
                                             Object[] keyPrefix)
  {
    return null;
  }

  public int removeNodeAttributeKeyspan(String nodeKey,
                                        String attributeName,
                                        Object[] keyPrefix)
  {
    return -1;
  }

  public void addNodeDataListener(CyNodeDataListener listener)
  {
  }

  public void removeNodeDataListener(CyNodeDataListener listener)
  {
  }

  public void defineEdgeAttribute(String attributeName,
                                  byte valueType,
                                  byte[] keyTypes,
                                  String[] keyNames)
  {
  }

  public byte getEdgeAttributeValueType(String attributeName)
  {
    return -1;
  }

  public int getEdgeAttributeKeyspaceDimensionality(String attributeName)
  {
    return -1;
  }

  public void getEdgeAttributeKeyspaceInfo(String attributeName,
                                           byte[] keyTypes, String[] keyNames)
  {
  }

  public void undefineEdgeAttribute(String attributeName)
  {
  }

  public void addEdgeDataDefinitionListener(
                                         CyEdgeDataDefinitionListener listener)
  {
  }

  public void removeEdgeDataDefinitionListener(
                                         CyEdgeDataDefinitionListener listener)
  {
  }

  public void setEdgeAttributeValue(String edgeKey, String attributeName,
                                    Object attributeValue,
                                    Object[] keyIntoValue)
  {
  }

  public Object getEdgeAttributeValue(String edgeKey, String attributeName,
                                      Object[] keyIntoValue)
  {
    return null;
  }

  public int getEdgeAttributeKeyspanCount(String edgeKey, String attributeName,
                                          Object[] keyPrefix)
  {
    return -1;
  }

  public Enumeration getEdgeAttributeKeyspan(String edgeKey,
                                             String attributeName,
                                             Object[] keyPrefix)
  {
    return null;
  }

  public int removeEdgeAttributeKeyspan(String edgeKey,
                                        String attributeName,
                                        Object[] keyPrefix)
  {
    return -1;
  }

  public void addEdgeDataListener(CyEdgeDataListener listener)
  {
  }

  public void removeEdgeDataListener(CyEdgeDataListener listener)
  {
  }

}
