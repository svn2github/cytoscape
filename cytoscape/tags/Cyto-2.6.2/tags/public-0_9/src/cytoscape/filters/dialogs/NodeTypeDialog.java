package cytoscape.filters.dialogs;

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
public class NodeTypeDialog extends FilterDialog {
    public static String DESC = "Select nodes that are of any of the selected types.";
    GraphObjAttributes nodeAttributes;

    JList typeList;

    public NodeTypeDialog(GraphObjAttributes nodeAttributes) {
	super(FilterDialog.NODE_TYPE);
	panel.setName("Node Type");
	panel.add(createDescPanel(DESC));
	this.nodeAttributes = nodeAttributes;

	typeList = new JList(getTypes());
	JPanel listPanel = new JPanel();
	listPanel.setLayout(new BorderLayout());
	listPanel.add(typeList, BorderLayout.CENTER);
	JPanel subPanel = FilterDialog.createSubPanel
	    ("Node Types", 
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
	NodeTypeFilter f;
	// used for the side-effect
	setValid();
	Object[] accO = typeList.getSelectedValues();
	String[] accTypes = new String[accO.length];
	for(int i = 0; i < accO.length; i++) {
	    accTypes[i] = (String) accO[i];
	}
	f = new NodeTypeFilter(g, nodeAttributes,
			       accTypes);
	return f;
    }

    public String[] getTypes() {
	return new String[]{
	    "gene",
	    "compound",
	    "reaction"
	};
    }
}


