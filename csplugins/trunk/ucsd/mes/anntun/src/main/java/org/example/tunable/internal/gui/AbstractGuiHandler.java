
package org.example.tunable.internal.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import org.example.tunable.*;

public abstract class AbstractGuiHandler extends AbstractHandler implements GuiHandler, ActionListener {

	protected JPanel panel;

    private String depName;
    private String depState;

	public AbstractGuiHandler(Field f, Object o, Tunable t) {
		super(f,o,t);	
        String s = t.dependsOn();
        if ( !s.equals("") ) {
            depName = s.substring(0,s.indexOf("="));
            depState = s.substring(s.indexOf("=") + 1);
        }
	
		panel = new JPanel();
	}

	public void actionPerformed(ActionEvent ae) {
		//System.out.println(this.getName() + " actionPerformed");
		handle();
		notifyDependents();
	}

	// notify dependencies that this object is changing
	public void notifyDependents() {
		String state = getState();
		String name = getName();
		for ( HandlerListener hl : listeners )
			hl.handlerChanged(this);	
	}


	public String getDependency() {
		return depName;
	}

	public void handlerChanged(Handler h) {

		if ( h instanceof GuiHandler ) {
			GuiHandler gh = (GuiHandler)h;	

			// if we don't depend on anything, then we should be enabled
			if ( depName == null || depState == null ) {
				setEnabledContainer(true,panel); 
				return;
			}

			String name = gh.getName();
			String state = gh.getState();

			// if the dependency name matches ...
   			if ( depName.equals(name) ) {
				// ... and the state matches, then enable 
				if ( depState.equals(state) )
					setEnabledContainer(true,panel); 
				// ... and the state doesn't match, then disable 
				else	
					setEnabledContainer(false,panel); 
			}
		}

        return;
		
	}

	private void setEnabledContainer(boolean enable, Container c) {
		c.setEnabled(enable);
		for ( Component child : c.getComponents() ) {
			if ( child instanceof Container )
				setEnabledContainer(enable,(Container)child);
			else
				child.setEnabled(enable);
		}
	}

	public String getName() {
        if ( f != null ) {
            return f.getName();
        } else if ( m != null ) {
            return m.getName();
        } else
            return "";
	}

	public JPanel getJPanel() {
		return panel;
	}

	public abstract void handle();

	public abstract String getState() ;
}
