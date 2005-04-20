package cytoscape.data.attr.util.test;

import cytoscape.data.attr.CyEdgeData;
import cytoscape.data.attr.CyEdgeDataDefinition;
import cytoscape.data.attr.CyNodeData;
import cytoscape.data.attr.CyNodeDataDefinition;
import cytoscape.data.attr.util.CyDataFactory;

import java.util.Enumeration;

public final class TestCyData
{

  public final static void main(final String[] args)
  {
    Object o = CyDataFactory.instantiateDataModel();
    final CyNodeDataDefinition nodeDef = (CyNodeDataDefinition) o;
    final CyNodeData nodeData = (CyNodeData) o;
    final CyEdgeDataDefinition edgeDef = (CyEdgeDataDefinition) o;
    final CyEdgeData edgeData = (CyEdgeData) o;
    final String nodeAttrName = "p-values";
    final String nodeOneName = "node1";
    final String nodeTwoName = "node2";
    final String nodeThreeName = "node3";
    nodeDef.defineNodeAttribute
      (nodeAttrName, CyNodeDataDefinition.TYPE_FLOATING_POINT,
       new byte[] { CyNodeDataDefinition.TYPE_STRING,
                    CyNodeDataDefinition.TYPE_INTEGER },
       new String[] { "experiment", "multi-value offset" });
    nodeData.setNodeAttributeValue
      (nodeOneName, nodeAttrName, new Double(0.5),
       new Object[] { "Ideker", new Long(0) });
    nodeData.setNodeAttributeValue
      (nodeOneName, nodeAttrName, new Double(0.6),
       new Object[] { "Ideker", new Long(1) });
    nodeData.setNodeAttributeValue
      (nodeTwoName, nodeAttrName, new Double(0.4),
       new Object[] { "Salk", new Long(0) });
    Enumeration enum = nodeData.getNodeAttributeKeyspan
      (nodeOneName, nodeAttrName, new Object[] { "Ideker" });
    for (int i = 0; i < 2; i++)
      if (!(enum.nextElement() instanceof java.lang.Long))
        throw new IllegalStateException("expected Long");
    if (enum.hasMoreElements())
      throw new IllegalStateException("did not expect more elements");
    o = nodeData.getNodeAttributeValue
      (nodeOneName, nodeAttrName, new Object[] { "Ideker", new Long(1) });
    if (!(((Double) o).doubleValue() == 0.6d))
      throw new IllegalStateException("expected 0.6");
    o = nodeData.getNodeAttributeValue
      (nodeTwoName, nodeAttrName, new Object[] { "Salk", new Long(0) });
    if (!(((Double) o).doubleValue() == 0.4d))
      throw new IllegalStateException("expected 0.4");
    o = nodeData.getNodeAttributeValue
      ("noNode", nodeAttrName, new Object[] { "Howdy", new Long(0) });
    if (o != null)
      throw new IllegalStateException("expected null");
    o = nodeData.getNodeAttributeValue
      (nodeTwoName, nodeAttrName, new Object[] { "Salk", new Long(1) });
    if (o != null)
      throw new IllegalStateException("expected null");
  }

}
