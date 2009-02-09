/*
  File: PreferenceAction.java

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
// $Revision: 12984 $
// $Date: 2008-02-08 13:12:37 -0800 (Fri, 08 Feb 2008) $
// $Author: mes $
//-------------------------------------------------------------------------
package cytoscape.actions;

import cytoscape.CyNetworkManager;
import cytoscape.dialogs.preferences.PreferencesDialog;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CySwingApplication;

import javax.swing.*;
import java.awt.event.ActionEvent;

import java.util.Properties;


/**
 *
 */
public class PreferenceAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339870248574L;
	/**
	 * Creates a new PreferenceAction object.
	 */
	private CySwingApplication desktop;
	private Properties props;
	public PreferenceAction(CySwingApplication desktop, CyNetworkManager netmgr, Properties props) {
		super("Properties...",netmgr);
		this.desktop = desktop;
		this.props = props;
		System.out.println("PreferenceAction()...");
		setPreferredMenu("Edit.Preferences");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		PreferencesDialog preferencesDialog = new PreferencesDialog(desktop.getJFrame(), props);
		preferencesDialog.refresh();
		preferencesDialog.setVisible(true);
	} 
}
