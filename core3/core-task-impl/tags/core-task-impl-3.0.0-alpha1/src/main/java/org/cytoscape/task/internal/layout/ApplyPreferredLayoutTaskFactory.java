package org.cytoscape.task.internal.layout;


import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.property.CyProperty;
import org.cytoscape.view.layout.CyLayouts;
import java.util.Properties; 

public class ApplyPreferredLayoutTaskFactory extends AbstractNetworkViewTaskFactory {

	private final CyLayouts layouts;
	private final Properties props;
	
	public ApplyPreferredLayoutTaskFactory(CyLayouts layouts, CyProperty<Properties> p) {
		this.layouts = layouts;
		this.props = p.getProperties();
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ApplyPreferredLayoutTask(view, layouts, props));
	}
}
