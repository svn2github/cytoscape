import java.util.NoSuchElementException;

abstract class Heiarchy {
  public static final Object inf = new Object();
  public abstract boolean priorityOver(Object l, Object r);
}

class IntHeiarchy extends Heiarchy {
  public boolean priorityOver(Object l, Object r) {
    if (l == inf && r == inf) return false;
    if (l == inf) return true;
    if (r == inf) return false;
    int left = ((Integer)l).intValue();
    int right = ((Integer)r).intValue();
    return left > right; }
}

public class Heap {

  private Heiarchy hc;
  private Object[] heap;
  private int currentSize;
  private boolean orderOK;
  private static final int DEFAULT_CAPACITY = 11;

  public Heap(Heiarchy h) {
    hc = h;
    heap = new Object[DEFAULT_CAPACITY];
    heap[0] = hc.inf;
    currentSize = 0;
    orderOK = true; }

  public void toss(Object x) {
    checkSize();
    currentSize++;
    heap[currentSize] = x;
    Object parent = heap[currentSize/2];
    if (!(hc.priorityOver(parent, x))) orderOK = false; }

  public boolean isEmpty() {
    return currentSize == 0; }

  private void checkSize() {
    if (currentSize < heap.length - 1) return;
    Object[] newHeap = new Object[heap.length * 2];
    for (int i = 0; i < heap.length; i++) {
      newHeap[i] = heap[i]; }
    heap = newHeap; }

  public void insert(Object x) {
    if (!orderOK) {
      this.toss(x);
      return; }
    this.toss(x);
    this.percolateUp(currentSize);
    orderOK = true; }

  private void percolateUp(int childIndex) {
    for (int parentIndex = childIndex/2;
	 hc.priorityOver(heap[childIndex], heap[parentIndex]);
	 childIndex = parentIndex, parentIndex = parentIndex/2) {
      Object temp = heap[parentIndex];
      heap[parentIndex] = heap[childIndex];
      heap[childIndex] = temp; } }

  public Object findMax() {
    if (currentSize == 0) throw new NoSuchElementException();
    if (!orderOK) this.fixHeap();
    return heap[1]; }

  public Object deleteMax() {
    Object returnThis = this.findMax();
    heap[1] = heap[currentSize];
    currentSize--;
    this.percolateDown(1);
    return returnThis; }

  private void percolateDown(int parentIndex) {
    for (int leftChildIndex = parentIndex * 2;
	 leftChildIndex <= currentSize;
	 parentIndex = leftChildIndex, leftChildIndex = leftChildIndex * 2) {
      if (leftChildIndex + 1 <= currentSize &&
	  hc.priorityOver(heap[leftChildIndex + 1], heap[leftChildIndex]))
	leftChildIndex++;
      if (hc.priorityOver(heap[leftChildIndex], heap[parentIndex])) {
	Object temp = heap[parentIndex];
	heap[parentIndex] = heap[leftChildIndex];
	heap[leftChildIndex] = temp; }
      else return; }
    return; }

  private void fixHeap() {
    for (int i = currentSize / 2; i > 0; i--) {
      percolateDown(i); }
    orderOK = true; }

  // this is a level-order traversal
  public void printHeap() {
    System.out.println();
    int levelMeter = 2;
    for (int i = 1; i <= currentSize; i++) {
      if (i < levelMeter) {
	System.out.print(heap[i]);
	System.out.print(" "); }
      else {
	System.out.println();
	System.out.println();
	levelMeter = levelMeter * 2;
	System.out.print(heap[i]);
	System.out.print(" "); } }
    System.out.println();
    System.out.println();
    return; }
}
      
