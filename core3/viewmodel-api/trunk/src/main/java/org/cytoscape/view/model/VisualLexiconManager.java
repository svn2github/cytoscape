package org.cytoscape.view.model;

import java.util.Map;

/**
 * Collection of Lexicons. This is the superset of VisualLexicons. All Visual
 * Properties will be available from this root object.
 * 
 */
public interface VisualLexiconManager {

	/**
	 * OSGi service listener method. Of course, this can be called from any POJO
	 * (without OSGi).
	 * 
	 * @param lexicon
	 * @param props
	 */

	@SuppressWarnings("unchecked")
	// This for Spring DM.
	public void addVisualLexicon(VisualLexicon lexicon, Map props);

	@SuppressWarnings("unchecked")
	public void removeVisualLexicon(VisualLexicon lexicon, Map props);
	
	public VisualLexicon getLexicon();
	
}
