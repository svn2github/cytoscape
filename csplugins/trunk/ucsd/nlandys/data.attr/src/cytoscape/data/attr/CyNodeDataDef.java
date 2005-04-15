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
  public final byte TYPE_BOOLEAN = 1;

  /**
   * This type corresponds to java.lang.Double.
   */
  public final byte TYPE_FLOATING_POINT = 2;

  /**
   * This type corresponds to java.lang.Long.
   */
  public final byte TYPE_INTEGER = 3;

  /**
   * This type corresponds to java.lang.String.
   */
  public final byte TYPE_STRING = 4;

  /**
   * Creates a node attribute definition.  An attribute definition must be
   * created before binding an attribute value to a node.<p>
   * Perhaps the most common type of attribute definition is one where the
   * key space has zero dimensions.  For example, if I want to identify each
   * node as having a color, I would create an attribute definition which
   * stores values of TYPE_STRING (for storing "red", "blue", and so on),
   * and has no key sequence mapping color
   * values.  By "no key sequence" I mean that the input parameters
   * keyTypes and keyNames would be either null or the empty array for my
   * color attribute definition.<p>
   * The more interesting case is where the key space in an attribute
   * definition has one or more dimensions.  For example, if I
   * wanted to create an attribute that represents measured p-values for
   * all nodes over a set of experiments ("Ideker experiment",
   * "Salk experiment", ...) I would define a one-dimensional key space
   * of TYPE_STRING (to represent the experiment names) and a value of
   * TYPE_FLOATING_POINT (to represent p-values).
   * @param attributeName an identifier for this attribute definition;
   *   this value must be unique from all existing node attribute definitions;
   *   ideally, the choice of name would describe values being stored by this
   *   attribute definition.
   * @param valueType one of the TYPE_* constants defining what type of
   *   values are bound to nodes in this attribute definition.
   * @param keyTypes defines the type (TYPE_*) of each dimension in the key
   *   space;
   *   the entry at index i defines the type of key space dimension i + 1;
   *   this parameter may either be null or the empty array if an attribute
   *   definition does not use a key space (this is perhaps the most common
   *   scenario).
   * @param keyNames defines the name of each dimension in the key space;
   *   the entry at index i defines the name of key space dimension i + 1;
   *   this parameter may either be null or the empty array if an attribute
   *   definition does not use a key space (this is perhaps the most common
   *   scenario).
   * @exception IllegalStateException if attributeName is already the name
   *   of an existing attribute definition.
   */
  public void defineNodeAttribute(String attributeName,
                                  byte valueType,
                                  byte[] keyTypes,
                                  String[] keyNames);

  /**
   * @return an enumeration of java.lang.String; each returned string
   *   is an attrName (an attribute definition name).
   */
  public Enumeration getDefinedNodeAttributes();

  /**
   * @return the number of dimensions in the attribute specified.
   */
  public int getNumDimensions(String attrName);

  /**
   * @param attrName the name of attribute whose information we are
   *   querying.
   * @param dimTypes this array is copied into by this method; the array
   *   is populated with the type of each dimension; the array must be
   *   of length at least equal to the dimensionality of attribute attrName,
   *   and the dimension types are copied into this array starting at index
   *   zero of the array.
   * @param dimNames this array is copied into by this method; the array
   *   is populated with the name of each dimension;
   *   the array must be of length at least equal to the dimensionality of
   *   attribute attrName, and the dimension names are copied into
   *   this array starting at index zero of the array.
   */
  public void getAttributeInfo(String attrName,
                               byte[] dimTypes, String[] dimNames);

  /**
   * @param attrName the attribute definition to undefine.
   */
  public void undefineNodeAttribute(String attrName);

  public void addNodeDataDefListener(CyNodeDataDefListener listener);

  public void removeNodeDataDefListener(CyNodeDataDefListener listener);

}
