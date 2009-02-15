
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

import csplugins.quickfind.view.QuickFindPanel;

import csplugins.test.widgets.test.unitTests.text.TestNumberIndex;
import csplugins.test.widgets.test.unitTests.view.TestTextIndexComboBox;

import csplugins.widgets.autocomplete.index.NumberIndex;
import csplugins.widgets.autocomplete.index.TextIndex;
import csplugins.widgets.autocomplete.view.TextIndexComboBox;

import prefuse.data.query.NumberRangeModel;

import prefuse.util.ui.JRangeSlider;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Used to Test QuickFindPanel from the command line.
 *
 * @author Ethan Cerami
 */
public class RunQuickFindPanel {
	private static JTextArea textArea;

	/**
	 * Creates and shows GUI.
	 */
	private static void createAndShowGUI() {
		JFrame frame = new JFrame();
		final QuickFindPanel panel = new QuickFindPanel();
		panel.enableAllQuickFindButtons();
		frame.getContentPane().add(panel, BorderLayout.NORTH);

		//  Create Sample Indexes
		final TextIndex textIndex = TestTextIndexComboBox.createSampleTextIndex();
		final NumberIndex numberIndex = TestNumberIndex.createSampleNumberIndex();
		panel.setIndex(textIndex);

		textArea = new JTextArea();

		JScrollPane scrollPane = new JScrollPane(textArea);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		final JComboBox box = new JComboBox();
		box.addItem("Text index");
		box.addItem("Number index");
		box.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String selection = (String) box.getSelectedItem();

					if (selection.equals("Text index")) {
						panel.setIndex(textIndex);
					} else {
						panel.setIndex(numberIndex);
					}
				}
			});

		final TextIndexComboBox comboBox = panel.getTextIndexComboBox();
		comboBox.addFinalSelectionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					textArea.append("Select:  " + comboBox.getSelectedItem() + "\n");
				}
			});

		final JRangeSlider slider = panel.getSlider();
		slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					NumberRangeModel model = (NumberRangeModel) slider.getModel();
					Number low = (Number) model.getLowValue();
					Number high = (Number) model.getHighValue();
					textArea.append("Select:  " + low + " - " + high + "\n");
				}
			});

		frame.getContentPane().add(box, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(3);
		frame.setSize(300, 200);
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
						RunQuickFindPanel.createAndShowGUI();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	}
}
