package cytoscape.data.gob;

import cytoscape.util.intr.IntEnumerator;

public class GOBRegistry
{

  public final int ATTR_TYPE_BOOLEAN = 2;
  public final int ATTR_TYPE_DOUBLE = 3;
  public final int ATTR_TYPE_LONG = 5;
  public final int ATTR_TYPE_STRING = 6;

  /**
   * @param type one of the ATTR_TYPE_* constants.
   * @return an identifier for this newly created attribute or -1 if
   *   the specified attribute type is not recognized as a valid type;
   *   attribute IDs are always non-negative.
   */
  public int defineAttribute(int type)
  {
    return -1;
  }

  /**
   * @param attrID the attribute ID of the attribute span to remove.
   * @return true if and only if the specified attribute exists at the time
   *   this method is called.
   */
  public boolean removeAttribute(int attrID)
  {
    return false;
  }

  /**
   * @return one of the ATTR_TYPE_* constants specifiying which kind of
   *   attribute the specified attribute is, or -1 if no attribute with
   *   specified attribute ID exists.
   */
  public int attributeType(int attrID)
  {
    return -1;
  }

  /**
   * @return an enumeration of all attributes defined in this registry; each
   *   entry in the returned enumeration is a unique attribute ID; this method
   *   never returns null.
   */
  public IntEnumerator attributes()
  {
    return null;
  }

  public void assignAttribute(int objID, int attrID, Object attrValue)
  {
  }

  public void assignBooleanAttribute(int objID, int attrID, boolean attrValue)
  {
  }

  public void assignDoubleAttribute(int objID, int attrID, double attrValue)
  {
  }

  public void assignLongAttribute(int objID, int attrID, long attrValue)
  {
  }

  public void assignStringAttribute(int objID, int attrID, String attrValue)
  {
  }

  /**
   * You are strongly encouraged to not use this method; use one of the
   * access methods that return a specific type instead.
   */
  public Object attributeValue(int objID, int attrID)
  {
    return null;
  }

  // We will have to throw a RuntimeException subclass if there's a type
  // mismatch or if specified obj attr map does not exist.
  public boolean booleanAttributeValue(int objID, int attrID)
  {
    throw new RuntimeException();
  }

  public double doubleAttributeValue(int objID, int attrID)
  {
    throw new RuntimeException();
  }

  public long longAttributeValue(int objID, int attrID)
  {
    throw new RuntimeException();
  }

  public String stringAttributeValue(int objID, int attrID)
  {
    throw new RuntimeException();
  }

  /**
   * @return an enumeration of attributes that currently have assigned
   *   values to the specified objects; every entry in the returned enumeration
   *   is an attribute identifier; this method null if and only if this
   *   registry knows nothing of the specified object (for example if no
   *   attributes were ever specified for this object or if all attributes
   *   were deleted for this [once existing] object); note that an object
   *   will cease to exist in this registry if it has no attributes assigned
   *   to it.
   */
  public IntEnumerator assignedAttributes(int objID)
  {
  }

  /**
   * @return an enumeration of all objects that have at least one
   *   attribute value assigned; each element of the returned enumeration
   *   is an object identifier; this method never returns null.
   */
  public IntEnumerator objects()
  {
  }

  /**
   * Forgets that any value for the specified attribute was ever assigned to
   * the specified object.  Note that an object will cease to exist in this
   * registry if it has no attributes assigned to it.
   * @return true if and only if a value for the specified attribute is
   *   assigned to the specified object at the time this method is called.
   */
  public boolean forgetAssignedAttribute(int objID, int attrID)
  {
    return false;
  }

  /**
   * Forgets all attribute values that have been assigned to the specified
   * object.<p>
   * NOTE: This method is superfluous because we can implement it by
   * getting all assigned attributes for specified object, then deleteing
   * those attributes one by one.
   * @return true if and only if the specified object ID is currently
   *   registered as having at least one attribute value assigned to it.
   */
  public boolean forgetObject(int objID)
  {
  }

}
