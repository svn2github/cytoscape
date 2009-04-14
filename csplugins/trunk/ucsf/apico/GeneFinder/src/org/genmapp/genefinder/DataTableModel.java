package org.genmapp.genefinder;

import giny.model.GraphObject;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.attr.MultiHashMapListener;

/**
 * @author kono Actual data manipulation is implemented here.
 */
public class DataTableModel extends DefaultTableModel implements
		SortTableModel, MultiHashMapListener {

	private CyAttributes data;
	private List graph_objects;
	private List attributes;

	public static final String LS = System.getProperty("line.separator");

	private int graphObjectType = 0;

	public DataTableModel() {
	}

	public DataTableModel(int rows, int cols) {
		super(rows, cols);
	}

	public DataTableModel(Object[][] data, Object[] names) {
		super(data, names);
	}

	public DataTableModel(Object[] names, int rows) {
		super(names, rows);
	}

	public DataTableModel(Vector names, int rows) {
		super(names, rows);
	}

	public DataTableModel(Vector data, Vector names) {
		super(data, names);
	}

	// Accept CyAttributes and create table
	// Source of bug?
	public void setTableData(CyAttributes data, List graph_objects,
			List attributes) {
		this.data = data;
		this.graph_objects = graph_objects;
		this.attributes = attributes;
		data.getMultiHashMap().addDataListener(this);
		setTable();
	}

	public List getObjects() {
		return graph_objects;
	}

	public void setTableData(List graph_objects, List attributes) {

		this.graph_objects = graph_objects;
		this.attributes = attributes;
		setTable();
	}

	public void setTableDataAttributes(List attributes) {
		this.attributes = attributes;
		setTable();
	}

	public void setTableDataObjects(List graph_objects) {
		this.graph_objects = graph_objects;
		setTable();

		
		
	}

	public void setGraphObjectType(int got) {
		graphObjectType = got;
	}

	// Fill the cells in the table
	// *** need to add an argument to copy edge attribute name correctly.
	//
	protected void setTable() {
		
		
		int att_length = attributes.size() + 1;
		int go_length = graph_objects.size();

		Object[][] data_vector = new Object[go_length][att_length];
		Object[] column_names = new Object[att_length];

		// Set column names (attribute names)
		// System.out.println("Debug: testsection start.");
		column_names[0] = "ID";
		for (int j = 0; j < go_length; ++j) {
			GraphObject obj = (GraphObject) graph_objects.get(j);

			data_vector[j][0] = obj.getIdentifier();

			// System.out.print("Debug: ID = " + data_vector[j][0] + " and cn =
			// " );
			// System.out.println(data.getStringAttribute((String)
			// data_vector[j][0], Semantics.CANONICAL_NAME));

		}

		// Set actual data
		for (int i1 = 0; i1 < attributes.size(); ++i1) {
			int i = i1 + 1;
			column_names[i] = attributes.get(i1);
			String attribute = (String) attributes.get(i1);

			byte type = data.getType(attribute);
			// System.out.println("Debug: col name = " + column_names[i] + "
			// TYPE is " + type );

			for (int j = 0; j < go_length; ++j) {
				GraphObject obj = (GraphObject) graph_objects.get(j);

				Object value = getAttributeValue(type, obj.getIdentifier(),
						attribute);
				//				
				// ArrayList testlist = (ArrayList) value;
				// value = testlist.get(0);
				//				

				data_vector[j][i] = value;

				// System.out.println("Debug: value = " + value.toString() + ",
				// class is " + value.getClass().toString() );
			}
		}

		setDataVector(data_vector, column_names);

	}

	public String exportTable() {
		return exportTable("\t", LS);
	}

	public Object getAttributeValue(byte type, String id, String att) {
		if (type == CyAttributes.TYPE_INTEGER)
			return data.getIntegerAttribute(id, att);
		else if (type == CyAttributes.TYPE_FLOATING)
			return data.getDoubleAttribute(id, att);
		else if (type == CyAttributes.TYPE_BOOLEAN)
			return data.getBooleanAttribute(id, att);
		else if (type == CyAttributes.TYPE_STRING)
			return data.getStringAttribute(id, att);
		else if (type == CyAttributes.TYPE_SIMPLE_LIST)
			return data.getAttributeList(id, att);
		else if (type == CyAttributes.TYPE_SIMPLE_MAP)
			return data.getAttributeMap(id, att);
		return null;
	}

	public String exportTable(String element_delim, String eol_delim) {

		// Clipboard clipboard =
		// Toolkit.getDefaultToolkit().getSystemClipboard();

		StringBuffer export = new StringBuffer();

		int att_length = attributes.size() + 1;
		int go_length = graph_objects.size();

		Object[][] data_vector = new Object[go_length][att_length];
		Object[] column_names = new Object[att_length];

		column_names[0] = "ID";
		for (int j = 0; j < go_length; ++j) {
			GraphObject obj = (GraphObject) graph_objects.get(j);

			data_vector[j][0] = obj.getIdentifier();
		}

		for (int i1 = 0; i1 < attributes.size(); ++i1) {
			int i = i1 + 1;
			column_names[i] = attributes.get(i1);
			String attribute = (String) attributes.get(i1);
			byte type = data.getType(attribute);
			for (int j = 0; j < go_length; ++j) {
				GraphObject obj = (GraphObject) graph_objects.get(j);

				Object value = getAttributeValue(type, obj.getIdentifier(),
						attribute);
				data_vector[j][i] = value;
			}
		}

		for (int i = 0; i < column_names.length; ++i) {
			export.append(column_names[i] + element_delim);
		}
		export.append(eol_delim);

		for (int i = 0; i < data_vector.length; i++) {
			for (int j = 0; j < data_vector[i].length; ++j) {
				export.append(data_vector[i][j] + element_delim);
			}
			export.append(eol_delim);
		}
		// StringSelection contents = new StringSelection(export.toString());
		// clipboard.setContents(contents, this);
		return export.toString();

	}

	public List getGraphObjects() {
		return graph_objects;
	}

	public Class getObjectTypeAt(String colName) {

		byte type = data.getType(colName);

		if (type == CyAttributes.TYPE_INTEGER)
			return Integer.class;
		else if (type == CyAttributes.TYPE_FLOATING)
			return Double.class;
		else if (type == CyAttributes.TYPE_BOOLEAN)
			return Boolean.class;
		else if (type == CyAttributes.TYPE_STRING)
			return String.class;
		else if (type == CyAttributes.TYPE_SIMPLE_LIST)
			return List.class;
		else if (type == CyAttributes.TYPE_SIMPLE_MAP)
			return Map.class;
		return null;

	}

	public void attributeValueAssigned(java.lang.String objectKey,
			java.lang.String attributeName, java.lang.Object[] keyIntoValue,
			java.lang.Object oldAttributeValue,
			java.lang.Object newAttributeValue) {
		// System.out.println( "attributeValueAssigned" );
		// setTable();
	}

	public void attributeValueRemoved(java.lang.String objectKey,
			java.lang.String attributeName, java.lang.Object[] keyIntoValue,
			java.lang.Object attributeValue) {
		// setTable();
	}

	public void allAttributeValuesRemoved(java.lang.String objectKey,
			java.lang.String attributeName) {
		// setTable();
	}

	// //////////////////////////////////////
	// Implements JSortTable

	public boolean isSortable(int col) {
		return true;
	}

	public void sortColumn(int col, boolean ascending) {
		Collections.sort(getDataVector(), new ColumnComparator(col, ascending));
	}

	public boolean isCellEditable(int rowIndex, int colIndex) {
		//System.out.println("row = " + rowIndex + ", col = " + colIndex);
		
		Class objectType = null;
		Object selectedObj = this.getValueAt(rowIndex, colIndex);
		
		if( selectedObj == null && colIndex != 0 ) {
			return true;
		} else if (selectedObj != null ) {
			objectType = this.getValueAt(rowIndex, colIndex).getClass();
		}
		
		
		if (objectType != null) {

			if (colIndex == 0) {
				return false;
			} else if (objectType == ArrayList.class) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * Instead of using a listener, just overwrite this method to save time and
	 * write to the temp object
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// System.out.println( "SetValueAt row "+rowIndex+" :
		// "+getValueAt(rowIndex, columnIndex )+" column "+columnIndex+" :
		// "+attributes.get( columnIndex - 1)+ "ID: "+getValueAt( rowIndex, 0 )
		// );

		// TODO: set the edit
		// super.setValueAt( aValue, rowIndex, columnIndex );

		DataEditAction edit = new DataEditAction(this, (String) getValueAt(
				rowIndex, 0), (String) attributes.get(columnIndex - 1), null,
				getValueAt(rowIndex, columnIndex), aValue, graphObjectType);
		cytoscape.Cytoscape.getDesktop().addEdit(edit);

	}

}
