/* vim: set ts=2:

  File: ClusterSettingsDialog.java

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
  Dout of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package clusterMaker.ui;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ObjectMapping;

import clusterMaker.treeview.dendroview.ColorExtractor;
import clusterMaker.algorithms.hierarchical.EisenCluster;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * The NetworkColorDialog is a dialog that provides a mechanism to map colors from
 * the HeatMap to the network.
 */
public class NetworkColorDialog extends JDialog 
                                implements ActionListener, ListSelectionListener {

	private ColorExtractor colorExtractor = null;
	private String attribute = null;
	private List<String>attributeList = null;
	private double maxValue;
	private double minValue;

	// Dialog components
	private JLabel titleLabel; // Our title
	private JPanel mainPanel; // The main content pane
	private JPanel buttonBox; // Our action buttons (Save Settings, Cancel, Execute, Done)
	private JList attributeSelector; // Which algorithm we're using
	private JButton animateButton;

	private boolean animating = false;

	/**
	 * Creates a new NetworkColorDialog object.
	 */
	public NetworkColorDialog(JFrame parent, ColorExtractor ce, 
	                          List<String>attributes, double minValue, double maxValue, 
	                          boolean symmetric) {
		super(parent, "Map Colors to Network", false);
		colorExtractor = ce;
		attributeList = attributes;
		this.maxValue = maxValue;
		this.minValue = minValue;

		if (symmetric) {
			CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
			CyNetwork network = Cytoscape.getCurrentNetwork();
			String attribute = networkAttributes.getStringAttribute(network.getIdentifier(), EisenCluster.CLUSTER_EDGE_ATTRIBUTE);
			VisualStyle style = createNewStyle(attribute.substring(5), "-heatMap", true, true);
			return;
		}
		// How many attributes are there?
		if (attributeList.size() == 1) {
			// Only one, so just do it (no dialog)
			VisualStyle style = createNewStyle(attributeList.get(0), "-heatMap", true, false);
		} else {
			initializeOnce(); // Initialize the components we only do once
			pack();
			setVisible(true);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// Are we the source of the event?
		String command = e.getActionCommand();

		if (command.equals("done")) {
			animating = false;
			this.setVisible(false);
		} else if (command.equals("up")) {
			shiftList(-1);
		} else if (command.equals("down")) {
			shiftList(1);
		} else if (command.equals("vizmap")) {
			String attribute = (String)attributeSelector.getSelectedValue();

			VisualStyle style = createNewStyle(attribute, "-heatMap", true, false);
		} else if (command.equals("animate")) {
			if (animating) {
				animating = false;
				animateButton.setText("Animate Vizmap");
				return;
			}

			// Get the selected attributes
			Object[] attributeArray = attributeSelector.getSelectedValues();
			if (attributeArray.length < 2) {
				// Really nothing to animate if we only have one map
			}

			// Build the necessary vizmap entries
			VisualStyle[] styles = new VisualStyle[attributeArray.length];
			for (int i = 0; i < attributeArray.length; i++) {
				styles[i] = createNewStyle((String)attributeArray[i], "-"+(String)attributeArray[i], false, false);
			}

			// Change the animate button
			animateButton.setText("Stop animation");
			animating = true;
			// Set up the animation task
			Animate a = new Animate(styles);
			a.start();
		}
	}
			
	private VisualStyle createNewStyle(String attribute, String suffix, boolean update, boolean edge) { 
		boolean newStyle = false;

		// Get our current vizmap
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog calculatorCatalog = manager.getCalculatorCatalog();

		// Get the current style
		VisualStyle style = Cytoscape.getCurrentNetworkView().getVisualStyle();
		// Get our colors
		Color missingColor = colorExtractor.getMissing();
		Color zeroColor = colorExtractor.getColor(0.0f);
		Color upColor = colorExtractor.getColor(maxValue);
		Color downColor = colorExtractor.getColor(minValue);

		// Adjust for contrast.  Since we really don't have contrast control,
		// we try to provide the same basic idea by adding extra points
		// in the continuous mapper
		Color downDeltaColor = colorExtractor.getColor(minValue/100.0);
		Color upDeltaColor = colorExtractor.getColor(maxValue/100.0);

		if (!style.getName().endsWith(suffix)) {
			// Create a new vizmap
			Set<String> styles = calculatorCatalog.getVisualStyleNames();
			if (styles.contains(style.getName()+suffix))
				style = calculatorCatalog.getVisualStyle(style.getName()+suffix);
			else {
				style = new VisualStyle(style, style.getName()+suffix);
				newStyle = true;
			}
		}

		// Get the right mapping, depending on whether we are mapping an edge or a node
		byte mapping = ObjectMapping.NODE_MAPPING;
		VisualPropertyType vizType = VisualPropertyType.NODE_FILL_COLOR;
		if (edge) {
			mapping = ObjectMapping.EDGE_MAPPING;
			vizType = VisualPropertyType.EDGE_COLOR;
		}

		// Create the new continuous mapper
		ContinuousMapping colorMapping = new ContinuousMapping(missingColor, mapping);
		colorMapping.setControllingAttributeName(attribute, Cytoscape.getCurrentNetwork(), false);
		
		colorMapping.addPoint (minValue,
       new BoundaryRangeValues (downColor, downColor, downColor));
		colorMapping.addPoint (minValue/100.0,
       new BoundaryRangeValues (downDeltaColor, downDeltaColor, downDeltaColor));
   	colorMapping.addPoint(0,
       new BoundaryRangeValues (zeroColor, zeroColor, zeroColor));
   	colorMapping.addPoint(maxValue/100.0,
       new BoundaryRangeValues (upDeltaColor, upDeltaColor, upDeltaColor));
   	colorMapping.addPoint(maxValue,
       new BoundaryRangeValues (upColor, upColor, upColor));

   	Calculator colorCalculator = new BasicCalculator("TreeView Color Calculator", 
		                                                 colorMapping, vizType);


		// Apply it
	
		if (edge) {
			EdgeAppearanceCalculator edgeAppCalc = style.getEdgeAppearanceCalculator();
   		edgeAppCalc.setCalculator(colorCalculator);
			style.setEdgeAppearanceCalculator(edgeAppCalc);
		} else {
			NodeAppearanceCalculator nodeAppCalc = style.getNodeAppearanceCalculator();
   		nodeAppCalc.setCalculator(colorCalculator);
			style.setNodeAppearanceCalculator(nodeAppCalc);
		}
		if (newStyle) {
			calculatorCatalog.addVisualStyle(style);
			if (update) {
				Cytoscape.getCurrentNetworkView().setVisualStyle(style.getName());
				manager.setVisualStyle(style);
			}
		} else if (update) {
			Cytoscape.getCurrentNetworkView().applyVizmapper(style);
		} 
		return style;
	}

	private void initializeOnce() {
		boolean enableAnimation = false;

		setDefaultCloseOperation(HIDE_ON_CLOSE);

		// Create our main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		// See if we have more than one attribute
		if (attributeList.size() > 1) {
			// We do, so enable the animation UI
			enableAnimation = true;
		}

		// Create our JList
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.LINE_AXIS));
		this.attributeSelector = new JList(attributeList.toArray());
		attributeSelector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		attributeSelector.addListSelectionListener(this);
		JScrollPane listScroller = new JScrollPane(attributeSelector);
		listScroller.setPreferredSize(new Dimension(200,100));
		listPanel.add(listScroller);
		Border listBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(listBorder, "Attribute List");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		listPanel.setBorder(titleBorder);

		// Now add the sorting arrows
		JPanel sortPanel = new JPanel();
		sortPanel.setLayout(new BoxLayout(sortPanel, BoxLayout.PAGE_AXIS));
		{
			JButton upButton = new JButton("^");
			upButton.setActionCommand("up");
			upButton.addActionListener(this);
			sortPanel.add(upButton);
		}

		{
			JButton downButton = new JButton("v");
			downButton.setActionCommand("down");
			downButton.addActionListener(this);
			sortPanel.add(downButton);
		}

		listPanel.add(sortPanel);
		mainPanel.add(listPanel);

		// Create a panel for our button box
		this.buttonBox = new JPanel();

		JButton doneButton = new JButton("Done");
		doneButton.setActionCommand("done");
		doneButton.addActionListener(this);

		JButton vizmapButton = new JButton("Create Vizmap");
		vizmapButton.setActionCommand("vizmap");
		vizmapButton.addActionListener(this);

		animateButton = new JButton("Animate Vizmap");
		animateButton.setActionCommand("animate");
		animateButton.addActionListener(this);
		animateButton.setEnabled(false);

		buttonBox.add(animateButton);
		buttonBox.add(vizmapButton);
		buttonBox.add(doneButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		mainPanel.add(buttonBox);
		setContentPane(mainPanel);
	}

	public void valueChanged(ListSelectionEvent e) {
		// Get the selected items
		int[] selIndices = attributeSelector.getSelectedIndices();

		// If there are more then one, enable the animate button
		if (selIndices.length > 1)
			animateButton.setEnabled(true);
		else
			animateButton.setEnabled(false);
	}

	public void shiftList(int amount) {
		int[] selIndices = attributeSelector.getSelectedIndices();
		// Remove each of he values from the list
		String[] removedValues = new String[selIndices.length];
		for (int i = 0; i < selIndices.length; i++) {
			removedValues[i] = attributeList.get(selIndices[i]);
		}

		// OK, now remove them
		for (int i = 0; i < selIndices.length; i++) {
			attributeList.remove(removedValues[i]);
			selIndices[i] += amount;
			if (selIndices[i] < 0) 
				selIndices[i] = 0;
			if (selIndices[i] > attributeList.size())
				selIndices[i] = attributeList.size();
		}

		// Re-insert them one up
		for (int i = 0; i < selIndices.length; i++) {
			attributeList.add(selIndices[i], removedValues[i]);
		}

		// OK, now update the list
		attributeSelector.setListData(attributeList.toArray());
		attributeSelector.setSelectedIndices(selIndices);
	}

	private class Animate extends Thread {
		VisualStyle[] styles;

		public Animate(VisualStyle[] styles) {
			this.styles = styles;
		}
		public void run() {
			while (animating) {
				// Cycle through the vizmaps we created
				for (int i = 0; i < styles.length; i++) {
					Cytoscape.getCurrentNetworkView().applyVizmapper(styles[i]);
					// Do we need to sleep a little????
					// Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
				}
			}
		}
	}

}
