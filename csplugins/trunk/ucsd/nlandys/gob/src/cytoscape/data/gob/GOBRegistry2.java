package cytoscape.data.gob;

public class GOBRegistry2 extends GOBRegistry
{

  public final byte ATTR_TYPE_MULTI = 47;

  // For now, offer no public construction mechanism.
  GOBRegistry2() {}

  /**
   * Creates an attribute definition of type ATTR_TYPE_MULTI.  Unlike creation
   * of all other attribute definition types, one cannot use the standard
   * defineAttribute(byte) method to create an attribute definition of type
   * ATTR_TYPE_MULTI - one must use this method.
   * @param types this defines the dimensions of this multi-attribute;
   *   the entry at index zero defines the type in the first dimension, the
   *   entry at index one defines the type in the second dimension, and so on;
   *   the length of this array must be greater than one and less than ___
   *   (dimensionality is limited because of memory constraints); every entry
   *   in this array must be one of the ATTR_TYPE_* constants specifying a
   *   primitive type (no ATTR_TYPE_MULTI allowed within this array).
   * @return an identifier for this newly created attribute definition;
   *   attribute definition IDs are non-negative and clustered close to
   *   zero.
   */
  public int defineMultiAttribute(byte[] types)
  {
    throw new IllegalArgumentException("not implemented yet - pardon me");
  }

  /**
   * Let us visualize a multi-attribute value as a point in a multidimensional
   * space, whose axes are the primitive types that make up the multi-attribute
   * definition.  In a given multi-attribute definition domain (such a
   * domain is created with the defineMultiAttribute(byte[]) method), for a
   * given [fixed] object, any number of unique multi-attribute values
   * [points] can be assigned to that object.  By "unique values" I mean the
   * following.  An object cannot have more than one
   * multi-attribute value "point" assigned to it in the same location in the
   * attribute definition domain space.  For example, if our multi-attribute
   * definition domain defID1 is of two dimensions, ATTR_TYPE_LONG cross
   * ATTR_TYPE_LONG, then a given object objID1 cannot be assigned two
   * attributes attrID1 and attrID2 in space defID1 such that attrID1's value
   * is (1,2) and attrID2's value is (1,2) - it is possible for attrID1's
   * value to be (1,2) and for attrID2's value to be (1,3), for example.
   * @return a multi-attribute identifier, referred to as "attrID" in other
   *   methods; to uniquely identify an attribute value [multidimensional]
   *   point for a given object in a given [multi] attribute definition domain,
   *   what is needed is the triplet { objID, defID, attrID } (see
   *   method multiAttributeValue()) - the attrID by itself
   *   is not enough to uniquely identify a multi-attribute value point;
   *   attribute IDs are nonnegative and are clustered close to zero.
   */
  public int assignMultiAttribute(int objID, int defID, Object[] attrValue)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @return an enumeration of attribute IDs (attrID types) for attributes
   *   assigned to the specified object (objID) in the specified attribute
   *   definition domain (defID); the attribute definition domain must
   *   be of type ATTR_TYPE_MULTI, otherwise an error condition is raised.
   */
  public IntEnumerator multiAttributeKeys(int objID, int defID)
  {
    throw new IllegalArgumentException();
  }

  public Object[] multiAttributeValue(int objID, int defID, int attrID)
  {
    throw new IllegalArgumentException();
  }

  /**
   * NOTE: Calling the non-multi-attribute method
   * forgetAssignedAttribute(objID, defID) is equivalent to calling this
   * method with every attrID assigned to this object in this attribute
   * definition domain.
   */
  public void forgetAssignedMultiAttribute(int objID, int defID, int attrID)
  {
    throw new IllegalArgumentException();
  }

}
