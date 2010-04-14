package cytoscape.visual.customgraphic;

public interface CustomGraphicsProperty<T> {
	
	public T getDefaultValue();
	
	public T getValue();
	public void setValue(Object value);
}
