package org.cytoscape.internal.task;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.task.TableTaskContext;
import org.cytoscape.task.TableTaskFactory;
import org.cytoscape.work.TaskContextManager;
import org.cytoscape.work.swing.DialogTaskManager;

public class TableTaskFactoryTunableAction extends TaskFactoryTunableAction<TableTaskFactory<?>>{
	
	public TableTaskFactoryTunableAction(
			DialogTaskManager manager,
			TableTaskFactory<?> factory, @SuppressWarnings("rawtypes") Map serviceProps,
			final CyApplicationManager applicationManager, TaskContextManager contextManager) {
		super(manager, factory, serviceProps, applicationManager, contextManager);
	}

	public void actionPerformed(ActionEvent a) {
		TableTaskContext context = contextManager.getContext(factory);
		if (context == null) {
			contextManager.registerTaskFactory(factory);
		}

		context.setTable(applicationManager.getCurrentTable());
		super.actionPerformed(a);
	}

}
