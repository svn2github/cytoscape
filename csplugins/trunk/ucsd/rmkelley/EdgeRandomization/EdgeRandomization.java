package ucsd.rmkelley.Temp;
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
    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public EdgeRandomization(){
	Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( new TestAction() );
    }
    
   

    public class TestAction extends AbstractAction{
    
	public TestAction() {super("Calculate Edge Scores");}
    
	/**
	 * This method is called when the user selects the menu item.
	 */
	public void actionPerformed(ActionEvent ae) {
	    Thread t = new EdgeRandomizationThread();
	    t.start();
	}

    }
}

class EdgeRandomizationThread extends Thread{
  int iteration_limit;
  Random rand = new Random();
  public void run(){
    CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
    //first need to figure out if 
    int [][] counts = createCountMatrix(currentNetwork.getNodeCount());
    
    //figure out what the different edge types are in the graph
    Set types = new HashSet();
    HashMap type2EdgeList = new HashMap();
    for(Iterator edgeIt = currentNetwork.edgesIterator();edgeIt.hasNext();){
      Edge edge = (Edge)edgeIt.next();
      if(edge.getSource() == edge.getTarget()){
	System.err.println("Ignoring self edge");
	continue;
      }
      String type = (String)currentNetwork.getEdgeAttributeValue(edge,Semantics.INTERACTION);
      if(!type2EdgeList.containsKey(type)){
	type2EdgeList.put(type,new Vector());
      }
      ((List)type2EdgeList.get(type)).add(edge);
      types.add(type);
    }
    
    EdgeRandomizationDialog dialog = new EdgeRandomizationDialog(new Vector(types));
    dialog.show();
    List directedTypes = dialog.getDirectedTypes();
    iteration_limit = dialog.getIterations();
    boolean [][] adjacencyMatrix = new boolean[currentNetwork.getNodeCount()][currentNetwork.getNodeCount()];
    
    for(Iterator typeIt = types.iterator();typeIt.hasNext();){
      String type = (String)typeIt.next();
      boolean directed = directedTypes.contains(type);
      List edgeList = (List)type2EdgeList.get(type);
      int [][] edges = new int[edgeList.size()][2];
      int idx = 0;
      for(Iterator edgeIt = edgeList.iterator();edgeIt.hasNext();idx++){
	Edge edge = (Edge)edgeIt.next();
	edges[idx][0] = currentNetwork.getIndex(edge.getSource())-1;
	edges[idx][1] = currentNetwork.getIndex(edge.getTarget())-1;
	if(!directed && edges[idx][0] < edges[idx][1]){
	  int temp = edges[idx][0];
	  edges[idx][0] = edges[idx][1];
	  edges[idx][1] = temp;
	}
      }
      updateCountMatrix(currentNetwork, type, edges, directed, counts, adjacencyMatrix);
    }
    
    String filename = currentNetwork.getTitle()+".rand";
    try{
      PrintStream stream = new PrintStream(new FileOutputStream(new File(filename)));
      stream.println(iteration_limit);
      for(int idx=0;idx<currentNetwork.getNodeCount();idx++){
	stream.print(currentNetwork.getNodeAttributeValue(currentNetwork.getNode(idx+1),Semantics.CANONICAL_NAME));
	for(int idy=0;idy<counts[idx].length;idy++){
	  stream.print("\t"+counts[idx][idy]);
	}
	stream.println();
      }
      stream.close();
    }catch(Exception e){
      e.printStackTrace();
      System.exit(-1);
    }
    JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Result stored in file: "+filename,"Randomization complete",JOptionPane.INFORMATION_MESSAGE);
  }

  public int [][] createCountMatrix(int nodeCount){
    int [][] result = new int[nodeCount][];
    for(int idx = 0;idx<nodeCount;idx++){
      result[idx] = new int[idx];
      for(int idy=0;idy<result[idx].length;idy++){
	result[idx][idy] = 1;
      }
    }
    return result;
  }


