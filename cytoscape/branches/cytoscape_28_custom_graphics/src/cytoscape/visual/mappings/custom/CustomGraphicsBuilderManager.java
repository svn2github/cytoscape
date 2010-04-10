package cytoscape.visual.mappings.custom;

public interface CustomGraphicsBuilderManager {
	public void registerBuilder(final String builderName, final CustomGraphicsBuilder<?> builder);
	
	public CustomGraphicsBuilder<?> getBuilder(final String builderName);
}
