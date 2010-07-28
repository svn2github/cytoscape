
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

package ManualLayout.rotate;

import cytoscape.Cytoscape;

import ding.view.ViewChangeEdit;
import ding.view.DGraphView;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import ManualLayout.common.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;

/**
 *
 * GUI for rotation of manualLayout
 *
 *      Rewrite based on the class RotateAction       9/13/2006        Peng-Liang Wang
 *
 */
public class RotatePanel extends JPanel implements ChangeListener, PolymorphicSlider {

	JCheckBox jCheckBox;
	JSlider jSlider;
	int prevValue; 

	boolean startAdjusting = true;
	ViewChangeEdit currentEdit = null;

	/**
	 * Creates a new RotatePanel object.
	 */
	public RotatePanel() {
		// set up the user interface
		JLabel jLabel = new JLabel();
		jLabel.setText("Rotate in Degrees:");
		jLabel.setPreferredSize(new Dimension(120, 50));

		jSlider = new JSlider();
		jSlider.setMaximum(360);
		jSlider.setMajorTickSpacing(90);
		jSlider.setPaintLabels(true);
		jSlider.setPaintTicks(true);
		jSlider.setMinorTickSpacing(15);
		jSlider.setValue(0);
		jSlider.setPreferredSize(new Dimension(120, 50));
		jSlider.addChangeListener(this);

		prevValue = jSlider.getValue();

		jCheckBox = new JCheckBox();
		jCheckBox.setText("Rotate Selected Nodes Only");

		new CheckBoxTracker( jCheckBox );

		GridBagConstraints gbc = new GridBagConstraints();

		setLayout(new GridBagLayout());

		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 15, 0, 15);
		add(jLabel, gbc);

		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(jSlider, gbc);

		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		add(jCheckBox, gbc);

		new SliderStateTracker(this);

		setMinimumSize(new Dimension(100,1000));
		setPreferredSize(new Dimension(100,1000));
		setMaximumSize(new Dimension(100,1000));
	} 

	public void updateSlider(int x) {
		// this will prevent the state change from producing a change
		prevValue = x;
		jSlider.setValue(x);
	}

	public int getSliderValue() {
		return jSlider.getValue();
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() != jSlider)
			return;

		// only create the edit at the beginning of the adjustment
		if ( startAdjusting ) {
			currentEdit = new ViewChangeEdit((DGraphView)(Cytoscape.getCurrentNetworkView()), "Rotate");
			startAdjusting = false;
		}

		MutablePolyEdgeGraphLayout nativeGraph = GraphConverter2.getGraphReference(128.0d, true,
		                                                   jCheckBox.isSelected());
		RotationLayouter rotation = new RotationLayouter(nativeGraph);

		double radians = (((double) (jSlider.getValue() - prevValue)) * 2.0d * Math.PI) / 360.0d;
		rotation.rotateGraph(radians);
		Cytoscape.getCurrentNetworkView().updateView();

		prevValue = jSlider.getValue();

		// only post edit when adjustment is complete
		if ( !jSlider.getValueIsAdjusting() ) {
			currentEdit.post();
			startAdjusting = true;
		}
	}
} 
