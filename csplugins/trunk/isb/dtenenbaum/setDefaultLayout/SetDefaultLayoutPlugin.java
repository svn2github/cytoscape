package csplugins.isb.dtenenbaum.setDefaultLayout;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.actions.FitContentAction;
import cytoscape.plugin.*;
import java.beans.*;

import phoebe.PGraphView;

import yfiles.YFilesLayout;
import yfiles.YFilesLayoutPlugin;

/**
 * For now this class just ensures that every new graph loaded is viewed in
 * organic layout to begin with. In the future it can take parameters telling 
 * it which layout to use. YFilesLayoutPlugin must also be loaded for this to work!   
 * A similar plugin could be written to center a graph every time one is loaded.
 * @author dtenenba
 *
 */
public class SetDefaultLayoutPlugin 
  extends 
    CytoscapePlugin 
  implements
    PropertyChangeListener  { 
   
  YFilesLayoutPlugin plugin;

  public SetDefaultLayoutPlugin () {
    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
    
  }

  public void propertyChange ( PropertyChangeEvent e ) { 

    if ( e.getPropertyName() ==  CytoscapeDesktop.NETWORK_VIEW_CREATED ) {
      CyNetworkView view = ( CyNetworkView )e.getNewValue();

      System.out.println("trying to change layout...");
      YFilesLayout layout = new YFilesLayout(view);
      layout.doLayout( YFilesLayout.ORGANIC, 0 );
      System.out.println("done");
      
      //System.out.println("trying to redraw graph...");
      //view.redrawGraph(true,true);
      //System.out.println("done");
      
      //System.out.println("trying to fit content....");
      // 2 ways to do it. way 1:
      //PGraphView vue =(PGraphView) view;
      //vue.getCanvas().getCamera().animateViewToCenterBounds( vue.getCanvas().getLayer().getFullBounds(), true, 50l );
      // way 2:
      //FitContentAction fca = new FitContentAction();
      //fca.actionPerformed(null);
      //System.out.println("done");
      view.redrawGraph(true,true);
      
    }
  }
}
  
