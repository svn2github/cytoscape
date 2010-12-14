/*
 Copyright (c) 2006, 2007, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.browser.internal;


import org.cytoscape.equations.BooleanList;
import org.cytoscape.equations.EqnCompiler;
import org.cytoscape.equations.DoubleList;
import org.cytoscape.equations.Equation;
import org.cytoscape.equations.FunctionUtil;
import org.cytoscape.equations.LongList;
import org.cytoscape.equations.StringList;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.undo.AbstractUndoableEdit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import javax.swing.JOptionPane;


/**
 * Validate and set new value to the CyAttributes.
 */
public class DataEditAction extends AbstractUndoableEdit {
	private final String attrKey;
	private final String attrName;
	private final Object old_value;
	private final Object new_value;
	private final CyTable table;
	private final DataTableModel tableModel;

	private boolean valid = false;
	private ValidatedObjectAndEditString objectAndEditString = null;

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
	public DataEditAction(final DataTableModel tableModel, final String attrKey, final String attrName,
	                      final Object old_value, final Object new_value, final CyTable table)
	{
		super(attrKey + " attribute " + attrName + " changed.");

		this.tableModel = tableModel;
		this.attrKey = attrKey;
		this.attrName = attrName;
		this.old_value = old_value;
		this.new_value = new_value;
		this.table = table;

		redo();
	}

	@Override
	public String getRedoPresentationName() {
		return "Redo: " + attrKey + ":" + attrName + " to:" + new_value + " from " + old_value;
	}

	@Override
	public String getUndoPresentationName() {
		return "Undo: " + attrKey + ":" + attrName + " back to:" + old_value + " from " + new_value;
	}

	public ValidatedObjectAndEditString getValidatedObjectAndEditString() { return objectAndEditString; }

	/**
	 * Set attribute value.  Input validater is added.
	 *
	 * @param id
	 * @param att
	 * @param newValue
	 */
	private void setAttributeValue(final Object id, final String attrName, final Object newValue) {
		valid = false;
		if (newValue == null)
			return;

		// Error message for the popup dialog.
		String errMessage = null;

		// Change object to String
		final String newValueStr = newValue.toString().trim();

		final Class targetType = table.getType(attrName);
		if (targetType == Integer.class)
			handleInteger(newValueStr, id);
		else if (targetType == Double.class)
			handleDouble(newValueStr, id);
		else if (targetType == Boolean.class)
			handleBoolean(newValueStr, id);
		else if (targetType == String.class)
			handleString(newValueStr, id);
		else if (targetType == Long.class)
			handleLong(newValueStr, id);
		else if (targetType == List.class)
			handleList(newValueStr, id);
		else if (targetType == Map.class)
			handleMap(newValueStr, id);
	}

	private void handleInteger(final String newValueStr, final Object id) {
		// Deal with equations first:
		if (newValueStr != null && newValueStr.length() >= 2 && newValueStr.charAt(0) == '=') {
			final Equation equation = parseEquation(newValueStr, id, attrName);
			if (equation == null) {
				objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#PARSE");
				table.getRow(id).set(attrName, null);
				return;
			}

			final Class returnType = equation.getType();
			if (returnType != Long.class && returnType != Double.class && returnType != Boolean.class) {
				showErrorWindow("Error in attribute \"" + attrName
						+ "\": equation is of type " + getLastDotComponent(returnType.toString())
						+ " but should be of type Integer!");
				objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#TYPE");
				table.getRow(id).set(attrName, null);
				return;
			}

			table.getRow(id).set(attrName, equation);
			final Integer attrValue = table.getRow(id).get(attrName, Integer.class);
			String errorMessage = table.getLastEquationError();
			if (errorMessage != null)
				errorMessage = "#ERROR(" + errorMessage + ")";
			objectAndEditString = new ValidatedObjectAndEditString(attrValue, newValueStr, errorMessage);
			valid = true;
			return;
		}

		Integer newIntVal;
		try {
			newIntVal = Integer.valueOf(newValueStr);
			table.getRow(id).set(attrName, newIntVal);
			objectAndEditString = new ValidatedObjectAndEditString(newIntVal);
			valid = true;
		} catch (final Exception e) {
			objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#ERROR");
			table.getRow(id).set(attrName, null);
			showErrorWindow("Attribute " + attrName
					+ " should be an Integer (or the number is too big/small).");
		}
	}

