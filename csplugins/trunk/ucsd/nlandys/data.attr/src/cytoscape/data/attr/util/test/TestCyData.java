package cytoscape.data.attr.util.test;

import cytoscape.data.attr.CountedEnumeration;
import cytoscape.data.attr.CyData;
import cytoscape.data.attr.CyDataDefinition;
import cytoscape.data.attr.util.CyDataFactory;
import cytoscape.data.attr.util.CyDataHelpers;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

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
                    CyDataDefinition.TYPE_INTEGER });
    Object[] oneVals = new Object[] { new Double(0.5),
                                      new Double(0.6),
                                      new Double(0.7) };
    data.setAttributeValue
      (oneName, attrName, oneVals[0],
       new Object[] { "Ideker", new Integer(0) });
    data.setAttributeValue
      (oneName, attrName, oneVals[1],
       new Object[] { "Ideker", new Integer(1) });
    data.setAttributeValue
      (oneName, attrName, oneVals[2],
       new Object[] { "Salk", new Integer(0) });
    data.setAttributeValue
      (oneName, attrName, oneVals[1],
       new Object[] { "Salk", new Integer(1) });
    data.setAttributeValue
      (twoName, attrName, new Double(0.4),
       new Object[] { "Salk", new Integer(0) });
    Enumeration foo = data.getAttributeKeyspan
      (oneName, attrName, new Object[] { "Ideker" });
    for (int i = 0; i < 2; i++)
      if (!(foo.nextElement() instanceof java.lang.Integer))
        throw new IllegalStateException("expected Integer");
    if (foo.hasMoreElements())
      throw new IllegalStateException("did not expect more elements");
    o = data.getAttributeValue
      (oneName, attrName, new Object[] { "Ideker", new Integer(1) });
    if (!(((Double) o).doubleValue() == 0.6d))
      throw new IllegalStateException("expected 0.6");
    o = data.getAttributeValue
      (twoName, attrName, new Object[] { "Salk", new Integer(0) });
    if (!(((Double) o).doubleValue() == 0.4d))
      throw new IllegalStateException("expected 0.4");
    o = data.getAttributeValue
      ("noNode", attrName, new Object[] { "Howdy", new Integer(0) });
    if (o != null)
      throw new IllegalStateException("expected null");
    o = data.getAttributeValue
      (twoName, attrName, new Object[] { "Salk", new Integer(1) });
    if (o != null)
      throw new IllegalStateException("expected null");
    CountedEnumeration boundValsOne =
      CyDataHelpers.getAllAttributeValues(oneName, attrName, data, def);
    int count = 0;
    while (boundValsOne.hasMoreElements()) {
      Object boundVal = boundValsOne.nextElement();
      count++;
      for (int i = 0;; i++) {
        if (boundVal.equals(oneVals[i])) break; } }
    if (count != 4) throw new IllegalStateException("count not 3");
    List l = CyDataHelpers.getAllAttributeValuesAlongPrefix
      (oneName, attrName, new Object[] { "Salk" }, data, def);
    if (l.size() != 2) throw new IllegalStateException("expected 2");
  }

}
