package org.cytoscape.browser.internal;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.cytoscape.equations.EqnCompiler;
import org.cytoscape.equations.Equation;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.RowCreatedMicroListener;
import org.cytoscape.model.events.RowSetMicroListener;


public class BrowserTableModel extends AbstractTableModel
	implements ColumnCreatedListener, ColumnDeletedListener, RowCreatedMicroListener
{
	private final JTable table;
	private final CyEventHelper eventHelper;
	private final CyTable attrs;
	private final EqnCompiler compiler;
	private boolean tableHasBooleanSelected;
	private Map<CyRow, RowSetMicroListenerProxy> rowToListenerProxyMap;

	public BrowserTableModel(final JTable table, final CyEventHelper eventHelper,
				 final CyTable attrs, final EqnCompiler compiler)
	{
		this.table = table;
		this.eventHelper = eventHelper;
		this.attrs = attrs;
		this.compiler = compiler;
		this.tableHasBooleanSelected = attrs.getColumnTypeMap().get(CyNetwork.SELECTED) == Boolean.class;
		this.rowToListenerProxyMap = new HashMap<CyRow, RowSetMicroListenerProxy>();

		eventHelper.addMicroListener(this, RowCreatedMicroListener.class, attrs);

		final List<CyRow> rows = attrs.getAllRows();
		for (final CyRow row : rows)
			rowToListenerProxyMap.put(row, new RowSetMicroListenerProxy(this, eventHelper, row));
	}

	@Override
	public int getRowCount() {
		final Map<String, Class<?>> columnNameToTypeMap = attrs.getColumnTypeMap();
		if (columnNameToTypeMap.isEmpty())
			return 0;

		if (!tableHasBooleanSelected)
			return attrs.getRowCount();

		final List<CyRow> rows = attrs.getAllRows();

		int selectedCount = 0;
		for (final CyRow row : rows) {
			if (row.get(CyNetwork.SELECTED, Boolean.class))
				++selectedCount;
		}

		return selectedCount;
	}

	@Override
	public int getColumnCount() {
		return attrs.getColumnTypeMap().size();
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final String columnName = getColumnName(columnIndex);
		final CyRow row = mapRowIndexToRow(rowIndex);

		return getValidatedObjectAndEditString(row, columnName);
	}

	private CyRow mapRowIndexToRow(final int rowIndex) {
		if (tableHasBooleanSelected) {
			final Set<CyRow> selectedRows = attrs.getMatchingRows(CyNetwork.SELECTED, true);
			int count = 0;
			CyRow cyRow = null;
			for (final CyRow selectedRow : selectedRows) {
				if (count == rowIndex) {
					cyRow = selectedRow;
					break;
				}

				++count;
			}

			return cyRow;
		} else {
			final List primaryKeyValues =
				attrs.getColumnValues(attrs.getPrimaryKey(),
						      attrs.getPrimaryKeyType());
			return attrs.getRow(primaryKeyValues.get(rowIndex));
		}
	}

	/**
	 *  @return the row index for "cyRow" or -1 if there is no matching row.
	 */
	private int mapRowToRowIndex(final CyRow cyRow) {
		int index = 0;
		if (tableHasBooleanSelected) {
			final Set<CyRow> selectedRows = attrs.getMatchingRows(CyNetwork.SELECTED, true);
			for (final CyRow selectedRow : selectedRows) {
				if (cyRow == selectedRow)
					return index;
				++index;
			}

			return -1; // Most likely the passed in row was not a selected row!
		} else {
			final List primaryKeyValues =
				attrs.getColumnValues(attrs.getPrimaryKey(),
						      attrs.getPrimaryKeyType());
			for (final Object primaryKey : primaryKeyValues) {
				if (cyRow == attrs.getRow(primaryKey))
					return index;
				++index;
			}

			throw new IllegalStateException("we should *never* get here!");
		}
	}

	private ValidatedObjectAndEditString getValidatedObjectAndEditString(final CyRow row,
									     final String columnName)
	{
		final Object raw = row.getRaw(columnName);
		if (raw == null)
			return null;

		final Map<String, Class<?>> columnNameToTypeMap = attrs.getColumnTypeMap();
		final Object cooked = row.get(columnName, columnNameToTypeMap.get(columnName));
		if (cooked != null)
			return new ValidatedObjectAndEditString(cooked, raw.toString());

		final String lastInternalError = attrs.getLastInternalError();
		return new ValidatedObjectAndEditString(cooked, raw.toString(), lastInternalError);
	}

	@Override
	public void handleEvent(final ColumnCreatedEvent e) {
		fireTableStructureChanged();
	}

	@Override
	public void handleEvent(final ColumnDeletedEvent e) {
		fireTableStructureChanged();
	}

	@Override
	public String getColumnName(final int column) {
		return mapColumnIndexToColumnName(column);
	}

	@Override
	public void handleRowCreated(final Object key) {
		final CyRow newRow = attrs.getRow(key);
		rowToListenerProxyMap.put(newRow, new RowSetMicroListenerProxy(this, eventHelper, newRow));
		fireTableStructureChanged();
	}

	private int mapColumnNameToColumnIndex(final String columnName) {
		final String primaryKey = attrs.getPrimaryKey();
		if (columnName.equals(primaryKey))
			return 0;

		final Map<String, Class<?>> columnNameToTypeMap = attrs.getColumnTypeMap();
		int index = 1;
		for (final String name : columnNameToTypeMap.keySet()) {
			if (name.equals(columnName))
				return index;

			if (!name.equals(primaryKey))
				++index;
		}

		throw new IllegalStateException("We should *never* get here!");
	}

	private String mapColumnIndexToColumnName(final int index) {
		final String primaryKey = attrs.getPrimaryKey();
		if (index == 0)
			return primaryKey;


		final Map<String, Class<?>> columnNameToTypeMap = attrs.getColumnTypeMap();
		int i = 1;
		for (final String name : columnNameToTypeMap.keySet()) {
			if (name.equals(primaryKey))
				continue;

			if (index == i)
				return name;

			++i;
		}

		throw new IllegalStateException("We should *never* get here!");
	}

	void handleRowValueUpdate(final CyRow row, final String columnName, final Object newValue,
				  final Object newRawValue)
	{
		if (tableHasBooleanSelected && columnName.equals(CyNetwork.SELECTED))
			fireTableStructureChanged();
		else {
			final int rowIndex = mapRowToRowIndex(row);
			if (rowIndex != -1)
				fireTableChanged(new TableModelEvent(this, rowIndex));
		}
	}

	@Override
	public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
		final String text = (String)value;
		final CyRow row = mapRowIndexToRow(rowIndex);
		final String columnName = mapColumnIndexToColumnName(columnIndex);
		final Class<?> columnType = attrs.getType(columnName);

		if (text.startsWith("=")) {
			final Map<String, Class> variableNameToTypeMap = new HashMap<String, Class>();
			initVariableNameToTypeMap(variableNameToTypeMap);
			if (compiler.compile(text, variableNameToTypeMap)) {
				final Equation eqn = compiler.getEquation();
				final Class<?> eqnType = eqn.getType();

				// Is the equation type compatible with the column type?
				if (eqnType == columnType
				    || eqnType == Long.class && columnType == Integer.class) // Yes!
					row.set(columnName, eqn);
				else { // The equation type is incompatible w/ the column type!
					final Class<?> expectedType = columnType == Integer.class ? Long.class : columnType;
					final String errorMsg = "Equation result type is "
						+ getUnqualifiedName(eqnType) + ", column type is "
						+ getUnqualifiedName(columnType) + "!";
					final Equation errorEqn = compiler.getErrorEquation(text, expectedType, errorMsg);
					row.set(columnName, errorEqn);
				}
			} else {
				final Class<?> eqnType = columnType == Integer.class ? Long.class : columnType;
				final String errorMsg = compiler.getLastErrorMsg();
				final Equation errorEqn = compiler.getErrorEquation(text, eqnType, errorMsg);
				row.set(columnName, errorEqn);
			}
		} else { // Not an equation!
			Object parsedValue;
			final StringBuilder errorMessage = new StringBuilder();
			if (columnType == String.class)
				parsedValue = text;
			else if (columnType == Long.class)
				parsedValue = parseLong(text, errorMessage);
			else if (columnType == Integer.class)
				parsedValue = parseInteger(text, errorMessage);
			else if (columnType == Double.class)
				parsedValue = parseDouble(text, errorMessage);
			else if (columnType == Boolean.class)
				parsedValue = parseBoolean(text, errorMessage);
			else if (columnType == List.class)
				parsedValue = parseList(text, attrs.getListElementType(columnName),
							errorMessage);
			else
				throw new IllegalStateException("unknown column type: "
								+ columnType.getName() + "!");
			if (parsedValue != null)
				row.set(columnName, parsedValue);
			else {
				final Class<?> eqnType = columnType == Integer.class ? Long.class : columnType;
				final Equation errorEqn = compiler.getErrorEquation(text, eqnType, errorMessage.toString());
				row.set(columnName, errorEqn);
			}
		}
	}

	private String getUnqualifiedName(final Class<?> type) {
		final String typeName = type.getName();
		final int lastDotPos = typeName.lastIndexOf('.');
		return lastDotPos == -1 ? typeName : typeName.substring(lastDotPos + 1);
	}

	private void initVariableNameToTypeMap(final Map<String, Class> variableNameToTypeMap) {
		final Map<String, Class<?>> columnNameToTypeMap = attrs.getColumnTypeMap();
		for (final String columnName : columnNameToTypeMap.keySet()) {
			final Class<?> columnType = columnNameToTypeMap.get(columnName);
			if (columnType == String.class)
				variableNameToTypeMap.put(columnName, String.class);
			else if (columnType == Integer.class)
				variableNameToTypeMap.put(columnName, Long.class);
			else if (columnType == Long.class)
				variableNameToTypeMap.put(columnName, Long.class);
			else if (columnType == Boolean.class)
				variableNameToTypeMap.put(columnName, Boolean.class);
			else if (columnType == List.class)
				variableNameToTypeMap.put(columnName, List.class);
			else
				throw new IllegalStateException("unknown type \""
								+ columnType.getName() + "\"!");
		}
	}

	private Object parseLong(final String text, final StringBuilder errorMessage) {
		try {
			return Long.valueOf(text);
		} catch (final Exception e) {
			errorMessage.append("Can't convert text to a whole number!");
			return null;
		}
	}

	private Object parseInteger(final String text, final StringBuilder errorMessage) {
		try {
			return Integer.valueOf(text);
		} catch (final Exception e) {
			errorMessage.append("Can't convert text to a whole number!");
			return null;
		}
	}

	private Object parseDouble(final String text, final StringBuilder errorMessage) {
		try {
			return Double.valueOf(text);
		} catch (final Exception e) {
			errorMessage.append("Can't convert text to a floating point number!");
			return null;
		}
	}

	private Object parseBoolean(final String text, final StringBuilder errorMessage) {
		if (text.compareToIgnoreCase("true") == 0)
			return Boolean.valueOf(true);

		if (text.compareToIgnoreCase("false") == 0)
			return Boolean.valueOf(false);

		errorMessage.append("Can't convert text to a truth value!");
		return null;
	}

	private Object parseList(final String text, final Class elementType, final StringBuilder errorMessage) {
		errorMessage.append("parseList() has not yet been implemented!");
		return null;
	}

	public void cleanup() {
		eventHelper.removeMicroListener(this, RowCreatedMicroListener.class, attrs);
		for (final RowSetMicroListenerProxy proxy : rowToListenerProxyMap.values())
			proxy.cleanup();
	}
}


class RowSetMicroListenerProxy implements RowSetMicroListener {
	private final BrowserTableModel tableModel;
	private final CyEventHelper eventHelper;
	private final CyRow row;

	RowSetMicroListenerProxy(final BrowserTableModel tableModel, final CyEventHelper eventHelper,
				 final CyRow row)
	{
		this.tableModel = tableModel;
		this.eventHelper = eventHelper;
		this.row = row;

		eventHelper.addMicroListener(this, RowSetMicroListener.class, row);
	}

	@Override
	public void handleRowSet(final String columnName, final Object newValue, final Object newRawValue) {
		tableModel.handleRowValueUpdate(row, columnName, newValue, newRawValue);
	}

	void cleanup() {
		eventHelper.removeMicroListener(this, RowSetMicroListener.class, row);
	}
}
