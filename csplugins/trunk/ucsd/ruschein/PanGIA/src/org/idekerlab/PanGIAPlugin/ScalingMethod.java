package org.idekerlab.PanGIAPlugin;


public enum ScalingMethod {
	NONE("none"),
	LINEAR_LOWER("linear/lower"),
	LINEAR_UPPER("linear/upper"),
	RANK_LOWER("rank/lower"),
	RANK_UPPER("rank/upper");

	private String asString;

	ScalingMethod(final String asString) { this.asString = asString; }

	@Override public String toString() { return asString; }

	static ScalingMethod getEnumValue(final String asString) {
		if (asString.equals("none"))
			return NONE;
		if (asString.equals("linear/lower"))
			return LINEAR_LOWER;
		if (asString.equals("linear/upper"))
			return LINEAR_UPPER;
		if (asString.equals("rank/lower"))
			return RANK_LOWER;
		if (asString.equals("rank/upper"))
			return RANK_UPPER;

		throw new IllegalStateException("unknown string representation: \"" + asString + "\"!");
	}
}
