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
import javax.swing.event.TableModelEvent;
import javax.swing.tree.TreePath;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelListener;
import javax.swing.JTable;


import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
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
  protected static int MAX_PREFERRED_SIZE = 100;

  public static void showDialog(){
    AttributeSaverDialog dialog = new AttributeSaverDialog();
    dialog.setVisible(true);
    return;
  }


  
  AttributeSaverState nodeState;
  AttributeSaverState edgeState;
    
  JTable nodeTable;
  JTable edgeTable;
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
    //fileTextField = new JTextField(10);
    //fileTextField.setEnabled(false);
    //fileTextField.setToolTipText("The current filename for the last clicked attribute");
    //create a focus listener that will update the filename when the text field loses keyboard focus
    //fileTextField.addFocusListener(new FocusAdapter(){
    //public void focusLost(FocusEvent fe){
    //  currentState.setAttributeFile(fileTextField.getText());}});
    
    String toolTipText = "Select multiple attributes to save. Modify \"Filename\" field to specify filename";
    nodeTable = new JTable(nodeState);
    nodeTable.setToolTipText(toolTipText);
    edgeTable = new JTable(edgeState);
    edgeTable.setToolTipText(toolTipText);
    //create a list that contains all of the node attributes
    //nodeAttributesList = new JList(nodeAttributes);
    //nodeAttributesList.setToolTipText("The list of available node attributes for the current network");
    //create a mouse listener that will update the contents of the text field with the file name of
    //the particular attribute that is clicked on
    //nodeAttributesList.addMouseListener(new MouseInputAdapter(){
    //	public void mousePressed(MouseEvent me){
    //fileTextField.setEnabled(true);
    //  if ( currentState != null) currentState.setAttributeFile(fileTextField.getText());
    //  currentState = nodeState;
    //  currentState.setCurrentAttribute((String)nodeAttributesList.getModel().getElementAt(nodeAttributesList.locationToIndex(me.getPoint())));
    //fileTextField.setText(currentState.getAttributeFile());}});
    
    //ibid
    //edgeAttributesList = new JList(edgeAttributes);
    //edgeAttributesList.setToolTipText("The list of available edge attributes for the current network");
    //edgeAttributesList.addMouseListener(new MouseInputAdapter(){
    //	public void mousePressed(MouseEvent me){
    //fileTextField.setEnabled(true);
    //  if ( currentState != null){
    //currentState.setAttributeFile(fileTextField.getText());
    //  }
    //  currentState = edgeState;
    //  currentState.setCurrentAttribute((String)edgeAttributesList.getModel().getElementAt(edgeAttributesList.locationToIndex(me.getPoint())));
    //  fileTextField.setText(currentState.getAttributeFile());}});
	  

    //initialize the directory browser component
    File currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
    fileChooser = new MyFileChooser(currentDirectory);

  
    JButton saveButton = new JButton("Choose Directory and Save");
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
	  JFileChooser myChooser = new JFileChooser(Cytoscape.getCytoscapeObj().getCurrentDirectory());
	  myChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	  if ( myChooser.showOpenDialog(Cytoscape.getDesktop()) == JFileChooser.APPROVE_OPTION){
	    nodeState.setSaveDirectory(myChooser.getSelectedFile());
	    edgeState.setSaveDirectory(myChooser.getSelectedFile());
	    int count = 0;
	    count += nodeState.writeState(nodeTable.getSelectedRows());
	    count += edgeState.writeState(edgeTable.getSelectedRows());
	    JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Successfully saved "+count+" files");
	    AttributeSaverDialog.this.dispose();
	  }}});
				 



    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
    leftPanel.add(new JLabel("Select multiple node attribute to save:"));
    //JScrollPane leftScrollPane = new JScrollPane(nodeTable,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JScrollPane leftScrollPane = new JScrollPane(nodeTable);
    leftScrollPane.setPreferredSize(new Dimension(MAX_PREFERRED_SIZE,(int)Math.min(MAX_PREFERRED_SIZE,nodeTable.getPreferredSize().getHeight())+nodeTable.getRowCount()*nodeTable.getRowMargin()+nodeTable.getRowHeight()));
    leftPanel.add(leftScrollPane);
    //leftPanel.add(nodeTable);

    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
    rightPanel.add(new JLabel("Select multiple edge attributes to save:"));
    JScrollPane rightScrollPane = new JScrollPane(edgeTable);
    rightScrollPane.setPreferredSize(new Dimension(MAX_PREFERRED_SIZE,(int)Math.min(MAX_PREFERRED_SIZE,edgeTable.getPreferredSize().getHeight())+edgeTable.getRowCount()*edgeTable.getRowMargin()+edgeTable.getRowHeight()));
    rightPanel.add(rightScrollPane);
    
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel,BoxLayout.Y_AXIS));
    centerPanel.add(leftPanel);
    centerPanel.add(rightPanel);
    
    JPanel southPanel = new JPanel();
    southPanel.add(saveButton);

    //    contentPane.add(new JScrollPane(fileTree));
    contentPane.add(centerPanel,BorderLayout.CENTER);
    contentPane.add(southPanel,BorderLayout.SOUTH);
    pack();
  }

}


