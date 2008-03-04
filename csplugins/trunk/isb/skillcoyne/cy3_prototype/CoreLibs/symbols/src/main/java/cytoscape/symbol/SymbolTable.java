package cytoscape.symbol;

import java.util.*;

public class SymbolTable {

	private SymbolTable() {
		
	}
	
	
}



// THIS IS JUST NOTES FOR ME

public abstract CytoscapeProp {

	private int propertyTypeSymbol;
	
	
	
	
	public CytoscapeProp(int Symbol) {
		propertyTypeSymbol = Symbol;
	}
	
	public int getSymbol() { return propertyTypeSymbol; }
	
	public String toString();
	public void fromString(String str);

}

public class ColorProperty extends CytoscapeProp {

	private Color color;
	
	
	public ColorProperty(int Symbol, Color c) {
		super(Symbol);
		color = cl;
	}
	
	public String toString() {
	 	//blah
	}

	public void fromString(String str) {
		//blah
	}

}


public class BasicGraphView {


public main() {

	GraphView gv = new GraphView();
	gv.setVisualProperty(node5, new ColorProperty(PropertyTypeSymbol.get("nodeFillColor"), Color.BLUE);


}



}
