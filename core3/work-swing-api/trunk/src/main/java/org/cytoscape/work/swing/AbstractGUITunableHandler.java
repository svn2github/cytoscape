package org.cytoscape.work.swing;


import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cytoscape.work.AbstractTunableHandler;
import org.cytoscape.work.Tunable;


/**
 * Abstract handler for the creation of the GUI.
 * <br>
 * It provides the functions that are common to all types of Handlers
 */
public abstract class AbstractGUITunableHandler extends AbstractTunableHandler implements GUITunableHandler, ActionListener, ChangeListener, ListSelectionListener {
	/**
	 * <code>JPanel</code> that will contain the GUI object that represents in the best way the <code>Tunable</code> to the user
	 */
	protected JPanel panel;

	/**
	 * <pre>
	 * If this <code>Tunable</code> has a dependency on another <code>Tunable</code>,
	 * it represents the name of this dependency (i.e name of the other <code>Tunable</code>
	 * </pre>
	 */
	private String dependencyName;

	/**
	 * <pre>
	 * Represents the state of the dependency :
	 * could be : "true, false, an item of a <code>ListSelection</code>, a value ..."
	 * </pre>
	 */
	private String dependencyState;
	private String dependencyUnState;

	/**
	 * The list of dependencies between the <code>GUITunableHandlers</code>
	 */
	private List<GUITunableHandler> dependencies;

	/**
	 * Constructs an Abstract GUITunableHandler with dependencies informations
	 *
	 * @param f Field that is intercepted
	 * @param o Object that is contained in the Field <code>f</code>
	 * @param t <code>Tunable</code> annotations of the Field <code>f</code> annotated as <code>Tunable</code>
	 */
	protected AbstractGUITunableHandler(Field f, Object o, Tunable t) {
		super(f, o, t);
		init();
	}

	protected AbstractGUITunableHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
		init();
	}

	private void init() {
		String s = dependsOn();
		if (!s.equals("")) {
	        	if (!s.contains("!=")) {
	        		dependencyName = s.substring(0, s.indexOf("="));
	        		dependencyState = s.substring(s.indexOf("=") + 1);
	        		dependencyUnState = "";
	        	} else {
	        		dependencyName = s.substring(0, s.indexOf("!"));
	        		dependencyUnState = s.substring(s.indexOf("=") + 1);
	        		dependencyState = "";
	        	}
	        }

		dependencies = new LinkedList<GUITunableHandler>();
		panel = new JPanel();
	}

	public void actionPerformed(ActionEvent ae) {
		notifyDependents();
	}

	/**
	 * Notify a change of state of a <code>GUITunableHandler</code>
	 *
	 * @param e a modification that happened to this <code>handler</code>
	 */
	public void stateChanged(ChangeEvent e){
		notifyDependents();
	}

	/**
	 * Notify a change during the selection of an item in the <code>ListSelection</code> objects
	 *
	 * @param le change in the selection of an item in a list
	 */
	public void valueChanged(ListSelectionEvent le) {
		boolean ok = le.getValueIsAdjusting();
		if (!ok)
			notifyDependents();
	}


	/**
	 *  Notify dependencies that this object is changing
	 */
	public void notifyDependents() {
		String state = getState();
		String name = getName();
		for ( GUITunableHandler gh : dependencies )
			gh.checkDependency( name, state );
	}

	/**
	 *  Add a dependency on this <code>GUITunableHandler</code> to another <code>Tunable</code>
	 *  While the dependency rule to this other <code>GUITunableHandler</code> doesn't match, this one won't be available
	 *
	 *  @param gh <code>Handler</code> on which this one depends on.
	 */
	public void addDependent(GUITunableHandler gh) {
		//System.out.println("adding " + gh.getName() + " dependent to " + this.getName() );
		if ( !dependencies.contains(gh) )
			dependencies.add(gh);
	}


	/**
	 * To get the name of the dependency of this <code>GUITunableHandler</code>
	 * @return the name of the dependency
	 */
	public String getDependency() {
		return dependencyName;
	}

	/**
	 * Get the new "values" for the <code>Tunables</code> object that have been modified if their JPanel is enabled
	 */
	public void handleDependents(){
		if (panel.isEnabled())
			handle();
	}

	/**
	 * To check the dependencies of this <code>GUITunableHandler</code> with the others.
	 *
	 *
	 * <p><pre>
	 * Check the dependencies :
	 *
	 *  - if there isn't any dependency, the JPanel container is enabled
	 *  - if there is, enable or not the JPanel, depending on the name (<code>depName</code>) and the state(<code>depState</code>)
	 *  of the dependencies of this <code>GUITunableHandler</code>
	 *  </pre></p>
	 */
	public void checkDependency(String name, String state) {
		// if we don't depend on anything, then we should be enabled
		if (dependencyName == null || dependencyState == null) {
			setEnabledContainer(true, panel);
			return;
		}

		// if the dependency name matches ...
		if (dependencyName.equals(name)) {
			// ... and the state matches, then enable
			if (dependencyState!=""){
				if (dependencyState.equals(state))
					setEnabledContainer(true, panel);
				else // ... and the state doesn't match, then disable
					setEnabledContainer(false, panel);
			}
			else {
				if (!dependencyUnState.equals(state))
					setEnabledContainer(true, panel);
				else // ... and the state doesn't match, then disable
					setEnabledContainer(false, panel);
			}
		}

		return;
	}

	/**
	 * Set enable or not a container and all the components that are in it
	 *
	 * @param enable if we enable or not the container
	 * @param c the container that will be enabled or not
	 */
	private void setEnabledContainer(boolean enable, Container container) {
		container.setEnabled(enable);
		for ( Component child : container.getComponents() ) {
			if ( child instanceof Container )
				setEnabledContainer(enable,(Container)child);
			else
				child.setEnabled(enable);
		}
	}

	/**
	 * To get the <code>JPanel</code> container
	 * @return the <code>JPanel</code> container of the <code>GUITunableHandler</code>
	 */
	public JPanel getJPanel() {
		return panel;
	}

	public abstract void handle();

	public String getState() {
		try {
			final Object value = getValue();
			return value == null ? "" : value.toString();
		} catch (final Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
