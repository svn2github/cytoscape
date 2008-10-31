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


/**
 *
 */
public class CytoscapeToolBar extends JToolBar {
	private final static long serialVersionUID = 1202339868655256L;
	protected Map<CyAction,JButton> actionButtonMap = null;

	/**
	 * Default constructor delegates to the superclass void constructor and then
	 * calls {@link #initializeCytoscapeToolBar()}.
	 */
	public CytoscapeToolBar() {
		super("Cytoscape Tools");
		initializeCytoscapeToolBar();
	}

	/**
	 * Envelop if you wish.  Presently does nothing.
	 */
	protected void initializeCytoscapeToolBar() {
	}

	/**
	 * If the given Action has an absent or false inToolBar property, return;
	 * otherwise delegate to addAction( String, Action ) with the value of its
	 * preferredButtonGroup property, or null if it does not have that property.
	 */
	public boolean addAction(CyAction action) {
		String button_group_name = null;

			if (action.isInToolBar()) {
				button_group_name = action.getPreferredButtonGroup();
			} else {
				return false;
			}

		return addAction(button_group_name, action);
	} 

	/**
	 * Note that this presently ignores the button group name.
	 */
	private boolean addAction(String button_group_name, CyAction action) {
		// At present we allow an Action to be in this tool bar only once.
		JButton button = null;

		if (actionButtonMap != null) {
			button = actionButtonMap.get(action);
		}

		if (button != null) {
			return false;
		}

		button = createJButton(action);
		button.setBorderPainted(false);
		button.setRolloverEnabled(true);

		//  If SHORT_DESCRIPTION exists, use this as tool-tip
		String shortDescription = (String) action.getValue(Action.SHORT_DESCRIPTION);

		if (shortDescription != null) {
			button.setToolTipText(shortDescription);
		}

		// TODO: Do something with the preferred button group.
		add(button);

		//add( action );
		if (actionButtonMap == null) {
			actionButtonMap = createActionButtonMap();
		}

		actionButtonMap.put(action, button);

		return true;
	} // addAction( button_group_name, action )

	/**
	 * If the given Action has an absent or false inToolBar property, return;
	 * otherwise if there's a button for the action, remove it.
	 */
	public boolean removeAction(CyAction action) {
		if (actionButtonMap == null) {
			return false;
		}

		JButton button = actionButtonMap.remove(action);

		if (button == null) {
			return false;
		}

		remove(button);

		return true;
	} 

	/**
	 * CytoscapeToolBars are unique -- this equals() method returns true
	 * iff the other object == this.
	 */
	public boolean equals(Object other_object) {
		return (this == other_object);
	} 

	/**
	 * Factory method for instantiating the buttons in the toolbar.
	 */
	protected JButton createJButton(CyAction action) {
		return new JButton(action);
	}

	/**
	 * Factory method for instantiating the action->button map.
	 */
	protected Map<CyAction,JButton> createActionButtonMap() {
		return new HashMap<CyAction,JButton>();
	}
} 
