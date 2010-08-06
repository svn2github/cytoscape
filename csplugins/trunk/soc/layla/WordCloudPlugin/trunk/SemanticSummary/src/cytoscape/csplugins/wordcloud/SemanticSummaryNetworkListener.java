/*
 File: SemanticSummaryNetworkListener.java

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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;

/**
 * When a new cytoscape network view is created, this class will register
 * a SemanticSummaryNodeContextMenuListener with the new DGraphView.
 * 
 * @author Layla Oesper
 * @version 1.0
 */

public class SemanticSummaryNetworkListener implements PropertyChangeListener
{
	//CONSTRUCTORS
	
	public SemanticSummaryNetworkListener()
	{
		//Do nothing
	}
	
	//METHODS
	
	/**
	 * Registers a SemanticSummaryNodeContextMenuListener for all
	 * new DGraphView objects
	 * @param evnt PropertyChangeEvent
	 */
	public void propertyChange(PropertyChangeEvent evnt)
	{
		if (evnt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED)
		{
			//Register NodeContexts
			SemanticSummaryNodeContextMenuListener nodeMenuListener = 
				new SemanticSummaryNodeContextMenuListener();
			
			((DGraphView)Cytoscape.getCurrentNetworkView()).
			addNodeContextMenuListener(nodeMenuListener);
		}
	}
}
