
package org.cytoscape.tunable;

import javax.swing.*;


public interface Tunable {

	// TODO - make an enum
	final public static int INTEGER = 0;
	final public static int DOUBLE = 1;
	final public static int BOOLEAN = 2;
	final public static int STRING = 3;
	final public static int NODEATTRIBUTE = 4;
	final public static int EDGEATTRIBUTE = 5;
	final public static int LIST = 6;
	final public static int GROUP = 7;
	final public static int BUTTON = 8;

	// Flags
	final public static int NOINPUT = 0x1;
	final public static int NUMERICATTRIBUTE = 0x2;
	final public static int MULTISELECT = 0x4;
	final public static int USESLIDER = 0x8;

	public void setFlag(int flag) ;
	public void clearFlag(int flag) ;
	public void setValue(Object value) ;
	public Object getValue() ;
	public boolean valueChanged() ;
	public void setLowerBound(Object lowerBound) ;
	public Object getLowerBound() ;
	public void setUpperBound(Object upperBound) ;
	public Object getUpperBound() ;
	public String toString() ;
	public String getName() ;
	public int getType() ;
	public String getDescription() ;
	public JPanel getPanel() ;
	public void updateValue() ;
}
