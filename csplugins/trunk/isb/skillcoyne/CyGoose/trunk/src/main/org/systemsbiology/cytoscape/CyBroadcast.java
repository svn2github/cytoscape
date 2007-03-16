/**
 * 
 */
package org.systemsbiology.cytoscape;

import org.systemsbiology.cytoscape.dialog.CyAttrDialog;
import org.systemsbiology.cytoscape.dialog.GooseDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.*;

import javax.swing.JOptionPane;

import org.systemsbiology.gaggle.boss.Boss;
import org.systemsbiology.gaggle.experiment.datamatrix.DataMatrix;
import org.systemsbiology.gaggle.network.*;
import org.systemsbiology.gaggle.geese.Goose;

import cytoscape.*;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

/**
 * @author skillcoy
 * 
 */
public class CyBroadcast
	{
	private GooseDialog GDialog;
	private Boss GaggleBoss;
	private Goose CyGoose;

	private String broadcastID = "ID";

 	// text strings for popup dialog boxes
  private String broadcastStr = "The following checked attributes will be captured for broadcast.\n"
      + "Click \"OK\" to proceed or \"Cancel\" to cancel transaction.";

	
	private static void print(String S)
		{ System.out.println(S); }
	
	public CyBroadcast(GooseDialog Dialog, Boss boss, Goose goose)
		{
		this.GDialog = Dialog;
		this.CyGoose = goose;
		this.GaggleBoss = boss;

		this.addButtonActions();
		}


	private String getTargetGoose()
		{
    int targetGooseIndex = this.GDialog.getGooseBox().getSelectedIndex();
		String targetGooseName = (String)this.GDialog.getGooseBox().getSelectedItem();
		print("Target index: "+targetGooseIndex+"  Target item: "+targetGooseName);
		return targetGooseName;
		}


	// very basically for the moment we will only broadcast by ID	
	public void broadcastNameList()
		{
		print("broadcastNameList");

    Set<CyNode> SelectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
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
			{ GaggleBoss.broadcast(this.CyGoose.getName(), this.getTargetGoose(), Species, NodeIds); }
		catch(Exception E) { E.printStackTrace(); }
		}

	// broadcasts hash of selected attributes	
	public void broadcastHashMap()
		{
		print("broadcastHashMap");

    Set<CyNode> SelectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

    // warning: no nodes are selected for broadcast
    if (SelectedNodes.size() == 0)
      {
      GagglePlugin.showDialogBox("No nodes selected for broadcast.", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
      }


    // pass string of attribute names
    String[] AllAttrNames = Cytoscape.getNodeAttributes().getAttributeNames();
    
    // confirmAttrSelection(AllAttrNames, "HashMap");
    AttrSelectAction okAction = new AttrSelectAction()
      {
        public void takeAction(String[] selectAttr)
          {
          broadcastHashMap(selectAttr);
          }
      };

    CyAttrDialog dialog = new CyAttrDialog(AllAttrNames, okAction, CyAttrDialog.MULTIPLE_SELECT);
    dialog.setDialogText(broadcastStr);
    dialog.preSelectCheckBox(AllAttrNames);
    dialog.buildDialogWin();
		}


  private void broadcastHashMap(String[] attrNames)
    {
    Set selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
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
	    { this.GaggleBoss.broadcast(this.CyGoose.getName(), this.getTargetGoose(), species, dataTitle, map); }
		catch (Exception E) { E.printStackTrace(); }
		}

	public void broadcastDataMatrix()
		{
		print("broadcastDataMatrix"); 

    Set SelectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

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

    // dialog for user to select attributes for broadcast
    AttrSelectAction okAction = new AttrSelectAction()
      {
        public void takeAction(String[] selectAttr)
          {
          broadcastDataMatrix(selectAttr);
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
	
  private void broadcastDataMatrix(String[] condNames)
    {
    Set selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
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
			{ this.GaggleBoss.broadcast(this.CyGoose.getName(), this.getTargetGoose(), matrix); }
		catch (Exception E) { E.printStackTrace(); }
		}

	public void broadcastNetwork()
		{
		print("broadcastNetwork");

    Iterator<CyNode> NodesIter = Cytoscape.getCurrentNetwork().getSelectedNodes().iterator();
  	Iterator<CyEdge> EdgesIter = Cytoscape.getCurrentNetwork().getSelectedEdges().iterator();
		CyAttributes EdgeAtts = Cytoscape.getEdgeAttributes();

    Network GaggleNetwork = new Network();
    String Species = "";
    while (EdgesIter.hasNext())
      {
      CyEdge CurrentSelectedEdge = EdgesIter.next();
      String SourceNodeId = CurrentSelectedEdge.getSource().getIdentifier();
      String TargetNodeId = CurrentSelectedEdge.getTarget().getIdentifier();

      String InteractionType = EdgeAtts.getStringAttribute(CurrentSelectedEdge.getIdentifier(), Semantics.INTERACTION);

      // create a new GaggleInteraction for broadcast
      org.systemsbiology.gaggle.network.Interaction GaggleInteraction = 
				new Interaction(SourceNodeId, TargetNodeId, InteractionType, CurrentSelectedEdge.isDirected());
      GaggleNetwork.add(GaggleInteraction);

      // again if there's more than one species we'll only get the last one!!!
      Species = this.getSpecies(SourceNodeId);
      }
    if (GaggleNetwork.edgeCount() <= 0)
    	{
    	GagglePlugin.showDialogBox("This network contains no interactions and will not be broadcast", "Warning", JOptionPane.WARNING_MESSAGE);
    	}
    else
    	{
			try 
				{ this.GaggleBoss.broadcast(this.CyGoose.getName(), this.getTargetGoose(), Species, GaggleNetwork); }
			catch (Exception E) { E.printStackTrace(); }
    	}
		}

	private String getSpecies(String NodeId)
		{
    // try the "species" attribute first; if not found, use DefaultSpeciesName
    String Species = "";

//    try
      {
      Species = Cytoscape.getNodeAttributes().getStringAttribute(NodeId, Semantics.SPECIES);

      if (Species == null) Species = CytoscapeInit.getProperties().getProperty("defaultSpeciesName");
      }
/*
    catch (Exception ex)
      {
      System.out.println("Exception encountered while fetching species name for node: " + nodeID);
      }
*/
    return Species;

		}	
	

	private void addButtonActions()
		{
    /* broadcast name list to other goose (geese) */
    GDialog.getListButton().addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
          {
          try
            {
            broadcastNameList();
            }
          catch (Exception ex)
            {
            ex.printStackTrace();
            }
          }
      });

    /* broadcast a network to other goose (geese) */
    GDialog.getNetButton().addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
          {
          try
            {
            broadcastNetwork();
            }
          catch (Exception ex)
            {
            ex.printStackTrace();
            }
          }
      });

    /* broadcast data matrix to other goose (geese) */
    GDialog.getMatrixButton().addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
          {
          try
            {
            broadcastDataMatrix();
            }
          catch (Exception ex)
            {
            ex.printStackTrace();
            }
          }
      });

    /* broadcast HashMap to other goose (geese) */
    GDialog.getMapButton().addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
          {
          try
            {
            broadcastHashMap();
            }
          catch (Exception ex)
            {
            ex.printStackTrace();
            }
					}
			});
		}

	}
