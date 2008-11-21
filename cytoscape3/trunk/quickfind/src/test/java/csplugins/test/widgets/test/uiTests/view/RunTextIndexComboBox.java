
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

package csplugins.test.widgets.test.uiTests.view;

import csplugins.test.widgets.test.unitTests.view.TestTextIndexComboBox;
import csplugins.widgets.autocomplete.index.TextIndex;
import csplugins.widgets.autocomplete.view.ComboBoxFactory;
import csplugins.widgets.autocomplete.view.TextIndexComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Used to Test TextIndexComboBox from the command line.
 *
 * @author Ethan Cerami
 */
public class RunTextIndexComboBox {
	private static JFrame frame;

	/**
	 * Creates and shows GUI.
	 */
	private static void createAndShowGUI()
	    throws IllegalAccessException, UnsupportedLookAndFeelException, ClassNotFoundException,
	               InstantiationException {
		//  Create Text Index, and populate with sample data.
		TextIndex textIndex = TestTextIndexComboBox.createSampleTextIndex();

		TextIndexComboBox comboBox = ComboBoxFactory.createTextIndexComboBox(textIndex, 1.2);
		comboBox.setPrototypeDisplayValue("0123456790");

		comboBox.addFinalSelectionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					TextIndexComboBox box = (TextIndexComboBox) e.getSource();
					Object item = box.getSelectedItem();
					JOptionPane.showMessageDialog(frame, "Final Selection:  " + item.toString());
				}
			});

		// create and show a window containing the combo box
		frame = new JFrame();
		frame.setDefaultCloseOperation(3);
		frame.getContentPane().add(comboBox, BorderLayout.NORTH);
		frame.setSize(200, 200);
		frame.setVisible(true);
	}

	/**
	 * Main method, invoked for testing purposes only.
	 *
	 * @param args Command line arguments (none expected).
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						RunTextIndexComboBox.createAndShowGUI();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	}
}
