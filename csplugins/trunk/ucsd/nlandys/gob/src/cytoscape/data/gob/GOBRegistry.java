package cytoscape.data.gob;

import cytoscape.util.intr.IntEnumerator;
import java.util.NoSuchElementException;

public class GOBRegistry
{

  public final byte ATTR_TYPE_BOOLEAN = 2;
  public final byte ATTR_TYPE_DOUBLE = 3;
  public final byte ATTR_TYPE_LONG = 5;
  public final byte ATTR_TYPE_STRING = 6;

  /**
   * @param type one of the ATTR_TYPE_* constants.
   * @return an identifier for this newly created attribute definition;
   *   attribute definition IDs are non-negative and clustered close to
   *   zero.
   * @exception IllegalArgumentException if the type specified is not one
   *   of the ATTR_TYPE_* constants.
   */
  public int defineAttribute(byte type)
  {
    throw new IllegalArgumentException("not implemented yet - pardon me");
  }

  /**
   * @param defID an identifier of an existing attribute definition.
   * @return one of the ATTR_TYPE_* constants specifiying which kind of
   *   attribute the specified attribute definition (defID) is.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public byte attributeType(int defID)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @param defID a potential attribute definition ID.
   * @return true if and only if the specified attribute definition (defID)
   *   is defined in this registry.
   */
  public boolean attributeDefined(int defID)
  {
    return false;
  }

  /**
   * @return an enumeration of unique attribute definition identifiers in
   *   this registry; this method never returns null.
   */
  public IntEnumerator attributes()
  {
    return new IntEnumerator() {
        public int numRemaining() { return 0; }
        public int nextInt() { return -1; } };
  }

  /**
   * Removes an attribute definition from this registry; all objects that
   * have been assigned an attribute value in this attribute domain will
   * lose the corresponding attribute value.
   * @param defID the ID of the attribute definition to remove.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public void removeAttribute(int defID)
  {
    throw new IllegalArgumentException();
  }

  /**
   * NOTE: Instead of using this method, you are encouraged to use one of the
   * assignXXXAttribute() methods that take a specific type of value as input.
   * @param objID an object identifier; assigned attribute values belong
   *   to objects, and objects are abstracted as integers by this API.
   * @param defID an identifier of an attribute definition that was created
   *   using defineAttribute().
   * @param attrValue the attribute value that is to be assigned to the
   *   specified object, in the specified attribute definition domain; the
   *   class of the Object must be java.lang.Boolean for ATTR_TYPE_BOOLEAN,
   *   java.lang.Double for ATTR_TYPE_DOUBLE, java.lang.Long for
   *   ATTR_TYPE_LONG, and java.lang.String for ATTR_TYPE_STRING.
   * @exception ClassCastException if the specified attribute definition
   *   (defID) exists but the specified attribute value (attrValue) is
   *   not of the class implied by the type (ATTR_TYPE_*) of specified
   *   attribute definition.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   * @exception NullPointerException if the specified attribute value
   *   (attrValue) is null.
   */
  public void assignAttribute(int objID, int defID, Object attrValue)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @param objID an object identifier; assigned attribute values belong
   *   to objects, and objects are abstracted as integers by this API.
   * @param defID an identifier of an attribute definition that was created
   *   using defineAttribute().
   * @param attrValue the attribute value that is to be assigned to the
   *   specified object, in the specified attribute definition domain.
   * @exception ClassCastException if the specified attribute definition
   *   (defID) exists but is not of type ATTR_TYPE_BOOLEAN.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public void assignBooleanAttribute(int objID, int defID, boolean attrValue)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @param objID an object identifier; assigned attribute values belong
   *   to objects, and objects are abstracted as integers by this API.
   * @param defID an identifier of an attribute definition that was created
   *   using defineAttribute().
   * @param attrValue the attribute value that is to be assigned to the
   *   specified object, in the specified attribute definition domain.
   * @exception ClassCastException if the specified attribute definition
   *   (defID) exists but is not of type ATTR_TYPE_DOUBLE.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public void assignDoubleAttribute(int objID, int defID, double attrValue)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @param objID an object identifier; assigned attribute values belong
   *   to objects, and objects are abstracted as integers by this API.
   * @param defID an identifier of an attribute definition that was created
   *   using defineAttribute().
   * @param attrValue the attribute value that is to be assigned to the
   *   specified object, in the specified attribute definition domain.
   * @exception ClassCastException if the specified attribute definition
   *   (defID) exists but is not of type ATTR_TYPE_LONG.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public void assignLongAttribute(int objID, int defID, long attrValue)
  {
    throw new IllegalArgumentException();
  }
  
