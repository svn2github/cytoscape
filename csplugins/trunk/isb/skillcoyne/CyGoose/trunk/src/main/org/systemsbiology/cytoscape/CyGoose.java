package org.systemsbiology.cytoscape;

import org.systemsbiology.cytoscape.dialog.GooseDialog;
import org.systemsbiology.cytoscape.dialog.GooseDialog.GooseButton;
import org.systemsbiology.cytoscape.task.HandleNetworkTask;

import org.systemsbiology.cytoscape.visual.SeedMappings;
import org.systemsbiology.cytoscape.script.*;
import org.systemsbiology.gaggle.core.Goose;
import org.systemsbiology.gaggle.core.Boss;
import org.systemsbiology.gaggle.core.datatypes.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;

import java.util.*;

import javax.swing.*;

import cytoscape.*;
import cytoscape.logger.CyLogger;
import static cytoscape.CytoscapeInit.getProperties;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualStyle;

import cytoscape.data.CyAttributes;

import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;

import giny.model.Node;
import giny.model.Edge;


/* TODO all cases need to handle the "null network" case by either doing the
 * action on all open networks or popping up a message that no network was selected (goose)
*/
/**
 * @author skillcoy <p/> This goose is going to make Cytoscape act like there's
 *         a goose for each network, so other networks outght to be able to
 *         direct broadcasts to specific networks rather than just the selected
 *         one.
 */
