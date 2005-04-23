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
  public static CountedEnumeration distinctBoundValues(
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
      r_distinctBoundValues(objectKey, attributeName, cyData,
                            duplicateFilter, dim1Keys,
                            new Object[0], keyspaceDims); }
    return new CyDataModel.Iterator2Enumeration
      (duplicateFilter.keySet().iterator(), duplicateFilter.size());
  }

  // Recursive helper for distinctBoundValues().
  private static void r_distinctBoundValues(
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
        r_distinctBoundValues(objectKey, attributeName, dataRegistry,
                              duplicateFilter, newKeyspan,
                              newPrefix, keyspaceDims); } }
  }

}
