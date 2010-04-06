package cytoscape.visual.mappings;

public interface RangeValueCalculator<T> {
	public T getRange(final Object attrValue);
	
	public Class<T> getRangeClass();
}
