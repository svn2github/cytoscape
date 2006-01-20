package ding.view.test;

import ding.view.DGraphView;
import fing.model.FingRootGraphFactory;
import giny.model.GraphPerspective;
import giny.model.RootGraph;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.view.EdgeView;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SanityTest
{

  public static void main(String[] args) throws Exception
  {
    final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
    final int node1 = root.createNode();
    final int node2 = root.createNode();
    final int node3 = root.createNode();
    final int edge1 = root.createEdge(node1, node2);
    final int edge2 = root.createEdge(node2, node3);
    final int edge3 = root.createEdge(node3, node1);
    final GraphPerspective persp = root.createGraphPerspective
      ((int[]) null, (int[]) null);
    final DGraphView view = new DGraphView(persp);
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new Frame();
//           f.add(new LWButton01("foo"));
          f.add(view.getComponent());
          for (int i = 0; i < 10; i++) {
            f.show();
            f.resize(400, 300); }
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
    final NodeView nv1 = view.addNodeView(node1);
    final NodeView nv2 = view.addNodeView(node2);
    final NodeView nv3 = view.addNodeView(node3);
    final EdgeView ev1 = view.addEdgeView(edge1);
    final EdgeView ev2 = view.addEdgeView(edge2);
    final EdgeView ev3 = view.addEdgeView(edge3);
    nv1.setOffset(0.0d, 50.0d);
    nv2.setOffset(-30.0d, -10.0d);
    nv3.setOffset(30.0d, 10.0d);
    nv1.setBorderWidth(1.0f);
    nv2.setBorderWidth(1.0f);
    nv3.setBorderWidth(1.0f);
    ev1.setTargetEdgeEnd(EdgeView.BLACK_DELTA);
    ev2.setTargetEdgeEnd(EdgeView.BLACK_DELTA);
    ev3.setTargetEdgeEnd(EdgeView.BLACK_DELTA);
    view.fitContent();
    view.updateView();
  }

}
