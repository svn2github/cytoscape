package cytoscape.visual.customgraphic;

public interface CyCustomGraphicsParser {
	public CyCustomGraphics<?> getInstance(final String entry);
	
	public String getVizMapPropsString(final CyCustomGraphics<?> customGraphics);
}
