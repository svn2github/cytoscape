package ucsd.rmkelley.ComplexFinder;
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
import javax.swing.event.*;
import javax.swing.table.*; 
import java.awt.BorderLayout;
import java.awt.event.*;
import cytoscape.layout.*;
import java.awt.Dimension;
import ucsd.rmkelley.EdgeRandomization.EdgeRandomizer;
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
public class ComplexFinder extends CytoscapePlugin{
  ComplexFinderOptionsDialog dialog;  
  
  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
  
  public ComplexFinder(){
    JMenu topMenu = new JMenu("Complex Finder");
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(topMenu);
    topMenu.add( new AbstractAction("Find Complexes"){
	public void actionPerformed(ActionEvent ae) {
	  if(dialog == null){
	    dialog = new ComplexFinderOptionsDialog();
	  }
	  new Thread(new Runnable(){
	      public void run(){
		dialog.show();
		if(!dialog.isCancelled()){
		  ComplexFinderOptions options = dialog.getOptions();
		  ComplexFinderThread thread = new ComplexFinderThread(options);
		  //BetweenPathwayThread thread = new BetweenPathwayThread(options);
		  try{
		    thread.setPhysicalNetwork(options.physicalNetwork);
		    thread.loadPhysicalScores(options.physicalScores);
		    thread.run();
		    JDialog complexFinderDialog = new ComplexFinderResultDialog(options.physicalNetwork, thread.getResults());
		    complexFinderDialog.show();
		  }
		  catch(Exception e){
		    e.printStackTrace();
		    JOptionPane.showMessageDialog(Cytoscape.getDesktop(),e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);	    
		  }
		  catch(OutOfMemoryError e){
		    JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Out of memory","Error",JOptionPane.ERROR_MESSAGE);	    
		  }
		}
	      }}).start();
	}

	
      });

     topMenu.add( new AbstractAction("Load previous results"){
	public void actionPerformed(ActionEvent ae){
	  new Thread(new Runnable(){
	      public void run(){
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(Cytoscape.getDesktop());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
		  try{
		    ComplexFinderResultDialog results = new ComplexFinderResultDialog(chooser.getSelectedFile());
		    results.show();
		  }catch(Exception e){
		    JOptionPane.showMessageDialog(Cytoscape.getDesktop(),e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);	    
		  }
		}
	      }
	    }).start();
	}
      });

    
     
     topMenu.add( new AbstractAction("Generate cutoff"){
	public void actionPerformed(ActionEvent ae){
	  new Thread(new Runnable(){
	      public void run(){
		ComplexFinderOptionsDialog dialog = new ComplexFinderOptionsDialog();
		dialog.show();
		ComplexFinderOptions options = dialog.getOptions();
		ComplexFinderThread thread = new ComplexFinderThread(options);
		thread.setPhysicalNetwork(options.physicalNetwork);
		thread.loadPhysicalScores(options.physicalScores);
		RandomizationDialog randomDialog = new RandomizationDialog(options.physicalNetwork,options);
		randomDialog.show();
		if(randomDialog.isCancelled()){
		  return;
		}
		CyNetwork physicalNetwork = options.physicalNetwork;
		double [] scores = new double[options.iterations];
		EdgeRandomizer randomizer = new EdgeRandomizer(physicalNetwork,options.directedTypes);
		int [] old_edges = physicalNetwork.getEdgeIndicesArray();
		Cytoscape.destroyNetwork(physicalNetwork);
		Cytoscape.getRootGraph().removeEdges(old_edges);
		for(int idx=0; idx<options.iterations; idx++){
		  System.err.println(idx);
		  CyNetwork randomNetwork = randomizer.randomizeNetwork();
		  thread.setPhysicalNetwork(randomNetwork);
		  thread.run();
		  Vector results = thread.getResults();
		  if(results.size() > 0){
		    scores[idx] = ((NetworkModel)results.firstElement()).score;
		    System.err.println(scores[idx]);
		  }
		  old_edges = randomNetwork.getEdgeIndicesArray();
		  Cytoscape.destroyNetwork(randomNetwork);
		  Cytoscape.getRootGraph().removeEdges(old_edges);
		  
		}
		Arrays.sort(scores);
		double cutoff = scores[(int)Math.floor((1-options.alpha)*options.iterations)];
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Cutoff is: "+cutoff);
	      }
	    }).start();
	}
      });
  }
}








  

    
