package csapps.layout.algorithms.hierarchicalLayout;

import java.util.Set;

import org.cytoscape.view.layout.LayoutContextImpl;
import org.cytoscape.work.Tunable;

public class HierarchicalLayoutContext extends LayoutContextImpl {
	@Tunable(description="Horizontal spacing between nodes")
	public int nodeHorizontalSpacing = 64;
	@Tunable(description="Vertical spacing between nodes")
	public int nodeVerticalSpacing = 32;
	@Tunable(description="Component spacing")
	public int componentSpacing = 64;
	@Tunable(description="Band gap")
	public int bandGap = 64;
	@Tunable(description="Left edge margin")
	public int leftEdge = 32;
	@Tunable(description="Top edge margin")
	public int topEdge = 32;
	@Tunable(description="Right edge margin")
	public int rightMargin = 7000;
	@Tunable(description="layout selected nodes only")
	public boolean selected_only = false;

	public HierarchicalLayoutContext(boolean supportsSelectedOnly,
			Set<Class<?>> supportedNodeAttributeTypes,
			Set<Class<?>> supportedEdgeAttributeTypes) {
		super(supportsSelectedOnly, supportedNodeAttributeTypes,
				supportedEdgeAttributeTypes);
	}

}
