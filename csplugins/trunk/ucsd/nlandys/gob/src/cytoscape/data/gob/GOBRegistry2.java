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

}
