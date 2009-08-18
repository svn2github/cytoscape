package org.cytoscape.task.internal.loaddatatable;

import java.util.Properties;

import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;

public class LoadDataTableTaskFactoryImpl implements TaskFactory {

	private CyReaderManager mgr;
	private Properties props;
	
	public LoadDataTableTaskFactoryImpl(CyReaderManager mgr,
			CyProperty<Properties> cyProp) {
		this.mgr = mgr;
		this.props = cyProp.getProperties();
	}

	public Task getTask() {
		return new LoadDataTableTask(mgr, props);
	}
}