package ucsd.rmkelley.EdgeRandomization;
import java.io.*;
import java.util.*;
import edu.umd.cs.piccolo.activities.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import giny.view.NodeView;
import giny.model.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import phoebe.PNodeView;
import phoebe.PGraphView;
import cytoscape.data.Semantics;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*; 
import java.awt.BorderLayout;
import java.awt.event.*;
import cytoscape.layout.*;
import cytoscape.data.GraphObjAttributes;
import cytoscape.CytoscapeInit;

/**
 * This is a sample Cytoscape plugin using Giny graph structures. For each
 * currently selected node in the graph view, the action method of this plugin
 * additionally selects the neighbors of that node if their canonical name ends
 * with the same letter. (For yeast genes, whose names are of the form 'YOR167C',
 * this selects genes that are on the same DNA strand). This operation was
 * chosen to be illustrative, not necessarily useful.
 *
 * Note that selection is a property of the view of the graph, while neighbors
 * are a property of the graph itself. Thus this plugin must access both the
 * graph and its view.
 */
public class EdgeRandomization extends CytoscapePlugin{
    
  EdgeRandomizationDialog dialog;
  EdgeRandomizationOptions options;
  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
  public EdgeRandomization(){
    options = new EdgeRandomizationOptions();
    String [] args = CytoscapeInit.getArgs();
    EdgeRandomizationCommandLineParser parser = new EdgeRandomizationCommandLineParser(args,options);
    if(parser.run()){
      EdgeRandomizationThread thread = new EdgeRandomizationThread(options);
      thread.run();
      if(parser.exit()){
	cytoscape.Cytoscape.exit();
      }
    }
    JMenu topMenu = new JMenu("Edge Randomization");
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(topMenu);
    topMenu.add(new AbstractAction("Edge Randomization"){ 
	public void actionPerformed(ActionEvent ae) {
	  new Thread(new Runnable(){
	      public void run(){
		try{
		  options.currentNetwork = Cytoscape.getCurrentNetwork();
		  dialog = new EdgeRandomizationDialog(options);
		  dialog.show();
		  if(!dialog.isCancelled()){
		    EdgeRandomizationThread thread = new EdgeRandomizationThread(options);
		    thread.run();
		    JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Result stored in file: "+thread.getScoreFile().getName(),"Randomization complete",JOptionPane.INFORMATION_MESSAGE);
		  }
		}catch(Exception e){
		  JOptionPane.showMessageDialog(Cytoscape.getDesktop(),e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}}}).start();
	}});
    
    topMenu.add(new AbstractAction("Save Random Graphs"){
	public void actionPerformed(ActionEvent ae){
	  new Thread(new Runnable(){
	      public void run(){
		CyNetwork network = Cytoscape.getCurrentNetwork();
		int [] old_edges = network.getEdgeIndicesArray();
		options.currentNetwork = network;
		EdgeRandomizationDialog dialog = new EdgeRandomizationDialog(options);
		dialog.show();
		if(dialog.isCancelled()){
		  return;
		}
		EdgeRandomizer randomizer = new EdgeRandomizer(network,options.directedTypes);
		for(int idx=0;idx<options.iterations;idx++){
		  Cytoscape.destroyNetwork(network);
		  Cytoscape.getRootGraph().removeEdges(old_edges);
		  network = randomizer.randomizeNetwork();
		  old_edges = network.getEdgeIndicesArray();
		  //save network to file
		  saveNetwork("random"+idx,network);
		}
	      }
	      protected void saveNetwork(String name,CyNetwork network){
		GraphObjAttributes nodeAttributes = Cytoscape.getNodeNetworkData();
		GraphObjAttributes edgeAttributes = Cytoscape.getEdgeNetworkData();
	      
		try {
		  FileWriter fileWriter = new FileWriter( name );
		  String lineSep = System.getProperty("line.separator");
		  List nodeList = network.nodesList();
		  giny.model.Node[] nodes = ( giny.model.Node[] ) nodeList.toArray ( new giny.model.Node [0] );
		  for (int i=0; i < nodes.length; i++) {
		    StringBuffer sb = new StringBuffer ();
		    giny.model.Node node = nodes[i];
		    String canonicalName = nodeAttributes.getCanonicalName(node);
		    List edges = network.getAdjacentEdgesList(node, true, true, true); 
		      
		    if (edges.size() == 0) {
		      sb.append(canonicalName + lineSep);
		    } else {
		      Iterator it = edges.iterator();
		      while ( it.hasNext() ) {
			giny.model.Edge edge = (giny.model.Edge)it.next();
			if (node == edge.getSource()){ //do only for outgoing edges
			  giny.model.Node target = edge.getTarget();
			  String canonicalTargetName = nodeAttributes.getCanonicalName(target);
			  String edgeName = edgeAttributes.getCanonicalName(edge);
			  String interactionName =
			    (String)(edgeAttributes.getValue("interaction", edgeName));
			  if (interactionName == null) {interactionName = "xx";}
			  sb.append(canonicalName);
			  sb.append("\t");
			  sb.append(interactionName);
			  sb.append("\t");
			  sb.append(canonicalTargetName);
			  sb.append(lineSep);
			}
		      } // while
		    } // else: this node has edges, write out one line for every out edge (if any) */
		    fileWriter.write(sb.toString());
		    //System.out.println(" WRITE: "+ sb.toString() );
		  }  // for i
		  fileWriter.close();
		} catch (IOException ioe) {
		  System.err.println("Error while writing " + name);
		  ioe.printStackTrace();
		} // catch
	      }}).start();
	}});

  }
}


  

  
