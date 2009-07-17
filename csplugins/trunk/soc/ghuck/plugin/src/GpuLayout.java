/**************************************************************************************
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

**************************************************************************************/
package GpuLayout;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.CyNode;

import giny.model.GraphPerspective;
import giny.model.Node;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.Iterator;
import java.lang.reflect.Field;
import GpuLayout.*; 




/**
 * This plugin provides a GPU assited graph layout utility by calling CUDA C++ code
 */
public class GpuLayout extends CytoscapePlugin {

	
    /**
     * Adds a menu entry and creates an instance of ForceDirected
     */
    public GpuLayout() {	
	// Add Layout to menu
	CyLayouts.addLayout(new ForceDirected(), "GPU Assisted Layout");

        //Show message on screen with AdjMatIndex and AdjMatVals   
	//String message2 = "GpuLayout Plugin loaded!\n"; 
        //JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message2);

    }
    

}








