package csplugins.layout;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntIntHash;
import cytoscape.util.intr.MinIntHeap;
import cytoscape.view.CyNetworkView;
import giny.view.NodeView;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

// This is a single-purpose plugin to specifically address something that
// Sourav needed.
public class SouravPlugin extends CytoscapePlugin
{

  private static void copyInto(IntHash hash, int[] arr, int beginIndex)
  {
    IntEnumerator enum = hash.elements();
    while (enum.numRemaining() > 0)
      arr[beginIndex++] = ~enum.nextInt();
  }

  public SouravPlugin()
  {
    JMenuItem sourav = new JMenuItem(new AbstractAction("Sourav's worries")
      {
        public void actionPerformed(ActionEvent e)
        {
          CyNetwork cyNet = Cytoscape.getCurrentNetwork();
          int[] nodeInxs = cyNet.getNodeIndicesArray();
          IntIntHash hash = new IntIntHash();
          MinIntHeap orderedAttrs = new MinIntHeap();
          int numUniqueAttrs = 0;
          for (int i = 0; i < nodeInxs.length; i++)
          {
            Object souravsAttrObj =
              cyNet.getNodeAttributeValue(nodeInxs[i], "cluster");
            if (souravsAttrObj == null) continue;
            if (!(souravsAttrObj instanceof Double)) continue;
            Double souravsAttrDbl = (Double) souravsAttrObj;
            int souravsAttrInt = (int) (souravsAttrDbl.doubleValue());
            if (hash.get(souravsAttrInt) < 0) { // Not in hash yet.
              hash.put(souravsAttrInt, numUniqueAttrs++);
              orderedAttrs.toss(souravsAttrInt); }
          }
          IntHash[] nodeAttrMap = new IntHash[numUniqueAttrs];
          for (int i = 0; i < nodeAttrMap.length; i++) {
            nodeAttrMap[i] = new IntHash(); }
          for (int i = 0; i < nodeInxs.length; i++)
          {
            Double souravsAttrObj = (Double)
              cyNet.getNodeAttributeValue(nodeInxs[i], "cluster");
            if (souravsAttrObj == null) continue;
            int souravsAttrInt = (int) (souravsAttrObj.doubleValue());
            int index = hash.get(souravsAttrInt);
            nodeAttrMap[index].put(~nodeInxs[i]);
          }
          while (orderedAttrs.size() > 0) {
            int nextAttr = orderedAttrs.deleteMin();
            int i = hash.get(nextAttr);
            IntHash neighbors = new IntHash();
            IntEnumerator nodeEnum = nodeAttrMap[i].elements();
            while (nodeEnum.numRemaining() > 0)
            {
              int node = ~nodeEnum.nextInt();
              int[] neighborsArray = cyNet.neighborsArray(node);
              for (int j = 0; j < neighborsArray.length; j++)
              {
                int neighbor = neighborsArray[j];
                if (nodeAttrMap[i].get(~neighbor) < 0)
                  neighbors.put(~neighbor);
              }
            }
            int[] allNodes = new int[nodeAttrMap[i].size() + neighbors.size()];
            copyInto(nodeAttrMap[i], allNodes, 0);
            copyInto(neighbors, allNodes, nodeAttrMap[i].size());
            int[] allEdges = cyNet.getConnectingEdgeIndicesArray(allNodes);
            CyNetwork newNetwork = Cytoscape.createNetwork
              (allNodes, allEdges, "" + nextAttr, cyNet);
            CyNetworkView newView = Cytoscape.createNetworkView(newNetwork);
            IntEnumerator theNodes = nodeAttrMap[i].elements();
            int index = 0;
            while (theNodes.numRemaining() > 0) {
              int theNode = ~theNodes.nextInt();
              NodeView theNodeView = newView.getNodeView(theNode);
              theNodeView.setXPosition(100 * index++);
              theNodeView.setYPosition(0); }
            theNodes = neighbors.elements();
            index = 0;
            while (theNodes.numRemaining() > 0) {
              int theNode = ~theNodes.nextInt();
              NodeView theNodeView = newView.getNodeView(theNode);
              theNodeView.setXPosition(100 * index++);
              theNodeView.setYPosition(200); }
          }
        }
      });
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Layout").add
      (sourav);
  }                                  

}
