package cytoscape.visual.customgraphic;

/**
 * Null object for Custom Graphics.
 * This is used to reset custom graphics on node views.
 * 
 * @author kono
 * 
 */
public class NullCustomGraphics extends AbstractCyCustomGraphics {

	private static final String NAME = "[ Remove Graphics ]";

	public NullCustomGraphics() {
		super(NAME);
	}
	
	
	@Override
	public String toString() {
		return this.getClass().getCanonicalName();
	}
}
