package org.cytoscape.ontology;

import java.util.Set;

public interface Alias {
	
	public Set<String> getAliases(String key);
	
	public void add(String key, String alias);
	public void add(String key, Set<String> aliases);
	
	public String getKey(String alias);

}
