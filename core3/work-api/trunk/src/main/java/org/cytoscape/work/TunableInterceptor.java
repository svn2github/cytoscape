package org.cytoscape.work;

import java.util.Map;


public interface TunableInterceptor<T extends Handler> {
	
	public void loadTunables(Object o);
	public Map<String,T> getHandlers(Object o);
	public boolean createUI(Object ... obs );
}
