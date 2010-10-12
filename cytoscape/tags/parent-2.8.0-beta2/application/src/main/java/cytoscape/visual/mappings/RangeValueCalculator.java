package cytoscape.visual.mappings;

public interface RangeValueCalculator<T> {
	public T getRange(final Object attrValue);
	
	public boolean isCompatible(final Class<?> type);
}
