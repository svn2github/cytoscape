package ManualLayout.control;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import cytoscape.Cytoscape;
import ManualLayout.control.view.AlignPanel;
import ManualLayout.control.view.DistPanel;

/**
 * 
 * GUI for Align and Distribute of manualLayout
 * 
 *  	Rewrite based on the class ControlAction   	9/13/2006		Peng-Liang Wang
 * 
 */
public class ControlPanel extends JPanel {

	public ControlPanel() {

		setBorder(javax.swing.BorderFactory.createEmptyBorder(3,3,3,3));

		AlignPanel ap = new AlignPanel();
		DistPanel dp = new DistPanel();

	    setLayout(new GridBagLayout());

	    GridBagConstraints gbc = new GridBagConstraints();

	    gbc.gridy =0;
	    gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 3, 0, 3);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

	    add(ap,gbc);

	    gbc.gridy =1;
	    add(dp, gbc);
	
	} // constructor

} // End of class ControlPanel
