package cytoscape.graph.dynamic.util.test;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GraphSerializationTest
{

  public static void main(String[] args) throws Exception
  {
    DynamicGraph graph = DynamicGraphFactory.instantiateDynamicGraph();
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
    objOut.writeObject(graph); objOut.flush(); objOut.close();
    System.out.println("An empty graph takes " + byteOut.size() +
                       " bytes in serialized form.");
    ByteArrayInputStream byteIn =
      new ByteArrayInputStream(byteOut.toByteArray());
    ObjectInputStream objIn = new ObjectInputStream(byteIn);
    graph = (DynamicGraph) objIn.readObject(); objIn.close();
    if (graph.nodes().numRemaining() != 0 ||
        graph.edges().numRemaining() != 0)
      throw new IllegalStateException("expected restored graph to be empty");
  }

}
