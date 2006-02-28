package cytoscape.fung.test;

import cytoscape.fung.EdgeView;
import cytoscape.fung.Fung;
import cytoscape.fung.NodeView;
import cytoscape.graph.dynamic.DynamicGraph;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SanityTest
{

  public static void main(String[] args) throws Exception
  {
    final Fung fung = new Fung();
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new Frame();
          f.add(fung.getComponent());
          for (int i = 0; i < 10; i++) {
            f.show();
            f.resize(400, 300); }
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
    DynamicGraph graph = fung.getGraphModel();
    int node1 = graph.nodeCreate();
    int node2 = graph.nodeCreate();
    int node3 = graph.nodeCreate();
    int edge1 = graph.edgeCreate(node1, node2, true);
    int edge2 = graph.edgeCreate(node2, node3, true);
    int edge3 = graph.edgeCreate(node3, node1, true);
    fung.setScaleFactor(5);
    NodeView nv1 = fung.getNodeView(node1);
    NodeView nv2 = fung.getNodeView(node2);
    NodeView nv3 = fung.getNodeView(node3);
    EdgeView ev1 = fung.getEdgeView(edge1);
    EdgeView ev2 = fung.getEdgeView(edge2);
    EdgeView ev3 = fung.getEdgeView(edge3);
    nv1.setLocation(-20, -20);
    nv2.setLocation(20, -20);
    nv3.setLocation(0, 20);
    fung.updateView();
  }

}
