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
   * @return an identifier for this newly created attribute definition or -1 if
   *   the specified attribute type [input parameter] is not recognized as a
   *   valid type; attribute definition IDs are always non-negative.
   */
  public int defineAttribute(int type)
  {
    return -1;
  }

  /**
   * @param attrID an identifier of an existing attribute definition.
   * @return one of the ATTR_TYPE_* constants specifiying which kind of
   *   attribute the specified attribute definition is, or -1 if no attribute
   *   definition with specified ID exists.
   */
  public int attributeType(int attrID)
  {
    return -1;
  }

  /**
   * @return an enumeration of all attribute definitions in this registry; each
   *   entry in the returned enumeration is a unique attribute definition ID;
   *   this method never returns null.
   */
  public IntEnumerator attributes()
  {
    return null;
  }

  /**
   * @param attrID the ID of the attribute definition to remove.
   * @return true if and only if the specified attribute definition exists at
   *   the time this method is called.
   */
  public boolean removeAttribute(int attrID)
  {
    return false;
  }

  /**
   * NOTE: Instead of using this method, you are encouraged to use one of the
   * assignXXXAttribute() methods that take a specific type of object as input.
   * @param objID an object identifier; assigned attribute values belong
   *   to objects, and objects are abstracted as integers by this API.
   * @param attrID an identifier of an attribute definition that was created
   *   using defineAttribute().
   * @param attrValue the attribute value that is to be assigned to the
   *   specified object, in the specified attribute definition domain; the
   *   class of the Object must be java.lang.Boolean for ATTR_TYPE_BOOLEAN,
   *   java.lang.Double for ATTR_TYPE_DOUBLE, java.lang.Long for
   *   ATTR_TYPE_LONG, and java.lang.String for ATTR_TYPE_STRING.
   * @return false if and only if no attribute definition with specified
   *   ID exists.
   * @exception ClassCastException if the specified attribute definition
   *   (attrID) exists but the specified attribute value (attrValue) is
   *   not of the class implied by the type (ATTR_TYPE_*) of specified
   *   attribute definition.
   */
  public boolean assignAttribute(int objID, int attrID, Object attrValue)
  {
    return false;
  }

  /**
   * @param objID an object identifier; assigned attribute values belong
   *   to objects, and objects are abstracted as integers by this API.
   * @param attrID an identifier of an attribute definition that was created
   *   using defineAttribute().
   * @param attrValue the attribute value that is to be assigned to the
   *   specified object, in the specified attribute definition domain.
   * @return false if and only if no attribute definition with specified
   *   ID exists.
   * @exception ClassCastException if the specified attribute definition
   *   (attrID) exists but is not of type ATTR_TYPE_BOOLEAN.
   */
  public boolean assignBooleanAttribute(int objID, int attrID,
                                        boolean attrValue)
  {
    return false;
  }

  /**
   * @param objID an object identifier; assigned attribute values belong
   *   to objects, and objects are abstracted as integers by this API.
   * @param attrID an identifier of an attribute definition that was created
   *   using defineAttribute().
   * @param attrValue the attribute value that is to be assigned to the
   *   specified object, in the specified attribute definition domain.
   * @return false if and only if no attribute definition with specified
   *   ID exists.
   * @exception ClassCastException if the specified attribute definition
   *   (attrID) exists but is not of type ATTR_TYPE_DOUBLE.
   */
  public boolean assignDoubleAttribute(int objID, int attrID, double attrValue)
  {
    return false;
  }

  /**
   * @param objID an object identifier; assigned attribute values belong
   *   to objects, and objects are abstracted as integers by this API.
   * @param attrID an identifier of an attribute definition that was created
   *   using defineAttribute().
   * @param attrValue the attribute value that is to be assigned to the
   *   specified object, in the specified attribute definition domain.
   * @return false if and only if no attribute definition with specified
   *   ID (attrID) exists.
   * @exception ClassCastException if the specified attribute definition
   *   (attrID) exists but is not of type ATTR_TYPE_LONG.
   */
  public boolean assignLongAttribute(int objID, int attrID, long attrValue)
  {
    return false;
  }
  
  /**
   * @param objID an object identifier; assigned attribute values belong
   *   to objects, and objects are abstracted as integers by this API.
   * @param attrID an identifier of an attribute definition that was created
   *   using defineAttribute().
   * @param attrValue the attribute value that is to be assigned to the
   *   specified object, in the specified attribute definition domain.
   * @return false if and only if no attribute definition with specified
   *   ID (attrID) exists.
   * @exception ClassCastException if the specified attribute definition
   *   (attrID) exists but is not of type ATTR_TYPE_STRING.
   */
  public boolean assignStringAttribute(int objID, int attrID, String attrValue)
  {
    return false;
  }

  /**
   * NOTE: Instead of using this method, you are encouraged to use one of the
   * XXXAttributeValue() methods that return a specific type of object.
   */
  public Object attributeValue(int objID, int attrID)
  {
    return null;
  }

  /**
   * @param objID an object identifier that was previously used in
   *   assigning an attribute value in the specified attribute definition
   *   domain (attrID).
   * @param attrID an identifier of an attribute definition that was
   *   created using defineAttribute().
   * @return the value that was previously assigned to specified object
   *   in specified attribute definition domain.
   * @exception ClassCastException if the specified attribute definition
   *   (attrID) exists but is not of type ATTR_TYPE_BOOLEAN, regardless of
   *   whether or not the specified object (objID) is recognized by this
   *   registry.
   * @exception NoSuchElementException if the specified attribute definition
   *   (attrID) exists but there is no corresponding attribute value assinged
   *   to specified object; note that Java try/catch blocks are a huge
   *   performance bottleneck when exceptions are thrown.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (attrID) exists.
   */
  public boolean booleanAttributeValue(int objID, int attrID)
  {
    throw new RuntimeException();
  }

  /**
   * @param objID an object identifier that was previously used in
   *   assigning an attribute value in the specified attribute definition
   *   domain (attrID).
   * @param attrID an identifier of an attribute definition that was created
   *   using defineAttribute().
   * @return the value that was previously assigned to specified object
   *   in specified attribute definition domain.
   * @exception ClassCastException if the specified attribute definition
   *   (attrID) exists but is not of type ATTR_TYPE_DOUBLE, regardless of
   *   whether or not the specified object (objID) is recongnized by this
   *   registry.
   * @exception NoSuchElementException if the specified attribute definition
   *   (attrID) exists but there is no corresponding attribute value assigned
   *   to specified object; note that Java try/catch blocks are a huge
   *   performance bottleneck when exceptions are thrown.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (attrID) exists.
   */
  public double doubleAttributeValue(int objID, int attrID)
  {
    throw new RuntimeException();
  }

  /**
   * @param objID an object identifier that was previously used in
   *   assigning an attribute value in the specified attribute definition
   *   domain (attrID).
   * @param attrID an identifier of an attribute definition that was
   *   created using defineAttribute().
   * @return the value that was previously assigned to specified object
   *   in specified attribute definition domain.
   * @exception ClassCastException if the specified attribute definition
   *   (attrID) exists but is not of type ATTR_TYPE_LONG, regardless of
   *   whether or not the specified object (objID) is recognized by this
   *   registry.
   * @exception NoSuchElementException if the specified attribute definition
   *   (attrID) exists but there is no corresponding attribute value assinged
   *   to specified object; note that Java try/catch blocks are a huge
   *   performance bottleneck when exceptions are thrown.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (attrID) exists.
   */
  public long longAttributeValue(int objID, int attrID)
  {
    throw new RuntimeException();
  }

  /**
   * @param objID an object identifier that was previously used in
   *   assigning an attribute value in the specified attribute definition
   *   domain (attrID).
   * @param attrID an identifier of an attribute definition that was
   *   created using defineAttribute().
   * @return the value that was previously assigned to specified object
   *   in specified attribute definition domain.
   * @exception ClassCastException if the specified attribute definition
   *   (attrID) exists but is not of type ATTR_TYPE_STRING, regardless of
   *   whether or not the specified object (objID) is recognized by this
   *   registry.
   * @exception NoSuchElementException if the specified attribute definition
   *   (attrID) exists but there is no corresponding attribute value assinged
   *   to specified object; note that Java try/catch blocks are a huge
   *   performance bottleneck when exceptions are thrown.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (attrID) exists.
   */
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
