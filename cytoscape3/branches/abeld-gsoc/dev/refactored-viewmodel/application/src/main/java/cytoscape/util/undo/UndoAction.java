/*
  File: UndoAction.java

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
package cytoscape.util.undo;

import cytoscape.util.CytoscapeAction;
import cytoscape.CyNetworkManager;

import org.cytoscape.work.UndoSupport;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.undo.CannotUndoException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * An action that calls undo for the most recent edit in the
 * undoable edit stack.
 */
public class UndoAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339875212525L;

	private UndoSupport undo;

	/**
	 * Constructs the action.
	 */
	public UndoAction(UndoSupport undo,CyNetworkManager netmgr) {
		super("Undo",netmgr);
		setAcceleratorCombo(KeyEvent.VK_Z, ActionEvent.CTRL_MASK);
		setPreferredMenu("Edit");
		setEnabled(true);
		this.undo = undo;
	}

    /**
     * Tries to run undo() on the top edit of the edit stack.
     * @param e The action event that triggers this method call.
     */
	public void actionPerformed(ActionEvent e) {
		try {
			if ( undo.getUndoManager().canUndo() )
				undo.getUndoManager().undo();
		} catch (CannotUndoException ex) {
			System.out.println("Unable to undo: " + ex);
			ex.printStackTrace();
		}
	}

    /**
     * Called when the menu that contains this action is clicked on.
     * @param e The menu event that triggers this method call.
     */
	public void menuSelected(MenuEvent e) {
		if (undo.getUndoManager().canUndo()) {
			setEnabled(true);
			putValue(Action.NAME, undo.getUndoManager().getUndoPresentationName());
		} else {
			setEnabled(false);
			putValue(Action.NAME, "Undo");
		}
	}
}
