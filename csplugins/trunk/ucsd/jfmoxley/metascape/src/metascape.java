package ucsd.jfmoxley.metascape;

import ucsd.jfmoxley.metascape.*;

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

import cytoscape.CyNode;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.data.GraphObjAttributes; // DEPRECATED???
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.Cytoscape;
import cytoscape.data.readers.TextFileReader;
import cytoscape.util.GinyFactory;
import cytoscape.util.CyFileFilter;


public class metascape extends CytoscapePlugin {

  // TODO Ask Ryan why these have to be here
  //private static String TYPE_ATTRIBUTE = "Type";
  //private static String LABEL_ATTRIBUTE = "Label";

  public metascape() {
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( new metascapeAction() );
  }

  public class metascapeAction extends AbstractAction {
        
    public metascapeAction() {super("Run Metascape");}
        
    public String describe() {
      StringBuffer sb = new StringBuffer();
      sb.append("Metascape lays out metabolic networks.");
      return sb.toString();
    }

    public void actionPerformed(ActionEvent ae) {
      Thread t = new metascapeThread();
      t.start();
    }

    class metascapeThread extends Thread{

      CyNetwork cyNetwork;
      CyNetworkView cyNetworkView;

      GraphObjAttributes nodeAttributes;  // DEPRECATED??
      GraphObjAttributes edgeAttributes;  // DEPRECATED??
      HashMap string2Node;

      public metascapeThread(){

      }

      public void run(){

	/*
	  First we must load the metabolic layout file.
	  Based up actions/LoadGraphFileAction.java
	*/
	JFileChooser chooser = new JFileChooser();
	CyFileFilter mlfFilter = new CyFileFilter();
	mlfFilter.addExtension("mlf");
	mlfFilter.setDescription("Metabolic layout files");
	chooser.addChoosableFileFilter(mlfFilter);
	int returnVal = chooser.showDialog(Cytoscape.getDesktop(),"Load metabolic layout");

	/*
	  Next we read in the file.
	*/
	TextFileReader textFileReader = new TextFileReader(chooser.getSelectedFile().getAbsolutePath());
	textFileReader.read();
	String file = textFileReader.getText();


	String lineDelimiter = "\n";
	StringTokenizer linesTokenizer = new StringTokenizer(file, lineDelimiter);
	while ( linesTokenizer.hasMoreTokens()) {
	  String line = linesTokenizer.nextToken();
	  System.out.println("line = "+line);
	  //Vector elements = new Vector ();
	  //elements.addElement("new element");
	}

	string2Node = new HashMap();
	    
	cyNetwork = Cytoscape.createNetwork("Metascape Graph");
	cyNetworkView = Cytoscape.createNetworkView(cyNetwork);

	// Hack: disable visual mapper
	// prevents overwriting when switching graphs
	cyNetworkView.setVisualMapperEnabled(false);
	
	//nodeAttributes = cyNetwork.getNodeAttributes();
	createNode("idString","nodeLookup","nodeLabel","nodeType",0.0,0.0);
	createNode("idString2","2lookup","2Label","nodetype",100.0,100.0);
	createNode("node3","3look","label3","nodEType",200.0,200.0);

	// Hack: fit content to window
	// prevents the initial improper zoom
	cyNetworkView.getView().fitContent();

	// TODO: hack to update left network viewer tab with node / edge count

	MetNode metNode = new MetNode();
	metNode.WhoAmI();
	metNode.graphics.setX(69);
	System.out.println(""+metNode.graphics.getX());
	Vector vector = new Vector();
	metNode.define("defineId","defineLabel","defineType",0,0, new Vector());
	System.out.println(metNode.getId());

      }

      protected Vector SplitLine(String line){
	Vector splitLine = new Vector();
	System.out.println("Hello, I am in splitLine.");
	return splitLine;
      }
    
      protected void createNode(String idString, String lookupName, String label, String type, double xPosition, double yPosition){
	//create the actual node object
	CyNode newNode = Cytoscape.getCyNode(idString,true);
	cyNetwork.addNode(newNode);

	//set its unique identifier
	string2Node.put(idString,newNode);

	cyNetwork.setNodeAttributeValue(newNode,"type",type);
	cyNetwork.setNodeAttributeValue(newNode,"label",label);

	//set the visual properties for this node
	//make it so it's not so ugly
	NodeView nodeView = cyNetworkView.getNodeView(newNode);
	
	nodeView.setXPosition(xPosition);
	nodeView.setYPosition(yPosition);
      
	nodeView.setShape(2);
	nodeView.getLabel().setText(label);

      }

      protected void createEdge(String idStringSource){

      }

      public class mlfNode {

	String id;

	public mlfNode() {
	}

	public String getId() {return id;}
	public void setId(String s) {id = s;}

	public void WhoAmI() {
	  System.out.println("I am the almight mlfNode!!");
	}
      }
    }
  }
}
