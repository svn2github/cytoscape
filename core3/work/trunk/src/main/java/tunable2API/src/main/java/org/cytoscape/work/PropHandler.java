package org.cytoscape.work;

import java.util.Properties;

public interface PropHandler extends Handler {

	public void setProps(Properties p);
	public Properties getProps();
	public void add(Properties p);
}
