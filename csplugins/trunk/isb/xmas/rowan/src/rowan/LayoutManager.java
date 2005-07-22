package rowan;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.*;

import javax.swing.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.event.*;

import giny.view.*;
import giny.model.*;

public class LayoutManager 
  extends JMenu 
  implements PropertyChangeListener,
             MenuListener{

  String currentLayout = "no layout";
  CyNetwork currentNetwork = null;
  CyNetworkView currentNetworkView = null;
  ApplyMenu apply_menu;

  JMenuItem layoutItem = null;

  public LayoutManager () {
    super( "Layout Manager", true );

    Cytoscape.getNodeNetworkData().initializeAttributeType( "NODE_X", cytoscape.data.CytoscapeData.TYPE_FLOATING_POINT );
    Cytoscape.getNodeNetworkData().initializeAttributeType( "NODE_Y", cytoscape.data.CytoscapeData.TYPE_FLOATING_POINT );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout" ).add( this );
    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( this );
  
    add( getSaveItem() );
    add( getSaveAsItem() );
    
    layoutItem = new JMenuItem( currentLayout );
    layoutItem.setEnabled( false );

    apply_menu = new ApplyMenu( this );
    apply_menu.addMenuListener( this );
    add( apply_menu );
  }

  public void saveLayout ( String layout ) {

    CytoscapeData data = Cytoscape.getNodeNetworkData();
    Iterator nodes = currentNetworkView.getNodeViewsIterator();
    while ( nodes.hasNext() ) {

      NodeView view = ( NodeView )nodes.next();
      Node node = view.getNode();

      double x = view.getXPosition();
      double y = view.getYPosition();
      
      data.putAttributeKeyValue( node.getIdentifier(),
                                 "NODE_X",
                                 layout,
                                 new Double(x) ); 
      data.putAttributeKeyValue( node.getIdentifier(),
                                 "NODE_Y",
                                 layout,
                                 new Double(y) );

    }

    currentLayout = layout;
    remove( 0 );
    layoutItem = new JMenuItem( currentLayout );
    layoutItem.setEnabled( false );
    insert( layoutItem, 0 );
  }

  public void applyLayout ( String layout ) {
    CytoscapeData data = Cytoscape.getNodeNetworkData();
    Iterator nodes = currentNetworkView.getNodeViewsIterator();
    while ( nodes.hasNext() ) {

      NodeView view = ( NodeView )nodes.next();
      Node node = view.getNode();

      double x = ((Double)data.getAttributeKeyValue( node.getIdentifier(),
                                                     "NODE_X",
                                                     layout ) ).doubleValue();
                  
      double y = ((Double)data.getAttributeKeyValue( node.getIdentifier(),
                                                     "NODE_Y",
                                                     layout ) ).doubleValue();

      view.setXPosition( x , false );
      view.setYPosition( y , false );
      
    }
    nodes = currentNetworkView.getNodeViewsIterator();
    while ( nodes.hasNext() ) {
      NodeView view = ( NodeView )nodes.next();
      view.setNodePosition(true);
    }
    currentLayout = layout;
    remove( 0 );
    layoutItem = new JMenuItem( currentLayout );
    layoutItem.setEnabled( false );
    insert( layoutItem, 0 );
  }

  public JMenuItem getSaveItem () {
    
    JMenuItem save = new JMenuItem( new AbstractAction( "Save Layout" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                
                if ( currentNetworkView != null && currentLayout == null ) {
                  showNameDialog();
                } else {
                  saveLayout( currentLayout );
                }
                
              } } ); } } );
    return save;
  }
  public JMenuItem getSaveAsItem () {
    
    JMenuItem saveas = new JMenuItem( new AbstractAction( "Save Layout As..." ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                
                if ( currentNetworkView != null ) {
                  showNameDialog();
                } 
                
              } } ); } } );
    return saveas;
  }

  public void menuCanceled(MenuEvent e) {
  }
  public void menuDeselected(MenuEvent e) {
  }
  public void menuSelected (MenuEvent e) {
    apply_menu.updateLayouts();
  }
 
  public void propertyChange ( PropertyChangeEvent e ) {


    if ( e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED ) {
        // get focus event from NetworkViewManager
      updateFocus( e.getNewValue().toString() );
      
    }
  }
  
  /**
   * @param new_network id of the network now being focused
   */
  protected void updateFocus ( String new_network ) {

    currentNetwork = Cytoscape.getNetwork( new_network );
    currentNetworkView = Cytoscape.getNetworkView( new_network );

  }

  protected void showNameDialog () {

    final JFrame name = new JFrame( "Layout Name" );
    final JTextField text = new JTextField( 20 );
    JButton save = new JButton( new AbstractAction( "Save" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                
                if ( !text.getText().startsWith( " " )) {
                  saveLayout( text.getText() );
                  name.setVisible( false );
                }
                
              } } ); } } );
    JPanel panel = new JPanel();
    panel.add( new JLabel( "Name of Layout: " ) );
    panel.add( text );
    panel.add( save );
    
    name.getContentPane().add( panel );
    name.pack();
    name.setVisible( true );
    
  }
    
  class ApplyMenu extends JMenu {

    LayoutManager lm;

    ApplyMenu ( LayoutManager laym ) {
      super( "Apply Layout" );
      lm = laym;
      updateLayouts();
    }

    public void updateLayouts() {
      removeAll();
      
      
      try {
        Node node = ( Node )currentNetwork.nodesList().get(0);
        Set keys = Cytoscape.getNodeNetworkData().getAttributeKeySet( node.getIdentifier(), "NODE_X" );
        for ( Iterator i = keys.iterator(); i.hasNext(); ) {
          add( createLayoutItem( (String)i.next() ) );
        }
      } catch ( Exception e ) {}
      
    }

    public JMenuItem createLayoutItem ( String lay ) {
    
      final String layout = lay;
      JMenuItem item = new JMenuItem( new AbstractAction( layout ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  lm.applyLayout( layout );
                  
                } } ); } } );
      return item;
    }
    

  }


}
