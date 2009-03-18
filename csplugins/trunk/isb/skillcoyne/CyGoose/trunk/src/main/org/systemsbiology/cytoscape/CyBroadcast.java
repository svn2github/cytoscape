/**
 *
 */
package org.systemsbiology.cytoscape;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.logger.CyLogger;
import org.systemsbiology.cytoscape.dialog.CyAttrDialog;
import org.systemsbiology.cytoscape.dialog.GooseDialog;
import org.systemsbiology.gaggle.core.Boss;
import org.systemsbiology.gaggle.core.datatypes.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/** @author skillcoy */
public class CyBroadcast
  {
  private static CyLogger logger = CyLogger.getLogger(CyBroadcast.class);

  private GooseDialog gDialog;
  private Boss gaggleBoss;

  private String broadcastID = "ID";

  // text strings for popup dialog boxes
  private String broadcastStr = "The following checked attributes will be captured for broadcast.\n"
      + "Click \"OK\" to proceed or \"Cancel\" to cancel transaction.";

  /**
   *
   */
  public CyBroadcast(GooseDialog Dialog, Boss boss)
    {
    this.gDialog = Dialog;
    this.gaggleBoss = boss;
    }

  // very basically for the moment we will only broadcast by ID
  public void broadcastNameList(CyGoose Goose, String TargetGoose)
    {
    logger.info("broadcastNameList");
    if (Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().size() <= 0)
      {
      GagglePlugin.showDialogBox("No nodes were selected for list broadcast.",
          "Warning", JOptionPane.WARNING_MESSAGE);
      return;
      }

    Set<CyNode> SelectedNodes = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes();
    Iterator<CyNode> NodesIter = SelectedNodes.iterator();

    /*Set<CyEdge> SelectedEdges = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedEdges();
 Iterator<CyEdge> EdgesIter = SelectedEdges.iterator();*/

    // warning: no nodes are selected for broadcast
    if (SelectedNodes.size() == 0) // && SelectedEdges.size() == 0) 
      {
      GagglePlugin.showDialogBox("No nodes or edges selected for broadcast.", "Warning",
          JOptionPane.WARNING_MESSAGE);
      return;
      }

    ArrayList<String> SelectedIds = new ArrayList<String>();
    while (NodesIter.hasNext())
      SelectedIds.add(NodesIter.next().getIdentifier());

    // don't need the edge list anymore
    /* while (EdgesIter.hasNext())
              SelectedIds.add(EdgesIter.next().getIdentifier()); */

    String Ids[] = new String[SelectedIds.size()];
    Namelist namelist = new Namelist();
    namelist.setSpecies(gDialog.getSpecies());
    namelist.setNames(SelectedIds.toArray(Ids));

    try
      {
      gaggleBoss.broadcastNamelist(Goose.getName(), TargetGoose, namelist);
      }
    catch (Exception E)
      {
      String msg = "Failed to broadcast list of names to " + TargetGoose;
      GagglePlugin.showDialogBox(msg, "Error", JOptionPane.ERROR_MESSAGE);
      logger.error(msg, E);
      }
    }

  // TODO broadcast edge attributes as well
  // broadcasts hash of selected attributes
  public void broadcastTuple(CyGoose Goose, String TargetGoose)
    {
    logger.info("broadcastTuple");

    Set<CyNode> SelectedNodes = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes();

    // warning: no nodes are selected for broadcast
    if (SelectedNodes.size() == 0)
      {
      GagglePlugin.showDialogBox("No nodes selected for broadcast.", "Warning",
          JOptionPane.WARNING_MESSAGE);
      return;
      }

    // pass string of attribute names
    String[] AllAttrNames = Cytoscape.getNodeAttributes().getAttributeNames();

    final CyGoose goose = Goose;
    final String target = TargetGoose;

    // confirmAttrSelection(AllAttrNames, "HashMap");
    AttrSelectAction okAction = new AttrSelectAction()
    {
    public void takeAction(String[] selectAttr)
      {
      broadcastTuple(selectAttr, goose, target);
      }
    };

    CyAttrDialog dialog = new CyAttrDialog(AllAttrNames, okAction,
        CyAttrDialog.MULTIPLE_SELECT);
    dialog.setDialogText(broadcastStr);
    dialog.preSelectCheckBox(AllAttrNames);
    dialog.buildDialogWin();
    }


  private void broadcastTuple(String[] attrNames, CyGoose goose,
                              String targetGoose)
    {
    if (Cytoscape.getNetwork(goose.getNetworkId()).getSelectedNodes().size() <= 0)
      {
      GagglePlugin.showDialogBox("No nodes were selected for tuple broadcast.",
          "Warning", JOptionPane.WARNING_MESSAGE);
      return;
      }

    GaggleTuple gaggleTuple = new GaggleTuple();
    gaggleTuple.setSpecies(gDialog.getSpecies());
    gaggleTuple.setName(Cytoscape.getNetwork(goose.getNetworkId()).getTitle()); //why?

    Set<CyNode> selectedNodes = Cytoscape.getNetwork(goose.getNetworkId()).getSelectedNodes();
    Iterator<CyNode> nodeIter = selectedNodes.iterator();

    // create a string array of node names
    String[] nodeArr = new String[selectedNodes.size()];
    for (int i = 0; (i < selectedNodes.size()) && nodeIter.hasNext(); i++)
      {
      CyNode currentNode = (CyNode) nodeIter.next();
      nodeArr[i] = currentNode.getIdentifier();
      }

    Tuple dataTuple = new Tuple();
    Tuple metadata = new Tuple();
    metadata.addSingle(new Single("condition", "static"));
    gaggleTuple.setMetadata(metadata);

    CyAttributes nodeAtts = Cytoscape.getNodeAttributes();

    for (String attr : attrNames)
      {
      switch (nodeAtts.getType(attr))
        {
        case CyAttributes.TYPE_INTEGER:
          for (String nodeName : nodeArr)
            {
            Tuple row = new Tuple();
            row.addSingle(new Single(nodeName));
            row.addSingle(new Single(attr));
            row.addSingle(new Single(nodeAtts.getIntegerAttribute(nodeName, attr)));
            dataTuple.addSingle(new Single(row));
            }
          break;
        case CyAttributes.TYPE_FLOATING:
          for (String nodeName : nodeArr)
            {
            Tuple row = new Tuple();
            row.addSingle(new Single(nodeName));
            row.addSingle(new Single(attr));
            row.addSingle(new Single(nodeAtts.getDoubleAttribute(nodeName, attr)));
            dataTuple.addSingle(new Single(row));
            }
          break;
        case CyAttributes.TYPE_STRING:
          for (String nodeName : nodeArr)
            {
            if (nodeAtts.getStringAttribute(nodeName, attr) != null)
              {
              Tuple row = new Tuple();
              row.addSingle(new Single(nodeName));
              row.addSingle(new Single(attr));
              row.addSingle(new Single(nodeAtts.getStringAttribute(nodeName, attr)));
              dataTuple.addSingle(new Single(row));
              }
            }
          break;
        }
      }

    gaggleTuple.setData(dataTuple);
    gaggleTuple.setSpecies(this.gDialog.getSpecies());

    try
      {
      //broadcastTuple(goose, targetGoose); // bogus call
      gaggleBoss.broadcastTuple(goose.getName(), targetGoose, gaggleTuple);
      }
    catch (Exception E)
      {
      String msg = "Failed to broadcast map to " + targetGoose;
      GagglePlugin.showDialogBox(msg, "Error", JOptionPane.ERROR_MESSAGE);
      logger.error(msg, E);
      }
    }


  public void broadcastDataMatrix(CyGoose Goose, String TargetGoose)
    {
    logger.info("broadcastDataMatrix");

    Set<CyNode> SelectedNodes = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes();

    // warning: no nodes are selected for broadcast
    if (SelectedNodes.size() == 0)
      {
      GagglePlugin.showDialogBox("No nodes were selected for Data Matrix broadcast.", "Warning",
          JOptionPane.WARNING_MESSAGE);
      return;
      }

    // create an array of experiment conditions (columnTitles in DataMatrix)
    ArrayList<String> condNamesArrList = new ArrayList<String>();
    String[] attributeNames = Cytoscape.getNodeAttributes().getAttributeNames();

    for (String CurrentAttr : attributeNames)
      {
      // assume all DOUBLE type attributes are expression data
      if (Cytoscape.getNodeAttributes().getType(CurrentAttr) == CyAttributes.TYPE_FLOATING)
        condNamesArrList.add(CurrentAttr);
      }

    // move everything from ArrayList to a String array
    String[] condNames = new String[condNamesArrList.size()];
    condNamesArrList.toArray(condNames);

    final CyGoose goose = Goose;
    final String target = TargetGoose;

    // dialog for user to select attributes for broadcast
    AttrSelectAction okAction = new AttrSelectAction()
    {
    public void takeAction(String[] selectAttr)
      {
      broadcastDataMatrix(selectAttr, goose, target);
      }
    };

    if (condNames.length > 0)
      {
      CyAttrDialog dialog = new CyAttrDialog(condNames, okAction, CyAttrDialog.MULTIPLE_SELECT);
      dialog.setDialogText(broadcastStr);
      dialog.preSelectCheckBox(condNames);
      dialog.buildDialogWin();
      }
    else
      {
      GagglePlugin.showDialogBox(
          "The selected nodes do not have numerical attributes for a matrix",
          "Warning", JOptionPane.WARNING_MESSAGE);
      }
    }

  private void broadcastDataMatrix(String[] condNames, CyGoose Goose,
                                   String TargetGoose)
    {
    if (Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().size() <= 0)
      {
      GagglePlugin.showDialogBox(
          "No nodes were selected for Data Matrix broadcast.", "Warning",
          JOptionPane.WARNING_MESSAGE);
      return;
      }

    // initialize DataMatrix
    DataMatrix matrix = new DataMatrix();
    matrix.setColumnTitles(condNames);
    matrix.setSpecies(gDialog.getSpecies());

    // loop through all flagged nodes and construct a DataMatrix with
    // row=columnNames & column=condNames
    Iterator<CyNode> selectedNodesIter = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().iterator();
    while (selectedNodesIter.hasNext())
      {
      double[] condVals = new double[condNames.length];
      CyNode CurrentSelectedNode = selectedNodesIter.next();
      String NodeId = CurrentSelectedNode.getIdentifier();
      for (int i = 0; i < condNames.length; i++)
        {
        try
          {
          Double val = Cytoscape.getNodeAttributes().getDoubleAttribute(NodeId,
              condNames[i]);
          if (val != null) condVals[i] = val.doubleValue();
          }
        catch (Exception ex)
          {
          logger.warn("broadcastDataMatrix() error: incompatible data type for "+ condNames[i], ex);
          }
        }
      // use other attribute to identify node if selected by user
      // At some point 'broadcastID' is meant to allow you to select the attribute name to broadcast as an ID, has not yet been added
      NodeId = Cytoscape.getNodeAttributes().getStringAttribute(NodeId, broadcastID);

      // add new row to DataMatrix
      matrix.addRow(NodeId, condVals);
      }

    try
      {
      this.gaggleBoss.broadcastMatrix(Goose.getName(), TargetGoose, matrix);
      }
    catch (Exception E)
      {
      String msg = "Failed to broadcast matrix to " + TargetGoose;
      GagglePlugin.showDialogBox(msg, "Error", JOptionPane.ERROR_MESSAGE);
      logger.error(msg, E);
      }
    }

  public void broadcastNetwork(CyGoose Goose, String TargetGoose)
    {
    logger.info("broadcastNetwork " + Cytoscape.getNetwork(Goose.getNetworkId()).getIdentifier());

    Network gaggleNetwork = new Network();
    gaggleNetwork.setSpecies(gDialog.getSpecies());

    Iterator<CyNode> nodesIter = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().iterator();
    while (nodesIter.hasNext())
      {
      CyNode currentSelectedNode = nodesIter.next();
      gaggleNetwork.add(currentSelectedNode.getIdentifier());
      }

    Iterator<CyEdge> edgesIter = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedEdges().iterator();
    CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();
    while (edgesIter.hasNext())
      {
      CyEdge currentSelectedEdge = edgesIter.next();

      CyNode sourceNode = (CyNode) currentSelectedEdge.getSource();
      CyNode targetNode = (CyNode) currentSelectedEdge.getTarget();

      // create a new GaggleInteraction for broadcast
      String interactionType = edgeAtts.getStringAttribute(currentSelectedEdge.getIdentifier(), Semantics.INTERACTION);
      Interaction gaggleInteraction = new Interaction(
          sourceNode.getIdentifier(), targetNode.getIdentifier(),
          interactionType, currentSelectedEdge.isDirected());
      gaggleNetwork.add(gaggleInteraction);
      }

    gaggleNetwork = addAttributes(gaggleNetwork);
    logger.debug("in broadcastnetwork, species is " + gaggleNetwork.getSpecies());
    try
      {
      this.gaggleBoss.broadcastNetwork(Goose.getName(), TargetGoose, gaggleNetwork);
      }
    catch (Exception E)
      {
      logger.error(E.getMessage(), E);
      }
    }


  private Network addAttributes(Network gaggleNet)
    {
    for (String id : gaggleNet.getNodes())
      gaggleNet = addAttributes(id, Cytoscape.getNodeAttributes(), gaggleNet, NetworkObject.NODE);

    for (Interaction interaction : gaggleNet.getInteractions())
      gaggleNet = addAttributes(interaction.toString(), Cytoscape.getEdgeAttributes(), gaggleNet, NetworkObject.EDGE);

    return gaggleNet;
    }


  // add attributes to the node/edge
  private Network addAttributes(String Identifier, CyAttributes cyAtts, Network gaggleNet, NetworkObject obj)
    {

    for (String AttributeName : cyAtts.getAttributeNames())
      {
      Object Value = "";

      // don't think we should pass on hidden attributes, they aren't useful to the user
      if (!cyAtts.getUserVisible(AttributeName))
        continue;

      switch (cyAtts.getType(AttributeName))
        {
        case CyAttributes.TYPE_BOOLEAN:
          Value = cyAtts.getBooleanAttribute(Identifier, AttributeName);
          break;

        case CyAttributes.TYPE_INTEGER:
          Value = cyAtts.getIntegerAttribute(Identifier, AttributeName);
          break;

        case CyAttributes.TYPE_STRING:
          Value = cyAtts.getStringAttribute(Identifier, AttributeName);
          break;

        case CyAttributes.TYPE_FLOATING:
          Value = cyAtts.getDoubleAttribute(Identifier, AttributeName);
          break;
        }
      ;
      if (Value == null) Value = "";
      switch (obj)
        {
        case NODE:
          gaggleNet.addNodeAttribute(Identifier, AttributeName, Value);
          break;

        case EDGE:
          gaggleNet.addEdgeAttribute(Identifier, AttributeName, Value);
          break;
        }
      }

    return gaggleNet;
    }


  }
