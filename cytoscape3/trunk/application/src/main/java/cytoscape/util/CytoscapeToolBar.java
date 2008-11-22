/*
  File: CytoscapeToolBar.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.util;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.List;
import java.util.ArrayList;


/**
 *
 */
public class CytoscapeToolBar extends JToolBar implements CyToolBar {
	private final static long serialVersionUID = 1202339868655256L;
	private Map<CyAction,JButton> actionButtonMap; 
	private SortedMap<String,Integer> groupNameCount; 
	private List<JButton> buttonList; 

	/**
	 * Default constructor delegates to the superclass void constructor and then
	 * calls {@link #initializeCytoscapeToolBar()}.
	 */
	public CytoscapeToolBar() {
		super("Cytoscape Tools");
		actionButtonMap = new HashMap<CyAction,JButton>();
		groupNameCount = new TreeMap<String,Integer>();
		buttonList = new ArrayList<JButton>();
	}

	/**
	 * If the given Action has an absent or false inToolBar property, return;
	 * otherwise delegate to addAction( String, Action ) with the value of its
	 * preferredButtonGroup property, or null if it does not have that property.
	 */
	public boolean addAction(CyAction action) {

		if (!action.isInToolBar()) 
			return false;

		// At present we allow an Action to be in this tool bar only once.
		if ( actionButtonMap.containsKey( action ) )
			return false;

		JButton button = new JButton(action); 
		button.setBorderPainted(false);
		button.setRolloverEnabled(true);
		button.setText("");

		//  If SHORT_DESCRIPTION exists, use this as tool-tip
		String shortDescription = (String) action.getValue(Action.SHORT_DESCRIPTION);
		if (shortDescription != null) 
			button.setToolTipText(shortDescription);

		String button_group_name = action.getPreferredButtonGroup();
		if ( button_group_name == null )
			button_group_name = "";

		actionButtonMap.put(action, button);
		int addInd = getActionIndex(button_group_name);
		buttonList.add(addInd, button );

		addButtons();

		return true;
	}

	private void addButtons() {
		for ( JButton b : buttonList) 
			remove(b);
		for ( JButton b : buttonList) 
			add(b);
		validate();
	}


	/**
	 * Returns the appropriate index for placing the action as the last item in 
	 * a group where the groups themselves are ordered lexicographically by name.
	 */
	private int getActionIndex(String name) {
		if ( !groupNameCount.containsKey(name) )
			groupNameCount.put(name,0);
		
		int index = 0;

		for ( String groupName : groupNameCount.keySet() ) {
			final int groupCount = groupNameCount.get(groupName).intValue();
			index += groupCount; 
			if ( name.equals( groupName ) ) {
				groupNameCount.put( groupName, groupCount + 1 );
				break;
			}
		}
		//System.out.println("calculated action index: " + index + " for group name: " + name);
		return index;
	}

	/**
	 * If the given Action has an absent or false inToolBar property, return;
	 * otherwise if there's a button for the action, remove it.
	 */
	public boolean removeAction(CyAction action) {

		JButton button = actionButtonMap.remove(action);

		if (button == null) {
			return false;
		}

		remove(button);

		return true;
	} 

	public JToolBar getJToolBar() {
		return this;
	}
} 
