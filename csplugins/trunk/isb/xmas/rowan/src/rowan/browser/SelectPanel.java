package rowan.browser;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.util.List;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;
 
import cytoscape.*;
import cytoscape.data.*;


import cytoscape.data.attr.*;

import giny.model.GraphObject;

public class SelectPanel extends JPanel 
  implements PropertyChangeListener,
             ActionListener {

  JComboBox networkBox;
  JComboBox filterBox;
  DataTableModel tableModel;
  Map titleIdMap;

  public SelectPanel ( DataTableModel tableModel ) {

    this.tableModel = tableModel;

    titleIdMap = new HashMap();
    networkBox = getNetworkBox();
    networkBox.setMaximumSize( new Dimension(  15, (int)networkBox.getPreferredSize().getHeight()) );
    filterBox = new JComboBox( FilterManager.defaultManager().getComboBoxModel() );
    Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener( this );
    

    setBorder( new TitledBorder( "Object Selection" ) );
    add( new JLabel( "Filter: ") );
    add( filterBox );
    add( new JLabel( "Network: " ) );
    add( networkBox );

    filterBox.addActionListener( this );
    networkBox.addActionListener( this );

    
  }
  
  public void actionPerformed ( ActionEvent e ) {
    if ( e.getSource() == filterBox ) {
      Filter filter = ( Filter )filterBox.getSelectedItem();
      System.out.println( "Showing all that Pass Filter: "+filter );
      List list = new ArrayList( Cytoscape.getRootGraph().getNodeCount() );
      Iterator nodes = Cytoscape.getRootGraph().nodesIterator();
      while ( nodes.hasNext() ) {
        Object node = nodes.next();
        if ( filter.passesFilter( node ) )
          list.add( node );
      }
      tableModel.setTableDataObjects( list );
    }
    
    if ( e.getSource() == networkBox ) {
      String network_id = ( String )titleIdMap.get( networkBox.getSelectedItem() );
      CyNetwork network = Cytoscape.getNetwork( network_id );
      System.out.println( "Showing all that Pass Network: "+network );
      tableModel.setTableDataObjects( network.nodesList() );
    }


  }


  public void propertyChange ( PropertyChangeEvent e ) {

    
    if ( e.getPropertyName().equals( Cytoscape.NETWORK_CREATED ) ||  e.getPropertyName().equals( Cytoscape.NETWORK_DESTROYED ) ) {
      updateNetworkBox();
    }
  }
 protected void updateNetworkBox () {
    Iterator i = Cytoscape.getNetworkSet().iterator();
    Vector vector = new Vector();
    while ( i.hasNext() ) {
      //System.out.println( i.next().getClass() );
      CyNetwork net = ( CyNetwork )i.next();
      titleIdMap.put( net.getTitle(), net.getIdentifier() );
      vector.add( net.getTitle() );
    }
    DefaultComboBoxModel model =new DefaultComboBoxModel( vector );
    networkBox.setModel( model );
  }

   protected JComboBox getNetworkBox () {
    Iterator i = Cytoscape.getNetworkSet().iterator();
    Vector vector = new Vector();
    while ( i.hasNext() ) {
      CyNetwork net = ( CyNetwork )i.next();
      titleIdMap.put( net.getTitle(), net.getIdentifier() );
      vector.add( net.getTitle() );
    }
    DefaultComboBoxModel model =new DefaultComboBoxModel( vector );
    return new JComboBox( model );
  }


}
