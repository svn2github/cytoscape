package ucsd.rmkelley.PathBlastBrowser;
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
import cytoscape.data.readers.GMLTree;
import cytoscape.view.CyWindow;
import cytoscape.util.GinyFactory;
import cytoscape.actions.FitContentAction;

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
																super("Load GML Files");
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
									* Title for the file selection dialog
									*/
								private static String TITLE = "Select all GML files";
								/**
									* This was is used to specify the overlap required
									* for adding an edge ot the graph
									*/
								private static int REQUIRED_OVERLAP = 2;
								/**
									* Teh attribute with which to associate data
									* that contains information about the number of 
									* overlapping nodes associated with this edge
									*/
								private static String COUNT_ATTRIBUTE = "count";
								private static String SPECIES1NODES_ATTRIBUTE = "speciesOneNodes";
								private static String SPECIES2NODES_ATTRIBUTE = "speciesTwoNodes";
								private static String INTERACTION_ATTRIBUTE = "interaction";
								/**
									* The interaction string for our overlap edges
									*/
								private static String OVERLAP_INTERACTION = "ov";
								private static String HOMOLOGY_INTERACTION = "hm";
								private HashMap node2GMLTree;
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
																HashMap GMLTree2Name = null; 
																try{
																								GMLTree2Name = createGMLTree2Name(chooser.getSelectedFiles());
																}catch(Exception e){
																								System.out.println("Failed to read in all GML files");
																								return;
																}


																//for each gmltree, create an associated hash of node identifier strings,
																//this hash will be used to determine the overlap between the networks
																//and eventually create the overlap graph
																HashMap GMLTree2NameSet = createGMLTree2NameSet(GMLTree2Name.keySet());

																//create a root graph using the information about the file names
																//and the node names contained, as a side effect this also creates
																//a hash from node2GMLTree 
																CyNetwork newNetwork = createOverlapGraph(GMLTree2Name,GMLTree2NameSet);	
																//set up the canonicalName mapping here
																//create a new window and add this network to that window
																CyWindow newWindow = new CyWindow(cyWindow.getCytoscapeObj(), newNetwork, "Overlap Graph");
																//create a new menu item and actionlistener for that window
																newWindow.getCyMenus().getOperationsMenu().add(new ShowGMLAction(newWindow,node2GMLTree));	 	
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
								public CyNetwork createOverlapGraph(HashMap GMLTree2Name, HashMap GMLTree2NameSet){
																RootGraph rootGraph = GinyFactory.createRootGraph();
																GraphObjAttributes nodeAttributes = new GraphObjAttributes();
																GraphObjAttributes edgeAttributes = new GraphObjAttributes();
																//create the nodes for this graph, also create a mapping from GMLTree objects to nodes in the graph
																HashMap GMLTree2Node = new HashMap();	
																node2GMLTree = new HashMap();
																Iterator GMLTreeIt = GMLTree2Name.keySet().iterator();
																while(GMLTreeIt.hasNext()){
																								GMLTree current = (GMLTree)GMLTreeIt.next();
																								Node newNode = rootGraph.getNode(rootGraph.createNode());
																								String name = (String)GMLTree2Name.get(current);
																								newNode.setIdentifier(name);
																								nodeAttributes.addNameMapping(name,newNode);
																								GMLTree2Node.put(current,newNode);
																								node2GMLTree.put(newNode,current);
																								//add the attributes about the identity of the nodes in the subtree
																								HashSet nameSet = (HashSet)GMLTree2NameSet.get(current);
																								Iterator nameIt = nameSet.iterator();
																								while(nameIt.hasNext()){
																												String compatName = (String)nameIt.next();
																												String [] speciesNames = split(compatName,"|");
																												nodeAttributes.append(SPECIES1NODES_ATTRIBUTE,name,speciesNames[0]);
																												nodeAttributes.append(SPECIES2NODES_ATTRIBUTE,name,speciesNames[1]);
																								}
																}

																Vector GMLTreeVec = new Vector(GMLTree2Name.keySet());
																for(int idx=0;idx<GMLTreeVec.size();idx++){
																								for(int idy = idx+1;idy<GMLTreeVec.size();idy++){
																																Set xSet = (Set)GMLTree2NameSet.get(GMLTreeVec.get(idx));
																																Set ySet = (Set)GMLTree2NameSet.get(GMLTreeVec.get(idy));
																																Iterator xIt = xSet.iterator();
																																int count = 0;
																																while(xIt.hasNext()){
																																								if(ySet.contains(xIt.next())){
																																																count++;
																																								}
																																}
																																if(count >= REQUIRED_OVERLAP){
																																				//create an edge between the two correpsonding nodes
																																				Node sourceNode = (Node)GMLTree2Node.get(GMLTreeVec.get(idx));
																																				Node targetNode = (Node)GMLTree2Node.get(GMLTreeVec.get(idy));
																																				Edge newEdge = rootGraph.getEdge(rootGraph.createEdge(sourceNode,targetNode));
																																				String name = nodeAttributes.getCanonicalName(sourceNode)+" ("+OVERLAP_INTERACTION+") "+nodeAttributes.getCanonicalName(targetNode);
																																				newEdge.setIdentifier(name);
																																				edgeAttributes.addNameMapping(name,newEdge);
																																				edgeAttributes.set(COUNT_ATTRIBUTE,name,(double)count);
																																				edgeAttributes.set(INTERACTION_ATTRIBUTE,name,OVERLAP_INTERACTION);	
																																}
																								}
																}
																return new CyNetwork(rootGraph,nodeAttributes,edgeAttributes);
								}
								/**
									* split the name of a node in the compatability graph into the names of
								 * the component nodes
									*/
								private String [] split(String s,String split){
												String [] result = new String [2];
												int index = s.indexOf(split);
												result[0] = s.substring(0,index);
												result[1] = s.substring(index + 1,s.length());
												return result;
								}

								/**
									* Take in a list of GMLTrees and creates a hashmap which maps from a GMLTree
									* to a set of the identifiers of all of the nodes in that GMLTree
									* @param GMLTrees a List of GMLTree objects
									* @return as described above
									*/
								public HashMap createGMLTree2NameSet(Collection GMLTrees){
																Iterator GMLTreeIt = GMLTrees.iterator();
																HashMap result = new HashMap();
																while(GMLTreeIt.hasNext()){
																								GMLTree current = ((GMLTree)GMLTreeIt.next());
																								HashSet compatLabels = new HashSet();
																								Vector nodeLabels = current.getVector("graph|node|label","|",GMLTree.STRING);
																								Vector nodeIds = current.getVector("graph|node|id","|",GMLTree.INTEGER);
																								HashMap id2Label = new HashMap();
																								for(int idx=0;idx<nodeLabels.size();idx++){
																												id2Label.put(nodeIds.get(idx),nodeLabels.get(idx));
																								}
																								Vector edgeLabels = current.getVector("graph|edge|label","|",GMLTree.STRING);
																								Vector edgeSources = current.getVector("graph|edge|source","|",GMLTree.INTEGER);
																								Vector edgeTargets = current.getVector("graph|edge|target","|",GMLTree.INTEGER);
																								for(int idx=0;idx<edgeLabels.size();idx++){
																												if(edgeLabels.get(idx).equals(HOMOLOGY_INTERACTION)){
																																String sourceLabel = (String)id2Label.get(edgeSources.get(idx));
																																String targetLabel = (String)id2Label.get(edgeTargets.get(idx));
																																compatLabels.add(sourceLabel+"|"+targetLabel);
																												}
																								}
																								result.put(current,compatLabels);
																}
																return result;
								}
								/**
									* Reads in all of the specified gml files
									* @param gmlFiles an array of files which contain gml data
									* @return A HashMap which maps from a filename to the gmlTree which as created
									* using the data containted in that filename.
									*/
								public HashMap createGMLTree2Name(File [] gmlFiles){
																HashMap result = new HashMap();
																for(int idx = 0;idx<gmlFiles.length;idx++){
																								File gmlFile = gmlFiles[idx];
																								result.put(new GMLTree(gmlFile.getAbsolutePath()),gmlFile.getName());
																}
																return result;
								}
}

