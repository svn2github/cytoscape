package org.cytoscape.neildhruva.chartapp.api;

import javax.swing.JPanel;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public interface ChartAppFactory {
	
	/**
	 * Creates a {@link JPanel} consisting of a JFreeChart, checkboxes with column names etc.
	 * @param currentNetwork The current {@link CyNetwork}.
	 * @param cyTable The {@link CyTable} that contains values to be plotted on the {@link JFreeChart}
	 * @return The {@link JPanel} consisting of the chart, checkboxes with column names etc.
	 */
	JPanel createPanel(CyNetwork currentNetwork, CyTable cyTable);

}
