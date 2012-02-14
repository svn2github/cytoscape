package org.cytoscape.ding.impl.cyannotator.tasks; 


import org.cytoscape.ding.impl.cyannotator.create.AnnotationFactory;
import org.cytoscape.dnd.DropNetworkViewTaskContext;
import org.cytoscape.dnd.DropNetworkViewTaskFactory;
import org.cytoscape.work.TaskIterator;


public class DropAnnotationTaskFactory implements DropNetworkViewTaskFactory<DropNetworkViewTaskContext> {
	private final BasicGraphicalEntity bge; 
	private final AnnotationFactory annotationFactory;
	
	public DropAnnotationTaskFactory(BasicGraphicalEntity bge, AnnotationFactory annotationFactory) {
		this.bge = bge;
		this.annotationFactory = annotationFactory;
	}

	public TaskIterator createTaskIterator(DropNetworkViewTaskContext context) {
		return new TaskIterator(new DropAnnotationTask(context, bge, annotationFactory));
	}
	
	@Override
	public DropNetworkViewTaskContext createTaskContext() {
		return new DropNetworkViewTaskContext();
	}
}
