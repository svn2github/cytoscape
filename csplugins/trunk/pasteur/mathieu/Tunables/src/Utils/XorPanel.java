package Utils;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import GuiInterception.Guihandler;
import HandlerFactory.Handler;
import HandlerFactory.HandlerListener;
import Tunable.Tunable;


public class XorPanel extends JPanel {
	
	
	JPanel switchPanel;
	JPanel contentPanel;
	JPanel currentPanel = null;	

	Guihandler gh;
	boolean first = true;

	public XorPanel(String title, Guihandler g) {
		super();
		gh = g;
		gh.addDependent( new GuiHandlerSwitchListener() ); 

		switchPanel = new JPanel(); 
		contentPanel = new JPanel(new CardLayout());
		TitledBorder titleborder = BorderFactory.createTitledBorder(title);
		titleborder.setTitleColor(Color.GREEN);
		setBorder(titleborder);
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		//setBorder(BorderFactory.createTitledBorder(title));
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

	
/*	class GuiHandlerSwitchListener implements  HandlerListener {

		public void handlerChanged(Handler gh) {
			if ( gh instanceof Guihandler ) {
				CardLayout cl = (CardLayout) contentPanel.getLayout();
				cl.show(contentPanel, ((Guihandler)gh).getState());
			}
		}
	}*/

	class GuiHandlerSwitchListener implements  Guihandler {

		public Tunable getTunable() {return null;}
		public Field getField() {return null;}
		public Method getMethod() {return null;}
		public Object getObject() {return null;}
		public void actionPerformed(ActionEvent ae) { }
		public void notifyDependents() { } 
		public void addDependent(Guihandler gh) { } 
		public String getDependency() { return null; }

		public void checkDependency(String name, String state) {
			CardLayout cl = (CardLayout) contentPanel.getLayout();
			cl.show(contentPanel, state);
		}
	
		public String getName() { return null; }
		public JPanel getJPanel() { return null; }
		public void handle() {}
		public String getState() {return null;}
		public void returnPanel() {}
		public void addHandlerListener(HandlerListener listener) {
			// TODO Auto-generated method stub
			
		}
		public void handlerChanged(Handler otherHandler) {
			// TODO Auto-generated method stub
			
		}
		public boolean removeHandlerListener(HandlerListener listener) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
	
