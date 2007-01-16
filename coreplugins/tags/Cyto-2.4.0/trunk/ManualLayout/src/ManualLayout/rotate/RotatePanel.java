package ManualLayout.rotate;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import cytoscape.Cytoscape;

/**
 * 
 * GUI for rotation of manualLayout
 * 
 *  	Rewrite based on the class RotateAction   	9/13/2006		Peng-Liang Wang
 * 
 */
public class RotatePanel extends JPanel {

	public JCheckBox jCheckBox;
	public JSlider jSlider;
		
	public RotatePanel() {
	    // set up the user interface

	    JLabel jLabel = new JLabel();
	    jLabel.setText("Rotate in Degrees:");
	    jLabel.setPreferredSize(new Dimension(120,50));

	    jSlider = new JSlider();
	    jSlider.setMaximum(360);
	    jSlider.setMajorTickSpacing(90);
	    jSlider.setPaintLabels(true);
	    jSlider.setPaintTicks(true);
	    jSlider.setMinorTickSpacing(15);
	    jSlider.setValue(0);
	    
	    jSlider.setPreferredSize(new Dimension(120,50));
	    jCheckBox = new JCheckBox();
	    jCheckBox.setText("Rotate Selected Nodes Only");

	    GridBagConstraints gbc = new GridBagConstraints();
	    
	    //setBorder(javax.swing.BorderFactory
	     //                           .createEmptyBorder(0,10,0,10));
	    
	    setLayout(new GridBagLayout());

	    gbc.gridy =0;
	    gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 15, 0, 15);
	    add(jLabel,gbc);

	    gbc.gridy =1;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    add(jSlider, gbc);
	    
	    gbc.gridy =2;
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
