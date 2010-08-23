package org.cytoscape.work.swing;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.cytoscape.work.HandlerFactory;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.spring.SpringTunableInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Interceptor of <code>Tunable</code> that will be applied on <code>GUITunableHandlers</code>.
 *
 * <p><pre>
 * To set the new value to the original objects contained in the <code>GUITunableHandlers</code>:
 * <ul>
 *   <li>Creates the parent container for the GUI, or use the one that is specified </li>
 *   <li>Creates a GUI with swing components for each intercepted <code>Tunable</code> </li>
 *   <li>
 *     Displays the GUI to the user, following the layout construction rule specified in the <code>Tunable</code>
 *     annotations, and the dependencies to enable or not the graphic components
 *   </li>
 *   <li>
 *     Applies the new <i>value,item,string,state...</i> to the object contained in the <code>GUITunableHandler</code>,
 *     if the modifications have been validated by the user.
 *   </li>
 * </ul>
 * </pre></p>
 *
 * @author pasteur
 */
public abstract class AbstractGUITunableInterceptor extends SpringTunableInterceptor<GUITunableHandler> implements GUITunableInterceptor<GUITunableHandler> {
	protected JPanel parentPanel = null;
	protected List<GUITunableHandler> handlers;
	protected Object[] objectsWithTunables;
	protected Logger logger;

	/**
	 * Creates an Interceptor that will use the <code>GUITunableHandlers</code> created in a <code>HandlerFactory</code> from intercepted <code>Tunables</code>.
	 * @param factory
	 */
	public AbstractGUITunableInterceptor(final HandlerFactory<GUITunableHandler> factory) {
		super(factory);
		logger = LoggerFactory.getLogger(getClass());
	}

	/**
	 * set the parent JPanel that will contain the GUI
	 * If no parent JPanel is specified, the GUI will be made using a JOptionPane
	 *
	 * @param parent component for the <code>GUITunableHandlers</code>'s panels
	 */
	final public void setParent(final Object parent) {
		if (!(parent instanceof JPanel))
			logger.error("\"parent\" tunable interceptor must be a JPanel!");
		else
			this.parentPanel = (JPanel)parent;
	}

	//get the value(Handle) of the Tunable if its JPanel is enabled(Dependency) and check if we have to validate the values of tunables
	/**
	 * get the <i>value,item,string,state...</i> from the GUI component, and check with the dependencies, if it can be set as the new one
	 *
	 * <p><pre>
	 * If the <code>TunableValidator</code> interface is implemented by the class that contains the <code>Tunables</code> :
	 * <ul>
	 * <li>a validate method has to be applied</li>
	 * <li>it checks the conditions that have been declared about the chosen <code>Tunable(s)</code> </li>
	 * <li>if validation fails, it displays an error to the user, and new values are not set</li>
	 * </ul>
	 * </pre></p>
	 * @return success or not of the <code>TunableValidator</code> validate method
	 */
	final public boolean handle() {
		for (final GUITunableHandler h : handlers)
			h.handleDependents();
		return validateTunableInput();
	}

	final public JPanel getUI(final Object... proxyObjs) {
		this.objectsWithTunables = convertSpringProxyObjs(proxyObjs);
		handlers = new ArrayList<GUITunableHandler>();
		return constructUI();
	}

	abstract protected JPanel constructUI();

	/**
	 * Creates a GUI for the detected <code>Tunables</code>, following the graphic rules specified in <code>Tunable</code>s annotations
	 * or uses the JPanel provided by the method annotated with <code>@ProvidesGUI</code>
	 *
	 * The new values that have been entered for the Object contained in <code>GUITunableHandlers</code> are also set if the user clicks on <i>"OK"</i>
	 *
	 * @param an array of objects with <code>Tunables</code>s
	 *
	 * @return if new values has been successfully set
	 */
	final public boolean execUI(Object... proxyObjs) {
		final JPanel panel = getUI(proxyObjs);
		if (panel == null)
			return false;

		return displayGUI(panel);
	}

	final public boolean hasTunables(final Object o) {
		return super.hasTunables(convertSpringProxyObj(o));
	}

	protected boolean displayGUI(final JPanel optionPanel) {
		Object[] buttons = {"OK", "Cancel"};
		int result = JOptionPane.showOptionDialog(parentPanel, optionPanel,
							  "Set Parameters",
							  JOptionPane.YES_NO_CANCEL_OPTION,
							  JOptionPane.PLAIN_MESSAGE,
							  null,
							  buttons,
							  buttons[0]);
		if (result == JOptionPane.OK_OPTION) {
			for (final GUITunableHandler h : handlers)
				h.handleDependents();
			return validateTunableInput();
		} else
			return false;
	}

	/**
	 * Check if the conditions set in validate method from <code>TunableValidator</code> are met
	 *
	 * If an exception is thrown, or something's wrong, it will be displayed to the user
	 *
	 * @return success(true) or failure(false) for the validation
	 */
	abstract protected boolean validateTunableInput();
}
