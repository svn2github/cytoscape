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
import cytoscape.CyNetwork;
import filter.view.*;
import filter.model.*;

import ViolinStrings.Strings;

/**
 * This filter will do any sort of search on any attribute for all
 * objects.  It will support String wild card searches, numerical
 * inequalities, and numerical comparisons.
 */

public class CsAttributeValueFilterEditor
  extends FilterEditor 
  implements ActionListener,
             PropertyChangeListener,
             ListSelectionListener {

  /**
   * This is the Name that will go in the Tab 
   * and is returned by the "toString" method
   */
  protected String identifier;
   protected JTextField nameField;

  protected JComboBox defaultCompareBox;
  protected JComboBox defaultSearchBox;

  protected JComboBox firstCompareBox;
  protected JComboBox firstSearchBox;
  
  protected JComboBox secondCompareBox;
  protected JComboBox secondSearchBox;

 
  protected DefaultListModel attributeModel;
  protected JList attributeList;
  protected JTextField attributeSearchField;
  protected JCheckBox multiSelection;

  protected JCheckBox rangeToggle;

  protected CsAttributeValueFilter filter;

  protected CyNetwork network;
  protected GraphObjAttributes nodeAttributes;
  protected GraphObjAttributes edgeAttributes;

  protected String DEFAULT_FIRST_SEARCH_STRING = "";
  protected String RESET_FIRST_SEARCH_STRING;

  protected Double DEFAULT_SECOND_SEARCH_STRING = new Double(0);
  protected Double RESET_SECOND_SEARCH_STRING;

  protected boolean DEFAULT_BETWEEN = false;
  protected boolean RESET_BETWEEN;

  protected String DEFAULT_FIRST_COMPARE = CsAttributeValueFilter.LE;
  protected String RESET_FIRST_COMPARE;

  protected String DEFAULT_SECOND_COMPARE = CsAttributeValueFilter.LE;
  protected String RESET_SECOND_COMPARE;


  protected String DEFAULT_FILTER_NAME = "AttVal: ";
  protected String RESET_FILTER_NAME;

  protected String[] DEFAULT_SELECTED_ATTRIBUTES = new String[]{""};
  protected String[] RESET_SELECTED_ATTRIBUTES;

  protected JPanel searchPanel;
  protected JPanel attributePanel;
  protected JPanel defaultSearchPanel;
  protected JPanel firstSearchPanel;
  protected JPanel secondSearchPanel;

  public CsAttributeValueFilterEditor ( CyNetwork network ) {
    super();
    this.network = network;
    this.nodeAttributes = network.getNodeAttributes();
    this.edgeAttributes = network.getEdgeAttributes();

    // set up the border and tab name
    identifier = "Attribute Value";
    setBorder( new TitledBorder( "Attribute Value Filter" ) );
    setLayout( new BorderLayout() );

    // this is the static panel at the top 
    JPanel namePanel = new JPanel();
    namePanel.add( new JButton (new AbstractAction( "Update" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  updateAttributeModel();
                }
              } ); } } ) );
    nameField = new JTextField(15);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
    rangeToggle = new JCheckBox( "Ranged Selection", true );
    namePanel.add( rangeToggle );
    rangeToggle.addActionListener( this );
    //updateRangeToggle();
    add( namePanel, BorderLayout.NORTH );


    // this is the center panel that will change if 
    // we go from ranged to not ranged
    searchPanel = new JPanel();
    add( searchPanel, BorderLayout.CENTER );


    // Attributes //////////////////////////////
    attributePanel = new JPanel();
    attributeList = new JList();
    attributeList.addListSelectionListener( this );
    attributeSearchField = new JTextField(10);
    attributeSearchField.addActionListener( this );
    JScrollPane scoll = new JScrollPane( attributeList );
    multiSelection = new JCheckBox("Multi", true );
    multiSelection.addActionListener( this );
    attributeList.addPropertyChangeListener( this );
    attributePanel.setLayout( new BorderLayout() );
    attributePanel.add( scoll, BorderLayout.CENTER );
    attributePanel.add( attributeSearchField, BorderLayout.NORTH );
    attributePanel.add( multiSelection, BorderLayout.SOUTH );

    // Default Search /////////////////////////
    defaultSearchPanel = new JPanel();
    defaultCompareBox = new JComboBox( new Object[] { CsAttributeValueFilter.EQ,
                                                      CsAttributeValueFilter.NE,
                                                      CsAttributeValueFilter.GT,
                                                      CsAttributeValueFilter.GE,
                                                      CsAttributeValueFilter.LT,
                                                      CsAttributeValueFilter.LE } );
    defaultCompareBox.setSelectedItem( CsAttributeValueFilter.EQ );
    defaultSearchPanel.add( defaultCompareBox );
    defaultSearchBox = new JComboBox();
    defaultSearchBox.setEditable( true );
    defaultSearchPanel.add( defaultSearchBox );

    
    // First Search /////////////////////////
    firstSearchPanel = new JPanel();
    firstSearchBox = new JComboBox();
    firstSearchBox.setEditable( true );
    firstSearchPanel.add( firstSearchBox );
    firstCompareBox = new JComboBox( new Object[] {   CsAttributeValueFilter.LT,
                                                      CsAttributeValueFilter.LE } );
    firstCompareBox.setSelectedItem( DEFAULT_FIRST_COMPARE );
    firstSearchPanel.add( firstCompareBox );
    
    
    // Second Search /////////////////////////
    secondSearchPanel = new JPanel();
    secondCompareBox = new JComboBox( new Object[] {   CsAttributeValueFilter.LT,
                                                      CsAttributeValueFilter.LE } );
    secondCompareBox.setSelectedItem( DEFAULT_SECOND_COMPARE );
    secondSearchPanel.add( secondCompareBox );
    secondSearchBox = new JComboBox();
    secondSearchBox.setEditable( true );
    secondSearchPanel.add( secondSearchBox );


    searchPanel.add( attributePanel );
    searchPanel.add( defaultSearchPanel );

    
    updateAttributeModel();
    setDefaults();
    validate();

  }

  //----------------------------------------//
  // Implements Filter Editor
  //----------------------------------------//

  public String toString () {
    return identifier;
  }

  public String getFilterID () {
    return CsAttributeValueFilter.FILTER_ID;
  }

  /** 
   * Returns a new Filter, or the Modified Filter 
   */
  public Filter getFilter() {

    String[] selected_attributes = getSelectedAttributes();
    String first_search = getFirstSearch();
    Double second_search = getSecondSearch();
    String first_compare = getFirstCompare();
    String second_compare = getSecondCompare();
    boolean between = rangeToggle.isSelected();
    
    if ( between ) {
      // this is a ranged filter
      if ( first_search == null || second_search == null ||
           first_compare == null || second_compare == null ||
           selected_attributes == null ) {
        return null;
      }
     
    } else {
      // this is not a ranged filter
      if ( first_search == null || first_compare == null ||
           selected_attributes == null ) {
        return null;
      }
    }
    return new CsAttributeValueFilter( network,
                                       getFilterName(),
                                       selected_attributes,
                                       first_search,
                                       second_search,
                                       first_compare,
                                       second_compare,
                                       between );
  }


  /**
   * Accepts a Filter for editing
   * Note that this Filter must be a Filter that can be edited
   * by this Filter editor. 
   */
  public void editFilter ( Filter filter ) {
    if ( filter instanceof CsAttributeValueFilter ) {
      // good, this Filter is of the right type
      getSwingPropertyChangeSupport().removePropertyChangeListener( this.filter );
      this.filter = ( CsAttributeValueFilter )filter;
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
  // CsAttributeValueFilter Methods
  //----------------------------------------//

  // There should be getter and setter methods for
  // every editable property that the Filter needs to
  // to find out from the Editor. 

  // Filter Name ///////////////////////////////////////

  public String getFilterName () {
    return nameField.getText();
  }

  public void setFilterName ( String name ) {
    nameField.setText( name );
  }

  // FirstSearch //////////////////////////
  
  public String getFirstSearch () {
    if ( rangeToggle.isSelected() ) {
      return ( String )firstSearchBox.getSelectedItem();
    }
    return ( String )defaultSearchBox.getSelectedItem();
  }

  public void setFirstSearch ( String search ) {
    firstSearchBox.setSelectedItem( search );
    defaultSearchBox.setSelectedItem( search );
  }

   // SecondSearch ////////////////////////
  
  public Double getSecondSearch () {
    try {
      return new Double( ( String )secondSearchBox.getSelectedItem() );
    } catch ( Exception e ) {
      return new Double( Double.NaN );
    } 
  }

  public void setSecondSearch ( Double search ) {
    secondSearchBox.setSelectedItem( search.toString() );
  }

  // First Compare ///////////////////////
  
  public String getFirstCompare () {
    if ( rangeToggle.isSelected() ) {
      return ( String )firstCompareBox.getSelectedItem();
    }
    return ( String )defaultCompareBox.getSelectedItem();
  }

  public void setFirstCompare ( String comp ) {
    firstCompareBox.setSelectedItem( comp );
    defaultCompareBox.setSelectedItem( comp );
  }

  // Second Compare ///////////////////////
  
  public String getSecondCompare () {
    return ( String )secondCompareBox.getSelectedItem();
  }

  public void setSecondCompare ( String comp ) {
    secondCompareBox.setSelectedItem( comp );
  }

  // Between /////////////////////////////

  public boolean getBetween () {
    return rangeToggle.isSelected();
  }

  public void setBetween ( Boolean bool ) {
    rangeToggle.setSelected( bool.booleanValue() );
  }

  public void setBetween ( boolean bool ) {
    rangeToggle.setSelected( bool );
  }

  // Selected_Attributes ///////////////

  public String[] getSelectedAttributes () {
    Object[] sel = attributeList.getSelectedValues();
    String[] ret = new String[sel.length];
    for ( int i = 0; i < ret.length; ++i ) {
      ret[i] = ( String )sel[i];
    }
    return ret;
  }

  public void setSelectedAttributes ( String[] atts ) {
    int[] indices = new int[ atts.length ];
    for ( int i = 0; i < atts.length; ++i ) {
      indices[i] = attributeModel.indexOf( atts[i] );
    }
    attributeList.setSelectedIndices( indices );
  }


  public void setDefaults () {
    setSelectedAttributes( DEFAULT_SELECTED_ATTRIBUTES );
    setFilterName( DEFAULT_FILTER_NAME );
    setFirstSearch( DEFAULT_FIRST_SEARCH_STRING );
    setSecondSearch( DEFAULT_SECOND_SEARCH_STRING );
    setFirstCompare( DEFAULT_FIRST_COMPARE );
    setSecondCompare( DEFAULT_SECOND_COMPARE );
    setBetween( DEFAULT_BETWEEN );
    rangeToggle.setSelected( DEFAULT_BETWEEN );
    updateRangeToggle();
  }

  public void readInFilter () {
    RESET_FILTER_NAME = filter.toString();
    RESET_SELECTED_ATTRIBUTES = filter.getSelectedAttributes();
    RESET_FIRST_SEARCH_STRING = filter.getFirstSearch();
    RESET_SECOND_SEARCH_STRING = filter.getSecondSearch();
    RESET_BETWEEN = filter.getBetween();
    RESET_FIRST_COMPARE = filter.getFirstCompare();
    RESET_SECOND_COMPARE = filter.getSecondCompare();
    resetFilter();
  }

  public void resetFilter () {
    setSelectedAttributes( RESET_SELECTED_ATTRIBUTES );
    setFilterName( RESET_FILTER_NAME );
    setFirstSearch( RESET_FIRST_SEARCH_STRING );
    setSecondSearch( RESET_SECOND_SEARCH_STRING );
    setFirstCompare( RESET_FIRST_COMPARE );
    setSecondCompare( RESET_SECOND_COMPARE );
    setBetween( RESET_BETWEEN );
    rangeToggle.setSelected( RESET_BETWEEN );
    updateRangeToggle();
  }


  //----------------------------------------//
  // CsAttributeValueFilter Methods
  //----------------------------------------//

   
  public void valueChanged ( ListSelectionEvent e ) {

    if ( e.getSource() == attributeList ) {
       String[] sel_atts = getSelectedAttributes();
       
       if ( sel_atts.length == 0 ) 
         return;

       System.out.println( "Attribute: "+sel_atts[0] );

       Object[] node_values = nodeAttributes.getUniqueValues( sel_atts[0] );
       Object[] edge_values = edgeAttributes.getUniqueValues( sel_atts[0] );
       
       if ( node_values == null ) {
         System.out.println( "node values returned null" );
         node_values = new Object[0];
       }

       if ( edge_values == null ) {
         System.out.println( "edge values returned null" );
        edge_values = new Object[0];
       }
    
       Object[] new_values = new Object[ node_values.length + edge_values.length ];
       System.arraycopy( node_values, 0, new_values, 0, node_values.length );
       System.arraycopy( edge_values, 0, new_values, node_values.length, edge_values.length );
       Object[] values = new_values;

   
       for ( int i = 1; i < sel_atts.length; ++i ) {
         node_values = nodeAttributes.getUniqueValues( sel_atts[i] );
         edge_values = edgeAttributes.getUniqueValues( sel_atts[i] );
         if ( node_values == null ) {
           System.out.println( "node values returned null" );
           node_values = new Object[0];
         }
         if ( edge_values == null ) {
           System.out.println( "edge values returned null" );
           edge_values = new Object[0];
         }

         new_values = new Object[values.length + node_values.length + edge_values.length ];
         System.arraycopy( values, 0, new_values, 0, values.length );
         System.arraycopy( node_values, 0, new_values, values.length, node_values.length );
         System.arraycopy( edge_values, 0, new_values, node_values.length + values.length , edge_values.length );
         values = new_values;
       }
       if ( values == null ) 
         return;
       
      //  System.out.println( "Values leunght: "+values.length );
//        for ( int i = 0; i < values.length; ++i ) {
//          System.out.println( i+" : "+values[i] );
//        }

       Arrays.sort( values );

       defaultSearchBox.setModel(  new DefaultComboBoxModel( values ) );
       firstSearchBox.setModel(  new DefaultComboBoxModel( values ) );
       secondSearchBox.setModel(  new DefaultComboBoxModel( values ) );
    }
  }

  public void propertyChange ( PropertyChangeEvent e ) {
    

  }



  public void actionPerformed ( ActionEvent e ) {
    if ( e.getSource() == rangeToggle ) {
      updateRangeToggle();
    } else if ( e.getSource() == attributeSearchField ) {
      searchAttributes();
    } else if ( e.getSource() == multiSelection ) {
      switchMultiSelection();
    }
  }

  protected void searchAttributes () {
    ArrayList attributes_pass = new ArrayList();
    Enumeration att_enum = attributeModel.elements();
    String[] pattern = attributeSearchField.getText().split("\\s");
    while ( att_enum.hasMoreElements() ) {
      String s = ( String )att_enum.nextElement();
      for ( int p = 0; p < pattern.length; ++p ) {
        if ( Strings.isLike( s.toString(), pattern[p], 0, true ) ) {
          attributes_pass.add( s );
          System.out.println( "Attribute: "+s.toString()+" matches "+pattern[p] );
        }
      }
    }
    Iterator api = attributes_pass.iterator();
    DefaultListModel alm = new DefaultListModel();
    while ( api.hasNext() ) {
      alm.addElement( api.next() );
    }
    attributeList.setModel( alm );
  }


  protected void switchMultiSelection () {
    if ( multiSelection.isSelected() ) {
      attributeList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
    } else {
      attributeList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    }
  }

  protected void updateAttributeModel () {
   attributeModel = new DefaultListModel();
   String[] natts = nodeAttributes.getAttributeNames();
   String[] eatts = edgeAttributes.getAttributeNames();

   for ( int i = 0; i < natts.length; ++i ) {
     attributeModel.addElement( natts[i] );
   }
   for ( int i = 0; i < eatts.length; ++i ) {
     attributeModel.addElement( eatts[i] );
   }
   
   attributeList.setModel( attributeModel );
   
  }



  protected void updateRangeToggle () {
    if ( rangeToggle.isSelected() ) {
      searchPanel.removeAll();
      searchPanel.add( firstSearchPanel );
      searchPanel.add( attributePanel );
      searchPanel.add( secondSearchPanel );
      validate();
      repaint();
    } else {
      searchPanel.removeAll();
      searchPanel.add( attributePanel );
      searchPanel.add( defaultSearchPanel );
      validate();
      repaint();
    }
  }


}
