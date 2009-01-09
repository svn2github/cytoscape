// MutableBool.java:  mutable boolean for listeners
//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------

package cytoscape.util;
public class MutableBool {
    private boolean b;
    public MutableBool(boolean b) {
	this.b = b;
    }
    public boolean getBool() {
	return this.b;
    }
    public void setBool(boolean b) {
	this.b = b;
    }
}
