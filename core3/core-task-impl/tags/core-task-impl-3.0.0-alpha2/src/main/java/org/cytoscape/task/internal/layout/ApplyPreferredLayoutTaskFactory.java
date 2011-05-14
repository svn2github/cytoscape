package org.cytoscape.task.internal.layout;


import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.property.CyProperty;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import java.util.Properties; 

public class ApplyPreferredLayoutTaskFactory extends AbstractNetworkViewTaskFactory {

	private final CyLayoutAlgorithmManager layouts;
	private final Properties props;
	
	public ApplyPreferredLayoutTaskFactory(CyLayoutAlgorithmManager layouts, CyProperty<Properties> p) {
		this.layouts = layouts;
		this.props = p.getProperties();
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ApplyPreferredLayoutTask(view, layouts, props));
	}
}
