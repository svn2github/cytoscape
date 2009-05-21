package csplugins.jActiveModules;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;
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

  public int size(){
    return super.size();
  }

}
