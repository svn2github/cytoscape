package cytoscape.util.intr.test;

import cytoscape.util.intr.ArrayIntEnumerator;
import cytoscape.util.intr.ArrayIntIterator;
import cytoscape.util.intr.IntArray;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntIntHash;
import cytoscape.util.intr.IntQueue;
import cytoscape.util.intr.IntStack;
import cytoscape.util.intr.MinIntHeap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationTests
{

  public static void main(String[] args) throws Exception
  {
    ArrayIntEnumerator arrayIntEnumerator =
      new ArrayIntEnumerator(new int[] { 2, 6, 3 }, 1, 2);
    ArrayIntIterator arrayIntIterator =
      new ArrayIntIterator(new int[] { 2, 6, 3}, 1, 2);
    IntArray intArray = new IntArray();
    intArray.setIntAtIndex(-3, 5);
    IntHash intHash = new IntHash();
    intHash.put(23);
    IntIntHash intIntHash = new IntIntHash();
    intIntHash.put(2, 3);
    IntQueue intQueue = new IntQueue();
    intQueue.enqueue(-43);
    IntStack intStack = new IntStack();
    intStack.push(72);
    MinIntHeap minIntHeap = new MinIntHeap();
    minIntHeap.insert(4);
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
    objOut.writeObject(arrayIntEnumerator);
    objOut.writeObject(arrayIntIterator);
    objOut.writeObject(intArray);
    objOut.writeObject(intHash);
    objOut.writeObject(intIntHash);
    objOut.writeObject(intQueue);
    objOut.writeObject(intStack);
    objOut.writeObject(minIntHeap);
    objOut.flush(); objOut.close();
    ByteArrayInputStream byteIn =
      new ByteArrayInputStream(byteOut.toByteArray());
    ObjectInputStream objIn = new ObjectInputStream(byteIn);
    arrayIntEnumerator = (ArrayIntEnumerator) objIn.readObject();
    arrayIntIterator = (ArrayIntIterator) objIn.readObject();
    intArray = (IntArray) objIn.readObject();
    intHash = (IntHash) objIn.readObject();
    intIntHash = (IntIntHash) objIn.readObject();
    intQueue = (IntQueue) objIn.readObject();
    intStack = (IntStack) objIn.readObject();
    minIntHeap = (MinIntHeap) objIn.readObject();
    objIn.close();
    if (arrayIntEnumerator.numRemaining() != 2 ||
        arrayIntEnumerator.nextInt() != 6 ||
        arrayIntEnumerator.nextInt() != 3)
      throw new IllegalStateException("ArrayIntEnumerator not good");
    if ((!arrayIntIterator.hasNext()) ||
        arrayIntIterator.nextInt() != 6 ||
        (!arrayIntIterator.hasNext()) ||
        arrayIntIterator.nextInt() != 3 ||
        arrayIntIterator.hasNext())
      throw new IllegalStateException("ArrayIntIterator not good");
    if (intArray.getIntAtIndex(5) != -3 ||
        intArray.getIntAtIndex(4) != 0)
      throw new IllegalStateException("IntArray not good");
    if (intHash.size() != 1 ||
        intHash.get(23) != 23 ||
        intHash.get(7) != -1)
      throw new IllegalStateException("IntHash not good");
    if (intIntHash.size() != 1 ||
        intIntHash.get(2) != 3 ||
        intIntHash.get(0) != -1)
      throw new IllegalStateException("IntIntHash not good");
    if (intQueue.dequeue() != -43 ||
        intQueue.size() != 0)
      throw new IllegalStateException("IntQueue not good");
    if (intStack.pop() != 72 ||
        intStack.size() != 0)
      throw new IllegalStateException("IntStack not good");
    if (minIntHeap.deleteMin() != 4 ||
        minIntHeap.size() != 0)
      throw new IllegalStateException("MinIntHeap not good");
  }

}
