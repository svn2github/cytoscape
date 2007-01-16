package ManualLayout.scale;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import javax.swing.JCheckBox;
//import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
//import javax.swing.JMenu;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;

import cytoscape.Cytoscape;
//import cytoscape.util.CytoscapeAction;
//import cytoscape.view.cytopanels.CytoPanelState;

import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;
import ManualLayout.common.GraphConverter2;

/**
 * 
 * GUI for scale of manualLayout
 * 
 *  	Rewrite based on the class ScaleAction   	9/13/2006		Peng-Liang Wang
 * 
 */
public class ScalePanel extends JPanel {

	public JCheckBox jCheckBox;
	public JSlider jSlider;

	public ScalePanel() {
	    // setup interface
	    
	    JLabel jLabel = new JLabel();
	    jLabel.setText("Scale:");
	    
	    jSlider = new JSlider();
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

	    jCheckBox = new JCheckBox();
	    jCheckBox.setText("Scale Selected Nodes Only");

	    //setBorder(javax.swing.BorderFactory
	     //                           .createEmptyBorder(0,10,0,10));
	    
	    setLayout(new GridBagLayout());

	    GridBagConstraints gbc = new GridBagConstraints();

	    gbc.gridy =0;
	    gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 15, 0, 15);
	    add(jLabel,gbc);
	    
	    gbc.gridy =1;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
	    add(jSlider, gbc);

	    gbc.gridy =2;
	    gbc.anchor = GridBagConstraints.WEST;
	    gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 15, 0, 15);
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

} // End of class ScalePanel
