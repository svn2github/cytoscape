/*
 File: ShowAboutPanelAction.java

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

import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

public class ShowAboutPanelAction extends CytoscapeAction 
{
	public ShowAboutPanelAction()
	{
		super("About");
	}
	
	public void actionPerformed(ActionEvent event)
	{
		AboutPanel aboutPanel = new AboutPanel();
		aboutPanel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		aboutPanel.pack();
		aboutPanel.setLocationRelativeTo(Cytoscape.getDesktop());
		aboutPanel.setVisible(true);
	}
}
