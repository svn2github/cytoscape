package org.cytoscape.view.model.internal;

import org.cytoscape.view.model.BasicVisualLexicon;
import org.cytoscape.view.model.visualproperties.NullVisualProperty;

/**
 * VisualLexicon only with a root node.
 * @author kono
 *
 */
public class MinimalVisualLexicon extends BasicVisualLexicon {

	public MinimalVisualLexicon() {
		super(new NullVisualProperty("ROOT_OF_EVRYTHING", "Root Visual Property"));
	}

}
