package cytoscape.data.attr.util.test;

import cytoscape.data.attr.CyData;
import cytoscape.data.attr.CyDataDefinition;
import cytoscape.data.attr.util.CyDataFactory;

import java.util.Enumeration;

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
    data.setAttributeValue
      (oneName, attrName, new Double(0.5),
       new Object[] { "Ideker", new Long(0) });
    data.setAttributeValue
      (oneName, attrName, new Double(0.6),
       new Object[] { "Ideker", new Long(1) });
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
  }

}
