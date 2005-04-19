package cytoscape.data.attr;

public interface CyNodeDataListener
{

  /**
   * @param keyIntoValue don't modify this array; this array will be
   *   null if attributeName has a zero-dimensional keyspace.
   * @param oldAttributeValue the previous attribute value that was bound
   *   at specified key sequence, or null if no previous attribute value was
   *   bound.
   * @param newAttributeValue the new attribute value that is now bound
   *   at specified key sequence.
   */
  public void nodeAttributeValueAssigned(String nodeKey,
                                         String attributeName,
                                         Object[] keyIntoValue,
                                         Object oldAttributeValue,
                                         Object newAttributeValue);

  /**
   * @param keyIntoValue don't modify this array; this array will be
   *   null if attributeName has a zero-dimensional keyspace.
   */
  public void nodeAttributeValueRemoved(String nodeKey,
                                        String attributeName,
                                        Object[] keyIntoValue,
                                        Object attributeValue);

  /**
   * @param keyPrefix don't modify this array; this array may be null,
   *   and will never be of length zero; a null array implies that all
   *   attribute values on specified node in attributeName definition have
   *   been deleted.
   */
  public void nodeAttributeKeyspanRemoved(String nodeKey,
                                          String attributeName,
                                          Object[] keyPrefix);

}
