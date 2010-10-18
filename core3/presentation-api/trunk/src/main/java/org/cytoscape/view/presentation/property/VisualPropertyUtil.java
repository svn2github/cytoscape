package org.cytoscape.view.presentation.property;

import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualLexiconNode;
import org.cytoscape.view.model.VisualProperty;

public class VisualPropertyUtil {

	public static boolean isChildOf(final VisualProperty<?> parent, final VisualProperty<?> vp,
			final VisualLexicon lexicon) {

		if (vp == parent || lexicon.getVisualLexiconNode(vp).getParent() == parent)
			return true;

		VisualLexiconNode node = lexicon.getVisualLexiconNode(vp);
		while (node.getParent() != null) {
			node = node.getParent();
			if (node.getVisualProperty() == parent)
				return true;
		}

		return false;
	}
	
	public static String getGraphObjectType(final VisualProperty<?> vp, final VisualLexicon lexicon) {
		if(isChildOf(TwoDVisualLexicon.NODE, vp, lexicon))
			return CyTableEntry.NODE;
		else if(isChildOf(TwoDVisualLexicon.EDGE, vp, lexicon))
			return CyTableEntry.EDGE;
		else if(isChildOf(TwoDVisualLexicon.NETWORK, vp, lexicon))
			return CyTableEntry.NETWORK;
		else
			throw new IllegalStateException("Could not find a category for Visual Property: " + vp.getDisplayName()); 
	}
}
