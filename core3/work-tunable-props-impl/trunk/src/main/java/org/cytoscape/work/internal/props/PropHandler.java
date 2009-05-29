package org.cytoscape.work.internal.props;

import java.util.Properties;

import org.cytoscape.work.Handler;

public interface PropHandler extends Handler {
	public void setProps(Properties p);
	public Properties getProps();
}
