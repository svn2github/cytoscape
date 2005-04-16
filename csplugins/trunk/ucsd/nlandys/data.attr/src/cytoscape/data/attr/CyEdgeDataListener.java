package cytoscape.data.attr;

public interface CyEdgeDataListener
{

  /**
   * @param keyIntoValue don't modify this array!
   */
  public void edgeAttributeValueAssigned(String edgeKey,
                                         String attributeName,
                                         Object[] keyIntoValue,
                                         Object attributeValue);

  /**
   * @param keyIntoValue don't modify this array!
   */
  public void edgeAttributeValueRemoved(String edgeKey,
                                        String attributeName,
                                        Object[] keyIntoValue,
                                        Object attributeValue);

  /**
   * @param keyPrefix don't modify this array!
   */
  public void edgeAttributeKeyspanRemoved(String edgeKey,
                                          String attributeName,
                                          Object[] keyPrefix);

}