	private void handleDouble(final String newValueStr, final Object id) {
		// Deal with equations first:
		if (newValueStr != null && newValueStr.length() >= 2 && newValueStr.charAt(0) == '=') {
			final Equation equation = parseEquation(newValueStr, id, attrName);
			if (equation == null) {
				objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#PARSE");
				table.getRow(id).set(attrName, null);
				return;
			}

			final Class returnType = equation.getType();
			if (returnType != Double.class && returnType != Long.class && returnType != Boolean.class) {
				showErrorWindow("Error in attribute \"" + attrName
						+ "\": equation is of type " + getLastDotComponent(returnType.toString())
						+ " but should be of type Floating Point!");
				objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#TYPE");
				table.getRow(id).set(attrName, null);
				return;
			}

			table.getRow(id).set(attrName, equation);
			final Double attrValue = table.getRow(id).get(attrName, Double.class);
			String errorMessage = table.getLastEquationError();
			if (errorMessage != null)
				errorMessage = "#ERROR(" + errorMessage + ")";
			objectAndEditString = new ValidatedObjectAndEditString(attrValue, newValueStr, errorMessage);
			valid = true;
			return;
		}

		Double newDblVal;
		try {
			newDblVal = Double.valueOf(newValueStr);
			table.getRow(id).set(attrName, newDblVal);
			objectAndEditString = new ValidatedObjectAndEditString(newDblVal);
			valid = true;
		} catch (final Exception e) {
			objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#ERROR");
			table.getRow(id).set(attrName, null);
			showErrorWindow("Attribute " + attrName
					+ " should be a floating point number (or the number is too big/small).");
		}
	}

	private void handleBoolean(final String newValueStr, final Object id) {
		// Deal with equations first:
		if (newValueStr != null && newValueStr.length() >= 2 && newValueStr.charAt(0) == '=') {
			final Equation equation = parseEquation(newValueStr, id, attrName);
			if (equation == null) {
				objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#PARSE");
				table.getRow(id).set(attrName, null);
				return;
			}

			final Class returnType = equation.getType();
			if (returnType != Boolean.class && returnType != Long.class && returnType != Double.class) {
				objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#TYPE");
				table.getRow(id).set(attrName, null);
				showErrorWindow("Error in attribute \"" + attrName
						+ "\": equation is of type " + getLastDotComponent(returnType.toString())
						+ " but should be of type Boolean!");
				return;
			}

			table.getRow(id).set(attrName, equation);
			final Boolean attrValue = table.getRow(id).get(attrName, Boolean.class);
			String errorMessage = table.getLastEquationError();
			if (errorMessage != null)
				errorMessage = "#ERROR(" + errorMessage + ")";
			objectAndEditString = new ValidatedObjectAndEditString(attrValue, newValueStr, errorMessage);
			valid = true;
			return;
		}

		Boolean newBoolVal = false;
		try {
			newBoolVal = Boolean.valueOf(newValueStr);
			table.getRow(id).set(attrName, newBoolVal);
			objectAndEditString = new ValidatedObjectAndEditString(newBoolVal);
			valid = true;
		} catch (final Exception e) {
			objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#ERROR");
			table.getRow(id).set(attrName, null);
			showErrorWindow("Attribute " + attrName + " should be a boolean value (true/false).");
		}
	}

	private void handleString(final String newValueStr, final Object id) {
		final String newStrVal = replaceCStyleEscapes(newValueStr);

		// Deal with equations first:
		if (newValueStr != null && newValueStr.length() >= 2 && newValueStr.charAt(0) == '=') {
			final Equation equation = parseEquation(newStrVal, id, attrName);
			if (equation == null) {
				objectAndEditString = new ValidatedObjectAndEditString(null, newStrVal, "#PARSE");
				table.getRow(id).set(attrName, null);
				return;
			}

			table.getRow(id).set(attrName, equation);
			objectAndEditString =
				new ValidatedObjectAndEditString(
					table.getRow(id).get(attrName, String.class),
					equation.toString());
			final String attrValue = table.getRow(id).get(attrName, String.class);
			String errorMessage = table.getLastEquationError();
			if (errorMessage != null)
				errorMessage = "#ERROR(" + errorMessage + ")";
			objectAndEditString =
				new ValidatedObjectAndEditString(attrValue, newValueStr, errorMessage);
			valid = true;
			return;
		}

		table.getRow(id).set(attrName, newStrVal);
		objectAndEditString = new ValidatedObjectAndEditString(newStrVal);
		valid = true;
	}

