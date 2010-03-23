package cytoscape.visual.customgraphic;

public interface CyCustomGraphicsParserFactory {
	public CyCustomGraphicsParser getParser(final String customGraphicsClassName);
	
	public void registerParser(Class<? extends CyCustomGraphics<?>> cgClass, CyCustomGraphicsParser parser);
}
