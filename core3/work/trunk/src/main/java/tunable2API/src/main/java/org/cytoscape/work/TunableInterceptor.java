package org.cytoscape.work;

import java.util.Map;


public interface TunableInterceptor<T extends Handler> {
	
	public void processProperties(Object o);
	public void interceptAndReinitializeObjects(Object o);
	public void interceptandDisplayResults(Object obj);
	
	
	//added
	public void loadTunables(Object o);
	public Map<String,T> getHandlers(Object o);
	public int createUI(Object ... obs );
	public void createProperties(Object ...obs);
}