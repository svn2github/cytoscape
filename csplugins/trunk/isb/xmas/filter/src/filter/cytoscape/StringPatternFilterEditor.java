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

		protected CyWindow cyWindow;
  protected String DEFAULT_SEARCH_STRING = "";
  protected String RESET_SEARCH_STRING;

  protected String DEFAULT_FILTER_NAME = "Regex: ";
  protected String RESET_FITLER_NAME;

  protected String DEFAULT_SELECTED_ATTRIBUTE = "";
  protected String RESET_SELECTED_ATTRIBUTE;

		protected String RESET_CLASS;
		protected Class NODE_CLASS;
		protected Class EDGE_CLASS;
		protected Class STRING_CLASS;
  protected String DEFAULT_CLASS = StringPatternFilter.NODE; 

  public StringPatternFilterEditor ( CyWindow cyWindow ) {
    super();
    this.cyWindow = cyWindow;
				try{
								STRING_CLASS = Class.forName("java.lang.String");
								NODE_CLASS = Class.forName("giny.model.Node");
								EDGE_CLASS = Class.forName("giny.model.Edge");
				}catch(Exception e){
								e.printStackTrace();
				}
				
				
				setLayout(new BorderLayout());
    identifier = "String Filter";
    setBorder( new TitledBorder( "String Filter - Select nodes or edges based on regular expression match to text attribute" ) );

    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
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
    bottomPanel.add(searchField);

				all_panel.add(topPanel);
				all_panel.add(middlePanel);
				all_panel.add(bottomPanel);
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
                */ 
																				updateAttributeBox();
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

    String search_item = getSearchString();
    String attr_item = getSelectedAttribute();
				String currentClass = getSelectedClass(); 
    if ( currentClass == null || search_item == null || attr_item == null || nameField.getText() == null ) {
      return null;
    }
    return new StringPatternFilter( cyWindow, currentClass, attr_item, search_item, nameField.getText() );
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
						if(attributeBox.getItemCount() ==0){
										return null;
						}
						return ( String )attributeBox.getSelectedItem();
  }

  public void setSelectedAttribute ( String new_attr ) {
						updateAttributeBox();
						attributeBox.setSelectedItem( new_attr );
  }

		public String getSelectedClass(){
						return (String)classBox.getSelectedItem();
		}

		public void setSelectedClass(String newClass){
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
						updateAttributeBox();
						pcs.firePropertyChange( StringPatternFilter.CLASS_TYPE_EVENT,null, getSelectedClass());
		
		}
  public void fireAttributeChanged () {
    String new_attr = getSelectedAttribute();
				//if(new_attr != null && !new_attr.equals("")){
						//		searchBox.setModel( new DefaultComboBoxModel( objectAttributes.getUniqueValues( new_attr ) ) );
				//}
				pcs.firePropertyChange( StringPatternFilter.SELECTED_ATTRIBUTE_EVENT, null, getSelectedAttribute() );
  }

		public void updateAttributeBox(){
						GraphObjAttributes objectAttributes = null;
						String type = getSelectedClass();
						if(type.equals(StringPatternFilter.NODE)){
										objectAttributes = cyWindow.getNetwork().getNodeAttributes();
						}
						else{
										objectAttributes = cyWindow.getNetwork().getEdgeAttributes();
						}
						String [] attributeNames = objectAttributes.getAttributeNames();
            Arrays.sort( attributeNames );
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
    setSearchString( DEFAULT_SEARCH_STRING );
    setFilterName( DEFAULT_FILTER_NAME );
				setSelectedClass(DEFAULT_CLASS); 
    setSelectedAttribute( DEFAULT_SELECTED_ATTRIBUTE );
		}

  public void readInFilter () {
    RESET_SEARCH_STRING = filter.getSearchString();
    RESET_FITLER_NAME = filter.toString();
    RESET_SELECTED_ATTRIBUTE = filter.getSelectedAttribute();
    RESET_CLASS = filter.getClassType();
				resetFilter();
  }

  public void resetFilter () {
    setSearchString( RESET_SEARCH_STRING );
    setFilterName( RESET_FITLER_NAME );
				setSelectedClass(RESET_CLASS);
    setSelectedAttribute( RESET_SELECTED_ATTRIBUTE );
				fireFilterNameChanged();
  }
}
