package csapps.layout.algorithms.graphPartition;

import java.util.Set;

import org.cytoscape.view.layout.LayoutContextImpl;
import org.cytoscape.work.Tunable;

public class ISOMLayoutContext extends LayoutContextImpl {
	@Tunable(description="Number of iterations")
	public int maxEpoch = 5000;
	@Tunable(description="Radius constant")
	public int radiusConstantTime = 100;
	@Tunable(description="Radius")
	public int radius = 20;
	@Tunable(description="Minimum radius")
	public int minRadius = 1;
	@Tunable(description="Initial adaptation")
	public double initialAdaptation = 90.0D / 100.0D;
	@Tunable(description="Minimum adaptation value")
	public double minAdaptation = 0;
	@Tunable(description="Size factor")
	public double sizeFactor = 100;
	@Tunable(description="Cooling factor")
	public double coolingFactor = 2;
	@Tunable(description="Don't partition graph before layout", groups="Standard settings")
	public boolean singlePartition;

	public ISOMLayoutContext(boolean supportsSelectedOnly,
			Set<Class<?>> supportedNodeAttributeTypes,
			Set<Class<?>> supportedEdgeAttributeTypes) {
		super(supportsSelectedOnly, supportedNodeAttributeTypes,
				supportedEdgeAttributeTypes);
	}

}
