// MutableString.java:  mutable string for listeners
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------

package cytoscape.util;
public class MutableString {
    private String str;
    public MutableString(String str) {
	this.str = str;
    }
    public String getString() {
	return this.str;
    }
    public void setString(String str) {
	this.str = str;
    }
}
