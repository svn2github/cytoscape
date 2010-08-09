package org.cytoscape.work.internal.tunables;


import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.cytoscape.work.HandlerFactory;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.internal.tunables.utils.CollapsablePanel;
import org.cytoscape.work.internal.tunables.utils.XorPanel;
import org.cytoscape.work.spring.SpringTunableInterceptor;


/**
 * Interceptor of <code>Tunable</code> that will be applied on <code>GUIHandlers</code>.
 *
 * <p><pre>
 * To set the new value to the original objects contained in the <code>GUIHandlers</code>:
 * <ul>
 *   <li>Creates the parent container for the GUI, or use the one that is specified </li>
 *   <li>Creates a GUI with swing components for each intercepted <code>Tunable</code> </li>
 *   <li>
 *     Displays the GUI to the user, following the layout construction rule specified in the <code>Tunable</code>
 *     annotations, and the dependencies to enable or not the graphic components
 *   </li>
 *   <li>
 *     Applies the new <i>value,item,string,state...</i> to the object contained in the <code>GUIHandler</code>,
 *     if the modifications have been validated by the user.
 *   </li>
 * </ul>
 * </pre></p>
 *
 * @author pasteur
 */
public class GuiTunableInterceptor extends SpringTunableInterceptor<GUIHandler> {
	private JPanel parentPanel = null;
	private Map<List<GUIHandler>,JPanel> panelMap;
	private List<GUIHandler> handlers;
	private boolean newValuesSet;
	private Object[] objs;

	/**
	 * Creates an Interceptor that will use the <code>GUIHandlers</code> created in a <code>HandlerFactory</code> from intercepted <code>Tunables</code>.
	 * @param factory
	 */
	public GuiTunableInterceptor(HandlerFactory<GUIHandler> factory) {
		super( factory );
		panelMap = new HashMap<java.util.List<GUIHandler>,JPanel>();
	}

