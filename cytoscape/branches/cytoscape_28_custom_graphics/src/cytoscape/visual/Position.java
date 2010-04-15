package cytoscape.visual;

import javax.swing.SwingConstants;
import giny.view.Label;

/**
 * Constants represent position of graphics objects.
 * 
 * @author kono
 * 
 */
public enum Position {
	NORTH_WEST("Northwest", "NW", SwingConstants.NORTH_WEST, Label.NORTHWEST), 
	NORTH("North", "N", SwingConstants.NORTH, Label.NORTH), 
	NORTH_EAST("Northeast", "NE", SwingConstants.NORTH_EAST, Label.NORTHEAST), 
	WEST("West", "W", SwingConstants.WEST, Label.WEST), 
	CENTER("Center",	"C", SwingConstants.CENTER, Label.CENTER), 
	EAST("East", "E",SwingConstants.EAST, Label.EAST), 
	NONE("none", "NONE", -1,Label.NONE), 
	SOUTH_WEST("Southwest", "SW",SwingConstants.SOUTH_WEST, Label.SOUTHWEST), 
	SOUTH("South", "S",SwingConstants.SOUTH, Label.SOUTH), 
	SOUTH_EAST("Southeast","SE", SwingConstants.SOUTH_EAST, Label.SOUTHEAST),
	JUSTIFY_CENTER("Center Justified", "c", SwingConstants.CENTER, Label.JUSTIFY_CENTER), 
	JUSTIFY_LEFT("Left Justified", "l", SwingConstants.LEFT, Label.JUSTIFY_LEFT), 
	JUSTIFY_RIGHT("Right Justified", "r", SwingConstants.RIGHT, Label.JUSTIFY_RIGHT);
	
	private static final String[] ANCHORS = { NORTH_WEST.displayName,
		NORTH.displayName, NORTH_EAST.displayName, WEST.displayName,
		CENTER.displayName, EAST.displayName, SOUTH_WEST.displayName,
		SOUTH.displayName, SOUTH_EAST.displayName };

	private static final String[] JUSTIFY = { JUSTIFY_LEFT.displayName,
		JUSTIFY_CENTER.displayName, JUSTIFY_RIGHT.displayName };

	private final String displayName;
	private final String shortName;
	private final int swingConstant;
	private final int ginyConstatnt;

	private Position(final String displayName, final String shortName,
			final int swingConstant, final int ginyConstant) {
		this.displayName = displayName;
		this.shortName = shortName;
		this.swingConstant = swingConstant;
		this.ginyConstatnt = ginyConstant;
	}

	public String getName() {
		return this.displayName;
	}

	public String getShortName() {
		return this.shortName;
	}

	public int getSwingConstant() {
		return this.swingConstant;
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

	public static String[] getAnchorNames() {
		return ANCHORS;
	}

	public static String[] getJustifyNames() {
		return JUSTIFY;
	}
}
