package cytoscape.data.attr.util.test;

import cytoscape.data.attr.CyData;
import cytoscape.data.attr.CyDataDefinition;
import cytoscape.data.attr.util.CyDataFactory;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

public final class TestCyData
{

  public final static void main(final String[] args)
  {
    Object o = CyDataFactory.instantiateDataModel();
    final CyDataDefinition def = (CyDataDefinition) o;
    final CyData data = (CyData) o;
    final String attrName = "p-values";
    final String oneName = "node1";
    final String twoName = "node2";
    final String threeName = "node3";
    def.defineAttribute
      (attrName, CyDataDefinition.TYPE_FLOATING_POINT,
       new byte[] { CyDataDefinition.TYPE_STRING,
                    CyDataDefinition.TYPE_INTEGER },
       new String[] { "experiment", "multi-value offset" });
    Object[] oneVals = new Object[] { new Double(0.5),
                                      new Double(0.6),
                                      new Double(0.7) };
    data.setAttributeValue
      (oneName, attrName, oneVals[0],
       new Object[] { "Ideker", new Long(0) });
    data.setAttributeValue
      (oneName, attrName, oneVals[1],
       new Object[] { "Ideker", new Long(1) });
    data.setAttributeValue
      (oneName, attrName, oneVals[2],
       new Object[] { "Salk", new Long(0) });
    data.setAttributeValue
      (oneName, attrName, oneVals[1],
       new Object[] { "Salk", new Long(1) });
    data.setAttributeValue
      (twoName, attrName, new Double(0.4),
       new Object[] { "Salk", new Long(0) });
    Enumeration foo = data.getAttributeKeyspan
      (oneName, attrName, new Object[] { "Ideker" });
    for (int i = 0; i < 2; i++)
      if (!(foo.nextElement() instanceof java.lang.Long))
        throw new IllegalStateException("expected Long");
    if (foo.hasMoreElements())
      throw new IllegalStateException("did not expect more elements");
    o = data.getAttributeValue
      (oneName, attrName, new Object[] { "Ideker", new Long(1) });
    if (!(((Double) o).doubleValue() == 0.6d))
      throw new IllegalStateException("expected 0.6");
    o = data.getAttributeValue
      (twoName, attrName, new Object[] { "Salk", new Long(0) });
    if (!(((Double) o).doubleValue() == 0.4d))
      throw new IllegalStateException("expected 0.4");
    o = data.getAttributeValue
      ("noNode", attrName, new Object[] { "Howdy", new Long(0) });
    if (o != null)
      throw new IllegalStateException("expected null");
    o = data.getAttributeValue
      (twoName, attrName, new Object[] { "Salk", new Long(1) });
    if (o != null)
      throw new IllegalStateException("expected null");
    Iterator boundValsOne =
      distinctBoundValues(oneName, attrName, data, def);
    int count = 0;
    while (boundValsOne.hasNext()) {
      Object boundVal = boundValsOne.next();
      count++;
      for (int i = 0;; i++) {
        if (boundVal.equals(oneVals[i])) break; } }
    if (count != 3) throw new IllegalStateException("count not 3");
  }

  // NOTE: If you want return value of an array containing all attribute
  // values that have been deleted (w/o keys that is) I can do that also.
  private final static void recursiveDelete(final String objectKey,
                                            final String attributeName,
                                            final CyData dataRegistry)
  {
    
  }

  static Iterator distinctBoundValues(final String objectKey,
                                      final String attributeName,
                                      final CyData dataRegistry,
                                      final CyDataDefinition def)
  {
    final HashMap duplicateFilter = new HashMap();
    final int keyspaceDims =
      def.getAttributeKeyspaceDimensionality(attributeName);
    if (keyspaceDims < 1) { // It's either 0 or -1.
      final Object attrVal = dataRegistry.getAttributeValue
        (objectKey, attributeName, null); // May trigger exception; OK.
      if (attrVal != null) duplicateFilter.put(attrVal, null); }
    else { // keyspaceDims > 1.
      final Enumeration dim1Keys = dataRegistry.getAttributeKeyspan
        (objectKey, attributeName, null);
      r_distinctBoundValues(objectKey, attributeName, dataRegistry,
                            duplicateFilter, dim1Keys,
                            new Object[0], keyspaceDims); }
    return duplicateFilter.keySet().iterator();
  }

  // Recursive helper for distinctBoundValues().
  private static void r_distinctBoundValues(final String objectKey,
                                            final String attributeName,
                                            final CyData dataRegistry,
                                            final HashMap duplicateFilter,
                                            final Enumeration currentKeyspan,
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
        final Enumeration newKeyspan = dataRegistry.getAttributeKeyspan
          (objectKey, attributeName, newPrefix);
        r_distinctBoundValues(objectKey, attributeName, dataRegistry,
                              duplicateFilter, newKeyspan,
                              newPrefix, keyspaceDims); } }
  }

}
