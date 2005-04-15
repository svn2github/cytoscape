package cytoscape.data.attr;

import java.util.Enumeration;

/**
 * This interface consists of the API specification to bind attribute
 * values to nodes.
 */
public interface CyNodeData
{

  /**
   * @param nodeKey the node to which to bind a new attribute value.
   * @param attributeName the attribute definition in which to assign an
   *   attribute value.
   * @param attributeValue the attribute value to bind;
   *   the type of this object must be of the appropriate type based on
   *   the value type of specified attribute definition.
   * @param keyIntoValue an array of length equal to the dimensionality of
   *   the key space of specified attribute definition; entry at index i
   *   is a "representative" from dimension i + 1 of the key space; if
   *   specified attribute definition has a zero-dimensional key space (this
   *   is perhaps the most common scenario) then
   *   this array may either be null or the empty array.
   */
  public void setNodeAttributeValue(String nodeKey, String attributeName,
                                    Object attributeValue,
                                    Object[] keyIntoValue);

  /**
   * @param nodeKey the node from which to retrieve a bound attribute
   *   value.
   * @param attributeName the attribute definition in which to assign an
   *   attribute value.
   * @param keyIntoValue an array of length equal to the dimensionality of
   *   the key space of specified attribute definition; entry at index i
   *   is a "representative" from dimension i + 1 of the key space; if
   *   specified attribute definition has a zero-dimensional key space (this
   *   is perhaps the most commen scenario) then this array may either
   *   be null or the empty array.
   * @return the same value that was set with setNodeAttributeValue() with
   *   parameters specified.
   */
  public Object getNodeAttributeValue(String nodeKey, String attributeName,
                                      Object[] keyIntoValue);


  /**
   * This method is the same as getNodeAttributeValue(), only the retrieved
   * attribute value is also deleted.
   * @see #getNodeAttributeValue(String, String, Object[])
   */
  public Object removeNodeAttributeValue(String nodeKey, String attributeName,
                                         Object[] keyIntoValue);

  /**
   * Returns the number of representatives in the dimension
   * prefix.length + 1, based on the prefix.
   */
  public int getAttributeSpanCount(String nodeKey, String attrName,
                                   Object[] prefix);

  /**
   * @param delete if true, all value entries with specified prefix are also
   *   deleted from specified nodeKey.
   * @return representatives, along specified prefix, of
   * dimension prefix.length + 1.  The type of objects in the returned
   * enumeration will all be of the type as specified by the
   * dimension prefix.length + 1 of attribute attrName.
   */
  public Enumeration getAttributeSpan(String nodeKey, String attrName,
                                      Object[] prefix, boolean delete);

  public void addListener(CyNodeDataListener listener);

  public void removeListener(CyNodeDataListener listener);

}
