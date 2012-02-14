package org.cytoscape.editor.internal;


import org.cytoscape.dnd.DropNetworkViewTaskContext;
import org.cytoscape.dnd.DropNetworkViewTaskFactory;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskIterator;


public class DropNetworkViewTaskFactoryImpl implements DropNetworkViewTaskFactory<DropNetworkViewTaskContext> {
	private final CyEventHelper eh;
	private final VisualMappingManager vmm;
	
	public DropNetworkViewTaskFactoryImpl(CyEventHelper eh, VisualMappingManager vmm) {
		this.eh = eh;
		this.vmm = vmm;
	}

	@Override
	public DropNetworkViewTaskContext createTaskContext() {
		return new DropNetworkViewTaskContext();
	}
	
	public TaskIterator createTaskIterator(DropNetworkViewTaskContext context) {
		return new TaskIterator(new DropNetworkViewTask(vmm, context.getNetworkView(), context.getTransferable(), context.getTransformedPoint(), eh));
	}
}
