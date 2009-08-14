package org.cytoscape.search.ui.filter;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.Dimension;

public class TopologyPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * This is the default constructor
	 */
	public TopologyPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(309, 538);
		this.setLayout(new GridBagLayout());
	}

}  //  @jve:decl-index=0:visual-constraint="194,17"
