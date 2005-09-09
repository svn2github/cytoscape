package cytoscape.graph.dynamic.util.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializationSanity implements Serializable
{

  public static void main(String[] args) throws Exception
  {
    final int loopSize = 3;
    final SerializationSanity ss = new SerializationSanity();
    {
      final Node nodeLoop = new Node();
      Node currNode = nodeLoop;
      for (int i = 1; i < loopSize; i++) {
        currNode.m_next = new Node();
        currNode = currNode.m_next; }
      currNode.m_next = nodeLoop;
      ss.m_node = nodeLoop;
    }

    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
    objOut.writeObject(ss); objOut.flush(); objOut.close();
  }

  private Node m_node;
  private SerializationSanity() {}

  private static class Node implements Serializable {
    private Node m_next;
    private Node() {} }

}
