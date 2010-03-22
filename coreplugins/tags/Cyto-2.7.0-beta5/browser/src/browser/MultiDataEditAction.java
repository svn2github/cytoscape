
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

package browser;

import browser.ui.ActionName;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import giny.model.GraphObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;

import static browser.ui.ActionName.*;


/**
 *
 */
public class MultiDataEditAction extends AbstractUndoableEdit {
	final List<Object> objects;
	final String attributeTo;
	final String attributeFrom;
	List<Object> old_values;
	List<Object> new_values;
	final String[] keys;
	final DataObjectType graphObjectType;
	final DataTableModel table;
	final ActionName action;
	final String input;
	CyAttributes attrData;
	byte attType;
	
//	private String newAttrType;

	/**
	 * Creates a new MultiDataEditAction object.
	 *
	 * @param input  DOCUMENT ME!
	 * @param action  DOCUMENT ME!
	 * @param objects  DOCUMENT ME!
	 * @param attributeTo  DOCUMENT ME!
	 * @param attributeFrom  DOCUMENT ME!
	 * @param keys  DOCUMENT ME!
	 * @param graphObjectType  DOCUMENT ME!
	 * @param table  DOCUMENT ME!
	 * @param dataType  DOCUMENT ME!
	 */
	public MultiDataEditAction(String input, ActionName action, List<Object> objects, String attributeTo,
	                           String attributeFrom, String[] keys, DataObjectType graphObjectType,
	                           DataTableModel table) {
		this.input = input;
		this.action = action;
		this.table = table;
		this.objects = objects;
		this.attributeTo = attributeTo;
		this.attributeFrom = attributeFrom;
		this.keys = keys;
		this.graphObjectType = graphObjectType;

		initEdit();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getPresentationName() {
		return "Attribute " + attributeTo + " changed.";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getRedoPresentationName() {
		return "Redo: " + action;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getUndoPresentationName() {
		return "Undo: " + action;
	}

	private void setAttributeValue(String id, String att, Object object) {
		if (object instanceof Integer)
			attrData.setAttribute(id, att, (Integer) object);
		else if (object instanceof Double)
			attrData.setAttribute(id, att, (Double) object);
		else if (object instanceof Boolean)
			attrData.setAttribute(id, att, (Boolean) object);
		else if (object instanceof String)
			attrData.setAttribute(id, att, (String) object);
		else if (object instanceof List)
			attrData.setListAttribute(id, att, (List) object);
		else if (object instanceof Map)
			attrData.setMapAttribute(id, att, (Map) object);
	}


	// put back the new_values
	/**
	 *  DOCUMENT ME!
	 */
	public void redo() {
		final int size = objects.size();
		GraphObject go;
		for (int i = 0; i < size; ++i) {
			go = (GraphObject) objects.get(i);

			if (new_values.get(i) == null) {
				attrData.getMultiHashMap().removeAllAttributeValues(go.getIdentifier(), attributeTo);
			} else {
				setAttributeValue(go.getIdentifier(), attributeTo, new_values.get(i));
			}
		}
		table.setTableData();
	}

	// put back the old_values
	/**
	 *  DOCUMENT ME!
	 */
	public void undo() {
		for (int i = 0; i < objects.size(); ++i) {
			GraphObject go = (GraphObject) objects.get(i);

			if (old_values.get(i) == null) {
				attrData.getMultiHashMap().removeAllAttributeValues(go.getIdentifier(), attributeTo);
			} else {
				setAttributeValue(go.getIdentifier(), attributeTo, old_values.get(i));
			}
		}

		table.setTableData();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void initEdit() {
		// get proper Global CytoscapeData object
		attrData = graphObjectType.getAssociatedAttribute();

		if (action == COPY) {
			copyAtt();
		} else if (action == CLEAR) {
			deleteAtt();
		} else {
			try {
				attType = attrData.getType(attributeTo);
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
			} else if (attType == CyAttributes.TYPE_INTEGER) {
				Integer d = new Integer(input);
				integerAction(d.intValue());
			} else if (attType == CyAttributes.TYPE_STRING) {
				stringAction(input);
			} else if (attType == CyAttributes.TYPE_BOOLEAN) {
				booleanAction(Boolean.valueOf(input));
			} else if (attType == CyAttributes.TYPE_SIMPLE_LIST) {
				listAction();
			} else if (attType == CyAttributes.TYPE_SIMPLE_MAP) {
				// TODO: HANDLE
			}
		}

		if (graphObjectType != DataObjectType.NETWORK) {
			table.setTableData();
		} else {
			table.setNetworkTable();
		}
	} // initEdit

	private void listAction() {
		if ((action == DIV) || (action == MUL))
			return;

		old_values = new ArrayList(objects.size());
		new_values = new ArrayList(objects.size());

		GraphObject go;
		String s;
		String new_v;
		for (Object obj : objects) {
			 go = (GraphObject) obj;

			// get the current value and set the old_value to it
			s = (String) attrData.getAttribute(go.getIdentifier(), attributeTo);
			old_values.add(s);

			if (action == SET)
				new_v = input;
			else if(action == ActionName.ADD_PREFIX) {
				new_v = s+input;
			} else if(action == ActionName.ADD_SUFFIX) {
				new_v = input+s;
			} else if(action == ActionName.REMOVE) {
				new_v = s.replaceAll(input, "");
			} else if(action == ActionName.REPLACE) {
				new_v = s.replaceAll(input, "");
			} else
				new_v = s.concat(input);

			new_values.add(new_v);
			setAttributeValue(go.getIdentifier(), attributeTo, new_v);
		} // iterator
	}

	/**
	 * Use the global edit variables to copy the attribute in attributeFrom to
	 * attributeTo the values that were copied will be saved to "new_values"
	 */
	private void copyAtt() {
		// Sanity check:
		if (attributeFrom == null || attributeTo == null || attributeTo.equals("")) {
			showErrorWindow("\"From\" attribute or \"To\" attribute has not been specified!");
			return;
		}

		final byte fromType = attrData.getType(attributeFrom);
		final byte toType = attrData.getType(attributeTo);
		if (toType == CyAttributes.TYPE_UNDEFINED || !copyAttrsAreCompatible(fromType, toType)) {
			showErrorWindow("Copy Failed: Incompatible data types.");

			return;
		}

		new_values = new ArrayList(objects.size());
		old_values = new ArrayList(objects.size());

		// System.out.println("####FROM: " + attributeFrom);
		// System.out.println("####TO: " + attributeTo);
		for (Iterator i = objects.iterator(); i.hasNext();) {
			GraphObject go = (GraphObject) i.next();

			Object value = null;
			if (fromType == CyAttributes.TYPE_SIMPLE_LIST)
				value = attrData.getListAttribute(go.getIdentifier(), attributeFrom);
			else if (fromType == CyAttributes.TYPE_SIMPLE_MAP)
				value = attrData.getMapAttribute(go.getIdentifier(), attributeFrom);
			else
				value = attrData.getAttribute(go.getIdentifier(), attributeFrom);
			new_values.add(value);

			if ((fromType == CyAttributes.TYPE_INTEGER) && (toType == CyAttributes.TYPE_FLOATING))
				value = new Double((Integer)value);
			setAttributeValue(go.getIdentifier(), attributeTo, value);

			old_values.add(null);
		}
	}

	private boolean copyAttrsAreCompatible(final byte fromType, final byte toType) {
		if (fromType == toType)
			return true;

		return (fromType == CyAttributes.TYPE_INTEGER) && (toType == CyAttributes.TYPE_FLOATING);
	}

	/**
	 * Use the global edit variables to delete the values from the given
	 * attribute. the deleted values will be stored in "old_values"
	 */
	private void deleteAtt() {
		new_values = new ArrayList(objects.size());
		old_values = new ArrayList(objects.size());

		// Check data compatibility
		for (Iterator i = objects.iterator(); i.hasNext();) {
			GraphObject go = (GraphObject) i.next();

			final Object attr = attrData.getAttribute(go.getIdentifier(), attributeTo);
			if (attr != null) {
				old_values.add(attr);
				attrData.getMultiHashMap().removeAllAttributeValues(go.getIdentifier(), attributeTo);
				new_values.add(null);
			}
		}
	}

	// Pop-up window for error message
	private void showErrorWindow(String errMessage) {
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), errMessage, "Error!",
		                              JOptionPane.ERROR_MESSAGE);

		return;
	}

	/**
	 * save the old and new values, subsequent redo/undo will only use these
	 * values.
	 */
	private void doubleAction(double input) {
		// Sanity check:
		if (action == DIV && input == 0.0) {
			showErrorWindow("Division by zero is invalid!");
			return;
		}

		old_values = new ArrayList(objects.size());
		new_values = new ArrayList(objects.size());

		for (Iterator i = objects.iterator(); i.hasNext();) {
			GraphObject go = (GraphObject) i.next();

			// get the current value and set the old_value to it
			Double d = (Double) attrData.getAttribute(go.getIdentifier(), attributeTo);
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
			setAttributeValue(go.getIdentifier(), attributeTo, new Double(new_v));
		} // iterator
	} // doubleAction

	/**
	 * save the old and new values, subsequent redo/undo will only use these
	 * values.
	 */
	private void integerAction(int input) {
		// Sanity check:
		if (action == DIV && input == 0.0) {
			showErrorWindow("Division by zero is invalid!");
			return;
		}

		old_values = new ArrayList(objects.size());
		new_values = new ArrayList(objects.size());

		for (Iterator i = objects.iterator(); i.hasNext();) {
			GraphObject go = (GraphObject) i.next();

			// get the current value and set the old_value to it
			Integer d = (Integer) attrData.getAttribute(go.getIdentifier(), attributeTo);
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
			setAttributeValue(go.getIdentifier(), attributeTo, new Integer(new_v));
		} // iterator
	} // integerAction

	/**
	 * save the old and new values, subsequent redo/undo will only use these
	 * values.
	 */
	private void stringAction(String input) {
		// return if number only action
		if ((action == DIV) || (action == MUL))
			return;

		old_values = new ArrayList(objects.size());
		new_values = new ArrayList(objects.size());

		GraphObject go;
		String s;
		String new_v;
		for (Object obj : objects) {
			 go = (GraphObject) obj;

			// get the current value and set the old_value to it
			s = (String) attrData.getAttribute(go.getIdentifier(), attributeTo);
			old_values.add(s);
			
			if(s == null && action != SET) {
				continue;
			}

			if (action == SET)
				new_v = input;
			else if(action == ActionName.ADD_PREFIX) {
				new_v = input+s;
			} else if(action == ActionName.ADD_SUFFIX) {
				new_v = s+input;
			} else if(action == ActionName.REMOVE) {
				new_v = s.replaceAll(input, "");
			} else if(action == ActionName.REPLACE) {
				String[] vals = input.split("\t");
				if(vals.length == 2) {
					new_v = s.replaceAll(vals[0], vals[1]);
				} else
					new_v = s.concat(input);
				
			} else if(action == ActionName.TO_LOWER) {
				new_v = s.toLowerCase();
			} else if(action == ActionName.TO_UPPER) {
				new_v = s.toUpperCase();
			} else
				new_v = s.concat(input);

			new_values.add(new_v);
			setAttributeValue(go.getIdentifier(), attributeTo, new_v);
		} // iterator
	} // stringAction

	private void booleanAction(Boolean input) {
		if ((action == DIV) || (action == MUL) || (action == ADD))
			return;

		old_values = new ArrayList(objects.size());
		new_values = new ArrayList(objects.size());

		for (Iterator i = objects.iterator(); i.hasNext();) {
			GraphObject go = (GraphObject) i.next();

			// get the current value and set the old_value to it
			Boolean b = (Boolean) attrData.getAttribute(go.getIdentifier(), attributeTo);
			old_values.add(b);
			setAttributeValue(go.getIdentifier(), attributeTo, input);
			new_values.add(input);
		} // iterator
	} // booleanAction
}
