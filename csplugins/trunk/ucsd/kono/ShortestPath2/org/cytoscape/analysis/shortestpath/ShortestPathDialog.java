package org.cytoscape.analysis.shortestpath;



import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.WindowConstants.*;
import javax.swing.border.*;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.*;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.plugin.CytoscapePlugin;

/**
 * Plugin for Cytoscape to find the shortest path between 2 nodes in a network.
 * It is possible to find the shortest path in directed and undirected networks
 * 
 * @author mrsva
 *
 */
public class ShortestPathDialog extends JDialog implements ActionListener {
	String selectedAttribute = null;
	JCheckBox dirButton;
	JCheckBox logButton;

	public ShortestPathDialog (Frame parent, String attribute) {
		super(parent, false);
		this.selectedAttribute = attribute;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Find Shorted Path using "+selectedAttribute);

		JPanel dataPanel = new JPanel();
		BoxLayout layout = new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS);
    dataPanel.setLayout(layout);

		JPanel checkBoxes = new JPanel(new GridLayout(2,1));
		// Set up our radio buttons (Directed vs Undirected)
		dirButton = new JCheckBox("Calculate path assuming directed edges");
		dirButton.setSelected(false);
		checkBoxes.add(dirButton);

		logButton = new JCheckBox("Weights are expectation values (will use negative log)");
		{
			logButton.setSelected(false);
			checkBoxes.add(logButton);
			// Is the negative log option available?
			CyAttributes EdgeAttributes = Cytoscape.getEdgeAttributes();
			byte type = EdgeAttributes.getType(selectedAttribute);
			if(type == EdgeAttributes.TYPE_INTEGER) {
				// No
				logButton.setEnabled(false);
			}
		}
		checkBoxes.add(logButton);
		checkBoxes.setBorder(new CompoundBorder(
                  BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                  new EmptyBorder(10,10,10,10)));

		dataPanel.add(checkBoxes);

    // Create the button box
    JPanel buttonBox = new JPanel();
    JButton doneButton = new JButton("Done");
    doneButton.setActionCommand("done");
    doneButton.addActionListener(this);

    JButton findButton = new JButton("Find Shortest Path");
    findButton.setActionCommand("find");
    findButton.addActionListener(this);
    buttonBox.add(doneButton);
    buttonBox.add(findButton);
    buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

    dataPanel.add(buttonBox);
    setContentPane(dataPanel);
	}
	
	public void actionPerformed(ActionEvent e) {
		if ("done".equals(e.getActionCommand())) {
			setVisible(false);
		} else if ("find".equals(e.getActionCommand())) {
			ShortestPath sp = new ShortestPath();
			if (logButton.isSelected()) {
				sp.setNegativeLog(true);
			} else {
				sp.setNegativeLog(false);
			}
			if (dirButton.isSelected()) {
				sp.calculate(true,selectedAttribute); 
			} else {
				sp.calculate(false,selectedAttribute); 
			}
		}
	}
}
