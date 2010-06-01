package cytoscape.visual.customgraphic.experimental;

public interface CustomGraphicsProperty<T> {
	
	public T getDefaultValue();
	
	public T getValue();
	public void setValue(Object value);
}
