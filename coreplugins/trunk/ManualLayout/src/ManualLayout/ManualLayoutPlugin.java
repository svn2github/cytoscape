package ManualLayout;

import java.awt.Dimension;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;

import ManualLayout.rotate.RotatePanel;
import ManualLayout.scale.ScalePanel;
import ManualLayout.control.ControlPanel;

public class ManualLayoutPlugin extends CytoscapePlugin
{
  public ManualLayoutPlugin()
  {
	init();
	
    JMenu layoutMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
                                .getMenu("Layout");
    JMenuItem rotateMenuItem = new JMenuItem(new ManualLayoutAction("Rotate"));
    JMenuItem scaleMenuItem = new JMenuItem(new ManualLayoutAction("Scale"));
    JMenuItem controlMenuItem = new JMenuItem(new ManualLayoutAction("Align and Distribute"));

    layoutMenu.add(rotateMenuItem, 0);
    layoutMenu.add(scaleMenuItem, 1);
    layoutMenu.add(controlMenuItem, 2);
  }
  
  private void init()
  {
	  //JTabbedPane manualLayoutPanel = new JTabbedPane();
	  
	  RotatePanel rotatePanel = new RotatePanel();
	  //rotatePanel.setPreferredSize(new Dimension(100,50));
	  //manualLayoutPanel.add("Rotation", rotatePanel);
	  
	  ScalePanel scalePanel = new ScalePanel();
	  //scalePanel.setPreferredSize(new Dimension(100,50));
	  //manualLayoutPanel.add("Scale", ScalePanel);
	  
	  ControlPanel controlPanel = new ControlPanel();
	  //controlPanel.setPreferredSize(new Dimension(100,50));
	  //manualLayoutPanel.add("Control", controlPanel);
	  
	  Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).add("Rotate",rotatePanel);
	  Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).add("Scale",scalePanel);
	  Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).add("Align and Distribute",controlPanel);
  }
}
