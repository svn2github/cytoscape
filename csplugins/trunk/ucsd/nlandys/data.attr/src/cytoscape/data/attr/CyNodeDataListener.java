package cytoscape.data.attr;

public interface CyNodeDataListener
{

  /**
   * @param keyIntoValue don't modify this array!
   */
  public void nodeAttributeValueAssigned(String nodeKey,
                                         String attributeName,
                                         Object[] keyIntoValue,
                                         Object attributeValue);

  /**
   * @param keyIntoValue don't modify this array!
   */
  public void nodeAttributeValueRemoved(String nodeKey,
                                        String attributeName,
                                        Object[] keyIntoValue,
                                        Object attributeValue);

  /**
   * @param keyPrefix don't modify this array!
   */
  public void nodeAttributeKeyspanRemoved(String nodeKey,
                                          String attributeName,
                                          Object[] keyPrefix);

}
