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
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( new AbstractAction("Find Between Pathway Models"){
	public void actionPerformed(ActionEvent ae) {
	  if(dialog == null){
	    dialog = new BetweenPathwayOptionsDialog();
	  }
	  new Thread(new Runnable(){
	      public void run(){
		dialog.show();
		if(!dialog.isCancelled()){
		  BetweenPathwayThread thread = new BetweenPathwayThread(dialog.getOptions());
		  try{
		    thread.run();
		  }
	      catch(Exception e){
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(),e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);	    
	      }
	      catch(OutOfMemoryError e){
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Out of memory","Error",JOptionPane.ERROR_MESSAGE);	    
	      }
		}
	      }}).start();
	  
	} 
      });

    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( new AbstractAction("Load previous results"){
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








  

    
