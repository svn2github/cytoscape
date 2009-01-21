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


public class CollapsablePanel extends JPanel implements ActionListener{
	
	private JToggleButton myExpandButton = null;
	private boolean expandPaneVisible=false;
	private static String ExpandName = "Expand>>";
	private static String CollapseName = "<<Collapse";
	private JPanel rightPanel = new JPanel();
	private JPanel leftPanel = new JPanel();
	List<Component> listInPane;
	
	
	public CollapsablePanel(String name){
		this.listInPane = new ArrayList<Component>(); 
		setBorder(BorderFactory.createTitledBorder(name));
		setLayout(new BorderLayout());

		add(rightPanel,BorderLayout.EAST);
		rightPanel.add(myExpandButton = createButton(ExpandName));

		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.PAGE_AXIS));
		add(leftPanel,BorderLayout.WEST);
	}

	public Component add(Component c) {
		listInPane.add(c);	
		return c;
	}
	
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == myExpandButton){
			if(expandPaneVisible){
				collapsePanel();
				myExpandButton.setText (ExpandName);
				expandPaneVisible = false;
			}
			else{
				expandPanel();
				myExpandButton.setText (CollapseName);
				expandPaneVisible = true;
			}
		}
	}

	
	private JToggleButton createButton(String name){
		JToggleButton button = new JToggleButton(name);		
		button.setPreferredSize (new Dimension (90, 20));
		button.setMargin (new Insets (2, 2, 2, 2));
		button.addActionListener(this);
		return button;
	}
	
	
	
	
	private void collapsePanel(){
		leftPanel.removeAll();
	}
		
		
	private void expandPanel() {
		for ( Component c : listInPane )
			leftPanel.add(c);
		updateUI();
		invalidate();
		validate();
	}
		
	
}
