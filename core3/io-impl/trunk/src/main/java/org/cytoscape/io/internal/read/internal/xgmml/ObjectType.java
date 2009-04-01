package org.cytoscape.io.read.internal.xgmml;

public enum ObjectType {
	NONE("none"), STRING("string"), BOOLEAN("boolean"), REAL("real"), INTEGER(
			"integer"), LIST("list"), MAP("map"), COMPLEX("complex");

	private String name;

	private ObjectType(String s) {
		name = s;
	}

	public String toString() {
		return name;
	}
}