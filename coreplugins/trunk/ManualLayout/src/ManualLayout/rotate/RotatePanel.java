
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;


/**
 *
 * GUI for rotation of manualLayout
 *
 *      Rewrite based on the class RotateAction       9/13/2006        Peng-Liang Wang
 *
 */
public class RotatePanel extends JPanel {
	/**
	 * 
	 */
	public JCheckBox jCheckBox;

	/**
	 * 
	 */
	public JSlider jSlider;

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
		jCheckBox = new JCheckBox();
		jCheckBox.setText("Rotate Selected Nodes Only");

		GridBagConstraints gbc = new GridBagConstraints();

		//setBorder(javax.swing.BorderFactory
		//                           .createEmptyBorder(0,10,0,10));
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

		/*
		// Disable the checkBox if nothing is selected
		if (Cytoscape.getCurrentNetworkView().getSelectedNodeIndices().length == 0)
		{
		  jCheckBox.setEnabled(false);
		  jCheckBox.setEnabled(false);
		}
		else
		{
		    jCheckBox.setEnabled(true);
		    jCheckBox.setEnabled(true);
		}
		*/
	} // constructor
} // End of class RotatePanel
