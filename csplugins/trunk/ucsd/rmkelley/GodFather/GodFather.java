package csplugins.ucsd.rmkelley.GodFather;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.Edge;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.model.RootGraphChangeListener;
import giny.model.RootGraphChangeEvent;

import cytoscape.AbstractPlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;
import cytoscape.util.GinyFactory;

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
public class Temp extends AbstractPlugin{
    
  CyWindow cyWindow;
  private static String TYPE_ATTRIBUTE = "Type";
  private static String LABEL_ATTRIBUTE = "Label";
  
    
  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
  public Temp(CyWindow cyWindow) {
    this.cyWindow = cyWindow;
    cyWindow.getCyMenus().getOperationsMenu().add( new SamplePluginAction() );
  }
    
  /**
   * This class gets attached to the menu item.
   */
  public class SamplePluginAction extends AbstractAction {
        
    /**
     * The constructor sets the text that should appear on the menu item.
     */
    public SamplePluginAction() {super("Sample action");}
        
    /**
     * Gives a description of this plugin.
     */
    public String describe() {
      StringBuffer sb = new StringBuffer();
      sb.append("testing");
      return sb.toString();
    }
        
    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {
                       
      //inform listeners that we're doing an operation on the network
      Thread t = new MyTask(cyWindow); 
      t.start();
    }
  }
}

public class MyTask extends Thread{
  protected static String STATE_ATTRIBUTE = "expression";
  CyWindow cyWindow;
    
  public MyTask(CyWindow cyWindow){
    this.cyWindow = cyWindow;
   
  }
  public void run(){
        
    GraphGenerator myGenerator = new ModularGenerator(size,modularity);
    //for loop wrapped around calls to runParameterCombination
    
    
  }

  protected void runParameterCombination(int moduleSize,double sn, double nsp, GraphGenerator myGenerator){
    RootGraph randomGraph = myGenerator.getRandomGraph();
    Set module = getModule(randomGraph,moduleSize);
    CyNetwork network = new CyNetwork(randomGraph,new GraphObjAttributes(),new GraphObjAttributes());
    setAndBlurAttribute(network,module,STATE_ATTRIBUTE,sn);
    scrambleNetwork(network, nsp);
    
    Set foundModule = null;
    /*Call jActiveModules to return what it thinks is the best module in the current network*/
    //foundModule = jActive.getModule(network);
    ///for rmk
    compareAndTabulate(module,foundModule);
   
  }

  protected void compareAndTabulate(Set module, Set foundModule){
    //figure out the overlap between these two and
    //update our global sensitivity and selectivity data structure
    //for jfm
  }

  /**
   * Returns a set random connected modules of size nodes
   * from the graph
   */
  protected Set getModule(RootGraph graph, int size){
    HashSet result = new HashSet();
    //here we would randomly choose size selected
    //nodes from graph and add them to result
    //for whoever

    return result;
  }

  /**
   * Assign values to "attribute" in hte nodeAttributes for network
   */
  protected void setAndBlurAttribute(CyNetwork network,Set module,String attribute){
    //for rmk    
  }

  /**
   * Add/remove edges randomly from this network according to some parameter
   */
  protected void scrambleNetwork(CyNetwork network, double parameter){
    //for jfm
  }
      
}



abstract class GraphGenerator {
  public abstract RootGraph getRandomGraph();
  public abstract RootGraph getSize(int size);
}

class ModularGenerator extends GraphGenerator{
  int size;
  int modularity;
  public ModularGenerator(int size, int modularity){
    setSize(size);
    setModularity(modularity);
  }
  
  public void setSize(int size){
    this.size = size;
  }

  public void getSize(int size){
    return size;
  }

  public void setModularity(int modularity){
    this.modularity = modularity;
  }
  
  public RootGraph getRandomGraph(){
    RootGraph result = GinyFactory.getRootGraph();
    //here we would add ndoes and edges to result in a random
    //to create a modular scale-free network


    return result;
  }
}
