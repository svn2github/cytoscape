/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/

/**
 * A plug-in that allows the user to expand a graph's interactions and/or nodes using different methods.
 *  
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 */

package graphExpander;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import cytoscape.*;
import cytoscape.plugin.CytoscapePlugin;
import graphExpander.gui.GraphExpanderDialog;

public class GraphExpanderPlugin extends CytoscapePlugin {
	
	public static final String PLUGIN_NAME = "Graph Expander";
	protected static GraphExpanderDialog graphExpanderDialog = new GraphExpanderDialog(Cytoscape.getDesktop());
	
	/**
	 * Constructor.
	 */
	public GraphExpanderPlugin (){
		
		Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
	             new AbstractAction(PLUGIN_NAME + "..."){
	               public void actionPerformed (ActionEvent e){
	                 GraphExpanderPlugin.graphExpanderDialog.pack();
	                 GraphExpanderPlugin.graphExpanderDialog.setLocationRelativeTo(Cytoscape.getDesktop());
	                 GraphExpanderPlugin.graphExpanderDialog.setVisible(true);
	               }
	             });
		
	}//constructor
	
	public String describe (){
		return "A plug-in that allows you to expand a graph's interactions and/or nodes using different methods.";
	}//describe
	
	/**
	 * @return the GraphExpanderDialog for this plugin
	 */
	public GraphExpanderDialog getDialog (){
		return GraphExpanderPlugin.graphExpanderDialog;
	}// getDialog

}//class GraphExpanderPlugin
