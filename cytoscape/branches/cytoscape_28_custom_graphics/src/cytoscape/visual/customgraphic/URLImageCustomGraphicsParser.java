package cytoscape.visual.customgraphic;

/**
 * Create instance of URLImageCustomGraphics object from String.
 * 
 * @author kono
 *
 */
public class URLImageCustomGraphicsParser implements CyCustomGraphicsParser {
	
	private String className;
	
	private static final Class<?> TARGET_CLASS = URLImageCustomGraphics.class;

	private static final URLImageCustomGraphicsParser INSTANCE = new URLImageCustomGraphicsParser();
	
	public static URLImageCustomGraphicsParser getInstance() {
		return INSTANCE;
	}
	
	
	private String entry[];
	
	private URLImageCustomGraphicsParser() {
		//register
		CyCustomGraphicsParserFactoryImpl.getFactory().registerParser(URLImageCustomGraphics.class, this);
	}

	@Override
	public CyCustomGraphics<?> getInstance(String entryStr) {
		if(!validate(entryStr))
			return null;
		
		System.out.println("VALID Class ======= " + entryStr);
		final String imageName = entry[1];
		CyCustomGraphics<?> cg = CustomGraphicsPool.get(imageName);
		cg.setDisplayName(entry[2]);
		return cg;
	}
	
	
	private boolean validate(final String entryStr) {
		entry = entryStr.split(",");
		if(entry == null || entry.length < 3) {
			System.out.println("!!!!! INVALID ======= " + entryStr);
			return false;
		}
		
		// Check class name
		if(entry[0].trim().equals(URLImageCustomGraphics.class.getCanonicalName()) == false) {
			System.out.println("!!!!! INVALID2 ======= " + entryStr);
			return false;
		}
		return true;
	}

	@Override
	public String getVizMapPropsString(CyCustomGraphics<?> customGraphics) {
		
		return customGraphics.toString();
	}

}
