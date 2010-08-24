package org.cytoscape.work.internal.tunables;


import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.cytoscape.work.HandlerFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.swing.AbstractGUITunableInterceptor;
import org.cytoscape.work.swing.GUITunableHandler;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.internal.tunables.utils.CollapsablePanel;
import org.cytoscape.work.internal.tunables.utils.XorPanel;

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
public class GUITunableInterceptorImpl extends AbstractGUITunableInterceptor {
	private Map<List<GUITunableHandler>, JPanel> panelMap;
	private boolean newValuesSet;

	/**
	 * Creates an Interceptor that will use the <code>GUITunableHandlers</code> created in a <code>HandlerFactory</code> from intercepted <code>Tunables</code>.
	 * @param factory
	 */
	public GUITunableInterceptorImpl(final HandlerFactory<GUITunableHandler> factory) {
		super(factory);
		panelMap = new HashMap<List<GUITunableHandler>, JPanel>();
	}

	@Override
	protected JPanel constructUI() {
		JPanel providedGUI = null;
		int factoryCount = 0; // # of descendents of TaskFactory...
		int otherCount = 0;   // ...everything else.  (Presumeably descendents of Task.)
		if (handlers == null)
			handlers = new ArrayList<GUITunableHandler>();
		for (final Object objectWithTunables : objectsWithTunables) {
			if (objectWithTunables instanceof TaskFactory)
				++factoryCount;
			else
				++otherCount;

			if (guiProviderMap.containsKey(objectWithTunables)) {
				if (providedGUI != null)
					throw new IllegalStateException("Found more than one provided GUI!");
				try {
					providedGUI = (JPanel)guiProviderMap.get(objectWithTunables).invoke(objectWithTunables);
				} catch (final Exception e) {
					logger.error("Can't retrieve @ProvidesGUI JPanel: " + e);
					return null;
				}
			} else if (handlerMap.containsKey(objectWithTunables))
				handlers.addAll(handlerMap.get(objectWithTunables).values());
			else
				throw new IllegalArgumentException("No Tunables and no provided GUI exists for Object yet!");
		}

		// Sanity check:
		if (factoryCount > 0) {
			if (factoryCount != 1) {
				logger.error("More than one annotated TaskFactory found!");
				return null;
			} else if (otherCount != 0) {
				logger.error("Found annotated Task objects in addition to an annotated TaskFactory!");
				return null;
			}
		}

		if (providedGUI != null) {
			//if no parentPanel is defined, then create a new JDialog to display the Tunables' panels
			if (parentPanel == null)
				return providedGUI;
			else { // add them to the "parentPanel" JPanel
				parentPanel.removeAll();
				parentPanel.add(providedGUI);
				final JPanel retVal = parentPanel;
				parentPanel = null;
				return retVal;
			}
		}

		if (handlers.isEmpty()) {
			if (parentPanel != null) {
				parentPanel.removeAll();
				return parentPanel;
			}
			return null;
		}

		if (!panelMap.containsKey(handlers)) {
			final String MAIN = " ";
			Map<String, JPanel> panels = new HashMap<String, JPanel>();
			final JPanel topLevel = createSimplePanel(MAIN, null, Param.hidden);
			panels.put(MAIN, topLevel);

			// construct the GUI
			for (GUITunableHandler gh : handlers) {
				// hook up dependency listeners
				String dep = gh.getDependency();
				if (dep != null && !dep.equals("")) {
					for (GUITunableHandler gh2 : handlers) {
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
				} else {
					for (int i = 0; i < alignments.length; i++)
						groupAlignment.put(group[i], alignments[i]);

					// Default alignment is "vertical."
					for (int i = alignments.length; i < group.length; i++)
						groupAlignment.put(group[i], Param.vertical);
				}

				if (group.length <= titleFlags.length) {
					for (int i = 0; i < group.length; i++)
						groupTitles.put(group[i], titleFlags[i]);
				} else {
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
		for (GUITunableHandler h : handlers)
			h.notifyDependents();

		//if no parentPanel is defined, then create a new JDialog to display the Tunables' panels
		if (parentPanel == null)
			return panelMap.get(handlers);
		else { // add them to the "parentPanel" JPanel
			parentPanel.removeAll();
			parentPanel.add(panelMap.get(handlers));
			final JPanel retVal = parentPanel;
			parentPanel = null;
			return retVal;
		}
	}

	/**
	 * Creation of a JPanel that will contain panels of <code>GUITunableHandler</code>
	 * This panel will have special features like, ability to collapse, ability to be displayed depending on another panel
	 * A layout will be set for this <i>"container"</i> of panels (horizontally or vertically), and a title (displayed or not)
	 *
	 *
	 * @param title of the panel
	 * @param gh provides access to <code>Tunable</code> annotations : see if this panel can be collapsable, and if its content will switch between different <code>GUITunableHandler</code> panels(depending on <code>xorKey</code>)
	 * @param alignment the way the panels will be set in this <i>"container</i> panel
	 * @param groupTitle parameter to choose whether or not the title of the panel has to be displayed
	 *
	 * @return a container for <code>GUITunableHandler</code>' panels with special features if it is requested, or a simple one if not
	 */
	private JPanel createJPanel(final String title, final GUITunableHandler gh, final Param alignment, final Param groupTitle) {
		if (gh == null)
			return createSimplePanel(title, alignment, groupTitle);

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
			return createSimplePanel(title,alignment, groupTitle);
		}
	}

	/**
	 * Creation of a JPanel that will contain panels of <code>GUITunableHandler</code>
	 * A layout will be set for this <i>"container"</i> of panels (horizontally or vertically), and a title (displayed or not)
	 *
	 * @param title of the panel
	 * @param alignment the way the panels will be set in this <i>"container</i> panel
	 * @param groupTitle parameter to choose whether or not the title of the panel has to be displayed
	 *
	 * @return a container for <code>GUITunableHandler</code>' panels
	 */
	private JPanel createSimplePanel(final String title, final Param alignment, final Param groupTitle) {
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
	 * Creation of a JDialog that will contain panels of <code>GUITunableHandler</code>
	 * A layout will be set for this <i>"container"</i> of panels (horizontally or vertically), and a title (displayed or not)
	 *
	 * @param title of the panel
	 * @param alignment the way the panels will be set in this <i>"container</i> panel
	 * @param groupTitle parameter to choose whether or not the title of the panel has to be displayed
	 *
	 * @return a container for <code>GUITunableHandler</code>' panels
	 */
	private JDialog createSimpleDialog(final String title, final Param alignment, final Param groupTitle) {
		final JDialog dialog = new JDialog((JDialog)null, title);

		if (groupTitle == Param.displayed || groupTitle == null)
			/* Do nothing. */;
		if (alignment == Param.vertical || alignment == null)
			dialog.setLayout(new BoxLayout(dialog, BoxLayout.PAGE_AXIS));
		else if (alignment == Param.horizontal)
			dialog.setLayout(new BoxLayout(dialog, BoxLayout.LINE_AXIS));
		return dialog;
	}

	/**
	 * Check if the conditions set in validate method from <code>TunableValidator</code> are met
	 *
	 * If an exception is thrown, or something's wrong, it will be displayed to the user
	 *
	 * @return success(true) or failure(false) for the validation
	 */
	protected boolean validateTunableInput() {
		for (final Object objectWithTunables : objectsWithTunables) {
			if (!(objectWithTunables instanceof TunableValidator))
				continue;

			final Appendable errMsg = new StringBuilder();
			try {
				if (!((TunableValidator)objectWithTunables).tunablesAreValid(errMsg)) {
					JOptionPane.showMessageDialog(new JFrame(), errMsg.toString(),
					                              "Input Validation Problem",
					                              JOptionPane.ERROR_MESSAGE);
					if (parentPanel == null)
						displayGUI(panelMap.get(handlers));
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		parentPanel = null;
		newValuesSet = true;
		return true;
	}
}
