package org.cytoscape.app.internal.ui;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class AppManagerDialog extends JDialog {

	private static final long serialVersionUID = 7046019190157943640L;

	public AppManagerDialog() {

		// Create tabbed pane
		JTabbedPane tabbedPane = new JTabbedPane();
		this.add(tabbedPane);
		
		JPanel searchPanel = new JPanel();
		JPanel listPanel = new JPanel();
		JPanel updatePanel = new JPanel();

		// Add panels to tabbed pane
		tabbedPane.addTab("Get Apps", searchPanel);
		tabbedPane.addTab("Current Apps", listPanel);
		tabbedPane.addTab("Update Apps", updatePanel);
		
		this.setVisible(true);
	}
}
