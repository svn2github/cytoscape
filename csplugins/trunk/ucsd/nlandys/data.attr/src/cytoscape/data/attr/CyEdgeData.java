package cytoscape.data.attr;

import java.util.Enumeration;

/**
 * This interface consists of the API specification to bind attribute
 * values to edges.
 */
public interface CyEdgeData
{

  /**
   * @param edgeKey the edge to which to bind a new attribute value.
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
  public void setEdgeAttributeValue(String edgeKey, String attributeName,
                                    Object attributeValue,
                                    Object[] keyIntoValue);

  /**
   * @param edgeKey the edge from which to retrieve a bound attribute
   *   value.
   * @param attributeName the attribute definition in which to assign an
   *   attribute value.
   * @param keyIntoValue an array of length equal to the dimensionality of
   *   the key space of specified attribute definition; entry at index i
   *   is a "representative" from dimension i + 1 of the key space; if
   *   specified attribute definition has a zero-dimensional key space (this
   *   is perhaps the most commen scenario) then this array may either
   *   be null or the empty array.
   * @return the same value that was set with setEdgeAttributeValue() with
   *   parameters specified.
   */
  public Object getEdgeAttributeValue(String edgeKey, String attributeName,
                                      Object[] keyIntoValue);


  /**
   * This method is the same as getEdgeAttributeValue(), only the retrieved
   * attribute value is also deleted.
   * @see #getEdgeAttributeValue(String, String, Object[])
   */
  public Object removeEdgeAttributeValue(String edgeKey, String attributeName,
                                         Object[] keyIntoValue);

  /**
   * For all bound attribute values on edgeKey in the specified attribute
   * definition, returns the number of representatives in the key space in
   * dimension keyPrefix.length + 1, along specified prefix.
   * @param edgeKey the edge to query.
   * @param attributeName the attribute definition to query.
   * @param keyPrefix an array of length K where K is strictly less than N,
   *   the dimensionality of key space of specified attribute definition;
   *   entry at index i contains a "representative" from dimension i + 1 of
   *   the key space of specified attribute definition; this parameter may
   *   be either null or the empty array, in which case the count returned
   *   is the number of representatives in the first dimension of
   *   key space.
   * @return the number of keys in key space dimension K + 1 along specified
   *   keyPrefix.
   */
  public int getEdgeAttributeKeyspanCount(String edgeKey, String attributeName,
                                          Object[] keyPrefix);

  /**
   * This method is the same as getEdgeAttributeKeyspanCount(), only
   * the actual representatives are returned, and not their count.
   * @see #getEdgeAttributeKeyspanCount(String, String, Object[])
   */
  public Enumeration getEdgeAttributeKeyspan(String edgeKey,
                                             String attributeName,
                                             Object[] keyPrefix);

  /**
   * @return the number of 
   */
  public int removeEdgeAttributeKeyspan(String edgeKey,
                                        String attributeName,
                                        Object[] keyPrefix);

  public void addEdgeDataListener(CyEdgeDataListener listener);

  public void removeEdgeDataListener(CyEdgeDataListener listener);

}
