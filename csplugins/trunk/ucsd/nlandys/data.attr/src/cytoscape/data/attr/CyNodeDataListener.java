package cytoscape.attr.data;

public interface CyNodeDataListener
{

  /**
   * @param keyIntoValue don't modify this array!
   */
  public void nodeAttributeValueAssigned(String nodeKey,
                                         String attributeName,
                                         Object[] keuIntoValue,
                                         Object attributeValue);

  /**
   * @param keyIntoValue don't modify this array!
   */
  public void nodeAttributeValueRemoved(String nodeKey,
                                        String attributeName,
                                        Object[] keyIntoValue,
                                        Object attributeValue);

  /**
   * @param prefix don't modify this array!
   */
  public void nodeAttributeKeyspanRemoved(String nodeKey,
                                          String attributeName,
                                          Object[] keyPrefix);

}