	/**
	 * set the parent JPanel that will contain the GUI
	 * If no parent JPanel is specified, the GUI will be made using a JOptionPane
	 *
	 * @param parent component for the <code>GUIHandlers</code>'s panels
	 */
	public void setParent(Object parent) {
		if (parent instanceof JPanel)
			this.parentPanel = (JPanel)parent;
		else
			throw new IllegalArgumentException("Not a JPanel");
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
	public boolean handle() {
		for (final GUIHandler h : handlers)
			h.handleDependents();
		return validateTunableInput();
	}


	/**
	 * Creates a GUI for the detected <code>Tunables</code>, following the graphic rules specified in <code>Tunable</code>s annotations
	 *
	 * The new values that have been entered for the Object contained in <code>GUIHandlers</code> are also set if the user clicks on <i>"OK"</i>
	 *
	 * @param an Object Array that contains <code>Tunables</code>
	 *
	 * @return if new values has been successfully set
	 */
	public boolean createUI(Object... proxyObjs) {
		this.objs = convertSpringProxyObjs(proxyObjs);
		handlers = new ArrayList<GUIHandler>();
		for (Object o : objs) {
			if (!handlerMap.containsKey(o))
				throw new IllegalArgumentException("No Tunables exist for Object yet!");
			handlers.addAll(handlerMap.get(o).values());
		}

		if (handlers.isEmpty()) {
			if (parentPanel != null) {
				parentPanel.removeAll();
				parentPanel.repaint();
			}
			return true;
		}

		if (!panelMap.containsKey(handlers)) {
			final String MAIN = " ";
			Map<String, JPanel> panels = new HashMap<String, JPanel>();
			panels.put(MAIN, createJPanel(MAIN,null,null,Param.hidden));

			// construct the GUI
			for (GUIHandler gh : handlers) {
				// hook up dependency listeners
				String dep = gh.getDependency();
				if (dep != null && !dep.equals("")) {
					for (GUIHandler gh2 : handlers) {
						if (gh2.getName().equals(dep)) {
							gh2.addDependent(gh);
							break;
						}
					}
				}

				// Get information about the Groups and alignment from Tunables Annotations in order to create the proper GUI
				Map<String,Param> groupAlignment = new HashMap<String,Param>();
				Map<String,Param> groupTitles = new HashMap<String,Param>();

				final String[] group = gh.getGroups();
				final Param[] alignments = gh.getAlignments();
				final Param[] titleFlags = gh.getGroupTitleFlags();

				if (group.length <= alignments.length) {
					for (int i = 0; i < group.length; i++)
						groupAlignment.put(group[i], alignments[i]);
				}
				else {
					for (int i = 0; i < alignments.length; i++)
						groupAlignment.put(group[i], alignments[i]);

					// Default alignment is "vertical."
					for (int i = alignments.length; i < group.length; i++)
						groupAlignment.put(group[i], Param.vertical);
				}

				if (group.length <= titleFlags.length) {
					for (int i = 0; i < group.length; i++)
						groupTitles.put(group[i], titleFlags[i]);
				}
				else {
					for (int i = 0; i < titleFlags.length; i++)
						groupTitles.put(group[i], titleFlags[i]);

					// Default group titleFlags setting is "displayed."
					for (int i = titleFlags.length; i < group.length; i++)
						groupTitles.put(group[i], Param.displayed);
				}

				// find the proper group to put the handler panel in given the Alignment/Group parameters
				String lastGroup = MAIN;
				String groupNames = null;
				for (String g : group) {
					if (g.equals(""))
						throw new IllegalArgumentException("A group's name must not be \"\"!");
					groupNames = groupNames + g;
					if (!panels.containsKey(groupNames)) {
						panels.put(groupNames, createJPanel(g, gh, groupAlignment.get(g), groupTitles.get(g)));
						panels.get(lastGroup).add(panels.get(groupNames), gh.getChildKey());
					}
					lastGroup = groupNames;
				}
				panels.get(lastGroup).add(gh.getJPanel());
			}
			panelMap.put(handlers, panels.get(MAIN));
		}

		// Get the GUI into the proper state
		for (GUIHandler h : handlers)
			h.notifyDependents();

		//if no parentPanel is defined, then create a new JDialog to display the Tunables' panels
		if (parentPanel == null) {
			displayOptionPanel();
			return newValuesSet;
		}
		else { //else add them to the "parentPanel" JPanel
			parentPanel.removeAll();
			parentPanel.add(panelMap.get(handlers));
			parentPanel.repaint();
			parentPanel = null;
			return true;
		}
	}


	/**
	 * Creation of a JPanel that will contain panels of <code>GUIHandler</code>
	 * This panel will have special features like, ability to collapse, ability to be displayed depending on another panel
	 * A layout will be set for this <i>"container"</i> of panels (horizontally or vertically), and a title (displayed or not)
	 *
	 *
	 * @param title of the panel
	 * @param gh provides access to <code>Tunable</code> annotations : see if this panel can be collapsable, and if its content will switch between different <code>GUIHandler</code> panels(depending on <code>xorKey</code>)
	 * @param alignment the way the panels will be set in this <i>"container</i> panel
	 * @param groupTitle parameter to choose whether or not the title of the panel has to be displayed
	 *
	 * @return a container for <code>GUIHandler</code>' panels with special features if it is requested, or a simple one if not
	 */
	private JPanel createJPanel(final String title, final GUIHandler gh, final Param alignment, final Param groupTitle) {
		if (gh == null)
			return getSimplePanel(title,alignment, groupTitle);

		// See if we need to create an XOR panel
		if (gh.controlsMutuallyExclusiveNestedChildren()) {
			JPanel p = new XorPanel(title, gh);
			return p;
		}
		else {
			// Figure out if the collapsable flag is set
			for (Param s : gh.getFlags()) {
				if (s.equals(Param.collapsed))
					return new CollapsablePanel(title, false);
				else if (s.equals(Param.uncollapsed))
					return new CollapsablePanel(title, true);
			}
			// We're not collapsable, so return a normal jpanel
			return getSimplePanel(title,alignment, groupTitle);
		}
	}


	/**
	 * Creation of a JPanel that will contain panels of <code>GUIHandler</code>
	 * A layout will be set for this <i>"container"</i> of panels (horizontally or vertically), and a title (displayed or not)
	 *
	 * @param title of the panel
	 * @param alignment the way the panels will be set in this <i>"container</i> panel
	 * @param groupTitle parameter to choose whether or not the title of the panel has to be displayed
	 *
	 * @return a container for <code>GUIHandler</code>' panels
	 */
	private JPanel getSimplePanel(final String title, final Param alignment, final Param groupTitle) {
		JPanel outPanel = new JPanel();
		TitledBorder titleborder = BorderFactory.createTitledBorder(title);
		titleborder.setTitleColor(Color.BLUE);

		if (groupTitle == Param.displayed || groupTitle == null)
			outPanel.setBorder(titleborder);
		if (alignment == Param.vertical || alignment == null)
			outPanel.setLayout(new BoxLayout(outPanel, BoxLayout.PAGE_AXIS));
		else if (alignment == Param.horizontal)
			outPanel.setLayout(new BoxLayout(outPanel, BoxLayout.LINE_AXIS));
		return outPanel;
	}


	/**
	 * Displays the JPanels of each <code>GUIHandler</code> in a <code>JOptionPane</code>
	 *
	 * Set the new <i>"value"</i> to <code>Tunable</code> object if the user clicked on <i>OK</i>, and if the validate method from <code>TunableValidator</code> interface succeeded
	 */
	private void displayOptionPanel() {
		Object[] buttons = {"OK", "Cancel"};
		int n = JOptionPane.showOptionDialog(parentPanel, panelMap.get(handlers),
		    "Set Parameters",
		    JOptionPane.YES_NO_CANCEL_OPTION,
		    JOptionPane.PLAIN_MESSAGE,
		    null,
		    buttons,
		    buttons[0]);
		if (n == JOptionPane.OK_OPTION) {
			for (final GUIHandler h : handlers)
				h.handleDependents();
			validateTunableInput();
		}
		else
			newValuesSet = false;
	}


	/**
	 * Check if the conditions set in validate method from <code>TunableValidator</code> are met
	 *
	 * If an exception is thrown, or something's wrong, it will be displayed to the user
	 *
	 * @return success(true) or failure(false) for the validation
	 */
	private boolean validateTunableInput() {
		for (Object o : objs) {
			Object[] interfaces = o.getClass().getInterfaces();
			for (Object inter : interfaces) {
				if(inter.equals(TunableValidator.class)) {
					try {
						((TunableValidator)o).validate();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(new JFrame(), e.toString(), "TunableValidator problem", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						if(parentPanel==null)displayOptionPanel();
						return false;
					}
				}
			}
		}
		parentPanel = null;
		newValuesSet = true;
		return true;
	}
}