/**
	* Creates a new thread to load up the GML file in a separate window.
	*/
class ShowGMLAction extends AbstractAction {

								/**
									* This is the window containing the overlap graph
									*/
								CyWindow cyWindow;
								/**
									* This contains a mapping from nodes in the network to the GMLTree associated with that ndoe
									*/
								HashMap node2GMLTree;
								/**
									* The constructor sets the text that should appear on the menu item.
									*/
								public ShowGMLAction(CyWindow cyWindow, HashMap node2GMLTree){
																super("Show GML");
																this.node2GMLTree = node2GMLTree;
																this.cyWindow = cyWindow;
								}


								/**
									* This method is called when the user selects the menu item.
									*/
								public void actionPerformed(ActionEvent ae) {
																List selectedNodes = cyWindow.getView().getSelectedNodes();
																if(selectedNodes.size() > 0){
																								Node selectedNode = ((NodeView)selectedNodes.get(0)).getNode();
																								Thread t = new ShowGMLThread(selectedNode,node2GMLTree,cyWindow);
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
								private static CyWindow overlapWindow;
								/**
									* The new winodw in which to display the gml,
									* may have to create this
									*/
								private static CyWindow newWindow;
								/**
									* The node that was selected in the graph
									*/
								Node selectedNode;
								private static HashMap node2GMLTree;
								/**
									* Keeps track of the CyNetworks we have already 
									* created so we don't have to create them again
									*/
								private static HashMap node2CyNetwork;
								public ShowGMLThread(Node selectedNode, HashMap node2GMLTree, CyWindow overlapWindow){
																this.node2GMLTree = node2GMLTree;
																this.selectedNode = selectedNode;
																this.overlapWindow = overlapWindow;
																if(node2CyNetwork == null){
																								node2CyNetwork = new HashMap();
																}
								}

								/**
									* Make a new window and show the graph associated with thte node, or try
									* to display in hte window that has already been created
									*/
								public void run(){
																synchronized (node2CyNetwork){
																								CyNetwork cyNetwork = (CyNetwork)node2CyNetwork.get(selectedNode);
																								if(cyNetwork == null){
																																cyNetwork = createCyNetwork();
																																node2CyNetwork.put(selectedNode,cyNetwork);
																								}
																								if(newWindow == null){
																																newWindow = new CyWindow(overlapWindow.getCytoscapeObj(),cyNetwork,"Split Graph");
																																newWindow.showWindow();
																								}
																								else{
																																newWindow.setNewNetwork(cyNetwork);
																								}
																								
																								newWindow.getMainFrame().setVisible(false);
																							 
																								//copy over node attributes, but keep our name mapping	
																								cyNetwork.getNodeAttributes().addClassMap(overlapWindow.getNetwork().getNodeAttributes().getClassMap());
																								cyNetwork.getNodeAttributes().set(overlapWindow.getNetwork().getNodeAttributes());
																								//cyNetwork.getEdgeAttributes().set(overlapWindow.getNetwork().getEdgeAttributes());
																								
																								layout(cyNetwork);	
																								//make everythign fit in the view
																								FitContentAction fitAction = new FitContentAction(newWindow);
																								fitAction.actionPerformed(new ActionEvent(this,0,""));
																								
																								newWindow.getMainFrame().setVisible(true);
																}
								}
								/**
									* Layout the nodes in the window
									* @param uses the nodeAttributes of the cyNetwork file to figure out what the identifier name of each node would be
									*/
								private void layout(CyNetwork myNetwork){
																GraphView myView = newWindow.getView();
																GMLTree gmlTree = (GMLTree)node2GMLTree.get(selectedNode);
																Vector nodeLabels  = gmlTree.getVector("graph|node|label","|",GMLTree.STRING);
																Vector nodeX = gmlTree.getVector("graph|node|graphics|x","|",GMLTree.DOUBLE);
																Vector nodeY = gmlTree.getVector("graph|node|graphics|y","|",GMLTree.DOUBLE);
																Iterator it = nodeLabels.iterator();
																GraphObjAttributes nodeAttributes = myNetwork.getNodeAttributes();
																for(int i=0;i<nodeLabels.size();i++){
																								String nodeName = (String)nodeLabels.get(i);
																								Number X = (Number) nodeX.get(i);
																								Number Y = (Number) nodeY.get(i);
																								//get the node associated with that node label
																								Node current = (Node)nodeAttributes.getGraphObject(nodeName);
																								NodeView currentView = myView.getNodeView(current);
																								currentView.setXPosition(X.doubleValue());
																								currentView.setYPosition(Y.doubleValue());
																}
								}

								/**
									* Create a cyNetwork from the global GMLTree object.
									* This will only be called if the CyNetwork has not been
									* created before, not that this really takes that long anyway
									*/
								private CyNetwork createCyNetwork(){
																//need to create a new cynetwork for this node
																//using hte gmlTree object we already have	
																RootGraph rootGraph = GinyFactory.createRootGraph();
																GraphObjAttributes nodeAttributes = new GraphObjAttributes();
																GraphObjAttributes edgeAttributes = new GraphObjAttributes();
																// create and read the GML file

																GMLTree gmlTree = (GMLTree)node2GMLTree.get(selectedNode);
																Vector nodeIds = gmlTree.getVector("graph|node|id","|",GMLTree.INTEGER);
																Vector nodeLabels = gmlTree.getVector("graph|node|label","|",GMLTree.STRING);

																// in case gml node ids are not ordered consecutively (0..n)
																Hashtable nodeNameMap  = new Hashtable(nodeIds.size());
																for(int i=0; i<nodeIds.size(); i++) {
																								nodeNameMap.put(nodeIds.get(i), nodeLabels.get(i));
																}

																Vector edgeSources = gmlTree.getVector("graph|edge|source","|",GMLTree.INTEGER);
																Vector edgeTargets = gmlTree.getVector("graph|edge|target","|",GMLTree.INTEGER);
																Vector edgeLabels = gmlTree.getVector("graph|edge|label|","|",GMLTree.STRING);


																String et = "pp";
																if(edgeLabels.isEmpty()) {
																								for(int i=0; i < edgeSources.size(); i++) {
																																edgeLabels.add(et);
																								}
																}

																//---------------------------------------------------------------------------
																// loop through all of the nodes (using a hash to avoid duplicates)
																// adding nodes to the rootGraph. 
																// Create the nodeName mapping in nodeAttributes
																//---------------------------------------------------------------------------
																Hashtable nodeHash = new Hashtable ();
																String nodeName, interactionType;
																for(int i=0; i<nodeIds.size(); i++) {
																								nodeName = (String) nodeNameMap.get(nodeIds.get(i));
																								//if(canonicalize) nodeName = canonicalizeName(nodeName);	      
																								if (!nodeHash.containsKey(nodeName)) {
																																Node node = rootGraph.getNode(rootGraph.createNode());
																																node.setIdentifier(nodeName);
																																nodeHash.put(nodeName, node);
																																nodeAttributes.addNameMapping(nodeName, node);
																								}
																}
																//---------------------------------------------------------------------------
																// loop over the interactions creating edges between all sources and their 
																// respective targets.
																// for each edge, save the source-target pair, and their interaction type,
																// in edgeAttributes -- a hash of a hash of name-value pairs, like this:
																// ???  edgeAttributes ["interaction"] = interactionHash
																// ???  interactionHash [sourceNode::targetNode] = "pd"
																//---------------------------------------------------------------------------
																String sourceName, targetName, edgeName;
																for (int i=0; i < edgeSources.size(); i++) {
																								sourceName = (String) nodeNameMap.get(edgeSources.get(i));
																								targetName = (String) nodeNameMap.get(edgeTargets.get(i));
																								interactionType = (String) edgeLabels.get(i);

																								Node sourceNode = (Node) nodeHash.get(sourceName);
																								Node targetNode = (Node) nodeHash.get(targetName);
																								Edge edge = rootGraph.getEdge(rootGraph.createEdge(sourceNode, targetNode));
																								edgeName = sourceName + " (" + interactionType + ") " + targetName;
																								int previousMatchingEntries = edgeAttributes.countIdentical(edgeName);
																								if (previousMatchingEntries > 0)
																																edgeName = edgeName + "_" + previousMatchingEntries;
																								edgeAttributes.add("interaction", edgeName, interactionType);
																								edgeAttributes.addNameMapping(edgeName, edge);
																}

																return new CyNetwork(rootGraph,nodeAttributes,edgeAttributes);
								}
}

