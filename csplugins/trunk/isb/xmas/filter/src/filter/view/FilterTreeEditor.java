package filter.view;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import filter.model.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * This Filter Tree View listens to its FilterTree and Fires events
 * about which node is selected to its listeners.
 * It also communicates with the FilterManager to cooridinate the
 * creation of the FilterTrees.
 */
public class FilterTreeEditor 
  extends FilterEditor
  implements PropertyChangeListener,
             TreeSelectionListener,
             ActionListener {

  /**
   *  PCS support
   */
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);
  /**
   * This is the FilterTree that is currently being viewed.  There will be 
   * multiple available. And they will be stored by the FilterManager.
   */
  public FilterTree filterTree;

  String identifier = "FilterTree";

  /**
   * The FilterListView that holds a JList of Filters and allows 
   * for the Searching of FilterNames via String wildcards.
   */
  public FilterListPanel filterListPanel;

  public JLabel status;
  public JComboBox filterTreeList;
  public JButton setTreeButton;
  public JPanel treePanel;
  public JButton addFilterChild, removeFilterChild, replaceFilter;


  /**
   * Craetes a default FilterTreeView that can have Filter trees added to it.
   * Also allows for some nice communication with the FilterManager.
   */
  public FilterTreeEditor (  ) {
    super();
    initialize();
  }
  
  protected void initialize () {

    //--------------------
    // Title Panel
    JPanel titlePanel = new JPanel();

    // Create the Combo Box of available TreeFilters
    filterTreeList = new JComboBox();
    filterTreeList.setEditable( true );
    filterTreeList.addActionListener( this );

    setTreeButton = new JButton ( "Set" );
    setTreeButton.addActionListener( this );
    titlePanel.add( new JLabel( "Tree Filter Name:") );
    titlePanel.add( filterTreeList );
    titlePanel.add( setTreeButton );


    status = new JLabel();
    titlePanel.add( status );

    //--------------------
    // Tree Panel
    treePanel = new JPanel();
    filterTree = new FilterTree();
    treePanel.add( filterTree );
    JScrollPane scroll = new JScrollPane( treePanel );
    scroll.setPreferredSize( new Dimension( 50, 100 ) );

    //--------------------
    // Filter Control Panel
    addFilterChild = new JButton( "<- Add Child" );
    addFilterChild.addActionListener( this );

    removeFilterChild = new JButton( "Remove Child ->" );
    removeFilterChild.addActionListener( this );

    replaceFilter = new JButton( "= Replace Filter =");
    removeFilterChild.addActionListener( this );
    JPanel filterControlPanel = new JPanel();
    filterControlPanel.setLayout( new GridLayout( 0, 1 ));
    filterControlPanel.setBorder( new TitledBorder( "Control" ) );
    filterControlPanel.add( addFilterChild );
    filterControlPanel.add( replaceFilter );
    filterControlPanel.add( removeFilterChild );


    filterListPanel = new FilterListPanel( FilterListPanel.SHOW_TOGETHER );

    // Rowan hates layout issues.
    JPanel a = new JPanel();
    a.add( titlePanel );
    a.add( filterControlPanel );

    setLayout( new BorderLayout() );
    add( a, BorderLayout.NORTH );
    add( scroll, BorderLayout.CENTER );
    add( filterListPanel, BorderLayout.WEST );

  }
 
  //----------------------------------------//
  // Implements Filter Editor
  //----------------------------------------//

  public Filter getFilter () {
    return filterTree;
  }

  public String toString () {
    return identifier;
  }

  public String getFilterID() {
    return FilterTree.FILTER_ID;
  }
  

  
  public void editFilter ( Filter filter ) {
    FilterTree tree = ( FilterTree )filter;
    if ( tree.getRoot() == null ) {
      System.out.println( "Tree Root was null for :"+tree.toString() );
      return;
    }
    
    this.filterTree = tree;
    treePanel.removeAll();
    treePanel.add( tree );
    for ( int i = 0; i < filterTree.getRowCount(); ++i ) {
      filterTree.expandRow( i );
    }
    
    validate();

  }

  public void reset () {}

  public void clear () {}

  //----------------------------------------//
  // Implements ActionListener
  //----------------------------------------//
  /**
   * This class listens to its ComboBox to be informed
   * of when a new selection is made.
   */
  public void actionPerformed ( ActionEvent e ) {

    

    if ( e.getSource() == filterTreeList ) {
      String name = ( String )filterTreeList.getSelectedItem();
      
      
      if ( FilterManager.defaultManager().filterExists( name ) ) {
        //status.setText( "NAME ALREADY EXISTS CHOOSE ANOTHER" );
        //status.setBackground( java.awt.Color.red );
        //status.setForeground( java.awt.Color.black );
        filterTreeList.setSelectedItem( name );
        editFilter( FilterManager.defaultManager().getFilterTree( name ) );
      } else {
        //status.setText( "" );
        if ( filterTree == null ) {
          // no filter tree is set.
          filterTree = new FilterTree();
          FilterManager.defaultManager().addFilterTree( filterTree );
        } else if ( filterTree.getRoot() == null ) {
          filterTreeList.removeItem( filterTree.toString() );
          FilterManager.defaultManager().removeFilterTree( filterTree.toString() );
          filterTree.setIdentifier( name );
          FilterManager.defaultManager().addFilterTree( filterTree );
          filterTreeList.addItem( name );
          filterTreeList.setSelectedItem( name );
        } else {
          System.out.println( "Clonining Tree" );
          FilterTree new_tree = ( FilterTree )filterTree.clone();
          new_tree.setIdentifier( name );
          FilterManager.defaultManager().addFilterTree( new_tree );
          filterTreeList.addItem( name );
          filterTreeList.setSelectedItem( name );
          editFilter( new_tree );
        }
      }
    } else if ( e.getSource() == addFilterChild ) {
      // add a new FilterChild to the selected node in the tree, 
      // or the Root, if none is selected
      Filter new_filter = filterListPanel.getLastSelectedFilter();
      if ( filterTree == null ) {
        System.out.println( "The Filter TRee is null." );
        filterTree = new FilterTree( new FilterNode( 0, new_filter ) );
        return;
      }
          
      if ( filterTree.getRoot() == null ) {
        filterTree.setRoot( new FilterNode( 0, new_filter ) );
        return;
      }
      filterTree.addFilterNode( new_filter );

    } else if ( e.getSource() == removeFilterChild ) {
      // remove the selected node from the tree, and all children
      // do nothing if the root is selected
      filterTree.removeSelectedNode();

    } else if ( e.getSource() == replaceFilter ) {
      // replace the filter of the selected FilterNode with the 
      // given new Filter
      Filter new_filter = ( Filter )filterListPanel.getLastSelectedFilter();
      filterTree.getSelectedNode().setFilter( new_filter );
    }

  }

 
  //----------------------------------------//
  // Implements TreeSelectionListener
  //----------------------------------------//
  public void valueChanged ( TreeSelectionEvent e ) {
    // Returns the Selected Node from what is presumably the current tree,
    // although it need not be.
    FilterNode filter_node = ( FilterNode )e.getPath().getLastPathComponent();
    
    // TODO:  Fire an appropriate change event, so the Editor knows 
    //        to update the Filter(Node) that is being changed.

  } 


  //----------------------------------------//
  // PCS Support
  //----------------------------------------//

  /**
   * handles property changes, usually just from the tree.
   */
  public void propertyChange ( PropertyChangeEvent evt ) {
    // handle property changes
  } 

  /**
   * In order to reduce the amount of code, this class will rely soley
   * on its PCS for all listener registration, and all firing will be done
   * by the PCS.
   * @return the PCS object
   */
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport () {
    return pcs;
  }




}
