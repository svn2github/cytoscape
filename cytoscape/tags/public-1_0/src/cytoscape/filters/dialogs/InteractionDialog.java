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
import cytoscape.*;
/** 
 * Class, which provides an expression dialog
 * within the filter dialog.
 *
 * @author namin@mit.edu
 * @version 2002-02-11
 */
public class InteractionDialog extends FilterDialog {
    public static String DESC = "Select nodes that are in any interaction of the selected types.";
    GraphObjAttributes edgeAttributes;
    JList typeList;
    String[] interactionTypes;
    RadioPanel search;

    public InteractionDialog(GraphObjAttributes edgeAttributes,
			     String[] interactionTypes) {
	super(FilterDialog.INTERACTION);
	panel.setName("Interaction");
	panel.add(createDescPanel(DESC));
	this.edgeAttributes = edgeAttributes;
	this.interactionTypes = interactionTypes;

	search = new RadioPanel (new String[] {"source", "target", "both"}, "both");

	JPanel searchSubPanel = FilterDialog.createSubPanel
	    ("Search in", 
	     new JPanel[] {
		 search.getPanel()
	     });
	panel.add(searchSubPanel);

	typeList = new JList(getTypes());
	JPanel listPanel = new JPanel();
	listPanel.setLayout(new BorderLayout());
	listPanel.add(typeList, BorderLayout.CENTER);
	JPanel subPanel = FilterDialog.createSubPanel
	    ("Interaction Types", 
	     new JPanel[] {
		 listPanel
	     });

	panel.add(subPanel);	
    }

    public boolean setValid() {
	boolean valid = true;
	clearInvalidMsg();
	return valid;
    }

    public Filter getFilter(Graph2D g) {
	InteractionFilter f;
	// used for the side-effect
	setValid();
	Object[] accO = typeList.getSelectedValues();
	String[] accTypes = new String[accO.length];
	for(int i = 0; i < accO.length; i++) {
	    accTypes[i] = (String) accO[i];
	}
	f = new InteractionFilter(g, edgeAttributes,
				  accTypes, search.toString());
	return f;
    }

    public String[] getTypes() {
	return interactionTypes;
    }
}


