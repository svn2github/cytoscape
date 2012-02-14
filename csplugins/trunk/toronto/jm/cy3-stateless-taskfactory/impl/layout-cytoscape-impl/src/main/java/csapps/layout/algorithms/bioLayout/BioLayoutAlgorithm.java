package csapps.layout.algorithms.bioLayout;

import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.work.undo.UndoSupport;

public abstract class BioLayoutAlgorithm<C extends BioLayoutContext> extends AbstractLayoutAlgorithm<C> {
	
	public BioLayoutAlgorithm(UndoSupport undo, String computerName,
			String humanName, boolean supportsSelectedOnly) {
		super(undo, computerName, humanName, supportsSelectedOnly);
	}
}
