package org.cytoscape.ontology;

import java.util.Set;

/**
 * Alias: Utility object for graph objects.
 * 
 * @author kono
 *
 */
public interface Alias {
	
	public Set<String> getAliasSet();
	
	public void add(String alias);
	public void add(Set<String> aliases);
	
	public void remove(String alias);
	
	public String getKey();

}
