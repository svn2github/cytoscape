package org.cytoscape.view.model.internal;

import java.util.Map;

import org.cytoscape.view.model.VisualLexiconManager;
import org.cytoscape.view.model.VisualLexicon;

public class VisualLexiconManagerImpl implements VisualLexiconManager {

	// There is only one lexicon.
	private final VisualLexicon lexicon;

	/**
	 * Constructor. Just initializes collections for currently available
	 * renderers and VPs
	 */
	public VisualLexiconManagerImpl(final VisualLexicon lexicon) {
		if(lexicon == null)
			throw new NullPointerException("Visual Lexicon cannot be null.");
		
		this.lexicon = lexicon;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public void addVisualLexicon(VisualLexicon newLexicon, Map props) {
		final String presentationName = lexicon.toString();

		lexicon.mergeLexicon(newLexicon);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void removeVisualLexicon(VisualLexicon lexicon, Map props) {
		final String presentationName = lexicon.toString();
		
		// FIXME: need to remove actual props!
	}


	@Override
	public VisualLexicon getLexicon() {
		return lexicon;
	}

}
