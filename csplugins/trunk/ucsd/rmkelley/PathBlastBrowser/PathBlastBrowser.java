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

import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.CyNetwork;
import cytoscape.data.CyNetworkFactory;
import cytoscape.data.readers.GMLReader;
import cytoscape.view.CyNetworkView;
import cytoscape.util.GinyFactory;
import cytoscape.actions.FitContentAction;
import cytoscape.data.Semantics;

//for saving the view to an image
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import phoebe.PGraphView;

/**
 * This plugin will display is used to browser a number of gml files created
 * by DualLayout.
 */
public class PathBlastBrowser extends CytoscapePlugin{

  /**
   * Print out debugging information
   */
  public static boolean DEBUG = true;

  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
  public PathBlastBrowser() {
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( new LoadPathBlastGMLAction( ) );
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
   * The constructor sets the text that should appear on the menu item.
   */
  public LoadPathBlastGMLAction(){
    super("Create overlap graph from SIF files");
  }


  /**
   * This method is called when the user selects the menu item.
   */
  public void actionPerformed(ActionEvent ae) {
    //inform listeners that we're doing an operation on the network
    Thread t = new LoadPathBlastGMLTask(); 
    t.start();
  }
}
/**
 * Responsible for loading a set of gml files, and creating an overlap
 * graph out of those networks. Will also add a new option to the newly created
 * menu to display the subgraph located at each node
 */
class LoadPathBlastGMLTask extends Thread{
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
  private static double REQUIRED_OVERLAP = 0;
  /**
   * Teh attribute with which to associate data
   * that contains information about the number of 
   * overlapping nodes associated with this edge
   */
  private static String COUNT_ATTRIBUTE = "intersection";
  private static String SPECIES_NODES_ATTRIBUTE = "speciesNodes";
  private static String INTERACTION_ATTRIBUTE = "interaction";
  /**
   * The interaction string for our overlap edges
   */
  private static String OVERLAP_INTERACTION = "ov";
  private static String HOMOLOGY_INTERACTION = "hm";
  private HashMap node2CyNetwork;
 

  /**
   * Starts thread execution
   */
  public void run(){
    JFileChooser chooser = new JFileChooser();
    chooser.setMultiSelectionEnabled(true);
    chooser.setDialogTitle(TITLE);
    int returnVal = chooser.showOpenDialog(Cytoscape.getDesktop());
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
    Cytoscape.createNetworkView(newNetwork);

    //create a new menu item and actionlistener for that window
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new ShowGMLAction(node2CyNetwork));	 	
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new SaveImagesAction(node2CyNetwork));
    //newWindow.showWindow();
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
    CyNetwork overlapNetwork = Cytoscape.createNetwork("Overlap Graph");
    
    //create the nodes for this graph, also create a mapping from GMLTree objects to nodes in the graph
    HashMap cyNetwork2Node = new HashMap();	
    node2CyNetwork = new HashMap();
    Iterator cyNetworkIt = cyNetwork2Name.keySet().iterator();
    while(cyNetworkIt.hasNext()){
      CyNetwork cyNetwork = (CyNetwork)cyNetworkIt.next();
      String name = (String)cyNetwork2Name.get(cyNetwork);
      CyNode newNode = overlapNetwork.addNode(Cytoscape.getCyNode(name,true));
      

      cyNetwork2Node.put(cyNetwork,newNode);
      node2CyNetwork.put(newNode,cyNetwork);
      //add the attributes about the identity of the nodes in the subtree
      HashSet nameSet = (HashSet)cyNetwork2NameSet.get(cyNetwork);
      Iterator nameIt = nameSet.iterator();
      while(nameIt.hasNext()){
	String compatName = (String)nameIt.next();
	String [] speciesNames = compatName.split(SPLIT_STRING);
	overlapNetwork.setNodeAttributeValue(newNode,SPECIES_NODES_ATTRIBUTE,Arrays.asList(speciesNames));
      }
    }

