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

import cytoscape.data.*;
import cytoscape.filters.*;
import cytoscape.filters.dialogs.*;
import cytoscape.*;
/** 
 * Old javadoc comments from copied code (-owo 2003.01.28):
 *
 * Class, which provides an expression dialog
 * within the filter dialog.
 *
 * @author namin@mit.edu
 * @version 2002-02-11
 */
public class AttributeDialog extends FilterDialog {
    public static String DESC = "Select all nodes whose attribute value matches a particular user-defined value.";
    GraphObjAttributes nodeAttributes;

    AttributeSelection attributeSelection;
    String matchingValue;
    JTextField matchField;

    public AttributeDialog( GraphObjAttributes nodeAttributes) {
	super(FilterDialog.ATTRIBUTE);
	panel.setName("Attribute");
	panel.add(createDescPanel(DESC));
	this.nodeAttributes = nodeAttributes;

	attributeSelection = new AttributeSelection(nodeAttributes);

	JPanel attributeSelectionPanel = FilterDialog.createFieldPanel
	    ("Attribute for selection:", attributeSelection);

	matchField = new JTextField(4);
	JPanel matchPanel = FilterDialog.createFieldPanel
	    ("Matching value:", matchField);

	panel.add(attributeSelectionPanel);
	panel.add(matchPanel);

    }

    public boolean setValid() {
	boolean valid = true;
	matchingValue = matchField.getText();
	/*
	matchingValue = FilterDialog.parseDouble(matchField.getText());
	if (matchingValue == -1) {
	    valid = false;
	    // default
	    matchingValue = 1;
	    addInvalidMsg("Matching value", "should be a number");
	}
	*/
	if(attributeSelection.getAttributeSelected()==null) {
	    valid = false;
	    addInvalidMsg("Attribute", "needs to be selected");
	}
	return valid;
    }

    public Filter getFilter(Graph2D g) {
	AttributeFilter f;
	// used for the side-effect
	setValid();
	f = new AttributeFilter(g, nodeAttributes,
				attributeSelection.getAttributeSelected(),
				matchingValue);
	return f;
    }
}


