package cytoscape.visual.customgraphic;

public interface CyCustomGraphicsParser {
	public CyCustomGraphics getInstance(final String entry);

	public Class<? extends CyCustomGraphics> getTargetClass();
}
