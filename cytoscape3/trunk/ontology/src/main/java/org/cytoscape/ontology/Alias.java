package org.cytoscape.ontology;

import java.util.Set;

/**
 * Alias: Utility for ID mapping for all graph objects.
 * 
 * TODO: Is ontology a right bundle to put this class?
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
