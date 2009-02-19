package org.cytoscape.work;

import java.util.Properties;

import org.cytoscape.work.Handler;

public interface PropHandler extends Handler {

	public void setProps(Properties p);
	public Properties getProps();
	public void add(Properties p);
}
