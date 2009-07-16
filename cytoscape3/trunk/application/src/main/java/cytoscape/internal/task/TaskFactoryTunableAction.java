/*
 File: TaskFactoryTunableAction.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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

package cytoscape.internal.task;

import java.awt.event.ActionEvent;

import java.util.Map;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TunableInterceptor;
import org.cytoscape.work.TaskManager;
//import org.cytoscape.work.HandlerController;

import cytoscape.util.CytoscapeAction;
import org.cytoscape.session.CyNetworkManager;

public class TaskFactoryTunableAction<T extends TaskFactory> extends CytoscapeAction {

	protected T factory;
	protected TunableInterceptor interceptor;
	protected TaskManager manager;

	public TaskFactoryTunableAction(TaskManager manager, TunableInterceptor interceptor, 
	                  T factory, Map serviceProps,
					  CyNetworkManager netmgr) {
		super(serviceProps,netmgr);
		this.manager = manager;
		this.factory = factory;
		this.interceptor = interceptor;
	}

	public void actionPerformed(ActionEvent a) {
		Task task = factory.getTask();

		// load the tunables from the object 
		interceptor.loadTunables(task);

		// if the object implements the interface,
		// give the object access to the handlers 
		// created for the tunables
	//	if ( task instanceof HandlerController )
	//		((HandlerController)task).controlHandlers(interceptor.getHandlers(task));
		
		// create the UI based on the object
			if ( !interceptor.createUI(task) )
				return;
		
		// execute the task in a separate thread
		manager.execute(task);
	}
}
