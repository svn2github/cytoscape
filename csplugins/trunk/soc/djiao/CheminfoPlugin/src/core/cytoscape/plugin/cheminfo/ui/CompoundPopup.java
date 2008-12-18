/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.plugin.cheminfo.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Frame;
import java.awt.Image;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import giny.model.GraphObject;
import giny.view.EdgeView;
import giny.view.NodeView;

import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;

import cytoscape.plugin.cheminfo.model.Compound;
import cytoscape.plugin.cheminfo.model.Compound.AttriType;

public class CompoundPopup extends JDialog implements ComponentListener {
	
	private List<Compound> compoundList;
	private Map<Component, Compound> imageMap;

	public CompoundPopup(List<Compound> compoundList, GraphObject go) {
		super(Cytoscape.getDesktop());
		this.compoundList = compoundList;
		this.imageMap = new HashMap();
		if (go instanceof CyNode) {
			setTitle("2D Structures for Node "+((CyNode)go).getIdentifier());
		} else  {
			setTitle("2D Structures for Edge "+((CyEdge)go).getIdentifier());
		}
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBackground(Color.WHITE);

		addImages(400);
		pack();
		setVisible(true);
	}

	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {
		Component labelComponent = e.getComponent();
		// Get our new width
		int width = labelComponent.getWidth();
		int height = labelComponent.getHeight();
		// Is it in our map?
		if (imageMap.containsKey(labelComponent)) {
			JLabel label = (JLabel)(labelComponent);
			Image img = imageMap.get(labelComponent).getImage(width,height);
			label.setIcon(new ImageIcon(img));
		}
	}

	private void addImages(int width) {
		// How many images do we have?
		int structureCount = compoundList.size();
		int nCols = (int)Math.sqrt((double)structureCount);
		GridLayout layout = new GridLayout(nCols, structureCount/nCols, 1, 1);
		setLayout(layout);

		for (Compound compound: compoundList) {
			// Get the image
			Image img = compound.getImage(width/nCols, width/nCols);
			JLabel label = new JLabel(new ImageIcon(img));
			label.addComponentListener(this);
			imageMap.put(label, compound);
			add (label);
		}
	}
}
