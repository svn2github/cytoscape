package cytoscape.filters.dialogs;

import y.base.*;
import y.view.*;

import y.algo.GraphHider;

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
public class EdgeTypeDialog extends FilterDialog {
    public static String DESC = "Select edges that are of any of the selected types.";
    GraphObjAttributes edgeAttributes;
    JList typeList;
    String[] interactionTypes;

    public EdgeTypeDialog(GraphObjAttributes edgeAttributes,
			     String[] interactionTypes) {
	super(FilterDialog.EDGE_TYPE);
	panel.setName("Edge Type");
	panel.add(createDescPanel(DESC));
	this.edgeAttributes = edgeAttributes;
	this.interactionTypes = interactionTypes;

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
	EdgeTypeFilter f;
	// used for the side-effect
	setValid();
	Object[] accO = typeList.getSelectedValues();
	String[] accTypes = new String[accO.length];
	for(int i = 0; i < accO.length; i++) {
	    accTypes[i] = (String) accO[i];
	}
	f = new EdgeTypeFilter(g, edgeAttributes, accTypes);
	return f;
    }

    public String[] getTypes() {
	return interactionTypes;
    }

}
