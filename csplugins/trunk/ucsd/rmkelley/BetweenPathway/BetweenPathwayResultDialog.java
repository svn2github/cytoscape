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
import javax.swing.border.TitledBorder;
import ucsd.rmkelley.Util.RyanDialog;

class BetweenPathwayResultDialog extends RyanDialog implements ListSelectionListener{
  Vector results;
  JTable table;
  JButton viewButton;
  CyNetwork geneticNetwork,physicalNetwork;
  

  /**
   * Creates a dialog which will display the values 
   * of a previous run svaed inot a file
   */
  public BetweenPathwayResultDialog(File inputFile) throws IOException{
    /*
     * Try to find the gentic network
     */
    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
    
    geneticNetwork = getNetworkByTitle(reader.readLine());
    physicalNetwork = getNetworkByTitle(reader.readLine());
    results = new Vector();
    while(reader.ready()){
      String [] splat = reader.readLine().split("\t");
      int id = Integer.parseInt(splat[0]);
      Set one = string2NodeSet(splat[1]);
      Set two = string2NodeSet(splat[2]);
      double score = Double.parseDouble(splat[3]);
      double physical_score = Double.parseDouble(splat[4]);
      double genetic_score = Double.parseDouble(splat[5]);
      results.add(new NetworkModel(id,one,two,score,physical_score,genetic_score));
    }
    initialize();
  }

  
  public BetweenPathwayResultDialog(CyNetwork geneticNetwork, CyNetwork physicalNetwork, Vector results){
    this.results = results;
    this.geneticNetwork = geneticNetwork;
    this.physicalNetwork = physicalNetwork;
    initialize();
  }

  /**
   * Does all hte initialization of display componenets
   */
  public void initialize(){
    setTitle("Results");
    /*
     * Initialize the table which is usedto display the results
     */
    table = new JTable(new BetweenPathwayResultModel(results));
    table.getSelectionModel().addListSelectionListener(this);
    table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    getContentPane().setLayout(new BorderLayout());
    

    /*
     * Create the center panel containing the results
     */
    JPanel centerPanel = new JPanel();
    centerPanel.setBorder(new TitledBorder("Result Table"));
    centerPanel.setLayout(new BorderLayout());
    JScrollPane scroller = new JScrollPane(table);
    scroller.setPreferredSize(new Dimension(300,200));
    centerPanel.add(scroller,BorderLayout.CENTER);
    getContentPane().add(centerPanel,BorderLayout.CENTER);
    

    /**
     * Create the bottom panel containg the action buttons
     */
    JPanel southPanel = new JPanel();
    southPanel.setBorder(new TitledBorder("Actions"));
    
    viewButton = new JButton("Display selected model");
    viewButton.setEnabled(false);
    viewButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  NetworkModel model = (NetworkModel)BetweenPathwayResultDialog.this.results.get(table.getSelectionModel().getMinSelectionIndex());
	  List allNodes = new Vector();
	  allNodes.addAll(model.one);
	  allNodes.addAll(model.two);
	  
	  List allEdges = new Vector();
	  allEdges.addAll(BetweenPathwayResultDialog.this.geneticNetwork.getConnectingEdges(allNodes));
	  allEdges.addAll(BetweenPathwayResultDialog.this.physicalNetwork.getConnectingEdges(allNodes));

	  CyNetwork newNetwork = Cytoscape.createNetwork(allNodes,allEdges,"Network Model: "+model.ID);
	  CyNetworkView newView = Cytoscape.getNetworkView(newNetwork.getIdentifier());
	  if(newView != null){
	    CircleGraphLayout layout = new CircleGraphLayout(newView,model.one,model.two);
	    layout.construct();
	  }
	}
      });
    JButton saveButton = new JButton("Save results");
    saveButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  BetweenPathwayResultDialog.this.disableInput();
	  JFileChooser chooser = new JFileChooser();
	  chooser.setDialogTitle("Choose Destination File");
	  int returnVal = chooser.showSaveDialog(Cytoscape.getDesktop());
	  if(returnVal == JFileChooser.APPROVE_OPTION){
	    try{
	      saveResults(chooser.getSelectedFile());
	    }catch(Exception e){
	      JOptionPane.showMessageDialog(Cytoscape.getDesktop(),e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);	    
	    }
	  }
	  BetweenPathwayResultDialog.this.enableInput();
	}
      });
    southPanel.add(viewButton);
    southPanel.add(saveButton);
    getContentPane().add(southPanel,BorderLayout.SOUTH);
    pack();
  }

  /**
   * Return the first network which matches the given 
   * title. Throws an exception if no such network exists
   */
  public CyNetwork getNetworkByTitle(String title){
    Set networkSet = Cytoscape.getNetworkSet();
    for(Iterator networkIt = networkSet.iterator();networkIt.hasNext();){
      CyNetwork cyNetwork = (CyNetwork)networkIt.next();
      if(cyNetwork.getTitle().equals(title)){
	return cyNetwork;
      }
    }
    throw new RuntimeException("No network found with title "+title+", please load this network and try again");

  }

  public void valueChanged(ListSelectionEvent e){
    int index = table.getSelectedRow();
    if(index > -1){
      viewButton.setEnabled(true);
      NetworkModel model = (NetworkModel)results.get(index);
      geneticNetwork.setFlaggedNodes(model.one,true);
      geneticNetwork.setFlaggedNodes(model.two,true);
      physicalNetwork.setFlaggedNodes(model.one,true);
      physicalNetwork.setFlaggedNodes(model.two,true);
    }
    else{
      viewButton.setEnabled(false);
    }
  }


  protected void saveResults(File outputFile) throws IOException{
    PrintStream stream = new PrintStream(new FileOutputStream(outputFile));
    stream.println(geneticNetwork.getTitle());
    stream.println(physicalNetwork.getTitle());
    for(Iterator modelIt = results.iterator();modelIt.hasNext();){
      NetworkModel model = (NetworkModel)modelIt.next();
      stream.print(model.ID);
      stream.print("\t"+nodeSet2String(model.one));
      stream.print("\t"+nodeSet2String(model.two));
      stream.println("\t"+model.score);
      stream.println("\t"+model.physical_score);
      stream.println("\t"+model.genetic_score);
    }
    stream.close();

  }

  /**
   * Create a set of nodes from a colon-delimitd list
   * If a node correpsonding to a particular string
   * can not be found, a runtime exception will be thrown
   */
  protected Set string2NodeSet(String nodesString){
    Set result = new HashSet();
    String [] splat = nodesString.split("::");
    for(int idx = 0; idx < splat.length;idx++){
      Node node = Cytoscape.getCyNode(splat[idx]);
      if(node == null){
	throw new RuntimeException("Could not find the node named "+splat[idx]+" from the input file");
      }
      else{
	result.add(node);
      }
    }
    return result;
	  
  }

  /**
   * Create a colon delimited list of node names
   * from a set of nodes
   */
  protected String nodeSet2String(Set nodes){
    String result = "";
    Iterator nodeIt = nodes.iterator();
    if(nodes.size() > 0){
      Node node = (Node)nodes.iterator().next();
      result = node.getIdentifier();
    }
    if(nodes.size() > 1){
      while(nodeIt.hasNext()){
	result += "::" + ((Node)nodeIt.next()).getIdentifier();
      }
    }
    return result;
  }

      
  
}
