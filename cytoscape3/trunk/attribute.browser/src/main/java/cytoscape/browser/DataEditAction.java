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

import static cytoscape.browser.DataObjectType.*;

import cytoscape.Cytoscape;

import org.cytoscape.attributes.CyAttributes;

import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;

/**
 *
 */
public class DataEditAction extends AbstractUndoableEdit {
	final String object;
	final String attrName;
	final Object old_value;
	final Object new_value;
	final String[] keys;
	final DataObjectType objectType;
	final DataTableModel tableModel;
	boolean valid = false;

	/**
	 * Creates a new DataEditAction object.
	 *
	 * @param table  DOCUMENT ME!
	 * @param object  DOCUMENT ME!
	 * @param attrName  DOCUMENT ME!
	 * @param keys  DOCUMENT ME!
	 * @param old_value  DOCUMENT ME!
	 * @param new_value  DOCUMENT ME!
	 * @param graphObjectType  DOCUMENT ME!
	 */
	public DataEditAction(DataTableModel table, String object, String attrName, String[] keys,
	                      Object old_value, Object new_value, DataObjectType graphObjectType) {
		this.tableModel = table;
		this.object = object;
		this.attrName = attrName;
		this.keys = keys;
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
		return object + " attribute " + attrName + " changed.";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getRedoPresentationName() {
		return "Redo: " + object + ":" + attrName + " to:" + new_value + " from " + old_value;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getUndoPresentationName() {
		return "Undo: " + object + ":" + attrName + " back to:" + old_value + " from " + new_value;
	}

	// Set value based on the data type.
	// Mod. by kono (11/10/2005)
	// Error check routine added.
	//
	private void setAttributeValue(String id, String att, Object object) {
		final CyAttributes data = objectType.getAssociatedAttribute();
		String errMessage = null;

		// Change object to String
		String strObject = object.toString();

		byte targetType = data.getType(att);

		if (targetType == CyAttributes.TYPE_INTEGER) {
			Integer newIntVal = new Integer(0);

			try {
				newIntVal = Integer.valueOf(strObject);
				data.setAttribute(id, att, newIntVal);
			} catch (Exception nfe) {
				errMessage = "Attribute " + att
				             + " should be an integer (or the number is too big/small).";
				showErrorWindow(errMessage);

				return;
			}
		} else if (targetType == CyAttributes.TYPE_FLOATING) {
			Double newDblVal = new Double(0);

			try {
				newDblVal = Double.valueOf(strObject);
				data.setAttribute(id, att, newDblVal);
			} catch (Exception e) {
				errMessage = "Attribute " + att
				             + " should be a floating point number (or the number is too big/small).";
				showErrorWindow(errMessage);

				return;
			}
		} else if (targetType == CyAttributes.TYPE_BOOLEAN) {
			Boolean newBoolVal = new Boolean(false);

			try {
				newBoolVal = Boolean.valueOf(strObject);
				data.setAttribute(id, att, newBoolVal);
			} catch (Exception e) {
				errMessage = "Attribute " + att + " should be a boolean value (true/false).";
				showErrorWindow(errMessage);

				return;
			}
		} else if (targetType == CyAttributes.TYPE_STRING) {
			data.setAttribute(id, att, strObject);
		} else if (targetType == CyAttributes.TYPE_SIMPLE_LIST) {
			errMessage = "List editing is not supported in this version.";
			showErrorWindow(errMessage);

			return;

			// data.setAttributeList(id, att, (List) object);
		} else if (targetType == CyAttributes.TYPE_SIMPLE_MAP) {
			errMessage = "Map editing is not supported in this version.";
			showErrorWindow(errMessage);

			return;

			// data.setAttributeMap(id, att, (Map) object);
		}

		valid = true;
	}

	// Pop-up window for error message
	private void showErrorWindow(String errMessage) {
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), errMessage, "Error!",
		                              JOptionPane.ERROR_MESSAGE);

		return;
	}

	// this sets the new value
	/**
	 *  DOCUMENT ME!
	 */
	public void redo() {
		setAttributeValue(object, attrName, new_value);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void undo() {
		setAttributeValue(object, attrName, old_value);

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
