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
public class TopologyDialog extends FilterDialog {
    public static String DESC = "Select nodes that have the indicated number of neighbors within the indicated depth.";
    // nNeighbors
    int nNeighbors;
    JTextField nNeighborsField;

    // maxDepth
    int maxDepth;
    JTextField maxDepthField;

    MutableBoolean stabilize = new MutableBoolean(true);

    public TopologyDialog() {
	super(FilterDialog.TOPOLOGY);
	panel.setName("Topology");
	panel.add(createDescPanel(DESC));

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
		 nNeighborsPanel, 
		 maxDepthPanel
	     });

	JPanel optionsPanel = FilterDialog.createSubPanel
	    ("Options",
	     new JPanel[] {
		 FilterDialog.createFieldPanel
		 ("Apply Filter Until Stable Point?", new BoolPanel
		     (stabilize, 
		      "Yes", 
		      "No (Only Once)").getPanel())
	     });

	panel.add(subPanel);
	panel.add(optionsPanel);
    }

    public boolean setValid() {
	boolean valid = true;
	String posIntVerbalGroup = "should be a positive integer";
	
	clearInvalidMsg();

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

    public Filter getFilter(Graph2D g) {
	Filter f;
	// used for the side-effect
	setValid();
	if (stabilize.booleanValue()) {
	    f = new TopologyFilter(g, nNeighbors, maxDepth);
	} else {
	    f = new TopologyOnePassFilter(g, nNeighbors, maxDepth);
	}

	return f;
    }
}







