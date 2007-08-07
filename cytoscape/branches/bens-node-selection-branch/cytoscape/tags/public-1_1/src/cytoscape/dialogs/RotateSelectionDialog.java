//

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

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


