package csplugins.ucsd.rmkelley.PathBlastBrowser;
import csplugins.ucsd.rmkelley.PathBlastBrowser.Layout.DualLayoutTask;
import csplugins.ucsd.rmkelley.PathBlastBrowser.Layout.DualLayoutCommandLineParser;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.io.File;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.Edge;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.model.RootGraphChangeListener;
import giny.model.RootGraphChangeEvent;

import cytoscape.AbstractPlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.data.CyNetworkFactory;
import cytoscape.data.readers.GMLReader;
import cytoscape.view.CyWindow;
import cytoscape.util.GinyFactory;
import cytoscape.actions.FitContentAction;
import cytoscape.data.Semantics;


/**
 * This plugin will display is used to browser a number of gml files created
 * by DualLayout.
 */
public class PathBlastBrowser extends AbstractPlugin{

  CyWindow cyWindow;
  /**
   * Print out debugging information
   */
  public static boolean DEBUG = true;

  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
  public PathBlastBrowser(CyWindow cyWindow) {
    this.cyWindow = cyWindow;
    cyWindow.getCyMenus().getOperationsMenu().add( new LoadPathBlastGMLAction( cyWindow ) );
  }

  /**
   * Gives a description of this plugin.
   */
  public String describe() {
    StringBuffer sb = new StringBuffer();
    sb.append("PathBlast Browser");
    return sb.toString();
  }
}

/**
 * This class gets attached to the menu item.
 */
class LoadPathBlastGMLAction extends AbstractAction {

  /**
   * This is the window we are operating on
   */
  CyWindow cyWindow;
  /**
   * The constructor sets the text that should appear on the menu item.
   */
  public LoadPathBlastGMLAction(CyWindow cyWindow){
    super("Load SIF Files");
    this.cyWindow = cyWindow;
  }


  /**
   * This method is called when the user selects the menu item.
   */
  public void actionPerformed(ActionEvent ae) {
    //inform listeners that we're doing an operation on the network
    Thread t = new LoadPathBlastGMLTask(cyWindow); 
    t.start();
  }
}
/**
 * Responsible for loading a set of gml files, and creating an overlap
 * graph out of those networks. Will also add a new option to the newly created
 * menu to display the subgraph located at each node
 */
class LoadPathBlastGMLTask extends Thread{
  CyWindow cyWindow;
  /**
   * The string use to split name is homology nodes
   */
  private static String SPLIT_STRING = "\\|";
  /**
   * Title for the file selection dialog
   */
  private static String TITLE = "Select all SIF files";
  /**
   * This was is used to specify the overlap required
   * for adding an edge ot the graph
   */
  private static int REQUIRED_OVERLAP = 1;
  /**
   * Teh attribute with which to associate data
   * that contains information about the number of 
   * overlapping nodes associated with this edge
   */
  private static String COUNT_ATTRIBUTE = "count";
  private static String SPECIES_NODES_ATTRIBUTE = "speciesNodes";
  private static String INTERACTION_ATTRIBUTE = "interaction";
  /**
   * The interaction string for our overlap edges
   */
  private static String OVERLAP_INTERACTION = "ov";
  private static String HOMOLOGY_INTERACTION = "hm";
  private HashMap node2CyNetwork;
  /**
   * Stores the cyWindow object for later reference
   */
  public LoadPathBlastGMLTask(CyWindow cyWindow){
    this.cyWindow = cyWindow;
  }

