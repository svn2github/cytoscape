
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

package org.cytoscape.cyprovision.internal;

import org.cytoscape.cyprovision.*;
//import org.eclipse.equinox.internal.provisional.p2.ui.operations.ProvisioningUtil;
//import org.cytoscape.cyprovision.internal.CyProvisionActivator;

//import cytoscape.Cytoscape;
///import cytoscape.util.CytoscapeAction;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 */
//public class CyProvisionMenuItemAction extends CytoscapeAction {
public class CyProvisionMenuItemAction { //extends CytoscapeAction {
	//protected JFrame frame;
	protected CyProvisionActivator thePlugin;

	/**
	 * Creates a new FilterMenuItem object.
	 *
	 * @param icon  DOCUMENT ME!
	 * @param csfilter  DOCUMENT ME!
	 */
	public CyProvisionMenuItemAction(ImageIcon icon, CyProvisionActivator pPlugin) {
		//super("Software Updates", icon);
		thePlugin = pPlugin;
		//setPreferredMenu("Help");
		//setAcceleratorCombo(java.awt.event.KeyEvent.VK_F7, 0);
	}


	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		System.out.println("Software update menuItem is clicked!");
		//java.net.URI uri = new java.net.URI("");
		//ProvisioningUtil.addMetadataRepository(uri);
		//ProvisioningUtil.addArtifactRepository(uri);

	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isInToolBar() {
		return false;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isInMenuBar() {
		return true;
	}
}
