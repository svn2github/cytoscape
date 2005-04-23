package cytoscape.data.attr.util;

import cytoscape.data.attr.CountedEnumeration;
import cytoscape.data.attr.CyData;
import cytoscape.data.attr.CyDataDefinition;

import java.util.HashMap;

public final class CyDataHelpers
{
  // NOTE: This class resides in the same package as the CyDataModel
  // implementation of CyData for a good reason.  We may want some
  // of these helper methods to be more optimized than just calling CyDataModel
  // by its CyData interface.  We could provide dual implementations for
  // every method in this class, and choose the path of execution based on
  // whether or not our input object is an instance of CyDataModel.

  // No constructor.  Static methods only.
  private CyDataHelpers() { }

  /**
   * @param objectKey the object whose attribute values to return.
   * @param attributeName the attribute definition in which to find attribute
   *   values.
   * @param cyData the data repository to use to dig for attribute values.
   * @param cyDataDef the data definition registry to use to find out about
   *   the dimensionality of attributeName.
   * @return an enumeration of all bound values on objectKey in
   *   attributeName, with duplicate values removed; the returned enumeration
   *   is never null.
   * @exception IllegalStateException if attributeName is not an existing
   *   attribute definition in cyData and cyDataDef.
   * @exception NullPointerException if any one of the input parameters is
   *   null.
   */
  public static CountedEnumeration getAllAttributeValues(
                                              final String objectKey,
                                              final String attributeName,
                                              final CyData cyData,
                                              final CyDataDefinition cyDataDef)
  {
    final HashMap duplicateFilter = new HashMap();
    final int keyspaceDims =
      cyDataDef.getAttributeKeyspaceDimensionality(attributeName);
    if (keyspaceDims < 1) { // It's either 0 or -1.
      final Object attrVal = cyData.getAttributeValue
        (objectKey, attributeName, null); // May trigger exception; OK.
      if (attrVal != null) duplicateFilter.put(attrVal, null); }
    else { // keyspaceDims > 1.
      final CountedEnumeration dim1Keys =
        cyData.getAttributeKeyspan(objectKey, attributeName, null);
      r_getAllAttributeValues(objectKey, attributeName, cyData,
                              duplicateFilter, dim1Keys,
                              new Object[0], keyspaceDims); }
    return new CyDataModel.Iterator2Enumeration
      (duplicateFilter.keySet().iterator(), duplicateFilter.size());
  }

  // Recursive helper for getAllAttributeValues().
  private static void r_getAllAttributeValues(
                                       final String objectKey,
                                       final String attributeName,
                                       final CyData dataRegistry,
                                       final HashMap duplicateFilter,
                                       final CountedEnumeration currentKeyspan,
                                       final Object[] prefixSoFar,
                                       final int keyspaceDims)
  {
    final Object[] newPrefix = new Object[prefixSoFar.length + 1];
    System.arraycopy(prefixSoFar, 0, newPrefix, 0, prefixSoFar.length);
    if (keyspaceDims == newPrefix.length) { // The final dimension.
      while (currentKeyspan.hasMoreElements()) {
        newPrefix[prefixSoFar.length] = currentKeyspan.nextElement();
        duplicateFilter.put
          (dataRegistry.getAttributeValue(objectKey, attributeName, newPrefix),
           null); } }
    else { // Not the final dimension.
      while (currentKeyspan.hasMoreElements()) {
        newPrefix[prefixSoFar.length] = currentKeyspan.nextElement();
        final CountedEnumeration newKeyspan = dataRegistry.getAttributeKeyspan
          (objectKey, attributeName, newPrefix);
        r_getAllAttributeValues(objectKey, attributeName, dataRegistry,
                                duplicateFilter, newKeyspan,
                                newPrefix, keyspaceDims); } }
  }

  /**
   * Convenience method for discovering attribute values along a specified
   * key prefix.
   * @param objectKey the object whose attribute values to return.
   * @param attributeName the attribute definition in which to find attribute
   *   values.
   * @param keyPrefix an array of length less than or equal to the
   *   dimensionality of key space of attributeName; entry at index i contains
   *   a "representative" from dimension i + 1 of the key space of
   *   attributeName; keyPrefix may be either null or the empty array, in
   *   which case all attribute values bound to objectKey in attributeName will
   *   be returned; if keyPrefix is not empty, all values having key
   *   sequences whose beginning matches the specified prefix will be returned.
   * @param cyData the data repository to use to dig for attribute values.
   * @param cyDataDef the data definition registry to use to find out about
   *   the dimensionality of attributeName.
   * @return an enumeration of all bound values on objectKey in attributeName
   *   along key space prefix keyPrefix, with duplicate values included; the
   *   returned enumeration is never null; elements in the returned
   *   enumeration are ordered arbitrarily.
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
  public static CountedEnumeration getAllAttributeValuesAlongPrefix(
                                              final String objectKey,
                                              final String attributeName,
                                              final Object[] keyPrefix,
                                              final CyData cyData,
                                              final CyDataDefinition cyDataDef)
  {
    throw new IllegalStateException("sorry not yet implemented");
  }

  /**
   * Convenience method for deleting attribute values along a specified key
   * prefix.<p>
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
