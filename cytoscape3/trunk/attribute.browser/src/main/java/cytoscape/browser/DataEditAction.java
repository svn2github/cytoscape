/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.browser;

import cytoscape.Cytoscape;
import static cytoscape.browser.DataObjectType.NETWORK;
import org.cytoscape.model.CyRow;

import javax.swing.*;
import javax.swing.undo.AbstractUndoableEdit;

/**
 * Validate and set new value to the CyAttributes.
 *
 */
public class DataEditAction extends AbstractUndoableEdit {
	private final String attrKey;
	private final String attrName;
	private final Object old_value;
	private final Object new_value;
	private final DataObjectType objectType;
	private final DataTableModel tableModel;
	
	private boolean valid = false;

	/**
	 * Creates a new DataEditAction object.
	 *
	 * @param table  DOCUMENT ME!
	 * @param attrKey  DOCUMENT ME!
	 * @param attrName  DOCUMENT ME!
	 * @param keys  DOCUMENT ME!
	 * @param old_value  DOCUMENT ME!
	 * @param new_value  DOCUMENT ME!
	 * @param graphObjectType  DOCUMENT ME!
	 */
	public DataEditAction(DataTableModel table, String attrKey, String attrName,
	                      Object old_value, Object new_value, DataObjectType graphObjectType) {
		this.tableModel = table;
		this.attrKey = attrKey;
		this.attrName = attrName;
		this.old_value = old_value;
		this.new_value = new_value;
		this.objectType = graphObjectType;

		redo();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getPresentationName() {
		return attrKey + " attribute " + attrName + " changed.";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getRedoPresentationName() {
		return "Redo: " + attrKey + ":" + attrName + " to:" + new_value + " from " + old_value;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getUndoPresentationName() {
		return "Undo: " + attrKey + ":" + attrName + " back to:" + old_value + " from " + new_value;
	}

	/**
	 * Set attribute value.  Input validater is added.
	 *
	 * @param id
	 * @param att
	 * @param newValue
	 */
	private void setAttributeValue(String id, String att, Object newValue) {

		final CyAttributes attr = objectType.getAssociatedAttribute();

		// Error message for the popup dialog.
		String errMessage = null;

		// Change object to String
		final String newValueStr = newValue.toString();
		final byte targetType = attr.getType(att);

		if (targetType == CyAttributes.TYPE_INTEGER) {
			Integer newIntVal;

			try {
				newIntVal = Integer.valueOf(newValueStr);
				attr.setAttribute(id, att, newIntVal);
			} catch (Exception nfe) {
				errMessage = "Attribute " + att
				             + " should be an integer (or the number is too big/small).";
				showErrorWindow(errMessage);

				return;
			}
		} else if (targetType == CyAttributes.TYPE_FLOATING) {
			Double newDblVal;

			try {
				newDblVal = Double.valueOf(newValueStr);
				attr.setAttribute(id, att, newDblVal);
			} catch (Exception e) {
				errMessage = "Attribute " + att
				             + " should be a floating point number (or the number is too big/small).";
				showErrorWindow(errMessage);

				return;
			}
		} else if (targetType == CyAttributes.TYPE_BOOLEAN) {
			Boolean newBoolVal = false;

			try {
				newBoolVal = Boolean.valueOf(newValueStr);
				attr.setAttribute(id, att, newBoolVal);
			} catch (Exception e) {
				errMessage = "Attribute " + att + " should be a boolean value (true/false).";
				showErrorWindow(errMessage);

				return;
			}
		} else if (targetType == CyAttributes.TYPE_STRING) {
			attr.setAttribute(id, att, replaceNewlines( newValueStr ));
		} else if (targetType == CyAttributes.TYPE_SIMPLE_LIST) {
			errMessage = "List editing is not supported in this version.";
			showErrorWindow(errMessage);

			return;

			// data.setAttributeList(id, att, (List) object);
		} else if (targetType == CyAttributes.TYPE_SIMPLE_MAP) {
			errMessage = "Map editing is not supported in this version.";
			showErrorWindow(errMessage);

			return;
		}

		valid = true;
	}

    private String replaceNewlines(String s) {
        StringBuffer sb = new StringBuffer( s );
        int index = 0;
        while ( index < sb.length() ) {
            if ( sb.charAt(index) == '\\' ) {
                if ( sb.charAt(index+1) == 'n') {
                    sb.setCharAt(index,'\n');
                    sb.deleteCharAt(index+1);
                    index++;
                } else if ( sb.charAt(index+1) == 'b') {
                    sb.setCharAt(index,'\b');
                    sb.deleteCharAt(index+1);
                    index++;
                } else if ( sb.charAt(index+1) == 'r') {
                    sb.setCharAt(index,'\r');
                    sb.deleteCharAt(index+1);
                    index++;
                } else if ( sb.charAt(index+1) == 'f') {
                    sb.setCharAt(index,'\f');
                    sb.deleteCharAt(index+1);
                    index++;
                } else if ( sb.charAt(index+1) == 't') {
                    sb.setCharAt(index,'\t');
                    sb.deleteCharAt(index+1);
                    index++;
                }
            }
            index++;
        }
        return sb.toString();
    }

	// Pop-up window for error message
	private void showErrorWindow(String errMessage) {
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), errMessage, "Invalid Value!",
		                              JOptionPane.ERROR_MESSAGE);

		return;
	}

	/**
	 * For redo function.
	 */
	public void redo() {
		setAttributeValue(attrKey, attrName, new_value);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void undo() {
		setAttributeValue(attrKey, attrName, old_value);

		if (objectType != NETWORK) {
			tableModel.setTableData();
		} else {
			tableModel.setNetworkTable();
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isValid() {
		return valid;
	}
}
