package org.cytoscape.view.presentation.property;

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
}
