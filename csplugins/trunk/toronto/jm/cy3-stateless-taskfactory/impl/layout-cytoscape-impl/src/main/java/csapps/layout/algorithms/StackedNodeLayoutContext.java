package csapps.layout.algorithms;

import java.util.Set;

import org.cytoscape.view.layout.LayoutContextImpl;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class StackedNodeLayoutContext extends LayoutContextImpl implements TunableValidator {
	public StackedNodeLayoutContext(boolean supportsSelectedOnly,
			Set<Class<?>> supportedNodeAttributeTypes,
			Set<Class<?>> supportedEdgeAttributeTypes) {
		super(supportsSelectedOnly, supportedNodeAttributeTypes,
				supportedEdgeAttributeTypes);
	}

	@Tunable(description="x_position")
	public double x_position = 10.0;

	@Tunable(description="y_start_position")
	public double y_start_position = 10.0;

	//@Tunable(description="nodes")
	//public Collection nodes;

	@Override // TODO
	public ValidationState getValidationState(final Appendable errMsg) {
		return ValidationState.OK;
	}

	
}
