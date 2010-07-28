package org.cytoscape.webservice.psicquic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class ServiceState {
	private final SortedSet<String> services;
	private final Map<String, Integer> lastCounts;
	
	private final Map<String, String> uri2name;
	
	
	public ServiceState(PSICQUICServiceRegistory reg) {
		services = new TreeSet<String>(RegistryManager.getManager().getActiveServices().keySet());
		services.addAll(RegistryManager.getManager().getInactiveServices().keySet());
		
		uri2name = new HashMap<String, String>();
		for(String serviceName: RegistryManager.getManager().getActiveServices().keySet()) {
			uri2name.put(RegistryManager.getManager().getActiveServices().get(serviceName), serviceName);
		}
		
		lastCounts = new HashMap<String, Integer>();
		
	}
	
	public boolean isActive(final String serviceName) {
		if(RegistryManager.getManager().getInactiveServices().keySet().contains(serviceName))
			return false;
		else
			return true;
		
	}
	
	public Collection<String> getServiceNames() {
		return services;
	}
	
	public int getRecentResultCount(final String serviceName) {
		Integer count = lastCounts.get(serviceName);
		
		if(count != null)
			return count;
		else
			return 0;
	}
	
	public void setRecentResultCount(final String serviceName, Integer count) {
		lastCounts.put(serviceName, count);
	}
	
	public String getName(String uriString) {
		return this.uri2name.get(uriString);
	}

	
	
}
