package org.cytoscape.view.model.internal;

import org.cytoscape.view.model.VisualLexiconNode;
import org.cytoscape.view.model.VisualLexiconNodeFactory;
import org.cytoscape.view.model.VisualProperty;

public class VisualLexiconNodeFactoryImpl implements VisualLexiconNodeFactory {

	@Override
	public VisualLexiconNode createNode(VisualProperty<?> vp,
			VisualLexiconNode parent) {
		return new VisualLexiconNodeImpl(vp, parent);
	}

}
