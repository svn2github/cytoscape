package foo;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;

import ding.view.DNodeView;

import java.awt.ActionEvent;
import java.awt.Color;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

public class NeriusPlugin extends CytoscapePlugin
{

  public NeriusPlugin()
  {
    JMenuItem nerius = new JMenuItem(new AbstractAction("Nerius is everywhere")
      {
        public void actionPerformed(ActionEvent e)
        {
          final CyNetworkView gView = Cytoscape.getCurrentNetworkView();
          final Iterator nViews = gView.getNodeViewsIterator();
          final Rectangle2D rect = new Rectangle2D.Double
            (-11.0, -15.0, 22.0, 30.0);
          Paint paint = null;
          try {
            paint = new TexturePaint
              (ImageIO.read
               (new URL("http://cytoscape.org/people_photos/nerius.jpg")),
               rect); }
          catch (Exception exc) {
            paint = Color.black; }              
          while (nViews.hasNext()) {
            final DNodeView nView = (DNodeView) nViews.next();
            nView.addCustomGraphic(rect, paint, 0); }
        }
      });
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Layout").add
      (nerius);
}
