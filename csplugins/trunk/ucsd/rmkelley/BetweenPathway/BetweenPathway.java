package ucsd.rmkelley.BetweenPathway;
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
public class BetweenPathway extends CytoscapePlugin{
  BetweenPathwayOptionsDialog dialog;  
  
  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
  
  public BetweenPathway(){
    JMenu topMenu = new JMenu("BetweenPathway");
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( topMenu);
    topMenu.add(new AbstractAction("Find Between Pathway Models"){
	public void actionPerformed(ActionEvent ae) {
	  if(dialog == null){
	    dialog = new BetweenPathwayOptionsDialog();
	  }
	  new Thread(new Runnable(){
	      public void run(){
		dialog.show();
		if(!dialog.isCancelled()){
		  BetweenPathwayOptions options = dialog.getOptions();
		  
		  BetweenPathwayThread2 thread = new BetweenPathwayThread2(options);
		  //BetweenPathwayThread thread = new BetweenPathwayThread(options);
		  try{
		    thread.setPhysicalNetwork(options.physicalNetwork);
		    //options.geneticNetwork = (new EdgeRandomizer(options.geneticNetwork,directedTypes)).randomizeNetwork();
		    thread.setGeneticNetwork(options.geneticNetwork);
		    thread.loadGeneticScores(options.geneticScores);
		    thread.loadPhysicalScores(options.physicalScores);
				      
				      
		    if(options.generateCutoff){
		      //use a randomization process to determine an appropriate
		      //significant score cutoff.
		      //RandomizationDialog randomDialog = new RandomizationDialog(options.geneticNetwork,options);
		      RandomizationDialog randomDialog = new RandomizationDialog(options.physicalNetwork,options);
		      randomDialog.show();
		      if(randomDialog.isCancelled()){
			throw new RuntimeException("Thresh-hold generation cancelled");
		      }
		      options.cutoff = 0.0;
		      CyNetwork physicalNetwork = options.physicalNetwork;
		      EdgeRandomizer physicalRandomizer = new EdgeRandomizer(physicalNetwork,options.directedTypes);

		      randomDialog = new RandomizationDialog(options.geneticNetwork,options);
		      randomDialog.show();
		      if(randomDialog.isCancelled()){
			throw new RuntimeException("Thresh-hold generation cancelled");
		      }
		      CyNetwork geneticNetwork = options.geneticNetwork;
		      EdgeRandomizer geneticRandomizer = new EdgeRandomizer(geneticNetwork,options.directedTypes);

		      double [] scores = new double[options.iterations];
		      ProgressMonitor myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),"Thresh-hold determination","Iteration 1 of "+options.iterations,1,options.iterations);
		      int [] physical_edges = physicalNetwork.getEdgeIndicesArray();
		      int [] genetic_edges = geneticNetwork.getEdgeIndicesArray();
		      Cytoscape.destroyNetwork(physicalNetwork);
		      Cytoscape.destroyNetwork(geneticNetwork);
		      Cytoscape.getRootGraph().removeEdges(physical_edges);
		      Cytoscape.getRootGraph().removeEdges(genetic_edges);
		      for(int idx=0 ; idx<options.iterations ; idx++){
			if(myMonitor.isCanceled()){
			  throw new RuntimeException("Thresh-hold generation cancelled");
			}
			myMonitor.setProgress(idx+1);
			myMonitor.setNote("Iteration "+(idx+1)+" of "+options.iterations);
			CyNetwork randomGeneticNetwork = geneticRandomizer.randomizeNetwork();
			thread.setGeneticNetwork(randomGeneticNetwork);	
			CyNetwork randomPhysicalNetwork = physicalRandomizer.randomizeNetwork();
			thread.setPhysicalNetwork(randomPhysicalNetwork);

			thread.run();
			Vector results = thread.getResults(); 
			if(results.size() > 0){
			  scores[idx] = ((NetworkModel)results.firstElement()).score;
			  System.err.println(scores[idx]);
			}
			else{
			  scores[idx] = 0.0;
			}
			int [] old_physical_edges = randomPhysicalNetwork.getEdgeIndicesArray();
			int [] old_genetic_edges = randomGeneticNetwork.getEdgeIndicesArray();
			Cytoscape.destroyNetwork(randomPhysicalNetwork);
			Cytoscape.destroyNetwork(randomGeneticNetwork);
			Cytoscape.getRootGraph().removeEdges(old_physical_edges);
			Cytoscape.getRootGraph().removeEdges(old_genetic_edges);
		      }
		      myMonitor.close();
		      /*
		       *now that we have an array of random scores, 
		       *we want to figure out what score is in the alpha percentile
		       */
		      /*
		       * First put the array of random scores in ascending order
		       */
		      Arrays.sort(scores);
		      /**
		       * Now figure out which array index represents the appropriate scoring
		       * percentile. Since must be between 0 and 1 (exclusive), the index
		       * calculated here must be in the range of the array
		       */
		      options.cutoff = scores[(int)Math.floor((1-options.alpha)*options.iterations)];
		      JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Calculated cutoff is: "+options.cutoff);
		      options.generateCutoff = false;
		      //thread.setGeneticNetwork(options.geneticNetwork);
		      //thread.setPhysicalNetwork(options.physicalNetwork);
		      return;
		    }
		    thread.run();
		    JDialog betweenPathwayDialog = new BetweenPathwayResultDialog(options.geneticNetwork, options.physicalNetwork, thread.getResults());
		    betweenPathwayDialog.show();
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
		    BetweenPathwayResultDialog results = new BetweenPathwayResultDialog(chooser.getSelectedFile());
		    results.show();
		  }catch(Exception e){
		    JOptionPane.showMessageDialog(Cytoscape.getDesktop(),e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);	    
		  }
		}
	      }
	    }).start();
	}
      });
  }

}








  

    
