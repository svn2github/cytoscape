package csapps.layout.algorithms.circularLayout;

import java.util.Set;

import org.cytoscape.view.layout.LayoutContextImpl;
import org.cytoscape.work.Tunable;

public class CircularLayoutContext extends LayoutContextImpl {
	
	//TODO: these are not used in current implementations.
	
	@Tunable(description="Horizontal spacing between nodes")
	public int nodeHorizontalSpacing = 64;
	@Tunable(description="Vertical spacing between nodes")
	public int nodeVerticalSpacing = 32;
	@Tunable(description="Left edge margin")
	public int leftEdge = 32;
	@Tunable(description="Top edge margin")
	public int topEdge = 32;
	@Tunable(description="Right edge margin")
	public int rightMargin = 1000;
        @Tunable(description="Don't partition graph before layout", groups="Standard settings")
	public boolean singlePartition;
	
	public CircularLayoutContext(boolean supportsSelectedOnly,
			Set<Class<?>> supportedNodeAttributeTypes,
			Set<Class<?>> supportedEdgeAttributeTypes) {
		super(supportsSelectedOnly, supportedNodeAttributeTypes,
				supportedEdgeAttributeTypes);
	}
}
