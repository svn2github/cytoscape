/**************************************************************************************
Copyright (C) Gerardo Huck, 2011


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

package cytoscape.plugins.igraph;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.util.*;

import cytoscape.Cytoscape;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

import giny.model.*;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;


public class IsConnected extends CytoscapeAction {
    
    Boolean selectedOnly;
    
    public IsConnected(IgraphPlugin myPlugin, String name, boolean selectedOnly) {
	super(name);
	setPreferredMenu("Plugins.Igraph.IsConnected");
	this.selectedOnly = new Boolean(selectedOnly);
    }
	
    public void actionPerformed(ActionEvent e) {
	JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Is Connected?: " + isConnected(this.selectedOnly));
    }
    
    public boolean isConnected(boolean selectedOnly) {
	IgraphAPI.loadGraph(selectedOnly, false);
	return IgraphInterface.isConnected(); 
    }
    
}	
