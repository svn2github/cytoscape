package ucsd.rmkelley.WithinPathway;
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


class BetweenPathwayResultDialog extends RyanDialog implements ListSelectionListener{
  Vector results;
  JTable table;
  JButton viewButton;
  CyNetwork physicalNetwork;
  CyNetwork geneticNetwork;
  

  /**
   * Creates a dialog which will display the values 
   * of a previous run svaed inot a file
   */
  public BetweenPathwayResultDialog(File inputFile) throws IOException{
    /*
     * Try to find the gentic network
     */
    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
    
    physicalNetwork = getNetworkByTitle(reader.readLine());
    geneticNetwork = getNetworkByTitle(reader.readLine());
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

  
  public BetweenPathwayResultDialog(CyNetwork physicalNetwork, CyNetwork geneticNetwork, Vector results){
    this.results = results;
    this.physicalNetwork = physicalNetwork;
    this.geneticNetwork = geneticNetwork;
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
	  allNodes.addAll(model.nodes);

	  
	  List allEdges = new Vector();
	  allEdges.addAll(physicalNetwork.getConnectingEdges(allNodes));
	  allEdges.addAll(geneticNetwork.getConnectingEdges(allNodes));

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

    JButton validateButton = new JButton("Make predictions");
    validateButton.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent ae){
	      GOprediction prediction = new GOprediction(new File("GOID2orfs.txt"),new File("GOID2parents.txt"));
	      prediction.makePredictions(results,new File("physical-predictions.txt"));
	    }
      });

    JButton cvButton = new JButton("Cross-Validate GO");
    cvButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  GOprediction prediction = new GOprediction(new File("GOID2orfs.txt"),new File("GOID2parents.txt"));
	  prediction.crossValidate(results,new File("physical-predictions.txt"));
	}
      });

    JButton enrichButton = new JButton("Enriched Complexes");
    enrichButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  GOprediction prediction = new GOprediction(new File("GOID2orfs.txt"),new File("GOID2parents.txt"));
	  try{
	    FileWriter writer = new FileWriter("enriched.txt",false);
	    for(Iterator pathwayIt = results.iterator();pathwayIt.hasNext();){
	      NetworkModel pathway = (NetworkModel)pathwayIt.next();
	      List categories = prediction.findCategories(pathway,null,10000);
	      writer.write(""+pathway.ID+"\t"+!categories.isEmpty()+"\t"+categories+"\n");
	    }
	    writer.close();
	  }catch(Exception e){
	    e.printStackTrace();
	    System.exit(-1);
	  }
	}
	protected void assignBestPathway(Pathway pathway, HashMap node2BestPathway){
	  for(Iterator nodeIt = pathway.nodes.iterator();nodeIt.hasNext();){
	    Node node = (Node)nodeIt.next();
	    if(!node2BestPathway.containsKey(node)){
	      node2BestPathway.put(node, pathway);
	    }
	    else{
	      Pathway oldPathway = (Pathway)node2BestPathway.get(node);
	      if(pathway.score > oldPathway.score){
		node2BestPathway.put(node,pathway);
	      }
	    }
	  }
	}});



    JButton assessButton = new JButton("Assess");
    assessButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  //find hte best scoring pathway for each node
	  HashMap node2BestPathway = new HashMap();
	  GOprediction prediction = new GOprediction(new File("GOID2orfs.txt"),new File("GOID2parents.txt"));
	  for(Iterator resultIt = results.iterator();resultIt.hasNext();){
	    Pathway pathway = (Pathway)resultIt.next();
	    assignBestPathway(pathway,node2BestPathway);
	  }
	   try{
	    FileWriter writer = new FileWriter("assessWithin.txt",false);
	    int trials = 100;
	    int [] random1_results = new int[trials];
	    int [] random2_results = new int[trials];
	    int true1_result = 0;
	    int true2_result = 0;
	    for(Iterator nodeIt = node2BestPathway.keySet().iterator();nodeIt.hasNext();){
	      Node node = (Node)nodeIt.next();
	      Pathway pathway = (Pathway)node2BestPathway.get(node);	    
	      writer.write(node.toString());
	      List all1Neighbors = null;
	      HashSet pathway1Neighbors = new HashSet();
	      {
		HashSet all1NeighborsSet = new HashSet(physicalNetwork.neighborsList(node));
		all1NeighborsSet.remove(node);
		all1Neighbors = new Vector(all1NeighborsSet);
		for(Iterator neighborIt = all1Neighbors.iterator();neighborIt.hasNext();){
		  Object o = neighborIt.next();
		  if(pathway.nodes.contains(o)){
		    pathway1Neighbors.add(o);
		  }
		}
	      }
	      List all2Neighbors = null;
	      HashSet pathway2Neighbors = new HashSet();
	      {
		HashSet all2NeighborsSet = new HashSet(all1Neighbors);
		for(Iterator neighborIt = all1Neighbors.iterator();neighborIt.hasNext();){
		  all2NeighborsSet.addAll(physicalNetwork.neighborsList((Node)neighborIt.next()));
		}
		all2NeighborsSet.remove(node);
		all2Neighbors = new Vector(all2NeighborsSet);
		for(Iterator neighborIt = all2Neighbors.iterator();neighborIt.hasNext();){
		  Object o = neighborIt.next();
		  if(pathway.nodes.contains(o)){
		    pathway2Neighbors.add(o);
		  }
		}
	      }
		
	      double distance = prediction.getAverageDistance(node,all1Neighbors,pathway1Neighbors);
	      double full_distance = prediction.getAverageDistance(node,all1Neighbors);
	      if ( distance < full_distance ){
		true1_result += 1;
	      }
	      for(int trial = 0 ; trial < trials ; trial++){
		Set randomSubset = getRandomSubset(all1Neighbors,pathway1Neighbors.size());
		double random_distance = prediction.getAverageDistance(node,all1Neighbors,randomSubset);
		if(random_distance < full_distance){
		  random1_results[trial] += 1;
		}
	      }
	      distance = prediction.getAverageDistance(node,all2Neighbors,pathway2Neighbors);
	      full_distance = prediction.getAverageDistance(node,all2Neighbors);
	      if ( distance < full_distance ){
		true2_result += 1;
	      }
	      for(int trial = 0 ; trial < trials ; trial++){
		Set randomSubset = getRandomSubset(all2Neighbors,pathway2Neighbors.size());
		double random_distance = prediction.getAverageDistance(node,all2Neighbors,randomSubset);
		if(random_distance < full_distance){
		  random2_results[trial] += 1;
		}
	      }
	    }
	    writer.write(""+trials+"\n");
	    writer.write("true\t"+true1_result+"\t"+true2_result);
	    for(int idx=0;idx<trials;idx++){
	      writer.write("random"+idx+"\t"+random1_results[idx]+"\t"+random2_results[idx]+"\n");
	    }
	    writer.close();
	  }catch(Exception e){
	    e.printStackTrace();
	    System.exit(-1);
	  }



	}


	
	protected Set getRandomSubset(List list,int size){
	  Collections.shuffle(list);
	  return new HashSet(list.subList(0,size));
	}

	protected void assignBestPathway(Pathway pathway, HashMap node2BestPathway){
	  for(Iterator nodeIt = pathway.nodes.iterator();nodeIt.hasNext();){
	    Node node = (Node)nodeIt.next();
	    if(!node2BestPathway.containsKey(node)){
	      node2BestPathway.put(node, pathway);
	    }
	    else{
	      Pathway oldPathway = (Pathway)node2BestPathway.get(node);
	      if(pathway.score > oldPathway.score){
		node2BestPathway.put(node,pathway);
	      }
	    }
	  }
	}});
    
    southPanel.add(viewButton);
    southPanel.add(saveButton);
    southPanel.add(validateButton);
    southPanel.add(cvButton);
    southPanel.add(assessButton);
    southPanel.add(enrichButton);
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
    stream.println(geneticNetwork.getTitle());
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
