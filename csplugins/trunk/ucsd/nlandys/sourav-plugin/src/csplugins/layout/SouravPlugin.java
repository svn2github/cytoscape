package csplugins.layout;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntIntHash;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

public class SouravPlugin extends CytoscapePlugin
{

  private static int size(IntHash hash)
  {
    return hash.elements().numRemaining();
  }

  private static void copyInto(IntHash hash, int[] arr, int beginIndex)
  {
    IntEnumerator enum = hash.elements();
    while (enum.numRemaining() > 0)
      arr[beginIndex++] = enum.nextInt();
  }

  public SouravPlugin()
  {
    JMenuItem sourav = new JMenuItem(new AbstractAction("Sourav's worries")
      {
        public void actionPerformed(ActionEvent e)
        {
          CyNetwork cyNet = Cytoscape.getCurrentNetwork();
          int[] edgeInxs = cyNet.getEdgeIndicesArray();
          IntIntHash hash = new IntIntHash();
          int numUniqueAttrs = 0;
          for (int i = 0; i < edgeInxs.length; i++)
          {
            Object souravsAttrObj =
              cyNet.getNodeAttributeValue(edgeInxs[i], "cluster");
            if (souravsAttrObj == null) continue;
            int souravsAttrInt = (int)
              Double.parseDouble((String) souravsAttrObj);
            if (hash.get(souravsAttrInt) < 0) { // Not in hash yet.
              hash.put(souravsAttrInt, numUniqueAttrs++); }
          }
          IntHash[] nodeAttrMap = new IntHash[numUniqueAttrs];
          for (int i = 0; i < nodeAttrMap.length; i++) {
            nodeAttrMap[i] = new IntHash(); }
          for (int i = 0; i < edgeInxs.length; i++)
          {
            Object souravsAttrObj =
              cyNet.getNodeAttributeValue(edgeInxs[i], "cluster");
            if (souravsAttrObj == null) continue;
            int souravsAttrInt = (int)
              Double.parseDouble((String) souravsAttrObj);
            int index = hash.get(souravsAttrInt);
            nodeAttrMap[index].put(edgeInxs[i]);
          }
          for (int i = 0; i < nodeAttrMap.length; i++)
          {
            IntHash neighbors = new IntHash();
            IntEnumerator nodeEnum = nodeAttrMap[i].elements();
            while (nodeEnum.numRemaining() > 0)
            {
              int node = nodeEnum.nextInt();
              int[] neighborsArray = cyNet.neighborsArray(node);
              for (int j = 0; j < neighborsArray.length; j++)
              {
                int neighbor = neighborsArray[j];
                if (nodeAttrMap[i].get(neighbor) < 0)
                  neighbors.put(neighbor);
              }
            }
            int[] allNodes = new int[size(nodeAttrMap[i]) + size(neighbors)];
            copyInto(nodeAttrMap[i], allNodes, 0);
            copyInto(neighbors, allNodes, size(nodeAttrMap[i]));
            int[] allEdges = cyNet.getConnectingEdgeIndicesArray(allNodes);
            Cytoscape.createNetwork(allNodes, allEdges,
                                    "" + System.currentTimeMillis(), cyNet);
          }
        }
      });
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Layout").add
      (sourav);
  }                                  

}
