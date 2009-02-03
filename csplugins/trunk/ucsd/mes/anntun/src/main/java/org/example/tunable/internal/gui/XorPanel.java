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

	public XorPanel(String title, GuiHandler g) {
		super();
		gh = g;
		gh.addHandlerListener( new GuiHandlerSwitchListener() ); 

		switchPanel = new JPanel(); 
		contentPanel = new JPanel(new CardLayout());

		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createTitledBorder(title));
		super.add(switchPanel);
		super.add(contentPanel);
	}

	public Component add(Component c) {
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
		if ( first ) {
			switchPanel.add(c); 
			first = false;
		} else {
			currentPanel = (JPanel)c;
			contentPanel.add(c,constraint);
		}
	}


	class GuiHandlerSwitchListener implements  HandlerListener {

		public void handlerChanged(Handler gh) {
			if ( gh instanceof GuiHandler ) {
				CardLayout cl = (CardLayout) contentPanel.getLayout();
				cl.show(contentPanel, ((GuiHandler)gh).getState());
			}
		}
	}
}
	
