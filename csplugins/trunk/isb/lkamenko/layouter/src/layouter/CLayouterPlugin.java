package csplugins.layouter;


import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JMenu;

import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.GraphView;
import giny.view.NodeView;

import cytoscape.AbstractPlugin;
import cytoscape.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;

/**
 * This is a  Cytoscape plugin that is using Giny graph structures. 
 */
public class CLayouterPlugin extends AbstractPlugin {
    
    CyWindow cyWindow;
    
    
    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public CLayouterPlugin(CyWindow cyWindow) {
        this.cyWindow = cyWindow;
	JMenu pluginName = new JMenu("Layouter Manager");
        pluginName.add( new SaveLayoutAction(cyWindow) );
	pluginName.add(new SaveSelectedLayoutAction(cyWindow) );
	pluginName.add( new ApplyLayoutAction(cyWindow) );
	cyWindow.getCyMenus().getOperationsMenu().add(pluginName);
	cyWindow.getCyMenus().getLayoutMenu().add( new SaveLayoutAction(cyWindow) );
	cyWindow.getCyMenus().getLayoutMenu().add( new ApplyLayoutAction(cyWindow) );
    }
    
 
}


