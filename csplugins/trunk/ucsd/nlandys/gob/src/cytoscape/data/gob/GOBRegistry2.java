package cytoscape.data.gob;

import cytoscape.util.intr.IntEnumerator;

/**
 * <font color="red">API in early alpha stage</font>.
 */
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
   *   primitive type (no ATTR_TYPE_MULTI is allowed within this array).
   * @return an identifier for this newly created attribute definition;
   *   attribute definition IDs are non-negative and clustered close to
   *   zero.
   */
  public int defineMultiAttribute(byte[] types)
  {
    throw new IllegalArgumentException("not implemented yet - pardon me");
  }

  /**
   * @param defID
   * @return a carbon copy of the types array used in the definition of the
   *   specified attribute definition (defID) is returned (see
   *   defineMultiAttribute(byte[])); therefore, the returned value defines
   *   the dimensionality of this attribute definition (the length of returned
   *   array defines the dimensionality, that is) and it defines the type
   *   of primitive (ATTR_TYPE_*) used in each dimension.
   * @exception IllegalArgumentException
   */
  public byte[] multiAttributeType(int defID)
  {
    throw new IllegalArgumentException();
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
   * is (1,2) and attrID2's value is (1,2) - it is, however, possible for
   * attrID1's value to be (1,2) and for attrID2's value to be (1,3).
   * @param objID
   * @param defID
   * @param attrValue
   * @return a multi-attribute identifier, referred to as "attrID" in other
   *   methods; to uniquely identify an attribute value [multidimensional]
   *   point for a given object in a given [multi] attribute definition domain,
   *   what is needed is the triplet { objID, defID, attrID } (see
   *   method multiAttributeValue()) - the attrID by itself
   *   is not enough to uniquely identify a multi-attribute value point;
   *   attribute IDs are nonnegative and are clustered close to zero.
   * @exception IllegalArgumentExcetion
   */
  public int assignMultiAttribute(int objID, int defID, Object[] attrValue)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @param objID
   * @param defID
   * @return an enumeration of unique attribute IDs (attrID types) for
   *   attributes
   *   assigned to the specified object (objID) in the specified attribute
   *   definition domain (defID); the attribute definition domain must
   *   be of type ATTR_TYPE_MULTI, otherwise an error condition is raised.
   * @exception IllegalArgumentException
   */
  public IntEnumerator multiAttributeKeys(int objID, int defID)
  {
    throw new IllegalArgumentException();
  }

  /**
   * @param objID
   * @param defID
   * @param beginningValues this array must be of length less than or equal
   *   to the dimensionality of the attribute definition specified (defID);
   *   the entry at index zero in this array must be of type corresponding
   *   to the type of the first dimension of the attribute definition
   *   specified, the entry at index one in this array must be of type
   *   corresponding to the type of the second dimension of the attribute
   *   definition specified, etc.; the values in this array are used to
   *   filter all attribute values assigned to the specified object (objID)
   *   in specified attribute definition domain (defID), returning only those
   *   point attribute values whose beginning dimension values match
   *   the values in this array.
   * @exception IllegalArgumentException
   */
  public IntEnumerator multiAttributeKeys(int objID, int defID,
                                          Object[] beginningValues)
  {
    throw new IllegalArgumentException();
  }

  /**
   * The returnVal parameter is written into - it is meant as a structure for
   * giving a return value in.
   * @param objID
   * @param defID
   * @param attrID
   * @param returnVal
   * @exception IllegalArgumentException
   */
  public void multiAttributeValue(int objID, int defID, int attrID,
                                  Object[] returnVal)
  {
    throw new IllegalArgumentException();
  }

  /**
   * NOTE: Calling the non-multi-attribute method
   * forgetAssignedAttribute(objID, defID) is equivalent to calling this
   * method with every attrID assigned to this object in this attribute
   * definition domain.
   * @param objID
   * @param defID
   * @param attrID
   * @exception IllegalArgumentException
   */
  public void forgetAssignedMultiAttribute(int objID, int defID, int attrID)
  {
    throw new IllegalArgumentException();
  }

}