  /**
   * @param objID an object identifier; assigned attribute values belong
   *   to objects, and objects are abstracted as integers by this API.
   * @param defID an identifier of an attribute definition that was created
   *   using defineAttribute().
   * @param attrValue the attribute value that is to be assigned to the
   *   specified object, in the specified attribute definition domain; null
   *   values are not allowed, but empty strings are allowed.
   * @exception ClassCastException if the specified attribute definition
   *   (defID) exists but is not of type ATTR_TYPE_STRING.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   * @exception NullPointerException if the specified attribute value
   *   (attrValue) is null.
   */
  public void assignStringAttribute(int objID, int defID, String attrValue)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @param objID an object identifier.
   * @param defID an identifier of an attribute definition that was
   *   created using defineAttribute().
   * @return true if and only if the specified object (objID) has an attribute
   *   value from the specified attribute definition domain (defID) assigned
   *   to it; note that if the specified object is not recognized by this
   *   registry, false is returned (as opposed to raising some sort of error
   *   condition).
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public boolean hasAttribute(int objID, int defID)
  {
    throw new IllegalArgumentException();
  }

  /**
   * NOTE: Instead of using this method, you are encouraged to use one of the
   * XXXAttributeValue() methods that return a specific type.
   * @param objID an object identifier that was previously used in
   *   assigning an attribute value in the specified attribute definition
   *   domain (defID).
   * @param defID an identifier of an attribute definition that was
   *   created using defineAttribute().
   * @return the value that was previously assigned to specified object
   *   in specified attribute definition domain; if this attribute definition
   *   if of type ATTR_TYPE_BOOLEAN, the returned object is of type
   *   java.lang.Boolean; if this attribute definition is of type
   *   ATTR_TYPE_DOUBLE, the returned object is of type java.lang.Double;
   *   if this attribute definition is of type ATTR_TYPE_LONG, the returned
   *   object is of type java.lang.Long; if this attribute definition is of
   *   type ATTR_TYPE_STRING, the returned object is of type java.lang.String;
   *   null is never returned by this method.
   * @exception NoSuchElementException if the specified attribute
   *   definition (defID) exists but there is no corresponding attribute
   *   value assigned to specified object; note that Java try/catch blocks
   *   are a huge performance bottleneck when exceptions are thrown.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public Object attributeValue(int objID, int defID)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @param objID an object identifier that was previously used in
   *   assigning an attribute value in the specified attribute definition
   *   domain (defID).
   * @param defID an identifier of an attribute definition that was
   *   created using defineAttribute().
   * @return the value that was previously assigned to specified object
   *   in specified attribute definition domain.
   * @exception ClassCastException if the specified attribute definition
   *   (defID) exists but is not of type ATTR_TYPE_BOOLEAN, regardless of
   *   whether or not the specified object (objID) is recognized by this
   *   registry.
   * @exception NoSuchElementException if the specified attribute definition
   *   (defID) exists but there is no corresponding attribute value assinged
   *   to specified object; note that Java try/catch blocks are a huge
   *   performance bottleneck when exceptions are thrown.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public boolean booleanAttributeValue(int objID, int defID)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @param objID an object identifier that was previously used in
   *   assigning an attribute value in the specified attribute definition
   *   domain (defID).
   * @param defID an identifier of an attribute definition that was created
   *   using defineAttribute().
   * @return the value that was previously assigned to specified object
   *   in specified attribute definition domain.
   * @exception ClassCastException if the specified attribute definition
   *   (defID) exists but is not of type ATTR_TYPE_DOUBLE, regardless of
   *   whether or not the specified object (objID) is recongnized by this
   *   registry.
   * @exception NoSuchElementException if the specified attribute definition
   *   (defID) exists but there is no corresponding attribute value assigned
   *   to specified object; note that Java try/catch blocks are a huge
   *   performance bottleneck when exceptions are thrown.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public double doubleAttributeValue(int objID, int defID)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @param objID an object identifier that was previously used in
   *   assigning an attribute value in the specified attribute definition
   *   domain (defID).
   * @param defID an identifier of an attribute definition that was
   *   created using defineAttribute().
   * @return the value that was previously assigned to specified object
   *   in specified attribute definition domain.
   * @exception ClassCastException if the specified attribute definition
   *   (defID) exists but is not of type ATTR_TYPE_LONG, regardless of
   *   whether or not the specified object (objID) is recognized by this
   *   registry.
   * @exception NoSuchElementException if the specified attribute definition
   *   (defID) exists but there is no corresponding attribute value assinged
   *   to specified object; note that Java try/catch blocks are a huge
   *   performance bottleneck when exceptions are thrown.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public long longAttributeValue(int objID, int defID)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @param objID an object identifier that was previously used in
   *   assigning an attribute value in the specified attribute definition
   *   domain (defID).
   * @param defID an identifier of an attribute definition that was
   *   created using defineAttribute().
   * @return the value that was previously assigned to specified object
   *   in specified attribute definition domain; null is never returned, but
   *   the empty string may be returned.
   * @exception ClassCastException if the specified attribute definition
   *   (defID) exists but is not of type ATTR_TYPE_STRING, regardless of
   *   whether or not the specified object (objID) is recognized by this
   *   registry.
   * @exception NoSuchElementException if the specified attribute definition
   *   (defID) exists but there is no corresponding attribute value assinged
   *   to specified object; note that Java try/catch blocks are a huge
   *   performance bottleneck when exceptions are thrown.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public String stringAttributeValue(int objID, int defID)
  {
    throw new IllegalArgumentException();
  }

  /**
   * NOTE: This method is superfluous because we can implement it by calling
   * assignedAttributes(objID) and checking for the empty enumeration.
   * @param objID a potential object identifier.
   * @return true if and only if the specified object (objID) has
   *   at least one attribute value assigned.
   */
  public boolean objectRecognized(int objID)
  {
    return false;
  }

