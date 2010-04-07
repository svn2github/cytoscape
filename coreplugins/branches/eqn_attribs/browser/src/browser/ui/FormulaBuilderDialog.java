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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import browser.DataObjectType;
import browser.DataTableModel;

import cytoscape.data.eqn_attribs.AttribFunction;
import cytoscape.data.eqn_attribs.AttribParser;
import cytoscape.data.eqn_attribs.Parser;

import org.jdesktop.layout.GroupLayout;


public class FormulaBuilderDialog extends JDialog {
	private JComboBox functionComboBox = null;
	private JLabel usageLabel = null;
	private JTextField formulaTextField = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private AttribFunction function = null;
	private Map<String, AttribFunction> stringToFunctionMap;


	public FormulaBuilderDialog(final DataTableModel tableModel, final DataObjectType tableObjectType,
	                            final Frame parent)
	{
		super(parent);
		this.setTitle("Creating a formula for: " + tableObjectType.getDisplayName());

		this.stringToFunctionMap = new HashMap<String, AttribFunction>();
		
		final Container panel = getContentPane();
		final GroupLayout groupLayout = new GroupLayout(panel);
		panel.setLayout(groupLayout);

		initFunctionComboBox(panel);
		initUsageLabel(panel);
		initFormulaTextField(panel);
		initOkButton(panel);
		initCancelButton(panel);

		setSize(600, 150);

		initLayout(groupLayout);
	}

	private void initFunctionComboBox(final Container panel) {
		functionComboBox = new JComboBox();
		panel.add(functionComboBox);
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

	private void initUsageLabel(final Container panel) {
		usageLabel = new JLabel();
		panel.add(usageLabel);
		if (function != null)
			usageLabel.setText(function.getUsageDescription());
	}

	private void initFormulaTextField(final Container panel) {
		formulaTextField = new JTextField(40);
		panel.add(formulaTextField);
		formulaTextField.setEditable(false);
		if (function != null)
			formulaTextField.setText(function.getName() + "(");
	}

	private void initOkButton(final Container panel) {
		okButton = new JButton("OK");
		panel.add(okButton);
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
	}

	private void initCancelButton(final Container panel) {
		cancelButton = new JButton("Cancel");
		panel.add(okButton);
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
					     .add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						       .add(okButton)
						       .add(cancelButton)));

		// 2. horizontal layout
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.CENTER)
					       .add(functionComboBox)
					       .add(usageLabel)
					       .add(formulaTextField)
					       .add(groupLayout.createSequentialGroup()
							 .add(okButton)
							 .add(cancelButton)));
	}
}
