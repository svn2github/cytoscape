//
// RotateSelectionDialog.java
//
// $Revision$
// $Date$
// $Author$
//


package cytoscape.dialogs;

import y.base.*;
import y.view.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.layout.*;


/**
 * Rotates the current selection in the given graph in the given
 * window interactively, through the use of a JSlider.
 */
public class RotateSelectionDialog extends JDialog {
    JDialog thisDialog;
	
    public RotateSelectionDialog (JFrame parentFrame, CytoscapeWindow window,
				  Graph2D graph) {
	super (parentFrame, "Rotate Selected Nodes", true);

	thisDialog = this;

	JLabel sliderLabel = new JLabel("Angle",
					JLabel.CENTER);
	sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

	JPanel actionButtonsPanel = new JPanel();
	actionButtonsPanel.setLayout(new FlowLayout());

	JButton dismissButton = new JButton("Dismiss");
	actionButtonsPanel.add(dismissButton);
	dismissButton.addActionListener(new DismissAction());

	JPanel sliderPanel = new JPanel();
	sliderPanel.setLayout(new FlowLayout());

	// new slider from -180 to +180 degrees
	JSlider angle = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
	sliderPanel.add(angle);
	angle.setMajorTickSpacing(90);
	angle.setPaintLabels(true);
	angle.addChangeListener(new RotateSelectionAngleListener(window,
								 graph));
	    
	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	panel.add(sliderPanel, BorderLayout.CENTER);
	panel.add(actionButtonsPanel, BorderLayout.SOUTH);

	setContentPane(panel);
	pack();
	this.setLocation(parentFrame.getLocationOnScreen());
	pack();
	setVisible(true);
    }

    protected class RotateSelectionAngleListener implements ChangeListener {
	CytoscapeWindow window;
	Graph2D graph;
	double last = 0.0;
	    
	RotateSelectionAngleListener (CytoscapeWindow window,
				      Graph2D graph) {
	    this.window = window;
	    this.graph = graph;
	}

	public void stateChanged(ChangeEvent e) {
	    JSlider source = (JSlider)e.getSource();
		
	    double angle = (source.getValue() / 180.0 * Math.PI);
	    RotateLayoutHelper.rotate(graph, graph.selectedNodes(),
				      angle - last);

	    last = angle;

	    window.redrawGraph();
	}
    }

    protected class DismissAction extends AbstractAction {
	DismissAction () {super (""); }
	public void actionPerformed (ActionEvent e) {
	    thisDialog.dispose();
	}
    }

}
