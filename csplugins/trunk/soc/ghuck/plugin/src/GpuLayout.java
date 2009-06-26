/**************************************************************************************
Copyright (C) Apeksha Godiyal, 2008
Copyright (C) Gerardo Huck, 2009


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

See licence.h for more information.
**************************************************************************************/


import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import giny.model.Node;
import giny.view.NodeView;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.view.CyNetworkView;
import cytoscape.data.Semantics;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.cytopanels.CytoPanelImp;
import javax.swing.SwingConstants;
import javax.swing.JPanel;



/**


 */


public class GpuLayout extends    CytoscapePlugin 
				  //implements CyGroupViewer,
				  //NodeContextMenuListener,
				  //PropertyChangeListener 
{
    /**
     * This constructor creates an action and adds it to the Plugins menu.
     */
    public GpuLayout() {
	
	/*  Show message on screen    */
	
	String message = "GPU Graph Layout Loaded!";

	// Use the CytoscapeDesktop as parent for a Swing dialog
	JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);

	
	/*    Add Button to Menu     */

        //create a new action to respond to menu activation
        GpuLayoutAction action = new GpuLayoutAction();

        //set the preferred menu
	action.setPreferredMenu("Layout");

        //and add it to the menus
	Cytoscape.getDesktop().getCyMenus().addAction(action);

    }

    class MyPanel extends JPanel {
	public MyPanel() {
	}
    }




    
    /**
     * Gives a description of this plugin.
     */
    /*        public String describe() {
        StringBuffer sb = new StringBuffer();
        sb.append("Bla Bla... ");
        sb.append(" more Bla.. ");
        return sb.toString();
    }
    */
    
        
    /**
     * This class gets attached to the menu item.
     */
    public class GpuLayoutAction extends CytoscapeAction 
    {
	
	/**
	 * The constructor sets the text that should appear on the menu item.
	 */
    public GpuLayoutAction() {
	super("GPU Layout Plugin");
    }
	
	/**
	 * This method is called when the user selects the menu item.
	 */
	public void actionPerformed(ActionEvent ae) {

	/*  Show message on screen    */
	
	String message = "Execute GPU Layout!";
	// use the CytoscapeDesktop as parent for a Swing dialog
	JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);

	


	}
	
    }
}

