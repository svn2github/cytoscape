import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.GraphObjAttributes;
import cytoscape.plugin.CytoscapePlugin;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.TreePath;


import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Collection;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import javax.swing.JPanel;

public class AttributeSaver extends CytoscapePlugin{

  public AttributeSaver(){
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new AbstractAction("Save Attributes"){
	public void actionPerformed(ActionEvent ae){AttributeSaverDialog.showDialog();}});
  }
}

class AttributeSaverDialog extends JDialog{
  protected static String NODE_SUFFIX = ".NA";
  protected static String EDGE_SUFFIX = ".EA";

  public static void showDialog(){
    AttributeSaverDialog dialog = new AttributeSaverDialog();
    dialog.setVisible(true);
    return;
  }


  
  AttributeSaverState nodeState;
  AttributeSaverState edgeState;
  AttributeSaverState currentState;
  
  JList nodeAttributesList;
  JList edgeAttributesList;
  JTextField fileTextField;
  MyFileChooser fileChooser;

  public AttributeSaverDialog(){
    super(Cytoscape.getDesktop(),"Save Attributes", true);
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());    

    //get the current CyNetwork
    CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
    
    //get the graph attributes
    String [] edgeAttributes = currentNetwork.getEdgeAttributesList();
    String [] nodeAttributes = currentNetwork.getNodeAttributesList();
    
    //create the objects which will maintain the state of the dialog

    nodeState = new AttributeSaverState(nodeAttributes,NODE_SUFFIX,AttributeSaverState.NODES,Cytoscape.getCurrentNetwork());
    edgeState = new AttributeSaverState(edgeAttributes,EDGE_SUFFIX,AttributeSaverState.EDGES,Cytoscape.getCurrentNetwork());

    //create a text field that contains the file name for the current attribute
    fileTextField = new JTextField(10);
    fileTextField.setEnabled(false);
    fileTextField.setToolTipText("The current filename for the last clicked attribute");
    //create a focus listener that will update the filename when the text field loses keyboard focus
    fileTextField.addFocusListener(new FocusAdapter(){
	public void focusLost(FocusEvent fe){
	  currentState.setAttributeFile(fileTextField.getText());}});
    

    
    //create a list that contains all of the node attributes
    nodeAttributesList = new JList(nodeAttributes);
    nodeAttributesList.setToolTipText("The list of available node attributes for the current network");
    //create a mouse listener that will update the contents of the text field with the file name of
    //the particular attribute that is clicked on
    nodeAttributesList.addMouseListener(new MouseInputAdapter(){
	public void mousePressed(MouseEvent me){
	  fileTextField.setEnabled(true);
	  if ( currentState != null) currentState.setAttributeFile(fileTextField.getText());
	  currentState = nodeState;
	  currentState.setCurrentAttribute((String)nodeAttributesList.getModel().getElementAt(nodeAttributesList.locationToIndex(me.getPoint())));
	  fileTextField.setText(currentState.getAttributeFile());}});
    
    //ibid
    edgeAttributesList = new JList(edgeAttributes);
    edgeAttributesList.setToolTipText("The list of available edge attributes for the current network");
    edgeAttributesList.addMouseListener(new MouseInputAdapter(){
	public void mousePressed(MouseEvent me){
	  fileTextField.setEnabled(true);
	  if ( currentState != null) currentState.setAttributeFile(fileTextField.getText());
	  currentState = edgeState;
	  currentState.setCurrentAttribute((String)edgeAttributesList.getModel().getElementAt(edgeAttributesList.locationToIndex(me.getPoint())));
	  fileTextField.setText(currentState.getAttributeFile());}});
	  

    //initialize the directory browser component
    File currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
    fileChooser = new MyFileChooser(currentDirectory);

  
    JButton saveButton = new JButton("Save Attribute Files");
    saveButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  //if the user navigates to a particular directory but
	  //doesn't actually select a file, make that directory
	  //the selecgted file.
	  //File selectedFile  = fileChooser.getSelectedFile();
	  //File currentDirectory = fileChooser.getCurrentDirectory();
	  //if ( !currentDirectory.equals(selectedFile.getParentFile())) {
	  //  selectedFile = currentDirectory;
	  //} // end of if ()
	  File selectedFile = fileChooser.getCurrentDirectory();
	  nodeState.setSaveDirectory(selectedFile);
	  edgeState.setSaveDirectory(selectedFile);
	  int count = 0;
	  count += nodeState.writeState(nodeAttributesList.getSelectedValues());
	  count += edgeState.writeState(edgeAttributesList.getSelectedValues());
	  JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Successfully saved "+count+" files");
	  AttributeSaverDialog.this.dispose();}});



    JPanel westPanel = new JPanel();
    westPanel.setLayout(new BoxLayout(westPanel,BoxLayout.Y_AXIS));
    westPanel.add(new JLabel("Node Attributes:"));
    westPanel.add(new JScrollPane(nodeAttributesList));
    westPanel.add(new JLabel("Edge Attributes:"));
    westPanel.add(new JScrollPane(edgeAttributesList));
    westPanel.add(new JLabel("Filename:"));
    westPanel.add(fileTextField);

    JPanel centerPanel = new JPanel();
    centerPanel.add(fileChooser);

    JPanel southPanel = new JPanel();
    southPanel.add(saveButton);

    //    contentPane.add(new JScrollPane(fileTree));
    contentPane.add(centerPanel,BorderLayout.CENTER);
    contentPane.add(southPanel,BorderLayout.SOUTH);
    contentPane.add(westPanel,BorderLayout.WEST);
    setSize(640,380);
  }

}


