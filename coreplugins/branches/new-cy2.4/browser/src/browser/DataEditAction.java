package browser;

import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class DataEditAction extends AbstractUndoableEdit {

	final String object;
	final String attribute;
	final Object old_value;
	final Object new_value;
	final String[] keys;
	final int objectType;
	final DataTableModel table;

	public DataEditAction(DataTableModel table, String object,
			String attribute, String[] keys, Object old_value,
			Object new_value, int graphObjectType) {
		this.table = table;
		this.object = object;
		this.attribute = attribute;
		this.keys = keys;
		this.old_value = old_value;
		this.new_value = new_value;
		this.objectType = graphObjectType;

		redo();
	}

	public String getPresentationName() {
		return object + " attribute " + attribute + " changed.";
	}

	public String getRedoPresentationName() {
		return "Redo: " + object + ":" + attribute + " to:" + new_value
				+ " from " + old_value;
	}

	public String getUndoPresentationName() {
		return "Undo: " + object + ":" + attribute + " back to:" + old_value
				+ " from " + new_value;
	}

	// Set value based on the data type.
	// Mod. by kono (11/10/2005)
	// Error check routine added.
	//
	private void setAttributeValue(CyAttributes data, String id, String att,
			Object object) {

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
				errMessage = "Attribute "
						+ att
						+ " should be an integer (or the number is too big/small).";
				showErrorWindow(errMessage);
			}

		} else if (targetType == CyAttributes.TYPE_FLOATING) {
			Double newDblVal = new Double(0);
			try {
				newDblVal = Double.valueOf(strObject);
				data.setAttribute(id, att, newDblVal);
			} catch (Exception e) {
				errMessage = "Attribute "
						+ att
						+ " should be a floating point number (or the number is too big/small).";
				showErrorWindow(errMessage);
			}
		} else if (targetType == CyAttributes.TYPE_BOOLEAN) {
			Boolean newBoolVal = new Boolean(false);
			try {
				newBoolVal = Boolean.valueOf(strObject);
				data.setAttribute(id, att, newBoolVal);
			} catch (Exception e) {
				errMessage = "Attribute " + att
						+ " should be a boolean value (true/false).";
				showErrorWindow(errMessage);
			}
		} else if (targetType == CyAttributes.TYPE_STRING) {
			data.setAttribute(id, att, strObject);
		} else if (targetType == CyAttributes.TYPE_SIMPLE_LIST) {
			errMessage = "List editing is not supported in this version.";
			showErrorWindow(errMessage);
			// data.setAttributeList(id, att, (List) object);
		} else if (targetType == CyAttributes.TYPE_SIMPLE_MAP) {
			errMessage = "Map editing is not supported in this version.";
			showErrorWindow(errMessage);
			// data.setAttributeMap(id, att, (Map) object);
		}

	}

	// Pop-up window for error message
	private void showErrorWindow(String errMessage) {
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), errMessage,
				"Error!", JOptionPane.ERROR_MESSAGE);
		return;
	}

	// this sets the new value
	public void redo() {

		CyAttributes data;

		if (objectType == DataTable.NODES) {
			// node
			data = Cytoscape.getNodeAttributes();
		} else if (objectType == DataTable.EDGES) {
			// edge
			data = Cytoscape.getEdgeAttributes();
		} else {
			// This is a network attr.
			data = Cytoscape.getNetworkAttributes();
		}

		setAttributeValue(data, object, attribute, new_value);

		if (objectType != DataTable.NETWORK) {
			table.setTable();
		} else {
			table.setNetworkTable();
		}
	}

	// this sets the old value
	public void undo() {

		CyAttributes data;

		if (objectType == DataTable.NODES) {
			// node
			data = Cytoscape.getNodeAttributes();
		} else if (objectType == DataTable.EDGES) {
			// edge
			data = Cytoscape.getEdgeAttributes();
		} else {
			// Network attr
			data = Cytoscape.getNetworkAttributes();
		}

		setAttributeValue(data, object, attribute, old_value);

		if (objectType != DataTable.NETWORK) {
			table.setTable();
		} else {
			table.setNetworkTable();
		}
	}

}
