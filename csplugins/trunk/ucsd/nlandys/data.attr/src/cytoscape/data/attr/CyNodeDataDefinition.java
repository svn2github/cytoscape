package cytoscape.data.attr;

import java.util.Enumeration;

/**
 * This interface contains the API specification for creating node
 * attribute definitions.
 */
public interface CyNodeDataDefinition
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
   * It is a programming error to define or undefine attribute domains
   * whilst iterating through the returned enumeration.
   * @return an enumeration of java.lang.String; each returned string
   *   is an attributeName (an attribute definition name).
   */
  public Enumeration getDefinedNodeAttributes();

  /**
   * @return the type (TYPE_*) of values bound to nodes by this attribute
   *   definition.
   */
  public byte getNodeAttributeValueType(String attributeName);

  /**
   * @param attributeName the attribute definition whose key space
   *   dimensionality we are querying.
   * @return the number of dimensions in the specified attribute's key
   *   space.
   */
  public int getNodeAttributeKeyspaceDimensionality(String attributeName);

  /**
   * @param attributeName the attribute definition whose key space information
   *   we are querying.
   * @param keyTypes this parameter is written into by this method; it is not
   *   used as input; consider this a return value; the size of this array
   *   must be at least the dimensionality of the key space of specified
   *   attribute definition, and the key space dimension types (TYPE_*) are
   *   written into this array starting at index zero of this array.
   * @param keyNames this parameter is written into by this method; it is not
   *   used as input; consider this a return value; the size of this array
   *   must be at least the dimensionality of the key space of specified
   *   attribute definition, and the key space dimension names are written
   *   into this array starting at index zero of this array.
   */
  public void getNodeAttributeKeyspaceInfo(String attributeName,
                                           byte[] keyTypes, String[] keyNames);

  /**
   * WARNING: All bound attribute values on nodes will go away in this
   * attribute namespace when this method is called.
   * @param attributeName the attribute definition to undefine.
   */
  public void undefineNodeAttribute(String attributeName);

  public void addNodeDataDefinitionListener(
                                        CyNodeDataDefinitionListener listener);

  public void removeNodeDataDefinitionListener(
                                        CyNodeDataDefinitionListener listener);

}
