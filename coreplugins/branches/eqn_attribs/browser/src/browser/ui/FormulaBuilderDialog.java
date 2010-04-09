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
import javax.swing.JTextField;

import browser.DataObjectType;
import browser.DataTableModel;

import cytoscape.data.eqn_attribs.AttribFunction;
import cytoscape.data.eqn_attribs.AttribParser;
import cytoscape.data.eqn_attribs.AttribToken;
import cytoscape.data.eqn_attribs.AttribTokeniser;
import cytoscape.data.eqn_attribs.EquationUtil;
import cytoscape.data.eqn_attribs.Parser;

import org.jdesktop.layout.GroupLayout;


public class FormulaBuilderDialog extends JDialog {
	private String columnName;
	private JComboBox functionComboBox = null;
	private JLabel usageLabel = null;
	private JTextField formulaTextField = null;
	private JPanel argumentPanel;
	private JComboBox attribNamesComboBox = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private AttribFunction function = null;
	private Map<String, AttribFunction> stringToFunctionMap;
	private Map<String, Class> attribNamesAndTypes;
	private ArrayList<Class> leadingArgs;


	public FormulaBuilderDialog(final DataTableModel tableModel, final DataObjectType tableObjectType,
	                            final Frame parent, final Map<String, Class> attribNamesAndTypes,
	                            final String columnName)
	{
		super(parent);
		this.setTitle("Creating a formula for: " + columnName);

		this.columnName = columnName;
		this.stringToFunctionMap = new HashMap<String, AttribFunction>();
		this.attribNamesAndTypes = attribNamesAndTypes;
		this.leadingArgs = new ArrayList<Class>();

		final Container contentPane = getContentPane();
		final GroupLayout groupLayout = new GroupLayout(contentPane);
		contentPane.setLayout(groupLayout);

		initFunctionComboBox(contentPane);
		initUsageLabel(contentPane);
		initFormulaTextField(contentPane);
		initArgumentPanel(contentPane);
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
		for (final String functionName : functionNames)
			functionComboBox.addItem(functionName);

		functionComboBox.setEditable(false);

		if (functionComboBox.getItemCount() != 0) {
			functionComboBox.setSelectedIndex(0);
			function = stringToFunctionMap.get(functionNames[0]);
		}
	}

	private void initUsageLabel(final Container contentPane) {
		usageLabel = new JLabel();
		contentPane.add(usageLabel);
		if (function != null)
			usageLabel.setText(function.getUsageDescription());
	}

