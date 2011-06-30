package cytoscape.visual.customgraphic;

public interface RenderingContext<T> {

	public void setContext(T context);
	public T getContext();
}
