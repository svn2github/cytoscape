package cytoscape.visual.customgraphic;

/**
 * Null object for Custom Graphics
 * 
 * @author kono
 * 
 */
public class NullCustomGraphics extends AbstractCyCustomGraphics {

	private static final String NAME = "Empty Custom Graphics";

	public NullCustomGraphics() {
		super(NAME);
	}

	@Override
	public CyCustomGraphicsParser getParser() {
		return null;
	}
	
	
	@Override
	public String toString() {
		return this.getClass().getCanonicalName();
	}
}
