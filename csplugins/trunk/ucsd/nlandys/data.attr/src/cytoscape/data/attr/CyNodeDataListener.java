package cytoscape.attr.data;

public interface CyNodeDataListener
{

  /**
   * @param keyIntoValue don't modify this array!
   */
  public void attributeValueAssigned(String nodeKey, String attrName,
                                     Object[] keuIntoValue, Object attrValue);

  /**
   * @param keyIntoValue don't modify this array!
   */
  public void attributeValueRemoved(String nodeKey, String attrName,
                                    Object[] keyIntoValue, Object attrValue);

  /**
   * @param prefix don't modify this array!
   */
  public void attributeSpanRemoved(String nodeKey, String attrName,
                                   Object[] prefix);

}
