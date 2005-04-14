package cytoscape.data.attr;

import java.util.Enumeration;

public interface CyNodeData
{

  /**
   * @param nodeKey the node to which to bind a new attribute value.
   * @param attrName the attribute definition in which to assign an
   *   attribute value.
   * @param keyIntoValue an array of length N - 1 where N is the
   *   dimensionality of attribute attrName; this set of objects is
   *   a key that will uniquely define the attribute value for this node
   *   in the specified attribute definition; the entry at index i in this
   *   array must correspond to the type of dimension i + 1 of the attribute
   *   definition (for example if attribuute attrName has two or more
   *   dimensions and if the first dimension of attribute attrName
   *   is of type
   *   CyNodeDataDef.DIM_TYPE_INTEGER, then the element at index zero in this
   *   array must be of type java.lang.Long); keyIntoValue may be null if
   *   the dimenstionality of attribute attrName is one.
   * @param attrValue the attribute value to bind;
   *   the type of this object must be of the appropriate type based on
   *   the final dimension of the attribute definition.
   */
  public void setAttributeValue(String nodeKey, String attrName,
                                Object[] keyIntoValue, Object attrValue);

  /**
   * @param delete if true, also deletes this attribute value; otherwise
   *   keeps this attribute value.
   * @return the same value that was set with setAttributeValue() with
   *   parameters specified.
   */
  public Object getAttributeValue(String nodeKey, String attrName,
                                  Object[] keyIntoValue, boolean delete);


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
