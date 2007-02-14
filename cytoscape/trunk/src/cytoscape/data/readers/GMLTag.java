package cytoscape.data.readers;

public enum GMLTag {
	GRAPH("graph"), NODE("node"), EDGE("edge"), GRAPHICS("graphics"), 
	LABEL("label"), SOURCE("source"), TARGET("target"), 
	
	X("x"), Y("y"), H("h"), W("w"), TYPE("type"), ID("id"), ROOT_INDEX("root_index"), 
	
	RECTANGLE("rectangle"), ELLIPSE("ellipse"), LINE("line"), POINT("point"), 
	DIAMOND("diamond"), HEXAGON("hexagon"), OCTAGON("octagon"), 
	PARALLELOGRAM("parallelogram"), TRIANGLE("triangle"), FILL("fill"), WIDTH("width"), 
	STRAIGHT_LINES("line"), CURVED_LINES("curved"),
	
	SOURCE_ARROW("source_arrow"), TARGET_ARROW("target_arrow"), ARROW("arrow"), 
	ARROW_NONE("none"), ARROW_FIRST("first"), ARROW_LAST("last"),
	ARROW_BOTH("both"), OUTLINE("outline"), OUTLINE_WIDTH("outline_width"),
	
	VERSION("Version"), CREATOR("Creator");
	
	private String tag;
	
	private GMLTag(String tag) {
		this.tag = tag;
	}
	
	public String toString() {
		return tag;
	}
}
