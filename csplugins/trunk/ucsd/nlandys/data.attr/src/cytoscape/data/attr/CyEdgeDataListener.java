package cytoscape.data.attr;

public interface CyEdgeDataListener
{

  /**
   * @param keyIntoValue don't modify this array; this array will be
   *   null if attributeName has a zero-dimensional keyspace.
   */
  public void edgeAttributeValueAssigned(String edgeKey,
                                         String attributeName,
                                         Object[] keyIntoValue,
                                         Object attributeValue);

  /**
   * @param keyIntoValue don't modify this array; this array will be
   *   null if attributeName has a zero-dimensional keyspace.
   */
  public void edgeAttributeValueRemoved(String edgeKey,
                                        String attributeName,
                                        Object[] keyIntoValue,
                                        Object attributeValue);

  /**
   * @param keyPrefix don't modify this array; this array may be null,
   *   and will never be of length zero; a null array implies that all
   *   attribute values on specified node in attributeName definition have
   *   been deleted.
   */
  public void edgeAttributeKeyspanRemoved(String edgeKey,
                                          String attributeName,
                                          Object[] keyPrefix);

}
