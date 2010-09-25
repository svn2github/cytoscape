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

import clusterMaker.algorithms.ClusterAlgorithm;

import cytoscape.Cytoscape;

import cytoscape.task.util.TaskManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.WindowConstants.*;
import javax.swing.border.*;
import javax.swing.text.Position;


/**
 *
 * The ClusterSettingsDialog is a dialog that provides an interface into all of the
 * various settings for cluster algorithms.  Each ClusterAlgorithm must return a single
 * JPanel that provides all of its settings.
 */
public class ClusterSettingsDialog extends JDialog 
                                   implements ActionListener, PropertyChangeListener, ComponentListener {
	private ClusterAlgorithm currentAlgorithm = null;
	private ClusterViz visualizer = null;
	private JButton vizButton = null;

	// Dialog components
	private JLabel titleLabel; // Our title
	private JPanel mainPanel; // The main content pane
	private JPanel buttonBox; // Our action buttons (Save Settings, Cancel, Execute, Done)
	private JComboBox algorithmSelector; // Which algorithm we're using
	private JPanel algorithmPanel; // The panel this algorithm uses

	/**
	 * Creates a new ClusterSettingsDialog object.
	 */
	public ClusterSettingsDialog(ClusterAlgorithm algorithm) {
		super(Cytoscape.getDesktop(), algorithm.getName()+" Settings", false);
		currentAlgorithm = algorithm;
		visualizer = algorithm.getVisualizer();
		if (visualizer != null && visualizer != algorithm) 
		                      algorithm.getPropertyChangeSupport().addPropertyChangeListener(this);
		initializeOnce(); // Initialize the components we only do once
		setResizable(false);
	}

	public void showDialog() {
		initialize();
		pack();
		setLocationRelativeTo(Cytoscape.getDesktop());
		setVisible(true);
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
			updateAllSettings();
			setVisible(false);
		} else if (command.equals("save")) {
			updateAllSettings();
		} else if (command.equals("execute")) {
			// Cluster using the current layout
			updateAllSettings();
			TaskManager.executeTask( new ClusterTask(currentAlgorithm, this),
			                         ClusterTask.getDefaultTaskConfig() );
		} else if (command.equals("visualize")) {
			visualizer.startViz();
			setVisible(false);
		} else if (command.equals("cancel")) {
			// Call revertSettings for each layout
			revertAllSettings();
			setVisible(false);
		} else {
			// OK, initialize and display
			initialize();
			pack();
			setLocationRelativeTo(Cytoscape.getDesktop());
			setVisible(true);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ( evt.getPropertyName() == ClusterAlgorithm.CLUSTER_COMPUTED ){
			updateVizButton();
    }
	}

	public void updateVizButton() {
		if (visualizer != null && visualizer.isAvailable())
			vizButton.setEnabled(true);
		else
			vizButton.setEnabled(false);
	}

	public void componentHidden(ComponentEvent e) { }

	public void componentMoved(ComponentEvent e) { }

	public void componentResized(ComponentEvent e) {
		doLayout();
		pack();
	}

	public void componentShown(ComponentEvent e) { }


	private void initializeOnce() {
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		// Create our main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		// Create a panel for algorithm's content
		this.algorithmPanel = currentAlgorithm.getSettingsPanel();
		this.algorithmPanel.addComponentListener(this);
		// this.algorithmPanel.setPreferredSize(new Dimension(500,500));

		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(selBorder,
		currentAlgorithm.toString()+ " Settings");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		algorithmPanel.setBorder(titleBorder);
		mainPanel.add(algorithmPanel);

		// Create a panel for our button box
		this.buttonBox = new JPanel();

		JButton doneButton = new JButton("Done");
		doneButton.setActionCommand("done");
		doneButton.addActionListener(this);

		JButton saveButton = new JButton("Save Settings");
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);

		if (currentAlgorithm != visualizer) {
			JButton executeButton = new JButton("Create Clusters");
			executeButton.setActionCommand("execute");
			executeButton.addActionListener(this);

			vizButton = new JButton("Visualize Clusters");
			vizButton.setActionCommand("visualize");
			vizButton.addActionListener(this);
			if (visualizer != null && visualizer.isAvailable())
				vizButton.setEnabled(true);
			else
				vizButton.setEnabled(false);

			JButton cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("cancel");
			cancelButton.addActionListener(this);
			buttonBox.add(executeButton);
			buttonBox.add(vizButton);
			buttonBox.add(saveButton);
			buttonBox.add(cancelButton);
		} else {
			vizButton = new JButton("Show");
			vizButton.setActionCommand("visualize");
			vizButton.addActionListener(this);
			buttonBox.add(vizButton);
		}
		buttonBox.add(doneButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		mainPanel.add(buttonBox);
		setContentPane(mainPanel);
	}

	private void initialize() {
	}

	private void updateAllSettings() {
			currentAlgorithm.updateSettings();
	}

	private void revertAllSettings() {
			currentAlgorithm.revertSettings();
	}
}
