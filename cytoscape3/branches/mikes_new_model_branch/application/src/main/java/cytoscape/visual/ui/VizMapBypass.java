/*
 File: VizMapBypass.java

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
package cytoscape.visual.ui;

import cytoscape.Cytoscape;
import org.cytoscape.model.GraphObject;
import org.cytoscape.model.CyRow;
import org.cytoscape.vizmap.ObjectToString;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualPropertyType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;


/**
 * An abstract class providing common methods and data structures to the
 * Node and Edge bypass classes.
 */
abstract class VizMapBypass {
	protected Frame parent = Cytoscape.getDesktop();
	protected VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
	protected GraphObject graphObj = null;

	abstract protected List<String> getBypassNames();

	protected void addResetAllMenuItem(JMenu menu) {
		JMenuItem jmi = new JMenuItem(new AbstractAction("Reset All") {
				private final static long serialVersionUID = 1202339876700753L;
				public void actionPerformed(ActionEvent e) {
					List<String> names = getBypassNames();
					CyRow row = graphObj.attrs();

					for (String attrName : names)
						row.set("name",""); // TODO should be null instead?

					Cytoscape.redrawGraph(vmm.getNetworkView());
					BypassHack.finished();
				}
			});
		menu.add(jmi);
	}

	protected void addResetMenuItem(JMenu menu, final VisualPropertyType type) {
		JMenuItem jmi = new JMenuItem(new AbstractAction("[ Reset " + type.getName() + " ]") {
				private final static long serialVersionUID = 1202339876709140L;
				public void actionPerformed(ActionEvent e) {
					CyRow row = graphObj.attrs();

					row.set(type.getBypassAttrName(),""); // TODO set to null instead?

					Cytoscape.redrawGraph(vmm.getNetworkView());
					BypassHack.finished();
				}
			});
		menu.add(jmi);
	}

	protected void addMenuItem(JMenu menu, final VisualPropertyType type) {
		final JMenuItem jmi = new JCheckBoxMenuItem(new AbstractAction(type.getName()) {
				private final static long serialVersionUID = 1202339876717506L;
				public void actionPerformed(ActionEvent e) {
					Object obj = null;

					try {
						obj = EditorFactory.showDiscreteEditor(type);
					} catch (Exception ex) {
						ex.printStackTrace();
						obj = null;
					}

					if (obj == null)
						return;

					String val = ObjectToString.getStringValue(obj);
					graphObj.attrs().set(type.getBypassAttrName(), val);
					Cytoscape.redrawGraph(vmm.getNetworkView());
					BypassHack.finished();
				}
			});

		menu.add(jmi);
		
		// Check node size lock state 
		if(type.equals(VisualPropertyType.NODE_SIZE)) {
			if(Cytoscape.getVisualMappingManager().getVisualStyle().getNodeAppearanceCalculator().getNodeSizeLocked() == false) {
				jmi.setEnabled(false);
			}
		} else if(type.equals(VisualPropertyType.NODE_WIDTH) || type.equals(VisualPropertyType.NODE_HEIGHT)) {
			if(Cytoscape.getVisualMappingManager().getVisualStyle().getNodeAppearanceCalculator().getNodeSizeLocked() == true) {
				jmi.setEnabled(false);
			}
		}

		String attrString = graphObj.attrs().get(type.getBypassAttrName(),String.class);

		if ((attrString == null) || (attrString.length() == 0))
			jmi.setSelected(false);
		else {
			jmi.setSelected(true);
			addResetMenuItem(menu, type);
		}
	}
}