	private void handleLong(final String newValueStr, final Object id) {
		// Deal with equations first:
		if (newValueStr != null && newValueStr.length() >= 2 && newValueStr.charAt(0) == '=') {
			final Equation equation = parseEquation(newValueStr, id, attrName);
			if (equation == null) {
				objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#PARSE");
				table.getRow(id).set(attrName, null);
				return;
			}

			final Class returnType = equation.getType();
			if (returnType != Long.class && returnType != Double.class && returnType != Boolean.class) {
				showErrorWindow("Error in attribute \"" + attrName
						+ "\": equation is of type " + getLastDotComponent(returnType.toString())
						+ " but should be of type Long!");
				objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#TYPE");
				table.getRow(id).set(attrName, null);
				return;
			}

			table.getRow(id).set(attrName, equation);
			final Long attrValue = table.getRow(id).get(attrName, Long.class);
			String errorMessage = table.getLastEquationError();
			if (errorMessage != null)
				errorMessage = "#ERROR(" + errorMessage + ")";
			objectAndEditString = new ValidatedObjectAndEditString(attrValue, newValueStr, errorMessage);
			valid = true;
			return;
		}

		Long newLongVal;
		try {
			newLongVal = Long.valueOf(newValueStr);
			table.getRow(id).set(attrName, newLongVal);
			objectAndEditString = new ValidatedObjectAndEditString(newLongVal);
			valid = true;
		} catch (final Exception e) {
			objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#ERROR");
			table.getRow(id).set(attrName, null);
			showErrorWindow("Attribute " + attrName
					+ " should be an Long (or the number is too big/small).");
		}
	}

	private void handleList(final String newValueStr, final Object id) {
		// Deal with equations first:
		if (newValueStr != null && newValueStr.length() >= 2 && newValueStr.charAt(0) == '=') {
			final Equation equation = parseEquation(newValueStr, id, attrName);
			if (equation == null) {
				objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#PARSE");
				table.getRow(id).set(attrName, null);
				return;
			}

			final Class returnType = equation.getType();
			if (!FunctionUtil.isSomeKindOfList(returnType)) {
				objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#TYPE");
				table.getRow(id).set(attrName, null);
				showErrorWindow("Error in attribute \"" + attrName
						+ "\": equation is of type " + getLastDotComponent(returnType.toString())
						+ " but should be of type List!");
				return;
			}

			final byte listElementType = table.getListElementType(attrName);
			if (returnType == DoubleList.class && listElementType != CyAttributes.TYPE_FLOATING
			    || returnType == LongList.class && listElementType != CyAttributes.TYPE_INTEGER
			    || returnType == StringList.class && listElementType != CyAttributes.TYPE_STRING
			    || returnType == BooleanList.class && listElementType != CyAttributes.TYPE_BOOLEAN)
			{
				objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#TYPE");
				table.getRow(id).set(attrName, null);
				showErrorWindow("Error in attribute \"" + attrName
						+ "\": equation is of type " + getLastDotComponent(returnType.toString())
						+ " which is the wrong type of list!");
				return;
			}

			table.setListAttribute(id, attrName, equation);
			final List attrValue = table.getRow(id).get(attrName, List.class);
			String errorMessage = table.getLastEquationError();
			if (errorMessage != null)
				errorMessage = "#ERROR(" + errorMessage + ")";
			objectAndEditString = new ValidatedObjectAndEditString(attrValue, newValueStr, errorMessage);
			valid = true;
			return;
		}

		final String escapedString = replaceCStyleEscapes(newValueStr);
		final List origList = table.getListAttribute(id, attrName);

		List newList = null;
		if (origList.isEmpty() || origList.get(0).getClass() == String.class)
			newList = parseStringListValue(escapedString);
		else if (origList.get(0).getClass() == Double.class)
			newList = parseDoubleListValue(escapedString);
		else if (origList.get(0).getClass() == Integer.class)
			newList = parseIntegerListValue(escapedString);
		else if (origList.get(0).getClass() == Boolean.class)
			newList = parseBooleanListValue(escapedString);
		else
			throw new ClassCastException("can't determined List type!");

		if (newList == null) {
			objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#ERROR");
			table.getRow(id).set(attrName, null);
			showErrorWindow("Invalid list!");
			return;
		}
		else {
			table.setListAttribute(id, attrName, newList);
			objectAndEditString = new ValidatedObjectAndEditString(escapedString);
			valid = true;
		}
	}

	private void handleMap(final String newValueStr, final Object id) {
		// Deal with equations first:
		if (newValueStr != null && newValueStr.length() >= 2 && newValueStr.charAt(0) == '=') {
			objectAndEditString = new ValidatedObjectAndEditString(null, newValueStr, "#ERROR");
			table.getRow(id).set(attrName, null);
			showErrorWindow("Error in attribute \"" + attrName
					+ "\": no equations are supported for maps!");
			return;
		}

		showErrorWindow("Map editing is not supported in this version.");
	}

	/**
	 *  Assumes that "s" consists of components separated by dots.
	 *  @returns the last component of "s" or all of "s" if there are no dots
	 */
	private static String getLastDotComponent(final String s) {
		final int lastDotPos = s.lastIndexOf('.');
		if (lastDotPos == -1)
			return s;

		return s.substring(lastDotPos + 1);
	}

