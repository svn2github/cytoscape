
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

package ManualLayout.control.actions.stack;

import ManualLayout.control.actions.AbstractControlAction;

import cytoscape.*;

import cytoscape.data.*;

import cytoscape.util.*;

import cytoscape.view.*;

import giny.model.*;

import giny.view.*;

import java.awt.event.*;

import java.util.*;

import javax.swing.*;


/**
 *
 */
public class VStackCenter extends AbstractControlAction {
	/**
	 * Creates a new VStack object.
	 *
	 * @param icon  DOCUMENT ME!
	 */
	public VStackCenter(ImageIcon icon) {
		super("Veritcal Stack", icon);
	}

	protected void control(List nodes) {
		if (nodes.size() <= 1)
			return;

		Collections.sort(nodes, new YComparator());

		
//		double d = Y_max - Y_min;
//		d = d / (nodes.size() - 1);

		//Note: X, Y are at node centers
		for (int i = 1; i < nodes.size(); i++) {
			((NodeView) nodes.get(i)).setYPosition(
					((NodeView) nodes.get(i-1)).getYPosition() + 
					((NodeView) nodes.get(i-1)).getHeight() *0.5 +
					((NodeView) nodes.get(i)).getHeight() * 0.5
					);
			((NodeView) nodes.get(i)).setXPosition(
					((NodeView) nodes.get(i-1)).getXPosition()  
					);
			
		}
	}
}
