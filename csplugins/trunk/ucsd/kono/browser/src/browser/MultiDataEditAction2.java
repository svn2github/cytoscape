package browser;

import giny.model.GraphObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class MultiDataEditAction2 extends AbstractUndoableEdit {

	final List objects;
	final String attributeTo;
	final String attributeFrom;
	List old_values;
	List new_values;
	final String[] keys;
	final int graphObjectType;
	final DataTableModel table;
	final String action;
	final String input;
	CyAttributes data;
	byte attType;

	static String ADD = "Add";
	static String SET = "Set";
	static String MUL = "Mul";
	static String DIV = "Div";
	static String COPY = "Copy";
	static String DELETE = "Delete";

	
	// Data types for the new attribute to be created here.
	static String STRING_TYPE = "String";
	static String INT_TYPE = "Integer";
	static String BOOL_TYPE = "Boolean";
	static String FLOAT_TYPE = "Double";
	
	private String newAttrType;
	
	// Constructor
	// Take the new value as a string, and then create correct data type
	// based on the user input ("dataType")
	//
	public MultiDataEditAction2(String input, String action, List objects,
			String attributeTo, String attributeFrom, String[] keys,
			int graphObjectType, DataTableModel table, String dataType ) {

		this.input = input;
		this.action = action;
		this.table = table;
		this.objects = objects;
		this.attributeTo = attributeTo;
		this.attributeFrom = attributeFrom;
		this.keys = keys;
		this.graphObjectType = graphObjectType;
		
		this.newAttrType = dataType;
		
		initEdit();

	}

	public String getPresentationName() {
		return "Attribute " + attributeTo + " changed.";
	}

	public String getRedoPresentationName() {
		return "Redo: " + action;
	}

	public String getUndoPresentationName() {
		return "Undo: " + action;
	}

	private void setAttributeValue(String id, String att, Object object) {
		if (object instanceof Integer)
			data.setAttribute(id, att, (Integer) object);
		else if (object instanceof Double)
			data.setAttribute(id, att, (Double) object);
		else if (object instanceof Boolean)
			data.setAttribute(id, att, (Boolean) object);
		else if (object instanceof String)
			data.setAttribute(id, att, (String) object);
		else if (object instanceof List)
			data.setAttributeList(id, att, (List) object);
		else if (object instanceof Map)
			data.setAttributeMap(id, att, (Map) object);
	}

	private Object getAttributeValue(String id, String att) {
		if (attType == CyAttributes.TYPE_INTEGER)
			return data.getIntegerAttribute(id, att);
		else if (attType == CyAttributes.TYPE_FLOATING)
			return data.getDoubleAttribute(id, att);
		else if (attType == CyAttributes.TYPE_BOOLEAN)
			return data.getBooleanAttribute(id, att);
		else if (attType == CyAttributes.TYPE_STRING)
			return data.getStringAttribute(id, att);
		else if (attType == CyAttributes.TYPE_SIMPLE_LIST)
			return data.getAttributeList(id, att);
		else if (attType == CyAttributes.TYPE_SIMPLE_MAP)
			return data.getAttributeMap(id, att);
		return null;
	}

	// put back the new_values
	public void redo() {
		for (int i = 0; i < objects.size(); ++i) {
			GraphObject go = (GraphObject) objects.get(i);
			if (new_values.get(i) == null) {
				data.getMultiHashMap().removeAllAttributeValues(
						go.getIdentifier(), attributeTo);
			} else {
				setAttributeValue(go.getIdentifier(), attributeTo, new_values
						.get(i));
			}
		}
		table.setTable();
	}

	// put back the old_values
	public void undo() {
		for (int i = 0; i < objects.size(); ++i) {
			GraphObject go = (GraphObject) objects.get(i);
			if (old_values.get(i) == null) {
				data.getMultiHashMap().removeAllAttributeValues(
						go.getIdentifier(), attributeTo);
			} else {
				setAttributeValue(go.getIdentifier(), attributeTo, old_values
						.get(i));
			}
		}
		table.setTable();
	}

	public void initEdit() {

		// Get proper Global CytoscapeData object
		if (graphObjectType == 0) {
			// node
			data = Cytoscape.getNodeAttributes();
		} else {
			// edge
			data = Cytoscape.getEdgeAttributes();
		}

		
		// Find the type of action
		if (action == COPY) {
			copyAtt();
		} else if (action == DELETE) {
			deleteAtt();
		} else {

			//
			// The following lines are for adding/setting attributes.
			//
			try {
				attType = data.getType(attributeTo);
			} catch (Exception ex) {
				// define the new attribute
				// attType = ( ( CytoscapeDataImpl )data
				// ).wildGuessAndDefineObjectType( input, attributeTo );
				attType = CyAttributes.TYPE_STRING;

				// TODO Type guessing!!!!

			}

			if (attType == -1) {
				// attType = ( ( CytoscapeDataImpl )data
				// ).wildGuessAndDefineObjectType( input, attributeTo );
			}

			if (attType == CyAttributes.TYPE_FLOATING) {
				Double d = new Double(input);
				doubleAction(d.doubleValue());
			} else if ( newAttrType == INT_TYPE ) {
				Integer d = new Integer(input);
				integerAction(d.intValue());
			} else if (attType == CyAttributes.TYPE_STRING) {
				stringAction(input);
			} else if (attType == CyAttributes.TYPE_BOOLEAN) {
				booleanAction(Boolean.valueOf(input));
			} else if (attType == CyAttributes.TYPE_SIMPLE_LIST) {
				// TODO: HANDLE LISTS
			} else if (attType == CyAttributes.TYPE_SIMPLE_MAP) {
				// TODO: HANDLE
			}
		}
		table.setTable();
	} // initEdit

	/**
	 * Use the global edit variables to copy the attribute in attributeFrom to
	 * attributeTo the values that were copied will be saved to "new_values"
	 */
	private void copyAtt() {

		new_values = new ArrayList(objects.size());
		old_values = new ArrayList(objects.size());
		for (Iterator i = objects.iterator(); i.hasNext();) {
			GraphObject go = (GraphObject) i.next();

			Object value = getAttributeValue(go.getIdentifier(), attributeFrom);
			new_values.add(value);
			setAttributeValue(go.getIdentifier(), attributeTo, value);
			old_values.add(null);
		}
	}

	/**
	 * Use the global edit variables to delete the values from the given
	 * attribute. the deleted values will be stored in "old_values"
	 */

	// only this one work now...
	//
	private void deleteAtt() {
		new_values = new ArrayList(objects.size());
		old_values = new ArrayList(objects.size());
		for (Iterator i = objects.iterator(); i.hasNext();) {
			GraphObject go = (GraphObject) i.next();

			old_values.add(getAttributeValue(go.getIdentifier(), attributeTo));
			data.getMultiHashMap().removeAllAttributeValues(go.getIdentifier(),
					attributeTo);
			new_values.add(null);
		}
	}

	/**
	 * save the old and new values, subsequent redo/undo will only use these
	 * values.
	 */
	private void doubleAction(double input) {

		old_values = new ArrayList(objects.size());
		new_values = new ArrayList(objects.size());
		for (Iterator i = objects.iterator(); i.hasNext();) {
			GraphObject go = (GraphObject) i.next();

			// get the current value and set the old_value to it
			Double d = (Double) getAttributeValue(go.getIdentifier(),
					attributeTo);
			old_values.add(d);

			double new_v;
			if (action == SET)
				new_v = input;
			else if (action == ADD)
				new_v = input + d.doubleValue();
			else if (action == MUL)
				new_v = input * d.doubleValue();
			else if (action == DIV)
				new_v = d.doubleValue() / input;
			else
				new_v = input;

			new_values.add(new Double(new_v));
			setAttributeValue(go.getIdentifier(), attributeTo,
					new Double(new_v));
		} // iterator
	} // doubleAction

	/**
	 * save the old and new values, subsequent redo/undo will only use these
	 * values.
	 */
	private void integerAction(int input) {

		old_values = new ArrayList(objects.size());
		new_values = new ArrayList(objects.size());
		for (Iterator i = objects.iterator(); i.hasNext();) {
			GraphObject go = (GraphObject) i.next();

			// get the current value and set the old_value to it
			Integer d = null;
			try {
				d = (Integer) getAttributeValue(go.getIdentifier(),
					attributeTo);
			} catch(Exception e) {
				d = new Integer(0);
			}
			old_values.add(d);

			int new_v;
			if (action == SET)
				new_v = input;
			else if (action == ADD)
				new_v = input + d.intValue();
			else if (action == MUL)
				new_v = input * d.intValue();
			else if (action == DIV)
				new_v = d.intValue() / input;
			else
				new_v = input;

			new_values.add(new Integer(new_v));
			
			if( action == ADD ) {
				setAttributeValue(go.getIdentifier(), "Fake", new Integer(
						new_v));
			} else {
			setAttributeValue(go.getIdentifier(), attributeTo, new Integer(
					new_v));
			}
		} // iterator
	} // integerAction

	/**
	 * save the old and new values, subsequent redo/undo will only use these
	 * values.
	 */
	private void stringAction(String input) {
		// return if number only action
		if (action == DIV || action == MUL) {
			return;
		}

		old_values = new ArrayList(objects.size());
		new_values = new ArrayList(objects.size());
		for (Iterator i = objects.iterator(); i.hasNext();) {
			GraphObject go = (GraphObject) i.next();

			// get the current value and set the old_value to it
			String s = null;
			try {
				s = (String) getAttributeValue(go.getIdentifier(), attributeTo);
			} catch (Exception e) {
				if (s == null) {
					s = "";
				}
			}

			old_values.add(s);
			String new_v;
			if (action == SET)
				new_v = input;
			else
				new_v = s.concat(input);

			new_values.add(new_v);
			if (action == ADD) {
				setAttributeValue(go.getIdentifier(), "TEST_AAA", new_v);
			} else {
				setAttributeValue(go.getIdentifier(), attributeTo, new_v);
			}
		} // iterator
	} // stringAction

	private void booleanAction(Boolean input) {

		if (action == DIV || action == MUL || action == ADD)
			return;

		old_values = new ArrayList(objects.size());
		new_values = new ArrayList(objects.size());
		for (Iterator i = objects.iterator(); i.hasNext();) {
			GraphObject go = (GraphObject) i.next();

			// get the current value and set the old_value to it
			Boolean b = (Boolean) getAttributeValue(go.getIdentifier(),
					attributeTo);
			old_values.add(b);
			setAttributeValue(go.getIdentifier(), attributeTo, input);
			new_values.add(input);
		} // iterator
	} // booleanAction

}
