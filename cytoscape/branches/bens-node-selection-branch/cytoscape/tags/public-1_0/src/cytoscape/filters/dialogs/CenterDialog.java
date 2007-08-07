package cytoscape.filters.dialogs;

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
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/


import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import cytoscape.data.*;
import cytoscape.filters.*;
import cytoscape.filters.dialogs.*;

/** 
 * Class, which provides a topology dialog
 * within the filter dialog.
 *
 * @author namin@mit.edu
 * @version 2002-02-11
 */
public class CenterDialog extends FilterDialog {
    public static String DESC = "Select nodes that are within the indicated depth of specific nodes.";

    Frame parent;
    Graph2D graph;

    // nNeighbors
    int nNeighbors;
    JTextField nNeighborsField;

    // maxDepth
    int maxDepth;
    JTextField maxDepthField;

    // starters
    JLabel startersLabel;
    Filter startersF;
    boolean startersSelected = false;

    public CenterDialog(Frame parent, Graph2D graph) {
	super(FilterDialog.CENTER);
	this.parent = parent;
	this.graph = graph;

	panel.setName("Nodes Center");
	panel.add(createDescPanel(DESC));

	// starter nodes selection mechanism
	JButton startersButton = new JButton("Get Selected Nodes");
	startersButton.addActionListener(new StartersSelectAction());
	startersLabel = new JLabel("None selected");
	startersLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));

	JPanel startersInternalPanel = new JPanel();
	startersInternalPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	startersInternalPanel.add(startersButton);
	startersInternalPanel.add(startersLabel);

	JPanel startersPanel = FilterDialog.createFieldPanel
	    ("Starter Nodes",
	     startersInternalPanel);

	nNeighborsField = new JTextField(4);
	JPanel nNeighborsPanel = FilterDialog.createFieldPanel
	    ("Minimum Number of Neighbors", 
	     nNeighborsField);

	maxDepthField = new JTextField(4);
	JPanel maxDepthPanel = FilterDialog.createFieldPanel
	    ("Within Depth",
	     maxDepthField);

	JPanel subPanel = FilterDialog.createSubPanel
	    ("Requirements", 
	     new JPanel[] {
		 startersPanel, 
		 nNeighborsPanel,
		 maxDepthPanel
	     });


	panel.add(subPanel);

	createStarters();
    }

    public boolean setValid() {
	boolean valid = true;
	String posIntVerbalGroup = "should be a positive integer";
	
	clearInvalidMsg();

	if (!startersSelected) {
	    valid = false;
	    addInvalidMsg("Starter nodes", "sould be selected (at least one node)");
	}

	nNeighbors = FilterDialog.parsePosInt(nNeighborsField.getText());
	if (nNeighbors == -1) {
	    valid = false;
	    // default
	    nNeighbors = 1;
	    addInvalidMsg("Number of Neighbors", posIntVerbalGroup);
	}

	maxDepth = FilterDialog.parsePosInt(maxDepthField.getText());
	if (maxDepth == -1) {
	    valid = false;
	    // default
	    maxDepth = 1;
	    addInvalidMsg("Depth", posIntVerbalGroup);
	}

	return valid;
    }

    public Filter getFilter(Graph2D graph) {
	Filter f;
	// used for the side-effect
	setValid();
	f = new TopologyCenteredFilter(graph, startersF, nNeighbors, maxDepth);
	return f;
    }

    public class StartersSelectAction extends AbstractAction {
	public void actionPerformed (ActionEvent e) {
	    if (!createStarters()) {
		JOptionPane.showMessageDialog(parent, 
					      "Please select the starter nodes from the graph first.");		
	    }
	}
    }

    private boolean createStarters() {
	NodeList selected = new NodeList();
	for (NodeCursor nc = graph.selectedNodes(); nc.ok(); nc.next()) {
	    Node node = nc.node();
	    selected.add(node);
	}
	
	int n = selected.size();
	if (n == 0) {
	    startersSelected = false;
	} else {
	    startersSelected = true;
	    startersLabel.setText(n + " selected");
	}

	startersF = new ListFilter(graph, selected);

	return startersSelected;
    }
}


