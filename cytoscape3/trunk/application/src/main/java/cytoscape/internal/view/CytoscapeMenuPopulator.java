/*
 File: CytoscapeMenus.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.internal.view;


import cytoscape.view.CyAction;
import cytoscape.view.CyMenuBar;
import cytoscape.view.CyToolBar;

import org.cytoscape.session.CyNetworkManager;

import cytoscape.internal.task.CytoPanelTaskFactoryTunableAction;
import cytoscape.internal.task.TaskFactoryTunableAction;
import cytoscape.internal.task.NetworkCollectionTaskFactoryTunableAction;
import cytoscape.internal.task.NetworkTaskFactoryTunableAction;
import cytoscape.internal.task.NetworkViewCollectionTaskFactoryTunableAction;
import cytoscape.internal.task.NetworkViewTaskFactoryTunableAction;

import cytoscape.view.CyMenus;
import cytoscape.view.CySwingApplication;

import java.util.Map;
import java.util.HashMap;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.swing.GUITunableInterceptor;

import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkViewCollectionTaskFactory;


/**
 * Creates the menu and tool bars for a Cytoscape window object. It
 * also provides access to individual menus and items.<BR>
 * <p>
 * AddAction takes one more optional argument to specify index. Plugin
 * writers can use this function to specify the location of the menu item.
 * </p>
 */
public class CytoscapeMenuPopulator {
	final private CySwingApplication app;
	final private CyMenus cyMenus;
	final private TaskManager taskManager;
	final private GUITunableInterceptor interceptor;
	final private CyNetworkManager netManager;

	final private Map<TaskFactory, CyAction> taskMap;


	/**
	 * Creates a new CytoscapeMenus object. This will construct the basic bar objects, 
	 * but won't fill them with menu items and associated action listeners.
	 */
	public CytoscapeMenuPopulator(final CySwingApplication app, final TaskManager taskManager,
				      final GUITunableInterceptor interceptor, final CyNetworkManager netManager)
	{
		this.app = app;
		this.cyMenus = app.getCyMenus();
		this.taskManager = taskManager;
		this.interceptor = interceptor;
		this.netManager = netManager;

		taskMap = new HashMap<TaskFactory,CyAction>();
	}

	public void addTaskFactory(TaskFactory factory, Map props) {
		if (interceptor.hasTunables(factory))
			addFactory(new CytoPanelTaskFactoryTunableAction(factory, taskManager, interceptor, app, props, netManager), factory, props);
		else
			addFactory(new TaskFactoryTunableAction<TaskFactory>(taskManager, interceptor, factory, props, netManager), factory, props);
	}

	public void removeTaskFactory(TaskFactory factory, Map props) {
		removeFactory(factory, props);
	}

	public void addNetworkTaskFactory(NetworkTaskFactory factory, Map props) {
		addFactory(new NetworkTaskFactoryTunableAction(taskManager, interceptor, factory, props, netManager), factory, props);
	}

	public void removeNetworkTaskFactory(NetworkTaskFactory factory, Map props) {
		removeFactory(factory, props);
	}

	public void addNetworkViewTaskFactory(NetworkViewTaskFactory factory, Map props) {
		addFactory(new NetworkViewTaskFactoryTunableAction(taskManager, interceptor, factory, props, netManager), factory, props);
	}

	public void removeNetworkViewTaskFactory(NetworkViewTaskFactory factory, Map props) {
		removeFactory(factory, props);
	}

	public void addNetworkViewCollectionTaskFactory(NetworkViewCollectionTaskFactory factory, Map props) {
		addFactory(new NetworkViewCollectionTaskFactoryTunableAction(taskManager, interceptor, factory, props, netManager), factory, props);
	}

	public void removeNetworkViewCollectionTaskFactory(NetworkViewCollectionTaskFactory factory, Map props) {
		removeFactory(factory, props);
	}
	
	public void addNetworkCollectionTaskFactory(NetworkCollectionTaskFactory factory, Map props) {
		addFactory(new NetworkCollectionTaskFactoryTunableAction(taskManager, interceptor, factory, props, netManager), factory, props);
	}

	public void removeNetworkCollectionTaskFactory(NetworkCollectionTaskFactory factory, Map props) {
		removeFactory(factory, props);
	}
	
	private <F extends TaskFactory> void addFactory(CyAction action, F factory, Map props) {
		taskMap.put(factory,action);
		cyMenus.addAction(action);
	}

	private void removeFactory(TaskFactory factory, Map props) {
		final CyAction action = taskMap.remove(factory);
		if (action != null) {
			if (action.isInMenuBar())
				cyMenus.getMenuBar().removeAction(action);

			if (action.isInToolBar())
				cyMenus.getToolBar().removeAction(action);
		}
	}
}
