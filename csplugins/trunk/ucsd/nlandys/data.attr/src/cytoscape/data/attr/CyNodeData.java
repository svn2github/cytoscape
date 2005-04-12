package cytoscape.data.attr;

import java.util.Enumeration;

/**
 * This API is one that plugin developers have visibility into - the ability
 * to define, delete, or change a node's [canonical] name is not included in
 * this API.
 */
public interface CyNodeData
{

  public final byte ATTR_TYPE_BOOLEAN = 1;
  public final byte ATTR_TYPE_DOUBLE = 2;
  public final byte ATTR_TYPE_LONG = 3;
  public final byte ATTR_TYPE_STRING = 4;
  public final byte ATTR_TYPE_MULTI = 5;

  /**
   * Defines an attribute domain.  To create multi-attribute domains
   * use defineMultiNodeAttribute().
   * @exception IllegalStateException if attrName specified already defines
   *   an existing attribute domain.
   * @exception IllegalArgumentException if attrType specified is not one of
   *   the ATTR_TYPE_* constants or if it is equal to ATTR_TYPE_MULTI.
   */
  public void defineNodeAttribute(String attrName, byte attrType);

  /**
   * The last entry in the dimNames input variable becomes the name of this
   * attribute definition.
   */
  public void defineMultiNodeAttribute(String[] dimNames, byte[] attrTypes);

  /**
   * @return an enumeration of java.lang.String, the set of strings returned
   *   is a list of unique node attribute names that are currently defined.
   */
  public Enumeration definedNodeAttributes();

  public byte nodeAttributeType(String attrName);

  /**
   * The last entry in the returned array is the string attrName (the input
   * parameter).
   */
  public String[] multiNodeAttributeDimensionNames(String attrName);

  public byte[] multiNodeAttributeType(String attrName);

  /**
   * The "un"-define of an attribute node.
   * @exception UnsupportedOperationException if the specified attribute domain
   *   exists but permission to delete it is not granted; for example, the
   *   "nodeName" attribute domain always exists and can never be deleted.
   */
  public void undefineNodeAttribute(String attrName);

}
