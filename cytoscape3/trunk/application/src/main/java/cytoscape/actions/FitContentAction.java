/*
  File: FitContentAction.java

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
// $Revision: 13022 $
// $Date: 2008-02-11 13:59:26 -0800 (Mon, 11 Feb 2008) $
// $Author: mes $
//-------------------------------------------------------------------------
package cytoscape.actions;

import cytoscape.CyNetworkManager;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;


//-------------------------------------------------------------------------
/**
 *
 */
public class FitContentAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339869547666L;
	/**
	 * Creates a new FitContentAction object.
	 */
	public FitContentAction(CyNetworkManager netmgr) {
		super("",new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_zoom-1.png")),netmgr);
		putValue(SHORT_DESCRIPTION,"Zoom out to display all of current Network");
	}

	public boolean isInToolBar() {
		return true;
	}

	public String getPreferredButtonGroup() {
   		return "c-zoom";
   	}	

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		netmgr.getCurrentNetworkView().fitContent();
	}
}
