package csplugins.ucsd.rmkelley.GeneticInteractions;


//java import statement
import java.util.List;
import java.util.Arrays;

/**
 * This class is meant to store the top scoring results from a large list. Ideally, the
 * each result is added to the list as it's score is calculated, and only the top scoring
 * ones will remain. It is backed up by a MinHeap.
 */
class RestrictedMinHeap{ // Min heap class

  /**
   * An array used to back the minheap
   */
  private Comparable [] heap;  // Pointer to the heap array
  /**
   * The number of elements currently in hte heap
   */
  private int n;        // Number of elements now in the heap

  /**
   * Constructor
   * @param capacity This is hte initial and final capacity of the heap. If a number of elements greater than capacity is added to the heap, the lower scoring ones will be discarded.
   */
  public RestrictedMinHeap(int capacity){
    heap = new Comparable[capacity];
    n = 0;		
  }

  /**
   * Move an element to its correct location in the heap.
   * @param pos The current position of the parameter
   */
  private void siftdown(int pos) { // Put element in its correct place
    while(pos < n/2){
      int minChild = 2*pos + 1;
      if(minChild < (n-1) && heap[minChild].compareTo(heap[minChild+1])>0){
	minChild++;
      }
      if(heap[pos].compareTo(heap[minChild]) <= 0){
	return;
      }
      else{
	Comparable temp = heap[minChild];
	heap[minChild] = heap[pos];
	heap[pos] = temp;
	pos = minChild;
      }
    }
  }

  /**
   * Insert this value into the restricted heap. This is for when the heap is not full,
   * so it just does a regular old insert.
   * @param val The value to be inserted
   */
  private void insert(Comparable val) { // Insert value into heap
    int current = n++;
    int parent = (current-1)/2;
    heap[current] = val;        
    // Now sift up until curr's parent's < curr's key
    while ((current!=0) && heap[current].compareTo(heap[parent])<0){
      Comparable temp = heap[parent];
      heap[parent] = heap[current];
      heap[current] = heap[parent];
      current = parent;
    }
  }

  /**
   * Helper function for insert. If the heap is already full, then we remove the current
   * min value and then do an insert. Only called if we know we want to do the insert
   * @param val The value to be optionally inserted or discarded
   */
  private void removeMinAndInsert(Comparable val){
    heap[0] = val;
    siftdown(0);
  }

  /**
   * Perform an insert that may optionally remove the current min element or fail to insert. If the
   * inserted value is better than the current min the current min will be ejected. Otherwise, the current
   * insert will be discarded
   * @param val The value to be inserted
   */
  public void restrictedInsert(Comparable val){ 
    if(n ==heap.length){
      //need to do a remove min and insert
      if(val.compareTo(heap[0]) > 0){
	//we would rather have the
	//value being inserted than
	//the min
	removeMinAndInsert(val);
      }
      //otherwise, we're not interested in
      //the value so just forget about it
    }
    else{
      //just do an insert
      insert(val);
    }
  }

  /**
   * Return a list view of the elements currently in the heap. 
   * @return A list of elements currently in the heap. I wouldn't use this
   * to remove anything if I were you.
   */
  public List getList(){
    return Arrays.asList(heap).subList(0,n);
  }
}
