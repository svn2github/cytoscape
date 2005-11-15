package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.giny.CytoscapeRootGraph;
import giny.model.Edge;
import giny.model.Node;
import java.util.Iterator;

public class Bug667Test
{

  private static CytoscapeRootGraph _rg = Cytoscape.getRootGraph ();

  public static void testBug()
  {
    String target_uuid  = "2:1127687773353:141.184.136.96";
    String s1_edge_uuid = "3:1127687773353:141.184.136.96";
    String s2_edge_uuid = "4:1127687773353:141.184.136.96";
    Node   n1           = Cytoscape.getCyNode ("S", true);
    Node   target       = Cytoscape.getCyNode (target_uuid, true);

    Edge   edge1 = createEdge (n1, target, s1_edge_uuid);
    Edge   edge2 = createEdge (n1, target, s2_edge_uuid);
    System.out.println ("After Creation:");
    if (null == findEdge (s2_edge_uuid)) throw new IllegalStateException();

    // Will cause edge1 and edge2 to be removed:
    Cytoscape.getRootGraph ().removeNode (target);
    System.out.println ("After Deleting 'Target' Node:");

    if (null != findEdge (s2_edge_uuid)) throw new IllegalStateException();

    // remake target:
    System.out.println ("Adding back Target Node:");
    target = Cytoscape.getCyNode (target_uuid, true);

    if (null != findEdge (s2_edge_uuid)) throw new IllegalStateException();
    // recreate edge1:
    edge1 = createEdge (n1, target, s1_edge_uuid);
    System.out.println ("After Recreating edge1:");
    if (null != slowGetEdge (s2_edge_uuid)) throw new IllegalStateException();

    // *****NOW getEdge() WILL FIND edge2 WHEN IT SHOULDN'T*********:
    if (null != findEdge (s2_edge_uuid)) throw new IllegalStateException();
  }

  private static Edge createEdge (Node   source,
                                  Node   target,
                                  String uuid)
  {
    int edge_idx = _rg.createEdge (source, target);
    System.out.println ("createEdge: " + uuid + " RGidx = " + edge_idx +
                        " source = " + source.getIdentifier () +
                        " target = " + target.getIdentifier ());
    Edge edge = _rg.getEdge (edge_idx);
    edge.setIdentifier (uuid);
    return edge;
  }

  public static Edge findEdge (String uuid)
  {
    Edge edge = _rg.getEdge (uuid);
    if (edge != null)
      {
        System.out.println ("findEdge: " + uuid + " exists = true" +
                            " RGidx = " + _rg.getIndex (edge));
      }
    else
      {
        System.out.println ("findEdge: " + uuid + " exists = false");
      }
    return edge;
  }

  private static Edge slowGetEdge (String uuid)
  {
    Iterator it   = _rg.edgesIterator ();
    Edge     edge;
    while (it.hasNext ())
      {
        edge = (Edge) it.next ();
        if (uuid.equals (edge.getIdentifier ()))
          {
            return edge;
          }
      }
    return null;
  }
}
