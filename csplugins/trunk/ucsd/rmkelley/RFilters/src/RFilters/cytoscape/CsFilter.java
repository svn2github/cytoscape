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
      Iterator i = FilterManager.defaultManager().getFilters();
      // StringBuffer buffer = new StringBuffer();
      try {

        File filter_file = Cytoscape.getCytoscapeObj().getConfigFile( "filter.props" );

        BufferedWriter writer = new BufferedWriter(new FileWriter( filter_file ));
        
        while ( i.hasNext() ) {
          try {
	    Filter f = (Filter)i.next();
            writer.write( FilterManager.defaultManager().getFilterID(f)+"\t"+f.getClass()+"\t"+f.output());
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
    

    ImageIcon icon = new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt( "filter36.gif" ) );
    ImageIcon icon2 = new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt( "filter16.gif" ) );
                                    //getClass().getResource("filter16.gif") );
    FilterPlugin action = new FilterPlugin( network, window, icon, this );
    FilterMenuItem menu_action = new FilterMenuItem(  network, window, icon2, this );
    
    window.getCyMenus().addCytoscapeAction( ( CytoscapeAction )action );
    window.getCyMenus().addCytoscapeAction( ( CytoscapeAction )menu_action );

    FilterEditorManager.defaultManager().addEditor( new NumericAttributeFilterEditor( window ) );
    FilterEditorManager.defaultManager().addEditor( new StringPatternFilterEditor (window)); 
    FilterEditorManager.defaultManager().addEditor( new NodeTopologyFilterEditor ());
    FilterEditorManager.defaultManager().addEditor( new BooleanMetaFilterEditor ());
    FilterEditorManager.defaultManager().addEditor( new InteractionFilterEditor());
  }

  public String describe () {
    return "New Filters";
  }

  public  FilterUsePanel getFilterUsePanel () {
    if ( filterUsePanel == null ) {
      filterUsePanel = new FilterUsePanel( frame,network, window );
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
