package cytoscape.data.attr;

/**
 * A hook to receive notification when attribute values are set and removed
 * from objects.
 */
public interface MultiHashMapListener
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
  public void attributeValueAssigned(String objectKey,
                                     String attributeName,
                                     Object[] keyIntoValue,
                                     Object oldAttributeValue,
                                     Object newAttributeValue);

  /**
   * This listener method gets called as a result of
   * MultiHashMap.removeAttributeValue(objectKey, attributeName, keyIntoValue),
   * but only if an attribute value was found [and removed] for specified key.
   * The parameter attributeValue in this listener method is the value that
   * is returned by MultiHashMap.removeAttributeValue(), and it is never null.
   * @param keyIntoValue don't modify this array; this array will be
   *   null if attributeName has a zero-dimensional keyspace.
   */
  public void attributeValueRemoved(String objectKey,
                                    String attributeName,
                                    Object[] keyIntoValue,
                                    Object attributeValue);

  /**
   * This listener method gets called as a result of
   * MultiHashMap.removeAllAttributeValues(objectKey, attributeName), but only
   * if objectKey has at least one attribute value bound in attributeName.
   */
  public void allAttributeValuesRemoved(String objectKey,
                                        String attributeName);

}
