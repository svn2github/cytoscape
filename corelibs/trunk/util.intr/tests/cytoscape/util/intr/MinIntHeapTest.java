package cytoscape.util.intr;

import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.MinIntHeap;

import junit.framework.*;


public class MinIntHeapTest extends TestCase {
    MinIntHeap heap;
    int[] arr; 

    public void setUp() {
        heap = new MinIntHeap();
        System.out.println("Instantiated new MinIntHeap.");

        arr = new int[] { 5, 8, 1, 3, 8, 3, 0, 9, 1, 2, 7, 3 };
        System.out.println( "Defined my int[] to be: { 5, 8, 1, 3, 8, 3, 0, 9, 1, 2, 7, 3 }.");

    }

    public void testToss() {

	int last = -1;
	int size = 0;
        for (int i = 0; i < arr.length; i++) {
            heap.toss(arr[i]);
	    size = heap.size(); 
	    assertTrue(size > last);
	    last = size;
	}
        System.out.println("Tossed all elements of array onto heap.");
    }

 
    public void testIterator() {
   
   	// doh!
    	testToss(); 

        IntEnumerator iter = heap.orderedElements(true);
	assertNotNull( iter );

        System.out.println( "Got an IntEnumerator by calling orderedElements(true) on heap.");
        System.out.println("The enumerator's numRemaining() method returns " +
            iter.numRemaining() + ".");

	assertEquals(8,iter.numRemaining());

        System.out.print("The enumerator looks like this: { ");

        while (iter.numRemaining() > 1)
            System.out.print(iter.nextInt() + ", ");

        System.out.println(iter.nextInt() + " }.");
    }

    public void testOrder() {

   	// doh!
    	testToss(); 

        IntEnumerator iter = heap.orderedElements(false);
	assertNotNull( iter );
        System.out.println("Got an IntEnumerator by calling orderedElements(false) on heap.");
        System.out.println("The enumerator's numRemaining() method returns " +
            iter.numRemaining() + ".");

	assertEquals(12,iter.numRemaining());
        System.out.print("The enumerator looks like this: { ");

        while (iter.numRemaining() > 1)
            System.out.print(iter.nextInt() + ", ");

        System.out.println(iter.nextInt() + " }.");
    }
}