  //this function must be updated to reflect the influence of multiple types of network edges
  //connecting the same two nodes.
  public void updateCountMatrix(CyNetwork fullNetwork,  String type, int [][] edgeList, boolean directed, int [][] counts, boolean [][] adjacencyMatrix){
    for(int idx=0;idx<adjacencyMatrix.length;idx++){
      for(int idy=0;idy<adjacencyMatrix[idx].length;idy++){
	adjacencyMatrix[idx][idy] = false;
      }
    }
        
 
    for(int idx=0;idx < edgeList.length;idx++){
      adjacencyMatrix[edgeList[idx][0]][edgeList[idx][1]] = true;
    }

    int iteration = 0;
    ProgressMonitor myMonitor =  new ProgressMonitor(Cytoscape.getDesktop(),null, "Randomizing Type: "+type,0,100);
    int updateInterval = (int)Math.ceil(iteration_limit/100.0);
    int progress = 0;
    while(iteration++ < iteration_limit){
      if(iteration%updateInterval == 0){
	myMonitor.setProgress(progress++);
      }
      int randomized_edges = 0;
      int randomized_edge_limit = edgeList.length*2;
      while(randomized_edges < randomized_edge_limit){
	//chooose pair of random edges
	int edgeOne = rand.nextInt(edgeList.length);
	int edgeTwo = rand.nextInt(edgeList.length);
	
	int old_source_1 = edgeList[edgeOne][0];
	int old_target_1 = edgeList[edgeOne][1];
	int old_source_2 = edgeList[edgeTwo][0];
	int old_target_2 = edgeList[edgeTwo][1];
	
	int new_source_1,new_source_2,new_target_1,new_target_2;
	new_source_1 = old_source_1;
	new_source_2 = old_source_2;
	new_target_1 = old_target_2;
	new_target_2 = old_target_1;
	if(!directed){
	  if(new_source_1 < new_target_1){
	    int temp = new_source_1;
	    new_source_1 = new_target_1;
	    new_target_1 = temp;
	  }
	  if(new_source_2 < new_target_2){
	    int temp = new_source_2;
	    new_source_2 = new_target_2;
	    new_target_2 = temp;
	  }
	}
	//check if the selected edge pair is a valid swap
	//check to see if this will result in a loop
	if(new_source_1 == new_target_1 || new_source_2 == new_target_2){
	  continue;
	}
	//check to see if one of the newly created edges is already in the graph
	if(adjacencyMatrix[new_source_1][new_target_1] || adjacencyMatrix[new_source_2][new_target_2]){
	  continue;
	}
	//everything looks ok, try to actually perform the swap
	adjacencyMatrix[old_source_1][old_target_1] = false;
	adjacencyMatrix[old_source_2][old_target_2] = false;
	adjacencyMatrix[new_source_1][new_target_1] = true;
	adjacencyMatrix[new_source_2][new_target_2] = true;
	edgeList[edgeOne][1] = new_target_1;
	edgeList[edgeTwo][1] = new_target_2;
	if(!directed){
	  edgeList[edgeOne][0] = new_source_1;
	  edgeList[edgeTwo][0] = new_source_2;
	}
	randomized_edges++;
      }
      //update the count array
      for(int idx=0;idx<edgeList.length;idx++){
	int source = edgeList[idx][0];
	int target = edgeList[idx][1];
	//if the edge is present in both directions, only count it once
	if(directed && source < target && adjacencyMatrix[target][source]){
	  continue;
	}
	if(source < target){
	  counts[target][source] += 1;
	}
	else{
	  counts[source][target] += 1;
	}
      }
    }
    myMonitor.close();
  }

}


class EdgeRandomizationDialog extends JDialog{
  JTable table;
  EdgeRandomizationTableModel tableModel;
  List directedTypes;
  int iterations = 10000;
  JTextField iterationText;
  public EdgeRandomizationDialog(Vector types){
    setModal(true);
    getContentPane().setLayout(new BorderLayout());
    
    JPanel northPanel = new JPanel();
    northPanel.add(new JLabel("Iterations: "));
    iterationText = new JTextField((new Integer(iterations)).toString());
    iterationText.addFocusListener(new FocusListener(){
	public void focusGained(FocusEvent e){}
	public void focusLost(FocusEvent e){
	  try{
	    Integer temp = new Integer(iterationText.getText());
	    iterations = temp.intValue();
	  }catch(Exception except){
	    iterationText.setText((new Integer(iterations)).toString());
	  }}});
    northPanel.add(iterationText);
    getContentPane().add(northPanel,BorderLayout.NORTH);



    tableModel = new EdgeRandomizationTableModel(types);
    table = new JTable(tableModel);
    getContentPane().add(new JScrollPane(table),BorderLayout.CENTER);
    
    JPanel southPanel = new JPanel();
    JButton ok = new JButton("OK");
    southPanel.add(ok);
    getContentPane().add(southPanel,BorderLayout.SOUTH);
    ok.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  directedTypes = new Vector();
	  for(int idx=0;idx<tableModel.getRowCount();idx++){
	    if(((Boolean)tableModel.getValueAt(idx,1)).booleanValue()){
	      directedTypes.add(tableModel.getValueAt(idx,0));
	    }
	  }
	  dispose();
	}});
    pack();

  }

  public List getDirectedTypes(){
    return directedTypes;
  }

  public int getIterations(){
    return iterations;
  }
  
}

class EdgeRandomizationTableModel extends AbstractTableModel{
  List columnNames;
  List [] data;
  int BOOLEAN_COLUMN = 1;

  public EdgeRandomizationTableModel(List types){
    columnNames = Arrays.asList(new String[]{"Type","Directed"});
    data = new List [2];
    data[0] = new Vector(types);
    Vector booleans = new Vector();
    for(int idx=0;idx<types.size();idx++){
      booleans.add(new Boolean(false));
    }
    data[1] = booleans;
  }

  public int getColumnCount(){
    return columnNames.size();
  }

  public int getRowCount(){
    return data[0].size();
  }

  public String getColumnName(int col){
    return (String)columnNames.get(col);
  }

  public Object getValueAt(int row, int col){
    return data[col].get(row);
  }

  public Class getColumnClass(int c){
    return getValueAt(0,c).getClass();
  }

  public boolean isCellEditable(int row, int column){
    if(column == BOOLEAN_COLUMN){
      return true;
    }
    else{
      return false;
    }
  }

  public void setValueAt(Object value, int row, int column){
    data[column].set(row,value);
  }
}


  

