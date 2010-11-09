/*
 Copyright (c) 2006, 2007, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.view.manual.internal.common;


import org.cytoscape.view.manual.internal.rotate.RotatePanel;
import org.cytoscape.view.manual.internal.rotate.RotationLayouter;

import org.cytoscape.view.manual.internal.scale.ScaleLayouter;
import org.cytoscape.view.manual.internal.scale.ScalePanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class CheckBoxTracker implements SelectEventListener, SetCurrentNetworkListener {
	private JCheckBox jCheckBox;
	private Set<CyNetwork> listeningNetworks;

	public CheckBoxTracker(JCheckBox j) {
		jCheckBox = j;
		listeningNetworks = new HashSet<CyNetwork>();
	}

	public void onSelectEvent(SelectEvent event) {
		jCheckBox.setSelected(Cytoscape.getCurrentNetworkView().getSelectedNodeIndices().length > 0);
	} 

	public void handleEvent(SetCurrentNetwork e) {
			CyNetwork curr = e.getNetwork();

			// only add this as a listener if it hasn't been done already
			if ( !listeningNetworks.contains(curr) )
				curr.addSelectEventListener(this);	
		
		 	// to make sure we're set intially	
			onSelectEvent(null);
	}
}
