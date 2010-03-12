package cytoscape.visual.customgraphic;

public interface DynamicRenderer<T extends RenderingContext> {
	public void render(T context);
}
