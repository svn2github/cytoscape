package csplugins.showExpressionData;


import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JMenu;

import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.GraphView;
import giny.view.NodeView;

import cytoscape.plugin.AbstractPlugin;
import cytoscape.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;

/**
 * This is a  Cytoscape plugin that is using Giny graph structures. 
 */
public class ShowExpressionData extends AbstractPlugin {
    
    CyWindow networkView;
    
    
    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public ShowExpressionData(CyWindow cyWindow) {
        this.networkView = cyWindow;
	JMenu pluginName = new JMenu("Show Expression Data");
	pluginName.add( new ShowExpressionDataAction( networkView, ShowExpressionDataAction.STAR_PLOT, "... as Star Plots" ) );
	pluginName.add( new ShowExpressionDataAction( networkView, ShowExpressionDataAction.GRID_NODE, "... as Grid Nodes" ) );
        pluginName.add( new ShowExpressionDataAction( networkView, ShowExpressionDataAction.PETAL_NODE, "... as Petal Nodes" ) );
	pluginName.add( new ShowExpressionDataAction( networkView, ShowExpressionDataAction.RADAR_NODE, "... as Radar Nodes" ) );
	//pluginName.add( new ReturnToOriginalStateAction(networkView);
        
	cyWindow.getCyMenus().getOperationsMenu().add(pluginName);
	
    }
    
 
}


