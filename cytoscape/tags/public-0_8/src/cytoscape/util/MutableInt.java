// MutableInt.java:  mutable int for listeners
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------

package cytoscape.util;
public class MutableInt {
    private int i;
    public MutableInt(int i) {
	this.i = i;
    }
    public int getInt() {
	return this.i;
    }
    public void setInt(int i) {
	this.i = i;
    }
}
