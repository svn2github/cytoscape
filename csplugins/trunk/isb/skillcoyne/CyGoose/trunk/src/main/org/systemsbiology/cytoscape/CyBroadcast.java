/**
 * 
 */
package org.systemsbiology.cytoscape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import org.systemsbiology.cytoscape.dialog.CyAttrDialog;
import org.systemsbiology.cytoscape.dialog.GooseDialog;
import org.systemsbiology.cytoscape.CyGoose;

import org.systemsbiology.gaggle.boss.Boss;
import org.systemsbiology.gaggle.experiment.datamatrix.DataMatrix;
//import org.systemsbiology.gaggle.geese.Goose;
import org.systemsbiology.gaggle.network.Network;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

/**
 * @author skillcoy
 *
 */
public class CyBroadcast
	{
	private GooseDialog gDialog;
	private Boss gaggleBoss;

	private String broadcastID = "ID";

 	// text strings for popup dialog boxes
  private String broadcastStr = "The following checked attributes will be captured for broadcast.\n"
      + "Click \"OK\" to proceed or \"Cancel\" to cancel transaction.";

	
	private static void print(String S)
		{ System.out.println(S); }

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
		print("broadcastNameList");
		if (Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().size() <= 0)
			{
			GagglePlugin.showDialogBox("No nodes were selected for list broadcast.", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
			}


    Set<CyNode> SelectedNodes = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes();
    Iterator<CyNode> NodesIter = SelectedNodes.iterator();

    // warning: no nodes are selected for broadcast
    if (SelectedNodes.size() == 0)
      {
      GagglePlugin.showDialogBox("No nodes selected for broadcast.", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
      }

    String[] NodeIds = new String[SelectedNodes.size()];
		// TODO: get species
		String Species = null;

    //CyNode Node;
    for (int i = 0; i < SelectedNodes.size(); i++)
      {
      CyNode Node = NodesIter.next();
			NodeIds[i] = Node.getIdentifier();
			}

		try 
			{ gaggleBoss.broadcast(Goose.getName(), TargetGoose, Species, NodeIds); }
		catch(Exception E) 
			{ 
			GagglePlugin.showDialogBox("Failed to broadcast list of names to " + TargetGoose, "Error", JOptionPane.ERROR_MESSAGE);
			E.printStackTrace(); 
			}
		}

	// broadcasts hash of selected attributes	
	public void broadcastHashMap(CyGoose Goose, String TargetGoose)
		{
		print("broadcastHashMap");

    Set<CyNode> SelectedNodes = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes();

    // warning: no nodes are selected for broadcast
    if (SelectedNodes.size() == 0)
      {
      GagglePlugin.showDialogBox("No nodes selected for broadcast.", "Warning", JOptionPane.WARNING_MESSAGE);
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
          broadcastHashMap(selectAttr, goose, target);
          }
      };

    CyAttrDialog dialog = new CyAttrDialog(AllAttrNames, okAction, CyAttrDialog.MULTIPLE_SELECT);
    dialog.setDialogText(broadcastStr);
    dialog.preSelectCheckBox(AllAttrNames);
    dialog.buildDialogWin();
		}


  private void broadcastHashMap(String[] attrNames, CyGoose Goose, String TargetGoose)
    {
		if (Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().size() <= 0)
			{
			GagglePlugin.showDialogBox("No nodes were selected for map broadcast.", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
			}

    Set selectedNodes = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes();
    Iterator nodeIter = selectedNodes.iterator();

    // create a string of node names
    String[] nodeArr = new String[selectedNodes.size()];
    //CyNode node;
    for (int i = 0; (i < selectedNodes.size()) && nodeIter.hasNext(); i++)
      {
      CyNode CurrentNode = (CyNode) nodeIter.next();
      nodeArr[i] = CurrentNode.getIdentifier();
      }

    HashMap map = new HashMap();
    for (int i = 0; i < attrNames.length; i++)
      {
      String attr = attrNames[i];
      ArrayList namesAndValues = new ArrayList();
      switch (Cytoscape.getNodeAttributes().getType(attr))
        {
        case CyAttributes.TYPE_INTEGER:
        int[] intValues = new int[nodeArr.length];
        for (int j = 0; j < nodeArr.length; j++)
          intValues[j] = Cytoscape.getNodeAttributes().getIntegerAttribute(
              nodeArr[j], attr).intValue();
        // create HashMap
        namesAndValues.add(nodeArr);
        namesAndValues.add(intValues);
        break;
        case CyAttributes.TYPE_FLOATING:
        double[] dbValues = new double[nodeArr.length];
        for (int j = 0; j < nodeArr.length; j++)
          dbValues[j] = Cytoscape.getNodeAttributes().getDoubleAttribute(
              nodeArr[j], attr).doubleValue();
        // create HashMap
        namesAndValues.add(nodeArr);
        namesAndValues.add(dbValues);
        break;
        case CyAttributes.TYPE_BOOLEAN:
        boolean[] boolValues = new boolean[nodeArr.length];
        for (int j = 0; j < nodeArr.length; j++)
          boolValues[j] = Cytoscape.getNodeAttributes().getBooleanAttribute(
              nodeArr[j], attr).booleanValue();
        // create HashMap
        namesAndValues.add(nodeArr);
        namesAndValues.add(boolValues);
        break;
        case CyAttributes.TYPE_STRING:
        String[] strValues = new String[nodeArr.length];
        for (int j = 0; j < nodeArr.length; j++)
          strValues[j] = Cytoscape.getNodeAttributes().getStringAttribute(
              nodeArr[j], attr);
        // create HashMap
        namesAndValues.add(nodeArr);
        namesAndValues.add(strValues);
        break;
        }
      map.put(attr, namesAndValues);
      }

    // String species = CytoscapeInit.getDefaultSpeciesName();
    String species = getSpecies(nodeArr[0]);
    String dataTitle = "";


		try
	    { this.gaggleBoss.broadcast(Goose.getName(), TargetGoose, species, dataTitle, map); }
		catch (Exception E) 
			{ 
			GagglePlugin.showDialogBox("Failed to broadcast map to " + TargetGoose, "Error", JOptionPane.ERROR_MESSAGE);
			E.printStackTrace(); 
			}
		}

	public void broadcastDataMatrix(CyGoose Goose, String TargetGoose)
		{
		print("broadcastDataMatrix"); 

		if (Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().size() <= 0)
			{
			GagglePlugin.showDialogBox("No nodes were selected for Data Matrix broadcast.", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
			}

    Set SelectedNodes = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes();

    // warning: no nodes are selected for broadcast
    if (SelectedNodes.size() == 0)
      {
      GagglePlugin.showDialogBox("No nodes selected for broadcast.", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
      }

    //create an array of experiment conditions (columnTitles in DataMatrix)
    ArrayList<String> condNamesArrList = new ArrayList<String>();
    String[] attributeNames = Cytoscape.getNodeAttributes().getAttributeNames();

    for(String CurrentAttr: attributeNames)
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
      GagglePlugin.showDialogBox("The selected nodes do not have numerical attributes for a matrix", "Warning", JOptionPane.WARNING_MESSAGE);
    	}
		}
	
  private void broadcastDataMatrix(String[] condNames, CyGoose Goose, String TargetGoose)
    {
		if (Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().size() <= 0)
			{
			GagglePlugin.showDialogBox("No nodes were selected for Data Matrix broadcast.", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
			}

    Set selectedNodes = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes();
    Iterator selectedNodesIter = selectedNodes.iterator();

    DataMatrix matrix = new DataMatrix();
    // initialize DataMatrix
    matrix.setColumnTitles(condNames);
    // matrix.setSpecies(getSpecies());

    
    //loop through all flagged nodes and construct a DataMatrix with
    // row=columnNames & column=condNames
    while (selectedNodesIter.hasNext())
      {
      double[] condVals = new double[condNames.length];
      CyNode CurrentSelectedNode = (CyNode) selectedNodesIter.next();
      String NodeId = CurrentSelectedNode.getIdentifier();
      for (int i=0; i < condNames.length; i++)
        {
        try
          {
          Double val = Cytoscape.getNodeAttributes().getDoubleAttribute(NodeId, condNames[i]);
          if (val != null) condVals[i] = val.doubleValue();
          }
        catch (Exception ex)
          {
          System.out.println("broadcastDataMatrix() error: incompatible data type for " + condNames[i]);
          }
        }
      // use other attribute to identify node if selected by user

      if (!broadcastID.equals(CyAttrDialog.DEFAULT_NODE_ID)) // again..."ID" == "ID" WHY??
        {
        NodeId = Cytoscape.getNodeAttributes().getStringAttribute(NodeId, broadcastID);

        // skip any node that is null // shouldn't this happen before?
        if (NodeId == null) continue;
        }
      // add new row to DataMatrix
      matrix.addRow(NodeId, condVals);
      matrix.setSpecies(getSpecies(NodeId)); // er...does this set the species every time 'round?  what if the species is different from one node to another?
      }

		try 
			{ this.gaggleBoss.broadcast(Goose.getName(), TargetGoose, matrix); }
		catch (Exception E) 
			{ 
			GagglePlugin.showDialogBox("Failed to broadcast matrix to " + TargetGoose, "Error", JOptionPane.ERROR_MESSAGE);
			E.printStackTrace(); 
			}
		}

	public void broadcastNetwork(CyGoose Goose, String TargetGoose)
		{
		print("broadcastNetwork " + Cytoscape.getNetwork(Goose.getNetworkId()).getIdentifier());

		Network GaggleNetwork = new Network();
    String Species = "";
		
    Iterator<CyNode> NodesIter = Cytoscape.getNetwork( Goose.getNetworkId() ).getSelectedNodes().iterator();
		CyAttributes NodeAtts = Cytoscape.getNodeAttributes();
		while (NodesIter.hasNext())
			{
			CyNode CurrentSelectedNode = NodesIter.next();
      GaggleNetwork = addNodeAttributes(CurrentSelectedNode, NodeAtts, GaggleNetwork);
			GaggleNetwork.add(CurrentSelectedNode.getIdentifier());
			}
		
  	Iterator<CyEdge> EdgesIter = Cytoscape.getNetwork( Goose.getNetworkId() ).getSelectedEdges().iterator();
		CyAttributes EdgeAtts = Cytoscape.getEdgeAttributes();
    while (EdgesIter.hasNext())
      {
      CyEdge CurrentSelectedEdge = EdgesIter.next();
      GaggleNetwork = addEdgeAttributes(CurrentSelectedEdge, EdgeAtts, GaggleNetwork);

      CyNode SourceNode = (CyNode) CurrentSelectedEdge.getSource();
      CyNode TargetNode = (CyNode) CurrentSelectedEdge.getTarget();
      
      // create a new GaggleInteraction for broadcast
      String InteractionType = EdgeAtts.getStringAttribute(CurrentSelectedEdge.getIdentifier(), Semantics.INTERACTION);
      org.systemsbiology.gaggle.network.Interaction GaggleInteraction = 
				new org.systemsbiology.gaggle.network.Interaction(SourceNode.getIdentifier(), TargetNode.getIdentifier(), 
						InteractionType, CurrentSelectedEdge.isDirected());
      GaggleNetwork.add(GaggleInteraction);

      // again if there's more than one species we'll only get the last one!!!
      Species = this.getSpecies(SourceNode.getIdentifier());
      }
			try 
				{ this.gaggleBoss.broadcast(Goose.getName(), TargetGoose, Species, GaggleNetwork); }
			catch (Exception E) { E.printStackTrace(); }
		}

  // try the "species" attribute first; if not found, use DefaultSpeciesName
	private String getSpecies(String NodeId)
		{
    String Species = "";

    Species = Cytoscape.getNodeAttributes().getStringAttribute(NodeId, Semantics.SPECIES);
    if (Species == null) Species = CytoscapeInit.getProperties().getProperty("defaultSpeciesName");
    
    return Species;
		}	

	
	private Network addNodeAttributes(CyNode Node, CyAttributes NodeAtts, Network GaggleNet)
		{
		for (String AttributeName: NodeAtts.getAttributeNames())
			{
			Object Value = "";
			switch (NodeAtts.getType(AttributeName))
				{
				case CyAttributes.TYPE_BOOLEAN:
					Value = NodeAtts.getBooleanAttribute(Node.getIdentifier(), AttributeName);
					break;
				case CyAttributes.TYPE_INTEGER:
					Value = NodeAtts.getIntegerAttribute(Node.getIdentifier(), AttributeName);
					break;
				case CyAttributes.TYPE_STRING:
					Value = NodeAtts.getStringAttribute(Node.getIdentifier(), AttributeName);
					break;
				case CyAttributes.TYPE_FLOATING:
					Value = NodeAtts.getDoubleAttribute(Node.getIdentifier(), AttributeName);
					break;
				};
			if (Value == null) Value = "";
			GaggleNet.addNodeAttribute(Node.getIdentifier(), AttributeName, Value);
			}
		return GaggleNet;
		}
	
	private Network addEdgeAttributes(CyEdge Edge, CyAttributes EdgeAtts, Network GaggleNet)
		{
		for (String AttributeName: EdgeAtts.getAttributeNames())
			{
			String Value = "";
			switch (EdgeAtts.getType(AttributeName))
				{
				case CyAttributes.TYPE_BOOLEAN:
					Value = Boolean.toString(EdgeAtts.getBooleanAttribute(Edge.getIdentifier(), AttributeName));
					break;
				case CyAttributes.TYPE_INTEGER:
					Value = Integer.toString(EdgeAtts.getIntegerAttribute(Edge.getIdentifier(), AttributeName));
					break;
				case CyAttributes.TYPE_STRING:
					Value = EdgeAtts.getStringAttribute(Edge.getIdentifier(), AttributeName);
					break;
				case CyAttributes.TYPE_FLOATING:
					Value = Double.toString(EdgeAtts.getDoubleAttribute(Edge.getIdentifier(), AttributeName));
					break;
				};
			if (Value == null) Value = ""; 
			GaggleNet.addEdgeAttribute(Edge.getIdentifier(), AttributeName, Value);
			}
		return GaggleNet;
		}

	
	
	}
