/*
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
package org.cytoscape.coreplugin.cpath.model;

import java.util.Observable;


/**
 * Encapsulates Current Selections of the User.
 * For example, this class stores which Search Request/Response has the
 * current focus, and which Interactor / Interaction has the current focus.
 *
 * @author Ethan Cerami
 */
public class UserSelection extends Observable {
	/**
	 * Property Change:  Interactor Focus Has Changed.
	 */
	public static final Integer INTERACTOR_CHANGED = new Integer(1);

	/**
	 * Property Change:  Interaction Focus Has Changed.
	 */
	public static final Integer INTERACTION_CHANGED = new Integer(2);

	/**
	 * The Currently Selected Interactor.
	 */

	//    private Interactor selectedInteractor;

	/**
	 * Selected Node ID.
	 */
	private String selectedNodeId;

	/**
	 * The Currently Selected Interaction.
	 */

	//    private Interaction selectedInteraction;

	/**
	 * Current Search Request / Response that is in Focus.
	 */
	private int currentSearchIndex;

	/**
	 * Gets the Currently Selected Interactor.
	 *
	 * @return Interactor Object.
	 */

	//    public Interactor getSelectedInteractor() {
	//        return selectedInteractor;
	//    }

	/**
	 * Gets the ID of the Currently Selected Node.
	 *
	 * @return Node ID.
	 */
	public String getSelectedNodeId() {
		return this.selectedNodeId;
	}

	/**
	 * Gets the Currently Selected Interaction.
	 *
	 * @return Interaction Object.
	 */

	//    public Interaction getSelectedInteraction() {
	//        return selectedInteraction;
	//    }

	/**
	 * Sets the Currently Selected Interactor.
	 *
	 * @param nodeId             Node Identifier.
	 * @param selectedInteractor Interactor Object.
	 */

	//    public void setSelectedInteractor(String nodeId,
	//            Interactor selectedInteractor) {
	//        this.selectedInteractor = selectedInteractor;
	//        this.selectedNodeId = nodeId;
	//        this.selectedInteraction = null;
	//        this.setChanged();
	//        this.notifyObservers(INTERACTOR_CHANGED);
	//    }

	/**
	 * Sets the Currently Selected Interaction.
	 *
	 * @param selectedInteraction Interaction Object.
	 */

	//    public void setSelectedInteraction(Interaction selectedInteraction) {
	//        this.selectedInteraction = selectedInteraction;
	//        this.selectedInteractor = null;
	//        this.selectedNodeId = null;
	//        this.setChanged();
	//        this.notifyObservers(INTERACTION_CHANGED);
	//    }
}
