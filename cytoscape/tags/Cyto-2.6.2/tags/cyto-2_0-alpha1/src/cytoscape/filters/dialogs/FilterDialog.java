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
import javax.swing.border.TitledBorder;

import cytoscape.data.*;
import cytoscape.filters.*;

/** 
 * Abstract base class, for filter dialogs.
 *
 * @author namin@mit.edu
 * @version 2002-02-20
 */
public abstract class FilterDialog {
    /**
     * Types of filter dialogs.
     */
    public static int TOPOLOGY = 1;
    public static int EXPRESSION = 2;
    public static int COMBINATION = 3;
    public static int CENTER = 4;
    public static int INTERACTION = 5;
    public static int NODE_TYPE = 6;
    public static int PATHWAY = 7;
    public static int EDGE_TYPE = 8;
    public static int ATTRIBUTE = 9;

    private int type;

    /**
     * The message displayed when the user input is not valid.
     */
    private String invalidMsg;
    
    /**
     * The final panel, the user will get.
     */
    protected JPanel panel;
    private JPanel framePanel;

    public FilterDialog(int type) {
	panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

	clearInvalidMsg();

	this.type = type;
    }

    public int getType() {
	return type;
    }

    /**
     * Compares the type of the filter dialog with a given type.
     * <p>
     * Eg:
     * <code>filter.isType(FilterDialog.TOPOLOGY)</code>
     * @param guessType The type to compare with
     * @return True if the filter dialog is of the given type
     * 
     * @see #TOPOLOGY
     */
    public boolean isType(int guessType) {
	return type == guessType;
    }

    /**
     * 
     * @return A panel with all the dialog fields
     */
    public JPanel getPanel() {
	framePanel = new JPanel();
	framePanel.setLayout(new BorderLayout());
	framePanel.add(panel, BorderLayout.NORTH);
	framePanel.setName(panel.getName());
	return framePanel;
    }

    /**
     *
     * @return The name of the dialog.
     */
    public String getName() {
	return framePanel.getName();
    }

    /**
     * Gets the string which explains what and why the user input is invalid.
     *
     * @return An explanation message
     */
    public String getInvalidMsg() {
	if (invalidMsg.equals("")) {
	    // Typically, programs shouldn't call the method
	    // if the input is valid.
	    return "Valid input";
	} else {
	    return "<ul>" + invalidMsg + "</ul>";
	}
    }
 
    /**
     * Clear the message about the validity of the user input.
     */
    protected void clearInvalidMsg() {
	invalidMsg = "";
    }

    /**
     * Adds a message to the invalid messages.
     * 
     * @param fieldName the name of the field with invalid user input (eg "Number of Neighbors")
     * @param verbalGroup a verbal group describing the input invalidity 
     *                    (eg "should be a positive integer")
     */
    protected void addInvalidMsg(String fieldName, String verbalGroup) {
	addInvalidMsg("<tt>" + fieldName + "</tt> " + verbalGroup + ".");
    }

    /**
     * Adds a message to the invalid messages.
     * 
     * @param text The text to add
     */
    protected void addInvalidMsg(String text) {
	invalidMsg += "<li>" + text + "</li>";
    }
    
    /**
     * Checks whether all the fields contain valid input,
     * while also setting the corresponding field variables.
     *
     * @return True if all the input is valid, false otherwise
     */    
    public abstract boolean setValid();

    /**
     * Builds a topology filter from the specified user input,
     * or with some default values if the user input is invalid.
     *
     * @param g The graph to which the filter apply
     * @return A filter as specified by the user input
     */
    public abstract Filter getFilter(Graph2D g);

    /**
     * Converts a string to an integer.
     * 
     * @param s The string to convert to an integer
     * @return Argument as integer, 
     *         if it can be converted to a non-negative integer, -1 otherwise
     *
     */
    public static int parsePosInt(String s) {
	int invalid = -1;
	int i = invalid;
	if (s != null) {
	    try {
		i = Integer.parseInt(s);
		if (i < 0) {
		    i = invalid;
		}
	    } catch (NumberFormatException e) {
		i = invalid;
	    }
	}
	return i;
    }

    /**
     * Converts a string to a double.
     * 
     * @param s The string to convert to a double.
     * @return Argument as double,
     *         if conversion fails, -1
     *
     */
    public static double parseDouble(String s) {
	double invalid = -1;
	double d = invalid;
	if (s != null) {
	    try {
		d = Double.valueOf(s).doubleValue();
	    } catch (NumberFormatException e) {
		d = invalid;
	    }
	}
	return d;
    }

    /**
     * Creates a panel with 
     * a given component and its label.
     *
     * @param labelString The string displayed by the label
     * @param field The component (usually a text field) included in the panel
     *
     * @return A panel for the text field
     */
    public static JPanel createFieldPanel(String labelString, JComponent field) {
	if (!labelString.equals("")) {
	    labelString = " " + labelString + ": ";
	}
	JLabel fieldLabel = new JLabel(labelString);
	fieldLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
	fieldLabel.setLabelFor(field);

	JPanel fieldPanel = new JPanel();
	fieldPanel.setLayout(new GridLayout(1, 2, 5, 5));
	fieldPanel.add(fieldLabel);
	fieldPanel.add(field);
	return fieldPanel;
    }

    /**
     * Calls {@link #createFieldPanel(String labelString, JComponent field)}
     * with an empty label.
     */
    public static JPanel createFieldPanel(JComponent field) {
	return createFieldPanel("", field);
    }

    /**
     * Create a labeled panel with
     * all the given subpanels
     *
     * @param labelString The string displayed by the panel
     * @param panels An array of panels to display
     *
     * @return A sub panel
     */
    public static JPanel createSubPanel(String labelString, JPanel[] panels) {
	JPanel subPanel = new JPanel();
	subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
	for (int i=0; i < panels.length; i++) {
	    subPanel.add(panels[i]);
	}

	TitledBorder titledBorder = BorderFactory.createTitledBorder(labelString);
	titledBorder.setTitleColor(Color.GRAY);
	titledBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 11));
	subPanel.setBorder(BorderFactory.createCompoundBorder
			   (titledBorder,
			    BorderFactory.createEmptyBorder(5,5,5,5)));
	/*
	subPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	*/

	return subPanel;
    }

    protected static JPanel createDescPanel(String desc) {
	JLabel descLabel = new JLabel(desc);
	descLabel.setFont(new Font("Serif", Font.ITALIC, 11));
	descLabel.setBackground(Color.WHITE);

	JPanel descPanel = new JPanel();
	descPanel.add(descLabel);
	return descPanel;
    }
}


