package filter.cytoscape;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;

import cytoscape.*;
import cytoscape.data.GraphObjAttributes;
import cytoscape.view.CyWindow;
import filter.view.*;
import filter.model.*;
import cytoscape.CyNetwork;
import ViolinStrings.Strings;

/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */

public class StringPatternFilterEditor 
  extends FilterEditor 
  implements ActionListener,FocusListener {
         
  /**
   * This is the Name that will go in the Tab 
   * and is returned by the "toString" method
   */
  protected String identifier;

  protected JTextField nameField;
  protected JComboBox classBox;
  protected JTextField searchField;
  protected JComboBox attributeBox;

  protected String searchString;
  protected String selectedAttribute;
  protected String selectedClass;

  protected StringPatternFilter filter;

  protected CyWindow cyWindow;
  protected String DEFAULT_SEARCH_STRING = "";
  protected String DEFAULT_FILTER_NAME = "Regex: ";
  protected String DEFAULT_SELECTED_ATTRIBUTE = "";


  protected String RESET_CLASS;
  protected Class NODE_CLASS;
  protected Class EDGE_CLASS;
  protected Class STRING_CLASS;
  protected String DEFAULT_CLASS = StringPatternFilter.NODE; 
  protected Class filterClass;

  protected ComboBoxModel nodeAttributeModel;
  protected ComboBoxModel edgeAttributeModel;


  public StringPatternFilterEditor ( CyWindow cyWindow ) {
    super();
    this.cyWindow = cyWindow;
    try{
      STRING_CLASS = Class.forName("java.lang.String");
      NODE_CLASS = Class.forName("giny.model.Node");
      EDGE_CLASS = Class.forName("giny.model.Edge");
      filterClass = Class.forName("filter.cytoscape.StringPatternFilter");
      nodeAttributeModel = new NodeAttributeComboBoxModel(STRING_CLASS);
      edgeAttributeModel = new EdgeAttributeComboBoxModel(STRING_CLASS);
    }catch(Exception e){
      e.printStackTrace();
    }
				
				
    setLayout(new BorderLayout());
    identifier = "String Filter";
    setBorder( new TitledBorder( getFilterID()+" - "+getDescription()));

    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
    nameField.addActionListener(this);
    nameField.addFocusListener(this);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
   
    add( namePanel,BorderLayout.NORTH );

    JPanel all_panel = new JPanel();
    all_panel.setLayout(new GridLayout(3,1));	
    JPanel topPanel = new JPanel();
    topPanel.add(new JLabel("Select graph objects of type "));
    classBox = new JComboBox();
    classBox.addItem(StringPatternFilter.NODE);
    classBox.addItem(StringPatternFilter.EDGE);
    classBox.setEditable( false );
    classBox.addActionListener(this);
    topPanel.add(classBox);
				
    JPanel middlePanel = new JPanel();
    middlePanel.add(new JLabel(" with a value for text attribute "));
    
    attributeBox = new JComboBox();
    attributeBox.setEditable(false);
    attributeBox.addActionListener(this);
    middlePanel.add(attributeBox);

    JPanel bottomPanel = new JPanel();
    bottomPanel.add(new JLabel(" that matches the regular expression "));
				
    searchField = new JTextField(10);
    searchField.setEditable( true );
    searchField.addActionListener( this );
    searchField.addFocusListener(this);
    bottomPanel.add(searchField);

    all_panel.add(topPanel);
    all_panel.add(middlePanel);
    all_panel.add(bottomPanel);
    add(all_panel,BorderLayout.CENTER);

  }


  //----------------------------------------//
  // Implements Filter Editor
  //----------------------------------------//

  public Class getFilterClass(){
    return filterClass;
  }
  public String toString () {
    return identifier;
  }

  public String getDescription(){
    return StringPatternFilter.FILTER_DESCRIPTION;
  }

  public String getFilterID () {
    return StringPatternFilter.FILTER_ID;
  }

  /**
   * Creates a new filter initialized to the default values with the given name
   */
  public Filter createDefaultFilter(){
    return new StringPatternFilter(cyWindow,DEFAULT_CLASS,DEFAULT_SELECTED_ATTRIBUTE,DEFAULT_SEARCH_STRING,DEFAULT_FILTER_NAME);
  }
  /**
   * Accepts a Filter for editing
   * Note that this Filter must be a Filter that can be edited
   * by this Filter editor. 
   */
  public void editFilter ( Filter filter ) {
    if ( filter instanceof StringPatternFilter ) {
      // good, this Filter is of the right type
      getSwingPropertyChangeSupport().removePropertyChangeListener( this.filter );
      this.filter = ( StringPatternFilter )filter;      
      setSearchString(this.filter.getSearchString());
      setFilterName(this.filter.toString());
      setSelectedAttribute(this.filter.getSelectedAttribute());
      setSelectedClass(this.filter.getClassType());
      updateName();
      getSwingPropertyChangeSupport().addPropertyChangeListener( this.filter );
    }
  }

  //----------------------------------------//
  // StringPatternFilter Methods
  //----------------------------------------//

  // There should be getter and setter methods for
  // every editable property that the Filter needs to
  // to find out from the Editor. In this case there is only one
  // the search string.

  // Filter Name ///////////////////////////////////////

  public String getFilterName () {
    return identifier;
  }

  public void setFilterName ( String name ) {
    nameField.setText( name );
    identifier = name;
    fireFilterNameChanged();
  }

  // Search String /////////////////////////////////////
  
  public String getSearchString () {
    return searchString;
  }

  public void setSearchString ( String search_string ) {
    searchString = search_string;
    searchField.setText( search_string );
    fireSearchStringChanged();
  }

  // Selected Attribute ////////////////////////////////
  
  public String getSelectedAttribute () {
    return selectedAttribute;
  }

  public void setSelectedAttribute ( String new_attr ) {
    selectedAttribute = new_attr;
    attributeBox.setSelectedItem(new_attr);
    fireAttributeChanged();
  }

  public String getSelectedClass(){
    return selectedClass;
  }

  public void setSelectedClass(String newClass){
    selectedClass = newClass;
    if ( selectedClass == NumericAttributeFilter.NODE) {
      attributeBox.setModel(nodeAttributeModel);
      attributeBox.setSelectedItem(selectedAttribute);    
    } // end of if ()
    else {
      attributeBox.setModel(edgeAttributeModel);
      attributeBox.setSelectedItem(selectedAttribute);
    } // end of else    
    classBox.setSelectedItem(newClass);
    fireClassChanged();
    setSelectedAttribute((String)attributeBox.getSelectedItem());
  }

  public void handleEvent ( AWTEvent e ) {
    if ( e.getSource() == searchField ) {
      setSearchString(searchField.getText());
    } else if ( e.getSource() == nameField ) {
      setFilterName(nameField.getText());
    } else if ( e.getSource() == attributeBox ) {
      setSelectedAttribute((String)attributeBox.getSelectedItem());
    } else if( e.getSource() == classBox ){
      setSelectedClass((String)classBox.getSelectedItem());
    }
    updateName();
  }

  public void actionPerformed(ActionEvent ae){
    handleEvent(ae);
  }

  public void focusGained(FocusEvent e){};

  public void focusLost(FocusEvent e){
    handleEvent(e);
  }

  public void updateName () {
    StringBuffer buffer = new StringBuffer();
    buffer.append( getSelectedClass() + " : " );
    buffer.append( getSelectedAttribute()+" ~ " );
    buffer.append( getSearchString() );
    setFilterName(buffer.toString());
  }

  public void fireSearchStringChanged () {
    pcs.firePropertyChange( StringPatternFilter.SEARCH_STRING_EVENT, null, getSearchString() );
  }

  public void fireFilterNameChanged () {
    pcs.firePropertyChange( StringPatternFilter.FILTER_NAME_EVENT, null, getFilterName() );
  }
 
  public void fireClassChanged(){
    pcs.firePropertyChange( StringPatternFilter.CLASS_TYPE_EVENT,null, getSelectedClass());
		
  }

  public void fireAttributeChanged () {
    pcs.firePropertyChange( StringPatternFilter.SELECTED_ATTRIBUTE_EVENT, null, getSelectedAttribute() );
  }

}
