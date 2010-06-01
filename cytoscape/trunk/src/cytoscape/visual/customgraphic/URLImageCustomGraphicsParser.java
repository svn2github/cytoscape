package cytoscape.visual.customgraphic;

import cytoscape.Cytoscape;

/**
 * Create instance of URLImageCustomGraphics object from String.
 * 
 * @author kono
 * 
 */
public class URLImageCustomGraphicsParser implements CyCustomGraphicsParser {

	private static final Class<? extends CyCustomGraphics<?>> TARGET_CLASS = URLImageCustomGraphics.class;
	private String entry[];

	/**
	 * Generate Custom Graphics object from a string.
	 * 
	 * <p>
	 * There are two types of valid string:
	 * <ul>
	 * <li>Image URL only - This will be used in Passthrough mapper.
	 * <li>Output of toString method of URLImageCustomGraphics
	 * </ul>
	 * 
	 */
	@Override
	public CyCustomGraphics<?> getInstance(String entryStr) {
		// Check this is URL or not
		if(entryStr == null) return null;
		
		if (!validate(entryStr)) {
			return null;
		}

		System.out.println("VALID Class ======= " + entry[1] + ", " + entry[2]);
		final String imageName = entry[1];
		CyCustomGraphics<?> cg = Cytoscape.getVisualMappingManager()
				.getCustomGraphicsPool().get(Integer.parseInt(imageName));
		cg.setDisplayName(entry[2]);
		return cg;
	}

	private boolean validate(final String entryStr) {
		entry = entryStr.split(",");
		if (entry == null || entry.length < 3) {
			System.out.println("!!!!! INVALID ======= " + entryStr);
			return false;
		}

		// Check class name
		if (entry[0].trim().equals(
				URLImageCustomGraphics.class.getCanonicalName()) == false) {
			System.out.println("!!!!! INVALID2 ======= " + entryStr);
			return false;
		}
		return true;
	}

	@Override
	public String getVizMapPropsString(CyCustomGraphics<?> customGraphics) {

		return customGraphics.toString();
	}

	@Override
	public Class<? extends CyCustomGraphics<?>> getTargetClass() {
		return TARGET_CLASS;
	}

}
