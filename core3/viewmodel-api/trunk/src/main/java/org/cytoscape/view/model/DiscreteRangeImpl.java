package org.cytoscape.view.model;

import java.util.Set;

public final class DiscreteRangeImpl<T> implements DiscreteRange<T> {

	private final Class<T> type;
	private final Set<T> values;
	
	public DiscreteRangeImpl(final Class<T> type, final Set<T> values) {
		this.type = type;
		this.values = values;
	}
	
	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public boolean isDiscrete() {
		return true;
	}

	@Override
	public Set<T> values() {
		return values;
	}

}
