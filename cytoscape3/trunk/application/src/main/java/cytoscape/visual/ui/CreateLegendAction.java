package cytoscape.visual.ui;

import java.awt.event.ActionEvent;

import cytoscape.util.SwingWorker;

public class CreateLegendAction extends AbstractVizMapperAction {

	private static final long serialVersionUID = 707566797144402515L;

	public void actionPerformed(ActionEvent e) {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				LegendDialog ld = new LegendDialog(cytoscapeDesktop,
						visualMappingManager.getVisualStyle());
				ld.setLocationRelativeTo(cytoscapeDesktop);
				ld.setVisible(true);

				return null;
			}
		};

		worker.start();
	}
}
