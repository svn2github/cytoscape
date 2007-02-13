
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

package ManualLayout.scale;

import ManualLayout.common.GraphConverter2;

import cytoscape.Cytoscape;

import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Hashtable;

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
public class ScaleAction extends AbstractAction {
	/**
	 * Creates a new ScaleAction object.
	 */
	public ScaleAction() {
		super("Scale");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// setup interface
		JLabel jLabel = new JLabel();
		jLabel.setText("Scale:");

		final JSlider jSlider = new JSlider();
		jSlider.setMaximum(300);
		jSlider.setMajorTickSpacing(100);
		jSlider.setPaintTicks(true);
		jSlider.setPaintLabels(true);
		jSlider.setValue(0);
		jSlider.setMinimum(-300);

		Hashtable labels = new Hashtable();
		labels.put(new Integer(-300), new JLabel("1/8"));
		labels.put(new Integer(-200), new JLabel("1/4"));
		labels.put(new Integer(-100), new JLabel("1/2"));
		labels.put(new Integer(0), new JLabel("1"));
		labels.put(new Integer(100), new JLabel("2"));
		labels.put(new Integer(200), new JLabel("4"));
		labels.put(new Integer(300), new JLabel("8"));

		jSlider.setLabelTable(labels);

		final JCheckBox jCheckBox = new JCheckBox();
		jCheckBox.setText("Scale Selected Nodes Only");

		GridLayout gridLayout = new GridLayout();
		gridLayout.setRows(3);

		JPanel jContentPane = new JPanel();
		jContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
		jContentPane.setLayout(gridLayout);
		jContentPane.add(jLabel, null);
		jContentPane.add(jSlider, null);
		jContentPane.add(jCheckBox, null);

		JDialog jDialog = new JDialog();
		jDialog.setSize(new java.awt.Dimension(341, 160));
		jDialog.setTitle("Scale");
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

		final ScaleLayouter[] scale = new ScaleLayouter[] { new ScaleLayouter(nativeGraph[0]) };

		jCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					nativeGraph[0] = GraphConverter2.getGraphReference(128.0d, true,
					                                                   jCheckBox.isSelected());
					scale[0] = new ScaleLayouter(nativeGraph[0]);
				}
			});

		jSlider.addChangeListener(new ChangeListener() {
				private int prevValue = jSlider.getValue();

				public void stateChanged(ChangeEvent e) {
					if (prevValue == jSlider.getValue())
						return;

					double prevAbsoluteScaleFactor = Math.pow(2, ((double) prevValue) / 100.0d);

					double currentAbsoluteScaleFactor = Math.pow(2,
					                                             ((double) jSlider.getValue()) / 100.0d);

					double neededIncrementalScaleFactor = currentAbsoluteScaleFactor / prevAbsoluteScaleFactor;

					scale[0].scaleGraph(neededIncrementalScaleFactor);
					Cytoscape.getCurrentNetworkView().updateView();
					prevValue = jSlider.getValue();
				}
			});

		jDialog.show();
	}
}