class AttributeSaverState{
  /**
   * The default string to append for an attribute filename
   */
  protected String suffix;
  /**
   * Operate on nodes
   */
  public static int NODES = 1;
  
  /**
   * Operate on edges
   */
  public static int EDGES = 2;

  /**
   * Maintain a mapping of attributes to names to use for that attribute
   */
  HashMap attribute2File;
  
  /**
   * The currently active attribute
   */
  String currentAttribute;

  /**
   * The directory in which to save the files
   */
  File saveDirectory;

  /**
   * Type of graph object to save
   */
  int type;

  /**
   * Network to from which to read graph objects
   */
  CyNetwork cyNetwork;

  /**
   * Initialize the state
   * @param nodeAttributes An array of strings containing all node attributes
   * @param type operate on NODES or EDGES
   * @param cyNetwork the network to save
   */
  public AttributeSaverState(String [] nodeAttributes, String suffix, int type, CyNetwork cyNetwork){
    attribute2File = new HashMap();
    for ( int idx = 0; idx < nodeAttributes.length ; idx++) {
      attribute2File.put(nodeAttributes[idx],nodeAttributes[idx]+suffix);
    } // end of for ()
    this.type = type;
    this.cyNetwork = cyNetwork;
  }

  /**
   * Get the filename assoicated with the current node attribute
   * @returns the filename
   */
  public String getAttributeFile(){
    return (String)attribute2File.get(currentAttribute);
  }

  /**
   * Sets the filename for the current node attribute
   * @returns previous value associate with key or null if no such value 
   */
  public String setAttributeFile(String file){
    return (String)attribute2File.put(currentAttribute,file);
  }

  /**
   * Set the current attribute
   * @param currentAttribute the current attribute
   */
  public void setCurrentAttribute(String currentAttribute){
    this.currentAttribute = currentAttribute;
  }

  /**
   * Set the directory where the files will be saved to
   */
  public void setSaveDirectory(File saveDirectory){
    this.saveDirectory = saveDirectory;
  }
  
  /**
   * Write out the state for the given attributes
   * @return number of files successfully saved, the better way to do this would just be to throw the error and display a specific message
   * for each failure, but oh well.
  */
  public int writeState(Object [] attributes){
    List graphObjects = null;
    GraphObjAttributes graphObjAttributes = null;
    if ( type == NODES) {
      graphObjAttributes = cyNetwork.getNodeAttributes();
      graphObjects = cyNetwork.nodesList();
    }
    else {
      graphObjAttributes = cyNetwork.getEdgeAttributes();
      graphObjects = cyNetwork.edgesList();
    } // end of else
    
    Vector canonicalNames = new Vector();
    for ( Iterator objIt = graphObjects.iterator();objIt.hasNext();) {
      String canonicalName = graphObjAttributes.getCanonicalName(objIt.next());
      if ( canonicalName != null) {
	canonicalNames.add(canonicalName);
      } // end of if ()
      else {
	System.err.println("Canonical name not found");
      } // end of else
    } // end of for ()
    
    int count = 0;
    for ( int idx=0 ; idx < attributes.length; idx++ ) {
      try {
	String attribute = (String)attributes[idx];
	File attributeFile = new File(saveDirectory,(String)attribute2File.get(attribute));
	FileWriter fileWriter = new FileWriter(attributeFile);
	fileWriter.write(attribute+"\n");
	HashMap attributeMap = graphObjAttributes.getAttribute(attribute);
	if ( attributeMap != null) {
	  for ( Iterator canonicalIt = canonicalNames.iterator();canonicalIt.hasNext();) {
	    String name = (String)canonicalIt.next();
	    Object value = attributeMap.get(name);
	    if ( value != null) {
	      if ( value instanceof Collection) {
		String result = name + " = ";
		Collection collection = (Collection)value;
		if ( collection.size()>0) {
		  if ( collection.size()>1) {
		    Iterator objIt = collection.iterator();
		    result += "("+objIt.next();
		    while ( objIt.hasNext()) {
		      result += "::"+objIt.next();
		    } 
		    result += ")\n";
		  } 
		  else {
		    result += collection.iterator().next()+"\n";
		  }
		  fileWriter.write(result);
		}
	      } 
	      else {
		fileWriter.write(name+" = "+value+"\n");
	      } 
	    } 
	    else {
	      //System.err.println("Value was null for "+name);
	    }
	  } 
	  
	}
	else {
	  //System.err.println("Attribute name map is null");
	} 
	fileWriter.close();
	count++;
      } catch ( Exception e) {
	e.printStackTrace();
      } // end of try-catch
    } // end of for ()
    return count;
  }
}


/**
 * This class extends the basic file chooser to remove
 * some of the unwanted features and set some default options
 */
class MyFileChooser extends JFileChooser{
  public MyFileChooser(File currentDirectory){
    super();
    //super.setSelectedFile(initialSelection);
    super.setCurrentDirectory(currentDirectory);
    super.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    super.setControlButtonsAreShown(false);
    super.setAcceptAllFileFilterUsed(false);
    super.add(new JLabel(""),BorderLayout.SOUTH);
  }

 }
