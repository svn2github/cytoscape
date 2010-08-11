package org.cytoscape.work.internal.props;


import java.util.Properties;

import org.cytoscape.work.TunableHandler;


public interface PropHandler extends TunableHandler {
	public void setProps(Properties p);
	public Properties getProps();
}
