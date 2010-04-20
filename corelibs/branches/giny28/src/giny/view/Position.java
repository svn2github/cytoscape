package giny.view;


/**
 * Constants represent position of graphics objects.
 * 
 * @author kono
 * 
 */
public enum Position {
	NORTH_WEST("Northwest", "NW", Label.NORTHWEST), 
	NORTH("North", "N", Label.NORTH), 
	NORTH_EAST("Northeast", "NE", Label.NORTHEAST), 
	WEST("West", "W", Label.WEST), 
	CENTER("Center",	"C", Label.CENTER), 
	EAST("East", "E", Label.EAST), 
	NONE("none", "NONE", Label.NONE), 
	SOUTH_WEST("Southwest", "SW", Label.SOUTHWEST), 
	SOUTH("South", "S", Label.SOUTH), 
	SOUTH_EAST("Southeast","SE", Label.SOUTHEAST);
	
	private static final String[] ANCHORS = {
		NORTH_WEST.displayName, NORTH.displayName, 
		NORTH_EAST.displayName, WEST.displayName, 
		CENTER.displayName, EAST.displayName, 
		NONE.displayName, SOUTH_WEST.displayName, 
		SOUTH.displayName, SOUTH_EAST.displayName };

	private final String displayName;
	private final String shortName;
	private final int ginyConstatnt;

	private Position(final String displayName, final String shortName, final int ginyConstant) {
		this.displayName = displayName;
		this.shortName = shortName;
		this.ginyConstatnt = ginyConstant;
	}

	
	public String getName() {
		return this.displayName;
	}

	public String getShortName() {
		return this.shortName;
	}

	public int getGinyConstant() {
		return this.ginyConstatnt;
	}

	public static Position parse(final String value) {
		for (final Position p : Position.values()) {
			if (p.getName().equals(value) || p.getShortName().equals(value))
				return p;
		}
		
		System.out.println("$$$$$$$COULD NOT PARSE: " + value);
		return null;
	}

	public static Position parse(final int giny) {
		for (final Position p : Position.values()) {
			if (p.getGinyConstant() == giny)
				return p;
		}
		
		System.out.println("$$$$$$$COULD NOT PARSE GINY: " + giny);
		Thread.dumpStack();
		System.out.println("\n\n\n");
		return null;
	}

	public static String[] getNames() {
		return ANCHORS;
	}
}
