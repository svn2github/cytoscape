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
