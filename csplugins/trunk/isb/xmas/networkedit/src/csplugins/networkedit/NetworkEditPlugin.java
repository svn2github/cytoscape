package csplugins.networkedit;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;
import cytoscape.util.*;
import cern.colt.list.*;

import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;

import java.beans.*;

import phoebe.*;

public class NetworkEditPlugin extends CytoscapePlugin implements PropertyChangeListener{

  private JCheckBoxMenuItem netItem;
  private NetworkEditEventHandler event;

  public NetworkEditPlugin () {

    event = new NetworkEditEventHandler();

    //ImageIcon icon = new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt( "add.png" ) );
    netItem = new JCheckBoxMenuItem( new AbstractAction( "Edit Network", null ) {
        public void actionPerformed ( java.awt.event.ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                toggleNetEdit();
              } } ); } } );
    netItem.setSelected( false );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Edit").add( netItem );

    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this );

  }
 
  protected void toggleNetEdit () {
    if ( netItem.isSelected() ) {
      System.out.println( "Toggle: start" );
      netItem.setSelected( true );
      event.start(( PGraphView )Cytoscape.getCurrentNetworkView() );
    } else {
      System.out.println( "Toggle: stop" );
      netItem.setSelected( false );
      event.stop();
    }

  }

  public void propertyChange ( PropertyChangeEvent e ) {
    if ( !netItem.getState() ) {
      // turn off if the focus changes
      toggleNetEdit();
    }
  }
}
