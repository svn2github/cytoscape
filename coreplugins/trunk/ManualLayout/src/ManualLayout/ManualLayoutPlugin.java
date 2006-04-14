package ManualLayout;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;

import ManualLayout.rotate.RotateAction;
import ManualLayout.scale.ScaleAction;
import ManualLayout.control.ControlAction;

public class ManualLayoutPlugin extends CytoscapePlugin
{
  public ManualLayoutPlugin()
  {
    JMenu layoutMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
                                .getMenu("Layout");
    JMenuItem rotateMenuItem = new JMenuItem(new RotateAction());
    JMenuItem scaleMenuItem = new JMenuItem(new ScaleAction());
    JMenuItem controlMenuItem = new JMenuItem(new ControlAction());

    layoutMenu.add(rotateMenuItem);
    layoutMenu.add(scaleMenuItem);
    layoutMenu.add(controlMenuItem);
  }
}
