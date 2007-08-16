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

package csplugins.enhanced.search;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.event.*;


class EnhancedSearchDialog extends JDialog {

	JButton search;
	JButton cancel;
	JTextField searchField;

	boolean cancelled = true;

	public EnhancedSearchDialog() {
		
		setTitle("Enhanced Search");
		setModal(true);
		getContentPane().setLayout(new BorderLayout());

		JPanel main_panel = new JPanel();
		main_panel.setLayout(new BorderLayout());

		JLabel label = new JLabel(
				"<HTML>Please enter your search query below <small>(use \"*\" and \"?\" for wildcards)</small></HTML>");
		main_panel.add(label, BorderLayout.NORTH);

		searchField = new JTextField(30);
		searchField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cancelled = false;
				EnhancedSearchDialog.this.setVisible(false);
			}
		});
		main_panel.add(searchField, BorderLayout.CENTER);

		search = new JButton("Search");
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cancelled = false;
				EnhancedSearchDialog.this.setVisible(false);
			}
		});

		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				EnhancedSearchDialog.this.setVisible(false);
			}
		});

		JPanel button_panel = new JPanel();
		button_panel.add(search);
		button_panel.add(cancel);
		main_panel.add(button_panel, BorderLayout.SOUTH);

		// getContentPane().add(main_panel, BorderLayout.CENTER);
		setContentPane(main_panel);

		setResizable(true);
		this.pack();
		
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public String getQuery() {
		String query = searchField.getText();
		return query;
	}

}
