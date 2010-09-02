package org.cytoscape.view.model.internal;

import org.cytoscape.view.model.VisualPropertyDependecyCalculator;

public class SizeConverter implements VisualPropertyDependecyCalculator<Number> {

	private Float ratio;

	public SizeConverter() {
		this.ratio = 1.0f;
	}

	public void setRatio(final Float ratio) {
		this.ratio = ratio;
	}

	@Override
	public Number convert(Number value) {
		return ratio*value.floatValue();
	}

}
