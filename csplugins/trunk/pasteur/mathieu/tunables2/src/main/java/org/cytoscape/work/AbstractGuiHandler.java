
package org.cytoscape.work;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cytoscape.work.internal.gui.*;


public abstract class AbstractGuiHandler extends AbstractHandler implements Guihandler, ActionListener,ChangeListener,ListSelectionListener {

	protected JPanel panel;

    private String depName;
    private String depState;
    
    private String depUnState;

	private List<Guihandler> deps;

	public AbstractGuiHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
        String s = t.dependsOn();
        if ( !s.equals("") ) {
        	if(!s.contains("!=")){
        		depName = s.substring(0,s.indexOf("="));
        		depState = s.substring(s.indexOf("=") + 1);
        		depUnState = "";
        	}
        	else {
        		depName = s.substring(0,s.indexOf("!"));
        		depUnState = s.substring(s.indexOf("=")+1);
        		depState = "";
        	}
        }
	
		deps = new LinkedList<Guihandler>();
		panel = new JPanel();
	}

	public void actionPerformed(ActionEvent ae) {
		//System.out.println(this.getName() + " actionPerformed");
		notifyDependents();
		handle();
	}

	public void stateChanged(ChangeEvent e){
		//handle();
		notifyDependents();
	}
	
    public void valueChanged(ListSelectionEvent le) {
    	boolean ok = le.getValueIsAdjusting();
    	if(!ok){
    		//handle();
    		notifyDependents();
    	}
    }

	
	// notify dependencies that this object is changing
	public void notifyDependents() {
		String state = getState();
		String name = getName();
		for ( Guihandler gh : deps )
			gh.checkDependency( name, state ); 
	}

	// add a dependency on this object 
	public void addDependent(Guihandler gh) {
		//System.out.println("adding " + gh.getName() + " dependent to " + this.getName() );
		if ( !deps.contains(gh) )
			deps.add(gh);
	}

	public String getDependency() {
		return depName;
	}

	public void checkDependency(String name, String state) {

		// if we don't depend on anything, then we should be enabled
		if ( depName == null || depState == null ) {
			setEnabledContainer(true,panel); 
			return;
		}

		// if the dependency name matches ...
        if ( depName.equals(name) ) {
			// ... and the state matches, then enable 
        	if(depState!=""){
        		if ( depState.equals(state) ){
        			setEnabledContainer(true,panel);
        			handle();}
			// ... and the state doesn't match, then disable 
        		else{
        			setEnabledContainer(false,panel);
        		}
        	}
        	else {
        		if ( !depUnState.equals(state) ){
        			setEnabledContainer(true,panel);
        			handle();
        		}
			// ... and the state doesn't match, then disable 
        		else{
        			setEnabledContainer(false,panel);
        		}
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

	public abstract String getState();

}
