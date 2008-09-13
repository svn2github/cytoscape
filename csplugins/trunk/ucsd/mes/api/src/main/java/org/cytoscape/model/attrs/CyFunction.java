package org.cytoscape.model.attrs;

public interface CyFunction<T> {
	Class<T> getBaseType();	
	T getValue();
	String getFunction();
	void setFunction(String s);
}
