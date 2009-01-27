package org.example.tunable.internal.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;


public class CollapsablePanel extends JPanel implements ActionListener {
	
	private static final String expandName = "Expand >>";
	private static final String collapseName = "<< Collapse";

	private boolean expandPaneVisible;

	private JToggleButton myExpandButton; 
	private JPanel buttonPanel; 
	private JPanel contentsPanel;

	private List<Component> listInPane;
	
	public CollapsablePanel(String name) {
		listInPane = new ArrayList<Component>(); 

		setBorder(BorderFactory.createTitledBorder(name));
		setLayout(new BorderLayout());

		myExpandButton = new JToggleButton(expandName);		
		myExpandButton.setPreferredSize (new Dimension (90, 20));
		myExpandButton.setMargin (new Insets (2, 2, 2, 2));
		myExpandButton.addActionListener(this);

		buttonPanel = new JPanel();
		buttonPanel.add(myExpandButton); 
		super.add(buttonPanel,BorderLayout.EAST);

		contentsPanel = new JPanel();
		contentsPanel.setLayout(new BoxLayout(contentsPanel,BoxLayout.PAGE_AXIS));
		super.add(contentsPanel,BorderLayout.WEST);
	
		expandPaneVisible = false;
	}

	public Component add(Component c) {
		listInPane.add(c);
		return c;
	}

	public void add(Component c, Object o) {
		listInPane.add(c);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == myExpandButton) {
			if (expandPaneVisible)
				collapsePanel();
			else
				expandPanel();
		}
	}

	private void collapsePanel() {
		contentsPanel.removeAll();
		myExpandButton.setText(expandName);
		expandPaneVisible = false;
	}
		
	private void expandPanel() {
		for ( Component c : listInPane )
			contentsPanel.add(c);
		invalidate();
		validate();
		myExpandButton.setText(collapseName);
		expandPaneVisible = true;
	}
}
