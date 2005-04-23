package cytoscape.data.attr.util;

import cytoscape.data.attr.CountedEnumeration;
import cytoscape.data.attr.CyData;
import cytoscape.data.attr.CyDataDefinition;

import java.util.ArrayList;
import java.util.List;

public final class CyDataHelpers
{
  // NOTE: This class resides in the same package as the CyDataModel
  // implementation of CyData for a good reason.  We may want some
  // of these helper methods to be more optimized than just calling CyDataModel
  // by its CyData interface.  We could provide dual implementations for
  // every method in this class, and choose the path of execution based on
  // whether or not our input object is an instance of CyDataModel.
  // Right now, though, everything is implemented in terms of CyData interface
  // methods.

  // No constructor.  Static methods only.
  private CyDataHelpers() { }

  /**
   * Convenience method for discovering all attribute values on an object in
   * a given attribute definition; this method is only useful with attribute
   * definitions that have nonzero key spaces.
   * @param objectKey the object whose attribute values to return.
   * @param attributeName the attribute definition in which to find attribute
   *   values.
   * @param cyData the data repository to use to dig for attribute values.
   * @param cyDataDef the data definition registry to use to find out about
   *   the dimensionality of attributeName.
   * @return a list of all bound values on objectKey in
   *   attributeName, with duplicate values included; the returned list
   *   is never null; elements in the returned list are ordered
   *   arbitrarily; subsequent operations on cyData or cyDataDef will have
   *   no effect on the returned list.
   * @exception IllegalStateException if attributeName is not an existing
   *   attribute definition in cyData and cyDataDef.
   * @exception NullPointerException if any one of the input parameters is
   *   null.
   */
  public static List getAllAttributeValues(final String objectKey,
                                           final String attributeName,
                                           final CyData cyData,
                                           final CyDataDefinition cyDataDef)
  {
    final ArrayList bucket = new ArrayList();
    final int keyspaceDims =
      cyDataDef.getAttributeKeyspaceDimensionality(attributeName);
    if (keyspaceDims < 1) { // It's either 0 or -1.
      final Object attrVal = cyData.getAttributeValue
        (objectKey, attributeName, null); // May trigger exception; OK.
      if (attrVal != null) bucket.add(attrVal); }
    else { // keyspaceDims > 0.
      r_getAllAttributeValues(objectKey, attributeName, cyData,
                              bucket, new Object[0], keyspaceDims); }
    return bucket;
  }

  /**
   * Convenience method for discovering attribute values along a specified
   * key prefix; this method is only useful with attribute
   * definitions that have nonzero key spaces.
   * @param objectKey the object whose attribute values to return.
   * @param attributeName the attribute definition in which to find attribute
   *   values.
   * @param keyPrefix an array of length less than or equal to the
   *   dimensionality of key space of attributeName; entry at index i contains
   *   a "representative" from dimension i + 1 of the key space of
   *   attributeName; keyPrefix may be either null or the empty array, in
   *   which case all attribute values bound to objectKey in attributeName will
   *   be returned; if keyPrefix is not empty, all bound values having key
   *   sequences whose beginning matches the specified prefix will be returned.
   * @param cyData the data repository to use to dig for attribute values.
   * @param cyDataDef the data definition registry to use to find out about
   *   the dimensionality of attributeName.
   * @return a list of all bound values on objectKey in attributeName
   *   along key space prefix keyPrefix, with duplicate values included; the
   *   returned list is never null; elements in the returned
   *   list are ordered arbitrarily; subsequent operations on cyData or
   *   cyDataDef will have no effect on the returned list.
   * @exception IllegalStateException if attributeName is not an existing
   *   attribute definition in cyData and cyDataDef.
   * @exception NullPointerException if any one of the input parameters except
   *   for keyPrefix is null, or if keyPrefix is of positive length and any one
   *   of its entries is null.
   * @exception ClassCastException if keyPrefix is of positive length and any
   *   one of its entries does not match the type of object specified
   *   by corresponding dimension type in attributeName's definition.
   * @exception IllegalArgumentException if keyPrefix's length is
   *   greater than the dimensionality of attributeName's key space.
   */
  public static List getAllAttributeValuesAlongPrefix(
                                              final String objectKey,
                                              final String attributeName,
                                              final Object[] keyPrefix,
                                              final CyData cyData,
                                              final CyDataDefinition cyDataDef)
  {
    final ArrayList bucket = new ArrayList();
    final int keyspaceDims =
      cyDataDef.getAttributeKeyspaceDimensionality(attributeName);
    final int prefixDims = (keyPrefix == null ? 0 : keyPrefix.length);
    if (keyspaceDims <= prefixDims) {
      final Object attrVal = cyData.getAttributeValue
        (objectKey, attributeName, keyPrefix); // May trigger exception; OK.
      if (attrVal != null) bucket.add(attrVal); }
    else {
      final Object[] keyPrefixCopy = new Object[prefixDims];
      for (int i = 0; i < prefixDims; i++) keyPrefixCopy[i] = keyPrefix[i];
      r_getAllAttributeValues(objectKey, attributeName, cyData,
                              bucket, keyPrefixCopy, keyspaceDims); }
    return bucket;
  }

