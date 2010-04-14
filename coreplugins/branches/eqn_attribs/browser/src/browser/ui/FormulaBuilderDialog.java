/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package browser.ui;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import browser.DataObjectType;
import browser.DataTableModel;

import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.data.eqn_attribs.AttribEqnCompiler;
import cytoscape.data.eqn_attribs.AttribFunction;
import cytoscape.data.eqn_attribs.AttribParser;
import cytoscape.data.eqn_attribs.AttribToken;
import cytoscape.data.eqn_attribs.AttribTokeniser;
import cytoscape.data.eqn_attribs.Equation;
import cytoscape.data.eqn_attribs.EquationUtil;
import cytoscape.data.eqn_attribs.Parser;

import giny.model.GraphObject;

import org.jdesktop.layout.GroupLayout;


enum ApplicationDomain {
	CURRENT_CELL("Current cell only"),      // The currently selected cell in the browser.
	CURRENT_SELECTION("Current selection"), // All entries in the browser.
	ENTIRE_ATTRIBUTE("Entire attribute");   // All values of the current attribute.

	private final String asString;
	ApplicationDomain(final String asString) { this.asString = asString; }
	@Override public String toString() { return asString; }
}


public class FormulaBuilderDialog extends JDialog {
	private String columnName;
	private JComboBox functionComboBox = null;
	private JLabel usageLabel = null;
	private JTextField formulaTextField = null;
	private JPanel argumentPanel;
	private JButton addButton;
	private JButton doneButton;
	private JComboBox attribNamesComboBox = null;
	private JLabel applyToLabel;
	private JComboBox applyToComboBox = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private AttribFunction function = null;
	private Map<String, AttribFunction> stringToFunctionMap;
	private Map<String, Class> attribNamesAndTypes;
	private ArrayList<Class> leadingArgs;
	private ApplicationDomain applicationDomain;
	private DataTableModel tableModel;
	private DataObjectType tableObjectType;
	private final JTable table;


	public FormulaBuilderDialog(final DataTableModel tableModel, final JTable table,
	                            final DataObjectType tableObjectType, final Frame parent,
	                            final Map<String, Class> attribNamesAndTypes,
	                            final String columnName)
	{
		super(parent);
		this.setTitle("Creating a formula for: " + columnName);

		this.columnName = columnName;
		this.stringToFunctionMap = new HashMap<String, AttribFunction>();
		this.attribNamesAndTypes = attribNamesAndTypes;
		this.leadingArgs = new ArrayList<Class>();
		this.applicationDomain = ApplicationDomain.CURRENT_CELL;
		this.tableModel = tableModel;
		this.tableObjectType = tableObjectType;
		this.table = table;

		final Container contentPane = getContentPane();
		final GroupLayout groupLayout = new GroupLayout(contentPane);
		contentPane.setLayout(groupLayout);

		initFunctionComboBox(contentPane);
		initUsageLabel(contentPane);
		initFormulaTextField(contentPane);
		initArgumentPanel(contentPane);
		initApplyToLabel(contentPane);
		initApplyToComboBox(contentPane);
		initOkButton(contentPane);
		initCancelButton(contentPane);

		setSize(600, 250);

		initLayout(groupLayout);
	}

