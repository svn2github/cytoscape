package org.jmathplot.gui.components;

import javax.swing.*;
import org.jmathplot.gui.*;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

/**
 * <p>Copyright : BSD License</p>

 * @author Yann RICHET
 * @version 3.0
 */

public class DatasFrame
	extends JFrame {

	private PlotPanel plotPanel;
	private JPanel panel;

	public DatasFrame(PlotPanel p) {
		super("Datas");
		plotPanel = p;
		setPanel();
		setContentPane(panel);
		this.pack();
		setVisible(true);
	}

	private void setPanel() {
		JTabbedPane panels = new JTabbedPane();
		for (int i = 0; i < plotPanel.getPlots().length; i++) {
			panels.add(new MatrixTablePanel(plotPanel.getPlot(i).getDatas()),
				plotPanel.getPlot(i).getName());
		}
		panel = new JPanel();
		panel.add(panels);
	}

}