  /**
   * Starts thread execution
   */
  public void run(){
    JFileChooser chooser = new JFileChooser();
    chooser.setMultiSelectionEnabled(true);
    chooser.setDialogTitle(TITLE);
    int returnVal = chooser.showOpenDialog(cyWindow.getMainFrame());
    if(returnVal != JFileChooser.APPROVE_OPTION){
      if(PathBlastBrowser.DEBUG){
	System.out.println("File selection cancelled");
      }
      return;
    }

    //create a list of gmltrees, one for each file
    HashMap cyNetwork2Name  = createCyNetwork2Name(chooser.getSelectedFiles());

    //for each gmltree, create an associated hash of node identifier strings,
    //this hash will be used to determine the overlap between the networks
    //and eventually create the overlap graph
    HashMap cyNetwork2NameSet = createCyNetwork2NameSet(cyNetwork2Name.keySet());

    //create a root graph using the information about the file names
    //and the node names contained, as a side effect this also creates
    //a hash from node2GMLTree
    CyNetwork newNetwork = createOverlapGraph(cyNetwork2Name,cyNetwork2NameSet);	
    //set up the canonicalName mapping here
    //create a new window and add this network to that window
    CyWindow newWindow = new CyWindow(cyWindow.getCytoscapeObj(), newNetwork, "Overlap Graph");
    //create a new menu item and actionlistener for that window
    newWindow.getCyMenus().getOperationsMenu().add(new ShowGMLAction(newWindow,node2CyNetwork));	 	
    newWindow.showWindow();
    
  }

  
  /**
   * Using information about hte name of each gmltree and the node identifiers contained
   * in those GMLTrees, create an overlap graph. In this graph, the individual nodes represent
   * entire networks as defined in the gml files. There is an edge between two nodes if the two
   * gml files share any sort of overlap
   * @param name2GMLTree maps from a network name to the GMLTree associated with that name, used
   * to name the nodes in hte overlap graph.
   * @param GMLTree2NameSet maps from a GMLTree object to a set of strings which are the node indentifiers
   * in that GML file. This is used to determine overlap information between two networks.
   * @return a CyNetwork which is the overlap graph. For the attribute objects, this graph has canonical
   * names mapped for all of the object. The edges also have a count attribute which counts the number
   * of overlaps created from this graph
   */
  public CyNetwork createOverlapGraph(HashMap cyNetwork2Name, HashMap cyNetwork2NameSet){
    RootGraph rootGraph = GinyFactory.createRootGraph();
    GraphObjAttributes nodeAttributes = new GraphObjAttributes();
    GraphObjAttributes edgeAttributes = new GraphObjAttributes();
    //create the nodes for this graph, also create a mapping from GMLTree objects to nodes in the graph
    HashMap cyNetwork2Node = new HashMap();	
    node2CyNetwork = new HashMap();
    Iterator cyNetworkIt = cyNetwork2Name.keySet().iterator();
    while(cyNetworkIt.hasNext()){
      CyNetwork cyNetwork = (CyNetwork)cyNetworkIt.next();
      Node newNode = rootGraph.getNode(rootGraph.createNode());
      String name = (String)cyNetwork2Name.get(cyNetwork);
      newNode.setIdentifier(name);
      nodeAttributes.addNameMapping(name,newNode);
      cyNetwork2Node.put(cyNetwork,newNode);
      node2CyNetwork.put(newNode,cyNetwork);
      //add the attributes about the identity of the nodes in the subtree
      HashSet nameSet = (HashSet)cyNetwork2NameSet.get(cyNetwork);
      Iterator nameIt = nameSet.iterator();
      while(nameIt.hasNext()){
	String compatName = (String)nameIt.next();
	String [] speciesNames = compatName.split(SPLIT_STRING);
	for (int  idx = 0; idx < speciesNames.length; idx++) {
	  nodeAttributes.append(SPECIES_NODES_ATTRIBUTE+idx,name,speciesNames[idx]);
	} // end of for (int  = 0;  < ; ++)
      }
    }

    Vector cyNetworkVec = new Vector(cyNetwork2Name.keySet());
    for(int idx=0;idx<cyNetworkVec.size();idx++){
      for(int idy = idx+1;idy<cyNetworkVec.size();idy++){
	Set xSet = (Set)cyNetwork2NameSet.get(cyNetworkVec.get(idx));
	Set ySet = (Set)cyNetwork2NameSet.get(cyNetworkVec.get(idy));
	Iterator xIt = xSet.iterator();
	int count = 0;
	while(xIt.hasNext()){
	  if(ySet.contains(xIt.next())){
	    count++;
	  }
	}
	if(count >= REQUIRED_OVERLAP){
	  //create an edge between the two correpsonding nodes
	  Node sourceNode = (Node)cyNetwork2Node.get(cyNetworkVec.get(idx));
	  Node targetNode = (Node)cyNetwork2Node.get(cyNetworkVec.get(idy));
	  Edge newEdge = rootGraph.getEdge(rootGraph.createEdge(sourceNode,targetNode));
	  String name = nodeAttributes.getCanonicalName(sourceNode)+" ("+OVERLAP_INTERACTION+") "+nodeAttributes.getCanonicalName(targetNode);
	  newEdge.setIdentifier(name);
	  edgeAttributes.addNameMapping(name,newEdge);
	  edgeAttributes.set(COUNT_ATTRIBUTE,name,new Integer(count));
	  edgeAttributes.set(INTERACTION_ATTRIBUTE,name,OVERLAP_INTERACTION);	
	}
      }
    }
    return new CyNetwork(rootGraph,nodeAttributes,edgeAttributes);
  }
  
  
  /**
   * Take in a list of GMLTrees and creates a hashmap which maps from a GMLTree
   * to a set of the identifiers of all of the nodes in that GMLTree
   * @param GMLTrees a List of GMLTree objects
   * @return as described above
   */
  public HashMap createCyNetwork2NameSet(Collection cyNetworks){
    Iterator cyNetworkIt = cyNetworks.iterator();
    HashMap result = new HashMap();
    while(cyNetworkIt.hasNext()){
      CyNetwork currentCyNetwork = ((CyNetwork)cyNetworkIt.next());
      RootGraph rootGraph = currentCyNetwork.getRootGraph();
      HashSet compatLabels = new HashSet();
      GraphObjAttributes nodeAttributes = currentCyNetwork.getNodeAttributes();
      Iterator nodeIt = rootGraph.nodesList().iterator();
      while(nodeIt.hasNext()){
	String currentName = nodeAttributes.getCanonicalName(nodeIt.next());
	compatLabels.add(currentName);
      }
      result.put(currentCyNetwork,compatLabels);
    }
    return result;
  }
  /**
   * Reads in all of the specified gml files
   * @param gmlFiles an array of files which contain gml data
   * @return A HashMap which maps from a filename to the gmlTree which as created
   * using the data containted in that filename.
   */
  public HashMap createCyNetwork2Name(File [] sifFiles){
    HashMap result = new HashMap();
    for(int idx = 0;idx<sifFiles.length;idx++){
      File sifFile = sifFiles[idx];
      try{
	CyNetwork cyNetwork = CyNetworkFactory.createNetworkFromInteractionsFile(sifFile.getAbsolutePath());
	result.put(cyNetwork,sifFile.getName());
      }catch(Exception e){
	System.err.println("Failed to read the sif file specified by "+sifFile.getAbsolutePath());
      }

    }
    return result;
  }
}

