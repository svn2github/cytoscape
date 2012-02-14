package org.cytoscape.io.internal.read.properties;

import java.util.Properties;

import org.cytoscape.io.internal.read.AbstractPropertyReader;
import org.cytoscape.io.read.InputStreamTaskContext;
import org.cytoscape.work.TaskMonitor;

public class PropertiesReader extends AbstractPropertyReader {

	public PropertiesReader(InputStreamTaskContext context) {
		super(context.getInputStream());
	}

	public void run(TaskMonitor tm) throws Exception {
		tm.setProgress(0.0);
		Properties props = new Properties();
		tm.setProgress(0.1);
		props.load(inputStream);
		propertyObject = props; 
		tm.setProgress(1.0);
	}
}
