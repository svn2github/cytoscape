package cytoscape.render.test;

import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SampleNetwork1
  extends Frame implements MouseListener, MouseMotionListener
{

  public static void main(String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new SampleNetwork1();
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }


  private final int m_imgWidth = 800;
  private final int m_imgHeight = 600;

  public SampleNetwork1()
  {
    super();
    m_graph = DynamicGraphFactory.instantiateDynamicGraph();
  }

}
