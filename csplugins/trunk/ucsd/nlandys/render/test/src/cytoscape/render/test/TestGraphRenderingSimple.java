package cytoscape.render.test;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TestGraphRenderingSimple
  extends Frame implements MouseListener, MouseMotionListener
{

  public static void main(String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestGraphRenderingSimple();
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  public TestGraphRenderingSimple()
  {
    super();
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e)
  {
//     if (e.getButton() == MouseEvent.BUTTON1) {
//       m_currMouseButton = 1;
//       m_lastXMousePos = e.getX();
//       m_lastYMousePos = e.getY(); }
//     else if (e.getButton() == MouseEvent.BUTTON2) {
//       m_currMouseButton = 2;
//       m_lastXMousePos = e.getX();
//       m_lastYMousePos = e.getY(); }
  }

  public void mouseReleased(MouseEvent e)
  {
//     if (e.getButton() == MouseEvent.BUTTON1) {
//       if (m_currMouseButton == 1) m_currMouseButton = 0; }
//     else if (e.getButton() == MouseEvent.BUTTON2) {
//       if (m_currMouseButton == 2) m_currMouseButton = 0; }
  }

  public void mouseDragged(MouseEvent e)
  {
//     if (m_currMouseButton == 1) {
//       double deltaX = e.getX() - m_lastXMousePos;
//       double deltaY = e.getY() - m_lastYMousePos;
//       m_lastXMousePos = e.getX();
//       m_lastYMousePos = e.getY();
//       m_currXCenter -= deltaX / m_currScale;
//       m_currYCenter += deltaY / m_currScale; // y orientations are opposite.
//       repaint(); }
//     else if (m_currMouseButton == 2) {
//       double deltaY = e.getY() - m_lastYMousePos;
//       m_lastXMousePos = e.getX();
//       m_lastYMousePos = e.getY();
//       m_currScale *= Math.pow(2, -deltaY / 300.0d);
//       repaint(); }
  }

  public void mouseMoved(MouseEvent e) {}

  public boolean isResizable() { return false; }

}
