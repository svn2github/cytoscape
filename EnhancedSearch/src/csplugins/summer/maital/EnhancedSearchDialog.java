package csplugins.summer.maital;


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

		JLabel label = new JLabel("<HTML>Please enter your search query below <small>(use \"*\" and \"?\" for wildcards)</small></HTML>");
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

//		getContentPane().add(main_panel, BorderLayout.CENTER);
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



