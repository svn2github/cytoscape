package cytoscape.dialogs;

import edu.umd.cs.piccolox.event.PNotificationCenter;
import edu.umd.cs.piccolox.event.PNotification;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

import java.util.*;
import java.awt.*;

import ViolinStrings.Strings;

import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.*;

import giny.model.*;
import giny.view.*;
import phoebe.*;

public class GraphObjectSelection extends JPanel implements ActionListener {

  JTextField searchField;
  JCheckBox regexpSearch, clearSelection;
  JList selectedAttributes;
  JList allAttributes;
  CyNetwork cyNetwork;
  GraphObjAttributes nodeAttributes;
  GraphObjAttributes edgeAttributes;
  PGraphView graphView;
  NetworkView networkView;

  public GraphObjectSelection ( NetworkView networkView  ) {
    this.networkView = networkView;
    this.cyNetwork = networkView.getNetwork();
    
    this.nodeAttributes = cyNetwork.getNodeAttributes();
    this.edgeAttributes = cyNetwork.getEdgeAttributes();
    initialize();
  }

  protected void initialize () {

    // Create the Node Selection Panel
    JPanel searchPanel = new JPanel();
    searchPanel.setBorder( new TitledBorder( "Search to Select" ) );
    searchPanel.add( new JLabel( "Attribute Search: " ) );
    searchField = new JTextField( 30 );
    searchField.addActionListener( this );
    searchPanel.add( searchField );
    regexpSearch = new JCheckBox( "Regexp?" );
    searchPanel.add( regexpSearch );
    searchPanel.add( new JButton (new AbstractAction( "Go!" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  // do the search
                  performSearch();
                }
              } ); } } ) );

    // Create the All Attributes List Panel
    JPanel allAttributesPanel = new JPanel();
    allAttributesPanel.setBorder( new TitledBorder( "All Available String Attributes" ) );


    String[] node_attribute_names = nodeAttributes.getAttributeNames();
    ArrayList string_attributes = new ArrayList( node_attribute_names.length );
    ArrayList number_attributes = new ArrayList( node_attribute_names.length );
    ArrayList other_attributes = new ArrayList( node_attribute_names.length );

    for ( int i = 0; i < node_attribute_names.length; ++i ) {
      Class type = nodeAttributes.deduceClass( node_attribute_names[i] );

      System.out.println( "Attr: "+node_attribute_names[i]+" Class: "+type.getName() );


      if ( type.getName().equals( String.class.getName() ) ) {
        string_attributes.add( node_attribute_names[i] );
      } else if (  type.getName().equals( Double.class.getName() ) ) {
        number_attributes.add( node_attribute_names[i] );
      } else {
        other_attributes.add( node_attribute_names[i] );
      }
    }

    DefaultListModel model_nodes = new DefaultListModel();
    Iterator string_atts = string_attributes.iterator();
    for (  int i = 0; string_atts.hasNext(); ++i  ) {
      model_nodes.add( i, ( String )string_atts.next() );
    }
    allAttributes = new JList( model_nodes );
    JScrollPane scrollPaneAll = new JScrollPane();
    scrollPaneAll.getViewport().setView( allAttributes );
    allAttributesPanel.add( scrollPaneAll );

    // Create the Selection Panel
    JPanel selectedAttributesPanel = new JPanel();
    selectedAttributesPanel.setBorder( new TitledBorder( "Selected Attributes" ) );
    selectedAttributes = new JList( new DefaultListModel() );
    JScrollPane scrollPaneSel = new JScrollPane();
    scrollPaneSel.getViewport().setView( selectedAttributes );
    allAttributesPanel.add( scrollPaneSel );
    selectedAttributesPanel.add( scrollPaneSel );

    // Create the Center Panel
    JPanel centerPanel = new JPanel();
    centerPanel.setBorder( new TitledBorder( "Control" ) );
    centerPanel.add(  new JButton (new AbstractAction( "+" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  // Add the Attributes selected in the all attributes list to the selectionlist
                  
                  // get the selected attributes
                  Object[] attributes = allAttributes.getSelectedValues();
                
                  // add them to the selection list
                  Set current_selection = new TreeSet( java.util.Arrays.asList( ( ( DefaultListModel )selectedAttributes.getModel() ).toArray() ) );
                  current_selection.addAll( java.util.Arrays.asList(  attributes ) );
                  DefaultListModel new_model = new DefaultListModel();
                  Iterator sel = current_selection.iterator();
                  while ( sel.hasNext() ){
                    new_model.addElement( sel.next() );
                  }
                  selectedAttributes.setModel( new_model );
                }
              } ); } } ) );
    centerPanel.add(  new JButton (new AbstractAction( "-" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  // Remove all Selected Attributes in the Selection List
                  Object[] attributes = selectedAttributes.getSelectedValues();
                  DefaultListModel dlm = ( DefaultListModel )selectedAttributes.getModel();
                  for ( int i = 0; i < attributes.length; ++i ) {
                    dlm.removeElement( attributes[i] );
                  }

                }
              } ); } } ) );
    centerPanel.add(  new JButton (new AbstractAction( "Update" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  // update the integration view
                  //integration.updateView();                
                  
                }
              } ); } } ) );
    
    clearSelection = new JCheckBox( "Clear", true );
    centerPanel.add( clearSelection );

    JSplitPane all_center_1 = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true,  centerPanel, allAttributesPanel  );
    JSplitPane sel = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true, selectedAttributesPanel, all_center_1 );
    JSplitPane all = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, searchPanel, sel );
    add( all );

  }

   public Object[] getSelectionList () {
    return ( ( DefaultListModel )selectedAttributes.getModel() ).toArray();
  }

   public void actionPerformed ( ActionEvent event ) {
     // when ENTER is pressed do a search
     this.graphView = networkView.getView();
    performSearch();    
    
    // update the view

  } // actionPerformed

  public void performSearch () {

    ArrayList passes = new ArrayList();
    Object[] selected_attributes_o = getSelectionList();
    String[] selected_attributes = new String[selected_attributes_o.length];
    for ( int i = 0; i<selected_attributes_o.length; ++i ) {
      selected_attributes[i] = ( String )selected_attributes_o[i];
    }


    if ( regexpSearch.isSelected() ) {
     
      System.out.println( "not Implemented" );

    } else {

      String[] pattern = searchField.getText().split("\\s");
      String[] nodes_with_attribute;
     
        for ( int i = 0; i < selected_attributes.length; ++i ) {
          
          nodes_with_attribute = nodeAttributes.getObjectNames( selected_attributes[i] );
          for ( int j =0; j < nodes_with_attribute.length; ++j ) {
            // get the String Value for the node for the given attribute
            String value = nodeAttributes.getStringValue( selected_attributes[i], 
                                                          nodes_with_attribute[j] );
        
            for ( int p = 0; p < pattern.length; ++p ) 
              if ( Strings.isLike( value, pattern[p], 0, true ) ) {
                // this means that:
                // the value, which is associated with a given selected attribute
                //     and a node name is like one of the strings in the search
                //     box, which includes wildcards.
                //passes.add( graphView.getNodeView( nodes_with_attribute[j] ) );
                //System.out.println( nodes_with_attribute[j]+" Matches Pattern: "+pattern[p]+" on Attribute: "+selected_attributes[i]+" and the object is a: "+ ( nodeAttributes.getGraphObject( nodes_with_attribute[j]) ).getClass().getName() );
                //passes.add( graphView.getNodeView( ( Node )nodeAttributes.getGraphObject( nodes_with_attribute[j]) ) );
                //System.out.println( "This got added to Passed: "+graphView.getNodeView( ( Node )nodeAttributes.getGraphObject( nodes_with_attribute[j]) ) );
                passes.add( nodes_with_attribute[j] );

              }
          }
        }
    }

//     Iterator views = networkView.getView().getNodeViewsList().iterator();
//     while ( views.hasNext() ) {
//       System.out.println( "On Crack: "+views.next() );
//     }


    // restore all EdgeViews prior to hiding
    networkView.getView().showGraphObjects( networkView.getView().getEdgeViewsList() );

    Iterator all_nodes = networkView.getView().getGraphPerspective().nodesList().iterator();
    while ( all_nodes.hasNext() ) {
      Node node =  ( Node )all_nodes.next();
      
      //System.out.println( "ALL NODES: "+ nv.toString() );

      if ( passes.contains( node.getIdentifier() ) ) {
        //graphView.showGraphObject( nv );
        //System.out.println( "PASSES: "+node.getIdentifier() );
        //System.out.println( "PASSES: "+node );
        //NodeView nv =  networkView.getView().getNodeView( node );
        //System.out.println( "Will Show: "+nv );
        //graphView.showGraphObject( networkView.getView().getNodeView( node.getRootGraphIndex() ) );
        //networkView.getView().showGraphObject( nv );
        networkView.getView().showNodeView( networkView.getView().getNodeView( node ), false );
      } else {
        //graphView.hideGraphObject( nv );
        //System.out.println( "        NOPE: "+node.getIdentifier()+ " : "+node.getRootGraphIndex()+" : "+networkView.getView().getGraphPerspective().getIndex( node ) );
        //System.out.println( "        NOPE: "+node.getIdentifier() );
        //NodeView nv =  networkView.getView().getNodeView( node );
        //System.out.println( "        Will Hide: "+nv );
        //networkView.getView().hideGraphObject( nv );
        networkView.getView().hideNodeView(  networkView.getView().getNodeView( node ) );
      }
    }
    

  //   Iterator sel;
//     DefaultListModel new_model;
//     if ( clearSelection.isSelected() ) {
//       sel = passes.iterator();
//     } else {
            
//       // add them to the selection list
//       Set current_selection = new TreeSet( java.util.Arrays.asList( ( ( DefaultListModel )selectedAttributes.getModel() ).toArray() ) );
//       current_selection.addAll( passes );
      
//       sel = current_selection.iterator();
//     }
//     new_model = new DefaultListModel();
//     while ( sel.hasNext() ){
//       new_model.addElement( sel.next() );
//     }
//     selectedAttributes.setModel( new_model );
    
  }

}
