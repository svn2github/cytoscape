package csapps.layout.algorithms.bioLayout;

import java.util.Set;

import org.cytoscape.view.layout.LayoutContextImpl;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class BioLayoutContext extends LayoutContextImpl implements TunableValidator {

	/**
	 * Whether or not to initialize by randomizing all points
	 */
	@Tunable(description="Randomize graph before layout", groups="Standard settings")
	public boolean randomize = true;

	public BioLayoutContext(boolean supportsSelectedOnly,
			Set<Class<?>> supportedNodeAttributeTypes,
			Set<Class<?>> supportedEdgeAttributeTypes) {
		super(supportsSelectedOnly, supportedNodeAttributeTypes,
				supportedEdgeAttributeTypes);
	}
	
	@Override
	public ValidationState getValidationState(Appendable errMsg) {
		return ValidationState.OK;
	}
}
