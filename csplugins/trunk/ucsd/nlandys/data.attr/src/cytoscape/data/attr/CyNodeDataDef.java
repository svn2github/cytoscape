import cytoscape.data.attr;

/**
 * This interface contains the API specification for creating node
 * attribute definitions.
 */
public interface CyNodeDataDef
{

  /**
   * This type corresponds to java.lang.Boolean.
   */
  public final byte DIM_TYPE_BOOLEAN = 1;

  /**
   * This type corresponds to java.lang.Double.
   */
  public final byte DIM_TYPE_FLOATING_POINT = 2;

  /**
   * This type corresponds to java.lang.Long.
   */
  public final byte DIM_TYPE_INTEGER = 3;

  /**
   * This type corresponds to java.lang.String.
   */
  public final byte DIM_TYPE_STRING = 4;

  /**
   * @param attrName a unique name for this attribute definition; this
   *   string should be interpreted as the [human-readable] name of
   *   dimension N, where N is the number of dimensions this attribute
   *   definition has.
   * @param dimTypes an array of length N, where N is the number of
   *   dimensions in this attribute definition; each entry in this array is
   *   one of the DIM_TYPE_* constants and the entry at index i in this
   *   array defines the type of dimension i + 1.
   * @param dimNames an array of length N - 1, where N is the number of
   *   dimensions in this attribute definition; the entry at index i in this
   *   array defines the [human-readable] name of dimension i + 1.
   */
  public void defineNodeAttribute(String attrName,
                                  byte[] dimTypes, String[] dimNames);

  /**
   * @return an enumeration of java.lang.String; each returned string
   * is an attrName (an attribute definition name).
   */
  public Enumeration getDefinedNodeAttributes();

  /**
   * @param attrName the name of attribute whose information we are
   *   querying.
   * @param dimTypes this array is copied into by this method; the array
   *   is populated with the type of each dimension; the array must be
   *   of length at least equal to the dimensionality of attribute attrName,
   *   and the dimension types are copied into this array starting at index
   *   zero of the array.
   * @param dimNames this array is copied into by this method; the array
   *   is populated with the name of each dimension but the last;
   *   the array must be of length at least equal to the dimensionality of
   *   attribute attrName minus one, and the dimension names are copied into
   *   this array starting at index zero of the array.
   */
  public void getAttributeInfo(String attrName,
                               byte[] dimTypes, String[] dimNames);

  /**
   * @param attrName the attribute definition to undefine.
   */
  public void undefineNodeAttribute(String attrName);

}
