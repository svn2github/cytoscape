package cytoscape.filters.dialogs;

/**
 * Mutable Boolean 
 * wraps a <code>boolean</code> to allow it to be mofied by reference.
 */
public class MutableBoolean {
    boolean b;

    public MutableBoolean(boolean b) {
	setValue(b);
    }

    public boolean booleanValue() {
	return b;
    }

    public void setValue(boolean b) {
	this.b = b;
    }

    public String toString() {
	return String.valueOf(b);
    }
}
