package org.cytoscape.app.internal.net;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is capable of filtering a set of {@link WebApp} objects to create a subset
 * containing only elements that match a given search.
 */
public class ResultsFilterer {
	
	public Set<WebApp> findMatches(String text, Set<WebApp> webApps) {
		Set<WebApp> result = new HashSet<WebApp>();
		
		for (WebApp webApp : webApps) {
			if (matches(text, webApp)) {
				result.add(webApp);
			}
		}
		
		return result;
	}
	
	// Return true if the app matches the filter text
	public boolean matches(String text, WebApp webApp) {
		String lowerCaseText = text.toLowerCase();
		
		if (webApp.getFullName().toLowerCase().indexOf(lowerCaseText) != -1) {
			return true;
		} else if (webApp.getDescription().toLowerCase().indexOf(lowerCaseText) != -1) {
			return true;
		} 
		
		return false;
	}
}
