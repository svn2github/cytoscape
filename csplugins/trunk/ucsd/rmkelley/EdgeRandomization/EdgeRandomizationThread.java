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


public class EdgeRandomizationThread extends Thread{
  int iteration_limit;
  Random rand = new Random();
  //the file to which the results are output
  File scoreFile;

  /**
   * The network which will be randomizaed
   */
  CyNetwork currentNetwork;
  
  /**
   * The options for computation
   */
  EdgeRandomizationOptions options;

  public EdgeRandomizationThread(EdgeRandomizationOptions options){
    this.options = options;
  }
  
  public void run(){
    currentNetwork = options.currentNetwork;
     
    
    List directedTypes = options.directedTypes;
    iteration_limit = options.iterations;
    EdgeRandomizer randomizer = new EdgeRandomizer(currentNetwork,options.directedTypes);
    int [][] counts = randomizer.createUndirectedCountMatrix(iteration_limit);
    
    scoreFile = options.saveFile;
    try{
      ProgressMonitor myMonitor =  new ProgressMonitor(Cytoscape.getDesktop(),null, "Writing file to disk",0,currentNetwork.getNodeCount());
      myMonitor.setMillisToPopup(50);
      int updateInterval = (int)Math.ceil(currentNetwork.getNodeCount()/100.0);
      PrintStream stream = new PrintStream(new FileOutputStream(scoreFile));
      stream.println(iteration_limit);
      for(int idx=0;idx<currentNetwork.getNodeCount();idx++){
	if(idx % updateInterval == 0){
	  if(myMonitor.isCanceled()){
	    throw new RuntimeException("Score file generation cancelled");
	  }
	  myMonitor.setProgress(idx);
	}
	stream.print(currentNetwork.getNodeAttributeValue(currentNetwork.getNode(idx+1),Semantics.CANONICAL_NAME));
	for(int idy=0;idy<counts[idx].length;idy++){
	  stream.print("\t"+counts[idx][idy]);
	}
	stream.println();
      }
      myMonitor.close();
      stream.close();
    }catch(Exception e){
      e.printStackTrace();
      System.exit(-1);
    }

  }



  public File getScoreFile(){
    return scoreFile;
  }
 

}

