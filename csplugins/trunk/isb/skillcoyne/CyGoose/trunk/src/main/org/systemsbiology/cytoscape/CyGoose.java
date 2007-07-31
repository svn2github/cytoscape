package org.systemsbiology.cytoscape;

//import org.systemsbiology.cytoscape.GagglePlugin;
//import org.systemsbiology.cytoscape.CyBroadcast;
import org.systemsbiology.cytoscape.dialog.GooseDialog;
import org.systemsbiology.cytoscape.visual.SeedMappings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;

import java.util.*;

import javax.swing.JComboBox;
//import javax.swing.JOptionPane;

import org.systemsbiology.gaggle.experiment.datamatrix.DataMatrix;
import org.systemsbiology.gaggle.network.*;
import org.systemsbiology.gaggle.boss.Boss;
import org.systemsbiology.gaggle.geese.Goose;

import cytoscape.*;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualStyle;
import cytoscape.data.Semantics;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayouts;


import giny.model.Node;
import giny.model.Edge;


/**
 * @author skillcoy
 * 
 * This goose is going to make Cytoscape act like there's a goose for each
 * network, so other networks outght to be able to direct broadcasts to specific
 * networks rather than just the selected one.
 * 
 */
public class CyGoose implements Goose
	{
	private String gooseName;
	private String gooseNetId;	

	private Boss gaggleBoss;
	private GooseDialog gDialog;
	
	private NodeAppearanceCalculator nac; 
	private SeedMappings visualMap;
	
	private String broadcastId;
	private String targetGoose = "Boss";
	
	private static void print(String S)
		{ System.out.println(S); }

	public CyGoose(GooseDialog GD, Boss boss)
		{
		gaggleBoss = boss;
		gDialog = GD;
		
		// deals with evertying but the broadcast actions
		addButtonActions();
		
		VisualStyle CurrentStyle = Cytoscape.getVisualMappingManager().getVisualStyle();
		nac = CurrentStyle.getNodeAppearanceCalculator();
		visualMap = new SeedMappings(nac);
		}

	// Deselect all nodes/edges.  
	// TODO: There is no button for this on the gaggle toolbar for cytoscape 
  public void clearSelections() throws RemoteException
		{
		CyNetwork Net = Cytoscape.getNetwork( this.getNetworkId() );
		Net.unselectAllNodes();
		Net.unselectAllEdges();
		}

	// the boss does not call this method, all connections are handled by GagglePlugin
	public void connectToGaggle() throws Exception
		{ print("Use GagglePlugin methods for connection"); }

	// boss does not call this method
	public void doBroadcastList() throws RemoteException
		{ print("doBroadcastList() not implemented"); }

	/**
	 * Destroy the network that is represented by this Goose or shut down Cytoscape
	 * if null network is chosen.
	 */
	public void doExit() throws RemoteException, UnmarshalException
		{
		if (Integer.valueOf(getNetworkId()).intValue() > 0)
			{
			print("Destroying network " + gooseName);
			Cytoscape.destroyNetwork(Cytoscape.getNetwork(gooseNetId), false);
			}
		else
			{
			print("Exiting Cytoscape...");
			Cytoscape.exit(0);
			}
		}

	/**
	 * Hide Cytoscape
	 */
	public void doHide() throws RemoteException
		{ 
		Cytoscape.getDesktop().setVisible(false); 
		}

	// shows goose
	public void doShow() throws RemoteException
		{
		Cytoscape.getDesktop().setVisible(true);
		Cytoscape.getDesktop().toFront();
		if (!this.getNetworkId().equals("0")) 
			{
			Cytoscape.getDesktop().setFocus(getNetworkId());
			}
		}

	/**
	 * @return Name of the goose
	 */
	public String getName() 
		{ 
		return gooseName; 
		}

	/**
	 * @return Network id of goose
	 */
	public String getNetworkId()
		{ 
		return gooseNetId; 
		}

	/**
	 * @return Array of selected node ids
	 */
	public String[] getSelection() throws RemoteException
		{
		CyNetwork Net = Cytoscape.getNetwork( this.getNetworkId() );
		
		String[] Selected = new String[ Net.getSelectedNodes().size() ];
		Iterator<CyNode> NodeIter = Net.getSelectedNodes().iterator();
		for(int i=0; (i<Selected.length) && (NodeIter.hasNext()); i++)
			{
			CyNode CurrentNode = NodeIter.next();
			Selected[i] = CurrentNode.getIdentifier(); // this could change based on the broadcastId but currently not implemented
			}
		return Selected;
		}

	/**
	 * @return Total number of selected nodes
	 */
	public int getSelectionCount() throws RemoteException
		{
		CyNetwork Net = Cytoscape.getNetwork( this.getNetworkId() );		
		return Net.getSelectedNodes().size();
		}

	// this is how the 1.1 goose handles clusters, should be updated?
 	// TODO: select all nodes that match geneNames with attributes in conditionNames of species specified should be selected
	public void handleCluster(String species, String clusterName,
			String[] rowNames, String[] columnNames) throws RemoteException
		{ this.handleNameList(species, rowNames); }

	// adds attributes to an existing network 
	// this method is called for the "movies" from the DMV
	// TODO: check that the attribute being used is part of a NodeAppearanceCalculator
	// TODO: setup some default calculators for color/shape/size in cases where an attribute is not matched so a movie
	// will do something regardless
	/**
	 * @param species
	 * @param dataTitle
	 * @param hashMap
	 * 
	 * Takes the attributes from the hashMap and adds them to the network goose
	 * and displayes the dataTitle in the message area on the CyGoose tab.
	 */
	public void handleMap(String species, String dataTitle, HashMap hashMap)
			throws RemoteException
		{
		gDialog.getMessageArea().setText(dataTitle);

		HashMap<String,ArrayList> AttrMap = hashMap;
		print("********handleMap(String, String, HashMap) \"dataTitle\"***********");
		CyNetwork Net = Cytoscape.getNetwork( this.getNetworkId() );
 		Cytoscape.getDesktop().setFocus(Net.getIdentifier());
 		Cytoscape.getDesktop().toFront();
 		
 		// if a user has anything previously selected it can obscure changes the movie makes
 		Net.unselectAllNodes();
 		Net.unselectAllEdges();
 		
    double UpperValue = 0;
    double LowerValue = 0;

		// iterate over the attribute hash, key=attribute name, value= attribute values ArrayList
    Iterator<String> attrKeyIter = AttrMap.keySet().iterator();
    while (attrKeyIter.hasNext())
      {
      String attrName = attrKeyIter.next();
      
      ArrayList AttrVals = (ArrayList) AttrMap.get(attrName);

      // check the array contains other arrays as expected
      // elements of ArrayLists: [array_of_node_names, array_of_values]
      String[] NodeIds = (String[]) AttrVals.get(0);

      Object nodeVals = AttrVals.get(1);
      Class nodeValsClass = nodeVals.getClass();
      if (!nodeValsClass.isArray())
        {
        System.err.println(this + ".handleMap() error: expecting an array of values!");
        return;
        }

      // determine the data type of attribute in hashMap (should be DOUBLE, STRING, BOOLEAN, or INT)
      String valType = nodeValsClass.getComponentType().getName();

      for (int i=0; i<NodeIds.length; i++)
        {
        CyNode selectNode = Cytoscape.getCyNode(NodeIds[i]);
        CyAttributes NodeAtts = Cytoscape.getNodeAttributes();

        // I can seed mappings currently only for DOUBLE's or INT's as these are continuous mappings
        if (selectNode != null)
          {
					NodeAtts.setAttribute(selectNode.getIdentifier(), Semantics.SPECIES, species);
					
					// set all attributes from the map
          if (valType.equals("double"))
            { // DOUBLE
            double[] Value = (double[]) AttrVals.get(1);
            NodeAtts.setAttribute(selectNode.getIdentifier(), attrName, new Double(Value[i]));
            // first node we'll just set the values for a base
            if (i == 0)
            	{
            	UpperValue = Value[i];
            	LowerValue = Value[i];
            	}
            else
            	{ 
            	if (Value[i] > UpperValue) UpperValue = Value[i];
            	if (Value[i] < LowerValue) LowerValue = Value[i];
            	}
            }
          else if (valType.equals("int"))
            { // INT
            int[] Value = (int[]) AttrVals.get(1);
            NodeAtts.setAttribute(selectNode.getIdentifier(), attrName, new Integer(Value[i]));
            if (i == 0)
            	{
            	UpperValue = Value[i];
            	LowerValue = Value[i];
            	}
            else
            	{ 
            	if (Value[i] > UpperValue) UpperValue = Value[i];
            	if (Value[i] < LowerValue) LowerValue = Value[i];
            	}
            }
          else if (valType.equals("boolean"))
            { // BOOLEAN
            boolean[] Value = (boolean[]) AttrVals.get(1);
            NodeAtts.setAttribute(selectNode.getIdentifier(), attrName, new Boolean(Value[i]));
            }
          else if (valType.equals("java.lang.String"))
            { // STRING
            String[] Value = (String[]) AttrVals.get(1);
            NodeAtts.setAttribute(selectNode.getIdentifier(), attrName, Value[i]);
            }
          else
            {
            System.err.println(this + ".handleMap() error: incompatible attribute data type (" + valType + ")");
            return;
            }
          }
        }
      UpperValue = UpperValue + (UpperValue * 0.2);
      LowerValue = LowerValue - (LowerValue * 0.2);
      
      visualMap.seedMappings(attrName, UpperValue, LowerValue);
      }
    Cytoscape.getNetworkView(Net.getIdentifier()).redrawGraph(true, true);
		}

	/**
	 * @param matrix
	 * Adds all attributes given in the matix to all matching nodes.
	 */
	public void handleMatrix(DataMatrix matrix) throws RemoteException
		{
    print("***** handleMatrix(DataMatrix) ****** ");
		CyNetwork Net = Cytoscape.getNetwork( this.getNetworkId() );
    Cytoscape.getDesktop().setFocus(Net.getIdentifier());
    
		String[] GeneNames = matrix.getRowTitles();
    String[] ConditionNames = matrix.getColumnTitles();

    for (int row=0; row<GeneNames.length; row++)
      {
      String NodeId = GeneNames[row];
			
      CyNode SelectNode = Cytoscape.getCyNode(NodeId); 

      if (SelectNode != null)
        {
        CyAttributes NodeAtts = Cytoscape.getNodeAttributes();
				Net.setSelectedNodeState( (Node)SelectNode, true );

        // set all experimental conditions as node attributes
        for (int col=0; col<ConditionNames.length; col++)
          {
          Double condVal = new Double(matrix.get(row, col));
          String attributeName = ConditionNames[col];
          if ( ( NodeAtts.hasAttribute(SelectNode.getIdentifier(),  attributeName) ) &&
               ( NodeAtts.getType(attributeName) != CyAttributes.TYPE_FLOATING ) )
            print("handleMatrix() Warning: \"" + attributeName + "\" is not of TYPE_FLOATING");
          else NodeAtts.setAttribute(SelectNode.getIdentifier(), attributeName, condVal);
          }
        }
      }

    // refresh network to flag selected nodes
    Cytoscape.getDesktop().setFocus(Net.getIdentifier());
		}



	/**
	 * @param species
	 * @param names
	 * 
	 * If sent to a network goose (not the default 'null' goose) all matching nodes
	 * will be selected.  Species is ignored.
	 */
	public void handleNameList(String species, String[] names) throws RemoteException
		{
		print("**** handleNameList(String, String[]) *****");
		CyNetwork Net = Cytoscape.getNetwork( this.getNetworkId() );

		for ( String CurrentName: names )
			{
			CyNode SelectNode = Cytoscape.getCyNode(CurrentName);
			if ( (SelectNode != null) ) 
				{ Net.setSelectedNodeState( (Node)SelectNode, true ); }
 			}

		if (Net.getSelectedNodes().size() <= 0)
			{ 
			String Msg = "No matching nodes were found, if you think this is incorrect check that your nodes have the ." + 
										Semantics.SPECIES + " attribute set.";
			print(Msg);
			}

		// refresh network to flag selected nodes
		Cytoscape.getDesktop().setFocus(Net.getIdentifier());
		}



	/**
	 * @param species
	 * @param network
	 * 
	 * If this is broadcast to the 'null' goose a network is created in Cytoscape.
	 * If this is broadcast to a network goose interactions are added to the network
	 * and all added interactions and matching interactions are selected.
	 */
	public void handleNetwork(String species, Network gNetwork) throws RemoteException
		{
    print("handleNetwork(String, Network, CyNetwork)");
    // create a network if none exists
    // network with ID=0 is the nullNetwork
    String NetworkId = null;
    if ( this.getNetworkId() == null || this.getNetworkId().equals("0") ) 
    	{ 
    	System.out.println("  --Null network");
    	CyNetwork NewNet = Cytoscape.createNetwork("Gaggle "+species, false);
    	handleNetwork(species, gNetwork, NewNet, false); 

    	// basic layout 
    	Cytoscape.createNetworkView(NewNet, NewNet.getTitle(), CyLayouts.getDefaultLayout());
    	
    	NetworkId = NewNet.getIdentifier();
    	}
		else 
			{ 
			System.out.println("  --Network " + this.getNetworkId());
			handleNetwork(species, gNetwork, Cytoscape.getNetwork(this.getNetworkId()), true); 
			NetworkId = getNetworkId();
			}
    // refresh network to flag selected nodes
		Cytoscape.getDesktop().setFocus(NetworkId);
		}


	/**
	 * 
	 * @param species
	 * @param GaggleNet
	 * @param CyNet
	 * @param SelectNodes
	 * @throws RemoteException
	 * 
	 * Create a network from the gaggle network either de novo (if null network is handling)
	 * or add to the network goose and select added nodes.
	 */
	public void handleNetwork(String species, Network GaggleNet, CyNetwork CyNet, boolean SelectNodes) 
		throws RemoteException
		{
    Collection<Node> srcCollection = new ArrayList<Node>();
    Collection<Node> targetCollection = new ArrayList<Node>();
    Collection<Edge> edgeCollection = new ArrayList<Edge>();
    
    for (String NodeName: GaggleNet.getNodes())
    	{
    	Node NewNode = (Node) Cytoscape.getCyNode(NodeName, true);
    	CyNet.addNode(NewNode);
    	CyNet.setSelectedNodeState(NewNode, SelectNodes);
    	}
    addAttributes(GaggleNet, NetworkObject.NODE);

    
    for (Interaction CurrentInteraction: GaggleNet.getInteractions())
      {
      //Interaction CurrentInteraction = GaggleInteractions[i];
      String srcNodeName = CurrentInteraction.getSource();
      String targetNodeName = CurrentInteraction.getTarget();
      String interactionType = CurrentInteraction.getType();

      // flag source node (create new node if it doesn't exist)
      Node srcNode = (Node) Cytoscape.getCyNode(srcNodeName, true);
      CyNet.addNode(srcNode);
      srcCollection.add(srcNode);

      // flag target node (create new node if it doesn't exist)
      Node targetNode = (Node) Cytoscape.getCyNode(targetNodeName, true);
      CyNet.addNode(targetNode);
      targetCollection.add(targetNode);

      // flag edge (create a new edge if it's not found)
      Edge selectEdge = (Edge) Cytoscape.getCyEdge(srcNode, targetNode, Semantics.INTERACTION, interactionType, true);
      // add newly created edge to current network
      if (!CyNet.containsEdge(selectEdge)) CyNet.addEdge(selectEdge);
      edgeCollection.add(selectEdge);
      }
    addAttributes(GaggleNet, NetworkObject.EDGE);

    // flag all selected nodes & edges
		if (SelectNodes)
			{
			CyNet.setSelectedNodeState(srcCollection, true);
			CyNet.setSelectedNodeState(targetCollection, true);
			CyNet.setSelectedEdgeState(edgeCollection, true);
			}

		}

	// TODO handle both node and edge atts from gaggle network
	private void addAttributes(Network gNet, NetworkObject obj)
		{
		System.out.println("Adding attributes");
    switch (obj)
	    {
	    case NODE:
			System.out.println("Adding NODE attributes");
		    for (String att: gNet.getNodeAttributeNames())
					{
					HashMap<String, Object> Attributes = gNet.getNodeAttributes(att);
					for (String nodeName : Attributes.keySet())
						setAttribute(Cytoscape.getNodeAttributes(), nodeName, att, Attributes.get(nodeName));
					}
			  break;

	    case EDGE:
			System.out.println("Adding EDGE attributes");
		    for (String att: gNet.getEdgeAttributeNames())
					{
					HashMap<String, Object> Attributes = gNet.getEdgeAttributes(att);
					for (String edgeName : Attributes.keySet())
						setAttribute(Cytoscape.getEdgeAttributes(), edgeName, att, Attributes.get(edgeName));
					}
	    	break;
	    };
	  Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		}

	
	private void setAttribute(CyAttributes cyAtts, String networkObjId, String attributeName, Object attributeValue)
		{
		//System.out.println("Setting attribute name '" + attributeName + "' to '" + attributeValue + "' on network object '" + networkObjId + "'");
		if (attributeValue.getClass().equals(java.lang.String.class))
			cyAtts.setAttribute(networkObjId, attributeName, (String)attributeValue);

		else if (attributeValue.getClass().equals(Integer.class))
			cyAtts.setAttribute(networkObjId, attributeName, (Integer)attributeValue);

		else if (attributeValue.getClass().equals(Double.class))
			cyAtts.setAttribute(networkObjId, attributeName, (Double)attributeValue);
		}
	
	
	
	// no point in this one
	public void setGeometry(int x, int y, int width, int height) throws RemoteException
		{ print("setGeometry() not implemented"); }

	// Used to set the goose network id to the cynetwork id
	public void setNetworkId(String Id) 
		{ gooseNetId = Id; }
	
	// sets the name goose is identified by in the boos
	public void setName(String newName) //throws RemoteException
		{ gooseName = newName; }

	// I think this is used to choose the identifier to broadcast/handle nodes by, currently not used
	public void setBroadcastId()
		{ broadcastId = "ID"; }

	private void addButtonActions()
		{ 
		// set attribute to broadcast to other geese as the ID 
		/*
		gDialog.getIdButton().addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
					{ setBroadcastId(); }
			});
		*/

		// listen in on the getGooseBox() 
		gDialog.getGooseBox().addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
					{
					JComboBox tempCombo = (JComboBox) event.getSource();
					targetGoose = (String) tempCombo.getSelectedItem();
					}
			});

    // show selected goose 
    gDialog.getShowButton().addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
          {
          try
            { gaggleBoss.show(targetGoose); }
          catch (Exception ex)
            { ex.printStackTrace(); }
          }
      });


		// hide selected goose 
		gDialog.getHideButton().addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
					{
					try
						{ gaggleBoss.hide(targetGoose); }
					catch (Exception ex)
						{ ex.printStackTrace(); }
					}
			});
		}

	}
