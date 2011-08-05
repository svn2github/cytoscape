package giny.view;


public enum Justification {
	JUSTIFY_CENTER("Center Justified", "c", Label.JUSTIFY_CENTER), JUSTIFY_LEFT(
			"Left Justified", "l", Label.JUSTIFY_LEFT), JUSTIFY_RIGHT(
			"Right Justified", "r", Label.JUSTIFY_RIGHT);
	
	private static String[] JUSTIFY;

	private final String displayName;
	private final String shortName;
	private final int ginyConstatnt;

	private Justification(final String displayName, final String shortName,
			final int ginyConstant) {
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
	
	public static Justification parse(final String value) {
		for (final Justification j : values()) {
			if (j.getName().equals(value) || j.getShortName().equals(value))
				return j;
		}
		return null;
	}

	public static Justification parse(final int giny) {
		for (final Justification j : values()) {
			if (j.getGinyConstant() == giny)
				return j;
		}
		
		return null;
	}


	public static String[] getNames() {
		if(JUSTIFY == null) {
			JUSTIFY = new String[values().length];
			int i = 0;
			for(Justification j: values()) {
				JUSTIFY[i] = j.displayName;
				i++;
			}
		}
		
		return JUSTIFY;
	}

}
