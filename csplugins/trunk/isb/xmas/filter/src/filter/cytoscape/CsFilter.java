package filter.cytoscape;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;
import filter.model.*;
import filter.view.*;
import cytoscape.CyNetwork;
import javax.swing.*;
import filter.cytoscape.network.*;


import java.util.*;
import java.awt.event.*;

import java.beans.*;
import java.io.*;


public class CsFilter 
  extends 
    AbstractPlugin 
  implements 
    PropertyChangeListener {
 protected JFrame frame;
  protected CyWindow window;
  protected CyNetwork network;
  protected FilterUsePanel filterUsePanel;

  public CsFilter ( CyWindow window ) {
    this.window = window;
    this.network = window.getNetwork();
    initialize();
  }

  public void propertyChange ( PropertyChangeEvent e ) {
    if ( e.getPropertyName() == Cytoscape.CYTOSCAPE_EXIT ) {
      Set filters = FilterManager.defaultManager().getFilters( true );
      Iterator i = filters.iterator();
      // StringBuffer buffer = new StringBuffer();
      try {

        File filter_file = Cytoscape.getCytoscapeObj().getConfigFile( "filter.props" );

        BufferedWriter writer = new BufferedWriter(new FileWriter( filter_file ));
        
        while ( i.hasNext() ) {
          try {
            writer.write( FilterManager.defaultManager().getFilter( ( String )i.next() ).output() );
            writer.newLine();
          } catch ( Exception ex ) {
            System.out.println( "Error with Filter output" );
          }
         
        }

        writer.close();
      } catch ( Exception ex ) {
        System.out.println( "Filter Write error" );
        ex.printStackTrace();
      }

    }
  }

  public void initialize () {

    Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener( this );
    
    try {
      File filter_file = Cytoscape.getCytoscapeObj().getConfigFile( "filter.props" );
      BufferedReader in
        = new BufferedReader(new FileReader(filter_file));
      String oneLine = in.readLine();
      while (oneLine != null) {
        if (oneLine.startsWith("#")) {
          // comment
        } else {
           FilterManager.defaultManager().createFilterFromString( oneLine );
        }
        oneLine = in.readLine();
      }
      in.close();
    } catch ( Exception ex ) {
      System.out.println( "Filter Read error" );
      ex.printStackTrace();
    }
    

    ImageIcon icon = new ImageIcon( getClass().getResource( "/filter36.gif" ) );
    ImageIcon icon2 = new ImageIcon(  getClass().getResource("/filter16.gif" ) );
                                    //getClass().getResource("filter16.gif") );
    FilterPlugin action = new FilterPlugin( network, window, icon, this );
    FilterMenuItem menu_action = new FilterMenuItem(  network, window, icon2, this );
    
    window.getCyMenus().addCytoscapeAction( ( CytoscapeAction )action );
    window.getCyMenus().addCytoscapeAction( ( CytoscapeAction )menu_action );

    CreateAddNetwork can = new CreateAddNetwork( null );
    window.getCyMenus().addCytoscapeAction( can );
    FilterDataView fdv = new FilterDataView( null );
    window.getCyMenus().addCytoscapeAction( fdv );

  //   JMenuItem spew = new JMenuItem( new AbstractAction( "spew" ) {
//          public void actionPerformed ( ActionEvent e ) {
//            // Do this in the GUI Event Dispatch thread...
//            SwingUtilities.invokeLater( new Runnable() {
//                public void run() {
//                  Set filters = FilterManager.defaultManager().getFilters( true );
//                  Iterator i = filters.iterator();
//                  int count = 0;
//                  while ( i.hasNext() ) {
//                    System.out.println( "Filter: "+count+" "+
//                                        FilterManager.defaultManager().getFilter( ( String )i.next() ).output() );
//                    count++;
//                  }
                 

//                } } ); } } );
//     window.getCyMenus().getMenuBar().getMenu( "Filters" ).add( spew );


//      JMenuItem make = new JMenuItem( new AbstractAction( "make" ) {
//          public void actionPerformed ( ActionEvent e ) {
//            // Do this in the GUI Event Dispatch thread...
//            SwingUtilities.invokeLater( new Runnable() {
//                public void run() {
//                  FilterManager.defaultManager().createFilterFromString( "filter.cytoscape.StringPatternFilter,Edge,canonicalName,hello,hello" );
//                  FilterManager.defaultManager().createFilterFromString( "filter.cytoscape.StringPatternFilter,Node,canonicalName,2134324,Regex: 32" );
//                  FilterManager.defaultManager().createFilterFromString( "filter.cytoscape.StringPatternFilter,Node,canonicalName,YD*4*W,Custom Filter from typing it in" );
//                  FilterManager.defaultManager().createFilterFromString( "filter.cytoscape.NumericAttributeFilter,<,Node,gal1RG.sigexp,0.02,LT 2" );
//                  FilterManager.defaultManager().createFilterFromString( "filter.cytoscape.NumericAttributeFilter,=,Node,gal1RG.sigexp,0.02,EQ 2" );
//                  FilterManager.defaultManager().createFilterFromString( "filter.cytoscape.NodeTopologyFilter,3,4,hello,Topo1" );
//                  FilterManager.defaultManager().createFilterFromString( "filter.cytoscape.BooleanMetaFilter,LT 2:Custom Filter from typing it in:Topology Filter mine,AT LEAST ONE,Boolean Filter test1" );


                
//            } } ); } } );
     
    
//      window.getCyMenus().getMenuBar().getMenu( "Filters" ).add( make );

     
     //FilterManager.defaultManager().addEditor( new DefaultFilterEditor() );
     //FilterManager.defaultManager().addEditor( new FilterTreeEditor() );
     //FilterManager.defaultManager().addEditor( new CsNodeTypeFilterEditor( window.getNetwork() ) );
     //FilterManager.defaultManager().addEditor( new CsEdgeTypeFilterEditor( window.getNetwork() ) );
     //FilterManager.defaultManager().addEditor( new CsNodeInteractionFilterEditor( window.getNetwork() ) );
     //FilterManager.defaultManager().addEditor( new CsAttributeValueFilterEditor( window.getNetwork() ) );
		
    FilterManager.defaultManager().addEditor( new InteractionFilterEditor( window,FilterManager.defaultManager().getFilters(false)));	
    FilterManager.defaultManager().addEditor( new NodeTopologyFilterEditor(window,FilterManager.defaultManager().getFilters(false))); 
    FilterManager.defaultManager().addEditor( new BooleanMetaFilterEditor (FilterManager.defaultManager().getFilters(false)));
    FilterManager.defaultManager().addEditor( new NumericAttributeFilterEditor( window ) );
    FilterManager.defaultManager().addEditor( new StringPatternFilterEditor (window)); 


  }

  public String describe () {
    return "New Filters";
  }

  public  FilterUsePanel getFilterUsePanel () {
    if ( filterUsePanel == null ) {
      filterUsePanel = new FilterUsePanel( network, window );
     }
    return filterUsePanel;
  }
                
  public void show () {
    if ( frame == null ) {
      frame = new JFrame( "Use Filters" );
      frame.getContentPane().add( getFilterUsePanel() );
      frame.pack();
    }
    frame.setVisible( true );
  }

}
