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
 * This filter will pass nodes based on the edges that 
 * they have.
 */

public class CsNodeInteractionFilterEditor 
  extends FilterEditor
  implements ActionListener {

   /**
   * This is the Name that will go in the Tab 
   * and is returned by the "toString" method
   */
  protected String identifier;

  protected JTextField nameField;
  protected JComboBox searchBox;
  protected JComboBox edgeAttributeBox;
  protected JRadioButton sourceNodeButton;
  protected JRadioButton targetNodeButton;
  protected JRadioButton bothNodesButton;

  protected String searchString;
  protected String selectedEdgeAttribute;
  protected boolean sourceNode;
  protected boolean targetNode;
  
  protected CsNodeInteractionFilter filter;

  protected CyNetwork network;
  protected GraphObjAttributes edgeAttributes;

  protected String DEFAULT_SEARCH_STRING = "";
  protected String RESET_SEARCH_STRING;

  protected String DEFAULT_FILTER_NAME = "NodeInteraction: ";
  protected String RESET_FITLER_NAME;

  protected String DEFAULT_SELECTED_EDGE_ATTRIBUTE = "";
  protected String RESET_SELECTED_EDGE_ATTRIBUTE;

  protected boolean DEFAULT_SOURCE_NODE = true;
  protected boolean RESET_SOURCE_NODE;

  protected boolean DEFAULT_TARGET_NODE = true;
  protected boolean RESET_TARGET_NODE;

  public CsNodeInteractionFilterEditor ( CyNetwork network ) {
    super();
    this.network = network;
    this.edgeAttributes = network.getEdgeAttributes();

    identifier = "Node Interactions";
    setBorder( new TitledBorder( "Node Interaction Based on Edges Filter" ) );
    setLayout( new BorderLayout() );

    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
    namePanel.add(  new JButton (new AbstractAction( "Update" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String[] atts = edgeAttributes.getAttributeNames();
                  System.out.println( "There are: "+atts.length+" attributes." );
                  for ( int i = 0; i < atts.length; ++i ) {
                    System.out.println( i+". "+atts[i] );
                  }
                  edgeAttributeBox.setModel( new DefaultComboBoxModel( edgeAttributes.getAttributeNames() ) );
                  //( ( DefaultComboBoxModel )attributeBox.getModel() ).addElement( "canonicalName" );
                }
              } ); } } ) );
    add( namePanel, BorderLayout.NORTH  );

    JPanel attribute_panel = new JPanel();
    edgeAttributeBox = new JComboBox( edgeAttributes.getAttributeNames() );
    edgeAttributeBox.setEditable( false );
    edgeAttributeBox.addActionListener( this );
    attribute_panel.add( edgeAttributeBox );
      
    searchBox = new JComboBox();
    searchBox.setEditable( true );
    searchBox.addActionListener( this );
    attribute_panel.add( searchBox );
    add( attribute_panel, BorderLayout.CENTER  );


    JPanel end_panel = new JPanel();
    sourceNodeButton = new JRadioButton( "source" );
    targetNodeButton = new JRadioButton( "target" );
    bothNodesButton = new JRadioButton( "both" );
    ButtonGroup group = new ButtonGroup();
    group.add( sourceNodeButton );
    group.add( targetNodeButton );
    group.add( bothNodesButton );
    end_panel.add( sourceNodeButton );
    end_panel.add( targetNodeButton );
    end_panel.add( bothNodesButton );
    sourceNodeButton.addActionListener( this );
    targetNodeButton.addActionListener( this );
    bothNodesButton.addActionListener( this );
    attribute_panel.add( end_panel, BorderLayout.SOUTH );
               

    setDefaults();

  }

  //----------------------------------------//
  // Implements Filter Editor
  //----------------------------------------//

  public String toString () {
    return identifier;
  }

  public String getFilterID () {
    return CsNodeInteractionFilter.FILTER_ID;
  }

  /** 
   * Returns a new Filter, or the Modified Filter 
   */
  public Filter getFilter() {

    String search_item = ( String )searchBox.getSelectedItem();
    String attr_item = ( String )edgeAttributeBox.getSelectedItem();
    boolean source = sourceNodeButton.isSelected();
    boolean target = targetNodeButton.isSelected();
    boolean both = bothNodesButton.isSelected();

    if ( search_item == null || attr_item == null || nameField.getText() == null ) {
      return null;
    }
    if ( both ) {
      return new CsNodeInteractionFilter( network, attr_item, search_item, true, true, nameField.getText() );
    } else {
      return new CsNodeInteractionFilter( network, attr_item, search_item, source, target, nameField.getText() );
    }
  }

  /**
   * Accepts a Filter for editing
   * Note that this Filter must be a Filter that can be edited
   * by this Filter editor. 
   */
  public void editFilter ( Filter filter ) {
    if ( filter instanceof CsNodeInteractionFilter ) {
      // good, this Filter is of the right type
      getSwingPropertyChangeSupport().removePropertyChangeListener( this.filter );
      this.filter = ( CsNodeInteractionFilter )filter;
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
  // CsNodeTypeFilter Methods
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
    return ( String )searchBox.getSelectedItem();
  }

  public void setSearchString ( String search_string ) {
    searchBox.setSelectedItem( search_string );
  }

  // Selected Edge Attribute ////////////////////////////
  
  public String getSelectedEdgeAttribute () {
    return ( String )edgeAttributeBox.getSelectedItem();
  }

  public void setSelectedEdgeAttribute ( String new_attr ) {
    edgeAttributeBox.setSelectedItem( new_attr );
  }

  // Source Node ////////////////////////////////////////
  
  public boolean getSourceNode () {
    if ( bothNodesButton.isSelected() || sourceNodeButton.isSelected() ) {
      return true;
    } 
    return false;
  }

  public void setSourceNode ( boolean new_val ) {
    if ( getTargetNode() && new_val ) {
      bothNodesButton.setSelected( true );
    } else if ( !getTargetNode() && new_val ) {
      sourceNodeButton.setSelected( true );
    } else {
      sourceNodeButton.setSelected( false );
    }
  }

  // Target Node ////////////////////////////////////////
  
  public boolean getTargetNode () {
    if ( bothNodesButton.isSelected() || targetNodeButton.isSelected() ) {
      return true;
    } 
    return false;
  }

  public void setTargetNode ( boolean new_val ) {
    if ( getTargetNode() && new_val ) {
      bothNodesButton.setSelected( true );
    } else if ( !getTargetNode() && new_val ) {
      targetNodeButton.setSelected( true );
    } else {
      targetNodeButton.setSelected( false );
    }
  }

  public void actionPerformed ( ActionEvent e ) {
    if ( e.getSource() == searchBox ) {
      fireSearchStringChanged();
    } else if ( e.getSource() == nameField ) {
      fireFilterNameChanged();
    } else if ( e.getSource() == edgeAttributeBox ) {
      fireEdgeAttributeChanged();
    } else if ( e.getSource() == sourceNodeButton ) {
      fireSourceNodeChanged();
    } else if ( e.getSource() == targetNodeButton ) {
      fireTargetNodeChanged();
    } else if ( e.getSource() == bothNodesButton ) {
      fireBothChanged();
    }
  }

  public void fireSearchStringChanged () {
    pcs.firePropertyChange( CsNodeInteractionFilter.SEARCH_STRING_EVENT, null, getSearchString() );
  }

  public void fireFilterNameChanged () {
    pcs.firePropertyChange( CsNodeInteractionFilter.FILTER_NAME_EVENT, null, nameField.getText() );
  }
  
  public void fireEdgeAttributeChanged () {
    String new_attr = getSelectedEdgeAttribute();
    searchBox.setModel( new DefaultComboBoxModel( edgeAttributes.getUniqueValues( new_attr ) ) );
    pcs.firePropertyChange( CsNodeInteractionFilter.SELECTED_EDGE_ATTRIBUTE_EVENT, null, getSelectedEdgeAttribute() );
  }
  
  public void fireSourceNodeChanged () {
    sourceNode = sourceNodeButton.isSelected();
    pcs.firePropertyChange( CsNodeInteractionFilter.SOURCE_NODE_EVENT, null, new Boolean( sourceNode ) );
  }

  public void fireTargetNodeChanged () {
    targetNode = targetNodeButton.isSelected();
    pcs.firePropertyChange( CsNodeInteractionFilter.TARGET_NODE_EVENT, null, new Boolean( targetNode ) );
  }
  
  public void fireBothChanged () {
    targetNode = true;
    sourceNode = true;
    pcs.firePropertyChange( CsNodeInteractionFilter.SOURCE_NODE_EVENT, null, new Boolean( sourceNode ) );
    pcs.firePropertyChange( CsNodeInteractionFilter.TARGET_NODE_EVENT, null, new Boolean( targetNode ) );
  }
  
   public void setDefaults () {
    setSelectedEdgeAttribute( DEFAULT_SELECTED_EDGE_ATTRIBUTE );
    setSearchString( DEFAULT_SEARCH_STRING );
    setFilterName( DEFAULT_FILTER_NAME );
    setSourceNode( DEFAULT_SOURCE_NODE );
    setTargetNode( DEFAULT_TARGET_NODE );

  }

  public void readInFilter () {
    RESET_SEARCH_STRING = filter.getSearchString();
    RESET_FITLER_NAME = filter.toString();
    RESET_SELECTED_EDGE_ATTRIBUTE = filter.getSelectedEdgeAttribute();
    RESET_SOURCE_NODE = filter.getSourceNode();
    RESET_TARGET_NODE = filter.getTargetNode();
    resetFilter();
  }

  public void resetFilter () {
    setSelectedEdgeAttribute( RESET_SELECTED_EDGE_ATTRIBUTE );
    setSearchString( RESET_SEARCH_STRING );
    setFilterName( RESET_FITLER_NAME );
    setSourceNode( RESET_SOURCE_NODE );
    setTargetNode( RESET_TARGET_NODE );
    fireFilterNameChanged();
  }

}