  /**
   * @return an enumeration of unique attribute definition identifiers such
   *   that the specified object (objID) currently has an assigned attribute
   *   value in each of the returned attribute definitions; this method never
   *   returns null, and an empty enumeration is synonymous with
   *   objectRecognized(objID) returning false.
   */
  public IntEnumerator assignedAttributes(int objID)
  {
    return new IntEnumerator() {
        public int numRemaining() { return 0; }
        public int nextInt() { return -1; } };
  }

  /**
   * @return an enumeration of unique objects (object identifiers) that have
   *   an attribute value in the specified attribute definition domain (defID)
   *   assigned; this method never returns null.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public IntEnumerator objectsWithAttribute(int defID)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @return an enumeration of unique objects (object identifiers) that have
   *   at least one attribute value assigned (from any existing attribute
   *   definition domain); this method never returns null.
   */
  public IntEnumerator objects()
  {
    return new IntEnumerator() {
        public int numRemaining() { return 0; }
        public int nextInt() { return -1; } };
  }

  /**
   * Forgets that any value for the specified attribute definition (defID)
   * was ever assigned to the specified object (objID).
   * Note that an object will cease to exist in this
   * registry if it has no attributes assigned to it.  Note also that
   * if no attribute value from the specified attribute definition domain
   * is assigned to the specified object at the time this
   * method is called, nothing is changed, and no error condition is raised.
   * @return true if and only if a value for the specified attribute is
   *   assigned to the specified object at the time this method is called.
   * @exception IllegalArgumentException if no attribute definition with
   *   specified ID (defID) exists.
   */
  public void forgetAssignedAttribute(int objID, int defID)
  {
    throw new IllegalArgumentException();
  }

  /**
   * Forgets all attribute values that have been assigned to the specified
   * object (objID).  Note that if the specified object (objID) is not
   * recognized by this registry at the time this method is called,
   * nothing is changed, and no error condition is raised.<p>
   * NOTE: This method is superfluous because we can implement it by
   * getting all assigned attributes for specified object, then deleteing
   * those attributes one by one.
   * @param objID an object identifier.
   */
  public void forgetObject(int objID)
  {
  }

}
