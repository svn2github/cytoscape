package cytoscape.data.attr;

import java.util.Enumeration;

public interface CyNodeData
{

  /**
   * @param attrValue the attribute value to set on specified nodeKey;
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
  public Object getMultiAttrValue(String nodeKey, String attrName,
                                  Object[] keyIntoValue, boolean delete);


  /**
   * Returns the number of representatives in the dimension
   * prefix.length + 1, based on the prefix.
   */
  public int getAttributeSpanCount(String nodeKey, String attrName,
                                   Object[] prefix);

  /**
   * Returns representatives, along specified prefix, of
   * dimension prefix.length + 1.  The type of object in the returned
   * enumeration will all be of the type as specified by the
   * dimension prefix.length + 1.
   * @param delete if true, all value entries with specified prefix are also
   *   deleted from specified nodeKey.
   */
  public Enumeration getAttributeSpan(String nodeKey, String attrName,
                                      Object[] prefix, boolean delete);

}
