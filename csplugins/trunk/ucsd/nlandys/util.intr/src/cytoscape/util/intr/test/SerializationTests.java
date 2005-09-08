package cytoscape.util.intr.test;

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
    objOut.writeObject(intArray);
    objOut.writeObject(intHash);
    objOut.writeObject(intIntHash);
    objOut.writeObject(intQueue);
    objOut.writeObject(intStack);
    objOut.writeObject(minIntHeap);
    objOut.flush(); objOut.close();
  }

}
