package ucsd.jfmoxley.metascape;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.Edge;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.model.RootGraphChangeListener;
import giny.model.RootGraphChangeEvent;

import cytoscape.plugin.AbstractPlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;
import cytoscape.data.readers.GMLReader;
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
public class metascape extends AbstractPlugin {
    
  CyWindow cyWindow;
  private static String TYPE_ATTRIBUTE = "Type";
  private static String LABEL_ATTRIBUTE = "Label";
    
  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
  public metascape(CyWindow cyWindow) {
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
    public SamplePluginAction() {super("Metascape");}
        
    /**
     * Gives a description of this plugin.
     */
    public String describe() {
      StringBuffer sb = new StringBuffer();
      sb.append("This is Metascape");
      return sb.toString();
    }
        
    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {
      Thread t = new metascapeThread(cyWindow);
      t.start();
    }
  }

  class metascapeThread extends Thread{
    CyWindow cyWindow;
    RootGraph rootGraph;
    GraphPerspective perspective;
    GraphView view;
    GraphObjAttributes nodeAttributes;
    GraphObjAttributes edgeAttributes;
    HashMap string2Node;
    public metascapeThread(CyWindow cyWindow){
      this.cyWindow = cyWindow;
      string2Node = new HashMap();
    }
    public void run(){
	    System.out.println("Here is my println");
	    /*
	    JFileChooser chooser = new JFileChooser();
	    // Note: source for ExampleFileFilter can be found in FileChooserDemo,
	    int returnVal = chooser.showOpenDialog(cyWindow.getMainFrame());
	    if(returnVal != JFileChooser.APPROVE_OPTION) {
		return;
	    }

	    TextFileReader textReader = new TextFileReader(chooser.getSelectedFile().getAbsolutePath());
	    textReader.read();
	    StringTokenizer stringTokenizer = new StringTokenizer(textReader.getText(),"\n");
	    while(stringTokenizer.hasMoreTokens()){
	    	StringTokenizer lineTokenizer = new StringTokenizer(stringTokenizer.nextToken());
		while(lineTokenizer.hasMoreTokens()){
			System.out.println(lineTokenizer.nextToken());
		}
	    }
	    GMLReader gmlReader = new GMLReader(chooser.getSelectedFile().getAbsolutePath());
	    CyNetwork newNetwork = new CyNetwork(gmlReader.getRootGraph(),gmlReader.getNodeAttributes(),gmlReader.getEdgeAttributes());
	    */
	    nodeAttributes = cyWindow.getNetwork().getNodeAttributes();
	    view = cyWindow.getView();
	    perspective = view.getGraphPerspective();
	    rootGraph = perspective.getRootGraph();
	    createNode("idString","nodeLookup","nodeLabel","nodeType",100.0,10.0);
	    createNode("idString2","2lookup","2Label","nodetype",200.0,30.0);
    }
    
    protected void createNode(String idString, String lookupName, String label, String type, double xPosition, double yPosition){
      //create the actual node object
      Node newNode = rootGraph.getNode(rootGraph.createNode());
      
      //set its unique identifier
      newNode.setIdentifier(idString);
      string2Node.put(idString,newNode);

      //restore the graph in the perspective
      perspective.restoreNode(newNode);

      //add lookup string for this node
      nodeAttributes.addNameMapping(lookupName,newNode);
      nodeAttributes.set(metascape.TYPE_ATTRIBUTE,lookupName,type);
      nodeAttributes.set(metascape.LABEL_ATTRIBUTE,lookupName,label);
      
      //set the visual properties for this node
      //make it so it's not so ugly
      NodeView nodeView = view.getNodeView(newNode);
      nodeView.setXPosition(xPosition);
      nodeView.setYPosition(yPosition);
      nodeView.getLabel().setText(label);

    }

    protected void createEdge(String idStringSource){

    }


  }

	
}