  // Recursive helper for getAllAttributeValues() and
  // getAllAttributeValuesAlongPrefix().
  private static void r_getAllAttributeValues(final String objectKey,
                                              final String attributeName,
                                              final CyData dataRegistry,
                                              final ArrayList bucket,
                                              final Object[] prefixSoFar,
                                              final int keyspaceDims)
  {
    final CountedEnumeration currentKeyspan =
      dataRegistry.getAttributeKeyspan(objectKey, attributeName, prefixSoFar);
    final Object[] newPrefix = new Object[prefixSoFar.length + 1];
    for (int i = 0; i < prefixSoFar.length; i++) newPrefix[i] = prefixSoFar[i];
    while (currentKeyspan.hasMoreElements()) {
      newPrefix[newPrefix.length - 1] = currentKeyspan.nextElement();
      if (keyspaceDims == newPrefix.length) // The final dimension.
        bucket.add(dataRegistry.getAttributeValue
                   (objectKey, attributeName, newPrefix));
      else // Not the final dimension.
        r_getAllAttributeValues(objectKey, attributeName, dataRegistry,
                                bucket, newPrefix, keyspaceDims); }
  }

  /**
   * @return a list of Object[]; each Object[] in the returned list is
   *   a unique full key into a bound value; the returned list is never null,
   *   and always contains the full set of key sequences registered on
   *   objectKey in attributeName.
   */
  public static List getAllAttributeKeys(final String objectKey,
                                         final String attributeName,
                                         final CyData cyData,
                                         final CyDataDefinition cyDataDef)
  {
    final ArrayList bucket = new ArrayList();
    final int keyspaceDims =
      cyDataDef.getAttributeKeyspaceDimensionality(attributeName);
    if (keyspaceDims < 1) { // It's either 0 or -1.
      final Object attrVal = cyData.getAttributeValue
        (objectKey, attributeName, null); // May trigger exception; OK.
      if (attrVal != null) bucket.add(new Object[0]); }
    else { // keyspaceDims > 0.
      r_getAllAttributeKeys(objectKey, attributeName, cyData,
                            bucket, new Object[0], keyspaceDims); }
    return bucket;
  }

  public static List getAllAttributeKeysAlongPrefix(
                                              final String objectKey,
                                              final String attributeName,
                                              final Object[] keyPrefix,
                                              final CyData cyData,
                                              final CyDataDefinition cyDataDef)
  {
    throw new IllegalStateException("not implemented yet");
  }

  // Recursive helper for getAllAttributeKeys() and
  // getAllAttributeKeysAlongPrefix().
  private static void r_getAllAttributeKeys(final String objectKey,
                                            final String attributeName,
                                            final CyData dataRegistry,
                                            final ArrayList bucket,
                                            final Object[] prefixSoFar,
                                            final int keyspaceDims)
  {
    final CountedEnumeration currentKeyspan =
      dataRegistry.getAttributeKeyspan(objectKey, attributeName, prefixSoFar);
    if (keyspaceDims == prefixSoFar.length + 1) { // The final dimension.
      while (currentKeyspan.hasMoreElements()) {
        final Object[] newPrefix = new Object[prefixSoFar.length + 1];
        for (int i = 0; i < prefixSoFar.length; i++)
          newPrefix[i] = prefixSoFar[i];
        newPrefix[newPrefix.length - 1] = currentKeyspan.nextElement();
        bucket.add(newPrefix); } }
    else { // Not the final dimension.
      final Object[] newPrefix = new Object[prefixSoFar.length + 1];
      for (int i = 0; i < prefixSoFar.length; i++)
        newPrefix[i] = prefixSoFar[i];
      while (currentKeyspan.hasMoreElements()) {
        newPrefix[newPrefix.length - 1] = currentKeyspan.nextElement();
        r_getAllAttributeKeys(objectKey, attributeName, dataRegistry,
                              bucket, newPrefix, keyspaceDims); } }
  }

  /**
   * Convenience method for deleting attribute values along a specified key
   * prefix; this method is only useful with attribute definitions
   * that have nonzero key spaces.<p>
   * TIP: To find out exactly what is deleted by this method, add a
   * CyDataListener to cyData.
   * @param objectKey the object whose attribute values to delete.
   * @param attributeName the attribute definition in which to delete
   *   attribute values.
   * @param keyPrefix an array of length less than or equal to the
   *   dimensionality
   *   of key space of attributeName; entry at index i contains a
   *   "representative" from dimension i + 1 of the key space of attributeName;
   *   keyPrefix may be either null or the empty array, in which case all
   *   attribute values bound to objectKey in attributeName will be deleted,
   *   one at a time; if keyPrefix is not empty, all values having key
   *   sequences whose beginning matches the specified prefix will be deleted.
   * @param cyData the data repository to use to dig for attribute values.
   * @param cyDataDef the data definition registry to use to find out about
   *   the dimensionality of attributeName.
   * @return true if and only if at least one attribute value has been deleted.
   * @exception IllegalStateException if attributeName is not an existing
   *   attribute definition in cyData and cyDataDef.
   * @exception NullPointerException if any one of the input parameters except
   *   for keyPrefix is null, or if keyPrefix is of positive length and any one
   *   of its entries is null.
   * @exception ClassCastException if keyPrefix is [not null and] of positive
   *   length and any one of its entries does not match the type of object
   *   specified by corresponding dimension type in attributeName's definition.
   * @exception IllegalArgumentException if keyPrefix's length is 
   *   greater than the dimensionality of attributeName's key space.
   */
  public static boolean removeAllAttributeValuesAlongPrefix(
                                             final String objectKey,
                                             final String attributeName,
                                             final Object[] keyPrefix,
                                             final CyData cyData,
                                             final CyDataDefinition cyDataDef)
  {
    throw new IllegalStateException("not implemented yet - no worries");
  }

}
