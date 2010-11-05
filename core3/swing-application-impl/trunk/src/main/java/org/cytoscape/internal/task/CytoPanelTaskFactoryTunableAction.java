/*
 File: CytoPanelTaskFactoryTunableAction.java

 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.internal.task;


import org.cytoscape.Cytoscape;
import org.cytoscape.view.CySwingApplication;
import org.cytoscape.view.CytoscapeAction;
import org.cytoscape.view.CytoPanel;
import org.cytoscape.view.CytoPanelName;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.swing.GUITaskManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  This class is used to provide actions for task factories that have been annotated with tunables and therefore
 *  should end up wrapped in CytoPanel components.
 */
public class CytoPanelTaskFactoryTunableAction extends CytoscapeAction {
	/**
	 *  A listener that upon receiving the button-click event validates the tunables and then
	 *  creates and executes a task.
	 */
	private static class ExecuteButtonListener implements ActionListener {
		final private TaskFactory factory;
		final private GUITaskManager manager;

		ExecuteButtonListener(final TaskFactory factory, final GUITaskManager manager) {
			this.factory = factory;
			this.manager = manager;
		}

		public void actionPerformed(final ActionEvent event) {
			// Perform input validation?
			if (factory instanceof TunableValidator) {
				final Appendable errMsg = new StringBuilder();
				try {
					if (!((TunableValidator)factory).tunablesAreValid(errMsg)) {
						JOptionPane.showMessageDialog(new JFrame(), errMsg.toString(),
									      "Input Validation Problem",
									      JOptionPane.ERROR_MESSAGE);
						return;
					}
				} catch (final Exception e) {
					e.printStackTrace();
					return;
				}
			}

			manager.execute(factory);
		}
	}


	final private static CytoPanelName DEFAULT_CYTOPANEL = CytoPanelName.WEST;
	final private TaskFactory factory;
	final private GUITaskManager manager;
	final private Map serviceProps;
	private CytoPanel cytoPanel;
	final private static Logger logger = LoggerFactory.getLogger(CytoPanelTaskFactoryTunableAction.class);

	public CytoPanelTaskFactoryTunableAction(final TaskFactory factory, 
	                                         final GUITaskManager manager,
	                                         final CySwingApplication app,
	                                         final Map serviceProps, 
	                                         final CyApplicationManager appMgr, 
	                                         final CyNetworkViewManager netViewMgr)
	{
		super(serviceProps, appMgr, netViewMgr);

		this.factory = factory;
		this.manager = manager;
		this.serviceProps = serviceProps;

		if (serviceProps.containsKey("preferredCytoPanel")) {
			try {
				cytoPanel = app.getCytoPanel(CytoPanelName.valueOf(serviceProps.get("preferredCytoPanel").toString()));
			} catch (final Exception e) {
				logger.warn("in CytoPanelTaskFactoryTunableAction constructor: value of serviceProps(\"preferredCytoPanel\") is \""
					    + serviceProps.get("preferredCytoPanel").toString() + "\"");
				cytoPanel = app.getCytoPanel(DEFAULT_CYTOPANEL);
			}
		} else
			this.cytoPanel = app.getCytoPanel(DEFAULT_CYTOPANEL);
	}

	/**
	 *  Creates a new CytoPanel component and adds it to a CytoPanel.
	 */
	public void actionPerformed(final ActionEvent a) {
		final JPanel innerPanel = manager.getConfigurationPanel(factory);
		if (innerPanel == null)
			return;

		cytoPanel.add(getCytoPanelComponentTitle(), createCytoPanelComponent(innerPanel));
	}

	/**
	 *  Attempts to provide a title for our new CytoPanel component.  First we try to get the value of the
	 *  "cytoPanelComponentTitle" from the service properties, if that fails we attempt to generate a title
	 *  from the menu title which can be found in the "title" service property entry.  If all this fails we
	 *  provide a goofy title that is only really useful for debugging.
	 */
	private String getCytoPanelComponentTitle() {
		try {
			final String cytoPanelComponentTitle = (String)serviceProps.get("cytoPanelComponentTitle");
			if (cytoPanelComponentTitle != null)
				return cytoPanelComponentTitle;

			// Try to create a panel component title from the menu item:
			final String menuTitle = (String)serviceProps.get("title");
			if (menuTitle != null) {
				if (menuTitle.endsWith("..."))
					return menuTitle.substring(0, menuTitle.length() - 3);
				else
					return menuTitle;
			}

			return "*No Title*";
		} catch (final ClassCastException e) {
			logger.warn("This should *never* happen!\n" + e.toString());
			return "*Missing Title*";
		}
	}

	/**
	 *  Adds Close/Execute buttons below "innerPanel" in a new enclosing panel.
	 *
	 *  @return the new enclosing panel
	 */
	private JPanel createCytoPanelComponent(final JPanel innerPanel) {
		final JPanel outerPanel = new JPanel();
		outerPanel.add(innerPanel);

		final JButton executeButton = new JButton("Execute");
		executeButton.addActionListener(new ExecuteButtonListener(factory, manager));
		outerPanel.add(executeButton);

		final JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent event) {
					cytoPanel.remove(outerPanel);
				}
			});
		outerPanel.add(closeButton);
		
		return outerPanel;
	}
}
