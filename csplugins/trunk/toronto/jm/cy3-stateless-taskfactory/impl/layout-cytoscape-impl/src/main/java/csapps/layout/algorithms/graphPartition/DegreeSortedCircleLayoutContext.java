package csapps.layout.algorithms.graphPartition;

import java.util.Set;

import org.cytoscape.view.layout.LayoutContextImpl;
import org.cytoscape.work.Tunable;

public class DegreeSortedCircleLayoutContext extends LayoutContextImpl {

	@Tunable(description="Don't partition graph before layout", groups="Standard settings")
	public boolean singlePartition;

	public DegreeSortedCircleLayoutContext(boolean supportsSelectedOnly,
			Set<Class<?>> supportedNodeAttributeTypes,
			Set<Class<?>> supportedEdgeAttributeTypes) {
		super(supportsSelectedOnly, supportedNodeAttributeTypes,
				supportedEdgeAttributeTypes);
	}

}
