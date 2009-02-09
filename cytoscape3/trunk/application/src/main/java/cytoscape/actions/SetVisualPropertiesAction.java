/*
 File: SetVisualPropertiesAction.java

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

//-------------------------------------------------------------------------
// $Revision: 12968 $
// $Date: 2008-02-06 15:34:25 -0800 (Wed, 06 Feb 2008) $
// $Author: mes $
//------------------------------------------------------------------------------
package cytoscape.actions;

import cytoscape.CyNetworkManager;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CySwingApplication;

import javax.swing.*;
import java.awt.event.ActionEvent;


//------------------------------------------------------------------------------
/**
 *
 */
public class SetVisualPropertiesAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339870882109L;
	private static String title = "Open VizMapper\u2122";
	private CySwingApplication desktop;
	/**
	 * Creates a new SetVisualPropertiesAction object.
	 */
	public SetVisualPropertiesAction(CySwingApplication desktop, CyNetworkManager netmgr) {
		super(title, new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_file-with-objects-16.png")),netmgr);
		setPreferredMenu("View");
		setEnabled(true);
		this.desktop = desktop;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		desktop.getCytoPanel(SwingConstants.WEST).setSelectedIndex(1);
	}
}
