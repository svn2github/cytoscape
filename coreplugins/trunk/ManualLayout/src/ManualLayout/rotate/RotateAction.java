
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

import ManualLayout.common.GraphConverter2;

import cytoscape.Cytoscape;

//import LayoutCommon.MutablePolyEdgeGraphLayout;
import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *
 */
public class RotateAction extends AbstractAction {
	/**
	 * Creates a new RotateAction object.
	 */
	public RotateAction() {
		super("Rotate");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// set up the user interface
		JLabel jLabel = new JLabel();
		jLabel.setText("Rotate in Degrees:");

		final JSlider jSlider = new JSlider();
		jSlider.setMaximum(360);
		jSlider.setMajorTickSpacing(90);
		jSlider.setPaintLabels(true);
		jSlider.setPaintTicks(true);
		jSlider.setMinorTickSpacing(15);
		jSlider.setValue(0);

		final JCheckBox jCheckBox = new JCheckBox();
		jCheckBox.setText("Rotate Selected Nodes Only");

		GridLayout gridLayout = new GridLayout();
		gridLayout.setRows(3);

		JPanel jContentPane = new JPanel();
		jContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
		jContentPane.setLayout(gridLayout);
		jContentPane.add(jLabel, null);
		jContentPane.add(jSlider, null);
		jContentPane.add(jCheckBox, null);

		JDialog jDialog = new JDialog();
		jDialog.setSize(new java.awt.Dimension(249, 152));
		jDialog.setTitle("Rotate");
		jDialog.setContentPane(jContentPane);
		jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jDialog.setResizable(false);
		jDialog.setModal(false);

		// set up interface logic
		if (Cytoscape.getCurrentNetworkView().getSelectedNodeIndices().length == 0) {
			jCheckBox.setEnabled(false);
		}

		final MutablePolyEdgeGraphLayout[] nativeGraph = new MutablePolyEdgeGraphLayout[] {
		                                                     GraphConverter2
		                                                                                                                 .getGraphReference(16.0d,
		                                                                                                                                    true,
		                                                                                                                                    false)
		                                                 };

		final RotationLayouter[] rotation = new RotationLayouter[] {
		                                        new RotationLayouter(nativeGraph[0])
		                                    };

		jSlider.addChangeListener(new ChangeListener() {
				int prevValue = jSlider.getValue();

				public void stateChanged(ChangeEvent e) {
					if (jSlider.getValue() == prevValue)
						return;

					double radians = (((double) (jSlider.getValue() - prevValue)) * 2.0d * Math.PI) / 360.0d;
					rotation[0].rotateGraph(radians);
					Cytoscape.getCurrentNetworkView().updateView();

					prevValue = jSlider.getValue();
				}
			});

		jCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					nativeGraph[0] = GraphConverter2.getGraphReference(128.0d, true,
					                                                   jCheckBox.isSelected());
					rotation[0] = new RotationLayouter(nativeGraph[0]);
				}
			});

		jDialog.show();
	}
}
