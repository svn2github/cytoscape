package org.idekerlab.PanGIAPlugin;


public enum ScalingMethodX {
	NONE("none (prescaled)"),
	LINEAR_LOWER("lower"),
	LINEAR_UPPER("upper");
	//RANK_LOWER("rank/lower"),
	//RANK_UPPER("rank/upper");

	private String displayString;

	ScalingMethodX(final String displayString) { this.displayString = displayString; }

	public String getDisplayString() { return displayString; }

	static public ScalingMethodX getEnumValue(final String displayString) {
		for (final ScalingMethodX method : ScalingMethodX.values()) {
			if (method.getDisplayString().equals(displayString))
				return method;
		}

		throw new IllegalStateException("unknown string representation: \"" + displayString + "\"!");
	}
}
