package filter.cytoscape;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
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
    CytoscapePlugin 
  implements 
    PropertyChangeListener {
 protected JFrame frame;
 protected FilterUsePanel filterUsePanel;

  public CsFilter () {
    initialize();
  }

  public void propertyChange ( PropertyChangeEvent e ) {
    if ( e.getPropertyName() == Cytoscape.CYTOSCAPE_EXIT ) {
      Iterator i = FilterManager.defaultManager().getFilters();
      // StringBuffer buffer = new StringBuffer();
      try {

        File filter_file = CytoscapeInit.getConfigFile( "filter.props" );

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
      File filter_file = CytoscapeInit.getConfigFile( "filter.props" );
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
    

    // create icons
    ImageIcon icon = new ImageIcon( getClass().getResource( "/stock_filter-data-by-criteria.png"));
    ImageIcon icon2 = new ImageIcon( getClass().getResource( "/stock_filter-data-by-criteria-16.png"));
    // 
    FilterPlugin action = new FilterPlugin( icon, this );
    FilterMenuItem menu_action = new FilterMenuItem( icon2, this );
    //Cytoscape.getDesktop().getCyMenus().addCytoscapeAction( ( CytoscapeAction )action );
    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction( ( CytoscapeAction )menu_action );
    CytoscapeDesktop desktop = Cytoscape.getDesktop();
    CyMenus cyMenus = desktop.getCyMenus();
    CytoscapeToolBar toolBar = cyMenus.getToolBar();
    JButton button = new JButton (icon);
    button.addActionListener(action);
    button.setToolTipText("Create and apply filters");
    button.setBorderPainted(false);
    toolBar.add(button);

    FilterEditorManager.defaultManager().addEditor( new NumericAttributeFilterEditor() );
    FilterEditorManager.defaultManager().addEditor( new StringPatternFilterEditor ()); 
    FilterEditorManager.defaultManager().addEditor( new NodeTopologyFilterEditor ());
    FilterEditorManager.defaultManager().addEditor( new BooleanMetaFilterEditor ());
    FilterEditorManager.defaultManager().addEditor( new NodeInteractionFilterEditor());
    FilterEditorManager.defaultManager().addEditor( new EdgeInteractionFilterEditor());

  }

  public String describe () {
    return "New Filters";
  }

  public  FilterUsePanel getFilterUsePanel () {
    if ( filterUsePanel == null ) {
      filterUsePanel = new FilterUsePanel( frame );
    }
    return filterUsePanel;
  }
                
  public void show () {
    if ( frame == null ) {
      frame = new JFrame( "Use Filters" );
      frame.getContentPane().add( getFilterUsePanel());
      frame.pack();
      //Cytoscape.getDesktop().getCytoPanel( SwingConstants.SOUTH ).add(getFilterUsePanel()); 
    }
    frame.setVisible( true );
  }

}