	private void initFormulaTextField(final Container contentPane) {
		formulaTextField = new JTextField(40);
		contentPane.add(formulaTextField);
		formulaTextField.setEditable(false);
		if (function != null)
			formulaTextField.setText(function.getName() + "(");
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

		final JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final String currentFormula = formulaTextField.getText();
					final String constExpr = constantValuesTextField.getText();
					if (constExpr != null && !constExpr.isEmpty()) {
						final List<Class> possibleArgTypes = getPossibleNextArgumentTypes();
						if (!expressionIsValid(possibleArgTypes, constExpr))
							return;

						formulaTextField.setText(currentFormula + constExpr);
						constantValuesTextField.setText("");
					}
					else {
						final String attribName = (String)attribNamesComboBox.getSelectedItem();
						formulaTextField.setText(currentFormula
						                         + EquationUtil.attribNameAsReference(attribName));
					}
				}
			});
		argumentPanel.add(addButton);
	}

	private boolean expressionIsValid(final List<Class> validArgTypes, final String expression) {
		final StringBuilder errorMessage = new StringBuilder(30);
		final Class expressionType = parseSimpleExpression(expression, errorMessage);
		if (expression == null) {
			displayErrorMessage(errorMessage.toString());
			return false;
		}

		if (validArgTypes.contains(expressionType))
			return true;

		displayErrorMessage("Expression is of an incompatible data type!");
		return false;
	}

	/**
	 *  Fills the attribute names combox box with the subset of valid (as in potential current function
	 *  arguments) attribute names.
	 */
	private void updateAttribNamesComboBox() {
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
					dispose();
				}
			});
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
		formulaTextField.setText(function.getName() + "(");
		usageLabel.setText(function.getUsageDescription());
	}

	private void initLayout(final GroupLayout groupLayout) {
		// 1. vertical layout
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
					     .add(functionComboBox)
					     .add(usageLabel)
					     .add(formulaTextField)
					     .add(argumentPanel)
					     .add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						       .add(okButton)
						       .add(cancelButton)));

		// 2. horizontal layout
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.CENTER)
					       .add(functionComboBox)
					       .add(usageLabel)
					       .add(formulaTextField)
					       .add(argumentPanel)
					       .add(groupLayout.createSequentialGroup()
							 .add(okButton)
							 .add(cancelButton)));
	}

	/**
	 *  Parses very simple expressions of the form: "constant" or "constant1 operator constant2".
	 *  @returns either the type of the expression if there was no error or null if there was an error
	 *  In case of an error "errorMessage" will contain the error message after the call.
	 */
	private Class parseSimpleExpression(final String expression, final StringBuilder errorMessage) {
		final AttribTokeniser scanner = new AttribTokeniser(expression);

		AttribToken token1 = scanner.getToken();
		if (token1 == AttribToken.ERROR) {
			errorMessage.append(scanner.getErrorMsg());
			return null;
		}

		AttribToken token2 = scanner.getToken();
		if (token2 == AttribToken.ERROR) {
			errorMessage.append(scanner.getErrorMsg());
			return null;
		}

		if (token2 == AttribToken.EOS) {
			switch (token1) {
			case STRING_CONSTANT:
				return String.class;
			case FLOAT_CONSTANT:
				return Double.class;
			case BOOLEAN_CONSTANT:
				return Boolean.class;
			default:
				errorMessage.append("Invalid argument \"" + expression
				                    + "\"!\nPlease enter a constant or a constant followed by an operator and another constant.");
				return null;
			}
		}

		if (token2.isStringOperator()) {
			if (token1 != AttribToken.STRING_CONSTANT) {
				errorMessage.append("String operator applied to a non-String operand!");
				return null;
			}

			token2 = scanner.getToken();
			if (token2 != AttribToken.STRING_CONSTANT) {
				errorMessage.append("String operator applied to a non-String operand!");
				return null;
			}

			token2 = scanner.getToken();
			if (token2 != AttribToken.EOS) {
				errorMessage.append("Invalid string expression!");
				return null;
			}

			return String.class;
		}

		if (token2.isArithmeticOperator()) {
			if (token1 != AttribToken.FLOAT_CONSTANT) {
				errorMessage.append("Numeric operator applied to a non-numeric operand!");
				return null;
			}

			token2 = scanner.getToken();
			if (token2 != AttribToken.FLOAT_CONSTANT) {
				errorMessage.append("Numeric operator applied to a non-numeric operand!");
				return null;
			}

			token2 = scanner.getToken();
			if (token2 != AttribToken.EOS) {
				errorMessage.append("Invalid arithmetic expression!");
				return null;
			}

			return Double.class;
		}

		if (token2.isComparisonOperator()) {
			final AttribToken operatorToken = token2;

			token2 = scanner.getToken();
			if (token1 != token2) {
				errorMessage.append("Comparsion operator applied to two incompatible operands!");
				return null;
			}

			if (token1 == AttribToken.BOOLEAN_CONSTANT && token2 == AttribToken.BOOLEAN_CONSTANT) {
				if (operatorToken != AttribToken.EQUAL && operatorToken != AttribToken.NOT_EQUAL) {
					errorMessage.append("Only equality or inequality comparisions are valid for booelan operands!");
					return null;
				}
				return Boolean.class;
			}

			if (token1 != AttribToken.FLOAT_CONSTANT && token1 != AttribToken.STRING_CONSTANT) {
				errorMessage.append("Comparsion are only allowed between 2 string values or 2 numeric values!");
				return null;
			}

			return Boolean.class;
		}

		errorMessage.append("Invalid expression!");
		return null;
	}

	private static void displayErrorMessage(final String errorMessage) {
		JOptionPane.showMessageDialog(new JFrame(), errorMessage, "Error",
		                              JOptionPane.ERROR_MESSAGE);
	}
}
