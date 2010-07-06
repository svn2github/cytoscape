package cytoscape.visual.customgraphic.impl.vector;

public interface CustomGraphicsProperty<T> {
	
	public T getDefaultValue();
	
	public T getValue();
	public void setValue(Object value);
}
