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


import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Vector;
import java.util.Enumeration;

import cytoscape.data.*;
import cytoscape.filters.*;
import cytoscape.filters.dialogs.*;

/** 
 * Class, which provides a dialog 
 * to combine filters of other dialogs.
 *
 * @author namin@mit.edu
 * @version 2002-02-11
 */
public class CombinationDialog extends FilterDialog {
    public static String DESC = 
	"<html>" +
	"Select nodes by combinining multiple filters:<br>" +
	"with <b><code>And</code></b>, select nodes that <b>all</b> of the individual filters would have selected;<br>" +
	"with <b><code>Or</code></b>,  select nodes that <b>any</b> of the individual filters would have selected." +
	"</html>";

    Vector dialogs;
    JList list;
    DefaultListModel listModel;
    MutableBoolean andType = new MutableBoolean(false);
    MutableBoolean stabilize = new MutableBoolean(false);

    public CombinationDialog(Vector dialogs) {
	super(FilterDialog.COMBINATION);
	panel.setName("Combination");
	panel.add(createDescPanel(DESC));

	this.dialogs = dialogs;

	listModel = new DefaultListModel();
	for (int i = 0; i < dialogs.size(); i++) {
	    FilterDialog dialog = (FilterDialog) dialogs.elementAt(i);
	    String name = dialog.getName();
	    listModel.addElement(name);
	}

	list = new JList(listModel);

	JScrollPane listScrollPane = new JScrollPane(list);
	JPanel listPanel = new JPanel();
	listPanel.setLayout(new BorderLayout());
	listPanel.add(listScrollPane, BorderLayout.CENTER);
	JPanel subPanel = FilterDialog.createSubPanel
	    ("Filters", 
	     new JPanel[] {
		 listPanel
	     });

	JPanel optionsPanel = FilterDialog.createSubPanel
	    ("Options", 
	     new JPanel[] {
		 FilterDialog.createFieldPanel
		 ("Combination Type",
		  new BoolPanel(andType, "And", "Or").getPanel()),
		 FilterDialog.createFieldPanel
		 ("Apply Filter Until Stable Point?", new BoolPanel
		     (stabilize, 
		      "Yes", 
		      "No (Only Once)").getPanel())
	     });

	panel.add(optionsPanel);
	panel.add(subPanel);

    }

    public boolean setValid() {
	boolean valid = true;
	clearInvalidMsg();

	int[] indices = list.getSelectedIndices();
	if (indices.length > 0) {
	    for (int i = 0; i < indices.length; i++) {
		int index = indices[i];
		FilterDialog dialog = (FilterDialog) dialogs.elementAt(index);
		if (!dialog.setValid()) {
		    valid = false;
		    addInvalidMsg(dialog.getName(), "is not valid");
		}
	    }
	} else {
	    valid = false;
	    addInvalidMsg("At least one filter should be included.");
	}
	return valid;
    }

    public Filter getFilter(Graph2D graph) {
	Filter f;

	int[] indices = list.getSelectedIndices();
	Filter[] filters = new Filter[indices.length];
	for (int i = 0; i < indices.length; i++) {
	    int index = indices[i];
	    FilterDialog dialog = (FilterDialog) dialogs.elementAt(index);
	    filters[i] = dialog.getFilter(graph);
	}

	f = new CombinationFilter(graph, filters, andType.booleanValue());

	if (stabilize.booleanValue()) {
	    f = new StableFilter(graph, f);
	}

	return f;
    }

    public void update(int newIndex) {
	int[] indices = list.getSelectedIndices();

	FilterDialog newDialog = (FilterDialog) dialogs.elementAt(newIndex);
	listModel.insertElementAt(newDialog.getName(), newIndex);

	// Update the selected indices.
	for(int i = 0; i < indices.length; i++) {
	    if (indices[i] >= newIndex) {
		indices[i] = indices[i] + 1;
	    }
	}
	list.clearSelection();
	list.setSelectedIndices(indices);
    }

    
}


