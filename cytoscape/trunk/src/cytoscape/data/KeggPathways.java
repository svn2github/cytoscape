package cytoscape.data;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A structure to store KEGG Pathways
 * <p>
 * It contains tables accessible by IDs of:
 * <ul>
 *  <li>Descriptions</li>
 *  <li>Lists of Node Names</li>
 * </ul>
 *
 * @author namin@mit.edu
 * @version 2002-04-24
 */
public class KeggPathways {
    Hashtable descs;
    Hashtable lists;

    public KeggPathways() {
	descs = new Hashtable();
	lists = new Hashtable();
    }

    public void add(String id, String desc, Vector list) {
	descs.put(id, desc);
	lists.put(id, list);
    }

    public void print() {
	System.out.println("Printing pathways");
	for (Enumeration e = descs.keys(); e.hasMoreElements(); ) {
	    System.out.print(" Id: ");
	    String id = (String) e.nextElement();
	    System.out.print(id);

	    System.out.print(" Desc: ");
	    String desc = (String) descs.get(id);
	    System.out.print(desc);

	    System.out.print(" List: ");
	    Vector list = (Vector) lists.get(id);
	    System.out.println(list);
	}
	System.out.println("Done with printing pathways");
    }
}
