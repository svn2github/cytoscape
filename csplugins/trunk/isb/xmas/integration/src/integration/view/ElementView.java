package integration.view;

import integration.data.*;
import integration.util.*;

import edu.umd.cs.piccolox.event.PNotificationCenter;
import edu.umd.cs.piccolox.event.PNotification;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

import java.util.*;
import java.awt.*;

import ViolinStrings.Strings;


public class ElementView extends JPanel implements ActionListener {


  Integration integration;
  DataCube dataCube;
  String name;
  int dimension;

  JTextField searchField;
  JCheckBox regexpSearch, clearSelection;
  JList selectedItems;
  JList allItems;

  

  public ElementView ( Integration integration, String name, int dim ) {
    super();
    this.integration = integration;
    this.dataCube = integration.getDataCube();
    this.name = name;
    this.dimension = dim;

    initialize( );

  }

  public void initialize () {

    // Create Search Panel
    JPanel searchPanel = new JPanel();
    searchPanel.setBorder( new TitledBorder( "Search to Select" ) );
    searchPanel.add( new JLabel( "Item Search: " ) );
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

    // Create the All Items List Panel
    JPanel allItemsPanel = new JPanel();
    allItemsPanel.setBorder( new TitledBorder( "All Available Items" ) );
    Object[] names;
    if ( dimension == Integration.SLICE ) 
     names = dataCube.getSliceNames().toArray();
    else if ( dimension == Integration.ROW ) 
     names = dataCube.getRowNames().toArray();
    else 
     names = dataCube.getColumnNames().toArray();
    DefaultListModel model_all = new DefaultListModel();
    for ( int i = 0; i < names.length; ++i ) {
      model_all.add( i, names[i] );
    }
    allItems = new JList( model_all );
    JScrollPane scrollPaneAll = new JScrollPane();
    scrollPaneAll.getViewport().setView( allItems );
    allItemsPanel.add( scrollPaneAll );


    // Create the Selection Panel
    JPanel selectedItemsPanel = new JPanel();
    selectedItemsPanel.setBorder( new TitledBorder( "Selected Items" ) );
    selectedItems = new JList( new DefaultListModel() );
    JScrollPane scrollPaneSel = new JScrollPane();
    scrollPaneSel.getViewport().setView( selectedItems );
    allItemsPanel.add( scrollPaneSel );
    selectedItemsPanel.add( scrollPaneSel );

    // Create the Center Panel
    JPanel centerPanel = new JPanel();
    centerPanel.setBorder( new TitledBorder( "Control" ) );
    centerPanel.add(  new JButton (new AbstractAction( "+" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  // Add the Items selected in the all items list to the selectionlist
                  
                  // get the selected items
                  Object[] items = allItems.getSelectedValues();
                
                  // add them to the selection list
                  Set current_selection = new TreeSet( java.util.Arrays.asList( ( ( DefaultListModel )selectedItems.getModel() ).toArray() ) );
                  current_selection.addAll( java.util.Arrays.asList(  items ) );
                  DefaultListModel new_model = new DefaultListModel();
                  Iterator sel = current_selection.iterator();
                  while ( sel.hasNext() ){
                    new_model.addElement( sel.next() );
                  }
                  selectedItems.setModel( new_model );
                }
              } ); } } ) );
    centerPanel.add(  new JButton (new AbstractAction( "-" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  // Remove all Selected Items in the Selection List
                  Object[] items = selectedItems.getSelectedValues();
                  DefaultListModel dlm = ( DefaultListModel )selectedItems.getModel();
                  for ( int i = 0; i < items.length; ++i ) {
                    dlm.removeElement( items[i] );
                  }

                }
              } ); } } ) );
    centerPanel.add(  new JButton (new AbstractAction( "Update" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  // update the integration view
                  integration.updateView();                

                }
              } ); } } ) );
    if ( dimension == Integration.COLUMN ) {
      clearSelection = new JCheckBox( "Clear", true );
    } else {
      clearSelection = new JCheckBox( "Clear", false );
    }
    centerPanel.add( clearSelection );



    if ( dimension == Integration.SLICE ) {
      JSplitPane all_center_1 = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true,  centerPanel, allItemsPanel  );
      JSplitPane sel = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, selectedItemsPanel, all_center_1 );
      JSplitPane all = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, searchPanel, sel );
      add( all );
    } else {
      JSplitPane all_center_1 = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true,  centerPanel, allItemsPanel  );
      JSplitPane sel = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true, selectedItemsPanel, all_center_1 );
      JSplitPane all = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, searchPanel, sel );
      add( all );
    }
 
  }

  public Object[] getSelectionList () {
    return ( ( DefaultListModel )selectedItems.getModel() ).toArray();
  }


  public void actionPerformed ( ActionEvent event ) {
    // when ENTER is pressed do a search
    performSearch();    
    integration.updateView(); 
  } // actionPerformed

  public void performSearch () {
    // do search of the proper elements according to what's in the text box
    Iterator i;
    if ( dimension == Integration.SLICE ) 
      i = dataCube.getSliceNames().iterator();
    else if ( dimension == Integration.ROW ) 
      i = dataCube.getRowNames().iterator();
    else
      i = dataCube.getColumnNames().iterator();

    ArrayList passes = new ArrayList();


    if ( regexpSearch.isSelected() ) {
      String pattern = searchField.getText();
      while ( i.hasNext() ) {
        String s = ( String )i.next();
        if ( s.matches( pattern ) ) {
          //select
          passes.add( s );
          System.out.println( s+" Matches search: "+pattern );
        }
      }
    } else {

      String[] pattern = searchField.getText().split("\\s");

      while ( i.hasNext() ) {
        String s = ( String )i.next();
        for ( int p = 0; p < pattern.length; ++p ) {
          if ( Strings.isLike( s, pattern[p], 0, true ) ) {
            // select
            passes.add( s );
            System.out.println( s+" Matches search: "+pattern[p] );
          }
        }
      }
    }
    Iterator sel;
    DefaultListModel new_model;
    if ( clearSelection.isSelected() ) {
      sel = passes.iterator();
    } else {
            
      // add them to the selection list
      Set current_selection = new TreeSet( java.util.Arrays.asList( ( ( DefaultListModel )selectedItems.getModel() ).toArray() ) );
      current_selection.addAll( passes );
      
      sel = current_selection.iterator();
    }
    new_model = new DefaultListModel();
    while ( sel.hasNext() ){
      new_model.addElement( sel.next() );
    }
    selectedItems.setModel( new_model );
    

  } // performSearch



}
