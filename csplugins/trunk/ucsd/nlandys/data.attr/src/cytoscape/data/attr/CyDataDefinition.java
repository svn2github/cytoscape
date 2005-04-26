package cytoscape.data.attr;

import java.util.Enumeration;

/**
 * This interface contains the API specification for creating
 * attribute definitions.
 */
public interface CyDataDefinition
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
   * This type corresponds to java.lang.Integer.
   */
  public final byte TYPE_INTEGER = 3;

  /**
   * This type corresponds to java.lang.String.
   */
  public final byte TYPE_STRING = 4;

  /**
   * Creates an attribute definition.  An attribute definition must be
   * created before binding an attribute value to an object.<p>
   * Perhaps the most common type of attribute definition is one where the
   * key space has zero dimensions.  For example, if I want to identify each
   * object as having a color, I would create an attribute definition which
   * stores values of TYPE_STRING (for storing "red", "blue", and so on),
   * and has no key sequence mapping color
   * values.  By "no key sequence" I mean that the input parameter
   * keyTypes would be either null or the empty array for my
   * color attribute definition.<p>
   * The more interesting case is where the key space in an attribute
   * definition has one or more dimensions.  For example, if I
   * wanted to create an attribute that represents measured p-values for
   * all objects over a set of experiments ("Ideker experiment",
   * "Salk experiment", ...) I would define a one-dimensional key space
   * of TYPE_STRING (to represent the experiment names) and a value of
   * TYPE_FLOATING_POINT (to represent p-values).<p>
   * NOTE: No constraints on attributeName are documented by this API.  In
   * other words, as far as this API is concerned, any attributeName is
   * acceptable, including the empty string ("").  The only necessary
   * condition is that each attributeName be unique.
   * @param attributeName an identifier for this attribute definition;
   *   this value must be unique from all existing attribute definitions;
   *   ideally, the choice of name would describe values being stored by this
   *   attribute definition.
   * @param valueType one of the TYPE_* constants defining what type of
   *   values are bound to objects in this attribute definition.
   * @param keyTypes defines the type (TYPE_*) of each dimension in the key
   *   space;
   *   the entry at index i defines the type of key space dimension i + 1;
   *   this parameter may either be null or the empty array if an attribute
   *   definition does not use a key space (this is perhaps the most common
   *   scenario).
   * @exception IllegalStateException if attributeName is already the name
   *   of an existing attribute definition.
   * @exception NullPointerException if attributeName is null.
   * @exception IllegalArgumentException if valueType is not one of the
   *   TYPE_* constants, or if keyTypes is [not null and] of positive length
   *   and any one of its elements is not one of the TYPE_* constants.
   */
  public void defineAttribute(String attributeName,
                              byte valueType,
                              byte[] keyTypes);

  /**
   * It is a programming error to define or undefine attribute domains
   * whilst iterating through the returned enumeration.<p>
   * NOTE: To find out whether or not an attributeName is defined, use
   * getAttrubuteValueType(attributeName) and test whether or not the
   * return value is negative.
   * @return an enumeration of java.lang.String; each returned string
   *   is an attributeName (an attribute definition name).
   */
  public CountedEnumeration getDefinedAttributes();

  /**
   * @return the type (TYPE_*) of values bound to objects by this attribute
   *   definition, or -1 if specified attribute definition does not exist;
   *   note that all of the TYPE_* constants are positive.
   * @exception NullPointerException if attributeName is null.
   */
  public byte getAttributeValueType(String attributeName);

  /**
   * @param attributeName the attribute definition whose key space
   *   dimensionality we are querying.
   * @return the number of dimensions in the specified attribute's key
   *   space, or -1 if specified attribute definition does not exist.
   * @exception NullPointerException if attributeName is null.
   */
  public int getAttributeKeyspaceDimensionality(String attributeName);

  /**
   * @param attributeName the attribute definition whose key space information
   *   we are querying.
   * @param keyTypes this parameter is written into by this method; it is not
   *   used as input; consider this a return value; the size of this array
   *   must be at least the dimensionality of the key space of specified
   *   attribute definition, and the key space dimension types (TYPE_*) are
   *   written into this array starting at index zero of this array.
   * @exception IllegalStateException if no attribute definition called
   *   attributeName exists.
   * @exception NullPointerException if attributeName is null.
   */
  public void copyAttributeKeyspaceInfo(String attributeName,
                                        byte[] keyTypes);

  /**
   * WARNING: All bound attribute values on objects will go away in this
   * attribute namespace when this method is called.
   * @param attributeName the attribute definition to undefine.
   * @exception NullPointerException if attributeName is null.
   */
  public void undefineAttribute(String attributeName);

  public void addDataDefinitionListener(CyDataDefinitionListener listener);

  public void removeDataDefinitionListener(CyDataDefinitionListener listener);

}