    Vector cyNetworkVec = new Vector(cyNetwork2Name.keySet());
    for(int idx=0;idx<cyNetworkVec.size();idx++){
      for(int idy = idx+1;idy<cyNetworkVec.size();idy++){
	Set xSet = (Set)cyNetwork2NameSet.get(cyNetworkVec.get(idx));
	Set ySet = (Set)cyNetwork2NameSet.get(cyNetworkVec.get(idy));
	Iterator xIt = xSet.iterator();
	int intersection = 0;
	while(xIt.hasNext()){
	  if(ySet.contains(xIt.next())){
	    intersection++;
	  }
	}
	int union = xSet.size()+ySet.size()-intersection;
	double percent = intersection/(double)union;
	
	if(percent > REQUIRED_OVERLAP){
	  //create an edge between the two correpsonding nodes
	  CyNode sourceNode = (CyNode)cyNetwork2Node.get(cyNetworkVec.get(idx));
	  CyNode targetNode = (CyNode)cyNetwork2Node.get(cyNetworkVec.get(idy));
	  String sourceName = (String)overlapNetwork.getNodeAttributeValue(sourceNode,Semantics.CANONICAL_NAME);
	  String targetName = (String)overlapNetwork.getNodeAttributeValue(targetNode,Semantics.CANONICAL_NAME);
	  String name = sourceName+ " ("+OVERLAP_INTERACTION+") "+targetName;
	  CyEdge newEdge = overlapNetwork.addEdge(Cytoscape.getCyEdge(sourceName,name,targetName,OVERLAP_INTERACTION));
	  overlapNetwork.setEdgeAttributeValue(newEdge,COUNT_ATTRIBUTE,new Double(percent));
	}
      }
    }
    overlapNetwork.setTitle("Overlap Graph");
    return overlapNetwork;
    
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
	//CyNetwork cyNetwork = CyNetworkFactory.createNetworkFromInteractionsFile(sifFile.getAbsolutePath());
	CyNetwork cyNetwork = Cytoscape.createNetwork(sifFile.getAbsolutePath(),Cytoscape.FILE_SIF,true,Cytoscape.getCytoscapeObj().getBioDataServer(),"");
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
   * This contains a mapping from nodes in the network to the GMLTree associated with that ndoe	
   */
  HashMap node2CyNetwork;
  /**					
   * The constructor sets the text that should appear on the menu item.
   */									
  public ShowGMLAction(HashMap node2CyNetwork){	
    super("Show GML");							
    this.node2CyNetwork = node2CyNetwork;
  }						
						
						
  /**						
   * This method is called when the user selects the menu item.
   */								
  public void actionPerformed(ActionEvent ae) {			
    List selectedNodes = Cytoscape.getCurrentNetworkView().getSelectedNodes();	
    if(selectedNodes.size() > 0){				
      CyNode selectedNode = (CyNode)((NodeView)selectedNodes.get(0)).getNode();
      Thread t = new ShowGMLThread(selectedNode,node2CyNetwork,false);
      t.start();								
    }										
  }										
}	

class SaveImagesAction extends AbstractAction {
  /**			
   * This contains a mapping from nodes in the network to the GMLTree associated with that ndoe	
   */
  HashMap node2CyNetwork;
  /**					
   * The constructor sets the text that should appear on the menu item.
   */									
  public SaveImagesAction(HashMap node2CyNetwork){	
    super("Save All Layouts to Images");							
    this.node2CyNetwork = node2CyNetwork;
  }						
    
  /**						
   * This method is called when the user selects the menu item.
   */								
  public void actionPerformed(ActionEvent ae) {			
    for ( Iterator nodeIt = Cytoscape.getCurrentNetwork().nodesIterator();nodeIt.hasNext();) {
      Thread t = new ShowGMLThread((CyNode)nodeIt.next(),node2CyNetwork,true);
      t.start();								
      try {
	t.join();
      } catch ( Exception e) {
	e.printStackTrace();
      } // end of try-catch
      
    }										
  }										
}	

/**										
 * Load up the specified GMLFile in a window 					
 */										
class ShowGMLThread extends Thread{	
  /**
   * Whether to save the layout to an image
   */
  private boolean save;
  /**													
   * The node that was selected in the graph								
   */													
  private CyNode selectedNode;											
  private HashMap node2CyNetwork;
  /**													
   * Keeps track of the CyNetworks we have already 							
   * created so we don't have to create them again							
   */													
  public ShowGMLThread(CyNode selectedNode, HashMap node2CyNetwork, boolean save){		
    this.node2CyNetwork = node2CyNetwork;								
    this.selectedNode = selectedNode;									
    this.save = save;
  }													
													
  /**													
   * Make a new window and show the graph associated with thte node, or try				
   * to display in hte window that has already been created						
   */													
  public void run(){											
    DualLayoutCommandLineParser parser = new DualLayoutCommandLineParser(Cytoscape.getCytoscapeObj().getConfiguration().getArgs());
    CyNetwork cyNetwork = (CyNetwork)node2CyNetwork.get(selectedNode);
    //CyNetworkView cyNetworkView = Cytoscape.createNetworkView(cyNetwork);
    String title = (String)Cytoscape.getCurrentNetwork().getNodeAttributeValue(selectedNode,Semantics.CANONICAL_NAME);
    Thread t = new DualLayoutTask(cyNetwork,parser,title+" - Split Graph");
    t.start();
    try {
      t.join();
    } catch (Exception e) {
      System.err.println("Failed to rejoin thread, exiting");
      e.printStackTrace();
      return;
    } // end of try-catch
      //Semantics.applyNamingServices(newWindow.getNetwork(),overlapWindow.getCytoscapeObj());
    
    FitContentAction fitAction = new FitContentAction();							   
    fitAction.actionPerformed(new ActionEvent(this,0,""));								   
    //newWindow.getVizMapManager().applyAppearances();
    if ( save) {
      try {
	ImageIO.write((BufferedImage)((PGraphView)(Cytoscape.getCurrentNetworkView())).getCanvas().getLayer().toImage(),"png",new File(selectedNode.getIdentifier()+".png"));  
      } catch ( Exception e) {
	e.printStackTrace();
      } // end of try-catch
    } // end of if ()
  }
}


