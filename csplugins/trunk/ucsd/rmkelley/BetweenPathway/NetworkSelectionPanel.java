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
import ucsd.rmkelley.EdgeRandomization.EdgeRandomizationThread;
import ucsd.rmkelley.EdgeRandomization.EdgeRandomizationDialog;
import ucsd.rmkelley.EdgeRandomization.EdgeRandomizationOptions;

class NetworkSelectionPanel extends JPanel{
  File scoreFile;
  JTextField fileText;
  JList list;
  BetweenPathwayOptionsDialog dialog;
  EdgeRandomizationDialog randomDialog;
  /**
   * This button will bring up dialog to generate a new score file
   */
  JButton generate;
  public NetworkSelectionPanel(BetweenPathwayOptionsDialog dialog){
    setLayout(new BorderLayout());
    this.dialog = dialog;
    Vector model = new Vector();
    for(Iterator networkIt = Cytoscape.getNetworkSet().iterator();networkIt.hasNext();){
      model.add(new NetworkContainer((CyNetwork)networkIt.next()));
    }
    list = new JList(model);
    list.setSelectedIndex(0);
    list.addListSelectionListener(new ListSelectionListener(){
	public void valueChanged(ListSelectionEvent e){
	  if(list.getSelectedIndex() > -1){
	    generate.setEnabled(true);
	  }
	  else{
	    generate.setEnabled(false);
	  }
	}});

    add(list,BorderLayout.CENTER);
    JPanel southPanel = new JPanel();
    southPanel.add(new JLabel("Score file"));
    fileText = new JTextField("No selected score file");
    fileText.setEditable(false);
    southPanel.add(fileText);
    JButton change = new JButton("Select score file");
    change.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  JFileChooser chooser = new JFileChooser(NetworkSelectionPanel.this.dialog.getCurrentDirectory());
	  int returnVal = chooser.showOpenDialog(Cytoscape.getDesktop());
	  if(returnVal == JFileChooser.APPROVE_OPTION) {
	    scoreFile = chooser.getSelectedFile();
	    fileText.setText(scoreFile.getName());
	    NetworkSelectionPanel.this.dialog.pack();
	    NetworkSelectionPanel.this.dialog.setCurrentDirectory(chooser.getCurrentDirectory());
	  }
	}});
    generate = new JButton("Generate new score file");
    /*
     * Add an action listener here which will wait for the button press and then
     * create a new EdgeRandomization Dialog. This will create a score file for the network
     * that is currently selected and set the current file to the file that is generated
     */
    generate.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  
	  NetworkSelectionPanel.this.dialog.disableInput();
	  
	  /*
	   * Create a new edge randomization thread that will use the network currently selected in
	   * the network list, this button should only be enabled when such a selection exists, will
	   * have to add a selection change listener to the list
	   */
	  randomDialog = new EdgeRandomizationDialog(((NetworkContainer)list.getSelectedValue()).getNetwork());
	  randomDialog.show();
	  
	  new Thread(new Runnable(){
	      public void run(){
		try{
		  synchronized(randomDialog){
		    randomDialog.wait();
		  }
		}catch(Exception e){
		  e.printStackTrace();
		}
		if(!randomDialog.isCancelled()){
		  try{
		    EdgeRandomizationThread thread = new EdgeRandomizationThread(randomDialog.getOptions());
		    thread.run();
		    scoreFile = thread.getScoreFile();
		    fileText.setText(scoreFile.getName());
		  }
		  catch(Exception e){
		    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Failed to generate score file","Error", JOptionPane.ERROR_MESSAGE);
		  }
		  catch(OutOfMemoryError e){
		    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Out of memory","Error", JOptionPane.ERROR_MESSAGE);
		  }
		}
		System.err.println("Enabling input");
		NetworkSelectionPanel.this.dialog.enableInput();
		NetworkSelectionPanel.this.dialog.pack();
	      }}).start();
	}});
    southPanel.add(change);
    southPanel.add(generate);
    add(southPanel,BorderLayout.NORTH);
  }
  
  /**
   * Return the network that is currently selected in this panel
   */
  public CyNetwork getSelectedNetwork(){
    if(list.getModel().getSize() == 0){
      return null;
    }
    NetworkContainer container = (NetworkContainer)list.getSelectedValue();
    if(container == null){
      return null;
    }
    else{
      return container.getNetwork();
    }
  }

  public File getScoreFile(){
    return scoreFile;
  }


}
