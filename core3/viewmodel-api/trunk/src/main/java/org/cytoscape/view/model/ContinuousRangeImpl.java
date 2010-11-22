package org.cytoscape.view.model;

public final class ContinuousRangeImpl<T> implements ContinuousRange<T> {

	private final Class<T> type;
	
	private final T min;
	private final T max;
	
	private final Boolean includeMin;
	private final Boolean includeMax;
	
	public ContinuousRangeImpl(final Class<T> type, final T min, final T max) {
		this(type, min, max, true, true);
	}
	
	public ContinuousRangeImpl(final Class<T> type, final T min, final T max, final Boolean includeMin, final Boolean includeMax) {
		this.type = type;
		this.min = min;
		this.max = max;
		this.includeMin = includeMin;
		this.includeMax = includeMax;
	}
	
	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public boolean isDiscrete() {
		return false;
	}

	@Override
	public T getMin() {
		return min;
	}

	@Override
	public T getMax() {
		return max;
	}

	@Override
	public boolean includeMin() {
		return includeMin;
	}

	@Override
	public boolean includeMax() {
		return includeMax;
	}

}
