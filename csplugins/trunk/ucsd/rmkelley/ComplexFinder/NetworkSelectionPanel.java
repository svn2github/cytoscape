package ucsd.rmkelley.ComplexFinder;
import java.io.*;
import java.util.*;
import edu.umd.cs.piccolo.activities.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
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
import java.beans.*;

class NetworkSelectionPanel extends JPanel{
  File scoreFile;
  JTextField fileText;
  JList list;
  ComplexFinderOptionsDialog dialog;
  EdgeRandomizationDialog randomDialog;
  PropertyChangeSupport pcs;
  /**
   * This button will bring up dialog to generate a new score file
   */
  JButton generate;
  public NetworkSelectionPanel(ComplexFinderOptionsDialog dialog){
    setLayout(new BorderLayout());
    this.dialog = dialog;
    pcs = new PropertyChangeSupport(this);
   

    JPanel centerPanel = new JPanel();
    centerPanel.setBorder(new TitledBorder("Select one of the following available networks"));
    centerPanel.setLayout(new BorderLayout());
    list = new JList(new NetworkListModel());
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.addListSelectionListener(new ListSelectionListener(){
	public void valueChanged(ListSelectionEvent e){
	  if(list.getSelectedIndex() > -1){
	    setGenerationEnabled(true);
	  }
	  else{
	    setGenerationEnabled(false);
	  }
	  pcs.firePropertyChange("",null,null);
	}});

    centerPanel.add(new JScrollPane(list),BorderLayout.CENTER);
    add(centerPanel,BorderLayout.CENTER);
    
    JPanel southPanel = new JPanel();
    southPanel.setBorder(new TitledBorder("Edge score file"));
    southPanel.add(new JLabel("Score file"));
    fileText = new JTextField("No selected score file");
    fileText.setEditable(false);
    southPanel.add(fileText);
    JButton change = new JButton("Select score file");
    change.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  File currentDirectory = NetworkSelectionPanel.this.dialog.getCurrentDirectory();
	  JFileChooser chooser = null;
	  if(currentDirectory != null){
	    chooser = new JFileChooser(currentDirectory);
	  }else{
	    chooser = new JFileChooser();
	  }
	  NetworkSelectionPanel.this.dialog.disableInput();
	  int returnVal = chooser.showOpenDialog(Cytoscape.getDesktop());
	  if(returnVal == JFileChooser.APPROVE_OPTION) {
	    scoreFile = chooser.getSelectedFile();
	    fileText.setText(scoreFile.getName());
	    NetworkSelectionPanel.this.dialog.pack();
	    NetworkSelectionPanel.this.dialog.setCurrentDirectory(chooser.getCurrentDirectory());
	    pcs.firePropertyChange("",null,null);
	  }
	  NetworkSelectionPanel.this.dialog.enableInput();
	  NetworkSelectionPanel.this.dialog.pack();
	  NetworkSelectionPanel.this.dialog.toFront();
	}});
    generate = new JButton("Generate new score file");
    setGenerationEnabled(false);
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
	 	  
	  new Thread(new Runnable(){
	      public void run(){
		randomDialog.show();
		if(!randomDialog.isCancelled()){
		  try{
		    EdgeRandomizationThread thread = new EdgeRandomizationThread(randomDialog.getOptions());
		    thread.run();
		    scoreFile = thread.getScoreFile();
		    fileText.setText(scoreFile.getName());
		    pcs.firePropertyChange("",null,null);
		  }
		  catch(Exception e){
		    e.printStackTrace();
		    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Failed to generate score file","Error", JOptionPane.ERROR_MESSAGE);
		  }
		  catch(OutOfMemoryError e){
		    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Out of memory","Error", JOptionPane.ERROR_MESSAGE);
		  }
		}
		NetworkSelectionPanel.this.dialog.enableInput();
		NetworkSelectionPanel.this.dialog.pack();
		NetworkSelectionPanel.this.dialog.toFront();
	      }}).start();
	}});
    southPanel.add(change);
    southPanel.add(generate);
    add(southPanel,BorderLayout.NORTH);
  }
  

  /**
   * Determine whether the current state of the input in this network selection panel
   * is valid, includes whether a network has been selected and if a score file has been
   * selected, this information is used to determine where the begin search button should
   * be enabled
   */
  public boolean validateInput(){
    return scoreFile != null && list.getSelectedIndex() > -1;
  }

  /**
   * Return an array detailing hte current problems with the input
   */
  public Vector getErrors(){
    Vector errors = new Vector();
    if(scoreFile == null){
      errors.add("No score file specified");
    }
    if(list.getSelectedIndex()< 0){
      errors.add("No network selected");
    }
    return errors;
  }


  /**
   * Get teh object which manages monitoring property changes
   */
  public PropertyChangeSupport getPropertyChangeSupport(){
    return pcs;
  }

  /**
   * Enable the generation of a score file
   */
  protected void setGenerationEnabled(boolean flag){
    if( flag ){
      generate.setEnabled(true);
      generate.setToolTipText("<html>This action will create a new<br>edge score file for the network "+((NetworkContainer)list.getSelectedValue()));
    }
    else{
      generate.setEnabled(false);
      generate.setToolTipText("<html>A network must be selected<br>for this action to be available");
    }
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


class NetworkListModel extends DefaultListModel implements PropertyChangeListener{
  protected HashMap id2Container = new HashMap();
  public NetworkListModel(){
    for(Iterator networkIt = Cytoscape.getNetworkSet().iterator();networkIt.hasNext();){
      NetworkContainer container = new NetworkContainer((CyNetwork)networkIt.next());
      id2Container.put(container.getNetwork().getIdentifier(),container);
      addElement(container);
    }
    Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
  }

  public void propertyChange(PropertyChangeEvent pce){
    if(pce.getPropertyName() == Cytoscape.NETWORK_CREATED){
      String id = (String)pce.getNewValue();
      CyNetwork network = Cytoscape.getNetwork(id); 
      NetworkContainer container = new NetworkContainer(network);
      id2Container.put(network.getIdentifier(),container);
      addElement(container);
    }
    else if(pce.getPropertyName() == Cytoscape.NETWORK_DESTROYED){
      String id = (String)pce.getNewValue();
      NetworkContainer container = (NetworkContainer)id2Container.get(id);
      if(container != null){
	id2Container.remove(id);
	removeElement(container);
      }
    }
  }
}