	private void initFunctionComboBox(final Container contentPane) {
		functionComboBox = new JComboBox();
		contentPane.add(functionComboBox);
		functionComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					functionSelected();
				}
			});

		final AttribParser parser = Parser.getParser();
		final Set<AttribFunction> functions = parser.getRegisteredFunctions();
		final String[] functionNames = new String[functions.size()];
		int index = 0;
		for (final AttribFunction function : functions) {
			functionNames[index] = function.getName() + ": " + function.getFunctionSummary();
			stringToFunctionMap.put(functionNames[index], function);
			++index;
		}

		Arrays.sort(functionNames);

		final Class requestedReturnType = getAttributeType(columnName);
		for (final String functionName : functionNames) {
			if (returnTypeIsCompatible(requestedReturnType, stringToFunctionMap.get(functionName).getReturnType()))
				functionComboBox.addItem(functionName);
		}

		functionComboBox.setEditable(false);

		if (functionComboBox.getItemCount() != 0) {
			functionComboBox.setSelectedIndex(0);
			function = stringToFunctionMap.get(functionNames[0]);
		}
	}

	/**
	 *  @returns the type of the attribute "attribName" translated into the language of attribute equations or null
	 */
	private Class getAttributeType(final String attribName) {
		final byte type = tableObjectType.getAssociatedAttribute().getType(attribName);
		switch (type) {
		case MultiHashMapDefinition.TYPE_BOOLEAN:
			return Boolean.class;
		case MultiHashMapDefinition.TYPE_FLOATING_POINT:
			return Double.class;
		case MultiHashMapDefinition.TYPE_INTEGER:
			return Long.class;
		case MultiHashMapDefinition.TYPE_STRING:
			return String.class;
		default:
			return null;
		}
	}

	private boolean returnTypeIsCompatible(final Class requiredType, final Class returnType) {
		if (returnType == Object.class || requiredType == String.class)
			return true;

		if (returnType == requiredType)
			return true;

		if (requiredType == Boolean.class && returnType != String.class)
			return true;

		if (requiredType == Double.class && returnType == Long.class)
			return true;

		if (requiredType == Long.class && returnType == Double.class)
			return true;

		return false;
	}

	private void initUsageLabel(final Container contentPane) {
		usageLabel = new JLabel();
		contentPane.add(usageLabel);
		if (function != null)
			usageLabel.setText(function.getUsageDescription());
	}

	private void initFormulaTextField(final Container contentPane) {
		formulaTextField = new JTextField(80);
		contentPane.add(formulaTextField);
		formulaTextField.setEditable(false);
		if (function != null)
			formulaTextField.setText("=" + function.getName() + "(");
	}

	private void initArgumentPanel(final Container contentPane) {
		argumentPanel = new JPanel();
		contentPane.add(argumentPanel);
		argumentPanel.setBorder(BorderFactory.createTitledBorder("Next Argument"));

		attribNamesComboBox = new JComboBox();
		updateAttribNamesComboBox();
		argumentPanel.add(attribNamesComboBox);

		final JLabel orLabel = new JLabel();
		orLabel.setText("or");
		argumentPanel.add(orLabel);

		final JTextField constantValuesTextField = new JTextField(15);
		argumentPanel.add(constantValuesTextField);

		addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final StringBuilder formula = new StringBuilder(formulaTextField.getText());
					if (!leadingArgs.isEmpty()) // Not the first argument => we need a comma!
						formula.append(',');
					final String constExpr = constantValuesTextField.getText();
					if (constExpr != null && !constExpr.isEmpty()) {
						final List<Class> possibleArgTypes = getPossibleNextArgumentTypes();
						final Class exprType;
						if ((exprType = expressionIsValid(possibleArgTypes, constExpr)) == null)
							return;

						formula.append(constExpr);
						constantValuesTextField.setText("");
						leadingArgs.add(exprType);
					}
					else {
						final String attribName = (String)attribNamesComboBox.getSelectedItem();
						formula.append(EquationUtil.attribNameAsReference(attribName));
						leadingArgs.add(attribNamesAndTypes.get(attribName));
					}
					formulaTextField.setText(formula.toString());

					final List<Class> possibleNextArgTypes = getPossibleNextArgumentTypes();
					if (possibleNextArgTypes == null) {
						final String currentFormula = formulaTextField.getText();
						formulaTextField.setText(currentFormula + ")");

						addButton.setEnabled(false);
						doneButton.setEnabled(false);
						okButton.setEnabled(true);
					}
					else if (possibleNextArgTypes.contains(null)) {
						doneButton.setEnabled(true);
						okButton.setEnabled(true);
					}
					updateAttribNamesComboBox();
				}
			});
		argumentPanel.add(addButton);

		doneButton = new JButton("Done");
		doneButton.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
					final String currentFormula = formulaTextField.getText();
					formulaTextField.setText(currentFormula + ")");

					addButton.setEnabled(false);
					doneButton.setEnabled(false);
					okButton.setEnabled(true);
				}
			});

		argumentPanel.add(doneButton);
		doneButton.setEnabled(false);
	}

	private void initApplyToLabel(final Container contentPane) {
		applyToLabel = new JLabel("Apply to: ");
		contentPane.add(applyToLabel);
	}

	private void initApplyToComboBox(final Container contentPane) {
		applyToComboBox = new JComboBox();
		applyToComboBox.addItem(ApplicationDomain.CURRENT_CELL);
		applyToComboBox.addItem(ApplicationDomain.CURRENT_SELECTION);
		applyToComboBox.addItem(ApplicationDomain.ENTIRE_ATTRIBUTE);

		final Dimension widthAndHeight = applyToComboBox.getPreferredSize();
		final Dimension desiredWidthAndHeight = new Dimension(60, (int)widthAndHeight.getHeight());
		applyToComboBox.setPreferredSize(desiredWidthAndHeight);

		contentPane.add(applyToComboBox);
		applyToComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					applicationDomain = (ApplicationDomain)applyToComboBox.getSelectedItem();
				}
			});
		applyToComboBox.setEditable(false);
	}

	/**
	 *  Tests whether "expression" is valid given the possible argument types are "validArgTypes".
	 *  @returns null if "expression" is invalid or the type of "expression" if it was valid
	 */
	private Class expressionIsValid(final List<Class> validArgTypes, final String expression) {
		final AttribParser parser = Parser.getParser();
		if (!parser.parse("=" + expression, attribNamesAndTypes)) {
			displayErrorMessage(parser.getErrorMsg());
			return null;
		}
			
		final Class expressionType = parser.getType();
		if (validArgTypes.contains(expressionType))
			return expressionType;

		final StringBuilder errorMessage = new StringBuilder("Expression is of an incompatible data type (");
		errorMessage.append(getLastDotComponent(expressionType.toString()));
		errorMessage.append(") valid types are: ");
		for (int i = 0; i < validArgTypes.size(); ++i) {
			errorMessage.append(getLastDotComponent(validArgTypes.get(i).toString()));
			if (i < validArgTypes.size() - 1)
				errorMessage.append(',');
		}
		displayErrorMessage(errorMessage.toString());

		return null;
	}

	/**
	 *  Assumes that "s" consists of components separated by dots.
	 *  @returns the last component of "s"
	 */
	private String getLastDotComponent(final String s) {
		final int lastDotPos = s.lastIndexOf('.');
		if (lastDotPos == -1)
			return s;

		return s.substring(lastDotPos + 1);
	}

	/**
	 *  Fills the attribute names combox box with the subset of valid (as in potential current function
	 *  arguments) attribute names.
	 */
	private void updateAttribNamesComboBox() {
		attribNamesComboBox.removeAllItems();
		final List<Class> possibleArgTypes = getPossibleNextArgumentTypes();
		final ArrayList possibleAttribNames = new ArrayList(20);
		for (final String attribName : attribNamesAndTypes.keySet()) {
			final Class attribType = attribNamesAndTypes.get(attribName);
			if (isTypeCompatible(possibleArgTypes, attribType))
				attribNamesComboBox.addItem(attribName);
		}
	}

	/**
	 *  @returns the set of allowed types for the next argument or null if no additional argument is valid
	 */
	private List<Class> getPossibleNextArgumentTypes() {
		final Class[] leadingArgsAsArray = new Class[leadingArgs.size()];
		leadingArgs.toArray(leadingArgsAsArray);
		return function.getPossibleArgTypes(leadingArgsAsArray);
	}

	private boolean isTypeCompatible(final List<Class> allowedArgumentTypes, final Class attribType) {
		if (allowedArgumentTypes == null)
			return false;
		if (allowedArgumentTypes.contains(Object.class))
			return true;
		return allowedArgumentTypes.contains(attribType);
	}
			
	private void initOkButton(final Container contentPane) {
		okButton = new JButton("OK");
		contentPane.add(okButton);
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final StringBuilder errorMessage = new StringBuilder(30);
					if (updateCells(errorMessage))
						dispose();
					else
						displayErrorMessage(errorMessage.toString());
				}
			});
	}

	private boolean updateCells(final StringBuilder errorMessage) {
		final String formula = formulaTextField.getText();
		final int cellColum = table.getSelectedColumn();
		final String attribName = tableModel.getColumnName(cellColum);
		final CyAttributes attribs = tableModel.getCyAttributes();

		final Equation equation = compileEquation(attribs, attribName, formula, errorMessage);
		if (equation == null)
			return false;
		
		switch (applicationDomain) {
		case CURRENT_CELL:
			final int cellRow = table.getSelectedRow();
			tableModel.setValueAt(formula, cellRow, cellColum);
			break;
		case CURRENT_SELECTION:
			final List<GraphObject> selectedGraphObjects = tableModel.getObjects();
			for (final GraphObject graphObject : selectedGraphObjects) {
				if (!setAttribute(attribs, graphObject.getIdentifier(), attribName,
				                  equation, errorMessage))
					return false;
			}
			tableModel.updateColumn(equation, cellColum);
			break;
		case ENTIRE_ATTRIBUTE:
			final Iterable<String> ids = tableObjectType.getAssociatedIdentifiers();
			for (final String id : ids) {
				if (!setAttribute(attribs, id, attribName, equation, errorMessage))
					return false;
			}
			tableModel.updateColumn(equation, cellColum);
			break;
		default:
			throw new IllegalStateException("unknown application domain: "
			                                + applicationDomain + "!");
		}

		return true;
	}

	/**
	 *  @returns the compiled equation upon success or null if an error occurred
	 */
	private Equation compileEquation(final CyAttributes attribs, final String attribName,
	                                 final String formula, final StringBuilder errorMessage) 
	{
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		EquationUtil.initAttribNameToTypeMap(attribs, attribName, attribNameToTypeMap);
		final AttribEqnCompiler compiler = new AttribEqnCompiler();
		if (compiler.compile(formula, attribNameToTypeMap))
			return compiler.getEquation();

		errorMessage.append(compiler.getLastErrorMsg());
		return null;
	}

	/**
	 *  @returns true if the attribute value has been successfully updated, else false
	 */
	private boolean setAttribute(final CyAttributes attribs, final String id,
	                             final String attribName, final Equation newValue,
	                             final StringBuilder errorMessage)
	{
		try {
			attribs.setAttribute(id, attribName, newValue);
			return true;
		} catch (final Exception e) {
			errorMessage.append(e.getMessage());
			return false;
		}
	}

	private void initCancelButton(final Container contentPane) {
		cancelButton = new JButton("Cancel");
		contentPane.add(okButton);
		cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
	}

	private void functionSelected() {
		final String funcName = (String)functionComboBox.getSelectedItem();
		if (funcName == null || formulaTextField == null || usageLabel == null)
			return;

		leadingArgs.clear();
		function = stringToFunctionMap.get(funcName);
		final boolean zeroArgumentFunction = getPossibleNextArgumentTypes() == null;
		formulaTextField.setText("=" + function.getName() + (zeroArgumentFunction ? "()" : "("));
		usageLabel.setText(function.getUsageDescription());
		updateAttribNamesComboBox();
		addButton.setEnabled(zeroArgumentFunction ? false : true);
		doneButton.setEnabled(false);
		okButton.setEnabled(zeroArgumentFunction);
	}

	private void initLayout(final GroupLayout groupLayout) {
		// 1. vertical layout
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
					     .add(functionComboBox)
					     .add(usageLabel)
					     .add(argumentPanel)
					     .add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						       .add(applyToLabel)
						       .add(applyToComboBox))
					     .add(formulaTextField)
					     .add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						       .add(okButton)
						       .add(cancelButton)));

		// 2. horizontal layout
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.CENTER)
					       .add(functionComboBox)
					       .add(usageLabel)
					       .add(argumentPanel)
					       .add(groupLayout.createSequentialGroup()
							 .add(applyToLabel)
							 .add(applyToComboBox))
					       .add(formulaTextField)
					       .add(groupLayout.createSequentialGroup()
							 .add(okButton)
							 .add(cancelButton)));
	}

	private static void displayErrorMessage(final String errorMessage) {
		JOptionPane.showMessageDialog(new JFrame(), errorMessage, "Error",
		                              JOptionPane.ERROR_MESSAGE);
	}
}
