/*
 File: CloudListMouseListener.java

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

package cytoscape.csplugins.semanticsummary;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

/**
 * This class handles all mouse actions associated with the list of
 * clouds displayed in the Input Panel for the Semantic Summary.
 * @author Layla Oesper
 * @version 1.0
 */

public class CloudListMouseListener extends MouseAdapter 
{
	//Methods
	
	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isRightMouseButton(e))
		{
			rightClickList(e);
		}
		
		else if (SwingUtilities.isLeftMouseButton(e))
		{
			leftClickList(e);
		}
	}
	
	
	//TODO
	private void rightClickList(MouseEvent e)
	{
		//RightClickMenu menu = new RightClickPopUpMenu();
		JPopupMenu menu = new JPopupMenu();
		menu.add(new ChangeCloudNameAction());
		menu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	private void leftClickList(MouseEvent e)
	{
		Point selPoint = new Point(e.getX(), e.getY());
		
		//Get current name
		JList cloudList = SemanticSummaryManager.getInstance().
		getInputWindow().getCloudList();
		
		int selIndex = cloudList.getSelectedIndex();
		
		int clickIndex = cloudList.locationToIndex(selPoint);
		
		if (selIndex == clickIndex)
		{
			cloudList.clearSelection();
			cloudList.setSelectedIndex(selIndex);
		}
	
	}
	
	public class ChangeCloudNameAction extends CytoscapeAction
	{
		//CONSTRUCTOR
		public ChangeCloudNameAction()
		{
			super("Edit Cloud Name");
		}
		
		//METHODS
		public void actionPerformed(ActionEvent ae)
		{
			//Get parent
			Component parent = Cytoscape.getDesktop();
			
			//Get current name
			JList cloudList = SemanticSummaryManager.getInstance().
			getInputWindow().getCloudList();
			
			int index = cloudList.getSelectedIndex();
			
			//Check for no network selected
			if (index == -1)
			{
				JOptionPane.showMessageDialog(parent, 
				"You must fist select a Cloud.");
				return;
			}
			
			DefaultListModel listValues = SemanticSummaryManager.
			getInstance().getInputWindow().getListValues();
			
			//Get name of currently selected cloud
			String curName = (String) listValues.get(index);
			
			//Variables for use
			String newName = "";
			int value = JOptionPane.NO_OPTION;
			Object[] options = { "Try Again", "Cancel"};
			
			//Show dialog box to change Cloud Name
			
			//loop until acceptable action
			while (true)
			{
				//Setup Dialog
				EditCloudNameDialog theDialog = 
					new EditCloudNameDialog(parent, true, curName);
				
				theDialog.setLocationRelativeTo(parent);
				theDialog.setVisible(true);
				newName = theDialog.getNewCloudName();
				
				//Same as old name
				if (curName.equals(newName))
					break;
				
				//Blank or null name
				else if ((newName == null) || newName.trim().equals(""))
				{
					newName = curName;
					break;
				}
				
				//Already taken name
				else if (isCloudNameTaken(newName))
				{
					value = JOptionPane.showOptionDialog(parent,
							"That cloud name alread exists, try again.",
							"Duplicate Cloud Name",
							JOptionPane.WARNING_MESSAGE,
							JOptionPane.YES_NO_CANCEL_OPTION,
							null,
							options,
							options[0]);
					
					if (value == JOptionPane.NO_OPTION)
					{
						newName = curName;
						break;
					}
				}
				else 
					break;
			}//end while true loop
			
			//Set new cloud title stuffs here
			CloudParameters cloudParams = SemanticSummaryManager.getInstance()
			.getCurCloud();
			
			SemanticSummaryParameters networkParams = SemanticSummaryManager.
			getInstance().getCurNetwork();
			
			networkParams.getClouds().remove(curName);
			
			cloudParams.setCloudName(newName);
			
			networkParams.addCloud(newName, cloudParams);
			
			//Update InputPanelList
			listValues.setElementAt(newName, index);
			cloudList.updateUI();
			
		}//end actionPerformed
		
		/**
		 * Returns true if the specified name is already taken in the
		 * current network.
		 */
		public boolean isCloudNameTaken(String name)
		{
			return SemanticSummaryManager.getInstance().
					getCurNetwork().containsCloud(name);
		}
		
	}//end changeCloudNameAction class
	
}