public class CyGoose implements Goose
	{
  private static CyLogger logger = CyLogger.getLogger(CyGoose.class);
  
  private static boolean debug = false;
	private static String debugTargetGoose;
	private List<GooseListChangedListener> gooseListChangedListeners = new ArrayList<GooseListChangedListener>();
	public enum Debug
		{
		// Track the data as I go through it and broadcast back
		ECHO_INSTANT("Echo-Instant", true),
		// Request the network and attributes back after I've set everything and
		// broadcast back
		ECHO_INDIRECT("Echo-Indirect", true),
		// No debug
		ECHO_OFF("Echo-Off", false);
		private String echo;

		// private boolean echoOn;
		private Debug(String arg, boolean echoOn)
			{
			echo = arg;
			CyGoose.debug = echoOn;
			}
		}
	private String[] activeGooseNames;
	private String gooseName;
	private String gooseNetId;
	private Boss gaggleBoss;
	private GooseDialog gDialog;
	private NodeAppearanceCalculator nac;
	private SeedMappings visualMap;
	private String broadcastId;
	private String targetGoose = "Boss";
  private String species;

  private static void print(String S)
		{
		System.out.println(S);
		}

	public CyGoose(GooseDialog GD)//, Boss boss)
		{
		gDialog = GD;
		// deals with everything but the broadcast actions
		addButtonActions();
    species = (String) getProperties().get("defaultSpeciesName");
    VisualStyle CurrentStyle = Cytoscape.getVisualMappingManager().getVisualStyle();
		nac = CurrentStyle.getNodeAppearanceCalculator();
		visualMap = new SeedMappings(nac);
		}
	
	public void setBoss(Boss boss) 
		{
		this.gaggleBoss = boss;
		}

	public void addGooseListChangedListener(GooseListChangedListener listener)
		{
		gooseListChangedListeners.add(listener);
		}

	// the boss does not call this method, all connections are handled by
	// GagglePlugin
	public void connectToGaggle() throws Exception
		{
		logger.info("Use GagglePlugin methods for connection");
		}

	// boss does not call this method
	public void doBroadcastList() throws RemoteException
		{
		logger.info("doBroadcastList() not implemented");
		}

	/**
	 * Destroy the network that is represented by this Goose or shut down
	 * Cytoscape if null network is chosen.
	 */
	public void doExit() throws RemoteException, UnmarshalException
		{
		if (Integer.valueOf(getNetworkId()).intValue() > 0)
			{
			logger.info("Destroying network " + gooseName);
			Cytoscape.destroyNetwork(Cytoscape.getNetwork(gooseNetId), false);
			}
		else
			{
			logger.info("Exiting Cytoscape...");
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

	/**
	 * shows Cytoscape
	 */
	public void doShow() throws RemoteException
		{
		Cytoscape.getDesktop().setAlwaysOnTop(true);
		Cytoscape.getDesktop().setVisible(true);
		if (!this.getNetworkId().equals("0")) Cytoscape.getDesktop().setFocus(
				getNetworkId());
		Cytoscape.getDesktop().setAlwaysOnTop(false);
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
   * @return Species name set for the goose
   */
  public String getSpeciesName()
    {
    return this.species;
    }

  public void setSpeciesName(String name)
    {
    this.species = name;
    this.gDialog.setSpeciesText(name);
    }

  /**
	 * @return Array of selected node ids
	 */
	public String[] getSelection() throws RemoteException
		{
		CyNetwork Net = Cytoscape.getNetwork(this.getNetworkId());
		String[] Selected = new String[Net.getSelectedNodes().size()];
		Iterator<CyNode> NodeIter = Net.getSelectedNodes().iterator();
		for (int i = 0; (i < Selected.length) && (NodeIter.hasNext()); i++)
			{
			CyNode CurrentNode = NodeIter.next();
			/* this could change based on
			 * the broadcastId but currently not implemented */
			Selected[i] = CurrentNode.getIdentifier(); 
			}
		return Selected;
		}

	/**
	 * @return Total number of selected nodes
	 */
	public int getSelectionCount() throws RemoteException
		{
		CyNetwork Net = Cytoscape.getNetwork(this.getNetworkId());
		return Net.getSelectedNodes().size();
		}

	/* this is how the 1.1 goose handles clusters, should be updated? 
	 * TODO: select all nodes that match geneNames with attributes in 
	 * conditionNames of species specified should be selected 
	 */
	public void handleCluster(String source, Cluster cluster)
			throws RemoteException
		{
		Namelist namelist = new Namelist();
		namelist.setNames(cluster.getRowNames());
		namelist.setSpecies(cluster.getSpecies());
		namelist.setName(cluster.getName());
		namelist.setMetadata(cluster.getMetadata());
		this.handleNameList(getName(), namelist);
		}

	// is this even used anymore?
	public void update(String[] activeGooseNames) throws RemoteException
		{
		this.activeGooseNames = activeGooseNames;
		fireGooseListChangedEvent();
		}

	private synchronized void fireGooseListChangedEvent()
		{
		for (GooseListChangedListener listener : gooseListChangedListeners)
			{
			try
				{
				listener.gooseListChanged(activeGooseNames);
				}
			catch (Exception e)
				{
				e.printStackTrace(); // listener may have gone away
				}
			}
		}

	public String[] getActiveGooseNames()
		{
		return activeGooseNames;
		}

	public void handleTuple(String string, GaggleTuple gaggleTuple)
			throws RemoteException
		{
		String condition = (String) gaggleTuple.getMetadata().getSingleAt(0).getValue();
		gDialog.displayDataType(condition);
		CyNetwork net = Cytoscape.getNetwork(this.getNetworkId());
		Cytoscape.getDesktop().setFocus(net.getIdentifier());
		Cytoscape.getDesktop().toFront();
		// if a user has anything previously selected it can obscure changes the
		// movie makes
		net.unselectAllNodes();
		net.unselectAllEdges();
		double upperValue = 0;
		double lowerValue = 0;
		boolean seedableValue = false;
		for (int i = 0; i < gaggleTuple.getData().getSingleList().size(); i++)
			{
			Tuple tuple = (Tuple) gaggleTuple.getData().getSingleAt(i).getValue();
			String Id = (String) tuple.getSingleAt(0).getValue();
			String attribute = (String) tuple.getSingleAt(1).getValue();
			Object valueObject = tuple.getSingleAt(2).getValue();
			
			CyNode selectNode = Cytoscape.getCyNode(Id);
			CyEdge selectEdge = null;
			for (Object obj: Cytoscape.getCyEdgesList()) 
				{
				CyEdge edge = (CyEdge)obj;
				if (edge.getIdentifier().equals(Id))
					{
					selectEdge = edge;
					break;
					}
				}
			
			CyAttributes nodeAtts = Cytoscape.getNodeAttributes(); 
			CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();
			/* does this need to be in this scope? */
			logger.info("Tuple value type: " + valueObject.getClass().getName() + " " + valueObject);
			
			if (valueObject instanceof Double)
				{
				Double value = (Double) valueObject;
				if (Double.isInfinite(value))
					value = 0.0;

				if (selectNode != null) 
					{
					nodeAtts.setAttribute(selectNode.getIdentifier(), attribute, value);
					}
				if (selectEdge != null)
					edgeAtts.setAttribute(selectEdge.getIdentifier(), attribute, value);
				
				if (i == 0)
					{
					upperValue = value;
					lowerValue = value;
					}
				else
					{
					if (value > upperValue) upperValue = value;
					if (value < lowerValue) lowerValue = value;
					}
				seedableValue = true;
				}
			else if (valueObject instanceof Integer)
				{
				Integer value = (Integer) valueObject;
				if (selectNode != null) 
					{
					nodeAtts.setAttribute(selectNode.getIdentifier(), attribute, value);
					}
				if (selectEdge != null)
					edgeAtts.setAttribute(selectEdge.getIdentifier(), attribute, value);
				if (i == 0)
					{
					upperValue = value;
					lowerValue = value;
					}
				else
					{
					if (value > upperValue) upperValue = value;
					if (value < lowerValue) lowerValue = value;
					}
				seedableValue = true;
				}
			else if (valueObject instanceof String)
				{
				String value = (String) valueObject;
				if (selectNode != null)
					nodeAtts.setAttribute(selectNode.getIdentifier(), attribute, value);
				if (selectEdge != null)
					edgeAtts.setAttribute(selectEdge.getIdentifier(), attribute, value);
				seedableValue = false;
				}
			else
				{
				throw new RuntimeException("Got a movie frame of the wrong type!");
				}
			
			if (seedableValue) // don't try it if the value wasn't a number!
				{
				upperValue = upperValue + (upperValue * 0.2);
				lowerValue = lowerValue - (lowerValue * 0.2);
				}
			visualMap.seedMappings(attribute, upperValue, lowerValue);
			}
    if (gaggleTuple.getSpecies() != null)
      this.setSpeciesName(gaggleTuple.getSpecies());

    Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		Cytoscape.getNetworkView(net.getIdentifier()).redrawGraph(true, true);
		}


	/**
	 * @param source
	 * @param matrix
	 *          Adds all attributes given in the matix to all matching nodes.
	 */
	public void handleMatrix(String source, DataMatrix matrix)
			throws RemoteException
		{
		logger.info("***** handleMatrix(DataMatrix) ****** ");
		CyNetwork Net = Cytoscape.getNetwork(this.getNetworkId());
		Cytoscape.getDesktop().setFocus(Net.getIdentifier());
		String[] GeneNames = matrix.getRowTitles();
		String[] ConditionNames = matrix.getColumnTitles();
		List<CyEdge> EdgeList = Cytoscape.getCyEdgesList();

		for (int row = 0; row < GeneNames.length; row++)
			{
			String Id = GeneNames[row];
			CyNode SelectNode = Cytoscape.getCyNode(Id);
			CyEdge SelectEdge = null;
			for (CyEdge edge : EdgeList)
				{
				if (edge.getIdentifier().equals(Id))
					{
					SelectEdge = edge;
					break;
					}
				}
			CyAttributes NodeAtts = Cytoscape.getNodeAttributes();
			CyAttributes EdgeAtts = Cytoscape.getEdgeAttributes();
			if (SelectNode != null)
				{
				Net.setSelectedNodeState((Node) SelectNode, true);
				}
			if (SelectEdge != null)
				{
				Net.setSelectedEdgeState((Edge) SelectEdge, true);
				}
			// set all experimental conditions as node attributes
			for (int col = 0; col < ConditionNames.length; col++)
				{
				Double condVal = new Double(matrix.get(row, col));
				String attributeName = ConditionNames[col];
				if (SelectNode != null)
					{
					if ((NodeAtts.hasAttribute(SelectNode.getIdentifier(), attributeName))
							&& (NodeAtts.getType(attributeName) != CyAttributes.TYPE_FLOATING)) logger.info("handleMatrix() Warning: \""
							+ attributeName + "\" is not of TYPE_FLOATING");
					else NodeAtts.setAttribute(SelectNode.getIdentifier(), attributeName,
							condVal);
					}
				if (SelectEdge != null)
					{
					if ((EdgeAtts.hasAttribute(SelectEdge.getIdentifier(), attributeName))
							&& (EdgeAtts.getType(attributeName) != CyAttributes.TYPE_FLOATING)) logger.info("handleMatrix() Warning: \""
							+ attributeName + "\" is not of TYPE_FLOATING");
					else EdgeAtts.setAttribute(SelectEdge.getIdentifier(), attributeName,
							condVal);
					}
				}
			}

    if (matrix.getSpecies() != null)
      this.setSpeciesName(matrix.getSpecies());      

    Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		// refresh network to flag selected nodes
		Cytoscape.getDesktop().setFocus(Net.getIdentifier());
		}

	public void handleCommand(String cmd, String[] params)
		{
		if (cmd.equalsIgnoreCase(Command.HIDE.getCommand()))
			{
			logger.info("    * running command 'hideSelection()'");
			CommandHandler.hideSelection(this.getNetworkId());
			}
		else if (cmd.equalsIgnoreCase(Command.INVERT.getCommand()))
			{
			logger.info("    * running command 'invertSelection()'");
			CommandHandler.invertSelection(this.getNetworkId());
			}
		else if (cmd.equalsIgnoreCase(Command.CLEAR.getCommand()))
			{
			logger.info("    * running command 'clearSelection()'");
			CommandHandler.clearSelection(this.getNetworkId());
			}
		else
			{
			logger.info("ERROR: Unknown command '" + cmd + "'");
			}
		// refresh network to flag selected nodes
		Cytoscape.getDesktop().setFocus(this.getNetworkId());
		}

	/**
	 * @param source
	 * @param namelist
	 *          If sent to a network goose (not the default 'null' goose) all
	 *          matching nodes will be selected. Species is ignored.
	 */
	public void handleNameList(String source, Namelist namelist)
			throws RemoteException
		{
		logger.info("**** handleNameList(String, Namelist) *****");
    String[] names = namelist.getNames();
/*  Not going to deal with the hacky commands for now.  Paul Shannon requested but isn't using them.
		if (names[0].equalsIgnoreCase("cmd"))
			{
			handleCommand(names[1], null);
			}
		else
*/
			{
			if (this.getNetworkId() == null || this.getNetworkId().equals("0"))
				{
				if (Cytoscape.getNetworkSet().size() <= 0)
					{
				System.out.println("  --Null network");
				String title = namelist.getName();
				if (title == null)
					{
					title = namelist.getSpecies();
					}
				CyNetwork NewNet = Cytoscape.createNetwork(title, true);
				for (String CurrentName : names)
					{
					CyNode NewNode = Cytoscape.getCyNode(CurrentName, true);
					NewNet.addNode(NewNode.getRootGraphIndex());
					}
				Cytoscape.getNetworkView(NewNet.getIdentifier()).applyLayout(CyLayouts.getDefaultLayout());
				Cytoscape.getNetworkView(NewNet.getIdentifier()).redrawGraph(true, true);
					}
				else // handle on all networks
					{
					for (Object cyNetwork: Cytoscape.getNetworkSet())
						selectNodesEdges( (CyNetwork) cyNetwork, names );
					}
				}
			else selectNodesEdges(Cytoscape.getNetwork(this.getNetworkId()), names);

      if (namelist.getSpecies() != null)
        this.setSpeciesName(namelist.getSpecies());      
      }
		}

	private void selectNodesEdges(CyNetwork CyNet, String[] names)
		{
		for (String CurrentName : names)
			{
			CyNode SelectNode = Cytoscape.getCyNode(CurrentName);
			if (SelectNode != null)
				{
				CyNet.setSelectedNodeState(SelectNode, true);
				}
			/* this means either nodes or edges can match...there's no
			 * way to tell what a namelist holds however if the first
			 * one in the name list is not an edge it won't look again
			*/
			List<CyEdge> EdgesList = Cytoscape.getCyEdgesList();
			for (CyEdge edge : EdgesList)
				{
				if (edge.getIdentifier().equals(CurrentName))
					{
					CyNet.setSelectedEdgeState(edge, true);
					break;
					}
				}
			}
		logger.debug("number of matching nodes: " + CyNet.getSelectedNodes().size());
		logger.debug("number of matching edges: "+ CyNet.getSelectedEdges().size());
		if (CyNet.getSelectedNodes().size() <= 0 && CyNet.getSelectedEdges().size() <= 0)
			{
			String msg = "No matching nodes/edges were found, please check that you are using the same ID's between geese";
			logger.warn(msg);
      GagglePlugin.showDialogBox(msg, "Warning", JOptionPane.WARNING_MESSAGE);
      }

		// refresh network to flag selected nodes
		Cytoscape.getDesktop().setFocus(CyNet.getIdentifier());
		}
	
	/**
	 * @param source
	 * @param gNetwork
	 *          If this is broadcast to the 'null' goose a network is created in
	 *          Cytoscape. If this is broadcast to a network goose interactions
	 *          are added to the network and all added interactions and matching
	 *          interactions are selected.
	 */
	public void handleNetwork(String source, Network gNetwork)
			throws RemoteException
		{
		logger.debug("handleNetwork(String, Network, CyNetwork)");
		logger.debug("network name: " + gNetwork.getName());
		logger.debug("broadcast source: " + source);
    logger.debug("species: " + gNetwork.getSpecies());
    // create a network if none exists
		// network with ID=0 is the nullNetwork
		String NetworkId = null;
		if (this.getNetworkId() == null || this.getNetworkId().equals("0"))
			{
			logger.debug("  --Null network");
			String title = source + " goose";
			
			if ( gNetwork.getSpecies() != null || !gNetwork.getSpecies().equalsIgnoreCase("unknown") ) 
				title = gNetwork.getSpecies();

      title = (gNetwork.getName() != null)? title + " " + gNetwork.getName(): source + " " + title;
			
			CyNetwork NewNet = Cytoscape.createNetwork(title, false);
			HandleNetworkTask.createHandleNetworkTask(source, gNetwork, NewNet, true);

			// basic layout
			CyLayoutAlgorithm Layout = CyLayouts.getDefaultLayout();
			String LayoutName = (String) gDialog.getLayoutChooser().getSelectedItem();
			if (!LayoutName.equalsIgnoreCase("default")) Layout = CyLayouts.getLayout(LayoutName);
			Cytoscape.createNetworkView(NewNet, NewNet.getTitle(), Layout);
			NetworkId = NewNet.getIdentifier();

			NewNet.unselectAllEdges();
			NewNet.unselectAllNodes();
			}
		else
			{
			logger.debug("  --Network " + this.getNetworkId());
			HandleNetworkTask.createHandleNetworkTask(source, gNetwork, Cytoscape.getNetwork(this.getNetworkId()), false);
			NetworkId = getNetworkId();
			}

    if (gNetwork.getSpecies() != null)
      this.setSpeciesName(gNetwork.getSpecies());      

    // refresh network to flag selected nodes
		Cytoscape.getDesktop().setFocus(NetworkId);
		}

	// no point in this one
	public void setGeometry(int x, int y, int width, int height)
			throws RemoteException
		{
		logger.info("setGeometry() not implemented");
		}

	// Used to set the goose network id to the cynetwork id
	public void setNetworkId(String Id)
		{
		gooseNetId = Id;
		}

	// sets the name goose is identified by in the boos
	public void setName(String newName)
		{
		gooseName = newName;
		}

	// I think this is used to choose the identifier to broadcast/handle nodes
	// by, currently not used
	public void setBroadcastId()
		{
		broadcastId = "ID";
		}

	private void addButtonActions()
		{
		// set attribute to broadcast to other geese as the ID
		/*
		 * gDialog.getIdButton().addActionListener(new ActionListener() { public
		 * void actionPerformed(ActionEvent event) { setBroadcastId(); } });
		 */
		// listen in on the getGooseBox()
		gDialog.getGooseChooser().addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
					{
					JComboBox tempCombo = (JComboBox) event.getSource();
					targetGoose = (String) tempCombo.getSelectedItem();
					}
			});
		// show selected goose
		gDialog.addButtonAction(GooseButton.SHOW, new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
					{
					try
						{
						gaggleBoss.show(targetGoose);
						}
					catch (Exception ex)
						{
            logger.error(ex.getMessage(), ex);
            }
					}
			});
		// hide selected goose
		gDialog.addButtonAction(GooseButton.HIDE, new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
					{
					try
						{
						gaggleBoss.hide(targetGoose);
						}
					catch (Exception ex)
						{
            logger.error(ex.getMessage(), ex);
						}
					}
			});
		}
	}
