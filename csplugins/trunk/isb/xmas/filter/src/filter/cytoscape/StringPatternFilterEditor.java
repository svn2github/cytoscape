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
import cytoscape.data.*;

import filter.view.*;
import filter.model.*;

import ViolinStrings.Strings;

/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */

public class StringPatternFilterEditor 
  extends FilterEditor 
  implements ActionListener {
         
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

  protected StringPatternFilter filter;

  protected CyNetwork network;
  protected GraphObjAttributes objectAttributes;

  protected String DEFAULT_SEARCH_STRING = "";
  protected String RESET_SEARCH_STRING;

  protected String DEFAULT_FILTER_NAME = "StringPatter: ";
  protected String RESET_FITLER_NAME;

  protected String DEFAULT_SELECTED_ATTRIBUTE = "";
  protected String RESET_SELECTED_ATTRIBUTE;

		protected Class RESET_CLASS;
		protected Class NODE_CLASS;
		protected Class EDGE_CLASS;
		protected Class STRING_CLASS;
  protected Class DEFAULT_CLASS; 

  public StringPatternFilterEditor ( CyNetwork network ) {
    super();
    this.network = network;
    this.objectAttributes = network.getNodeAttributes();
				setLayout(new BorderLayout());
    identifier = "String Pattern";
    setBorder( new TitledBorder( "String Pattern Filter - Select nodes or edges based on pattern match to text attribute" ) );

    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
    add( namePanel,BorderLayout.NORTH );

				JPanel all_panel = new JPanel();
				//all_panel.setLayout(new GridLayout(3,3,20,20));
				//all_panel.add(new JLabel("Select Object Type"));
				//all_panel.add(new JLabel("Select Attribute"));
				//all_panel.add(new JLabel("String Pattern"));
				
				
				//JPanel class_panel = new JPanel();
				//class_panel.setLayout(new BoxLayout(class_panel,BoxLayout.Y_AXIS));
				all_panel.add(new JLabel("Select graph objects of type "));
				
				Vector classes = new Vector();
				try{
								STRING_CLASS = Class.forName("java.lang.String");
								NODE_CLASS = Class.forName("giny.model.Node");
								EDGE_CLASS = Class.forName("giny.model.Edge");
								DEFAULT_CLASS = NODE_CLASS;
								classes.add(NODE_CLASS);
								classes.add(EDGE_CLASS);
				}catch(Exception e){
								e.printStackTrace();
				}
				classBox = new JComboBox(classes);
				classBox.setEditable( false );
				classBox.addActionListener(this);
				all_panel.add(classBox);
				//class_panel.add(new JLabel( "Select Object Type"));
				//class_panel.add(classBox);
				//all_panel.add( class_panel);
				
				all_panel.add(new JLabel(" with a value for text attribute "));
				//JPanel attribute_panel = new JPanel();
				//attribute_panel.setLayout(new BorderLayout());
    attributeBox = new JComboBox();
				attributeBox.setEditable(false);
				attributeBox.addActionListener(this);
				all_panel.add(attributeBox);
				//attribute_panel.add(new JLabel("Select Attribute"),BorderLayout.NORTH);
				//attribute_panel.add( attributeBox,BorderLayout.SOUTH );
    //all_panel.add( attribute_panel );

				all_panel.add(new JLabel(" that matches the pattern "));
    //JPanel search_panel = new JPanel();
    //search_panel.setLayout(new BorderLayout());
				searchField = new JTextField(10);
    searchField.setEditable( true );
    searchField.addActionListener( this );
    all_panel.add(searchField);
				//search_panel.add(new JLabel("Pattern String"), BorderLayout.NORTH);
				//search_panel.add(searchField,BorderLayout.SOUTH );
    //all_panel.add( search_panel );


				updateAttributeBox(NODE_CLASS);
    all_panel.add(new JLabel(""));
				add( new JButton (new AbstractAction( "Update List of Attributes" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  /*String[] atts = objectAttributes.getAttributeNames();
                  System.out.println( "There are: "+atts.length+" attributes." );
                  for ( int i = 0; i < atts.length; ++i ) {
                    System.out.println( i+". "+atts[i] );
                  }
                  attributeBox.setModel( new DefaultComboBoxModel( objectAttributes.getAttributeNames() ) );
                  //( ( DefaultComboBoxModel )attributeBox.getModel() ).addElement( "canonicalName" );
                */  classBox.setSelectedItem(NODE_CLASS);
																				updateAttributeBox(NODE_CLASS);
																		}
              } ); } } ),BorderLayout.SOUTH );
				add(all_panel,BorderLayout.CENTER);
    setDefaults();
  }


  //----------------------------------------//
  // Implements Filter Editor
  //----------------------------------------//

  public String toString () {
    return identifier;
  }

  public String getFilterID () {
    return StringPatternFilter.FILTER_ID;
  }

  /** 
   * Returns a new Filter, or the Modified Filter 
   */
  public Filter getFilter() {

    String search_item = searchField.getText();
    String attr_item = ( String )attributeBox.getSelectedItem();
				Class currentClass = (Class)classBox.getSelectedItem(); 
    if ( currentClass == null || search_item == null || attr_item == null || nameField.getText() == null ) {
      return null;
    }
    return new StringPatternFilter( network, currentClass, attr_item, search_item, nameField.getText() );
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
      readInFilter();
      getSwingPropertyChangeSupport().addPropertyChangeListener( this.filter );
    }
  }

  /**
   * If the Filter is null, then set all values to it, otherwise reset 
   * to the Defaults.
   */
  public void reset () {
    if ( filter == null ) {
      setDefaults();
    } else {
      resetFilter();
    }
  }

  /**
   * Clears the Filter, and sets to Defaults.
   */
  public void clear () {
    filter = null;
    getSwingPropertyChangeSupport().removePropertyChangeListener( filter );
    setDefaults();
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
    return nameField.getText();
  }

  public void setFilterName ( String name ) {
    nameField.setText( name );
  }

  // Search String /////////////////////////////////////
  
  public String getSearchString () {
    return searchField.getText();
  }

  public void setSearchString ( String search_string ) {
    searchField.setText( search_string );
  }

  // Selected Attribute ////////////////////////////////
  
  public String getSelectedAttribute () {
    return ( String )attributeBox.getSelectedItem();
  }

  public void setSelectedAttribute ( String new_attr ) {
    attributeBox.setSelectedItem( new_attr );
  }

		public Class getSelectedClass(){
						return (Class)classBox.getSelectedItem();
		}

		public void setSelectedClass(Class newClass){
						classBox.setSelectedItem(newClass);
		}

  public void actionPerformed ( ActionEvent e ) {
    if ( e.getSource() == searchField ) {
      fireSearchStringChanged();
    } else if ( e.getSource() == nameField ) {
      fireFilterNameChanged();
    } else if ( e.getSource() == attributeBox ) {
      fireAttributeChanged();
    } else if( e.getSource() == classBox ){
								fireClassChanged();
				}
  }

  public void fireSearchStringChanged () {
    pcs.firePropertyChange( StringPatternFilter.SEARCH_STRING_EVENT, null, getSearchString() );
  }

  public void fireFilterNameChanged () {
    pcs.firePropertyChange( StringPatternFilter.FILTER_NAME_EVENT, null, nameField.getText() );
  }
 
		public void fireClassChanged(){
						Class type = (Class)classBox.getSelectedItem();
						updateAttributeBox(type);
						pcs.firePropertyChange( StringPatternFilter.CLASS_TYPE_EVENT,null, classBox.getSelectedItem());
		
		}
  public void fireAttributeChanged () {
    String new_attr = getSelectedAttribute();
				//if(new_attr != null && !new_attr.equals("")){
						//		searchBox.setModel( new DefaultComboBoxModel( objectAttributes.getUniqueValues( new_attr ) ) );
				//}
				pcs.firePropertyChange( StringPatternFilter.SELECTED_ATTRIBUTE_EVENT, null, getSelectedAttribute() );
  }

		public void updateAttributeBox(Class type){
						if(type.equals(NODE_CLASS)){
										objectAttributes = network.getNodeAttributes();
						}
						else{
										objectAttributes = network.getEdgeAttributes();
						}
						String [] attributeNames = objectAttributes.getAttributeNames();
						Vector stringAttributes = new Vector();
						for(int idx=0;idx<attributeNames.length;idx++){
										if(STRING_CLASS.isAssignableFrom(objectAttributes.getClass(attributeNames[idx]))){
														stringAttributes.add(attributeNames[idx]);
										}	
						}
						attributeBox.removeAllItems();
						Iterator attrIt = stringAttributes.iterator();
						while(attrIt.hasNext()){
										attributeBox.addItem(attrIt.next());
						}
						if(attributeBox.getItemCount() != 0){
										attributeBox.setSelectedIndex(0);
						}
						fireAttributeChanged();
	
		}
  public void setDefaults () {
    setSelectedAttribute( DEFAULT_SELECTED_ATTRIBUTE );
    setSearchString( DEFAULT_SEARCH_STRING );
    setFilterName( DEFAULT_FILTER_NAME );
				setSelectedClass(DEFAULT_CLASS); 
		}

  public void readInFilter () {
    RESET_SEARCH_STRING = filter.getSearchString();
    RESET_FITLER_NAME = filter.toString();
    RESET_SELECTED_ATTRIBUTE = filter.getSelectedAttribute();
    RESET_CLASS = NODE_CLASS;
				resetFilter();
  }

  public void resetFilter () {
    setSelectedAttribute( RESET_SELECTED_ATTRIBUTE );
    setSearchString( RESET_SEARCH_STRING );
    setFilterName( RESET_FITLER_NAME );
				setSelectedClass(RESET_CLASS);
    fireFilterNameChanged();
  }
}