	/** Does some rudimentary list syntax checking and returns the number of items in "listCandidate."
	 * @param listCandidate a string that will be analysed as to list-syntax conformance.
	 * @returns -1 if "listCandidate" does not conform to a list syntax, otherwise the number of items in the simple list.
	 */
	private int countListItems(final String listCandidate) {
		if (listCandidate.length() < 2 || listCandidate.charAt(0) != '[' || listCandidate.charAt(listCandidate.length() - 1) != ']')
			return -1;

		int commaCount = 0;
		for (int charIndex = 1; charIndex < listCandidate.length() - 1; ++charIndex) {
			if (listCandidate.charAt(charIndex) == ',')
				++commaCount;
		}

		return commaCount;
	}

	/** Attemps to convert "listCandidate" to a List of String.
	 * @param listCandidate hopefully a list of strings.
	 * @returns the List if "listCandidate" has been successfully parsed, else null.
	 */
	private List parseStringListValue(final String listCandidate) {
		final int itemCount = countListItems(listCandidate);
		if (itemCount == -1)
			return null;

		final String bracketlessList = listCandidate.substring(1, listCandidate.length() - 1);
		final String[] items = bracketlessList.split("\\s*,\\s*");

		return Arrays.asList(items);
	}

	/** Attemps to convert "listCandidate" to a List of Double.
	 * @param listCandidate hopefully a list of doubles.
	 * @returns the List if "listCandidate" has been successfully parsed, else null.
	 */
	private List parseDoubleListValue(final String listCandidate) {
		final int itemCount = countListItems(listCandidate);
		if (itemCount == -1)
			return null;

		final String bracketlessList = listCandidate.substring(1, listCandidate.length() - 1);
		final String[] items = bracketlessList.split("\\s*,\\s*");

		final List<Double> doubleList = new ArrayList<Double>(itemCount);
		try {
			for (final String item : items) {
				final Double newDouble = Double.valueOf(item);
				doubleList.add(newDouble);
			}
		} catch (final NumberFormatException e) {
			return null; // At least one of the list items was not a double.
		}

		return doubleList;
	}

	/** Attemps to convert "listCandidate" to a List of Integer.
	 * @param listCandidate hopefully a list of ints.
	 * @returns the List if "listCandidate" has been successfully parsed, else null.
	 */
	private List parseIntegerListValue(final String listCandidate) {
		final int itemCount = countListItems(listCandidate);
		if (itemCount == -1)
			return null;

		final String bracketlessList = listCandidate.substring(1, listCandidate.length() - 1);
		final String[] items = bracketlessList.split("\\s*,\\s*");

		final List<Integer> intList = new ArrayList<Integer>(itemCount);
		try {
			for (final String item : items) {
				final Integer newInteger = Integer.valueOf(item);
				intList.add(newInteger);
			}
		} catch (final NumberFormatException e) {
			return null; // At least one of the list items was not a int.
		}

		return intList;
	}

	/** Attemps to convert "listCandidate" to a List of Boolean.
	 * @param listCandidate hopefully a list of booleans.
	 * @returns the List if "listCandidate" has been successfully parsed, else null.
	 */
	private List parseBooleanListValue(final String listCandidate) {
		final int itemCount = countListItems(listCandidate);
		if (itemCount == -1)
			return null;

		final String bracketlessList = listCandidate.substring(1, listCandidate.length() - 1);
		final String[] items = bracketlessList.split("\\s*,\\s*");

		final List<Boolean> booleanList = new ArrayList<Boolean>(itemCount);
		try {
			for (final String item : items) {
				final Boolean newBoolean = Boolean.valueOf(item);
				booleanList.add(newBoolean);
			}
		} catch (final NumberFormatException e) {
			return null; // At least one of the list items was not a boolean.
		}

		return booleanList;
	}

	private String replaceCStyleEscapes(String s) {
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
	private static void showErrorWindow(final String errMessage) {
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), errMessage, "Invalid Value!",
		                              JOptionPane.ERROR_MESSAGE);
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
		tableModel.setTableData();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 *  @returns the successfully compiled equation or null if an error occurred
	 */
	private Equation parseEquation(final String equation, final Object id,
	                               final String currentAttrName)
	{
		final Map<String, Class> attribNameToTypeMap = AttrUtil.getAttrNamesAndTypes(table);
		attribNameToTypeMap.put("ID", String.class);

		final EqnCompiler compiler = new EqnCompiler();
		if (!compiler.compile(equation, attribNameToTypeMap)) {
			showErrorWindow("Error in equation for attribute\"" + currentAttrName + "\": "
			                + compiler.getLastErrorMsg());
			return null;
		}

		return compiler.getEquation();
	}
}
