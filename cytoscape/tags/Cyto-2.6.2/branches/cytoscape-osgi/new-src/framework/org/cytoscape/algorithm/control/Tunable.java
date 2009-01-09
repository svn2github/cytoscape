
package org.cytoscape.algorithm.control;

public interface Tunable {

	void setConstraint(Constraint c);
	TunableType getType();
	Object getValue();
	void setValue(Object o);
	String getID();
	String getLabel();
	String getDescription();
}
