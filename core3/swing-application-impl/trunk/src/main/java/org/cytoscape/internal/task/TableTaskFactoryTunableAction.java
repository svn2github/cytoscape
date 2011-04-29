package org.cytoscape.internal.task;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.task.DataTableTaskFactory;
import org.cytoscape.work.TaskManager;

public class TableTaskFactoryTunableAction extends TaskFactoryTunableAction<DataTableTaskFactory>{
	
	public TableTaskFactoryTunableAction(
			TaskManager manager,
			DataTableTaskFactory factory, @SuppressWarnings("rawtypes") Map serviceProps,
			final CyApplicationManager applicationManager) {
		super(manager, factory, serviceProps, applicationManager);
	}

	public void actionPerformed(ActionEvent a) {
		factory.setDataTable(applicationManager.getCurrentTable());
		super.actionPerformed(a);
	}

}
