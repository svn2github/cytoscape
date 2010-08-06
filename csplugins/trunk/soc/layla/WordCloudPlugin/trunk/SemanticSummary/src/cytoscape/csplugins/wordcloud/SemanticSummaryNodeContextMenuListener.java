/*
 File: SemanticSummaryNodeConextMenuListener.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */


package cytoscape.csplugins.wordcloud;

import javax.swing.JPopupMenu;

import giny.view.NodeView;
import ding.view.NodeContextMenuListener;

/**
 * When a node is selected this adds the SemanticSummaryAction to the
 * node's popup menu.
 * 
 * @author Layla Oesper
 * @version 1.0
 */

public class SemanticSummaryNodeContextMenuListener 
	implements NodeContextMenuListener
{
	//CONSTRUCTORS
	
	/**
	 * Creates a new SemanticSummaryNodeContextMenuListener object.
	 */
	public SemanticSummaryNodeContextMenuListener()
	{
		//Do nothing
	}
	
	
	//METHODS
	
	/**
	 * Add Semantic Summary to context menu.
	 * @param nodeView - the click nodeView
	 * @param menu - menu pop-up to add the Semantic Summary Option to
	 */
	public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu)
	{
		if (menu == null)
		{
			menu = new JPopupMenu();
		}
		menu.add(new CreateCloudAction());
	}
}
