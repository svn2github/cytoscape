package csplugins.isb.dtenenbaum.setDefaultLayout;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.visual.VisualMappingManager;
import cytoscape.actions.FitContentAction;
import cytoscape.plugin.*;

import java.awt.event.ActionEvent;
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
public class CenterViewPlugin 
  extends 
    CytoscapePlugin 
  implements
    PropertyChangeListener  {
   
  YFilesLayoutPlugin plugin;

  public CenterViewPlugin () {
    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
    
  }

  public void propertyChange ( PropertyChangeEvent e ) {

    if ( e.getPropertyName() ==  CytoscapeDesktop.NETWORK_VIEW_CREATED ) {
      CyNetworkView view = ( CyNetworkView )e.getNewValue();

      System.out.println("trying to fit content....");
      //PGraphView vue =(PGraphView) Cytoscape.getCurrentNetworkView();
      //PGraphView vue =(PGraphView) view;
      //vue.getCanvas().getCamera().animateViewToCenterBounds( vue.getCanvas().getLayer().getFullBounds(), true, 50l );
      FitContentAction fca = new FitContentAction();
      fca.actionPerformed(null);
      System.out.println("done");
      view.redrawGraph(true,true);
      
      
      
      // vmm (below) is null. why?
      /*
      VisualMappingManager vmm = view.getVizMapManager();
      if (null == vmm)
      	System.out.println("vmm is null");
      else
      	System.out.println("vmm " +vmm);
      vmm.getNetworkView().redrawGraph(false, true);
      */

      
    }
  }
}
  
