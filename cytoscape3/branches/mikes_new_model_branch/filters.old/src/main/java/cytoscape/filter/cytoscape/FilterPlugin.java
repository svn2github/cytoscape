
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.filter.cytoscape;

import cytoscape.util.CytoscapeAction;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 *
 */
public class FilterPlugin extends CytoscapeAction {
	protected JFrame frame;
	protected CsFilter csfilter;

	/**
	 * Creates a new FilterPlugin object.
	 *
	 * @param icon  DOCUMENT ME!
	 * @param csfilter  DOCUMENT ME!
	 */
	public FilterPlugin(ImageIcon icon, CsFilter csfilter) {
		super("", icon);
		//  Set SHORT_DESCRIPTION;  used to create tool-tip
		this.putValue(Action.SHORT_DESCRIPTION, "Use Filters");
		this.csfilter = csfilter;
		setPreferredMenu("Select");
		//setAcceleratorCombo(java.awt.event.KeyEvent.VK_A,
		//                    ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public FilterUsePanel getFilterUsePanel() {
		return csfilter.getFilterUsePanel();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		csfilter.show();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isInToolBar() {
		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isInMenuBar() {
		return false;
	}
}
