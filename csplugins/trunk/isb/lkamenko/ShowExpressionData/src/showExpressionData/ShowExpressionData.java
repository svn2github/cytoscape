package csplugins.showExpressionData;


import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.ImageIcon;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.GraphView;
import giny.view.NodeView;
import cytoscape.plugin.jar.JarLoader;
import cytoscape.plugin.AbstractPlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.CyNetwork;

import cytoscape.view.CyWindow;

/**
 * This is a  Cytoscape plugin that is using Giny graph structures. 
 */
public class ShowExpressionData extends AbstractPlugin {
 
  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
  public ShowExpressionData(CyWindow cyWindow) {
   
    JMenu pluginName = new JMenu("Show Expression Data");

    pluginName.add( new ShowExpressionDataAction( ShowExpressionDataAction.NORMAL, "Normally", null ) );
    pluginName.add( new ShowExpressionDataAction( ShowExpressionDataAction.STAR_PLOT, "Star Plots", new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt( "star.gif" ) ) ) );
    pluginName.add( new ShowExpressionDataAction( ShowExpressionDataAction.GRID_NODE, "Grid Nodes", new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt( "grid.gif" ) ) ) );
    pluginName.add( new ShowExpressionDataAction( ShowExpressionDataAction.PETAL_NODE, "Petal Nodes", new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt( "petal.gif" ) ) ) );
    pluginName.add( new ShowExpressionDataAction( ShowExpressionDataAction.RADAR_NODE, "Radar Nodes", new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt( "radar.gif" ) ) ) );
    //pluginName.add( new ReturnToOriginalStateAction(networkView);
        
    cyWindow.getCyMenus().getMenuBar().getMenu( "Visualization" ).add(pluginName);
	
  }
    
 
}


