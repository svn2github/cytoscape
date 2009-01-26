package org.example.tunable.internal.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.reflect.*;
import org.example.tunable.*; 


public class XorPanel extends JPanel {
	
	
	JPanel switchPanel;
	JPanel contentPanel;
	JPanel currentPanel = null;	

	GuiHandler gh;
	boolean first = true;

	public XorPanel(GuiHandler g) {
		super();
		//System.out.println("XOR PANEL for " + g.getName());
		gh = g;
		gh.addDependent( new GuiHandlerSwitchListener() ); 

		switchPanel = new JPanel(); 
		contentPanel = new JPanel(new CardLayout());

		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		super.add(switchPanel);
		super.add(contentPanel);
	}

	public Component add(Component c) {
		//System.out.println("adding unconstrained " + first);
		if ( first ) {
			switchPanel.add(c); 
			first = false;
			return c;
		} else {
			if ( currentPanel == null )
				throw new RuntimeException("current panel is null!");

			currentPanel.add(c);
			return c;
		}
	}

	public void add(Component c, Object constraint) {
		//System.out.println("adding constrained " + first + "  " + constraint);
		if ( first ) {
			switchPanel.add(c); 
			first = false;
		} else {
			currentPanel = (JPanel)c;
			contentPanel.add(c,constraint);
		}
	}


	class GuiHandlerSwitchListener implements  GuiHandler {

		public Tunable getTunable() {return null;}
		public Field getField() {return null;}
		public Method getMethod() {return null;}
		public Object getObject() {return null;}
		public void actionPerformed(ActionEvent ae) { }
		public void notifyDependents() { } 
		public void addDependent(GuiHandler gh) { } 
		public String getDependency() { return null; }

		public void checkDependency(String name, String state) {
			//System.out.println("calling check dep " + name + " " + state);
		
			CardLayout cl = (CardLayout) contentPanel.getLayout();
			cl.show(contentPanel, state);
		}
	
		public String getName() { return null; }
		public JPanel getJPanel() { return null; }
		public void handle() {}
		public String getState() {return null;}
	}
}
	
