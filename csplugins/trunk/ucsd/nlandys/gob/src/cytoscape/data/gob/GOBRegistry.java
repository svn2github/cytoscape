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
   *   the specified attribute type is not recognized as a valid type.
   */
  public int defineAttribute(int type)
  {
    return false;
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
  public int getAttributeType(int attrID)
  {
    return -1;
  }

  /**
   * @return an enumeration of all attributes defined in this registry; each
   *   entry in the returned enumeration is a unique attribute ID; this method
   *   never returns null.
   */
  public IntEnumerator getAttributes()
  {
    return null;
  }

  public void assignAttribute(int objID, int attrID, Object attrValue)
  {
  }

  public void assignBooleanAttribute(int objID, int attrID, boolean attrValue)
  {
  }

  public void assignDoubleAttribute(int objID, int attrID, boolean attrValue)
  {
  }

  public void assignLongAttribute(int objID, int attrID, long attrValue)
  {
  }

  public void assignStringAttribute(int objID, int attrID, String attrValue)
  {
  }

  public IntEnumerator assignedAttributes(int objID)
  {
  }

  public IntEnumerator objects()
  {
  }

  /**
   * Forgets that any value for the specified attribute was ever assigned to
   * the specified object.
   * @return true if and only if a value for the specified attribute is
   *   assigned to the specified object at the time this method is called.
   */
  public boolean forgetAttribute(int objID, int attrID)
  {
    return false;
  }

  /**
   * Forgets all attribute values that have been assigned to the specified
   * object ID.
   * @return true if and only if the specified object ID is currently
   *   registered as having at least one attribute value assigned to it.
   */
  public boolean forgetObject(int objID)
  {
  }

}
