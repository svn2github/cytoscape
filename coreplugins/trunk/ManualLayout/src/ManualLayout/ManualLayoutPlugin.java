package ManualLayout;

import java.awt.Dimension;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.SwingConstants;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;

import ManualLayout.rotate.RotatePanel;
import ManualLayout.scale.ScalePanel;
import ManualLayout.control.ControlPanel;

public class ManualLayoutPlugin extends CytoscapePlugin {
	public ManualLayoutPlugin() {
		init();

		ManualLayoutAction manualLayoutActionListener = new ManualLayoutAction();
		JMenu layoutMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
				.getMenu("Layout");
		JCheckBoxMenuItem rotateMenuItem = new JCheckBoxMenuItem("Rotate");
		JCheckBoxMenuItem scaleMenuItem = new JCheckBoxMenuItem("Scale");
		JCheckBoxMenuItem controlMenuItem = new JCheckBoxMenuItem(
				"Align and Distribute");

		layoutMenu.add(rotateMenuItem, 0);
		layoutMenu.add(scaleMenuItem, 1);
		layoutMenu.add(controlMenuItem, 2);

		rotateMenuItem.setSelected(false);
		scaleMenuItem.setSelected(false);
		controlMenuItem.setSelected(false);

		rotateMenuItem.addActionListener(manualLayoutActionListener);
		scaleMenuItem.addActionListener(manualLayoutActionListener);
		controlMenuItem.addActionListener(manualLayoutActionListener);
	}

	private void init() {

		RotatePanel rotatePanel = new RotatePanel();
		ScalePanel scalePanel = new ScalePanel();
		ControlPanel controlPanel = new ControlPanel();

		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST).add("Rotate",
				rotatePanel);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST).add("Scale",
				scalePanel);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST).add(
				"Align and Distribute", controlPanel);
	}
}