class AttributeSaverState implements TableModel{
  public static String newline = System.getProperty("line.separator");
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
   * The directory in which to save the files
   */
  File saveDirectory;

  /**
   * Type of graph object to save
   */
  int type;

  /**
   * List of all attributes
   */
  Vector attributes;

  /**
   * List of all filenames
   */
  Vector filenames;

  /**
   * A vector of all the objects that
   * are listening to this TableModel
   */
  Vector listeners;
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
    this.type = type;
    this.cyNetwork = cyNetwork;
    this.listeners = new Vector();
    this.attributes = new Vector();
    this.filenames = new Vector();
    for ( int idx = 0; idx < nodeAttributes.length ; idx++) {
      attributes.add(nodeAttributes[idx]);
      filenames.add(nodeAttributes[idx]+suffix);
    } // end of for ()
  }

  /**
   * Get the filename assoicated with the current node attribute
   * @returns the filename
   */
  //public String getAttributeFile(String attribute){
  //  return (String)attribute2File.get(attribute);
  //}

  /**
   * Sets the filename for the current node attribute
   * @returns previous value associate with key or null if no such value 
   */
  //public String setAttributeFile(String attribute,String file){
  //  return (String)attribute2File.put(attribute,file);
  //}

  /**
   * Add an attribute ot the list of attributes which will be svaed
   * @param attribute the attribute to be added
  */
  //public void addAttribute(String attribute){
  //  currentAttributes.add(attribute);
  //  notify();
  //}

  /**
   * Remove an attribute from the list of attributes whic will be saved
   * @param attribute the attribute ot be removed
   * @return true if sucessful
   */
  //public boolean removeAttribute(String attribute){
  //  boolean result = currentAttributes.remove(attribute);
  //  this.notify();
  //  return result;
  //}
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
  public int writeState(int [] selectedRows){
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
    for ( int idx=0 ; idx < selectedRows.length; idx++ ) {
      try {
	String attribute = (String)attributes.get(selectedRows[idx]);
	//File attributeFile = new File(saveDirectory,(String)attribute2File.get(attribute));
	File attributeFile = new File(saveDirectory,(String)filenames.get(attributes.indexOf(attribute)));
	FileWriter fileWriter = new FileWriter(attributeFile);
	fileWriter.write(attribute+newline);
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
		    result += ")"+newline;
		  } 
		  else {
		    result += collection.iterator().next()+newline;
		  }
		  fileWriter.write(result);
		}
	      } 
	      else {
		fileWriter.write(name+" = "+value+newline);
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


  
  //below here is implementing the tableModel
  //see the interface for description of the methods
  public void addTableModelListener(TableModelListener tml){
    this.listeners.add(tml);
    return;
  }

  public void removeTableModelListener(TableModelListener tml){
    this.listeners.remove(tml);
    return;
  }

  //public void notifyListeners(){
  //  for ( Iterator tmlIt = listeners.iterator();tmlIt.hasNext();) {
  //    TableModelListener tml = (TableModelListener)tmlIt.next();
  //    tml.tableChanged(new TableModelEvent(this));
  //  } // end of for ()
    
  //}    

  public java.lang.Class getColumnClass(int columnIndex){
    return String.class;
  }

  public int getColumnCount(){
    return 2;
  }
    
  public int getRowCount(){
    return attributes.size();
  }

  public Object getValueAt(int rowIndex, int columnIndex){
    if (columnIndex == 0) {
      return attributes.get(rowIndex);
    } // end of if ()
    else {
      return filenames.get(rowIndex);
    } // end of else
  }

  public String getColumnName(int columnIndex){
    if ( columnIndex == 0) {
      return "Attribute";
    } // end of if ()
    else {
      return "Filename";
    } // end of else
  }
  
  public boolean isCellEditable(int rowIndex, int columnIndex){
    if ( columnIndex == 1) {
      return true;
    } // end of if ()
    return false;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex){
    if ( columnIndex != 1) {
      throw new RuntimeException("Can't set value in this column");
    } // end of if ()
    else {
      filenames.set(rowIndex,aValue);
    } // end of else
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