/**			
 * Creates a new thread to load up the GML file in a separate window.
 */			
class ShowGMLAction extends AbstractAction {	
  /** 
   *This is the window containing the overlap graph
   */
  CyWindow cyWindow;
  /**			
   * This contains a mapping from nodes in the network to the GMLTree associated with that ndoe	
   */
  HashMap node2CyNetwork;
  /**					
   * The constructor sets the text that should appear on the menu item.
   */									
  public ShowGMLAction(CyWindow cyWindow, HashMap node2CyNetwork){	
    super("Show GML");							
    this.node2CyNetwork = node2CyNetwork;
    this.cyWindow = cyWindow;			
  }						
						
						
  /**						
   * This method is called when the user selects the menu item.
   */								
  public void actionPerformed(ActionEvent ae) {			
    List selectedNodes = cyWindow.getView().getSelectedNodes();	
    if(selectedNodes.size() > 0){				
      Node selectedNode = ((NodeView)selectedNodes.get(0)).getNode();
      Thread t = new ShowGMLThread(selectedNode,node2CyNetwork,cyWindow);
      t.start();								
    }										
  }										
}										
/**										
 * Load up the specified GMLFile in a window 					
 */										
class ShowGMLThread extends Thread{						
  /**										
   * The old window with the overlap graph, bascially just need this to geta  reference to the global
   * cytoscape thingy											
   */													
  private CyWindow overlapWindow;								
  /**													
   * The new winodw in which to display the gml,							
   * may have to create this										
   */													
  private static CyWindow newWindow;									
  /**													
   * The node that was selected in the graph								
   */													
  private Node selectedNode;											
  private HashMap node2CyNetwork;								
  /**													
   * Keeps track of the CyNetworks we have already 							
   * created so we don't have to create them again							
   */													
  public ShowGMLThread(Node selectedNode, HashMap node2CyNetwork, CyWindow overlapWindow){		
    this.node2CyNetwork = node2CyNetwork;								
    this.selectedNode = selectedNode;									
    this.overlapWindow = overlapWindow;									
  }													
													
  /**													
   * Make a new window and show the graph associated with thte node, or try				
   * to display in hte window that has already been created						
   */													
  public void run(){											
    synchronized (overlapWindow){									
      
      if (newWindow == null) {
	newWindow = new CyWindow(overlapWindow.getCytoscapeObj(),CyNetworkFactory.createEmptyNetwork(),"Split Graph");
	newWindow.showWindow();
      } // end of if ()
      DualLayoutCommandLineParser parser = new DualLayoutCommandLineParser(overlapWindow.getCytoscapeObj().getConfiguration().getArgs());
      CyNetwork cyNetwork = (CyNetwork)node2CyNetwork.get(selectedNode);
      Thread t = new DualLayoutTask(cyNetwork,newWindow,parser);
      
      t.start();
      try {
	t.join();
      } catch (Exception e) {
	System.err.println("Failed to rejoin thread, exiting");
	e.printStackTrace();
	return;
      } // end of try-catch
      Semantics.applyNamingServices(newWindow.getNetwork(),overlapWindow.getCytoscapeObj());
      newWindow.getMainFrame().setVisible(false);
      FitContentAction fitAction = new FitContentAction(newWindow);									
      fitAction.actionPerformed(new ActionEvent(this,0,""));										
      newWindow.getMainFrame().setVisible(true);											
    }																	
  }																	
 																	
}

