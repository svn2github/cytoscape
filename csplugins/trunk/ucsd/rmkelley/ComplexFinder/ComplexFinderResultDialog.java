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
import javax.swing.border.TitledBorder;
import ucsd.rmkelley.Util.*;


class ComplexFinderResultDialog extends RyanDialog implements ListSelectionListener{
  Vector results;
  JTable table;
  JButton viewButton;
  CyNetwork physicalNetwork;
  

  /**
   * Creates a dialog which will display the values 
   * of a previous run svaed inot a file
   */
  public ComplexFinderResultDialog(File inputFile) throws IOException{
    /*
     * Try to find the gentic network
     */
    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
    
    physicalNetwork = getNetworkByTitle(reader.readLine());
    results = new Vector();
    while(reader.ready()){
      String [] splat = reader.readLine().split("\t");
      int id = Integer.parseInt(splat[0]);
      Set one = string2NodeSet(splat[1]);
      double score = Double.parseDouble(splat[2]);
      results.add(new NetworkModel(id,one,score));
    }
    initialize();
  }

  
  public ComplexFinderResultDialog(CyNetwork physicalNetwork, Vector results){
    this.results = results;
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
    table = new JTable(new ComplexFinderResultModel(results));
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
	  NetworkModel model = (NetworkModel)ComplexFinderResultDialog.this.results.get(table.getSelectionModel().getMinSelectionIndex());
	  List allNodes = new Vector();
	  allNodes.addAll(model.nodes);

	  
	  List allEdges = new Vector();
	  allEdges.addAll(ComplexFinderResultDialog.this.physicalNetwork.getConnectingEdges(allNodes));

	  CyNetwork newNetwork = Cytoscape.createNetwork(allNodes,allEdges,"Network Model: "+model.ID);
	  CyNetworkView newView = Cytoscape.getNetworkView(newNetwork.getIdentifier());
	  if(newView != null){
	    CircleGraphLayout layout = new CircleGraphLayout(newView,model.nodes);
	    layout.construct();
	  }
	}
      });
    JButton saveButton = new JButton("Save results");
    saveButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  ComplexFinderResultDialog.this.disableInput();
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
	  ComplexFinderResultDialog.this.enableInput();
	}
      });

    JButton validateButton = new JButton("Make predictions");
    validateButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  GOprediction prediction = new GOprediction(new File("GOID2orfs.txt"),new File("GOID2parents.txt"));
	  prediction.makePredictions(results,(List)null);
	}
      });
    
    JButton assessButton = new JButton("Assess");
    assessButton.addActionListener(new ActionListener(){
	public void actionPerformed = new GOprediction(new File("GOID2orfs.txt"),new File("GOID2parents.txt"));
	HashMap result = prediction.complexAssessment(results);
	try{
	  FileWriter writer = new FileWriter("assessment.txt",false);
	  for(Iterator it = result.keySet().iterator();it.hasNext();){
	    Node node = (Node)it.next();
	    writer.write(node+"\t"+result.get(node)+"\n");
	  }
	  writer.close();
	}catch(Exception e){
	  e.printStackTrace();
	  System.exit(-1);
	}
      });


    southPanel.add(viewButton);
    southPanel.add(saveButton);
    southPanel.add(validateButton);
    southPanel.add(assessButton = new JButton("Assess");
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
      physicalNetwork.setFlaggedNodes(model.nodes,true);
    }
    else{
      viewButton.setEnabled(false);
    }
  }


  protected void saveResults(File outputFile) throws IOException{
    PrintStream stream = new PrintStream(new FileOutputStream(outputFile));
    stream.println(physicalNetwork.getTitle());
    for(Iterator modelIt = results.iterator();modelIt.hasNext();){
      NetworkModel model = (NetworkModel)modelIt.next();
      stream.print(model.ID);
      stream.print("\t"+nodeCollection2String(model.nodes));
      stream.println("\t"+model.score);
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
  protected String nodeCollection2String(Collection nodes){
    String result = "";
    Iterator nodeIt = nodes.iterator();
    if(nodes.size() > 0){
      Node node = (Node)nodeIt.next();
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
