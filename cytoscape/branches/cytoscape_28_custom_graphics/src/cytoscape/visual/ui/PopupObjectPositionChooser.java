/*
 File: PopupLabelPositionChooser.java

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

import giny.model.GraphObject;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import cytoscape.Cytoscape;
import cytoscape.visual.ObjectPosition;
import cytoscape.visual.ObjectPositionImpl;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.parsers.ObjectToString;

/**
 *
 */
public class PopupObjectPositionChooser extends JDialog implements
		PropertyChangeListener {

	private static final long serialVersionUID = 7146654020668346430L;
	protected ObjectPosition lp;
	protected ObjectPosition newlp;

	/**
	 * DOCUMENT ME!
	 * 
	 * @param f
	 *            DOCUMENT ME!
	 * @param pos
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static ObjectPosition showDialog(final Window f, final ObjectPosition pos) {
		final ObjectPosition currentPosition;
		
		if(pos == null)
			currentPosition = (ObjectPosition) Cytoscape
				.getVisualMappingManager().getVisualStyle()
				.getNodeAppearanceCalculator().getDefaultAppearance().get(
						VisualPropertyType.NODE_LABEL_POSITION);
		else
			currentPosition = pos;

		final PopupObjectPositionChooser placer = new PopupObjectPositionChooser(
				f, true, currentPosition);
		System.out.println("--------- Got New Position: "
				+ placer.getObjectPosition());
		return placer.getObjectPosition();
	}
	

	private PopupObjectPositionChooser(final Window f, boolean modal,
			ObjectPosition pos) {
		super();
		this.setModal(modal);
		this.setLocationRelativeTo(f);
		init(pos);
	}

	private void init(ObjectPosition pos) {
		if (pos == null)
			lp = new ObjectPositionImpl();
		else
			lp = pos;

		newlp = new ObjectPositionImpl(lp);

		setTitle("Select Object Placement");

		JPanel placer = new JPanel();
		placer.setLayout(new BoxLayout(placer, BoxLayout.Y_AXIS));
		placer.setOpaque(true); // content panes must be opaque

		// Set up and connect the gui components.
		ObjectPlacerGraphic graphic = new ObjectPlacerGraphic(
				new ObjectPositionImpl(lp));
		ObjectPlacerControl control = new ObjectPlacerControl(
				new ObjectPositionImpl(lp));

		control.addPropertyChangeListener(graphic);
		control.addPropertyChangeListener(this);

		graphic.addPropertyChangeListener(control);
		graphic.addPropertyChangeListener(this);

		placer.add(graphic);
		placer.add(control);

		JPanel buttonPanel = new JPanel();
		final JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lp = newlp;
				dispose();
			}
		});
		ok.addActionListener(control);

		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		placer.add(buttonPanel);
		add(placer);

		pack();
		setVisible(true);
	}

	private ObjectPosition getObjectPosition() {
		return lp;
	}

	/**
	 * Handles all property changes that the panel listens for.
	 */
	public void propertyChange(PropertyChangeEvent e) {
		final String type = e.getPropertyName();

		if (type.equals(ObjectPlacerGraphic.OBJECT_POSITION_CHANGED)
				&& e.getNewValue() instanceof ObjectPosition) {

			newlp = (ObjectPosition) e.getNewValue();

			// horrible, horrible hack
			GraphObject go = BypassHack.getCurrentObject();
			if (go != null) {
				String val = ObjectToString.getStringValue(newlp);
				Cytoscape.getNodeAttributes().setAttribute(
						go.getIdentifier(),
						VisualPropertyType.NODE_LABEL_POSITION
								.getBypassAttrName(), val);
				Cytoscape.getVisualMappingManager().getNetworkView()
						.redrawGraph(false, true);
			}
		}
	}
}
