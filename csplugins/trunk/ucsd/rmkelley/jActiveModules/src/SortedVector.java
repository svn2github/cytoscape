package csplugins.jActiveModules;
import java.util.Vector;
import java.util.Collection;
import java.util.Collections;
/**
 * Like a vector, but supports a sorted insert option
 */
public class SortedVector extends Vector{

    public SortedVector(){
	super();
    }

    public SortedVector(Collection c){
	super(c);
    }
    /**
     * Insert an object into an already sorted
     * vector in sorted order
     * @param o object to be inserted
     * @return result of calling Vector.add
     */
    public void sortedAdd(Object o){
	int insert_point = Collections.binarySearch(this,o);
	if(insert_point < 0){
	    this.add(-insert_point-1, o);
	}
	else{
	    this.add(insert_point,o);
	}
    }

}
