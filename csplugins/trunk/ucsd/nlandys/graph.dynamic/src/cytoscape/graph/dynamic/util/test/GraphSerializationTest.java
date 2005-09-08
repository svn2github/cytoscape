package cytoscape.graph.dynamic.util.test;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class GraphSerializationTest
{

  public static void main(String[] args) throws Exception
  {
    DynamicGraph graph = DynamicGraphFactory.instantiateDynamicGraph();
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
    objOut.writeObject(graph);
  }

}
