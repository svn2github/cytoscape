
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

package cytoscape.layout.manual.scale;

import cytoscape.Cytoscape;
import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;
import cytoscape.layout.manual.common.CheckBoxTracker;
import cytoscape.layout.manual.common.GraphConverter2;
import cytoscape.layout.manual.common.PolymorphicSlider;
import cytoscape.layout.manual.common.SliderStateTracker;
import org.cytoscape.view.ViewChangeEdit;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Hashtable;


/**
 *
 * GUI for scale of manualLayout
 *
 *      Rewrite based on the class ScaleAction       9/13/2006        Peng-Liang Wang
 *
 */
public class ScalePanel extends JPanel implements ChangeListener, PolymorphicSlider {

	JCheckBox jCheckBox;
	JSlider jSlider;
	int prevValue; 

	boolean startAdjusting = true;
	ViewChangeEdit currentEdit = null;

	/**
	 * Creates a new ScalePanel object.
	 */
	public ScalePanel() {
		// setup interface
		JLabel jLabel = new JLabel();
		jLabel.setText("Scale:");

		jSlider = new JSlider();

		jSlider.setMajorTickSpacing(100);
		jSlider.setPaintTicks(true);
		jSlider.setPaintLabels(true);

		jSlider.setMaximum(300);
		jSlider.setValue(0);
		jSlider.setMinimum(-300);

		jSlider.addChangeListener(this);


		prevValue = jSlider.getValue();

		Hashtable<Integer,JLabel> labels = new Hashtable<Integer,JLabel>();
		labels.put(Integer.valueOf(-300), new JLabel("1/8"));
		labels.put(Integer.valueOf(-200), new JLabel("1/4"));
		labels.put(Integer.valueOf(-100), new JLabel("1/2"));
		labels.put(Integer.valueOf(0), new JLabel("1"));
		labels.put(Integer.valueOf(100), new JLabel("2"));
		labels.put(Integer.valueOf(200), new JLabel("4"));
		labels.put(Integer.valueOf(300), new JLabel("8"));

		jSlider.setLabelTable(labels);

		jCheckBox = new JCheckBox();
		jCheckBox.setText("Scale Selected Nodes Only");

		new CheckBoxTracker( jCheckBox );

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 15, 0, 15);
		add(jLabel, gbc);

		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 15, 10, 15);
		add(jSlider, gbc);

		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 15, 0, 15);
		add(jCheckBox, gbc);

		new SliderStateTracker(this);

		setMinimumSize(new java.awt.Dimension(100,1000));
		setPreferredSize(new java.awt.Dimension(100,1000));
		setMaximumSize(new java.awt.Dimension(100,1000));
	} 

	public void updateSlider(int x) {
		prevValue = x;
		jSlider.setValue(x);
	}

	public int getSliderValue() {
		return jSlider.getValue();
	}

	public void stateChanged(ChangeEvent e) {
		if ( e.getSource() != jSlider )
			return;

		// only create the edit when we're beginning to adjust
		if ( startAdjusting ) { 
			currentEdit = new ViewChangeEdit(Cytoscape.getCurrentNetworkView(), "Scale");
			startAdjusting = false;
		}

		// do the scaling
		MutablePolyEdgeGraphLayout nativeGraph = GraphConverter2.getGraphReference(128.0d, true,
			                                                   jCheckBox.isSelected());
		ScaleLayouter scale = new ScaleLayouter(nativeGraph);

		double prevAbsoluteScaleFactor = Math.pow(2, ((double) prevValue) / 100.0d);

		double currentAbsoluteScaleFactor = Math.pow(2, ((double) jSlider.getValue()) / 100.0d);

		double neededIncrementalScaleFactor = currentAbsoluteScaleFactor / prevAbsoluteScaleFactor;

		scale.scaleGraph(neededIncrementalScaleFactor);
		Cytoscape.getCurrentNetworkView().updateView();
		prevValue = jSlider.getValue();

		// only post the edit when we're finished adjusting 
		if ( !jSlider.getValueIsAdjusting() ) { 
			currentEdit.post();
			startAdjusting = true;
		} 
	}
}
